package com.example.printergrado.ui.reserva;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.printergrado.R;
import com.example.printergrado.viewmodel.ReservaViewModel;
import com.google.android.material.button.MaterialButton;

public class ReservaActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTitulo, tvGenero, tvDuracion, tvSinopsis, tvCantidad, btnVolver;
    private AutoCompleteTextView spinnerCine;
    private ImageButton btnMenos, btnMas;
    private MaterialButton btnComprar;

    private int cantidadEntradas = 1;
    private int idSesionSeleccionada = 1;
    private ReservaViewModel reservaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_reserva);

        // Enlazar vistas
        btnVolver = findViewById(R.id.btnVolverReserva);
        tvTitulo = findViewById(R.id.tvTituloReserva);
        tvGenero = findViewById(R.id.tvGeneroReserva);
        tvDuracion = findViewById(R.id.tvDuracionReserva);
        tvSinopsis = findViewById(R.id.tvSinopsisReserva);
        spinnerCine = findViewById(R.id.spinnerCineReserva);
        btnMenos = findViewById(R.id.btnMenosEntradas);
        btnMas = findViewById(R.id.btnMasEntradas);
        tvCantidad = findViewById(R.id.tvCantidadEntradas);
        btnComprar = findViewById(R.id.btnComprar);

        // Gestionar Insets (Misma lógica que TicketDetailActivity para bajar la barra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorReserva);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        // Evento Botón Volver
        btnVolver.setOnClickListener(v -> finish());

        // Cargar datos del Intent
        if (getIntent() != null) {
            idSesionSeleccionada = getIntent().getIntExtra("ID_PELICULA", 1);

            String titulo = getIntent().getStringExtra("TITULO");
            if (titulo != null) tvTitulo.setText(titulo);

            String genero = getIntent().getStringExtra("GENERO");
            if (genero != null) tvGenero.setText(genero);

            int duracion = getIntent().getIntExtra("DURACION", 0);
            if (duracion > 0) tvDuracion.setText(duracion + " min");

            String sinopsis = getIntent().getStringExtra("SINOPSIS");
            if (sinopsis != null) tvSinopsis.setText(sinopsis);
        }

        // Lógica del contador
        btnMas.setOnClickListener(v -> {
            if (cantidadEntradas < 10) {
                cantidadEntradas++;
                tvCantidad.setText(String.valueOf(cantidadEntradas));
            }
        });

        btnMenos.setOnClickListener(v -> {
            if (cantidadEntradas > 1) {
                cantidadEntradas--;
                tvCantidad.setText(String.valueOf(cantidadEntradas));
            }
        });

        // Configurar ViewModel
        reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);

        reservaViewModel.getMensajeReserva().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(ReservaActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        reservaViewModel.getReservaExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) {
                finish(); // Volvemos a la cartelera si la compra sale bien
            }
        });

        // Lógica del botón Comprar
        btnComprar.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);

            if (token != null) {
                String authHeader = "Bearer " + token;
                reservaViewModel.hacerReserva(authHeader, idSesionSeleccionada, cantidadEntradas);
            } else {
                Toast.makeText(this, "Error: No has iniciado sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}