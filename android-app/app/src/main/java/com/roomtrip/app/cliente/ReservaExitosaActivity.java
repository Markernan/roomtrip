package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

public class ReservaExitosaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_exitosa);

        String nombreHotel = getIntent().getStringExtra("nombreHotel");
        if (nombreHotel != null) {
            ((TextView) findViewById(R.id.tvMensajeExito))
                    .setText("¡Tu reserva a " + nombreHotel + " fue exitosa!");
        }

        findViewById(R.id.btnVolverAlInicio).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}
