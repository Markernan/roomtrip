package com.example.roomtrip.superadmin;

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
        btnExportarPDF.setOnClickListener(v -> 
            Toast.makeText(this, "Generando y exportando reporte PDF...", Toast.LENGTH_SHORT).show()
        );

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaHoteles = new ArrayList<>();
        listaHoteles.add(new Hotel("Hotel Marriott Lima", "Miraflores, Lima", 4.8f, "Admin Marriott", "admin@marriott.com", "+51 987 111 222", R.drawable.foto_hotel_miraflores, true));
        listaHoteles.add(new Hotel("Gran Hotel Bolivar", "Centro de Lima, Lima", 4.5f, "Admin Bolivar", "admin@bolivar.com", "+51 987 333 444", R.drawable.hotel_italia_fachada_calle, true));
        listaHoteles.add(new Hotel("Hotel Palacio del Inka", "Cusco", 4.9f, "Admin Palacio", "admin@palacio.com", "+51 987 555 666", R.drawable.hotel_habitacion_elegante, true));
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