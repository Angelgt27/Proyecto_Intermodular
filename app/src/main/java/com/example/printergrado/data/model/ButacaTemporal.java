package com.example.printergrado.data.model;

public class ButacaTemporal {
    private String fila;
    private String columna;
    private int numeroComercial;
    private boolean activa;

    public ButacaTemporal(String fila, String columna, boolean activa) {
        this.fila = fila;
        this.columna = columna;
        this.activa = activa;
    }

    public String getFila() { return fila; }
    public String getColumna() { return columna; }
    public int getNumeroComercial() { return numeroComercial; }
    public void setNumeroComercial(int numeroComercial) { this.numeroComercial = numeroComercial; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}