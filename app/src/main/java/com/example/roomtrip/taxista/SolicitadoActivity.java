package com.example.roomtrip.taxista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivitySolicitadoBinding;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionHelper;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pantalla principal del taxista: pedidos disponibles (SOLICITADO) leídos en tiempo real de
 * Firestore. Al aceptar uno, una transacción lo asigna a este taxista (RF-TAX-006).
 */
public class SolicitadoActivity extends AppCompatActivity {

    private static final String TAG = "SolicitadoActivity";

    private ActivitySolicitadoBinding binding;
    private SesionTaxistaManager sesion;
    private ServicioTaxiRepository repositorio;
    private TaxiNotificationHelper notificaciones;
    private String uid;

    private final List<ServicioTaxi> pedidos = new ArrayList<>();
    private PedidoTaxiAdapter adapter;

    // Listener de Firestore: se registra en onStart y se quita en onStop (Clase 9.2, consumo de batería)
    private ListenerRegistration registroPedidos;
    // La primera respuesta del listener trae todos los pedidos: no son "nuevos", no se notifican
    private boolean primeraCarga;
    // Evita aceptar dos pedidos por tocar dos veces seguidas
    private boolean aceptando;

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

        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivitySolicitadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavegacionHelper.configurarBarra(this, "TRABAJO");

        sesion = new SesionTaxistaManager(this);
        repositorio = new ServicioTaxiRepository();
        notificaciones = new TaxiNotificationHelper(this); // crea los canales "taxi" y "taxi_estado"

        pedirPermisoNotificaciones();
        configurarDisponibilidad();

        adapter = new PedidoTaxiAdapter(pedidos, this::aceptarPedido);
        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPedidos.setAdapter(adapter);
        binding.tvSinPedidos.setVisibility(View.VISIBLE);

        // Crea un pedido de prueba en Firestore: aparece en la lista por el listener, como uno real
        binding.btnSimularPedido.setOnClickListener(v -> simularPedido());

        // Si la app se cerró con un servicio en curso, se recupera desde Firestore
        if (savedInstanceState == null) {
            recuperarServicioEnCurso();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid == null) return;
        primeraCarga = true;
        registroPedidos = repositorio.escucharPedidosDisponibles(new ServicioTaxiRepository.ListenerPedidos() {
            @Override
            public void onPedidos(@NonNull List<ServicioTaxi> disponibles, @NonNull List<ServicioTaxi> nuevos) {
                mostrarPedidos(disponibles);
                if (!primeraCarga && sesion.isDisponible()) {
                    for (ServicioTaxi nuevo : nuevos) {
                        notificaciones.notificarNuevoPedido(nuevo);
                    }
                }
                primeraCarga = false;
            }

            @Override
            public void onFallo(@NonNull Exception e) {
                Log.e(TAG, "No se pudieron escuchar los pedidos", e);
                mostrarError(e);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registroPedidos != null) {
            registroPedidos.remove();
            registroPedidos = null;
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

    private void mostrarPedidos(List<ServicioTaxi> disponibles) {
        pedidos.clear();
        pedidos.addAll(disponibles);
        adapter.notifyDataSetChanged();
        binding.tvSinPedidos.setVisibility(pedidos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void simularPedido() {
        List<ServicioTaxi> ejemplos = MockData.getPedidosTaxiEjemplo();
        ServicioTaxi pedido = ejemplos.get(new Random().nextInt(ejemplos.size()));
        binding.btnSimularPedido.setEnabled(false);
        repositorio.crearPedido(pedido, id -> {
            binding.btnSimularPedido.setEnabled(true);
            Toast.makeText(this, R.string.toast_taxista_pedido_simulado, Toast.LENGTH_SHORT).show();
        }, e -> {
            binding.btnSimularPedido.setEnabled(true);
            Log.e(TAG, "No se pudo crear el pedido de prueba", e);
            mostrarError(e);
        });
    }

    private void recuperarServicioEnCurso() {
        repositorio.obtenerServicioEnCurso(uid, servicio -> {
            if (servicio == null) return;
            Toast.makeText(this, getString(R.string.toast_taxista_recuperando_servicio,
                    servicio.getEstado().getEtiqueta()), Toast.LENGTH_SHORT).show();
            notificaciones.mostrarServicioActivo(servicio);
            abrirPantallaDelServicio(servicio);
        }, e -> Log.e(TAG, "No se pudo consultar el servicio en curso", e));
    }

    /**
     * Aceptar pedido: SOLICITADO → ASIGNADO. Primero se verifica que el taxista no tenga otro servicio
     * en curso; luego la transacción asegura que nadie más lo haya tomado.
     */
    private void aceptarPedido(ServicioTaxi pedido) {
        if (!sesion.isDisponible()) {
            Toast.makeText(this, R.string.toast_taxista_activar_disponible, Toast.LENGTH_SHORT).show();
            return;
        }
        if (aceptando) return;
        aceptando = true;

        repositorio.obtenerServicioEnCurso(uid, actual -> {
            if (actual != null) {
                aceptando = false;
                Toast.makeText(this, R.string.toast_taxista_servicio_en_curso, Toast.LENGTH_SHORT).show();
                abrirPantallaDelServicio(actual);
                return;
            }
            repositorio.aceptarPedido(pedido.getId(), uid, sesion.getNombre(), aceptado -> {
                aceptando = false;
                if (!aceptado) {
                    Toast.makeText(this, R.string.toast_taxista_pedido_ya_tomado, Toast.LENGTH_SHORT).show();
                    return;
                }
                RegistroEventos.registrar(RegistroEventos.SERVICIO_TAXI_ACEPTADO, RolUsuario.TAXISTA,
                        "Servicio " + pedido.getId() + " aceptado");
                binding.switchDisponible.setChecked(false); // el listener del switch guarda disponible = false

                pedido.setEstado(EstadoServicioTaxi.ASIGNADO);
                pedido.setTaxistaId(uid);
                notificaciones.mostrarServicioActivo(pedido);
                abrirPantallaDelServicio(pedido);
            }, e -> {
                aceptando = false;
                Log.e(TAG, "No se pudo aceptar el pedido " + pedido.getId(), e);
                mostrarError(e);
            });
        }, e -> {
            aceptando = false;
            Log.e(TAG, "No se pudo consultar el servicio en curso", e);
            mostrarError(e);
        });
    }

    private void abrirPantallaDelServicio(ServicioTaxi servicio) {
        startActivity(new Intent(this, TaxiNotificationHelper.activityParaEstado(servicio.getEstado())));
    }

    private void mostrarError(Exception e) {
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
    }
}
