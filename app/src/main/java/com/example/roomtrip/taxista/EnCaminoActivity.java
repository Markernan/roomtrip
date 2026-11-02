package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivityEnCaminoBinding;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionHelper;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Cubre dos estados: ASIGNADO (aún no sale) y EN_CAMINO (yendo al hotel a recoger al pasajero).
 * La pantalla se dibuja con lo que llega del listener de Firestore, así siempre muestra el estado real.
 */
public class EnCaminoActivity extends AppCompatActivity {

    private static final String TAG = "EnCaminoActivity";

    private ActivityEnCaminoBinding binding;
    private ServicioTaxiRepository repositorio;
    private TaxiNotificationHelper notificaciones;
    private ListenerRegistration registroServicio;
    private ServicioTaxi servicio;
    private String uid;
    private boolean saliendo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivityEnCaminoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de la barra inferior de navegación
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        repositorio = new ServicioTaxiRepository();
        notificaciones = new TaxiNotificationHelper(this);

        binding.btnLlegueAlEncuentro.setEnabled(false);
        binding.btnLlegueAlEncuentro.setOnClickListener(v -> avanzarEstado());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid == null) return;
        registroServicio = repositorio.escucharServicioEnCurso(uid, s -> {
            if (saliendo) return;
            if (s == null) {          // se finalizó o ya no está asignado a este taxista
                saliendo = true;
                finish();
                return;
            }
            servicio = s;
            if (s.getEstado() == EstadoServicioTaxi.EN_TRASLADO) {
                saliendo = true;
                startActivity(new Intent(EnCaminoActivity.this, EnTrasladoActivity.class));
                finish();
                return;
            }
            mostrarServicio();
        }, e -> {
            Log.e(TAG, "No se pudo escuchar el servicio en curso", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registroServicio != null) {
            registroServicio.remove();
            registroServicio = null;
        }
    }

    private void mostrarServicio() {
        binding.tvNombrePasajero.setText(servicio.getClienteNombre());
        binding.tvRatingPasajero.setText(getString(R.string.en_camino_rating, servicio.getCalificacionCliente()));
        binding.tvOrigen.setText(getString(R.string.en_camino_recojo, servicio.getHotelNombre()));
        binding.tvDestino.setText(getString(R.string.pedido_destino, servicio.getAeropuerto()));
        binding.tvTiempoEncuentro.setText(getString(R.string.en_camino_tiempo, servicio.getMinutosAlEncuentro()));
        binding.tvEstado.setText(servicio.getEstado().getEtiqueta());
        binding.btnLlegueAlEncuentro.setText(servicio.getEstado() == EstadoServicioTaxi.ASIGNADO
                ? R.string.en_camino_iniciar_ruta : R.string.en_camino_llegue_al_encuentro);
        binding.btnLlegueAlEncuentro.setEnabled(true);
    }

    /**
     * ASIGNADO → EN_CAMINO → EN_TRASLADO en Firestore (transacción). La pantalla se actualiza sola con
     * el listener; aquí solo se actualiza la notificación persistente y se registra el evento.
     */
    private void avanzarEstado() {
        if (servicio == null) return;
        binding.btnLlegueAlEncuentro.setEnabled(false);
        String servicioId = servicio.getId();

        repositorio.avanzarEstado(servicioId, uid, nuevoEstado -> {
            if (nuevoEstado == null) {
                // Cambió desde otro lado: el listener ya trae el estado real
                Toast.makeText(this, R.string.toast_taxista_servicio_cambio, Toast.LENGTH_SHORT).show();
                return;
            }
            servicio.setEstado(nuevoEstado);
            notificaciones.mostrarServicioActivo(servicio);
            RegistroEventos.registrar(RegistroEventos.SERVICIO_TAXI_CAMBIO_ESTADO, RolUsuario.TAXISTA,
                    "Servicio " + servicioId + " pasó a " + nuevoEstado.name());
        }, e -> {
            Log.e(TAG, "No se pudo avanzar el estado del servicio " + servicioId, e);
            binding.btnLlegueAlEncuentro.setEnabled(true);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }
}
