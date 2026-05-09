package com.example.printergrado.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.CheckBox;

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
    private CheckBox cbRecordarSesion;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        cbRecordarSesion = findViewById(R.id.cbRecordarSesion);

        

        authViewModel.getMensajeToast().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        

        authViewModel.getLoginRole().observe(this, rol -> {
            if (rol != null) {
                SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("rol", rol).apply();
            }
        });

        

        authViewModel.getLoginToken().observe(this, token -> {
            if (token != null) {
                SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("jwt_token", token);
                editor.apply();

                Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        

        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();


            boolean recordar = false;
            if (cbRecordarSesion != null) {
                recordar = cbRecordarSesion.isChecked();
            }

            

            authViewModel.login(email, password, recordar);
        });

        

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}