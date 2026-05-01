package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Usuario;
import com.example.printergrado.ui.auth.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail;
    private String token;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);

        LinearLayout btnCambiarNombre = view.findViewById(R.id.btnCambiarNombre);
        LinearLayout btnHistorialReservas = view.findViewById(R.id.btnHistorialReservas);
        LinearLayout btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");
        apiService = ApiClient.getClient().create(ApiService.class);

        // 1. CARGAR DATOS AL ABRIR LA PESTAÑA
        cargarPerfil();

        // 2. CAMBIAR NOMBRE (Ventana emergente con estilo)
        btnCambiarNombre.setOnClickListener(v -> mostrarDialogoCambiarNombre());

        // 3. HISTORIAL DE RESERVAS
        btnHistorialReservas.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HistorialActivity.class);
            startActivity(intent);
        });

        // 4. CERRAR SESIÓN
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        return view;
    }

    private void cargarPerfil() {
        apiService.obtenerPerfil(token).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvProfileName.setText("Hola, " + response.body().getNombre());
                    tvProfileEmail.setText(response.body().getEmail());
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                tvProfileName.setText("Error al cargar perfil");
            }
        });
    }

    private void mostrarDialogoCambiarNombre() {
        // Creamos un campo de texto con el estilo de la app
        TextInputLayout layout = new TextInputLayout(requireContext());
        layout.setPadding(50, 20, 50, 0);
        TextInputEditText input = new TextInputEditText(requireContext());
        input.setHint("Nuevo nombre");
        layout.addView(input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cambiar Nombre")
                .setMessage("Introduce tu nuevo nombre de usuario:")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();
                    if (!nuevoNombre.isEmpty()) {
                        actualizarNombreServidor(nuevoNombre);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarNombreServidor(String nuevoNombre) {
        Map<String, String> body = new HashMap<>();
        body.put("nombre", nuevoNombre);

        apiService.cambiarNombre(token, body).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    tvProfileName.setText("Hola, " + nuevoNombre);
                    Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {}
        });
    }

    private void cerrarSesion() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        prefs.edit().remove("jwt_token").remove("rol").apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}