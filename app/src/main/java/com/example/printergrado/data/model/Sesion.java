package com.example.printergrado.data.model;
public class Sesion {
    private int id_sesion;
    private String fecha;
    private String hora;
    private String cine;
    private double precio;
    private int fk_sala;
    public int getIdSesion() { return id_sesion; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getCine() { return cine; }
    public double getPrecio() { return precio; }
    public int getFkSala() { return fk_sala; }
}