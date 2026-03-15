package com.example.printergrado.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.CheckBox; // <-- AÑADIDO

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.printergrado.R;
import com.example.printergrado.viewmodel.AuthViewModel;
import com.example.printergrado.ui.main.MainActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnIniciarSesion, btnRegistrarse;
    private CheckBox cbRecordarSesion; // <-- AÑADIDO

    // Instancia del ViewModel
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar el ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Enlazar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        cbRecordarSesion = findViewById(R.id.cbRecordarSesion); // <-- AÑADIDO

        // Observar los mensajes (Toasts) del ViewModel
        authViewModel.getMensajeToast().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        // Observar si recibimos un Token (Login Exitoso)
        authViewModel.getLoginToken().observe(this, token -> {
            if (token != null) {
                // 1. Abrimos las preferencias compartidas del teléfono
                SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // 2. Guardamos el token
                editor.putString("jwt_token", token);
                editor.apply();

                // 3. Navegamos a la pantalla principal
                Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                // 4. Cerramos el Login para que no pueda volver atrás con el botón de retroceso
                finish();
            }
        });

        // Evento: Botón Iniciar Sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // --- AÑADIDO: Leemos el CheckBox y lo pasamos al ViewModel ---
            boolean recordar = cbRecordarSesion.isChecked();

            // Delegar la lógica al ViewModel
            authViewModel.login(email, password, recordar);
        });

        // Evento: Botón Registrarse (Lleva a la pantalla de registro)
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}