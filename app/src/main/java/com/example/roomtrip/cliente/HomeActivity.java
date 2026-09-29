package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.utils.NavegacionClienteHelper;

import java.util.List;

/** Pantalla 2: inicio del cliente, con el hotel recomendado. */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText("Hola, William");

        List<Hotel> hoteles = MockData.getHotelesClienteEjemplo();

        RecyclerView rvHoteles = findViewById(R.id.rvHoteles);
        rvHoteles.setLayoutManager(new LinearLayoutManager(this));
        rvHoteles.setAdapter(new HotelAdapter(hoteles, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra("nombreHotel", hotel.getNombre());
            startActivity(intent);
        }));

        findViewById(R.id.campoBuscar).setOnClickListener(v ->
                startActivity(new Intent(this, BuscarHotelActivity.class)));

        NavegacionClienteHelper.configurarBarra(this, "EXPLORAR");
    }
}