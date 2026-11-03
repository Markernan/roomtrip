package com.example.roomtrip.utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
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
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;

import java.util.HashMap;
import java.util.Map;

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

        tvTituloPerfil.setText(activity.getString(R.string.perfil_dialogo_titulo, rol));
        etDialogNombre.setText(nombreActual);

        containerFotoPerfil.setOnClickListener(v ->
                Toast.makeText(activity, R.string.perfil_dialogo_foto, Toast.LENGTH_SHORT).show());

        btnDialogGuardar.setOnClickListener(v -> {
            String nuevoNombre = etDialogNombre.getText().toString().trim();
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(activity, R.string.perfil_dialogo_nombre_vacio, Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = SesionFirebase.uidActual();
            if (uid == null) {
                dialog.dismiss();
                return;
            }
            // Lab 6: el nombre se guarda en usuarios/{uid} de Firestore
            Map<String, Object> cambios = new HashMap<>();
            cambios.put(PerfilUsuario.CAMPO_NOMBRES, nuevoNombre);
            cambios.put(PerfilUsuario.CAMPO_APELLIDOS, "");
            btnDialogGuardar.setEnabled(false);
            new UsuarioRepository().actualizar(uid, cambios, ignorado -> {
                if (listener != null) {
                    listener.onNombreActualizado(nuevoNombre);
                }
                Toast.makeText(activity, activity.getString(R.string.perfil_dialogo_nombre_actualizado, nuevoNombre),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }, e -> {
                btnDialogGuardar.setEnabled(true);
                Log.e("PerfilDialogHelper", "No se pudo guardar el nombre", e);
                Toast.makeText(activity, ErroresFirebase.mensaje(activity, e), Toast.LENGTH_LONG).show();
            });
        });

        // Logout real (Lab 6): cierra Firebase Authentication y vuelve al login
        tvDialogCerrarSesion.setOnClickListener(v -> {
            dialog.dismiss();
            RolUsuario rolUsuario = "SuperAdmin".equalsIgnoreCase(rol) ? RolUsuario.SUPERADMIN : RolUsuario.ADMIN_HOTEL;
            SesionFirebase.cerrarSesion(activity, rolUsuario);
        });

        dialog.show();
    }
}
