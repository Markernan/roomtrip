package com.example.roomtrip.superadmin;

import android.util.Log;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.EstadoReserva;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.utils.ErroresFirebase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ReportesActivity extends AppCompatActivity {

    private RecyclerView rvReportes;
    private ImageButton btnBack;
    private Button btnExportarPDF;
    private ReporteHotelAdapter adapter;
    private final List<Hotel> listaHoteles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_reportes);

        rvReportes = findViewById(R.id.rvReportes);
        btnBack = findViewById(R.id.btnBack);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);

        rvReportes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReporteHotelAdapter(this, listaHoteles, hotel -> {
            Intent intent = new Intent(ReportesActivity.this, DetalleHotelReporteActivity.class);
            intent.putExtra("hotel", hotel);
            startActivity(intent);
        });
        rvReportes.setAdapter(adapter);
        cargarDatosMock();

        btnBack.setOnClickListener(v -> finish());
        btnExportarPDF.setOnClickListener(v -> 
            Toast.makeText(this, "Generando y exportando reporte PDF...", Toast.LENGTH_SHORT).show()
        );

        setupBottomNavigation();
    }

    /** Lab 6: reporte de reservas por hotel (cantidad y monto) calculado con los datos de Firestore. */
    private void cargarDatosMock() {
        new HotelRepository().obtenerTodos(hoteles -> new ReservaRepository().obtenerTodas(reservas -> {
            Map<String, Hotel> porId = new HashMap<>();
            for (Hotel h : hoteles) {
                h.setReservasReporte(0);
                h.setMontoReporte(0);
                porId.put(h.getId(), h);
            }
            for (Reserva r : reservas) {
                Hotel h = porId.get(r.getHotelId());
                if (h == null || r.getEstado() == EstadoReserva.CANCELADA) continue;
                h.setReservasReporte(h.getReservasReporte() + 1);
                h.setMontoReporte(h.getMontoReporte() + (r.getMontoCobrado() > 0 ? r.getMontoCobrado() : r.getMontoTotal()));
            }
            hoteles.sort((a, b) -> Double.compare(b.getMontoReporte(), a.getMontoReporte()));
            listaHoteles.clear();
            listaHoteles.addAll(hoteles);
            adapter.notifyDataSetChanged();
        }, this::mostrarError), this::mostrarError);
    }

    private void mostrarError(Exception e) {
        Log.e("ReportesActivity", "Error de Firestore", e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
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
