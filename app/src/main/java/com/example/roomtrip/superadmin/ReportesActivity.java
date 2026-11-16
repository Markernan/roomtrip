package com.example.roomtrip.superadmin;

import android.app.DatePickerDialog;
import android.util.Log;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.EstadoReserva;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.utils.ErroresFirebase;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;
import java.util.Locale;

public class ReportesActivity extends AppCompatActivity {

    private RecyclerView rvReportes;
    private ImageButton btnBack, btnCalendario;
    private Button btnExportarPDF;
    private TextInputEditText etBuscarHotelReporte;
    private Chip chipEsteMes, chipTrimestre, chipAnual;
    private TextView tvIngresosGlobal, tvReservasGlobal, tvHotelTopGlobal, tvFechaFiltroActivo;
    private ReporteHotelAdapter adapter;

    private final List<Hotel> allHoteles = new ArrayList<>();
    private final List<Reserva> allReservas = new ArrayList<>();

    private String periodoActual = "ESTE_MES";
    private LocalDate fechaPersonalizada = null;
    private String queryBusqueda = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_reportes);

        rvReportes = findViewById(R.id.rvReportes);
        btnBack = findViewById(R.id.btnBack);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);
        btnCalendario = findViewById(R.id.btnCalendario);
        etBuscarHotelReporte = findViewById(R.id.etBuscarHotelReporte);
        chipEsteMes = findViewById(R.id.chipEsteMes);
        chipTrimestre = findViewById(R.id.chipTrimestre);
        chipAnual = findViewById(R.id.chipAnual);
        tvIngresosGlobal = findViewById(R.id.tvIngresosGlobal);
        tvReservasGlobal = findViewById(R.id.tvReservasGlobal);
        tvHotelTopGlobal = findViewById(R.id.tvHotelTopGlobal);
        tvFechaFiltroActivo = findViewById(R.id.tvFechaFiltroActivo);

        rvReportes.setLayoutManager(new LinearLayoutManager(this));
        rvReportes.setNestedScrollingEnabled(false);

        adapter = new ReporteHotelAdapter(this, new ArrayList<>(), hotel -> {
            Intent intent = new Intent(ReportesActivity.this, DetalleHotelReporteActivity.class);
            intent.putExtra("hotel", hotel);
            intent.putExtra("PERIODO", periodoActual);
            startActivity(intent);
        });
        rvReportes.setAdapter(adapter);

        cargarDatosFirestore();

        btnBack.setOnClickListener(v -> finish());
        btnExportarPDF.setOnClickListener(v -> 
            Toast.makeText(this, "Generando y exportando reporte PDF...", Toast.LENGTH_SHORT).show()
        );

        chipEsteMes.setOnClickListener(v -> seleccionarPeriodo("ESTE_MES"));
        chipTrimestre.setOnClickListener(v -> seleccionarPeriodo("TRIMESTRE"));
        chipAnual.setOnClickListener(v -> seleccionarPeriodo("ANUAL"));
        btnCalendario.setOnClickListener(v -> mostrarSelectorFecha());

        etBuscarHotelReporte.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryBusqueda = s != null ? s.toString().toLowerCase().trim() : "";
                procesarYMostrarDatos();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        seleccionarPeriodo("ESTE_MES");
        setupBottomNavigation();
    }

    private void cargarDatosFirestore() {
        new HotelRepository().obtenerTodos(hoteles -> {
            allHoteles.clear();
            allHoteles.addAll(hoteles);
            // Mostrar inmediatamente todos los hoteles (con 0 reservas y 0 montos)
            procesarYMostrarDatos();

            new ReservaRepository().obtenerTodas(reservas -> {
                allReservas.clear();
                allReservas.addAll(reservas);
                // Actualizar con datos reales de reservas y cobros
                procesarYMostrarDatos();
            }, e -> {
                Log.w("ReportesActivity", "No se pudieron cargar las reservas, mostrando hoteles sin actividad", e);
                procesarYMostrarDatos();
            });
        }, this::mostrarError);
    }

    private void seleccionarPeriodo(String periodo) {
        periodoActual = periodo;
        fechaPersonalizada = null;

        if (tvFechaFiltroActivo != null) {
            tvFechaFiltroActivo.setVisibility(View.GONE);
        }

        resetearChips();
        int colorBlanco = ContextCompat.getColor(this, R.color.blanco);

        switch (periodo) {
            case "ESTE_MES":
                chipEsteMes.setChipBackgroundColorResource(R.color.turquesa);
                chipEsteMes.setTextColor(colorBlanco);
                chipEsteMes.setChipStrokeColorResource(R.color.turquesa);
                break;
            case "TRIMESTRE":
                chipTrimestre.setChipBackgroundColorResource(R.color.turquesa);
                chipTrimestre.setTextColor(colorBlanco);
                chipTrimestre.setChipStrokeColorResource(R.color.turquesa);
                break;
            case "ANUAL":
                chipAnual.setChipBackgroundColorResource(R.color.turquesa);
                chipAnual.setTextColor(colorBlanco);
                chipAnual.setChipStrokeColorResource(R.color.turquesa);
                break;
        }
        procesarYMostrarDatos();
    }

    private void resetearChips() {
        int colorTexto = ContextCompat.getColor(this, R.color.texto_principal);

        chipEsteMes.setChipBackgroundColorResource(R.color.blanco);
        chipEsteMes.setTextColor(colorTexto);
        chipEsteMes.setChipStrokeColorResource(R.color.texto_secundario);

        chipTrimestre.setChipBackgroundColorResource(R.color.blanco);
        chipTrimestre.setTextColor(colorTexto);
        chipTrimestre.setChipStrokeColorResource(R.color.texto_secundario);

        chipAnual.setChipBackgroundColorResource(R.color.blanco);
        chipAnual.setTextColor(colorTexto);
        chipAnual.setChipStrokeColorResource(R.color.texto_secundario);
    }

    private void mostrarSelectorFecha() {
        LocalDate hoy = LocalDate.now();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fechaPersonalizada = LocalDate.of(year, month + 1, dayOfMonth);
            periodoActual = "PERSONALIZADO";
            resetearChips();
            String fechaFmt = String.format(Locale.US, "%02d/%02d/%d", fechaPersonalizada.getDayOfMonth(), fechaPersonalizada.getMonthValue(), fechaPersonalizada.getYear());
            if (tvFechaFiltroActivo != null) {
                tvFechaFiltroActivo.setText("Filtrando por fecha: " + fechaFmt);
                tvFechaFiltroActivo.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, "Filtrando por fecha: " + fechaFmt, Toast.LENGTH_SHORT).show();
            procesarYMostrarDatos();
        }, hoy.getYear(), hoy.getMonthValue() - 1, hoy.getDayOfMonth());
        dialog.show();
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

    private boolean cumpleFiltroFecha(LocalDate dia) {
        LocalDate hoy = LocalDate.now();
        switch (periodoActual) {
            case "ESTE_MES":
                return dia.getYear() == hoy.getYear() && dia.getMonth() == hoy.getMonth();
            case "TRIMESTRE":
                return !dia.isBefore(hoy.minusMonths(3)) && !dia.isAfter(hoy);
            case "ANUAL":
                return dia.getYear() == hoy.getYear();
            case "PERSONALIZADO":
                return fechaPersonalizada != null && dia.equals(fechaPersonalizada);
            default:
                return true;
        }
    }

    private void procesarYMostrarDatos() {
        if (allHoteles.isEmpty()) return;

        Map<String, Hotel> porId = new HashMap<>();
        for (Hotel h : allHoteles) {
            h.setReservasReporte(0);
            h.setMontoReporte(0.0);
            porId.put(h.getId(), h);
        }

        int totalReservasGlobal = 0;
        double totalIngresosGlobal = 0.0;

        for (Reserva r : allReservas) {
            if (r.getEstado() == EstadoReserva.CANCELADA) continue;
            LocalDate dia = obtenerFechaReserva(r);
            if (!cumpleFiltroFecha(dia)) continue;

            Hotel h = porId.get(r.getHotelId());
            if (h == null) continue;

            double monto = r.getMontoCobrado() > 0 ? r.getMontoCobrado() : r.getMontoTotal();
            h.setReservasReporte(h.getReservasReporte() + 1);
            h.setMontoReporte(h.getMontoReporte() + monto);

            totalReservasGlobal++;
            totalIngresosGlobal += monto;
        }

        // Ordenar todos los hoteles por monto descendente (los de 0 quedan al final)
        allHoteles.sort((a, b) -> Double.compare(b.getMontoReporte(), a.getMontoReporte()));

        Hotel hotelTop = (!allHoteles.isEmpty() && allHoteles.get(0).getMontoReporte() > 0) ? allHoteles.get(0) : null;
        double maxMonto = hotelTop != null ? hotelTop.getMontoReporte() : 0.0;

        // Actualizar KPIs superiores
        tvIngresosGlobal.setText(String.format(Locale.US, "S/ %.2f", totalIngresosGlobal));
        tvReservasGlobal.setText(String.valueOf(totalReservasGlobal));
        tvHotelTopGlobal.setText(hotelTop != null ? hotelTop.getNombre() : "Ninguno");

        // Mostrar TODOS los hoteles registrados (filtrando únicamente por la barra de búsqueda si tiene texto)
        List<Hotel> filtrados = new ArrayList<>();
        for (Hotel h : allHoteles) {
            boolean coincide = queryBusqueda.isEmpty() ||
                    (h.getNombre() != null && h.getNombre().toLowerCase().contains(queryBusqueda)) ||
                    (h.getUbicacion() != null && h.getUbicacion().toLowerCase().contains(queryBusqueda));
            if (coincide) {
                filtrados.add(h);
            }
        }

        adapter.actualizarDatos(filtrados, maxMonto, totalIngresosGlobal);
    }

    private void mostrarError(Exception e) {
        Log.e("ReportesActivity", "Error de Firestore", e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_auditoria);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_hoteles) {
                startActivity(new Intent(this, HotelesActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, UsuariosActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_auditoria) {
                startActivity(new Intent(this, AuditoriaActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
