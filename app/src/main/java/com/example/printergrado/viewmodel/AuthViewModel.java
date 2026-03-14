package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

        // TODO: Aquí llamaremos al Repository que conectará con Flask
        // Simulamos un éxito por ahora
        mensajeToast.setValue("Registrando en la API...");
        // authSuccess.setValue(true); // Se activará cuando la API responda OK
    }
}