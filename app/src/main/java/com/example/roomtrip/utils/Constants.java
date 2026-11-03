package com.example.roomtrip.utils;

public class Constants {

    // Roles de usuario
    public static final String ROL_CLIENTE = "Cliente";
    public static final String ROL_ADMIN = "Admin";
    public static final String ROL_TAXISTA = "Taxista";
    public static final String ROL_SUPERADMIN = "SuperAdmin";

    // Claves de Extras de Intents
    public static final String EXTRA_ROL_USUARIO = "ROL_USUARIO";
    public static final String EXTRA_NOMBRE_HOTEL = "nombreHotel";
    public static final String EXTRA_HOTEL = "hotel";
    public static final String EXTRA_USUARIO = "usuario";
    public static final String EXTRA_PEDIDO_TAXI = "pedidoTaxi";

    // Lab 6: ids de documentos de Firestore que viajan entre pantallas
    public static final String EXTRA_HOTEL_ID = "hotelId";
    public static final String EXTRA_HABITACION_ID = "habitacionId";
    public static final String EXTRA_FECHA_ENTRADA = "fechaEntrada";
    public static final String EXTRA_FECHA_SALIDA = "fechaSalida";
    public static final String EXTRA_RESERVA_ID = "reservaId";
    public static final String EXTRA_SERVICIO_TAXI_ID = "servicioTaxiId";
}