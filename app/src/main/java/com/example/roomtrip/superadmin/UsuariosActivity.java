package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;
    private ImageButton btnBack;
    private EditText etBuscarUsuario;
    private ChipGroup chipGroupFiltros;

    private List<Usuario> listaCompleta = new ArrayList<>();
    private List<Usuario> listaFiltrada = new ArrayList<>();
    private UsuariosAdapter adapter;
    private String filtroRol = "Todos";
    private String textoBusqueda = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_usuarios);

        rvUsuarios = findViewById(R.id.rvUsuarios);
        btnBack = findViewById(R.id.btnBack);
        etBuscarUsuario = findViewById(R.id.etBuscarUsuario);
        chipGroupFiltros = findViewById(R.id.chipGroupFiltros);

        btnBack.setOnClickListener(v -> finish());

        cargarDatosMock();

        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsuariosAdapter(this, listaFiltrada, usuario -> {
            Intent intent = new Intent(UsuariosActivity.this, DetalleUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        });
        rvUsuarios.setAdapter(adapter);

        setupFiltros();
        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaCompleta = MockData.getUsuariosCompletosEjemplo();

        listaFiltrada.clear();

        listaFiltrada.addAll(listaCompleta);
    }

    private void setupFiltros() {
        etBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusqueda = s.toString().trim().toLowerCase();
                filtrarUsuarios();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                filtroRol = "Todos";
            } else {
                int id = checkedIds.get(0);
                if (id == R.id.chipTodos) {
                    filtroRol = "Todos";
                } else if (id == R.id.chipClientes) {
                    filtroRol = "Cliente";
                } else if (id == R.id.chipAdmins) {
                    filtroRol = "Admin";
                } else if (id == R.id.chipTaxistas) {
                    filtroRol = "Taxista";
                }
            }
            filtrarUsuarios();
        });
    }

    private void filtrarUsuarios() {
        listaFiltrada.clear();
        for (Usuario u : listaCompleta) {
            boolean coincideRol = filtroRol.equals("Todos") || 
                    u.getRole().equalsIgnoreCase(filtroRol) ||
                    (filtroRol.equalsIgnoreCase("Admin") && u.getRole().toLowerCase().contains("admin"));
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                    u.getName().toLowerCase().contains(textoBusqueda) ||
                    u.getEmail().toLowerCase().contains(textoBusqueda);

            if (coincideRol && coincideTexto) {
                listaFiltrada.add(u);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        filtrarUsuarios();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_usuarios);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (id == R.id.nav_hoteles) {
                startActivity(new Intent(this, HotelesActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                return true;
            } else if (id == R.id.nav_auditoria) {
                startActivity(new Intent(this, AuditoriaActivity.class));
                return true;
            }
            return false;
        });
    }
}
