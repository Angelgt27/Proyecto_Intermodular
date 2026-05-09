package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuperadminAdminsListaActivity extends AppCompatActivity {

    private RecyclerView rvAdmins;
    private SuperadminAdminAdapter adapter;
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

        TextView tvTitulo = findViewById(R.id.tvTituloSalasList);
        tvTitulo.setText("Gestion de Administradores");

        findViewById(R.id.btnVolverSalasList).setOnClickListener(v -> finish());

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        rvAdmins = findViewById(R.id.rvSalasListAdmin);
        rvAdmins.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SuperadminAdminAdapter(new SuperadminAdminAdapter.OnAdminActionListener() {
            @Override
            public void onEditar(Usuario admin) {
                Intent intent = new Intent(SuperadminAdminsListaActivity.this, SuperadminAdminFormActivity.class);
                intent.putExtra("ID_ADMIN", admin.getIdUsuario());
                intent.putExtra("NOMBRE", admin.getNombre());
                intent.putExtra("EMAIL", admin.getEmail());
                if (admin.getFkCineGestionado() != null) {
                    intent.putExtra("FK_CINE", admin.getFkCineGestionado());
                }
                startActivity(intent);
            }

            @Override
            public void onEliminar(int idAdmin) {
                eliminarAdmin(idAdmin);
            }
        });
        rvAdmins.setAdapter(adapter);

        findViewById(R.id.fabAgregarSalaList).setOnClickListener(v -> {
            startActivity(new Intent(this, SuperadminAdminFormActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarAdmins();
    }

    private void cargarAdmins() {
        apiService.getAdminsSuperadmin(token).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setAdmins(response.body());
                }
            }
            @Override public void onFailure(Call<List<Usuario>> call, Throwable t) {}
        });
    }

    private void eliminarAdmin(int idAdmin) {
        apiService.eliminarAdminSuperadmin(token, idAdmin).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SuperadminAdminsListaActivity.this, "Administrador eliminado", Toast.LENGTH_SHORT).show();
                    cargarAdmins();
                }
            }
            @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {}
        });
    }
}