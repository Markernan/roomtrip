package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomtrip.app.R;

import java.util.ArrayList;
import java.util.List;

/** Pantalla de búsqueda/catálogo de hoteles (RF-CL-02). */
public class BuscarHotelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_hotel);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // TODO: reemplazar por consulta a Firestore filtrada por el texto de etBuscar (Lab 6).
        List<Hotel> resultados = new ArrayList<>();
        resultados.add(new Hotel("Hotel Larco Suites", "Lima, Perú · 4.5 estrellas", "", 4.5f,
                R.drawable.foto_hotel_miraflores));
        resultados.add(new Hotel("Grand Palace Lima", "Lima, Perú · 5.0 estrellas", "", 5.0f,
                R.drawable.foto_hotel_miraflores));

        RecyclerView rv = findViewById(R.id.rvResultados);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new HotelAdapter(resultados, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra("nombreHotel", hotel.nombre);
            startActivity(intent);
        }));
    }
}
