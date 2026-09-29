package com.example.roomtrip.data;

import com.example.roomtrip.data.model.Chat;
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.LogEvento;
import com.example.roomtrip.data.model.Mensaje;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.data.model.Notificacion.TipoNotificacion;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.data.model.Servicio;
import com.example.roomtrip.data.model.Traslado;
import com.example.roomtrip.data.model.Usuario;
import com.example.roomtrip.data.model.Valoracion;
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

    // ======================= Cliente =======================
    // Todos los métodos devuelven una lista NUEVA y modificable en cada llamada: hay pantallas
    // que le agregan elementos (por ejemplo, el chat).

    // Para HomeActivity y BuscarHotelActivity: el mismo catálogo de hoteles
    public static List<Hotel> getHotelesClienteEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Miraflores", "Lima, Perú", "S/250 noche", 4.8f, R.drawable.foto_hotel_miraflores));
        lista.add(new Hotel("Hotel Larco Suites", "Lima, Perú", "S/180 noche", 4.5f, R.drawable.foto_hotel_miraflores));
        lista.add(new Hotel("Grand Palace Lima", "Lima, Perú", "S/320 noche", 5.0f, R.drawable.foto_hotel_miraflores));
        return lista;
    }

    // Para MisReservasActivity. Todavía no existe un modelo Reserva: cada reserva se representa con
    // el Hotel reservado.
    public static List<Hotel> getReservasClienteEjemplo() {
        List<Hotel> lista = new ArrayList<>();
        lista.add(new Hotel("Hotel Larco Suites", "Lima, Perú · 4.5 estrellas", "", 4.5f, R.drawable.foto_hotel_miraflores));
        return lista;
    }

    // Para ChatActivity (cliente)
    public static List<Mensaje> getMensajesChatEjemplo() {
        List<Mensaje> lista = new ArrayList<>();
        lista.add(new Mensaje("¡Hola! Tengo una consulta sobre mi reserva.", "10:30 AM", true));
        lista.add(new Mensaje("¡Hola William! Con gusto te ayudamos. ¿En qué podemos servirte?", "10:32 AM", false));
        return lista;
    }

    // ==================== Administrador de hotel ====================

    // Para ChatsFragment
    public static List<Chat> getChatsAdminEjemplo() {
        List<Chat> lista = new ArrayList<>();
        lista.add(new Chat("Carlos Ruiz", "308", "¿A qué hora sirven el desayuno?", "10:30", 1));
        lista.add(new Chat("María Fernández", "Suite 2", "Necesito toallas extra, por favor.", "09:15", 1));
        lista.add(new Chat("Juan Gómez", "102", "Todo perfecto, gracias.", "Ayer", 0));
        lista.add(new Chat("Ana López", "205", "¿Tienen servicio de taxi al aeropuerto?", "Ayer", 0));
        return lista;
    }

    // Para CheckoutsFragment
    public static List<Checkout> getCheckoutsEjemplo() {
        List<Checkout> lista = new ArrayList<>();
        lista.add(new Checkout("María González", "204", "11:00 AM", "Pendiente"));
        lista.add(new Checkout("Carlos Ruiz", "308", "09:30 AM", "Procesado", 450.0));
        lista.add(new Checkout("Ana López", "105", "Ayer, 12:00 PM", "Pendiente"));
        lista.add(new Checkout("Juan Pérez", "201", "Ayer, 10:15 AM", "Procesado", 320.0));
        return lista;
    }

    // Para InventarioFragment (pestaña Servicios). La pestaña Habitaciones usa getHabitacionesEjemplo().
    public static List<Servicio> getServiciosEjemplo() {
        List<Servicio> lista = new ArrayList<>();
        lista.add(new Servicio("Desayuno Buffet Continental", "Alimentos y Bebidas", 35.00, true));
        lista.add(new Servicio("Lavandería Express (x Prenda)", "Limpieza", 12.00, true));
        lista.add(new Servicio("Masaje Relajante (45 min)", "Bienestar", 90.00, true));
        lista.add(new Servicio("Tour Guiado Centro Histórico", "Tours", 60.00, false));
        return lista;
    }

    // Para TrasladosEnCursoFragment
    public static List<Traslado> getTrasladosEjemplo() {
        List<Traslado> lista = new ArrayList<>();
        lista.add(new Traslado("Carlos Ruiz", "308", "Hotel ➔ Aeropuerto Jorge Chávez", "14:30 PM",
                EstadoServicioTaxi.EN_TRASLADO, "Pedro V. (ABC-123)", 60.0));
        lista.add(new Traslado("María Fernández", "Suite 2", "Terminal Cruz del Sur ➔ Hotel", "15:00 PM",
                EstadoServicioTaxi.SOLICITADO, "Asignando conductor...", 45.0));
        lista.add(new Traslado("Elena Rostova", "402", "Hotel ➔ Centro Histórico", "16:15 PM",
                EstadoServicioTaxi.EN_CAMINO, "Jorge M. (XYZ-987)", 35.0));
        return lista;
    }

    // Para NotificacionesFragment
    public static List<Notificacion> getNotificacionesEjemplo() {
        List<Notificacion> lista = new ArrayList<>();
        lista.add(new Notificacion("Nueva Reserva Confirmada",
                "Carlos Ruiz reservó la Habitación 308 del 12 al 15 de Octubre.",
                "Hace 10 min", TipoNotificacion.RESERVA, false));
        lista.add(new Notificacion("Pago Recibido (S/ 450)",
                "Se confirmó el pago por transferencia de María González (Hab. 204).",
                "Hace 1 hora", TipoNotificacion.PAGO, false));
        lista.add(new Notificacion("Reserva Cancelada",
                "El huésped Fernando Torres canceló la reserva #4021.",
                "Ayer, 18:30", TipoNotificacion.CANCELACION, true));
        lista.add(new Notificacion("Pago Confirmado (S/ 320)",
                "Pago recibido exitosamente para la reserva de Juan Pérez (Hab. 201).",
                "Ayer, 14:15", TipoNotificacion.PAGO, true));
        return lista;
    }

    // Para ValoracionesFragment
    public static List<Valoracion> getValoracionesEjemplo() {
        List<Valoracion> lista = new ArrayList<>();
        lista.add(new Valoracion("Carlos Ruiz", "Hab. 308", "Hace 2 días", 5.0f,
                "Excelente atención y limpieza. La habitación estaba impecable y el personal fue muy amable."));
        lista.add(new Valoracion("María Fernández", "Suite Presidencial", "Hace 5 días", 4.5f,
                "Muy buena vista y comodidades. El desayuno podría mejorar un poco en variedad."));
        lista.add(new Valoracion("Juan Gómez", "Hab. Económica", "Hace 1 semana", 4.0f,
                "Relación calidad-precio muy justa. Volvería a hospedarme aquí."));
        return lista;
    }
}