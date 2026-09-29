package com.example.roomtrip.data;

import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.LogEvento;
import com.example.roomtrip.data.model.Usuario;
import com.example.roomtrip.R;
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

    // Para AuditoriaActivity
    public static List<LogEvento> getLogsEjemplo() {
        List<LogEvento> lista = new ArrayList<>();
        lista.add(new LogEvento("Superadmin activó al usuario cliente \"Ana Gómez\"", "31/08/2026", "14:32:05 hrs", "IP: 190.45.12.34", R.drawable.ic_person));
        lista.add(new LogEvento("Inicio de sesión exitoso - Usuario: Carlos Ruiz (Administrador)", "31/08/2026", "14:15:42 hrs", "Dispositivo: Web", R.drawable.ic_lock));
        lista.add(new LogEvento("Checkout completado en Hotel Miraflores - Reserva #1042", "31/08/2026", "13:47:18 hrs", "Dispositivo: Android 14", R.drawable.ic_card));
        lista.add(new LogEvento("Hotel \"Gran Hotel Lima\" modificado por Ana García", "31/08/2026", "13:22:11 hrs", "IP: 190.45.12.34", R.drawable.ic_home_pin));
        lista.add(new LogEvento("Error al procesar pago - Reserva #1038 (Tarjeta rechazada)", "31/08/2026", "12:58:33 hrs", "Dispositivo: Android 14", R.drawable.ic_warning));

        lista.add(new LogEvento("Creación de nuevo Administrador de Hotel: Mario Paredes", "31/08/2026", "11:40:12 hrs", "IP: 190.45.12.34", R.drawable.ic_person));
        lista.add(new LogEvento("Eliminación de solicitud de servicio de Taxi #8821", "31/08/2026", "10:15:00 hrs", "Dispositivo: Web", R.drawable.ic_warning));
        lista.add(new LogEvento("Actualización de tarifas de temporada alta en Cusco Plaza", "31/08/2026", "09:05:45 hrs", "IP: 181.66.40.12", R.drawable.ic_home_pin));
        lista.add(new LogEvento("Intento de acceso fallido al panel Superadmin", "31/08/2026", "08:30:21 hrs", "IP: 200.12.85.90", R.drawable.ic_lock));
        lista.add(new LogEvento("Exportación masiva de reportes PDF de ingresos mensuales", "31/08/2026", "07:50:11 hrs", "Dispositivo: Web", R.drawable.ic_card));

        return lista;
    }

    // Para HotelesActivity
    public static List<Hotel> getHotelesSuperadminEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Italia (Fachada)", "Miraflores, Lima", 4.7f, "Roberto Gómez", "roberto@miraflores.com", "+51 987 654 321", R.drawable.hotel_italia_fachada_calle, true));
        lista.add(new Hotel("Hotel Italia (Suite Azul)", "Centro de Lima, Lima", 4.5f, "María Mendoza", "maria@granhotellima.com", "+51 912 345 678", R.drawable.hotel_habitacion_azul, true));
        lista.add(new Hotel("Hotel Italia (Elegante)", "Plaza de Armas, Cusco", 4.9f, "Carlos Inca", "carlos@cuscoplaza.com", "+51 965 432 198", R.drawable.hotel_habitacion_elegante, true));
        lista.add(new Hotel("Grand Hotel Italia", "Bahía de Paracas, Ica", 4.6f, "Elena Mar", "elena@resortparacas.com", "+51 954 123 789", R.drawable.grand_hotel_italia_fachada, false));

        lista.add(new Hotel("Belmond Sanctuary Lodge", "Machu Picchu, Cusco", 4.9f, "Fernando Quispe", "f.quispe@belmond.pe", "+51 984 112 233", R.drawable.hotel_habitacion_elegante, true));
        lista.add(new Hotel("Hotel Casa Andina Standard", "Yanahuara, Arequipa", 4.4f, "Rosa Arenas", "rarenas@casaandina.com", "+51 959 334 455", R.drawable.hotel_italia_fachada_calle, true));
        lista.add(new Hotel("Sonesta Hotel El Olivar", "San Isidro, Lima", 4.7f, "Gabriel Soler", "gsoler@sonesta.pe", "+51 914 556 677", R.drawable.foto_hotel_miraflores, true));
        lista.add(new Hotel("Hotel Swissôtel Lima", "San Isidro, Lima", 4.8f, "Patricia Vega", "pvega@swissotel.com", "+51 920 778 899", R.drawable.grand_hotel_italia_fachada, true));
        lista.add(new Hotel("Ecology Lodge Mancora", "Máncora, Piura", 4.3f, "Jorge Solis", "jsolis@ecolodge.pe", "+51 968 990 011", R.drawable.hotel_habitacion_azul, false));

        return lista;
    }

    // Para ReportesActivity
    public static List<Hotel> getReporteHotelesEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Marriott Lima", "Miraflores, Lima", 4.8f, "Admin Marriott", "admin@marriott.com", "+51 987 111 222", R.drawable.foto_hotel_miraflores, true));
        lista.add(new Hotel("Gran Hotel Bolivar", "Centro de Lima, Lima", 4.5f, "Admin Bolivar", "admin@bolivar.com", "+51 987 333 444", R.drawable.hotel_italia_fachada_calle, true));
        lista.add(new Hotel("Hotel Palacio del Inka", "Cusco", 4.9f, "Admin Palacio", "admin@palacio.com", "+51 987 555 666", R.drawable.hotel_habitacion_elegante, true));

        lista.add(new Hotel("Hilton Lima Miraflores", "Miraflores, Lima", 4.7f, "Admin Hilton", "admin@hilton.com", "+51 987 777 888", R.drawable.grand_hotel_italia_fachada, true));
        lista.add(new Hotel("Hotel Aranwa Sacred Valley", "Urubamba, Cusco", 4.8f, "Admin Aranwa", "admin@aranwa.com", "+51 987 999 000", R.drawable.hotel_habitacion_azul, true));
        lista.add(new Hotel("Thunderbird Hotel Fiesta", "Miraflores, Lima", 4.2f, "Admin Thunderbird", "admin@fiesta.com", "+51 981 222 333", R.drawable.hotel_italia_fachada_calle, false));
        lista.add(new Hotel("Hotel Colca Lodge Spa", "Caylloma, Arequipa", 4.9f, "Admin Colca", "admin@colcalodge.com", "+51 981 444 555", R.drawable.hotel_habitacion_elegante, true));
        lista.add(new Hotel("DoubleTree by Hilton Paracas", "Paracas, Ica", 4.6f, "Admin DoubleTree", "admin@doubletree.com", "+51 981 666 777", R.drawable.foto_hotel_miraflores, true));

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

        Usuario t3 = new Usuario("Raúl Benavides", "raul.benavides@roomtrip.com", "Taxista", "+51 945 112 233", true);
        t3.setLicencia("A-I-88392");
        t3.setPlacaAuto("F4T-552");
        t3.setModeloVehiculo("Hyundai Elantra 2023");
        lista.add(t3);

        Usuario t4 = new Usuario("Hugo Santillán", "hugo.s@roomtrip.com", "Taxista", "+51 912 667 788", true);
        t4.setLicencia("A-IIa-11203");
        t4.setPlacaAuto("B9V-104");
        t4.setModeloVehiculo("Kia Cerato 2020");
        lista.add(t4);

        Usuario t5 = new Usuario("Diego Flores", "dflores@roomtrip.com", "Taxista", "+51 978 445 566", false);
        t5.setLicencia("A-I-99401");
        t5.setPlacaAuto("A1M-902");
        t5.setModeloVehiculo("Chevrolet Sail 2019");
        lista.add(t5);

        Usuario t6 = new Usuario("Manuel Valdivia", "m.valdivia@roomtrip.com", "Taxista", "+51 965 223 344", true);
        t6.setLicencia("A-I-33290");
        t6.setPlacaAuto("C8X-331");
        t6.setModeloVehiculo("Toyota Yaris 2021");
        lista.add(t6);

        Usuario t7 = new Usuario("César Augusto", "caugusto@roomtrip.com", "Taxista", "+51 932 889 900", true);
        t7.setLicencia("A-IIb-55412");
        t7.setPlacaAuto("D2W-881");
        t7.setModeloVehiculo("Honda Civic 2022");
        lista.add(t7);
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

        Usuario c3 = new Usuario("Lucía Méndez", "lucia.mendez@hotmail.com", "Cliente", "+51 918 223 344", true);
        c3.setDireccion("Av. El Sol 102, Cusco");
        c3.setHistorialReservas("2 reservaciones completadas");
        lista.add(c3);

        Usuario c4 = new Usuario("Ricardo Palma", "rpalma@yahoo.es", "Cliente", "+51 967 889 900", false);
        c4.setDireccion("Jr. de la Unión 304, Centro de Lima");
        c4.setHistorialReservas("Sin reservaciones activas");
        lista.add(c4);

        Usuario a3 = new Usuario("Gisela Delgado", "gdelgado@westin.pe", "Admin", "+51 951 334 455", true);
        a3.setHotelAsignado("Hotel Westin Lima");
        lista.add(a3);

        Usuario t3 = new Usuario("Raúl Benavides", "raul.benavides@roomtrip.com", "Taxista", "+51 945 112 233", true);
        t3.setLicencia("A-I-88392");
        t3.setPlacaAuto("F4T-552");
        t3.setModeloVehiculo("Hyundai Elantra 2023");
        lista.add(t3);

        Usuario a4 = new Usuario("Valeria Prado", "vprado@marriott.pe", "Admin", "+51 923 114 556", true);
        a4.setHotelAsignado("JW Marriott Hotel");
        lista.add(a4);

        return lista;
    }
}