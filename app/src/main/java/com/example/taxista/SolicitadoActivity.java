package com.example.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SolicitadoActivity extends AppCompatActivity {

    private LinearLayout btnUsuario1, btnUsuario2, btnUsuario3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitado);

        // Configuración de la barra inferior de navegación
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        btnUsuario1 = findViewById(R.id.btnUsuario1);
        btnUsuario2 = findViewById(R.id.btnUsuario2);
        btnUsuario3 = findViewById(R.id.btnUsuario3);

        View.OnClickListener irAEnCamino = v -> {
            Intent intent = new Intent(SolicitadoActivity.this, EnCaminoActivity.class);
            startActivity(intent);
        };

        btnUsuario1.setOnClickListener(irAEnCamino);
        btnUsuario2.setOnClickListener(irAEnCamino);
        btnUsuario3.setOnClickListener(irAEnCamino);
    }
}