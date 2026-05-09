package com.example.printergrado.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ticket {
    private int id_sesion;
    private String titulo;
    private String fecha;
    private String hora;
    private String cine;
    private String imagen; 

    private List<String> butacas;
    private List<String> qr_codes;
    @SerializedName("cantidad")
    private int cantidad_tickets;
    private String estado;

    public int getIdSesion() { return id_sesion; }
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getCine() { return cine; }
    public String getImagen() { return imagen; } 

    public String getEstado() { return estado; }
    public List<String> getButacas() { return butacas; }
    public List<String> getQrCodes() { return qr_codes; }
    public int getCantidadTickets() { return cantidad_tickets; }
}