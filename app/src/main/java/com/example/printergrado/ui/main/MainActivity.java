package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.printergrado.R;
import com.example.printergrado.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Comprobamos las preferencias ANTES de cargar la pantalla
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        // 2. Si no hay token, lo mandamos al Login inmediatamente
        if (token == null || token.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            // Cerramos el MainActivity para que no quede en segundo plano
            finish();

            // El 'return' evita que se siga ejecutando el código de abajo
            return;
        }

        // 3. Si llega hasta aquí, es que SÍ hay token. Cargamos la interfaz normal.
        setContentView(R.layout.activity_main);

        // TODO: Cargar la lista de películas, configurar el menú inferior, etc.
    }
}