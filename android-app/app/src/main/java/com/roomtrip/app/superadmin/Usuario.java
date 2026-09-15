package com.roomtrip.app.superadmin;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String name;
    private String email;
    private String role;
    private String phone;
    private boolean isActive;

    // Campos adicionales para roles específicos
    private String licencia;
    private String placaAuto;
    private String modeloVehiculo;
    private String hotelAsignado;
    private String historialReservas;
    private String direccion;

    public Usuario() {}

    public Usuario(String name, String email, String role, String phone, boolean isActive) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.isActive = isActive;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }

    public String getPlacaAuto() { return placaAuto; }
    public void setPlacaAuto(String placaAuto) { this.placaAuto = placaAuto; }

    public String getModeloVehiculo() { return modeloVehiculo; }
    public void setModeloVehiculo(String modeloVehiculo) { this.modeloVehiculo = modeloVehiculo; }

    public String getHotelAsignado() { return hotelAsignado; }
    public void setHotelAsignado(String hotelAsignado) { this.hotelAsignado = hotelAsignado; }

    public String getHistorialReservas() { return historialReservas; }
    public void setHistorialReservas(String historialReservas) { this.historialReservas = historialReservas; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}