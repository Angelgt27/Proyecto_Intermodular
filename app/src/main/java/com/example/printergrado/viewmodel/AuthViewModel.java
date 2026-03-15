package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.AuthResponse;
import com.example.printergrado.data.model.RegistroRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    // LiveData para enviar mensajes a la UI (errores o éxitos)
    private final MutableLiveData<String> mensajeToast = new MutableLiveData<>();
    // LiveData para saber si el login/registro fue exitoso
    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();

    // Getters para que la Activity observe estos datos
    public LiveData<String> getMensajeToast() {
        return mensajeToast;
    }

    public LiveData<Boolean> getAuthSuccess() {
        return authSuccess;
    }

    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);


    // Lógica de Iniciar Sesión
    public void login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            mensajeToast.setValue("Por favor, completa todos los campos");
            return;
        }

        // TODO: Aquí llamaremos al Repository que conectará con Flask
        // Simulamos un éxito por ahora
        mensajeToast.setValue("Conectando con la API...");
        // authSuccess.setValue(true); // Se activará cuando la API responda OK
    }

    // Lógica de Registro
    public void register(String nombre, String email, String password, String confirmPassword) {
        // Validaciones locales (las que ya tenías)
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            mensajeToast.setValue("Por favor, completa todos los campos");
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

        mensajeToast.setValue("Conectando con el servidor...");

        // 1. Preparamos los datos
        RegistroRequest request = new RegistroRequest(nombre, email, password);

        // 2. Hacemos la llamada asíncrona a Flask
        apiService.registrarUsuario(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ¡Código 201: Creado con éxito!
                    mensajeToast.setValue(response.body().getMensaje());
                    authSuccess.setValue(true); // Avisamos a la Activity para que vuelva al Login
                } else {
                    // Código 400: Error (ej. "El correo ya está registrado")
                    mensajeToast.setValue("Error en el registro. Quizás el correo ya exista.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Error de red (No hay internet, servidor apagado, IP incorrecta...)
                mensajeToast.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}