package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Butaca;
import com.example.printergrado.data.model.ButacaTemporal;
import com.example.printergrado.data.model.ReservaResponse;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalaCreatorActivity extends AppCompatActivity {

    private EditText etNombreSala, etFilas, etColumnas;
    private AutoCompleteTextView spinnerFormatoFilas, spinnerFormatoColumnas;
    private RecyclerView rvMapa;
    private View tvInstrucciones, viewPantallaCreator;
    private MaterialButton btnGuardarSala;

    private SalaCreatorAdapter adapter;
    private ApiService apiService;

    String[] opcionesFormato = {"Numérica", "Alfabética"};

    private boolean modoEdicion = false;
    private int idSalaAEditar = -1;
    private boolean isLoadingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_sala_creator);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorCreator);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverCreator).setOnClickListener(v -> finish());

        etNombreSala = findViewById(R.id.etNombreSalaCreator);
        etFilas = findViewById(R.id.etFilasCreator);
        etColumnas = findViewById(R.id.etColumnasCreator);
        spinnerFormatoFilas = findViewById(R.id.spinnerFormatoFilas);
        spinnerFormatoColumnas = findViewById(R.id.spinnerFormatoColumnas);

        rvMapa = findViewById(R.id.rvMapaCreator);
        tvInstrucciones = findViewById(R.id.tvInstruccionesCreator);
        viewPantallaCreator = findViewById(R.id.viewPantallaCreator);
        btnGuardarSala = findViewById(R.id.btnGuardarSala);

        apiService = ApiClient.getClient().create(ApiService.class);

        ArrayAdapter<String> adapterFormatos = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opcionesFormato);
        spinnerFormatoFilas.setAdapter(adapterFormatos);
        spinnerFormatoColumnas.setAdapter(adapterFormatos);

        spinnerFormatoFilas.setText("Numérica", false);
        spinnerFormatoColumnas.setText("Numérica", false);

        TextWatcher autoDrawWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { generarCuadricula(); }
        };

        etFilas.addTextChangedListener(autoDrawWatcher);
        etColumnas.addTextChangedListener(autoDrawWatcher);
        spinnerFormatoFilas.setOnItemClickListener((parent, view1, position, id) -> generarCuadricula());
        spinnerFormatoColumnas.setOnItemClickListener((parent, view1, position, id) -> generarCuadricula());

        btnGuardarSala.setOnClickListener(v -> guardarSalaEnServidor());

        if (getIntent() != null && getIntent().hasExtra("ID_SALA")) {
            modoEdicion = true;
            idSalaAEditar = getIntent().getIntExtra("ID_SALA", -1);
            String nombreSala = getIntent().getStringExtra("NOMBRE_SALA");

            etNombreSala.setText(nombreSala);
            btnGuardarSala.setText("Guardar Cambios de Sala");
            cargarDatosSalaExistente();
        }
    }

    // NUEVA FUNCIÓN: Obtiene el tamaño real máximo (Ej: si la última es 'E', sabe que son 5 columnas)
    private int obtenerTamanoReal(String ultimoElemento, boolean esLetra) {
        if (esLetra) {
            return ultimoElemento.charAt(0) - 'A' + 1;
        } else {
            return Integer.parseInt(ultimoElemento);
        }
    }

    private void cargarDatosSalaExistente() {
        isLoadingData = true;
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("jwt_token", "");

        apiService.getButacasSalaAdmin(token, idSalaAEditar).enqueue(new Callback<List<Butaca>>() {
            @Override
            public void onResponse(Call<List<Butaca>> call, Response<List<Butaca>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Butaca> butacasReales = response.body();
                    if (butacasReales.isEmpty()) {
                        isLoadingData = false;
                        return;
                    }

                    List<String> filas = new ArrayList<>();
                    List<String> columnas = new ArrayList<>();
                    for (Butaca b : butacasReales) {
                        if (!filas.contains(b.getFila())) filas.add(b.getFila());
                        if (!columnas.contains(b.getColumna())) columnas.add(b.getColumna());
                    }

                    Collections.sort(filas, (s1, s2) -> compararAlfaNumerico(s1, s2));
                    Collections.sort(columnas, (s1, s2) -> compararAlfaNumerico(s1, s2));

                    boolean filasLetras = !Character.isDigit(filas.get(0).charAt(0));
                    boolean columnasLetras = !Character.isDigit(columnas.get(0).charAt(0));

                    // REPARACIÓN COLUMNA FANTASMA: Calculamos en base al máximo elemento, no al conteo de únicos
                    int numFilas = obtenerTamanoReal(filas.get(filas.size() - 1), filasLetras);
                    int numColumnas = obtenerTamanoReal(columnas.get(columnas.size() - 1), columnasLetras);

                    spinnerFormatoFilas.setText(filasLetras ? "Alfabética" : "Numérica", false);
                    spinnerFormatoColumnas.setText(columnasLetras ? "Alfabética" : "Numérica", false);

                    etFilas.setText(String.valueOf(numFilas));
                    etColumnas.setText(String.valueOf(numColumnas));

                    List<ButacaTemporal> matriz = new ArrayList<>();
                    for (int i = 0; i < numFilas; i++) {
                        String f = filasLetras ? String.valueOf((char)('A' + i)) : String.valueOf(i + 1);
                        for (int j = 0; j < numColumnas; j++) {
                            String c = columnasLetras ? String.valueOf((char)('A' + j)) : String.valueOf(j + 1);

                            boolean existe = false;
                            for (Butaca b : butacasReales) {
                                if (b.getFila().equals(f) && b.getColumna().equals(c)) {
                                    existe = true;
                                    break;
                                }
                            }
                            matriz.add(new ButacaTemporal(f, c, existe));
                        }
                    }

                    rvMapa.setLayoutManager(new GridLayoutManager(SalaCreatorActivity.this, numColumnas));
                    adapter = new SalaCreatorAdapter(matriz);
                    rvMapa.setAdapter(adapter);

                    tvInstrucciones.setVisibility(View.VISIBLE);
                    rvMapa.setVisibility(View.VISIBLE);
                    viewPantallaCreator.setVisibility(View.VISIBLE);
                    btnGuardarSala.setVisibility(View.VISIBLE);

                    isLoadingData = false;
                }
            }
            @Override
            public void onFailure(Call<List<Butaca>> call, Throwable t) {
                isLoadingData = false;
            }
        });
    }

    private int compararAlfaNumerico(String s1, String s2) {
        try {
            return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
        } catch (NumberFormatException e) {
            return s1.compareTo(s2);
        }
    }

    private void generarCuadricula() {
        if (isLoadingData) return;

        String filasStr = etFilas.getText().toString();
        String columnasStr = etColumnas.getText().toString();

        if (filasStr.isEmpty() || columnasStr.isEmpty()) {
            ocultarMapa();
            return;
        }

        int numFilas = Integer.parseInt(filasStr);
        int numColumnas = Integer.parseInt(columnasStr);
        if(numFilas <= 0 || numColumnas <= 0) {
            ocultarMapa();
            return;
        }

        boolean filasLetras = spinnerFormatoFilas.getText().toString().equals("Alfabética");
        boolean columnasLetras = spinnerFormatoColumnas.getText().toString().equals("Alfabética");

        List<ButacaTemporal> matriz = new ArrayList<>();

        for (int i = 0; i < numFilas; i++) {
            String nombreFila = filasLetras ? String.valueOf((char)('A' + i)) : String.valueOf(i + 1);
            for (int j = 0; j < numColumnas; j++) {
                String nombreColumna = columnasLetras ? String.valueOf((char)('A' + j)) : String.valueOf(j + 1);
                matriz.add(new ButacaTemporal(nombreFila, nombreColumna, true));
            }
        }

        rvMapa.setLayoutManager(new GridLayoutManager(this, numColumnas));
        adapter = new SalaCreatorAdapter(matriz);
        rvMapa.setAdapter(adapter);

        tvInstrucciones.setVisibility(View.VISIBLE);
        rvMapa.setVisibility(View.VISIBLE);
        viewPantallaCreator.setVisibility(View.VISIBLE);
        btnGuardarSala.setVisibility(View.VISIBLE);
    }

    private void ocultarMapa() {
        tvInstrucciones.setVisibility(View.GONE);
        rvMapa.setVisibility(View.GONE);
        viewPantallaCreator.setVisibility(View.GONE);
        btnGuardarSala.setVisibility(View.GONE);
    }

    private void guardarSalaEnServidor() {
        String nombreSala = etNombreSala.getText().toString().trim();
        if (nombreSala.isEmpty() || adapter == null) {
            Toast.makeText(this, "Ponle un nombre a la sala", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ButacaTemporal> matrizActual = adapter.getMatriz();
        List<Map<String, Object>> butacasFinales = new ArrayList<>();
        int capacidadReal = 0;

        for (ButacaTemporal b : matrizActual) {
            if (b.isActiva()) {
                Map<String, Object> butacaJson = new HashMap<>();
                butacaJson.put("fila", b.getFila());
                butacaJson.put("columna", b.getColumna());
                butacaJson.put("numero_comercial", b.getNumeroComercial());
                butacasFinales.add(butacaJson);
                capacidadReal++;
            }
        }

        if (capacidadReal == 0) {
            Toast.makeText(this, "La sala no puede tener 0 butacas", Toast.LENGTH_SHORT).show();
            return;
        }

        final int finalCapacidadReal = capacidadReal;

        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", nombreSala);
        payload.put("capacidad", finalCapacidadReal);
        payload.put("butacas", butacasFinales);

        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("jwt_token", "");

        btnGuardarSala.setEnabled(false);
        btnGuardarSala.setText("Guardando en la Base de Datos...");

        Callback<ReservaResponse> callback = new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getMensaje() != null) {
                        Toast.makeText(SalaCreatorActivity.this, response.body().getMensaje(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SalaCreatorActivity.this, "¡Sala guardada con éxito!", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Toast.makeText(SalaCreatorActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    btnGuardarSala.setEnabled(true);
                    btnGuardarSala.setText("Guardar Sala Definitiva");
                }
            }
            @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {
                Toast.makeText(SalaCreatorActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                btnGuardarSala.setEnabled(true);
            }
        };

        if (modoEdicion) {
            apiService.editarSalaAdmin(token, idSalaAEditar, payload).enqueue(callback);
        } else {
            apiService.crearSalaAdmin(token, payload).enqueue(callback);
        }
    }
}