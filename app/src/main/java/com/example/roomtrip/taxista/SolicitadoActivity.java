package com.example.roomtrip.taxista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivitySolicitadoBinding;
import com.example.roomtrip.taxista.data.ServicioLocalRepository;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.example.roomtrip.utils.NavegacionHelper;

import java.util.List;

/** Pantalla principal del taxista: lista de pedidos disponibles (estado SOLICITADO). */
public class SolicitadoActivity extends AppCompatActivity {

    private ActivitySolicitadoBinding binding;
    private SesionTaxistaManager sesion;
    private ServicioLocalRepository repositorio;
    private TaxiNotificationHelper notificaciones;

    // Debe registrarse antes de que la Activity llegue a STARTED (por eso es un campo)
    private final ActivityResultLauncher<String> permisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, R.string.toast_taxista_permiso_notificaciones, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolicitadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavegacionHelper.configurarBarra(this, "TRABAJO");

        sesion = new SesionTaxistaManager(this);
        repositorio = new ServicioLocalRepository(this);
        notificaciones = new TaxiNotificationHelper(this); // crea los canales "taxi" y "taxi_estado"

        pedirPermisoNotificaciones();
        configurarDisponibilidad();

        // Data estática (Lab 4). En el Lab 6/7 vendrá de Firebase.
        List<PedidoTaxi> pedidos = MockData.getPedidosTaxiEjemplo();

        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPedidos.setAdapter(new PedidoTaxiAdapter(pedidos, this::aceptarPedido));

        binding.tvSinPedidos.setVisibility(pedidos.isEmpty() ? View.VISIBLE : View.GONE);

        binding.btnSimularPedido.setOnClickListener(v -> {
            if (!sesion.isDisponible()) {
                Toast.makeText(this, R.string.toast_taxista_activar_disponible, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pedidos.isEmpty()) {
                notificaciones.notificarNuevoPedido(pedidos.get(0));
            }
        });

        // Si la app se cerró con un servicio en curso, se recupera desde servicio_activo.json
        if (savedInstanceState == null) {
            recuperarServicioActivo();
        }
    }

    // POST_NOTIFICATIONS es obligatorio desde Android 13; minSdk de la app es 34, así que siempre se pide
    private void pedirPermisoNotificaciones() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void configurarDisponibilidad() {
        boolean disponible = sesion.isDisponible();
        binding.switchDisponible.setChecked(disponible);
        actualizarBadgeEstado(disponible);
        binding.switchDisponible.setOnCheckedChangeListener((boton, marcado) -> {
            sesion.setDisponible(marcado);
            actualizarBadgeEstado(marcado);
        });
    }

    private void actualizarBadgeEstado(boolean disponible) {
        binding.tvEstado.setText(disponible
                ? R.string.solicitado_estado_buscando : R.string.solicitado_estado_no_disponible);
    }

    private void recuperarServicioActivo() {
        repositorio.obtenerServicioActivo(servicio -> {
            if (servicio == null) return;
            Toast.makeText(this, getString(R.string.toast_taxista_recuperando_servicio,
                    servicio.getEstado().getEtiqueta()), Toast.LENGTH_SHORT).show();
            notificaciones.mostrarServicioActivo(servicio);
            abrirPantallaDelServicio(servicio);
        });
    }

    /** Aceptar pedido: SOLICITADO -> ASIGNADO, se guarda en JSON y se muestra la notificación persistente. */
    private void aceptarPedido(PedidoTaxi pedido) {
        if (!sesion.isDisponible()) {
            Toast.makeText(this, R.string.toast_taxista_activar_disponible, Toast.LENGTH_SHORT).show();
            return;
        }
        repositorio.obtenerServicioActivo(actual -> {
            if (actual != null) {
                Toast.makeText(this, R.string.toast_taxista_servicio_en_curso, Toast.LENGTH_SHORT).show();
                abrirPantallaDelServicio(actual);
                return;
            }
            ServicioTaxi servicio = new ServicioTaxi(pedido);
            servicio.avanzarEstado();
            repositorio.guardarServicioActivo(servicio, ignorado -> {
                binding.switchDisponible.setChecked(false); // el listener guarda disponible = false
                notificaciones.mostrarServicioActivo(servicio);
                abrirPantallaDelServicio(servicio);
            });
        });
    }

    private void abrirPantallaDelServicio(ServicioTaxi servicio) {
        startActivity(new Intent(this, TaxiNotificationHelper.activityParaEstado(servicio.getEstado())));
    }
}
