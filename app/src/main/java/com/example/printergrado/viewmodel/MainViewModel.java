package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Ticket;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<Pelicula>> peliculasLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeLiveData = new MutableLiveData<>();

    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<List<Pelicula>> getPeliculas() { return peliculasLiveData; }
    public LiveData<List<Ticket>> getTickets() { return ticketsLiveData; }
    public LiveData<String> getMensajes() { return mensajeLiveData; }

    public void cargarPeliculas() {
        apiService.getPeliculas().enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    peliculasLiveData.setValue(response.body());
                } else {
                    mensajeLiveData.setValue("Error al cargar la cartelera");
                }
            }
            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                mensajeLiveData.setValue("Error de conexión");
            }
        });
    }

    public void cargarTickets(String token) {
        apiService.getMisTickets(token).enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticketsLiveData.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Ticket>> call, Throwable t) {}
        });
    }

    public void eliminarTicket(String token, int idSesion) {
        apiService.cancelarTicket(token, idSesion).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeLiveData.setValue(response.body().getMensaje());
                    cargarTickets(token); // Recargamos la lista automáticamente
                }
            }
            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                mensajeLiveData.setValue("Error al eliminar");
            }
        });
    }
}