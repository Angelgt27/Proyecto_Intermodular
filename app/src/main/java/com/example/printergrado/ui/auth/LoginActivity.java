package com.example.printergrado.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.printergrado.viewmodel.AuthViewModel;
import com.example.printergrado.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnIniciarSesion, btnRegistrarse;

    // Instancia del ViewModel
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar el ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        // Observar los mensajes del ViewModel
        authViewModel.getMensajeToast().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        // Eventos de click
        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            // Delegar la lógica al ViewModel
            authViewModel.login(email, password);
        });

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}