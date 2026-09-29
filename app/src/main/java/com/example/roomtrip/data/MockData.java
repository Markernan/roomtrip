package com.example.roomtrip.data;

import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.LogEvento;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.data.model.Usuario;
import com.example.roomtrip.R;
import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Hotel> getHotelesEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Westin Lima", "San Isidro, Lima", "admin@westin.pe", EstadoCuenta.ACTIVO));
        lista.add(new Hotel("JW Marriott Hotel", "Miraflores, Lima", "admin@marriott.pe", EstadoCuenta.ACTIVO));
        lista.add(new Hotel("Hotel Costa del Sol", "Aeropuerto Jorge Chávez", "admin@costadelsol.pe", EstadoCuenta.ACTIVO));
        lista.add(new Hotel("Hotel Aranwa Valley", "Valle Sagrado, Cusco", "admin@aranwa.pe", EstadoCuenta.INACTIVO));
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

    // Para SolicitadoActivity (taxista)
    public static List<PedidoTaxi> getPedidosTaxiEjemplo() {
        List<PedidoTaxi> lista = new ArrayList<>();
        lista.add(new PedidoTaxi("Carlos Mendoza", 4.9f, "Hotel La Dolce Vita", "Aeropuerto Jorge Chávez", 8));
        lista.add(new PedidoTaxi("María Torres", 4.7f, "Hotel Miraflores Park", "Aeropuerto Jorge Chávez", 12));
        lista.add(new PedidoTaxi("Juan Pérez", 4.5f, "JW Marriott Hotel", "Aeropuerto Jorge Chávez", 15));
        lista.add(new PedidoTaxi("Lucía Ramírez", 5.0f, "Hotel Westin Lima", "Aeropuerto Jorge Chávez", 18));
        lista.add(new PedidoTaxi("Diego Salazar", 4.2f, "Hotel Costa del Sol", "Aeropuerto Jorge Chávez", 5));
        return lista;
    }

    // Para AuditoriaActivity
    public static List<LogEvento> getLogsEjemplo() {
        List<LogEvento> lista = new ArrayList<>();
        lista.add(new LogEvento("Superadmin activó al usuario cliente \"Ana Gómez\"", "31/08/2026", "14:32:05 hrs", "IP: 190.45.12.34", R.drawable.ic_person));
        lista.add(new LogEvento("Inicio de sesión exitoso - Usuario: Carlos Ruiz (Administrador)", "31/08/2026", "14:15:42 hrs", "Dispositivo: Web", R.drawable.ic_lock));
        lista.add(new LogEvento("Checkout completado en Hotel Miraflores - Reserva #1042", "31/08/2026", "13:47:18 hrs", "Dispositivo: Android 14", R.drawable.ic_card));
        lista.add(new LogEvento("Hotel \"Gran Hotel Lima\" modificado por Ana García", "31/08/2026", "13:22:11 hrs", "IP: 190.45.12.34", R.drawable.ic_home_pin));
        lista.add(new LogEvento("Error al procesar pago - Reserva #1038 (Tarjeta rechazada)", "31/08/2026", "12:58:33 hrs", "Dispositivo: Android 14", R.drawable.ic_warning));
        return lista;
    }

    // Para HotelesActivity
    public static List<Hotel> getHotelesSuperadminEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Italia (Fachada)", "Miraflores, Lima", 4.7f, "Roberto Gómez", "roberto@miraflores.com", "+51 987 654 321", R.drawable.hotel_italia_fachada_calle, true));
        lista.add(new Hotel("Hotel Italia (Suite Azul)", "Centro de Lima, Lima", 4.5f, "María Mendoza", "maria@granhotellima.com", "+51 912 345 678", R.drawable.hotel_habitacion_azul, true));
        lista.add(new Hotel("Hotel Italia (Elegante)", "Plaza de Armas, Cusco", 4.9f, "Carlos Inca", "carlos@cuscoplaza.com", "+51 965 432 198", R.drawable.hotel_habitacion_elegante, true));
        lista.add(new Hotel("Grand Hotel Italia", "Bahía de Paracas, Ica", 4.6f, "Elena Mar", "elena@resortparacas.com", "+51 954 123 789", R.drawable.grand_hotel_italia_fachada, false));
        return lista;
    }

    // Para ReportesActivity
    public static List<Hotel> getReporteHotelesEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Marriott Lima", "Miraflores, Lima", 4.8f, "Admin Marriott", "admin@marriott.com", "+51 987 111 222", R.drawable.foto_hotel_miraflores, true));
        lista.add(new Hotel("Gran Hotel Bolivar", "Centro de Lima, Lima", 4.5f, "Admin Bolivar", "admin@bolivar.com", "+51 987 333 444", R.drawable.hotel_italia_fachada_calle, true));
        lista.add(new Hotel("Hotel Palacio del Inka", "Cusco", 4.9f, "Admin Palacio", "admin@palacio.com", "+51 987 555 666", R.drawable.hotel_habitacion_elegante, true));
        return lista;
    }

    // Para TaxistasActivity
    public static List<Usuario> getTaxistasEjemplo() {
        List<Usuario> lista = new ArrayList<>();
        Usuario t1 = new Usuario("Carlos Mendoza", "carlos.mendoza@roomtrip.com", "Taxista", "+51 987 654 321", true);
        t1.setLicencia("A-I-77482");
        t1.setPlacaAuto("ABC-123");
        t1.setModeloVehiculo("Toyota Corolla 2022");
        lista.add(t1);

        Usuario t2 = new Usuario("Marcos López", "marcos.lopez@roomtrip.com", "Taxista", "+51 912 345 678", false);
        t2.setLicencia("A-IIb-99321");
        t2.setPlacaAuto("XYZ-789");
        t2.setModeloVehiculo("Nissan Sentra 2021");
        lista.add(t2);
        return lista;
    }

    // Para UsuariosActivity
    public static List<Usuario> getUsuariosCompletosEjemplo() {
        List<Usuario> lista = new ArrayList<>();

        Usuario t1 = new Usuario("Carlos Mendoza", "carlos.mendoza@roomtrip.com", "Taxista", "+51 987 654 321", true);
        t1.setLicencia("A-I-77482");
        t1.setPlacaAuto("ABC-123");
        t1.setModeloVehiculo("Toyota Corolla 2022");
        lista.add(t1);

        Usuario t2 = new Usuario("Marcos López", "marcos.lopez@roomtrip.com", "Taxista", "+51 912 345 678", false);
        t2.setLicencia("A-IIb-99321");
        t2.setPlacaAuto("XYZ-789");
        t2.setModeloVehiculo("Nissan Sentra 2021");
        lista.add(t2);

        Usuario c1 = new Usuario("Ana García", "ana.garcia@gmail.com", "Cliente", "+51 955 443 322", true);
        c1.setDireccion("Av. Larco 456, Miraflores, Lima");
        c1.setHistorialReservas("3 reservaciones completadas, 1 cancelada");
        lista.add(c1);

        Usuario c2 = new Usuario("Sofía Benítez", "sofia.b@outlook.com", "Cliente", "+51 933 221 100", true);
        c2.setDireccion("Calle San Martín 789, Arequipa");
        c2.setHistorialReservas("5 reservaciones completadas");
        lista.add(c2);

        Usuario a1 = new Usuario("Luis Torres", "luis.torres@grandhotel.com", "Admin", "+51 966 778 899", true);
        a1.setHotelAsignado("Grand Hotel RoomTrip");
        lista.add(a1);

        Usuario a2 = new Usuario("Javier Ramírez", "jramirez@hotelparadise.com", "Admin", "+51 944 556 677", true);
        a2.setHotelAsignado("Hotel Paradise Costero");
        lista.add(a2);

        return lista;
    }
}