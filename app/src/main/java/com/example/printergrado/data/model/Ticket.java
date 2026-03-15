package com.example.printergrado.data.model;

public class Ticket {
    private int id_sesion;
    private String titulo;
    private String fecha;
    private String hora;
    private int cantidad_tickets;

    public int getIdSesion() { return id_sesion; }
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public int getCantidadTickets() { return cantidad_tickets; }
}