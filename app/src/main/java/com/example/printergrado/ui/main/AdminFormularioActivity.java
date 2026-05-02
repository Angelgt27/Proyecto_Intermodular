package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFormularioActivity extends AppCompatActivity {

    private TextInputEditText etTitulo, etGenero, etDuracion, etSinopsis, etCine;
    private MaterialButton btnGuardar, btnEliminar;
    private TextView tvTituloToolbar;
    private int peliculaId = -1; // -1 significa crear nueva
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_admin_formulario);

        // Configuración visual (padding notch/cámara)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorForm);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        // Referencias UI
        etTitulo = findViewById(R.id.etTituloPelicula);
        etGenero = findViewById(R.id.etGeneroPelicula);
        etDuracion = findViewById(R.id.etDuracionPelicula);
        etSinopsis = findViewById(R.id.etSinopsisPelicula);
        etCine = findViewById(R.id.etCinePelicula);
        btnGuardar = findViewById(R.id.btnGuardarPelicula);
        btnEliminar = findViewById(R.id.btnEliminarPelicula);
        tvTituloToolbar = findViewById(R.id.tvTituloToolbar);

        findViewById(R.id.btnVolverForm).setOnClickListener(v -> finish());

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        // Cargar datos si estamos en modo Edición
        if (getIntent() != null && getIntent().hasExtra("ID_PELICULA")) {
            peliculaId = getIntent().getIntExtra("ID_PELICULA", -1);
            tvTituloToolbar.setText("Editar Película");
            etTitulo.setText(getIntent().getStringExtra("TITULO"));
            etGenero.setText(getIntent().getStringExtra("GENERO"));
            etDuracion.setText(String.valueOf(getIntent().getIntExtra("DURACION", 0)));
            etSinopsis.setText(getIntent().getStringExtra("SINOPSIS"));
            etCine.setText(String.valueOf(getIntent().getIntExtra("FK_CINE", 1)));

            btnEliminar.setVisibility(View.VISIBLE); // Mostrar botón eliminar
        }

        btnGuardar.setOnClickListener(v -> guardarPelicula());
        btnEliminar.setOnClickListener(v -> eliminarPelicula());
    }

    private void guardarPelicula() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", etTitulo.getText().toString());
        datos.put("genero", etGenero.getText().toString());
        datos.put("sinopsis", etSinopsis.getText().toString());

        try {
            datos.put("duracion", Integer.parseInt(etDuracion.getText().toString()));
            datos.put("fk_cine", Integer.parseInt(etCine.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duración y Cine deben ser números", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);

        if (peliculaId == -1) {
            // CREAR NUEVA
            apiService.crearPelicula(token, datos).enqueue(new Callback<ReservaResponse>() {
                @Override
                public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                    gestionarRespuesta(response);
                }
                @Override public void onFailure(Call<ReservaResponse> call, Throwable t) { errorConexion(t); }
            });
        } else {
            apiService.actualizarPelicula(token, peliculaId, datos).enqueue(new Callback<ReservaResponse>() {
                @Override
                public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                    gestionarRespuesta(response);
                }
                @Override public void onFailure(Call<ReservaResponse> call, Throwable t) { errorConexion(t); }
            });
        }
    }

    private void eliminarPelicula() {
        btnEliminar.setEnabled(false);
        apiService.eliminarPelicula(token, peliculaId).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                gestionarRespuesta(response);
            }
            @Override public void onFailure(Call<ReservaResponse> call, Throwable t) { errorConexion(t); }
        });
    }

    private void gestionarRespuesta(Response<ReservaResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            Toast.makeText(this, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error en la operación. Revisa las dependencias de datos.", Toast.LENGTH_LONG).show();
            btnGuardar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }

    private void errorConexion(Throwable t) {
        Toast.makeText(this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }
}