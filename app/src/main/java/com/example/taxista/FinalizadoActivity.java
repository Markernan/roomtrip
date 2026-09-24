package com.example.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class FinalizadoActivity extends AppCompatActivity {

    private Button btnVolverBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizado);

        btnVolverBuscar = findViewById(R.id.btnVolverBuscar);

        // Regresa a SolicitadoActivity limpiando el historial de pantallas
        btnVolverBuscar.setOnClickListener(v -> {
            Intent intent = new Intent(FinalizadoActivity.this, SolicitadoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Configuración de la barra inferior de navegación
        NavegacionHelper.configurarBarra(this, "TRABAJO");
    }
}