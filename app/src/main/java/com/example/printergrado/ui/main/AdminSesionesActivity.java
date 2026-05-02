package com.example.printergrado.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.printergrado.data.model.Sesion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
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
            @Override
            public void onEditar(Sesion sesion) {
                mostrarDialogoSesion(sesion);
            }

            @Override
            public void onEliminar(int idSesion) {
                eliminarSesion(idSesion);
            }
        });
        rvSesiones.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAgregarSesion);
        fab.setOnClickListener(v -> mostrarDialogoSesion(null));

        cargarSesiones();
    }

    private void cargarSesiones() {
        apiService.getSesionesPelicula(idPelicula, true).enqueue(new Callback<List<Sesion>>() {
            @Override
            public void onResponse(Call<List<Sesion>> call, Response<List<Sesion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setSesiones(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Sesion>> call, Throwable t) {
                Toast.makeText(AdminSesionesActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoSesion(Sesion sesionActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sesion, null);
        builder.setView(view);

        TextView tvTituloDialog = view.findViewById(R.id.tvTituloDialogSesion);
        TextInputEditText etFecha = view.findViewById(R.id.etFechaSesion);
        TextInputEditText etHora = view.findViewById(R.id.etHoraSesion);
        TextInputEditText etPrecio = view.findViewById(R.id.etPrecioSesion);
        TextInputEditText etSala = view.findViewById(R.id.etSalaSesion);

        if (sesionActual != null) {
            tvTituloDialog.setText("Editar Sesión");
            etFecha.setText(sesionActual.getFecha());
            etHora.setText(sesionActual.getHora());
            etPrecio.setText(String.valueOf(sesionActual.getPrecio()));
            etSala.setText(String.valueOf(sesionActual.getFkSala()));
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("fecha", etFecha.getText().toString());
            datos.put("hora", etHora.getText().toString());
            try {
                datos.put("precio", Double.parseDouble(etPrecio.getText().toString()));
                datos.put("fk_sala", Integer.parseInt(etSala.getText().toString()));
            } catch (Exception e) {
                Toast.makeText(this, "Precio y Sala deben ser números", Toast.LENGTH_SHORT).show();
                return;
            }

            if (sesionActual == null) {
                // Crear
                apiService.crearSesion(token, idPelicula, datos).enqueue(new Callback<ReservaResponse>() {
                    @Override public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) { cargarSesiones(); }
                    @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {}
                });
            } else {
                // Actualizar
                apiService.actualizarSesion(token, sesionActual.getIdSesion(), datos).enqueue(new Callback<ReservaResponse>() {
                    @Override public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) { cargarSesiones(); }
                    @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {}
                });
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void eliminarSesion(int idSesion) {
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