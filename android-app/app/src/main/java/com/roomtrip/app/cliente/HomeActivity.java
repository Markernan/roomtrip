package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomtrip.app.R;

import java.util.ArrayList;
import java.util.List;

/** Pantalla 2: inicio del cliente, con el hotel recomendado. */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // TODO: reemplazar "William" por el nombre real del usuario autenticado (Lab 6).
        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText("Hola, William");

        // TODO: reemplazar por la consulta a Firestore de hoteles disponibles (Lab 6).
        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel("Hotel Miraflores", "Lima, Perú", "S/250 noche", 4.8f,
                R.drawable.foto_hotel_miraflores));
        hoteles.add(new Hotel("Hotel Larco Suites", "Lima, Perú", "S/180 noche", 4.5f,
                R.drawable.foto_hotel_miraflores));
        hoteles.add(new Hotel("Grand Palace Lima", "Lima, Perú", "S/320 noche", 5.0f,
                R.drawable.foto_hotel_miraflores));

        RecyclerView rvHoteles = findViewById(R.id.rvHoteles);
        rvHoteles.setLayoutManager(new LinearLayoutManager(this));
        rvHoteles.setAdapter(new HotelAdapter(hoteles, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra("nombreHotel", hotel.nombre);
            startActivity(intent);
        }));

        findViewById(R.id.campoBuscar).setOnClickListener(v ->
                startActivity(new Intent(this, BuscarHotelActivity.class)));

        configurarNavegacionInferior();
    }

    private void configurarNavegacionInferior() {
        findViewById(R.id.navReservas).setOnClickListener(v ->
                startActivity(new Intent(this, MisReservasActivity.class)));
        findViewById(R.id.navPerfil).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));
    }
}
