package com.example.roomtrip.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomtrip.R;
import com.example.roomtrip.login.LoginActivity;

public class PerfilDialogHelper {

    public interface OnNombreActualizadoListener {
        void onNombreActualizado(String nuevoNombre);
    }

    public static void mostrarDialogoPerfil(Activity activity, String rol, String nombreActual, OnNombreActualizadoListener listener) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_perfil_admin, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.88),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        TextView tvTituloPerfil = view.findViewById(R.id.tvTituloPerfil);
        EditText etDialogNombre = view.findViewById(R.id.etDialogNombre);
        Button btnDialogGuardar = view.findViewById(R.id.btnDialogGuardar);
        TextView tvDialogCerrarSesion = view.findViewById(R.id.tvDialogCerrarSesion);
        View containerFotoPerfil = view.findViewById(R.id.containerFotoPerfil);

        tvTituloPerfil.setText("Perfil de " + rol);
        etDialogNombre.setText(nombreActual);

        containerFotoPerfil.setOnClickListener(v ->
                Toast.makeText(activity, "Seleccionar foto de perfil...", Toast.LENGTH_SHORT).show());

        btnDialogGuardar.setOnClickListener(v -> {
            String nuevoNombre = etDialogNombre.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                if (listener != null) {
                    listener.onNombreActualizado(nuevoNombre);
                }
                Toast.makeText(activity, "Nombre actualizado a: " + nuevoNombre, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(activity, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        tvDialogCerrarSesion.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        });

        dialog.show();
    }
}