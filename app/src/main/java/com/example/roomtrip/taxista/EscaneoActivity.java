package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;

public class EscaneoActivity extends AppCompatActivity {

    private Button btnEscanearQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo);

        btnEscanearQR = findViewById(R.id.btnEscanearQR);

        // Al presionar Escanear, navega a FinalizadoActivity
        btnEscanearQR.setOnClickListener(v -> {
            Intent intent = new Intent(EscaneoActivity.this, FinalizadoActivity.class);
            startActivity(intent);
        });
<<<<<<< HEAD
=======

        NavegacionHelper.configurarBarra(this, "TRABAJO");
>>>>>>> 75833f8393f9963e8f2c999a5783ce672f53fce2
    }
}