package com.example.roomtrip.data;

import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.PedidoTaxi;

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

    public static List<PedidoTaxi> getPedidosTaxiEjemplo() {
        List<PedidoTaxi> lista = new ArrayList<>();
        lista.add(new PedidoTaxi("Carlos Mendoza", 4.9f, "Hotel La Dolce Vita", "Aeropuerto Jorge Chávez", 8));
        lista.add(new PedidoTaxi("María Torres", 4.7f, "Hotel Miraflores Park", "Aeropuerto Jorge Chávez", 12));
        lista.add(new PedidoTaxi("Juan Pérez", 4.5f, "JW Marriott Hotel", "Aeropuerto Jorge Chávez", 15));
        lista.add(new PedidoTaxi("Lucía Ramírez", 5.0f, "Hotel Westin Lima", "Aeropuerto Jorge Chávez", 18));
        lista.add(new PedidoTaxi("Diego Salazar", 4.2f, "Hotel Costa del Sol", "Aeropuerto Jorge Chávez", 5));
        return lista;
    }
}