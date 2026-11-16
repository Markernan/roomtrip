package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Reserva;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetalleHotelReporteActivity extends AppCompatActivity {

    private static final String TAG = "DetalleHotelReporte";
    private Toolbar toolbar;
    private ImageView ivHotelHeader;
    private TextView tvHotelTitle, tvHotelLocation;
    private TextView tvIngresosDetalle, tvOcupacionDetalle, tvTicketDetalle, tvNochesDetalle;
    private TrendChartView viewTrendChart;
    private StatusPieChartView viewStatusChart;
    private LinearLayout llDesgloseHabitaciones;
    private ImageButton btnExportSmall;
    private Hotel hotel;
    private String periodo = "ESTE_MES";

    private final List<Habitacion> listaHabitaciones = new ArrayList<>();
    private final List<Reserva> listaReservas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_detalle_hotel_reporte);

        toolbar = findViewById(R.id.toolbar);
        ivHotelHeader = findViewById(R.id.ivHotelHeader);
        tvHotelTitle = findViewById(R.id.tvHotelTitle);
        tvHotelLocation = findViewById(R.id.tvHotelLocation);
        tvIngresosDetalle = findViewById(R.id.tvIngresosDetalle);
        tvOcupacionDetalle = findViewById(R.id.tvOcupacionDetalle);
        tvTicketDetalle = findViewById(R.id.tvTicketDetalle);
        tvNochesDetalle = findViewById(R.id.tvNochesDetalle);
        viewTrendChart = findViewById(R.id.viewTrendChart);
        viewStatusChart = findViewById(R.id.viewStatusChart);
        llDesgloseHabitaciones = findViewById(R.id.llDesgloseHabitaciones);
        btnExportSmall = findViewById(R.id.btnExportSmall);

        toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra("hotel")) {
            hotel = (Hotel) getIntent().getSerializableExtra("hotel");
        }
        if (getIntent().hasExtra("PERIODO")) {
            periodo = getIntent().getStringExtra("PERIODO");
        }

        if (hotel != null) {
            tvHotelTitle.setText(hotel.getNombre());
            tvHotelLocation.setText(hotel.getUbicacion());

            if (hotel.getImagenResId() != 0) {
                Glide.with(this)
                        .load(hotel.getImagenResId())
                        .placeholder(R.drawable.foto_hotel_miraflores)
                        .into(ivHotelHeader);
            }

            cargarDatosHotel();
        }

        btnExportSmall.setOnClickListener(v -> compartirReporte());
    }

    private void cargarDatosHotel() {
        if (hotel == null || hotel.getId() == null) return;

        HotelRepository hotelRepo = new HotelRepository();
        ReservaRepository reservaRepo = new ReservaRepository();

        hotelRepo.obtenerHabitaciones(hotel.getId(), habitaciones -> {
            listaHabitaciones.clear();
            listaHabitaciones.addAll(habitaciones);
            reservaRepo.obtenerDeHotel(hotel.getId(), reservas -> {
                listaReservas.clear();
                listaReservas.addAll(reservas);
                calcularYMostrarMetricas();
            }, e -> Log.e(TAG, "Error al cargar reservas del hotel", e));
        }, e -> Log.e(TAG, "Error al cargar habitaciones del hotel", e));
    }

    private void calcularYMostrarMetricas() {
        double totalIngresos = 0.0;
        int totalReservasValidas = 0;
        int totalNoches = 0;
        Map<String, Double> tendenciaMap = new LinkedHashMap<>();
        Map<String, Integer> estadosMap = new HashMap<>();
        Map<String, Integer> habitacionesContador = new HashMap<>();

        for (Habitacion hab : listaHabitaciones) {
            habitacionesContador.put(hab.getTipo(), 0);
        }

        for (Reserva r : listaReservas) {
            if (r.getEstado() == null || r.getEstado().name().equals("CANCELADA")) {
                String est = r.getEstado() != null ? r.getEstado().name() : "DESCONOCIDO";
                estadosMap.put(est, estadosMap.getOrDefault(est, 0) + 1);
                continue;
            }

            double monto = r.getMontoCobrado() > 0 ? r.getMontoCobrado() : r.getMontoTotal();
            totalIngresos += monto;
            totalReservasValidas++;
            totalNoches += r.getNoches();

            // Estado de reservas
            String estadoStr = r.getEstado() != null ? r.getEstado().name() : "CONFIRMADA";
            estadosMap.put(estadoStr, estadosMap.getOrDefault(estadoStr, 0) + 1);

            // Tendencia por mes/fecha
            LocalDate dia = obtenerFechaReserva(r);
            String mesKey = dia.format(DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "ES")));
            tendenciaMap.put(mesKey, tendenciaMap.getOrDefault(mesKey, 0.0) + monto);

            // Desglose por habitación
            String tipoHab = r.getHabitacionTipo() != null ? r.getHabitacionTipo() : "Estándar";
            habitacionesContador.put(tipoHab, habitacionesContador.getOrDefault(tipoHab, 0) + 1);
        }

        // Ocupación
        double ocupacion = (!listaHabitaciones.isEmpty() && totalReservasValidas > 0)
                ? Math.min(100.0, ((double) totalReservasValidas / listaHabitaciones.size()) * 100.0)
                : 0.0;

        // Ticket promedio
        double ticketPromedio = totalReservasValidas > 0 ? totalIngresos / totalReservasValidas : 0.0;

        // Noches promedio
        double nochesPromedio = totalReservasValidas > 0 ? (double) totalNoches / totalReservasValidas : 0.0;

        // Actualizar KPIs
        tvIngresosDetalle.setText(getString(R.string.superadmin_monto_soles, totalIngresos));
        tvOcupacionDetalle.setText(String.format(Locale.US, "%.0f%%", ocupacion));
        tvTicketDetalle.setText(getString(R.string.superadmin_monto_soles, ticketPromedio));
        tvNochesDetalle.setText(String.format(Locale.US, "%.1f", nochesPromedio));

        // Actualizar gráficas
        viewTrendChart.setDatos(tendenciaMap);
        viewStatusChart.setEstados(estadosMap);

        // Actualizar desglose por habitación
        llDesgloseHabitaciones.removeAllViews();
        for (Map.Entry<String, Integer> entry : habitacionesContador.entrySet()) {
            String tipo = entry.getKey();
            int count = entry.getValue();
            int porcentaje = totalReservasValidas > 0 ? (int) Math.round(((double) count / totalReservasValidas) * 100.0) : 0;

            View item = getLayoutInflater().inflate(R.layout.item_desglose_habitacion_reporte, llDesgloseHabitaciones, false);
            TextView tvHabitacionTipo = item.findViewById(R.id.tvHabitacionTipo);
            TextView tvHabitacionPorcentaje = item.findViewById(R.id.tvHabitacionPorcentaje);
            ProgressBar pbHabitacion = item.findViewById(R.id.pbHabitacion);

            tvHabitacionTipo.setText(tipo);
            tvHabitacionPorcentaje.setText(porcentaje + "%");
            pbHabitacion.setProgress(porcentaje);

            llDesgloseHabitaciones.addView(item);
        }
    }

    private LocalDate obtenerFechaReserva(Reserva r) {
        if (r.getFechaCobro() != null) {
            return r.getFechaCobro().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (r.getFechaCreacion() != null) {
            return r.getFechaCreacion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (r.getFechaEntrada() != null) {
            try {
                return LocalDate.parse(r.getFechaEntrada());
            } catch (Exception ignored) {}
        }
        return LocalDate.now();
    }

    private void compartirReporte() {
        if (hotel == null) return;
        String resumen = String.format(Locale.US,
                "Reporte de Hotel: %s\nUbicación: %s\nIngresos: %s\nOcupación: %s\nTicket Promedio: %s",
                hotel.getNombre(), hotel.getUbicacion(),
                tvIngresosDetalle.getText().toString(),
                tvOcupacionDetalle.getText().toString(),
                tvTicketDetalle.getText().toString());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de " + hotel.getNombre());
        intent.putExtra(Intent.EXTRA_TEXT, resumen);
        startActivity(Intent.createChooser(intent, "Compartir reporte vía"));
    }
}
