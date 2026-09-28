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
import com.example.roomtrip.login.LoginActivity;
import com.example.roomtrip.utils.NavegacionHelper;

public class PerfilTaxistaActivity extends AppCompatActivity {

    private ImageView ivFotoPerfil, ivFotoAuto;
    private FrameLayout btnSubirFotoAuto;
    private Button btnGuardarCambios;
    private TextView tvCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_taxista);

        NavegacionHelper.configurarBarra(this, "PERFIL");

        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        ivFotoAuto = findViewById(R.id.ivFotoAuto);
        btnSubirFotoAuto = findViewById(R.id.btnSubirFotoAuto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        tvCerrarSesion = findViewById(R.id.tvCerrarSesion);

        btnSubirFotoAuto.setOnClickListener(v -> {
            Toast.makeText(this, "Opción para cambiar foto del vehículo", Toast.LENGTH_SHORT).show();
        });

        ivFotoPerfil.setOnClickListener(v -> {
            Toast.makeText(this, "Opción para cambiar foto de perfil", Toast.LENGTH_SHORT).show();
        });

        btnGuardarCambios.setOnClickListener(v -> {
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });

        tvCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilTaxistaActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}