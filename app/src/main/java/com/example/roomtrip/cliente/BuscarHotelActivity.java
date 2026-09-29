package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Hotel;

import java.util.List;

public class BuscarHotelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_hotel);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        List<Hotel> hoteles = MockData.getHotelesClienteEjemplo();

        RecyclerView rv = findViewById(R.id.rvResultados);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new HotelAdapter(hoteles, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra("nombreHotel", hotel.getNombre());
            startActivity(intent);
        }));
    }
}