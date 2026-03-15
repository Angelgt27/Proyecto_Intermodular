package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.printergrado.R;
import com.example.printergrado.ui.auth.LoginActivity;
import com.example.printergrado.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private View barraSuperior;
    private BottomNavigationView bottomNav;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 1. SEGURIDAD ---
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || !isTokenValido(token)) {
            prefs.edit().remove("jwt_token").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // --- 2. VISTAS ---
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        barraSuperior = findViewById(R.id.barraSuperiorMain);
        bottomNav = findViewById(R.id.bottomNavigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            barraSuperior.getLayoutParams().height = insets.top;
            barraSuperior.requestLayout();
            bottomNav.setPadding(0, 0, 0, insets.bottom);
            return windowInsets;
        });

        // --- 3. VIEWMODEL COMPARTIDO (Solo para Toasts de error aquí) ---
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getMensajes().observe(this, msj -> {
            if (msj != null) Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
        });

        // Pedimos descargar las películas una sola vez al abrir la app
        mainViewModel.cargarPeliculas();

        // --- 4. NAVEGACIÓN CON FRAGMENTS ---
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_tickets) {
                selectedFragment = new TicketsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment(); // Pestaña de Perfil integrada
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Seleccionar Home por defecto si es la primera vez que arranca
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean isTokenValido(String token) {
        try {
            String[] split = token.split("\\.");
            if (split.length < 2) return false;
            String payload = new String(Base64.decode(split[1], Base64.URL_SAFE));
            JSONObject jsonObject = new JSONObject(payload);
            if (jsonObject.has("exp")) {
                long exp = jsonObject.getLong("exp");
                return (System.currentTimeMillis() / 1000) < exp;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}