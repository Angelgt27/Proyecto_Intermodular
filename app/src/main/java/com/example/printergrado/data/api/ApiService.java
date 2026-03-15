package com.example.printergrado.data.api;

import com.example.printergrado.data.model.AuthResponse;
import com.example.printergrado.data.model.LoginRequest;
import com.example.printergrado.data.model.RegistroRequest;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.data.model.ReservaRequest;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Ticket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @Headers("Connection: close")
    @POST("api/register")
    Call<AuthResponse> registrarUsuario(@Body RegistroRequest request);

    @Headers("Connection: close")
    @POST("api/login")
    Call<AuthResponse> loginUsuario(@Body LoginRequest request);

    @Headers("Connection: close")
    @GET("api/peliculas")
    Call<List<Pelicula>> getPeliculas();

    @Headers("Connection: close")
    @POST("api/reservas")
    Call<ReservaResponse> crearReserva(
            @Header("Authorization") String token,
            @Body ReservaRequest request
    );

    @Headers("Connection: close")
    @GET("api/reservas")
    Call<List<Ticket>> getMisTickets(@Header("Authorization") String token);

    @Headers("Connection: close")
    @DELETE("api/reservas/{id_sesion}")
    Call<ReservaResponse> cancelarTicket(@Header("Authorization") String token, @Path("id_sesion") int idSesion);
}