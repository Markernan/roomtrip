package com.example.roomtrip.superadmin;

import android.util.Log;
import android.widget.Toast;
import com.example.roomtrip.data.firebase.LogRepository;
import com.example.roomtrip.utils.ErroresFirebase;
import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.LogEvento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class AuditoriaActivity extends AppCompatActivity {

    private RecyclerView rvLogs;
    private ImageButton btnBack;
    private LogAdapter adapter;
    private final List<LogEvento> listaLogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_auditoria);

        rvLogs = findViewById(R.id.rvLogs);
        btnBack = findViewById(R.id.btnBack);

        rvLogs.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LogAdapter(this, listaLogs);
        rvLogs.setAdapter(adapter);
        cargarDatosMock();

        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    /** Lab 6: eventos de la colección logs (los registra cada rol con RegistroEventos). */
    private void cargarDatosMock() {
        new LogRepository().obtenerRecientes(logs -> {
            listaLogs.clear();
            listaLogs.addAll(logs);
            adapter.notifyDataSetChanged();
            if (logs.isEmpty()) {
                Toast.makeText(this, R.string.superadmin_sin_logs, Toast.LENGTH_SHORT).show();
            }
        }, e -> {
            Log.e("AuditoriaActivity", "No se pudieron leer los logs", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
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
