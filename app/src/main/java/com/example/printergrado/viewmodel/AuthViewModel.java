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

    private final MutableLiveData<String> mensajeToast = new MutableLiveData<>();
    private final MutableLiveData<String> loginToken = new MutableLiveData<>();
    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);
    private final MutableLiveData<String> loginRole = new MutableLiveData<>();

    public LiveData<String> getMensajeToast() { return mensajeToast; }
    public LiveData<String> getLoginToken() { return loginToken; }
    public LiveData<Boolean> getAuthSuccess() { return authSuccess; }
    public LiveData<String> getLoginRole() { return loginRole; }

    

    public void login(String email, String password, boolean recordar) {

        

        if (email.isEmpty() || password.isEmpty()) {
            mensajeToast.setValue("Por favor, introduce tu correo y contraseña");
            return;
        }

        LoginRequest request = new LoginRequest(email, password, recordar);

        apiService.loginUsuario(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    

                    

                    loginRole.setValue(response.body().getRol());
                    loginToken.setValue(response.body().getAccessToken());
                } else {
                    mensajeToast.setValue("Error de credenciales. Revisa tu email o contraseña.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                mensajeToast.setValue("Error de conexión al servidor");
            }
        });
    }

    

    public void register(String nombre, String email, String password, String confirmPassword) {
        

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mensajeToast.setValue("Por favor, rellena todos los campos");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mensajeToast.setValue("Las contraseñas no coinciden");
            return;
        }

        if (password.length() < 6) {
            mensajeToast.setValue("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        RegistroRequest request = new RegistroRequest(nombre, email, password);

        apiService.registrarUsuario(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeToast.setValue(response.body().getMensaje());
                    authSuccess.setValue(true); 

                } else {
                    mensajeToast.setValue("Error al registrar: Puede que el correo ya exista.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                mensajeToast.setValue("Error de conexión al registrarse");
            }
        });
    }
}