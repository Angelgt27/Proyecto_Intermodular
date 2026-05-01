package com.example.printergrado.data.model;

public class Butaca {
    private int id_butaca;
    private String fila;
    private String numero;
    private boolean ocupada;
    private boolean seleccionada; // Estado local para saber si la hemos pulsado

    public int getIdButaca() { return id_butaca; }
    public String getFila() { return fila; }
    public String getNumero() { return numero; }
    public boolean isOcupada() { return ocupada; }

    public boolean isSeleccionada() { return seleccionada; }
    public void setSeleccionada(boolean seleccionada) { this.seleccionada = seleccionada; }
}