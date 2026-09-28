package com.example.roomtrip.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Evita que botones flotantes (sobre fotos/mapas a pantalla completa) queden debajo del notch o la barra de estado. */
public class InsetsHelper {

    public static void empujarBajoBarraEstado(View boton) {
        ViewGroup.MarginLayoutParams paramsOriginales = (ViewGroup.MarginLayoutParams) boton.getLayoutParams();
        int margenBaseTop = paramsOriginales.topMargin;

        ViewCompat.setOnApplyWindowInsetsListener(boton, (v, insets) -> {
            int topStatusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = margenBaseTop + topStatusBar;
            v.setLayoutParams(params);
            return insets;
        });
    }
}
