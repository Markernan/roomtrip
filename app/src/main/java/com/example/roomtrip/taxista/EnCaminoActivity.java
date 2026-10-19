package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivityEnCaminoBinding;
import com.example.roomtrip.taxista.data.ServicioLocalRepository;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.example.roomtrip.utils.NavegacionHelper;

/** Cubre dos estados: ASIGNADO (aún no sale) y EN_CAMINO (yendo al hotel a recoger al pasajero). */
public class EnCaminoActivity extends AppCompatActivity {

    private ActivityEnCaminoBinding binding;
    private ServicioLocalRepository repositorio;
    private TaxiNotificationHelper notificaciones;
    private ServicioTaxi servicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnCaminoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de la barra inferior de navegación
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        repositorio = new ServicioLocalRepository(this);
        notificaciones = new TaxiNotificationHelper(this);

        binding.btnLlegueAlEncuentro.setEnabled(false);
        binding.btnLlegueAlEncuentro.setOnClickListener(v -> avanzarEstado());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Se lee siempre desde el archivo para mostrar el estado real guardado
        repositorio.obtenerServicioActivo(s -> {
            if (s == null) {
                finish();
                return;
            }
            servicio = s;
            mostrarServicio();
        });
    }

    private void mostrarServicio() {
        PedidoTaxi pedido = servicio.getPedido();
        binding.tvNombrePasajero.setText(pedido.getNombrePasajero());
        binding.tvRatingPasajero.setText(getString(R.string.en_camino_rating, pedido.getCalificacionPasajero()));
        binding.tvOrigen.setText(getString(R.string.en_camino_recojo, pedido.getOrigen()));
        binding.tvDestino.setText(getString(R.string.pedido_destino, pedido.getDestino()));
        binding.tvTiempoEncuentro.setText(getString(R.string.en_camino_tiempo, pedido.getMinutosAlEncuentro()));
        binding.tvEstado.setText(servicio.getEstado().getEtiqueta());
        binding.btnLlegueAlEncuentro.setText(servicio.getEstado() == EstadoServicioTaxi.ASIGNADO
                ? R.string.en_camino_iniciar_ruta : R.string.en_camino_llegue_al_encuentro);
        binding.btnLlegueAlEncuentro.setEnabled(true);
    }

    /** ASIGNADO -> EN_CAMINO -> EN_TRASLADO. Cada cambio se guarda y actualiza la MISMA notificación. */
    private void avanzarEstado() {
        servicio.avanzarEstado();
        boolean pasaATraslado = servicio.getEstado() == EstadoServicioTaxi.EN_TRASLADO;
        binding.btnLlegueAlEncuentro.setEnabled(false);

        repositorio.guardarServicioActivo(servicio, ignorado -> {
            notificaciones.mostrarServicioActivo(servicio);
            if (pasaATraslado) {
                startActivity(new Intent(EnCaminoActivity.this, EnTrasladoActivity.class));
                finish();
            } else {
                mostrarServicio();
            }
        });
    }
}
