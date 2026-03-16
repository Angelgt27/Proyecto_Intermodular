package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaRequest;
import com.example.printergrado.data.model.ReservaResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaViewModel extends ViewModel {

    private final MutableLiveData<String> mensajeReserva = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservaExitosa = new MutableLiveData<>();
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<String> getMensajeReserva() { return mensajeReserva; }
    public LiveData<Boolean> getReservaExitosa() { return reservaExitosa; }

    // --- AÑADIDO: Ahora recibe 'fechaSeleccionada' como parámetro ---
    public void hacerReserva(String token, int idSesion, int cantidadEntradas, String fechaSeleccionada) {

        ReservaRequest request = new ReservaRequest(idSesion, cantidadEntradas, fechaSeleccionada);

        apiService.crearReserva(token, request).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeReserva.setValue(response.body().getMensaje());
                    reservaExitosa.setValue(true);
                } else {
                    mensajeReserva.setValue("Error al reservar. Comprueba tu sesión o los datos.");
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                mensajeReserva.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}