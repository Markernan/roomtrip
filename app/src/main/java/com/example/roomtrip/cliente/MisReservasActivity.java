package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.utils.NavegacionClienteHelper;

import java.util.List;

public class MisReservasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        List<Hotel> reservas = MockData.getReservasClienteEjemplo();

        RecyclerView rv = findViewById(R.id.rvMisReservas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ReservaAdapter(
                reservas,
                hotel -> startActivity(new Intent(this, ChatActivity.class)),
                hotel -> {
                    Intent intent = new Intent(this, CheckoutActivity.class);
                    intent.putExtra("nombreHotel", hotel.getNombre());
                    startActivity(intent);
                }));

        NavegacionClienteHelper.configurarBarra(this, "RESERVAS");
    }
}