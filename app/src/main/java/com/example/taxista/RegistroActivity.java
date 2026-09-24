package com.example.taxista; // Reemplaza con el nombre de tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private ImageView btnVolver;
    private Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro); // Asegúrate de que coincida con tu XML de registro

        btnVolver = findViewById(R.id.btnVolver);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        // Flecha arriba/atrás para volver a la pantalla de Login
        btnVolver.setOnClickListener(v -> finish());

        // Botón Registrarse: lleva directamente a la pantalla 'Solicitado'
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, SolicitadoActivity.class);
            startActivity(intent);
            finish(); // Cierra esta pantalla
        });
    }
}