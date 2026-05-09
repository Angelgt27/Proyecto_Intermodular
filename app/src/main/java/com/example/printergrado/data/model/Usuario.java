package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id_usuario")
    private int idUsuario;

    private String nombre;
    private String email;
    private String rol;

    @SerializedName("fk_cine_gestionado")
    private Integer fkCineGestionado;

    @SerializedName("nombre_cine")
    private String nombreCine;

    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public Integer getFkCineGestionado() { return fkCineGestionado; }
    public String getNombreCine() { return nombreCine; }
}