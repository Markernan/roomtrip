package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roomtrip.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class ReportesActivity extends AppCompatActivity {

    private RecyclerView rvReportes;
    private ImageButton btnBack;
    private Button btnExportarPDF;
    private ReporteHotelAdapter adapter;
    private List<Hotel> listaHoteles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_reportes);

        rvReportes = findViewById(R.id.rvReportes);
        btnBack = findViewById(R.id.btnBack);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);

        rvReportes.setLayoutManager(new LinearLayoutManager(this));

        cargarDatosMock();

        adapter = new ReporteHotelAdapter(listaHoteles, hotel -> {
            Intent intent = new Intent(ReportesActivity.this, DetalleHotelReporteActivity.class);
            intent.putExtra("hotel", hotel);
            startActivity(intent);
        });
        rvReportes.setAdapter(adapter);
        
        btnBack.setOnClickListener(v -> finish());
        btnExportarPDF.setOnClickListener(v -> {
            Toast.makeText(this, "Exportando reporte global a PDF...", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaHoteles = new ArrayList<>();
        listaHoteles.add(new Hotel("Hotel Miraflores", "Miraflores, Lima", 4.7f, "Roberto Gómez", "roberto@miraflores.com", "+51 987 654 321", R.drawable.foto_hotel_miraflores, true));
        listaHoteles.add(new Hotel("Gran Hotel Lima", "Centro de Lima, Lima", 4.5f, "María Mendoza", "maria@granhotellima.com", "+51 912 345 678", R.drawable.ic_home_pin, true));
        listaHoteles.add(new Hotel("Hotel Cusco Plaza", "Plaza de Armas, Cusco", 4.9f, "Carlos Inca", "carlos@cuscoplaza.com", "+51 965 432 198", R.drawable.ic_home_pin, true));
        listaHoteles.add(new Hotel("Resort Paracas", "Bahía de Paracas, Ica", 4.6f, "Elena Mar", "elena@resortparacas.com", "+51 954 123 789", R.drawable.ic_home_pin, false));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_auditoria);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (id == R.id.nav_hoteles) {
                startActivity(new Intent(this, HotelesActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, UsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_auditoria) {
                startActivity(new Intent(this, AuditoriaActivity.class));
                return true;
            }
            return false;
        });
    }
}