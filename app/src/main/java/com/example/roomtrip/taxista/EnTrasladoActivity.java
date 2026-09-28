package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.utils.NavegacionHelper;

public class EnTrasladoActivity extends AppCompatActivity {

    private Button btnEscanear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_traslado);

        // Configuración de la barra de navegación inferior
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        btnEscanear = findViewById(R.id.btnEscanear);

        btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(EnTrasladoActivity.this, EscaneoActivity.class);
            startActivity(intent);
        });
    }
}