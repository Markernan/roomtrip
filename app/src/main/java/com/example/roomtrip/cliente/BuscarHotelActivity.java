package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.SesionFirebase;

import java.util.ArrayList;
import java.util.List;

/** Resultados de búsqueda: los hoteles activos de Firestore (Lab 6). */
public class BuscarHotelActivity extends AppCompatActivity {

    private final List<Hotel> hoteles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;
        setContentView(R.layout.activity_buscar_hotel);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvResultados);
        rv.setLayoutManager(new LinearLayoutManager(this));
        HotelAdapter adapter = new HotelAdapter(hoteles, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra(Constants.EXTRA_HOTEL_ID, hotel.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        new HotelRepository().obtenerActivos(lista -> {
            hoteles.clear();
            hoteles.addAll(lista);
            adapter.notifyDataSetChanged();
        }, e -> {
            Log.e("BuscarHotelActivity", "No se pudieron leer los hoteles", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }
}
