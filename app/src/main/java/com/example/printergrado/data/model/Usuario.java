package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id_usuario")
    private int idUsuario;

    private String nombre;
    private String email;
    private String rol;

    
    private Double saldo;

    @SerializedName("fk_cine_gestionado")
    private Integer fkCineGestionado;

    @SerializedName("nombre_cine")
    private String nombreCine;

    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public Double getSaldo() { return saldo; } 
    public Integer getFkCineGestionado() { return fkCineGestionado; }
    public String getNombreCine() { return nombreCine; }
}