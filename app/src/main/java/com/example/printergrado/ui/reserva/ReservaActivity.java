package com.example.printergrado.ui.reserva;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class ReservaActivity extends AppCompatActivity {

    private TextView tvTitulo, tvGenero, tvDuracion, tvSinopsis, tvCantidad, btnVolver;
    private AutoCompleteTextView spinnerCine;
    private TextInputEditText etFechaReserva;
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

        // 1. Enlazar vistas
        btnVolver = findViewById(R.id.btnVolverReserva);
        tvTitulo = findViewById(R.id.tvTituloReserva);
        tvGenero = findViewById(R.id.tvGeneroReserva);
        tvDuracion = findViewById(R.id.tvDuracionReserva);
        tvSinopsis = findViewById(R.id.tvSinopsisReserva);
        spinnerCine = findViewById(R.id.spinnerCineReserva);
        etFechaReserva = findViewById(R.id.etFechaReserva);
        btnMenos = findViewById(R.id.btnMenosEntradas);
        btnMas = findViewById(R.id.btnMasEntradas);
        tvCantidad = findViewById(R.id.tvCantidadEntradas);
        btnComprar = findViewById(R.id.btnComprar);

        // 2. Gestionar Insets (Barra Superior)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorReserva);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        btnVolver.setOnClickListener(v -> finish());

        // 3. Cargar datos de la Película
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

        // --- 4. CONFIGURAR DESPLEGABLE DE CINES ---
        String[] cines = new String[]{"Cine Yelmo Ideal", "Cinesa Las Rozas", "Kinepolis Ciudad de la Imagen"};
        ArrayAdapter<String> adapterCines = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cines);
        spinnerCine.setAdapter(adapterCines);

        // --- 5. CONFIGURAR CALENDARIO PARA LA FECHA ---
        etFechaReserva.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                // Formateamos la fecha a YYYY-MM-DD para que Flask la entienda
                String fechaElegida = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etFechaReserva.setText(fechaElegida);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 6. Lógica del contador
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

        // 7. Lógica de Compra
        reservaViewModel = new ViewModelProvider(this).get(ReservaViewModel.class);
        reservaViewModel.getMensajeReserva().observe(this, mensaje -> {
            if (mensaje != null) Toast.makeText(ReservaActivity.this, mensaje, Toast.LENGTH_LONG).show();
        });
        reservaViewModel.getReservaExitosa().observe(this, exitosa -> {
            if (exitosa != null && exitosa) finish();
        });

        btnComprar.setOnClickListener(v -> {
            String fecha = etFechaReserva.getText().toString();
            String cine = spinnerCine.getText().toString();

            // Validamos que haya elegido fecha y cine
            if (fecha.isEmpty() || cine.isEmpty()) {
                Toast.makeText(this, "Por favor, elige el cine y la fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt_token", null);

            if (token != null) {
                reservaViewModel.hacerReserva("Bearer " + token, idSesionSeleccionada, cantidadEntradas, fecha);
            }
        });
    }
}