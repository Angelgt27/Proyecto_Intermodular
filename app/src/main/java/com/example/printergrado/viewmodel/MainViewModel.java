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

    public void cargarPeliculas(boolean isAdmin) {
        Log.d("DEPURACION", "1. Pidiendo películas al servidor. Modo Admin: " + isAdmin);

        apiService.getPeliculas(isAdmin).enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {
                Log.d("DEPURACION", "2. Servidor respondió con código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DEPURACION", "3. Éxito. Películas recibidas: " + response.body().size());
                    // ARREGLADO: Ahora inyectamos los datos en la variable correcta
                    peliculasLiveData.postValue(response.body());
                } else {
                    Log.e("DEPURACION", "Error. El servidor devolvió algo raro o vacío.");
                    mensajeLiveData.postValue("Error del servidor: Código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                Log.e("DEPURACION", "Fallo total de red: " + t.getMessage());
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