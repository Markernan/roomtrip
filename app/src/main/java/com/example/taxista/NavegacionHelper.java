package com.example.taxista;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

public class NavegacionHelper {

    public static void configurarBarra(Activity activity, String itemActivo) {
        LinearLayout navTrabajo = activity.findViewById(R.id.navTrabajo);
        LinearLayout navPerfil = activity.findViewById(R.id.navPerfil);

        ImageView ivNavTrabajo = activity.findViewById(R.id.ivNavTrabajo);
        TextView tvNavTrabajo = activity.findViewById(R.id.tvNavTrabajo);

        ImageView ivNavPerfil = activity.findViewById(R.id.ivNavPerfil);
        TextView tvNavPerfil = activity.findViewById(R.id.tvNavPerfil);

        int colorTurquesa = ContextCompat.getColor(activity, R.color.turquesa);
        int colorGris = ContextCompat.getColor(activity, R.color.texto_secundario);

        // 1. Aplicar colores dinámicos
        if ("TRABAJO".equalsIgnoreCase(itemActivo)) {
            if (ivNavTrabajo != null) ImageViewCompat.setImageTintList(ivNavTrabajo, ColorStateList.valueOf(colorTurquesa));
            if (tvNavTrabajo != null) tvNavTrabajo.setTextColor(colorTurquesa);

            if (ivNavPerfil != null) ImageViewCompat.setImageTintList(ivNavPerfil, ColorStateList.valueOf(colorGris));
            if (tvNavPerfil != null) tvNavPerfil.setTextColor(colorGris);
        } else if ("PERFIL".equalsIgnoreCase(itemActivo)) {
            if (ivNavPerfil != null) ImageViewCompat.setImageTintList(ivNavPerfil, ColorStateList.valueOf(colorTurquesa));
            if (tvNavPerfil != null) tvNavPerfil.setTextColor(colorTurquesa);

            if (ivNavTrabajo != null) ImageViewCompat.setImageTintList(ivNavTrabajo, ColorStateList.valueOf(colorGris));
            if (tvNavTrabajo != null) tvNavTrabajo.setTextColor(colorGris);
        }

        // 2. Navegación basada en pila de actividades (Back Stack)
        if (navTrabajo != null) {
            navTrabajo.setOnClickListener(v -> {
                if ("PERFIL".equalsIgnoreCase(itemActivo)) {
                    // Cierra Perfil y vuelve exactamente a la vista de Trabajo anterior
                    activity.finish();
                }
            });
        }

        if (navPerfil != null) {
            navPerfil.setOnClickListener(v -> {
                if ("TRABAJO".equalsIgnoreCase(itemActivo)) {
                    // Abre Perfil encima de la pantalla de Trabajo (sin llamar a finish())
                    Intent intent = new Intent(activity, PerfilTaxistaActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
    }
}