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
import com.example.printergrado.ui.auth.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private ApiService apiService;
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout btnLicencia = view.findViewById(R.id.btnLicencia);
        LinearLayout btnEliminar = view.findViewById(R.id.btnEliminarCuentaSettings);

        LinearLayout btnGestionCine = view.findViewById(R.id.btnGestionCine);
        View separadorGestion = view.findViewById(R.id.separadorGestionCine);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");
        String rol = prefs.getString("rol", "Usuario");

        boolean isAdmin = "Admin".equals(rol);
        boolean isSuperAdmin = "Superadmin".equals(rol);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Si es cualquiera de los dos admins, mostramos el botón
        if (isAdmin || isSuperAdmin) {
            btnGestionCine.setVisibility(View.VISIBLE);
            separadorGestion.setVisibility(View.VISIBLE);

            // Si es Superadmin, cambiamos el texto visualmente buscando el TextView dentro del botón
            if (isSuperAdmin) {
                for (int i = 0; i < btnGestionCine.getChildCount(); i++) {
                    View child = btnGestionCine.getChildAt(i);
                    if (child instanceof TextView) {
                        ((TextView) child).setText("Ajustes de SuperAdministrador");
                    }
                }
            }

            btnGestionCine.setOnClickListener(v -> {
                // Por ahora abre el panel de Admin, en la Fase 3 lo adaptaremos
                Intent intent = new Intent(requireContext(), AdminDashboardActivity.class);
                startActivity(intent);
            });
        }

        btnLicencia.setOnClickListener(v -> mostrarDialogoLicencia());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminarCuenta());

        return view;
    }

    private void mostrarDialogoLicencia() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Licencia de Uso")
                .setMessage("Este proyecto se distribuye bajo la licencia Atribución-No Comercial (CC BY-NC).\n\n" +
                        "Esta licencia permite que cualquier persona pueda utilizar, modificar y distribuir el código de la aplicación " +
                        "siempre que sea con fines no comerciales, y siempre y cuando respeten la autoría original.")
                .setPositiveButton("Entendido", null)
                .show();
    }

    private void mostrarDialogoEliminarCuenta() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Eliminar cuenta?")
                .setMessage("¿Estás completamente seguro? Esta acción borrará todas tus reservas y no se puede deshacer.")
                .setPositiveButton("Eliminar para siempre", (dialog, which) -> {
                    apiService.eliminarCuenta(token).enqueue(new Callback<ReservaResponse>() {
                        @Override
                        public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                                cerrarSesion();
                            }
                        }
                        @Override public void onFailure(Call<ReservaResponse> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}