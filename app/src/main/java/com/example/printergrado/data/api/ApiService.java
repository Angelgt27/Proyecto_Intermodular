package com.example.printergrado.data.api;

import com.example.printergrado.data.model.AuthResponse;
import com.example.printergrado.data.model.Cine;
import com.example.printergrado.data.model.LoginRequest;
import com.example.printergrado.data.model.RegistroRequest;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.data.model.ReservaRequest;
import com.example.printergrado.data.model.ReservaResponse;
import com.example.printergrado.data.model.Sala;
import com.example.printergrado.data.model.Ticket;
import com.example.printergrado.data.model.Butaca;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/register")
    Call<AuthResponse> registrarUsuario(@Body RegistroRequest request);

    @POST("api/login")
    Call<AuthResponse> loginUsuario(@Body LoginRequest request);

    @GET("api/peliculas")
    Call<List<Pelicula>> getPeliculas(@retrofit2.http.Query("admin") boolean isAdmin, @retrofit2.http.Query("fecha") String fecha);

    @POST("api/reservas")
    Call<ReservaResponse> crearReservaConButacas(@Header("Authorization") String token, @Body Map<String, Object> request);

    @GET("api/reservas")
    Call<List<Ticket>> getMisTickets(@Header("Authorization") String token);

    @DELETE("api/reservas/{id_sesion}")
    Call<ReservaResponse> cancelarTicket(@Header("Authorization") String token, @Path("id_sesion") int idSesion);

    @POST("api/escanear/{qr_code}")
    Call<ReservaResponse> escanearTicket(@Header("Authorization") String token, @Path("qr_code") String qrCode);

    @GET("api/usuario/perfil")
    Call<com.example.printergrado.data.model.Usuario> obtenerPerfil(@Header("Authorization") String token);

    @PUT("api/usuario/nombre")
    Call<com.example.printergrado.data.model.ReservaResponse> cambiarNombre(@Header("Authorization") String token, @Body java.util.Map<String, String> body);

    @DELETE("api/usuario")
    Call<com.example.printergrado.data.model.ReservaResponse> eliminarCuenta(@Header("Authorization") String token);

    @GET("api/usuario/historial")
    Call<List<com.example.printergrado.data.model.Ticket>> obtenerHistorial(@Header("Authorization") String token);

    @GET("api/sesiones/{id_sesion}/butacas")
    Call<List<Butaca>> getMapaButacas(@Header("Authorization") String token, @Path("id_sesion") int idSesion);

    @POST("api/peliculas")
    Call<ReservaResponse> crearPelicula(@Header("Authorization") String token, @Body java.util.Map<String, Object> body);

    @PUT("api/peliculas/{id}")
    Call<ReservaResponse> actualizarPelicula(@Header("Authorization") String token, @Path("id") int idPelicula, @Body java.util.Map<String, Object> body);

    @DELETE("api/peliculas/{id}")
    Call<ReservaResponse> eliminarPelicula(@Header("Authorization") String token, @Path("id") int idPelicula);

    @GET("api/peliculas/{id}/sesiones")
    Call<java.util.List<com.example.printergrado.data.model.Sesion>> getSesionesPelicula(@Path("id") int idPelicula, @retrofit2.http.Query("admin") boolean isAdmin);

    @POST("api/peliculas/{id}/sesiones")
    Call<ReservaResponse> crearSesion(@Header("Authorization") String token, @Path("id") int idPelicula, @Body java.util.Map<String, Object> body);

    @PUT("api/sesiones/{id}")
    Call<ReservaResponse> actualizarSesion(@Header("Authorization") String token, @Path("id") int idSesion, @Body java.util.Map<String, Object> body);

    @DELETE("api/sesiones/{id}")
    Call<ReservaResponse> eliminarSesion(@Header("Authorization") String token, @Path("id") int idSesion);

    @GET("api/salas")
    Call<List<com.example.printergrado.data.model.Sala>> getSalas(@Header("Authorization") String token);
    @GET("api/admin/cine")
    Call<Map<String, Object>> getDatosCine(@Header("Authorization") String token);

    @PUT("api/admin/cine")
    Call<ReservaResponse> updateDatosCine(@Header("Authorization") String token, @Body Map<String, String> body);

    @GET("api/admin/reservas")
    Call<List<Map<String, Object>>> getEstadisticasReservas(@Header("Authorization") String token);

    @POST("api/admin/salas")
    Call<ReservaResponse> crearSalaAdmin(@Header("Authorization") String token, @Body Map<String, Object> body);

    @DELETE("api/admin/salas/{id_sala}")
    Call<ReservaResponse> eliminarSalaAdmin(@Header("Authorization") String token, @Path("id_sala") int idSala);

    @PUT("api/admin/salas/{id_sala}")
    Call<ReservaResponse> editarSalaAdmin(@Header("Authorization") String token, @Path("id_sala") int idSala, @Body Map<String, Object> body);

    @GET("api/admin/salas/{id_sala}/butacas")
    Call<List<Butaca>> getButacasSalaAdmin(@Header("Authorization") String token, @Path("id_sala") int idSala);
    @GET("api/cines")
    Call<List<Map<String, Object>>> getTodosLosCines();
    @GET("api/superadmin/cines")
    Call<List<Cine>> getCinesSuperadmin(@Header("Authorization") String token);

    @POST("api/superadmin/cines")
    Call<ReservaResponse> crearCineSuperadmin(@Header("Authorization") String token, @Body Map<String, Object> body);

    @DELETE("api/superadmin/cines/{id_cine}")
    Call<ReservaResponse> eliminarCineSuperadmin(@Header("Authorization") String token, @Path("id_cine") int idCine);
    @GET("api/salas")
    Call<List<Sala>> getSalas(@Header("Authorization") String token, @Query("cine_id") Integer cineId);
    @PUT("api/superadmin/cines/{id_cine}")
    Call<ReservaResponse> actualizarCineSuperadmin(@Header("Authorization") String token, @Path("id_cine") int idCine, @Body Map<String, String> body);
}