package com.example.printergrado.data.model;

public class AuthResponse {
    private String mensaje;
    private String error;
    private String access_token;
    private String rol;

    public String getMensaje() { return mensaje; }
    public String getError() { return error; }
    public String getAccessToken() { return access_token; }
    public String getRol() { return rol; }
}