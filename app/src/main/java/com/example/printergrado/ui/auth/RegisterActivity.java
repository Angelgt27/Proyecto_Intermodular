package com.example.printergrado.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
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
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        etNombre = findViewById(R.id.etRegNombre);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnVolverLogin = findViewById(R.id.btnVolverLogin);

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

        btnCrearCuenta.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Por favor, introduce un correo electronico valido", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.register(nombre, email, password, confirmPassword);
        });

        btnVolverLogin.setOnClickListener(v -> finish());
    }
}