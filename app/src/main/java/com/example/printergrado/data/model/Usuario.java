package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id_usuario")
    private int idUsuario;

    private String nombre;
    private String email;
    private String rol;

    // Getters
    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
}