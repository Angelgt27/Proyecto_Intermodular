package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

public class Pelicula {
    @SerializedName("id_pelicula")
    private int idPelicula;

    private String titulo;
    private int duracion;
    private String genero;
    private String sinopsis;

    @SerializedName("fk_cine")
    private int fkCine;

    // Getters
    public int getIdPelicula() { return idPelicula; }
    public String getTitulo() { return titulo; }
    public int getDuracion() { return duracion; }
    public String getGenero() { return genero; }
    public String getSinopsis() { return sinopsis; }
    public int getFkCine() { return fkCine; }
}