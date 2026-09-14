package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/**
 * Seguimiento del servicio de taxi en curso: datos del taxista y ubicación en
 * mapa (RF-CL-09), obtenidos por la API REST del sistema de taxistas.
 */
public class SeguimientoTaxiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_taxi);

        findViewById(R.id.btnVolverMapa).setOnClickListener(v -> finish());
        findViewById(R.id.btnMostrarQr).setOnClickListener(v ->
                startActivity(new Intent(this, MostrarQrActivity.class)));
    }
}
