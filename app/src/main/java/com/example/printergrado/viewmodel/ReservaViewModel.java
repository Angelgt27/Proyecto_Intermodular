package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservaViewModel extends ViewModel {

    private final MutableLiveData<String> mensajeReserva = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservaExitosa = new MutableLiveData<>();
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<String> getMensajeReserva() { return mensajeReserva; }
    public LiveData<Boolean> getReservaExitosa() { return reservaExitosa; }

    

    public void hacerReservaConButacas(String token, int idSesion, List<Integer> idsButacas) {

        

        Map<String, Object> request = new HashMap<>();
        request.put("id_sesion", idSesion);
        request.put("ids_butacas", idsButacas);

        apiService.crearReservaConButacas(token, request).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeReserva.setValue(response.body().getMensaje());
                    reservaExitosa.setValue(true);
                } else {
                    mensajeReserva.setValue("Error al reservar. Puede que alguien haya comprado la butaca.");
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                mensajeReserva.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}