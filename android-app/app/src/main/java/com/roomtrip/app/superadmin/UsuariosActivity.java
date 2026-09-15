package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roomtrip.app.R;
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
        adapter = new UsuariosAdapter(listaFiltrada, usuario -> {
            Intent intent = new Intent(UsuariosActivity.this, DetalleUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        });
        rvUsuarios.setAdapter(adapter);

        setupFiltros();
        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        // Taxistas
        Usuario t1 = new Usuario("Carlos Mendoza", "carlos.mendoza@roomtrip.com", "Taxista", "+51 987 654 321", true);
        t1.setLicencia("A-I-77482");
        t1.setPlacaAuto("ABC-123");
        t1.setModeloVehiculo("Toyota Corolla 2022");
        listaCompleta.add(t1);

        Usuario t2 = new Usuario("Marcos López", "marcos.lopez@roomtrip.com", "Taxista", "+51 912 345 678", false);
        t2.setLicencia("A-IIb-99321");
        t2.setPlacaAuto("XYZ-789");
        t2.setModeloVehiculo("Nissan Sentra 2021");
        listaCompleta.add(t2);

        // Clientes
        Usuario c1 = new Usuario("Ana García", "ana.garcia@gmail.com", "Cliente", "+51 955 443 322", true);
        c1.setDireccion("Av. Larco 456, Miraflores, Lima");
        c1.setHistorialReservas("3 reservaciones completadas, 1 cancelada");
        listaCompleta.add(c1);

        Usuario c2 = new Usuario("Sofía Benítez", "sofia.b@outlook.com", "Cliente", "+51 933 221 100", true);
        c2.setDireccion("Calle San Martín 789, Arequipa");
        c2.setHistorialReservas("5 reservaciones completadas");
        listaCompleta.add(c2);

        // Admin Hoteles
        Usuario a1 = new Usuario("Luis Torres", "luis.torres@grandhotel.com", "Admin Hotel", "+51 966 778 899", true);
        a1.setHotelAsignado("Grand Hotel RoomTrip");
        listaCompleta.add(a1);

        Usuario a2 = new Usuario("Javier Ramírez", "jramirez@hotelparadise.com", "Admin Hotel", "+51 944 556 677", true);
        a2.setHotelAsignado("Hotel Paradise Costero");
        listaCompleta.add(a2);

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
                    filtroRol = "Admin Hotel";
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
            boolean coincideRol = filtroRol.equals("Todos") || u.getRole().equalsIgnoreCase(filtroRol);
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