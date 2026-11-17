package com.example.roomtrip.data.api;

/** Cuerpo de {@code POST /api/auth/login}. */
public class LoginApi {

    private String correo;
    private String password;

    public LoginApi() {
    }

    public LoginApi(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }
}
