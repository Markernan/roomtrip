package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.cliente.notificaciones.ClienteNotificationHelper;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivityCheckoutBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;

import java.time.LocalDate;

/**
 * Checkout del cliente (Lab 6) sobre una reserva de Firestore:
 * <ul>
 *   <li>Valoración y observaciones obligatorias (RF-CL-04). Se guardan en la reserva y en
 *       {@code valoraciones}, y recalculan la calificación del hotel (transacción).</li>
 *   <li>El checkout queda PENDIENTE; el cobro final lo hace el hotel y el cliente recibe la
 *       notificación cuando pasa a PROCESADO (RF-CL-05, ver {@code AvisosCobro}).</li>
 *   <li>Si el total alcanza el monto mínimo del hotel, puede pedir el taxi gratis al aeropuerto
 *       (RF-CL-08, RN-005): se crea el documento en {@code serviciosTaxi} que ven los taxistas.</li>
 * </ul>
 */
public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";

    /** Aún no hay calificación de clientes: todos empiezan con 5.0 para el taxista. */
    private static final double CALIFICACION_CLIENTE_INICIAL = 5.0;
    /** Estimado fijo hasta tener la ubicación real del taxista (Google Maps, Lab 7). */
    private static final int MINUTOS_AL_ENCUENTRO_ESTIMADOS = 10;

    private ActivityCheckoutBinding binding;
    private final ReservaRepository reservas = new ReservaRepository();
    private String uid;
    private String reservaId;
    private Reserva reserva;
    private Hotel hotel;
    /** El checkout ya se guardó en esta pantalla (si falla el taxi, se reintenta solo el taxi). */
    private boolean checkoutHecho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new ClienteNotificationHelper(this); // crea el canal "cliente_cobros"

        ArrayAdapter<String> aeropuertos = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.cliente_aeropuertos));
        binding.spinnerAeropuerto.setAdapter(aeropuertos);

        binding.btnSolicitarTaxi.setOnClickListener(v -> solicitarTaxi());
        binding.btnFinalizarCheckout.setOnClickListener(v -> finalizarCheckout());
        habilitarBotones(false);

        reservaId = getIntent().getStringExtra(Constants.EXTRA_RESERVA_ID);
        if (reservaId == null) {
            cerrarConMensaje(R.string.cliente_reserva_no_encontrada);
            return;
        }
        cargarReserva();
    }

    private void cargarReserva() {
        reservas.obtener(reservaId, encontrada -> {
            if (encontrada == null || !uid.equals(encontrada.getClienteId())) {
                cerrarConMensaje(R.string.cliente_reserva_no_encontrada);
                return;
            }
            if (!encontrada.puedeHacerCheckout(LocalDate.now())) {
                cerrarConMensaje(R.string.cliente_checkout_no_disponible);
                return;
            }
            reserva = encontrada;
            new HotelRepository().obtener(reserva.getHotelId(), h -> {
                hotel = h;
                mostrarReserva();
            }, this::mostrarError);
        }, this::mostrarError);
    }

    private void mostrarReserva() {
        String total = ClienteNotificationHelper.formatearMonto(reserva.getMontoTotal());
        binding.tvNotificacionCobro.setText(getString(R.string.cliente_checkout_banner_total, total));
        binding.tvResumenEstadia.setText(getResources().getQuantityString(R.plurals.cliente_checkout_resumen,
                reserva.getNoches(), reserva.getNoches(), reserva.getHotelNombre(), reserva.getHabitacionTipo()));

        // RN-005 / RF-AH-10: el hotel define el monto mínimo para el taxi gratis
        double minimo = hotel != null ? hotel.getMontoMinimoTaxi() : Hotel.MONTO_MINIMO_TAXI_POR_DEFECTO;
        boolean califica = reserva.calificaParaTaxi(minimo) && !reserva.isTaxiSolicitado();
        binding.cajaTaxiGratis.setVisibility(califica ? View.VISIBLE : View.GONE);
        habilitarBotones(true);
    }

    /** RF-CL-04: la valoración (1 a 5 estrellas) y las observaciones son obligatorias. */
    private boolean valoracionCompleta() {
        if (binding.ratingBarEstadia.getRating() < 1) {
            Toast.makeText(this, R.string.cliente_checkout_falta_valoracion, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (observaciones().isEmpty()) {
            binding.etObservaciones.setError(getString(R.string.cliente_checkout_falta_observaciones));
            binding.etObservaciones.requestFocus();
            return false;
        }
        return true;
    }

    private String observaciones() {
        return binding.etObservaciones.getText().toString().trim();
    }

    private void finalizarCheckout() {
        if (checkoutHecho) {
            irAMisReservas();
            return;
        }
        if (!valoracionCompleta()) return;
        guardarCheckout(() -> {
            Toast.makeText(this, R.string.cliente_checkout_enviado_detalle, Toast.LENGTH_LONG).show();
            irAMisReservas();
        });
    }

    /** Con taxi: primero guarda el checkout (si no se hizo) y luego crea el pedido. */
    private void solicitarTaxi() {
        if (checkoutHecho) {
            crearPedidoTaxi();
            return;
        }
        if (!valoracionCompleta()) return;
        guardarCheckout(this::crearPedidoTaxi);
    }

    private void guardarCheckout(Runnable despues) {
        habilitarBotones(false);
        float calificacion = binding.ratingBarEstadia.getRating();
        reservas.hacerCheckout(reservaId, uid, calificacion, observaciones(), hecho -> {
            if (!hecho) {
                cerrarConMensaje(R.string.cliente_checkout_no_disponible);
                return;
            }
            checkoutHecho = true;
            RegistroEventos.registrar(RegistroEventos.CHECKOUT_SOLICITADO, RolUsuario.CLIENTE,
                    "Checkout de la reserva " + reservaId + " en " + reserva.getHotelNombre());
            despues.run();
        }, e -> {
            habilitarBotones(true);
            mostrarError(e);
        });
    }

    private void crearPedidoTaxi() {
        habilitarBotones(false);
        String nombreCliente = new SesionClienteManager(this).getNombre();
        String aeropuerto = String.valueOf(binding.spinnerAeropuerto.getSelectedItem());
        ServicioTaxi pedido = ServicioTaxi.nuevaSolicitud(uid, nombreCliente, CALIFICACION_CLIENTE_INICIAL,
                reserva.getHotelId(), reserva.getHotelNombre(), aeropuerto, MINUTOS_AL_ENCUENTRO_ESTIMADOS);
        pedido.setReservaId(reservaId);

        new ServicioTaxiRepository().crearPedido(pedido, servicioId -> {
            RegistroEventos.registrar(RegistroEventos.SERVICIO_TAXI_SOLICITADO, RolUsuario.CLIENTE,
                    "Taxi " + servicioId + " desde " + reserva.getHotelNombre());
            // Si esta marca falla, el pedido ya existe: igual se muestra la búsqueda
            reservas.marcarTaxiSolicitado(reservaId, servicioId, ignorado -> { },
                    e -> Log.w(TAG, "No se pudo marcar el taxi en la reserva " + reservaId, e));
            Intent intent = new Intent(this, BuscandoTaxistaActivity.class);
            intent.putExtra(Constants.EXTRA_SERVICIO_TAXI_ID, servicioId);
            startActivity(intent);
            finish();
        }, e -> {
            // El checkout ya quedó guardado: el botón de taxi sirve para reintentar
            habilitarBotones(true);
            mostrarError(e);
        });
    }

    private void habilitarBotones(boolean habilitar) {
        binding.btnFinalizarCheckout.setEnabled(habilitar);
        binding.btnSolicitarTaxi.setEnabled(habilitar);
    }

    private void irAMisReservas() {
        Intent intent = new Intent(this, MisReservasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void cerrarConMensaje(@StringRes int mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        finish();
    }

    private void mostrarError(Exception e) {
        Log.e(TAG, "Error en el checkout de la reserva " + reservaId, e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
    }
}
