package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.roomtrip.R;
import com.example.roomtrip.utils.PerfilDialogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private CardView cardHotelesRegistrados, cardUsuariosTotales, cardHotelesActivos, cardTaxistasAprobar;
    private CardView cardGestionHoteles, cardGestionUsuarios, cardReporteGlobal, cardAuditoria;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_dashboard);

        // Panel de control y Gestión
        cardHotelesRegistrados = findViewById(R.id.cardHotelesRegistrados);
        cardUsuariosTotales = findViewById(R.id.cardUsuariosTotales);
        cardHotelesActivos = findViewById(R.id.cardHotelesActivos);
        cardTaxistasAprobar = findViewById(R.id.cardTaxistasAprobar);

        // Accesos rápidos
        cardGestionHoteles = findViewById(R.id.cardGestionHoteles);
        cardGestionUsuarios = findViewById(R.id.cardGestionUsuarios);
        cardReporteGlobal = findViewById(R.id.cardReporteGlobal);
        cardAuditoria = findViewById(R.id.cardAuditoria);

        bottomNav = findViewById(R.id.bottomNav);

        setupClickListeners();
        setupBottomNavigation();
    }

    private void setupClickListeners() {
        View cardPerfilSuperAdmin = findViewById(R.id.cardPerfilSuperAdmin);
        if (cardPerfilSuperAdmin != null) {
            cardPerfilSuperAdmin.setOnClickListener(v -> {
                PerfilDialogHelper.mostrarDialogoPerfil(this, "SuperAdmin", "SuperAdmin", nuevoNombre -> {
                    TextView tvSaludo = findViewById(R.id.tvSaludoSuperAdmin);
                    if (tvSaludo != null) {
                        tvSaludo.setText("Hola, " + nuevoNombre);
                    }
                });
            });
        }

        // Redirecciones Panel
        cardHotelesRegistrados.setOnClickListener(v -> startActivity(new Intent(this, HotelesActivity.class)));
        cardUsuariosTotales.setOnClickListener(v -> startActivity(new Intent(this, UsuariosActivity.class)));
        cardHotelesActivos.setOnClickListener(v -> startActivity(new Intent(this, HotelesActivity.class)));
        cardTaxistasAprobar.setOnClickListener(v -> startActivity(new Intent(this, TaxistasActivity.class)));

        // Redirecciones Accesos rápidos
        cardGestionHoteles.setOnClickListener(v -> startActivity(new Intent(this, HotelesActivity.class)));
        cardGestionUsuarios.setOnClickListener(v -> startActivity(new Intent(this, UsuariosActivity.class)));
        cardReporteGlobal.setOnClickListener(v -> startActivity(new Intent(this, ReportesActivity.class)));
        cardAuditoria.setOnClickListener(v -> startActivity(new Intent(this, AuditoriaActivity.class)));
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
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