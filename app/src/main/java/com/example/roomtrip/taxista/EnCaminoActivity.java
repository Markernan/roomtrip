package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.utils.NavegacionHelper;

public class EnCaminoActivity extends AppCompatActivity {

    private Button btnLlegueAlEncuentro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_camino);

        // Configuración de la barra inferior de navegación
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        btnLlegueAlEncuentro = findViewById(R.id.btnLlegueAlEncuentro);

        btnLlegueAlEncuentro.setOnClickListener(v -> {
            Intent intent = new Intent(EnCaminoActivity.this, EnTrasladoActivity.class);
            startActivity(intent);
        });
    }
}