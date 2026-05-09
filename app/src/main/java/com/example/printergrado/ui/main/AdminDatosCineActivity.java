package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class AdminDatosCineActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDireccion, etTelefono;
    private MaterialButton btnGuardar;
    private ApiService apiService;
    private String token;

    

    private int idCineSuperadmin = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_admin_datos_cine);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorDatosCine);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverDatosCine).setOnClickListener(v -> finish());

        etNombre = findViewById(R.id.etNombreCineAdmin);
        etDireccion = findViewById(R.id.etDireccionCineAdmin);
        etTelefono = findViewById(R.id.etTelefonoCineAdmin);
        btnGuardar = findViewById(R.id.btnGuardarDatosCine);

        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");
        apiService = ApiClient.getClient().create(ApiService.class);

        

        if (getIntent() != null && getIntent().hasExtra("ID_CINE")) {
            idCineSuperadmin = getIntent().getIntExtra("ID_CINE", -1);
            etNombre.setText(getIntent().getStringExtra("NOMBRE"));
            String dir = getIntent().getStringExtra("DIRECCION");
            etDireccion.setText((dir == null || dir.isEmpty()) ? "" : dir);
            String tel = getIntent().getStringExtra("TELEFONO");
            etTelefono.setText((tel == null || tel.isEmpty()) ? "" : tel);
        } else {
            cargarDatosActuales();
        }

        btnGuardar.setOnClickListener(v -> guardarDatos());
    }

    private void cargarDatosActuales() {
        btnGuardar.setEnabled(false);
        apiService.getDatosCine(token).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> datos = response.body();
                    if (datos.containsKey("nombre")) etNombre.setText(String.valueOf(datos.get("nombre")));
                    if (datos.containsKey("direccion")) etDireccion.setText(String.valueOf(datos.get("direccion")));
                    if (datos.containsKey("telefono")) etTelefono.setText(String.valueOf(datos.get("telefono")));
                } else {
                    Toast.makeText(AdminDatosCineActivity.this, "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show();
                }
                btnGuardar.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AdminDatosCineActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                btnGuardar.setEnabled(true);
            }
        });
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        Map<String, String> payload = new HashMap<>();
        payload.put("nombre", nombre);
        payload.put("direccion", direccion);
        payload.put("telefono", telefono);

        Callback<ReservaResponse> callback = new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminDatosCineActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminDatosCineActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar Cambios");
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                Toast.makeText(AdminDatosCineActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Cambios");
            }
        };

        

        if (idCineSuperadmin != -1) {
            apiService.actualizarCineSuperadmin(token, idCineSuperadmin, payload).enqueue(callback);
        } else {
            apiService.updateDatosCine(token, payload).enqueue(callback);
        }
    }
}