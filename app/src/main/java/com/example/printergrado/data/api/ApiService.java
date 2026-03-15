package com.example.printergrado.data.api;

import com.example.printergrado.data.model.AuthResponse;
import com.example.printergrado.data.model.LoginRequest;
import com.example.printergrado.data.model.RegistroRequest;
import com.example.printergrado.data.model.Pelicula;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/register")
    Call<AuthResponse> registrarUsuario(@Body RegistroRequest request);

    @POST("api/login")
    Call<AuthResponse> loginUsuario(@Body LoginRequest request);

    @GET("api/peliculas")
    Call<List<Pelicula>> getPeliculas();
}