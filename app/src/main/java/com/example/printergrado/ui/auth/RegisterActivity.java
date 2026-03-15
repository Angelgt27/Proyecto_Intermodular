package com.example.printergrado.ui.auth;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.printergrado.viewmodel.AuthViewModel;
import com.example.printergrado.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnCrearCuenta, btnVolverLogin;

    // Instancia del ViewModel
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar el ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etNombre = findViewById(R.id.etRegNombre);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnVolverLogin = findViewById(R.id.btnVolverLogin);

        // Observar los mensajes del ViewModel
        authViewModel.getMensajeToast().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getAuthSuccess().observe(this, success -> {
            if (success != null && success) {
                finish();
            }
        });

        // Eventos de click
        btnCrearCuenta.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Delegar la lógica al ViewModel
            authViewModel.register(nombre, email, password, confirmPassword);
        });

        btnVolverLogin.setOnClickListener(v -> finish());
    }
}