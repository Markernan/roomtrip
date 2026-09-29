package com.example.roomtrip.data;

import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Hotel> getHotelesEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Westin Lima", "San Isidro, Lima", "admin@westin.pe", "Activo"));
        lista.add(new Hotel("JW Marriott Hotel", "Miraflores, Lima", "admin@marriott.pe", "Activo"));
        lista.add(new Hotel("Hotel Costa del Sol", "Aeropuerto Jorge Chávez", "admin@costadelsol.pe", "Activo"));
        lista.add(new Hotel("Hotel Aranwa Valley", "Valle Sagrado, Cusco", "admin@aranwa.pe", "Inactivo"));
        return lista;
    }

    public static List<Habitacion> getHabitacionesEjemplo() {
        List<Habitacion> lista = new ArrayList<>();
        lista.add(new Habitacion("Habitación Standard", "2 Adultos", 120.00, "Disponible"));
        lista.add(new Habitacion("Suite Presidencial", "2 Adultos, 2 Niños", 350.00, "Ocupada"));
        lista.add(new Habitacion("Habitación Económica", "1 Adulto", 80.00, "Disponible"));
        lista.add(new Habitacion("Junior Suite", "2 Adultos, 1 Niño", 220.00, "Mantenimiento"));
        return lista;
    }
}