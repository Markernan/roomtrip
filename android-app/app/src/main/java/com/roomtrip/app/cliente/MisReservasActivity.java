package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomtrip.app.R;

import java.util.ArrayList;
import java.util.List;

/** Historial de reservas del cliente (RF-CL-06). */
public class MisReservasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        // TODO: reemplazar por la consulta a Firestore de las reservas del cliente autenticado (Lab 6).
        List<Hotel> reservas = new ArrayList<>();
        reservas.add(new Hotel("Hotel Larco Suites", "Lima, Perú · 4.5 estrellas", "", 4.5f,
                R.drawable.foto_hotel_miraflores));

        RecyclerView rv = findViewById(R.id.rvMisReservas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ReservaAdapter(
                reservas,
                hotel -> startActivity(new Intent(this, ChatActivity.class)),
                hotel -> {
                    Intent intent = new Intent(this, CheckoutActivity.class);
                    intent.putExtra("nombreHotel", hotel.nombre);
                    startActivity(intent);
                }));

        findViewById(R.id.navExplorar).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.navPerfil).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));
    }
}
