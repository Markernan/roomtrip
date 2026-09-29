package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.LogEvento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class AuditoriaActivity extends AppCompatActivity {

    private RecyclerView rvLogs;
    private ImageButton btnBack;
    private LogAdapter adapter;
    private List<LogEvento> listaLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_auditoria);

        rvLogs = findViewById(R.id.rvLogs);
        btnBack = findViewById(R.id.btnBack);

        rvLogs.setLayoutManager(new LinearLayoutManager(this));

        cargarDatosMock();

        adapter = new LogAdapter(this, listaLogs);
        rvLogs.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaLogs = MockData.getLogsEjemplo();
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
                return true;
            }
            return false;
        });
    }
}
