package com.example.printergrado.data.api;

import com.example.printergrado.data.model.AuthResponse;
import com.example.printergrado.data.model.RegistroRequest;

import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Body;

public interface ApiService {
    @POST("api/register")
    Call<AuthResponse> registrarUsuario(@Body RegistroRequest request);
}
