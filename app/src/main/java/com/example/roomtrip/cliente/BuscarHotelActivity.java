package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;

import java.util.ArrayList;
import java.util.List;

public class BuscarHotelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_hotel);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel("Hotel Miraflores", "Lima, Perú", "S/250 noche", 4.8f,
                R.drawable.foto_hotel_miraflores));
        hoteles.add(new Hotel("Hotel Larco Suites", "Lima, Perú", "S/180 noche", 4.5f,
                R.drawable.foto_hotel_miraflores));

        RecyclerView rv = findViewById(R.id.rvResultados);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new HotelAdapter(hoteles, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra("nombreHotel", hotel.nombre);
            startActivity(intent);
        }));
    }
}