package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class TaxistasActivity extends AppCompatActivity {

    private RecyclerView rvTaxistas;
    private ImageButton btnBack;
    private Button btnSolicitudes;
    private UsuariosAdapter adapter;
    private List<Usuario> listaTaxistas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_taxistas);

        rvTaxistas = findViewById(R.id.rvTaxistas);
        btnBack = findViewById(R.id.btnBack);
        btnSolicitudes = findViewById(R.id.btnSolicitudes);

        rvTaxistas.setLayoutManager(new LinearLayoutManager(this));

        cargarDatosMock();

        adapter = new UsuariosAdapter(this, listaTaxistas, usuario -> {
            Intent intent = new Intent(TaxistasActivity.this, DetalleUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        });
        rvTaxistas.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaTaxistas = MockData.getTaxistasEjemplo();
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
