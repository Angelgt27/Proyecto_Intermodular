package com.example.printergrado.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Pelicula;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<Pelicula>> peliculasLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public LiveData<List<Pelicula>> getPeliculas() { return peliculasLiveData; }
    public LiveData<String> getError() { return errorLiveData; }

    public void cargarPeliculas() {
        apiService.getPeliculas().enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    peliculasLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Error al cargar la cartelera");
                }
            }

            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                errorLiveData.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}