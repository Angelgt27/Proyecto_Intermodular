package com.example.printergrado.data.model;

public class Sala {
    private int id_sala;
    private String nombre;

    public int getIdSala() { return id_sala; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}