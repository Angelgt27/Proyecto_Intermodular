package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSalasListaActivity extends AppCompatActivity {

    private RecyclerView rvSalas;
    private AdminSalaAdapter adapter;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_admin_salas_lista);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorSalas);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverSalasList).setOnClickListener(v -> finish());

        findViewById(R.id.fabAgregarSalaList).setOnClickListener(v -> {
            startActivity(new Intent(this, SalaCreatorActivity.class));
        });

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        rvSalas = findViewById(R.id.rvSalasListAdmin);
        rvSalas.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminSalaAdapter(new AdminSalaAdapter.OnSalaActionListener() {
            @Override
            public void onEditar(Sala sala) {
                Intent intent = new Intent(AdminSalasListaActivity.this, SalaCreatorActivity.class);
                intent.putExtra("ID_SALA", sala.getIdSala());
                intent.putExtra("NOMBRE_SALA", sala.getNombre());
                startActivity(intent);
            }

            @Override
            public void onEliminar(int idSala) {
                eliminarSala(idSala);
            }
        });
        rvSalas.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSalas();
    }

    private void cargarSalas() {
        apiService.getSalas(token).enqueue(new Callback<List<Sala>>() {
            @Override
            public void onResponse(Call<List<Sala>> call, Response<List<Sala>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setSalas(response.body());
                }
            }
            @Override public void onFailure(Call<List<Sala>> call, Throwable t) {}
        });
    }

    private void eliminarSala(int idSala) {
        apiService.eliminarSalaAdmin(token, idSala).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSalasListaActivity.this, "Sala eliminada", Toast.LENGTH_SHORT).show();
                    cargarSalas();
                } else {
                    Toast.makeText(AdminSalasListaActivity.this, "No se puede eliminar (tiene sesiones activas)", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {
                Toast.makeText(AdminSalasListaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}