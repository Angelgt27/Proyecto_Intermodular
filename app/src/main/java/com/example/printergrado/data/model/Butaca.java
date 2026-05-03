package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

public class Butaca {

    @SerializedName("id_butaca")
    private int idButaca;

    private String fila;

    @SerializedName("columna_grid")
    private String columna;

    @SerializedName("numero_comercial")
    private int numeroComercial;

    private boolean ocupada;

    public int getIdButaca() { return idButaca; }
    public String getFila() { return fila; }
    public String getColumna() { return columna; }
    public int getNumeroComercial() { return numeroComercial; }
    public boolean isOcupada() { return ocupada; }
}