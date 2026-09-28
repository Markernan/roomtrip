package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
<<<<<<< HEAD
import com.example.roomtrip.login.LoginActivity;
import com.example.roomtrip.utils.NavegacionHelper;
=======
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2

public class PerfilTaxistaActivity extends AppCompatActivity {

    private ImageView ivFotoPerfil, ivFotoAuto;
    private FrameLayout btnSubirFotoAuto;
    private Button btnGuardarCambios;
    private TextView tvCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_taxista);

<<<<<<< HEAD
        NavegacionHelper.configurarBarra(this, "PERFIL");

=======
        // Activa el menú inferior con la pestaña 'PERFIL' destacada
        NavegacionHelper.configurarBarra(this, "PERFIL");

        // Inicialización de componentes de imágenes y botones
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        ivFotoAuto = findViewById(R.id.ivFotoAuto);
        btnSubirFotoAuto = findViewById(R.id.btnSubirFotoAuto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        tvCerrarSesion = findViewById(R.id.tvCerrarSesion);

<<<<<<< HEAD
=======
        // Acción para cargar o seleccionar foto del auto
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
        btnSubirFotoAuto.setOnClickListener(v -> {
            Toast.makeText(this, "Opción para cambiar foto del vehículo", Toast.LENGTH_SHORT).show();
        });

<<<<<<< HEAD
=======
        // Acción para cargar o cambiar foto de perfil
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
        ivFotoPerfil.setOnClickListener(v -> {
            Toast.makeText(this, "Opción para cambiar foto de perfil", Toast.LENGTH_SHORT).show();
        });

<<<<<<< HEAD
=======
        // Guardar cambios y cerrar la pantalla para volver a la de trabajo
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
        btnGuardarCambios.setOnClickListener(v -> {
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });

<<<<<<< HEAD
=======
        // Cerrar sesión y limpiar historial de actividades
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
        tvCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilTaxistaActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}