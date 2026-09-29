package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.databinding.ActivityEnTrasladoBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.NavegacionHelper;

public class EnTrasladoActivity extends AppCompatActivity {

    private ActivityEnTrasladoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnTrasladoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de la barra de navegación inferior
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        // Mismo pedido que viene de EnCaminoActivity. Si no llega, se queda el texto de ejemplo del XML.
        PedidoTaxi pedido = getIntent().getSerializableExtra(Constants.EXTRA_PEDIDO_TAXI, PedidoTaxi.class);
        if (pedido != null) {
            binding.tvNombrePasajero.setText(pedido.getNombrePasajero());
            binding.tvRatingPasajero.setText(getString(R.string.en_camino_rating, pedido.getCalificacionPasajero()));
            binding.tvOrigen.setText(getString(R.string.pedido_origen, pedido.getOrigen()));
            binding.tvDestino.setText(getString(R.string.pedido_destino, pedido.getDestino()));
        }

        binding.btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(EnTrasladoActivity.this, EscaneoActivity.class);
            startActivity(intent);
        });
    }
}
