package com.example.printergrado.ui.main;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Sala;
import com.example.printergrado.data.model.Sesion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSesionesActivity extends AppCompatActivity {

    private int idPelicula;
    private String tituloPelicula;
    private RecyclerView rvSesiones;
    private AdminSesionAdapter adapter;
    private ApiService apiService;
    private String token;

    // Lista para guardar las salas del cine del admin
    private List<Sala> listaSalas = new ArrayList<>();
    // Variable temporal para guardar la sala que se elige en el desplegable
    private int idSalaSeleccionadaTemporal = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_admin_sesiones);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorSesiones);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        if (getIntent() != null) {
            idPelicula = getIntent().getIntExtra("ID_PELICULA", -1);
            tituloPelicula = getIntent().getStringExtra("TITULO_PELICULA");
        }

        TextView tvTitulo = findViewById(R.id.tvTituloPeliSesiones);
        tvTitulo.setText("Sesiones: " + (tituloPelicula != null ? tituloPelicula : ""));
        findViewById(R.id.btnVolverSesiones).setOnClickListener(v -> finish());

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        rvSesiones = findViewById(R.id.rvSesionesAdmin);
        rvSesiones.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminSesionAdapter(new AdminSesionAdapter.OnSesionActionListener() {
            @Override public void onEditar(Sesion sesion) { mostrarDialogoSesion(sesion); }
            @Override public void onEliminar(int idSesion) { eliminarSesion(idSesion); }
        });
        rvSesiones.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAgregarSesion);
        fab.setOnClickListener(v -> mostrarDialogoSesion(null));

        cargarSesiones();
        cargarSalasDelCine(); // Descargamos las salas al abrir la pantalla
    }

    private void cargarSesiones() {
        apiService.getSesionesPelicula(idPelicula, true).enqueue(new Callback<List<Sesion>>() {
            @Override
            public void onResponse(Call<List<Sesion>> call, Response<List<Sesion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setSesiones(response.body());
                }
            }
            @Override public void onFailure(Call<List<Sesion>> call, Throwable t) {}
        });
    }

    private void cargarSalasDelCine() {
        apiService.getSalas(token).enqueue(new Callback<List<Sala>>() {
            @Override
            public void onResponse(Call<List<Sala>> call, Response<List<Sala>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaSalas = response.body();
                }
            }
            @Override public void onFailure(Call<List<Sala>> call, Throwable t) {}
        });
    }

    private void mostrarDialogoSesion(Sesion sesionActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sesion, null);
        builder.setView(view);

        TextView tvTituloDialog = view.findViewById(R.id.tvTituloDialogSesion);
        TextInputEditText etFecha = view.findViewById(R.id.etFechaSesion);
        TextInputEditText etHora = view.findViewById(R.id.etHoraSesion);

        EditText etPrecio = view.findViewById(R.id.etPrecioSesion);
        View btnRestar = view.findViewById(R.id.btnRestarPrecio);
        View btnSumar = view.findViewById(R.id.btnSumarPrecio);

        AutoCompleteTextView spinnerSala = view.findViewById(R.id.spinnerSalaSesion);

        idSalaSeleccionadaTemporal = -1;

        // 1. Lógica del Selector de Fecha
        etFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (vista, year, month, day) -> {
                etFecha.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 2. Lógica del Selector de Hora
        etHora.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (vista, hourOfDay, minute) -> {
                etHora.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        // 3. Lógica del Numeric Up/Down del Precio
        btnRestar.setOnClickListener(v -> {
            try {
                double precio = Double.parseDouble(etPrecio.getText().toString());
                if (precio > 0.5) etPrecio.setText(String.format(Locale.US, "%.2f", precio - 0.5));
            } catch (Exception e){}
        });
        btnSumar.setOnClickListener(v -> {
            try {
                double precio = Double.parseDouble(etPrecio.getText().toString());
                etPrecio.setText(String.format(Locale.US, "%.2f", precio + 0.5));
            } catch (Exception e){}
        });

        // 4. Lógica del Desplegable de Salas
        ArrayAdapter<Sala> adapterSalas = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listaSalas);
        spinnerSala.setAdapter(adapterSalas);
        spinnerSala.setOnItemClickListener((parent, view1, position, id) -> {
            idSalaSeleccionadaTemporal = ((Sala) parent.getItemAtPosition(position)).getIdSala();
        });

        // 5. Rellenar datos si estamos Editando
        if (sesionActual != null) {
            tvTituloDialog.setText("Editar Sesión");
            etFecha.setText(sesionActual.getFecha());
            etHora.setText(sesionActual.getHora());
            etPrecio.setText(String.format(Locale.US, "%.2f", sesionActual.getPrecio()));

            // Buscar la sala actual para pre-seleccionarla en el desplegable
            idSalaSeleccionadaTemporal = sesionActual.getFkSala();
            for (Sala s : listaSalas) {
                if (s.getIdSala() == sesionActual.getFkSala()) {
                    spinnerSala.setText(s.getNombre(), false);
                    break;
                }
            }
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            if (etFecha.getText().toString().isEmpty() || etHora.getText().toString().isEmpty() || idSalaSeleccionadaTemporal == -1) {
                Toast.makeText(this, "Rellena todos los campos y selecciona una sala", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> datos = new HashMap<>();
            datos.put("fecha", etFecha.getText().toString());
            datos.put("hora", etHora.getText().toString());
            datos.put("fk_sala", idSalaSeleccionadaTemporal);
            try {
                datos.put("precio", Double.parseDouble(etPrecio.getText().toString()));
            } catch (Exception e) { return; }

            if (sesionActual == null) {
                apiService.crearSesion(token, idPelicula, datos).enqueue(new Callback<ReservaResponse>() {
                    @Override public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) { cargarSesiones(); }
                    @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {}
                });
            } else {
                apiService.actualizarSesion(token, sesionActual.getIdSesion(), datos).enqueue(new Callback<ReservaResponse>() {
                    @Override public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) { cargarSesiones(); }
                    @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {}
                });
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void eliminarSesion(int idSesion) { /* ... igual ... */
        apiService.eliminarSesion(token, idSesion).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSesionesActivity.this, "Sesión eliminada", Toast.LENGTH_SHORT).show();
                    cargarSesiones();
                } else {
                    Toast.makeText(AdminSesionesActivity.this, "No se puede eliminar (tiene reservas)", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                Toast.makeText(AdminSesionesActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}