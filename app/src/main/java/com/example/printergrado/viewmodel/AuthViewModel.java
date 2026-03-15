package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.AuthResponse;
import com.example.printergrado.data.model.LoginRequest;
import com.example.printergrado.data.model.RegistroRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    // LiveData para comunicarnos con la interfaz (Activity)
    private final MutableLiveData<String> mensajeToast = new MutableLiveData<>();
    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> loginToken = new MutableLiveData<>();

    // Instanciamos nuestro servicio API de Retrofit
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    // Getters para que la Activity pueda observar
    public LiveData<String> getMensajeToast() { return mensajeToast; }
    public LiveData<Boolean> getAuthSuccess() { return authSuccess; }
    public LiveData<String> getLoginToken() { return loginToken; }

    // --- LÓGICA DE LOGIN ---
    public void login(String email, String password) {
        // Validaciones silenciosas: si falta algo, simplemente no hace nada
        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        LoginRequest request = new LoginRequest(email, password);

        apiService.loginUsuario(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ¡Login exitoso! Guardamos el token en el LiveData
                    loginToken.setValue(response.body().getAccessToken());
                }
                // Si hay error 401 (mala contraseña), ahora falla en silencio
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Si no hay internet, falla en silencio
            }
        });
    }

    // --- LÓGICA DE REGISTRO ---
    public void register(String nombre, String email, String password, String confirmPassword) {
        // Validaciones silenciosas
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            return;
        }

        if (password.length() < 6) {
            return;
        }

        RegistroRequest request = new RegistroRequest(nombre, email, password);

        apiService.registrarUsuario(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Mantenemos SOLO el mensaje de éxito del servidor
                    mensajeToast.setValue(response.body().getMensaje());
                    authSuccess.setValue(true); // Avisamos para volver al Login
                }
                // Si el correo ya existe (400), falla en silencio
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Si no hay internet, falla en silencio
            }
        });
    }
}