package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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
        LinearLayout btnCambiarPassword = view.findViewById(R.id.btnCambiarPassword);
        LinearLayout btnHistorialReservas = view.findViewById(R.id.btnHistorialReservas);
        LinearLayout btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        View separadorHistorial = view.findViewById(R.id.separadorHistorial);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");
        String rol = prefs.getString("rol", "Usuario");

        apiService = ApiClient.getClient().create(ApiService.class);

        

        if ("Admin".equals(rol) || "Superadmin".equals(rol)) {
            btnHistorialReservas.setVisibility(View.GONE);
            if (separadorHistorial != null) {
                separadorHistorial.setVisibility(View.GONE);
            }
        }

        cargarPerfil();

        btnCambiarNombre.setOnClickListener(v -> mostrarDialogoCambiarNombre());
        btnCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword());

        btnHistorialReservas.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), HistorialActivity.class);
            startActivity(intent);
        });

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

    private void mostrarDialogoCambiarPassword() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 0);

        TextInputLayout tilAntigua = new TextInputLayout(requireContext());
        TextInputEditText etAntigua = new TextInputEditText(requireContext());
        etAntigua.setHint("Contrasena actual");
        etAntigua.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilAntigua.addView(etAntigua);
        layout.addView(tilAntigua);

        TextInputLayout tilNueva = new TextInputLayout(requireContext());
        TextInputEditText etNueva = new TextInputEditText(requireContext());
        etNueva.setHint("Nueva contrasena");
        etNueva.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilNueva.addView(etNueva);
        tilNueva.setPadding(0, 20, 0, 0);
        layout.addView(tilNueva);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cambiar Contrasena")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String actual = etAntigua.getText().toString().trim();
                    String nueva = etNueva.getText().toString().trim();
                    if (!actual.isEmpty() && !nueva.isEmpty()) {
                        actualizarPasswordServidor(actual, nueva);
                    } else {
                        Toast.makeText(getContext(), "Rellena ambos campos", Toast.LENGTH_SHORT).show();
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

    private void actualizarPasswordServidor(String actual, String nueva) {
        Map<String, String> body = new HashMap<>();
        body.put("password_actual", actual);
        body.put("password_nueva", nueva);

        apiService.cambiarPassword(token, body).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Contrasena actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "La contrasena actual es incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexion", Toast.LENGTH_SHORT).show();
            }
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