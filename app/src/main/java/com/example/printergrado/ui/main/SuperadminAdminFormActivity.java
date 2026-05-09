package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Cine;
import com.example.printergrado.data.model.ReservaResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuperadminAdminFormActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etEmail, etPassword;
    private AutoCompleteTextView spinnerCine;
    private MaterialButton btnGuardar;
    private ApiService apiService;
    private String token;

    private List<Cine> listaCines = new ArrayList<>();
    private Integer idCineSeleccionado = null;

    private boolean modoEdicion = false;
    private int idAdminEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_superadmin_admin_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorAdminForm);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverAdminForm).setOnClickListener(v -> finish());
        TextView tvTitulo = findViewById(R.id.tvTituloAdminForm);

        etNombre = findViewById(R.id.etNombreAdminForm);
        etEmail = findViewById(R.id.etEmailAdminForm);
        etPassword = findViewById(R.id.etPasswordAdminForm);
        spinnerCine = findViewById(R.id.spinnerCineAdminForm);
        btnGuardar = findViewById(R.id.btnGuardarAdminForm);

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        if (getIntent() != null && getIntent().hasExtra("ID_ADMIN")) {
            modoEdicion = true;
            idAdminEditar = getIntent().getIntExtra("ID_ADMIN", -1);

            tvTitulo.setText("Editar Administrador");
            etNombre.setText(getIntent().getStringExtra("NOMBRE"));
            etEmail.setText(getIntent().getStringExtra("EMAIL"));

            if (getIntent().hasExtra("FK_CINE")) {
                idCineSeleccionado = getIntent().getIntExtra("FK_CINE", -1);
            }
        }

        cargarCines();
        btnGuardar.setOnClickListener(v -> guardarAdmin());
    }

    private void cargarCines() {
        apiService.getCinesSuperadmin(token).enqueue(new Callback<List<Cine>>() {
            @Override
            public void onResponse(Call<List<Cine>> call, Response<List<Cine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCines = response.body();
                    List<String> nombresCines = new ArrayList<>();

                    int posicionSeleccionar = -1;
                    for (int i = 0; i < listaCines.size(); i++) {
                        Cine c = listaCines.get(i);
                        nombresCines.add(c.getNombre());
                        if (idCineSeleccionado != null && c.getIdCine() == idCineSeleccionado) {
                            posicionSeleccionar = i;
                        }
                    }

                    ArrayAdapter<String> adp = new ArrayAdapter<String>(SuperadminAdminFormActivity.this, android.R.layout.simple_dropdown_item_1line, nombresCines) {
                        @NonNull
                        @Override
                        public Filter getFilter() {
                            return new Filter() {
                                @Override protected FilterResults performFiltering(CharSequence constraint) {
                                    FilterResults results = new FilterResults();
                                    results.values = nombresCines;
                                    results.count = nombresCines.size();
                                    return results;
                                }
                                @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                                    notifyDataSetChanged();
                                }
                            };
                        }
                    };

                    spinnerCine.setAdapter(adp);

                    if (posicionSeleccionar != -1) {
                        spinnerCine.setText(nombresCines.get(posicionSeleccionar), false);
                    }

                    spinnerCine.setOnItemClickListener((parent, view, position, id) -> {
                        idCineSeleccionado = listaCines.get(position).getIdCine();
                    });
                }
            }
            @Override public void onFailure(Call<List<Cine>> call, Throwable t) {}
        });
    }

    private void guardarAdmin() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nombre y correo son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!modoEdicion && password.isEmpty()) {
            Toast.makeText(this, "La contrasena es obligatoria para un usuario nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idCineSeleccionado == null) {
            Toast.makeText(this, "Debes asignar un cine", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", nombre);
        payload.put("email", email);
        payload.put("fk_cine_gestionado", idCineSeleccionado);

        if (!password.isEmpty()) {
            payload.put("password", password);
        }

        Callback<ReservaResponse> callback = new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SuperadminAdminFormActivity.this, "Guardado con exito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SuperadminAdminFormActivity.this, "Error, el correo podria estar en uso", Toast.LENGTH_LONG).show();
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar Administrador");
                }
            }
            @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {
                Toast.makeText(SuperadminAdminFormActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                btnGuardar.setEnabled(true);
                btnGuardar.setText("Guardar Administrador");
            }
        };

        if (modoEdicion) {
            apiService.editarAdminSuperadmin(token, idAdminEditar, payload).enqueue(callback);
        } else {
            apiService.crearAdminSuperadmin(token, payload).enqueue(callback);
        }
    }
}