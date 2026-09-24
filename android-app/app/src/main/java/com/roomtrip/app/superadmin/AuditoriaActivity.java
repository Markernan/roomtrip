package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roomtrip.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
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

        adapter = new LogAdapter(listaLogs);
        rvLogs.setAdapter(adapter);
        
        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaLogs = new ArrayList<>();
        listaLogs.add(new LogEvento("Superadmin activó al usuario cliente \"Ana Gómez\"", "31/08/2026", "14:32:05 hrs", "IP: 190.45.12.34", R.drawable.ic_person));
        listaLogs.add(new LogEvento("Inicio de sesión exitoso - Usuario: Carlos Ruiz (Administrador)", "31/08/2026", "14:15:42 hrs", "Dispositivo: Web", R.drawable.ic_lock));
        listaLogs.add(new LogEvento("Checkout completado en Hotel Miraflores - Reserva #1042", "31/08/2026", "13:47:18 hrs", "Dispositivo: Android 14", R.drawable.ic_card));
        listaLogs.add(new LogEvento("Hotel \"Gran Hotel Lima\" modificado por Ana García", "31/08/2026", "13:22:11 hrs", "IP: 190.45.12.34", R.drawable.ic_home_pin));
        listaLogs.add(new LogEvento("Error al procesar pago - Reserva #1038 (Tarjeta rechazada)", "31/08/2026", "12:58:33 hrs", "Dispositivo: Android 14", R.drawable.ic_warning));
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