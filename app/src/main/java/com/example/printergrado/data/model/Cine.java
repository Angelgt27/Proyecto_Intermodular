package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

public class Cine {
    @SerializedName("id_cine")
    private int idCine;
    private String nombre;
    private String direccion;
    private String telefono;

    public int getIdCine() { return idCine; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
}