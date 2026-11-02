package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.Servicio;
import com.example.roomtrip.data.model.ValoracionHotel;
import com.example.roomtrip.databinding.ItemFullReviewCardBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.InsetsHelper;
import com.example.roomtrip.utils.NavegacionClienteHelper;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detalle del hotel (Lab 6): datos, habitaciones, servicios y opiniones desde Firestore. En
 * "Detalles" el cliente elige la habitación y el rango de fechas; antes de pagar se avisa si esas
 * noches ya están tomadas (RN-001/RN-002). La validación definitiva la hace la transacción al pagar.
 */
public class DetalleHotelActivity extends AppCompatActivity {

    private static final String TAG = "DetalleHotelActivity";
    private static final DateTimeFormatter FORMATO_CORTO = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es-PE"));

    private View contenidoOpiniones, contenidoDetalles, contenidoServicios;
    private TextView tabOpiniones, tabDetalles, tabServicios;
    private TextView tvFechaEntrada, tvFechaSalida, tvAlertaConflicto, tvDescripcionHabitacion,
            tvAreaHabitacion, tvDetallePrecio, tvPrecioTotal;
    private View btnReservar;
    private Spinner spinnerHabitacion;

    private final HotelRepository hoteles = new HotelRepository();
    private final ReservaRepository reservas = new ReservaRepository();
    private final List<Habitacion> habitaciones = new ArrayList<>();
    private String uid;
    private String hotelId;
    private Hotel hotel;
    private Habitacion habitacion;
    private LocalDate entrada = LocalDate.now();
    private LocalDate salida = LocalDate.now().plusDays(1);
    private boolean hayConflicto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        setContentView(R.layout.activity_detalle_hotel);

        hotelId = getIntent().getStringExtra(Constants.EXTRA_HOTEL_ID);

        View btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());
        InsetsHelper.empujarBajoBarraEstado(btnVolver);

        contenidoOpiniones = findViewById(R.id.contenidoOpiniones);
        contenidoDetalles = findViewById(R.id.contenidoDetalles);
        contenidoServicios = findViewById(R.id.contenidoServicios);
        tabOpiniones = findViewById(R.id.tabOpiniones);
        tabDetalles = findViewById(R.id.tabDetalles);
        tabServicios = findViewById(R.id.tabServicios);

        tabOpiniones.setOnClickListener(v -> mostrarPestana(0));
        tabDetalles.setOnClickListener(v -> mostrarPestana(1));
        tabServicios.setOnClickListener(v -> mostrarPestana(2));
        mostrarPestana(0);

        tvFechaEntrada = contenidoDetalles.findViewById(R.id.tvFechaEntrada);
        tvFechaSalida = contenidoDetalles.findViewById(R.id.tvFechaSalida);
        tvAlertaConflicto = contenidoDetalles.findViewById(R.id.tvAlertaConflicto);
        tvDescripcionHabitacion = contenidoDetalles.findViewById(R.id.tvDescripcionHabitacion);
        tvAreaHabitacion = contenidoDetalles.findViewById(R.id.tvAreaHabitacion);
        tvDetallePrecio = contenidoDetalles.findViewById(R.id.tvDetallePrecio);
        tvPrecioTotal = contenidoDetalles.findViewById(R.id.tvPrecioTotal);
        spinnerHabitacion = contenidoDetalles.findViewById(R.id.spinnerHabitacion);
        btnReservar = contenidoDetalles.findViewById(R.id.btnReservar);

        contenidoDetalles.findViewById(R.id.contenedorFechas).setOnClickListener(v -> elegirFechas());
        btnReservar.setOnClickListener(v -> reservar());
        btnReservar.setEnabled(false);

        NavegacionClienteHelper.configurarBarra(this, "EXPLORAR");

        if (hotelId == null) {
            finish();
            return;
        }
        cargarHotel();
        cargarHabitaciones();
        cargarServicios();
        cargarOpiniones();
    }

    // ---------- Pestañas ----------

    private void mostrarPestana(int indice) {
        contenidoOpiniones.setVisibility(indice == 0 ? View.VISIBLE : View.GONE);
        contenidoDetalles.setVisibility(indice == 1 ? View.VISIBLE : View.GONE);
        contenidoServicios.setVisibility(indice == 2 ? View.VISIBLE : View.GONE);

        activarPestana(tabOpiniones, indice == 0);
        activarPestana(tabDetalles, indice == 1);
        activarPestana(tabServicios, indice == 2);
    }

    private void activarPestana(TextView tab, boolean activa) {
        if (activa) {
            tab.setBackgroundResource(R.drawable.bg_boton_turquesa);
            tab.setTextColor(getColor(R.color.blanco));
        } else {
            tab.setBackground(null);
            tab.setTextColor(getColor(R.color.texto_secundario));
        }
    }

    // ---------- Datos del hotel ----------

    private void cargarHotel() {
        hoteles.obtener(hotelId, h -> {
            if (h == null) {
                Toast.makeText(this, R.string.cliente_hotel_no_disponible, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            hotel = h;
            ((TextView) findViewById(R.id.tvNombreHotel)).setText(h.getNombre());
            ((TextView) findViewById(R.id.tvCalificacionDetalle)).setText(String.format(Locale.US, "%.1f", h.getCalificacion()));
            ((ImageView) findViewById(R.id.ivFotoHotel)).setImageResource(h.getImagenResId());
            actualizarResumen();
        }, this::mostrarError);
    }

    private void cargarHabitaciones() {
        hoteles.obtenerHabitaciones(hotelId, lista -> {
            habitaciones.clear();
            habitaciones.addAll(lista);
            List<String> etiquetas = new ArrayList<>();
            for (Habitacion h : lista) {
                etiquetas.add(getString(R.string.cliente_habitacion_opcion, h.getTipo(), h.getPrecio()));
            }
            if (etiquetas.isEmpty()) etiquetas.add(getString(R.string.cliente_sin_habitaciones));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner_cliente, etiquetas);
            adapter.setDropDownViewResource(R.layout.item_spinner_cliente);
            spinnerHabitacion.setAdapter(adapter);
            spinnerHabitacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    habitacion = position < habitaciones.size() ? habitaciones.get(position) : null;
                    actualizarResumen();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    habitacion = null;
                    actualizarResumen();
                }
            });
            habitacion = lista.isEmpty() ? null : lista.get(0);
            actualizarResumen();
        }, this::mostrarError);
    }

    private void cargarServicios() {
        LinearLayout contenedor = contenidoServicios.findViewById(R.id.contenedorServicios);
        hoteles.obtenerServicios(hotelId, lista -> {
            contenedor.removeAllViews();
            for (Servicio s : lista) {
                String detalle = s.getDescripcion() != null && !s.getDescripcion().isEmpty() ? s.getDescripcion() : s.getCategoria();
                String precio = s.getPrecio() > 0 ? getString(R.string.cliente_servicio_precio, s.getPrecio())
                        : getString(R.string.cliente_servicio_gratis);
                agregarServicio(contenedor, iconoServicio(s), s.getNombre(), detalle + " · " + precio);
            }
            // RN-005: traslado gratuito al aeropuerto si la reserva alcanza el monto mínimo del hotel
            double minimo = hotel != null ? hotel.getMontoMinimoTaxi() : Hotel.MONTO_MINIMO_TAXI_POR_DEFECTO;
            agregarServicio(contenedor, R.drawable.ic_taxi, getString(R.string.cliente_servicio_taxi),
                    getString(R.string.cliente_servicio_taxi_detalle, minimo));
        }, this::mostrarError);
    }

    private int iconoServicio(Servicio s) {
        String texto = (s.getNombre() + " " + s.getCategoria()).toLowerCase(Locale.ROOT);
        if (texto.contains("wifi") || texto.contains("wi-fi")) return R.drawable.ic_wifi;
        if (texto.contains("desayuno") || texto.contains("aliment")) return R.drawable.ic_breakfast;
        if (texto.contains("masaje") || texto.contains("spa") || texto.contains("bienestar")) return R.drawable.ic_spa;
        return R.drawable.ic_check;
    }

    private void agregarServicio(LinearLayout contenedor, int icono, String nombre, String descripcion) {
        View fila = LayoutInflater.from(this).inflate(R.layout.fila_servicio, contenedor, false);
        ((ImageView) fila.findViewById(R.id.ivIconoServicio)).setImageResource(icono);
        ((TextView) fila.findViewById(R.id.tvNombreServicio)).setText(nombre);
        ((TextView) fila.findViewById(R.id.tvDescripcionServicio)).setText(descripcion);
        contenedor.addView(fila);
    }

    private void cargarOpiniones() {
        LinearLayout contenedor = contenidoOpiniones.findViewById(R.id.contenedorOpiniones);
        TextView tvGeneral = contenidoOpiniones.findViewById(R.id.tvCalificacionGeneral);
        TextView tvCantidad = contenidoOpiniones.findViewById(R.id.tvNumOpiniones);
        reservas.obtenerValoraciones(hotelId, lista -> {
            contenedor.removeAllViews();
            double suma = 0;
            for (ValoracionHotel v : lista) {
                suma += v.getCalificacion();
                ItemFullReviewCardBinding fila = ItemFullReviewCardBinding.inflate(getLayoutInflater(), contenedor, false);
                fila.tvAvatarInitials.setText(v.aVista().getInicialesUsuario());
                fila.tvReviewerName.setText(v.getClienteNombre());
                fila.tvReviewMeta.setText(getString(R.string.adapter_valoracion_meta, v.getHabitacionTipo(), v.aVista().getFecha()));
                fila.tvStars.setText(estrellas(v.getCalificacion()));
                fila.tvCommentText.setText(v.getComentario());
                contenedor.addView(fila.getRoot());
            }
            if (lista.isEmpty()) {
                tvGeneral.setText(R.string.cliente_sin_opiniones);
                tvCantidad.setText("");
            } else {
                tvGeneral.setText(getString(R.string.detalle_calificacion_general_valor, suma / lista.size()));
                tvCantidad.setText(getResources().getQuantityString(R.plurals.cliente_opiniones, lista.size(), lista.size()));
            }
        }, this::mostrarError);
    }

    private static String estrellas(double calificacion) {
        int llenas = (int) Math.max(0, Math.min(5, Math.round(calificacion)));
        return "★".repeat(llenas) + "☆".repeat(5 - llenas);
    }

    // ---------- Fechas y disponibilidad ----------

    /** Selector de rango de Material; no deja elegir días pasados. */
    private void elegirFechas() {
        MaterialDatePicker<Pair<Long, Long>> selector = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.cliente_elige_fechas)
                .setSelection(Pair.create(aUtc(entrada), aUtc(salida)))
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now()).build())
                .build();
        selector.addOnPositiveButtonClickListener(seleccion -> {
            if (seleccion == null || seleccion.first == null || seleccion.second == null) return;
            entrada = deUtc(seleccion.first);
            salida = deUtc(seleccion.second);
            if (!salida.isAfter(entrada)) salida = entrada.plusDays(1);  // al menos una noche
            actualizarResumen();
        });
        selector.show(getSupportFragmentManager(), "rangoFechas");
    }

    /** MaterialDatePicker trabaja con medianoche UTC en milisegundos. */
    private static long aUtc(LocalDate fecha) {
        return fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private static LocalDate deUtc(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private void actualizarResumen() {
        tvFechaEntrada.setText(entrada.format(FORMATO_CORTO));
        tvFechaSalida.setText(salida.format(FORMATO_CORTO));
        int noches = Reserva.contarNoches(entrada.toString(), salida.toString());
        if (habitacion == null) {
            tvDescripcionHabitacion.setText(R.string.cliente_sin_habitaciones);
            tvAreaHabitacion.setText("");
            tvPrecioTotal.setText("");
            btnReservar.setEnabled(false);
            return;
        }
        tvDescripcionHabitacion.setText(getString(R.string.cliente_habitacion_descripcion, habitacion.getTipo(), habitacion.getCapacidad()));
        tvAreaHabitacion.setText(habitacion.getAreaM2() > 0 ? getString(R.string.cliente_habitacion_area, habitacion.getAreaM2()) : "");
        tvDetallePrecio.setText(getResources().getQuantityString(R.plurals.cliente_precio_noches, noches, noches,
                entrada.format(FORMATO_CORTO), salida.format(FORMATO_CORTO)));
        tvPrecioTotal.setText(getString(R.string.superadmin_monto_soles, noches * habitacion.getPrecio()));
        verificarDisponibilidad(noches);
    }

    /** Aviso previo de RN-001/RN-002 (tvAlertaConflicto). La transacción al pagar es la que manda. */
    private void verificarDisponibilidad(int noches) {
        tvAlertaConflicto.setVisibility(View.GONE);
        hayConflicto = false;
        if (noches > Reserva.MAXIMO_NOCHES) {
            mostrarConflicto(getString(R.string.cliente_maximo_noches, Reserva.MAXIMO_NOCHES));
            return;
        }
        btnReservar.setEnabled(hotel != null);
        Habitacion consultada = habitacion;
        reservas.verificarDisponibilidad(habitacion.getId(), uid, entrada.toString(), salida.toString(), resultado -> {
            if (consultada != habitacion) return;   // el cliente ya cambió de habitación
            switch (resultado) {
                case HABITACION_OCUPADA:
                    mostrarConflicto(getString(R.string.cliente_conflicto_habitacion));
                    break;
                case CLIENTE_CON_RESERVA:
                    mostrarConflicto(getString(R.string.cliente_conflicto_cliente));
                    break;
                case FECHAS_INVALIDAS:
                    mostrarConflicto(getString(R.string.cliente_fechas_invalidas));
                    break;
                default:
                    break;
            }
        }, e -> Log.w(TAG, "No se pudo verificar la disponibilidad", e));
    }

    private void mostrarConflicto(String mensaje) {
        hayConflicto = true;
        tvAlertaConflicto.setText(mensaje);
        tvAlertaConflicto.setVisibility(View.VISIBLE);
        btnReservar.setEnabled(false);
    }

    private void reservar() {
        if (hotel == null || habitacion == null || hayConflicto) return;
        Intent intent = new Intent(this, PagoReservaActivity.class);
        intent.putExtra(Constants.EXTRA_HOTEL, hotel);
        intent.putExtra(Constants.EXTRA_HABITACION_ID, habitacion.getId());
        intent.putExtra(PagoReservaActivity.EXTRA_HABITACION_TIPO, habitacion.getTipo());
        intent.putExtra(PagoReservaActivity.EXTRA_HABITACION_CAPACIDAD, habitacion.getCapacidad());
        intent.putExtra(PagoReservaActivity.EXTRA_HABITACION_PRECIO, habitacion.getPrecio());
        intent.putExtra(Constants.EXTRA_FECHA_ENTRADA, entrada.toString());
        intent.putExtra(Constants.EXTRA_FECHA_SALIDA, salida.toString());
        startActivity(intent);
    }

    private void mostrarError(Exception e) {
        Log.e(TAG, "Error de Firestore", e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
    }
}
