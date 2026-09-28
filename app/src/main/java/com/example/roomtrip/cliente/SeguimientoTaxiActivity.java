package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.utils.InsetsHelper;

/**
 * Seguimiento del servicio de taxi en curso: datos del taxista y ubicación en
 * mapa (RF-CL-09), obtenidos por la API REST del sistema de taxistas.
 */
public class SeguimientoTaxiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_taxi);

        View btnVolverMapa = findViewById(R.id.btnVolverMapa);
        btnVolverMapa.setOnClickListener(v -> finish());
        InsetsHelper.empujarBajoBarraEstado(btnVolverMapa);
        findViewById(R.id.btnMostrarQr).setOnClickListener(v ->
                startActivity(new Intent(this, MostrarQrActivity.class)));
    }
}
