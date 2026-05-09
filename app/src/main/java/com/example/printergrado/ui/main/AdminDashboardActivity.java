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

import com.example.printergrado.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_admin_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorDashboard);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverDashboard).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        boolean isSuperAdmin = "Superadmin".equals(prefs.getString("rol", "Usuario"));

        TextView tvTitulo = findViewById(R.id.tvTituloDashboard);

        View cardCines = findViewById(R.id.cardGestionCines);
        View cardAdmins = findViewById(R.id.cardGestionAdmins);
        View cardDatos = findViewById(R.id.cardDatosCine);
        View cardSalas = findViewById(R.id.cardGestionSalas);

        if (isSuperAdmin) {
            tvTitulo.setText("Panel Superadmin");

            // Ocultamos las del Admin normal
            cardDatos.setVisibility(View.GONE);
            cardSalas.setVisibility(View.GONE);

            // Mostramos las exclusivas
            cardCines.setVisibility(View.VISIBLE);
            cardAdmins.setVisibility(View.VISIBLE);

            cardCines.setOnClickListener(v -> {
                startActivity(new Intent(this, SuperadminCinesListaActivity.class));
            });

            cardAdmins.setOnClickListener(v -> {
                // startActivity(new Intent(this, SuperadminAdminsListaActivity.class));
            });

        } else {
            // Es un Admin normal
            tvTitulo.setText("Panel de Control");
            cardCines.setVisibility(View.GONE);
            cardAdmins.setVisibility(View.GONE);
            cardDatos.setVisibility(View.VISIBLE);
            cardSalas.setVisibility(View.VISIBLE);

            cardDatos.setOnClickListener(v -> startActivity(new Intent(this, AdminDatosCineActivity.class)));
            cardSalas.setOnClickListener(v -> startActivity(new Intent(this, AdminSalasListaActivity.class)));
        }
    }
}