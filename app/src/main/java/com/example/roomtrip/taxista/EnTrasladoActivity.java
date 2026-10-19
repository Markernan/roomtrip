package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.databinding.ActivityEnTrasladoBinding;
import com.example.roomtrip.taxista.data.ServicioLocalRepository;
import com.example.roomtrip.utils.NavegacionHelper;

/** Estado EN_TRASLADO: el pasajero va en el taxi rumbo al aeropuerto. */
public class EnTrasladoActivity extends AppCompatActivity {

    private ActivityEnTrasladoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnTrasladoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de la barra de navegación inferior
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        binding.btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(EnTrasladoActivity.this, EscaneoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // El servicio se lee desde servicio_activo.json; si ya se finalizó, se cierra esta pantalla
        new ServicioLocalRepository(this).obtenerServicioActivo(servicio -> {
            if (servicio == null) {
                finish();
                return;
            }
            PedidoTaxi pedido = servicio.getPedido();
            binding.tvNombrePasajero.setText(pedido.getNombrePasajero());
            binding.tvRatingPasajero.setText(getString(R.string.en_camino_rating, pedido.getCalificacionPasajero()));
            binding.tvOrigen.setText(getString(R.string.pedido_origen, pedido.getOrigen()));
            binding.tvDestino.setText(getString(R.string.pedido_destino, pedido.getDestino()));
            binding.tvEstado.setText(servicio.getEstado().getEtiqueta());
        });
    }
}
