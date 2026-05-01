package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
import com.example.printergrado.data.model.Ticket;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_historial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorHistorial);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverHistorial).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvHistorial);
        rv.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("jwt_token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.obtenerHistorial(token).enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HistorialAdapter adapter = new HistorialAdapter(response.body());
                    rv.setAdapter(adapter);
                }
            }
            @Override public void onFailure(Call<List<Ticket>> call, Throwable t) {}
        });
    }
}