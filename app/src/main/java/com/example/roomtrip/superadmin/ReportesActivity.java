package com.example.roomtrip.superadmin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Hotel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

        adapter = new ReporteHotelAdapter(this, listaHoteles, hotel -> {
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
        listaHoteles = cargarReporteDesdeCache();
        if (listaHoteles == null || listaHoteles.isEmpty()) {
            listaHoteles = MockData.getReporteHotelesEjemplo();
            guardarReporteEnCache(listaHoteles);
        }
    }

    private void guardarReporteEnCache(List<Hotel> hoteles) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(hoteles);
            FileOutputStream fos = openFileOutput("reporte_hoteles_cache.json", Context.MODE_PRIVATE);
            FileWriter writer = new FileWriter(fos.getFD());
            writer.write(json);
            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Hotel> cargarReporteDesdeCache() {
        List<Hotel> hoteles = null;
        try {
            FileInputStream fis = openFileInput("reporte_hoteles_cache.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type type = new TypeToken<List<Hotel>>(){}.getType();
            hoteles = gson.fromJson(reader, type);
            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hoteles;
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
