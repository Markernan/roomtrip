package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.databinding.ActivityEnCaminoBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.NavegacionHelper;

public class EnCaminoActivity extends AppCompatActivity {

    private ActivityEnCaminoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnCaminoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de la barra inferior de navegación
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        // Pedido elegido en SolicitadoActivity. Si no llega, se queda el texto de ejemplo del XML.
        PedidoTaxi pedido = getIntent().getSerializableExtra(Constants.EXTRA_PEDIDO_TAXI, PedidoTaxi.class);
        if (pedido != null) {
            mostrarPedido(pedido);
        }

        binding.btnLlegueAlEncuentro.setOnClickListener(v -> {
            Intent intent = new Intent(EnCaminoActivity.this, EnTrasladoActivity.class);
            intent.putExtra(Constants.EXTRA_PEDIDO_TAXI, pedido);
            startActivity(intent);
        });
    }

    private void mostrarPedido(PedidoTaxi pedido) {
        binding.tvNombrePasajero.setText(pedido.getNombrePasajero());
        binding.tvRatingPasajero.setText(getString(R.string.en_camino_rating, pedido.getCalificacionPasajero()));
        binding.tvOrigen.setText(getString(R.string.en_camino_recojo, pedido.getOrigen()));
        binding.tvDestino.setText(getString(R.string.pedido_destino, pedido.getDestino()));
        binding.tvTiempoEncuentro.setText(getString(R.string.en_camino_tiempo, pedido.getMinutosAlEncuentro()));
    }
}
