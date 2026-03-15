package com.example.printergrado.data.model;

public class ReservaRequest {
    private int id_sesion;
    private int cantidad_entradas;
    private String fecha_compra;

    public ReservaRequest(int id_sesion, int cantidad_entradas, String fecha_compra) {
        this.id_sesion = id_sesion;
        this.cantidad_entradas = cantidad_entradas;
        this.fecha_compra = fecha_compra;
    }
}