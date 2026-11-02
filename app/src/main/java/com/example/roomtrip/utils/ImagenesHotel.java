package com.example.roomtrip.utils;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.example.roomtrip.R;

/**
 * Fotos de hotel incluidas en la app. En Firestore cada hotel guarda una clave ({@code imagen}) y
 * aquí se traduce a un drawable. Es provisional hasta decidir Firebase Storage o Cloudinary
 * (arquitectura.md, pendientes); entonces el hotel guardará la URL y se cargará con Glide.
 */
public final class ImagenesHotel {

    public static final String MIRAFLORES = "miraflores";
    public static final String ITALIA_FACHADA = "italia_fachada";
    public static final String GRAND_ITALIA = "grand_italia";
    public static final String SUITE_AZUL = "suite_azul";
    public static final String ELEGANTE = "elegante";

    /** Claves en el orden en que se asignan a los hoteles nuevos. */
    public static final String[] CLAVES = {MIRAFLORES, ITALIA_FACHADA, GRAND_ITALIA, SUITE_AZUL, ELEGANTE};

    private ImagenesHotel() {
    }

    @DrawableRes
    public static int recurso(@Nullable String clave) {
        if (clave == null) return R.drawable.hotel_placeholder;
        switch (clave) {
            case MIRAFLORES:
                return R.drawable.foto_hotel_miraflores;
            case ITALIA_FACHADA:
                return R.drawable.hotel_italia_fachada_calle;
            case GRAND_ITALIA:
                return R.drawable.grand_hotel_italia_fachada;
            case SUITE_AZUL:
                return R.drawable.hotel_habitacion_azul;
            case ELEGANTE:
                return R.drawable.hotel_habitacion_elegante;
            default:
                return R.drawable.hotel_placeholder;
        }
    }
}
