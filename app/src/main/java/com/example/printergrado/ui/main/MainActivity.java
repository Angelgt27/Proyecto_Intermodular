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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.ui.auth.LoginActivity;
import com.example.printergrado.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private View barraSuperior;
    private BottomNavigationView bottomNav;

    private RecyclerView rvPeliculas;
    private PeliculaAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- AÑADIDO: SEGURIDAD Y TOKEN ---
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null) {
            // No hay token, mandamos al usuario al Login y cerramos el Main
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // ----------------------------------

        // 1. Pantalla completa (borde a borde)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        // 2. Enlazar vistas
        barraSuperior = findViewById(R.id.barraSuperiorMain);
        bottomNav = findViewById(R.id.bottomNavigation);
        rvPeliculas = findViewById(R.id.rvPeliculas);

        // 3. Gestionar Insets (Cámara y Gestos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            barraSuperior.getLayoutParams().height = insets.top;
            barraSuperior.requestLayout();
            bottomNav.setPadding(0, 0, 0, insets.bottom);
            return windowInsets;
        });

        // 4. Configurar RecyclerView y Adaptador
        rvPeliculas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PeliculaAdapter();
        rvPeliculas.setAdapter(adapter);

        // 5. Configurar ViewModel y observar los datos
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getPeliculas().observe(this, peliculas -> {
            if (peliculas != null) {
                adapter.setPeliculas(peliculas); // Mandamos las pelis a la lista
            }
        });

        mainViewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        // 6. Finalmente, pedimos a la API que descargue las películas
        mainViewModel.cargarPeliculas();
    }
}