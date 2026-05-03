package com.example.printergrado.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Ticket;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    // ÚNICAS VARIABLES NECESARIAS
    private final MutableLiveData<List<Pelicula>> peliculasLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeLiveData = new MutableLiveData<>();

    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<List<Pelicula>> getPeliculas() { return peliculasLiveData; }
    public LiveData<List<Ticket>> getTickets() { return ticketsLiveData; }
    public LiveData<String> getMensajes() { return mensajeLiveData; }

    public void cargarPeliculas(boolean isAdmin, String fecha) {
        Log.d("DEPURACION", "1. Pidiendo películas. Admin: " + isAdmin + " | Fecha: " + fecha);

        apiService.getPeliculas(isAdmin, fecha).enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    peliculasLiveData.postValue(response.body());
                } else {
                    mensajeLiveData.postValue("Error del servidor al buscar películas");
                }
            }

            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                mensajeLiveData.postValue("Error de conexión crítico.");
            }
        });
    }

    public void cargarTickets(String token) {
        apiService.getMisTickets(token).enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticketsLiveData.setValue(response.body());
                } else {
                    // Si el servidor da error, enviamos lista vacía para que quite la rueda
                    ticketsLiveData.setValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<List<Ticket>> call, Throwable t) {
                // Si no hay red, también pasamos lista vacía y avisamos
                ticketsLiveData.setValue(new ArrayList<>());
                mensajeLiveData.setValue("Problema de conexión al buscar entradas");
            }
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

    public void escanearTicket(String token, String qrCode) {
        apiService.escanearTicket(token, qrCode).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeLiveData.setValue(response.body().getMensaje());
                } else {
                    mensajeLiveData.setValue("Error: Entrada rechazada o ya usada");
                }
            }
            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                mensajeLiveData.setValue("Error de conexión con el escáner");
            }
        });
    }
}