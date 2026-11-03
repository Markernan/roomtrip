package com.example.roomtrip.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.HomeActivity;
import com.example.roomtrip.cliente.MisReservasActivity;
import com.example.roomtrip.cliente.PerfilActivity;

public class NavegacionClienteHelper {

    public static void configurarBarra(Activity activity, String itemActivo) {
        LinearLayout navExplorar = activity.findViewById(R.id.navExplorar);
        LinearLayout navReservas = activity.findViewById(R.id.navReservas);
        LinearLayout navPerfil = activity.findViewById(R.id.navPerfil);

        ImageView ivNavExplorar = activity.findViewById(R.id.ivNavExplorar);
        TextView tvNavExplorar = activity.findViewById(R.id.tvNavExplorar);

        ImageView ivNavReservas = activity.findViewById(R.id.ivNavReservas);
        TextView tvNavReservas = activity.findViewById(R.id.tvNavReservas);

        ImageView ivNavPerfil = activity.findViewById(R.id.ivNavPerfil);
        TextView tvNavPerfil = activity.findViewById(R.id.tvNavPerfil);

        int colorTurquesa = ContextCompat.getColor(activity, R.color.turquesa);
        int colorGris = ContextCompat.getColor(activity, R.color.texto_secundario);

        // 1. Resetear todos los íconos y textos a gris
        if (ivNavExplorar != null) ImageViewCompat.setImageTintList(ivNavExplorar, ColorStateList.valueOf(colorGris));
        if (tvNavExplorar != null) tvNavExplorar.setTextColor(colorGris);

        if (ivNavReservas != null) ImageViewCompat.setImageTintList(ivNavReservas, ColorStateList.valueOf(colorGris));
        if (tvNavReservas != null) tvNavReservas.setTextColor(colorGris);

        if (ivNavPerfil != null) ImageViewCompat.setImageTintList(ivNavPerfil, ColorStateList.valueOf(colorGris));
        if (tvNavPerfil != null) tvNavPerfil.setTextColor(colorGris);

        // 2. Iluminar en turquesa el ítem activo correspondiente
        if ("EXPLORAR".equalsIgnoreCase(itemActivo)) {
            if (ivNavExplorar != null) ImageViewCompat.setImageTintList(ivNavExplorar, ColorStateList.valueOf(colorTurquesa));
            if (tvNavExplorar != null) tvNavExplorar.setTextColor(colorTurquesa);
        } else if ("RESERVAS".equalsIgnoreCase(itemActivo)) {
            if (ivNavReservas != null) ImageViewCompat.setImageTintList(ivNavReservas, ColorStateList.valueOf(colorTurquesa));
            if (tvNavReservas != null) tvNavReservas.setTextColor(colorTurquesa);
        } else if ("PERFIL".equalsIgnoreCase(itemActivo)) {
            if (ivNavPerfil != null) ImageViewCompat.setImageTintList(ivNavPerfil, ColorStateList.valueOf(colorTurquesa));
            if (tvNavPerfil != null) tvNavPerfil.setTextColor(colorTurquesa);
        }

        // 3. Asignar listeners de navegación
        if (navExplorar != null) {
            navExplorar.setOnClickListener(v -> {
                if (!"EXPLORAR".equalsIgnoreCase(itemActivo)) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        if (navReservas != null) {
            navReservas.setOnClickListener(v -> {
                if (!"RESERVAS".equalsIgnoreCase(itemActivo)) {
                    Intent intent = new Intent(activity, MisReservasActivity.class);
                    activity.startActivity(intent);
                    if (!"EXPLORAR".equalsIgnoreCase(itemActivo)) {
                        activity.finish();
                    }
                }
            });
        }

        if (navPerfil != null) {
            navPerfil.setOnClickListener(v -> {
                if (!"PERFIL".equalsIgnoreCase(itemActivo)) {
                    Intent intent = new Intent(activity, PerfilActivity.class);
                    activity.startActivity(intent);
                    if (!"EXPLORAR".equalsIgnoreCase(itemActivo)) {
                        activity.finish();
                    }
                }
            });
        }
    }
}