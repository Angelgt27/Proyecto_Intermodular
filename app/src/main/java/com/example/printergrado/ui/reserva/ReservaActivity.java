package com.example.printergrado.ui.reserva;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaActivity extends AppCompatActivity {

    private TextView tvTitulo, tvGenero, tvDuracion, tvSinopsis, btnVolver;
    private AutoCompleteTextView spinnerCine, spinnerFecha, spinnerHora;
    private View layoutFecha, layoutHora, tvInstruccionButacas;
    private RecyclerView rvButacas;
    private MaterialButton btnComprar;

    private List<Sesion> todasLasSesiones = new ArrayList<>();
    private int idPelicula = 1;
    private int idSesionFinal = -1;
    private ButacaAdapter butacaAdapter;
    private ReservaViewModel reservaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_reserva);

        // Enlaces UI
        btnVolver = findViewById(R.id.btnVolverReserva);
        tvTitulo = findViewById(R.id.tvTituloReserva);
        tvGenero = findViewById(R.id.tvGeneroReserva);
        tvDuracion = findViewById(R.id.tvDuracionReserva);
        tvSinopsis = findViewById(R.id.tvSinopsisReserva);
        spinnerCine = findViewById(R.id.spinnerCine);
        spinnerFecha = findViewById(R.id.spinnerFecha);
        spinnerHora = findViewById(R.id.spinnerHora);
        layoutFecha = findViewById(R.id.layoutFecha);
        layoutHora = findViewById(R.id.layoutHora);
        tvInstruccionButacas = findViewById(R.id.tvInstruccionButacas);
        rvButacas = findViewById(R.id.rvButacas);
        btnComprar = findViewById(R.id.btnComprar);

        // Ajuste para la barra superior
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorReserva);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        btnVolver.setOnClickListener(v -> finish());

        // Cargar datos del Intent
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
        }

        reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        // Observadores de ViewModel
        reservaViewModel.getMensajeReserva().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(ReservaActivity.this, mensaje, Toast.LENGTH_LONG).show();
                btnComprar.setEnabled(true);
                btnComprar.setText("Confirmar Reserva");
            }
        });
        reservaViewModel.getReservaExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) finish();
        });

        // 1. Cargamos las sesiones
        cargarSesiones();

        // 2. Evento del botón comprar
        btnComprar.setOnClickListener(v -> realizarCompra());
    }

    private void cargarSesiones() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getSesionesPelicula(idPelicula).enqueue(new Callback<List<Sesion>>() {
            @Override
            public void onResponse(Call<List<Sesion>> call, Response<List<Sesion>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    todasLasSesiones = response.body();
                    configurarSelectorCine();
                } else {
                    spinnerCine.setHint("No hay sesiones disponibles");
                    spinnerCine.setEnabled(false);
                }
            }
            @Override public void onFailure(Call<List<Sesion>> call, Throwable t) {
                Toast.makeText(ReservaActivity.this, "Error conectando con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSelectorCine() {
        Set<String> cines = new HashSet<>();
        for (Sesion s : todasLasSesiones) cines.add(s.getCine());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(cines));
        spinnerCine.setAdapter(adapter);

        spinnerCine.setOnItemClickListener((parent, view, position, id) -> {
            String cineSel = (String) parent.getItemAtPosition(position);
            spinnerFecha.setText("", false);
            spinnerHora.setText("", false);
            layoutHora.setVisibility(View.GONE);
            ocultarMapa();

            filtrarFechas(cineSel);
            layoutFecha.setVisibility(View.VISIBLE);
        });
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
                horas.add(s.getHora());
            }
        }
        spinnerHora.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, horas));

        spinnerHora.setOnItemClickListener((parent, view, position, id) -> {
            idSesionFinal = sesionesFinales.get(position).getIdSesion();
            cargarMapaButacas(idSesionFinal);
        });
    }

    private void ocultarMapa() {
        tvInstruccionButacas.setVisibility(View.GONE);
        rvButacas.setVisibility(View.GONE);
        btnComprar.setEnabled(false);
        btnComprar.setText("Selecciona al menos una butaca");
    }

    private void cargarMapaButacas(int idSesion) {
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("jwt_token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMapaButacas(token, idSesion).enqueue(new Callback<List<Butaca>>() {
            @Override
            public void onResponse(Call<List<Butaca>> call, Response<List<Butaca>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Butaca> listaButacas = response.body();

                    tvInstruccionButacas.setVisibility(View.VISIBLE);
                    rvButacas.setVisibility(View.VISIBLE);

                    // --- MAGIA: Calcular columnas dinámicamente ---
                    int numColumnas = 0;
                    if (!listaButacas.isEmpty()) {
                        String primeraFila = listaButacas.get(0).getFila();
                        for (Butaca b : listaButacas) {
                            if (b.getFila().equals(primeraFila)) {
                                numColumnas++;
                            }
                        }
                    }
                    if (numColumnas == 0) numColumnas = 10; // Valor de seguridad

                    // Aplicamos el spanCount dinámico
                    rvButacas.setLayoutManager(new GridLayoutManager(ReservaActivity.this, numColumnas));

                    butacaAdapter = new ButacaAdapter(listaButacas, cantidad -> {
                        if (cantidad > 0) {
                            btnComprar.setEnabled(true);
                            btnComprar.setBackgroundColor(getResources().getColor(R.color.rojo_cine));
                            btnComprar.setText("Comprar " + cantidad + " entradas");
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
            btnComprar.setText("Procesando...");
            // Llamamos al ViewModel (Ahora enviando la lista de IDs)
            reservaViewModel.hacerReservaConButacas("Bearer " + token, idSesionFinal, butacasElegidas);
        }
    }
}