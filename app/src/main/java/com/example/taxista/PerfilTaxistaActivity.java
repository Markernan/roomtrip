package com.example.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilTaxistaActivity extends AppCompatActivity {

    private ImageView ivFotoPerfil, ivFotoAuto;
    private FrameLayout btnSubirFotoAuto;
    private Button btnGuardarCambios;
    private TextView tvCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_taxista);

        // Activa el menú inferior con la pestaña 'PERFIL' destacada
        NavegacionHelper.configurarBarra(this, "PERFIL");

        // Inicialización de componentes de imágenes y botones
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        ivFotoAuto = findViewById(R.id.ivFotoAuto);
        btnSubirFotoAuto = findViewById(R.id.btnSubirFotoAuto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        tvCerrarSesion = findViewById(R.id.tvCerrarSesion);

        // Acción para cargar o seleccionar foto del auto
        btnSubirFotoAuto.setOnClickListener(v -> {
            Toast.makeText(this, "Opción para cambiar foto del vehículo", Toast.LENGTH_SHORT).show();
        });

        // Acción para cargar o cambiar foto de perfil
        ivFotoPerfil.setOnClickListener(v -> {
            Toast.makeText(this, "Opción para cambiar foto de perfil", Toast.LENGTH_SHORT).show();
        });

        // Guardar cambios y cerrar la pantalla para volver a la de trabajo
        btnGuardarCambios.setOnClickListener(v -> {
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Cerrar sesión y limpiar historial de actividades
        tvCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilTaxistaActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}