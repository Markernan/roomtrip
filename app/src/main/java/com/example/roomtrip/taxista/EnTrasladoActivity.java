package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
<<<<<<< HEAD
import com.example.roomtrip.utils.NavegacionHelper;
=======
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2

public class EnTrasladoActivity extends AppCompatActivity {

    private Button btnEscanear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_traslado);

        // Configuración de la barra de navegación inferior
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        btnEscanear = findViewById(R.id.btnEscanear);

<<<<<<< HEAD
=======
        // Salta a la pantalla de EscaneoActivity
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
        btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(EnTrasladoActivity.this, EscaneoActivity.class);
            startActivity(intent);
        });
    }
}