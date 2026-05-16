package com.example.printergrado.ui.reserva;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Butaca;
import com.example.printergrado.data.model.Sesion;
import com.example.printergrado.viewmodel.ReservaViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaActivity extends AppCompatActivity {

    private TextView tvTitulo, tvGenero, tvDuracion, tvSinopsis, btnVolver;
    private ImageView ivCartel;
    private AutoCompleteTextView spinnerCine, spinnerFecha, spinnerHora;
    private View layoutFecha, layoutHora, tvInstruccionButacas, viewPantalla;
    private RecyclerView rvButacas;
    private MaterialButton btnComprar;

    private List<Sesion> todasLasSesiones = new ArrayList<>();
    private int idPelicula = 1;
    private int idSesionFinal = -1;
    private double precioSesionActual = 0.0;
    private ButacaAdapter butacaAdapter;
    private ReservaViewModel reservaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_reserva);

        btnVolver = findViewById(R.id.btnVolverReserva);
        tvTitulo = findViewById(R.id.tvTituloReserva);
        tvGenero = findViewById(R.id.tvGeneroReserva);
        tvDuracion = findViewById(R.id.tvDuracionReserva);
        tvSinopsis = findViewById(R.id.tvSinopsisReserva);
        ivCartel = findViewById(R.id.ivCartelReserva);
        spinnerCine = findViewById(R.id.spinnerCine);
        spinnerFecha = findViewById(R.id.spinnerFecha);
        spinnerHora = findViewById(R.id.spinnerHora);
        layoutFecha = findViewById(R.id.layoutFecha);
        layoutHora = findViewById(R.id.layoutHora);
        tvInstruccionButacas = findViewById(R.id.tvInstruccionButacas);
        viewPantalla = findViewById(R.id.viewPantalla);
        rvButacas = findViewById(R.id.rvButacas);
        btnComprar = findViewById(R.id.btnComprar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorReserva);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        btnVolver.setOnClickListener(v -> finish());

        if (getIntent() != null) {
            idPelicula = getIntent().getIntExtra("ID_PELICULA", 1);
            String titulo = getIntent().getStringExtra("TITULO");
            if (titulo != null) tvTitulo.setText(titulo);
            String genero = getIntent().getStringExtra("GENERO");
            if (genero != null) tvGenero.setText(genero);
            int duracion = getIntent().getIntExtra("DURACION", 0);
            if (duracion > 0) tvDuracion.setText(duracion + " min");
            String sinopsis = getIntent().getStringExtra("SINOPSIS");
            if (sinopsis != null) tvSinopsis.setText(sinopsis);

            String imagenBase64 = getIntent().getStringExtra("IMAGEN");
            if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                try {
                    byte[] decoded = Base64.decode(imagenBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    ivCartel.setImageBitmap(bitmap);
                } catch (Exception e) {
                    ivCartel.setImageResource(R.drawable.ic_imagen);
                }
            }
        }

        reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        reservaViewModel.getMensajeReserva().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(ReservaActivity.this, mensaje, Toast.LENGTH_LONG).show();
                btnComprar.setEnabled(true);

                if (butacaAdapter != null) {
                    int cant = butacaAdapter.getButacasSeleccionadas().size();
                    btnComprar.setText(String.format("Comprar %d entradas (%.2f €)", cant, cant * precioSesionActual));
                } else {
                    btnComprar.setText("Confirmar Reserva");
                }
            }
        });
        reservaViewModel.getReservaExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) finish();
        });

        cargarSesiones();
        btnComprar.setOnClickListener(v -> realizarCompra());
    }

    private void cargarSesiones() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getSesionesPelicula(idPelicula, false).enqueue(new Callback<List<Sesion>>() {
            @Override
            public void onResponse(Call<List<Sesion>> call, Response<List<Sesion>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    todasLasSesiones = response.body();
                    configurarSelectorCine();
                } else {
                    spinnerCine.setText("No hay sesiones disponibles", false);
                    spinnerCine.setEnabled(false);
                }
            }
            @Override public void onFailure(Call<List<Sesion>> call, Throwable t) {}
        });
    }

    private void configurarSelectorCine() {
        if (todasLasSesiones.isEmpty()) return;

        String cineSel = todasLasSesiones.get(0).getCine();

        spinnerCine.setText(cineSel, false);
        spinnerCine.setEnabled(false);

        spinnerFecha.setText("", false);
        spinnerHora.setText("", false);
        layoutHora.setVisibility(View.GONE);
        ocultarMapa();

        filtrarFechas(cineSel);
        layoutFecha.setVisibility(View.VISIBLE);
    }

    private void filtrarFechas(String cine) {
        Set<String> fechas = new HashSet<>();
        for (Sesion s : todasLasSesiones) {
            if (s.getCine().equals(cine)) fechas.add(s.getFecha());
        }
        spinnerFecha.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(fechas)));
        spinnerFecha.setOnItemClickListener((parent, view, position, id) -> {
            spinnerHora.setText("", false);
            ocultarMapa();
            filtrarHoras(cine, (String) parent.getItemAtPosition(position));
            layoutHora.setVisibility(View.VISIBLE);
        });
    }

    private void filtrarHoras(String cine, String fecha) {
        List<Sesion> sesionesFinales = new ArrayList<>();
        List<String> horas = new ArrayList<>();
        for (Sesion s : todasLasSesiones) {
            if (s.getCine().equals(cine) && s.getFecha().equals(fecha)) {
                sesionesFinales.add(s);
                horas.add(s.getHora() + " - " + s.getPrecio() + "€");
            }
        }
        spinnerHora.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, horas));

        spinnerHora.setOnItemClickListener((parent, view, position, id) -> {
            idSesionFinal = sesionesFinales.get(position).getIdSesion();
            precioSesionActual = sesionesFinales.get(position).getPrecio();
            cargarMapaButacas(idSesionFinal);
        });
    }

    private void ocultarMapa() {
        tvInstruccionButacas.setVisibility(View.GONE);
        viewPantalla.setVisibility(View.GONE);
        rvButacas.setVisibility(View.GONE);
        btnComprar.setEnabled(false);
        btnComprar.setText("Selecciona al menos una butaca");
    }

    private int compararAlfaNumerico(String s1, String s2) {
        try { return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2)); }
        catch (NumberFormatException e) { return s1.compareTo(s2); }
    }

    private int obtenerTamanoReal(String ultimoElemento, boolean esLetra) {
        if (esLetra) return ultimoElemento.charAt(0) - 'A' + 1;
        else return Integer.parseInt(ultimoElemento);
    }

    private void cargarMapaButacas(int idSesion) {
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("jwt_token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMapaButacas(token, idSesion).enqueue(new Callback<List<Butaca>>() {
            @Override
            public void onResponse(Call<List<Butaca>> call, Response<List<Butaca>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Butaca> listaButacasReales = response.body();
                    if (listaButacasReales.isEmpty()) return;

                    tvInstruccionButacas.setVisibility(View.VISIBLE);
                    viewPantalla.setVisibility(View.VISIBLE);
                    rvButacas.setVisibility(View.VISIBLE);

                    List<String> filas = new ArrayList<>();
                    List<String> columnas = new ArrayList<>();
                    for (Butaca b : listaButacasReales) {
                        if (!filas.contains(b.getFila())) filas.add(b.getFila());
                        if (!columnas.contains(b.getColumna())) columnas.add(b.getColumna());
                    }

                    Collections.sort(filas, (s1, s2) -> compararAlfaNumerico(s1, s2));
                    Collections.sort(columnas, (s1, s2) -> compararAlfaNumerico(s1, s2));

                    boolean filasLetras = !Character.isDigit(filas.get(0).charAt(0));
                    boolean columnasLetras = !Character.isDigit(columnas.get(0).charAt(0));

                    int numFilas = obtenerTamanoReal(filas.get(filas.size() - 1), filasLetras);
                    int numColumnasGrid = obtenerTamanoReal(columnas.get(columnas.size() - 1), columnasLetras);

                    rvButacas.setLayoutManager(new GridLayoutManager(ReservaActivity.this, numColumnasGrid));

                    List<Butaca> cuadriculaCompleta = new ArrayList<>();
                    for (int i = 0; i < numFilas; i++) {
                        String f = filasLetras ? String.valueOf((char)('A' + i)) : String.valueOf(i + 1);
                        for (int j = 0; j < numColumnasGrid; j++) {
                            String c = columnasLetras ? String.valueOf((char)('A' + j)) : String.valueOf(j + 1);
                            Butaca butacaEncontrada = null;
                            for (Butaca b : listaButacasReales) {
                                if (b.getFila().equals(f) && b.getColumna().equals(c)) {
                                    butacaEncontrada = b; break;
                                }
                            }
                            cuadriculaCompleta.add(butacaEncontrada);
                        }
                    }

                    butacaAdapter = new ButacaAdapter(cuadriculaCompleta, cantidad -> {
                        if (cantidad > 0) {
                            btnComprar.setEnabled(true);
                            btnComprar.setBackgroundColor(getResources().getColor(R.color.rojo_cine));
                            btnComprar.setText(String.format("Comprar %d entradas (%.2f €)", cantidad, cantidad * precioSesionActual));
                        } else {
                            btnComprar.setEnabled(false);
                            btnComprar.setBackgroundColor(getResources().getColor(R.color.gris_oscuro));
                            btnComprar.setText("Selecciona al menos una butaca");
                        }
                    });

                    rvButacas.setAdapter(butacaAdapter);
                }
            }
            @Override public void onFailure(Call<List<Butaca>> call, Throwable t) {}
        });
    }

    private void realizarCompra() {
        if (idSesionFinal == -1 || butacaAdapter == null) return;
        List<Integer> butacasElegidas = butacaAdapter.getButacasSeleccionadas();
        if (butacasElegidas.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token != null) {
            btnComprar.setEnabled(false);
            btnComprar.setText("Procesando pago...");
            reservaViewModel.hacerReservaConButacas("Bearer " + token, idSesionFinal, butacasElegidas);
        }
    }
}