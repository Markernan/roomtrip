package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.Usuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    private static final String TAG = "UsuariosActivity";
    private final UsuarioRepository repositorio = new UsuarioRepository();

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

        // Cargar filtro guardado en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SuperadminPrefs", MODE_PRIVATE);
        filtroRol = prefs.getString("ultimo_filtro_rol", "Todos");
        actualizarChipSeleccionado();

        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsuariosAdapter(this, listaFiltrada, usuario -> {
            Intent intent = new Intent(UsuariosActivity.this, DetalleUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        });
        adapter.setOnEstadoCambiadoListener(this::cambiarEstado);
        rvUsuarios.setAdapter(adapter);

        setupFiltros();
        setupBottomNavigation();
    }

    private void actualizarChipSeleccionado() {
        if (chipGroupFiltros == null) return;
        switch (filtroRol) {
            case "Cliente":
                chipGroupFiltros.check(R.id.chipClientes);
                break;
            case "Admin":
                chipGroupFiltros.check(R.id.chipAdmins);
                break;
            case "Taxista":
                chipGroupFiltros.check(R.id.chipTaxistas);
                break;
            case "Todos":
            default:
                chipGroupFiltros.check(R.id.chipTodos);
                break;
        }
    }

    /** Lab 6: todos los usuarios de Firestore (solo el Superadmin puede leerlos, ver firestore.rules). */
    private void cargarUsuarios() {
        repositorio.obtenerTodos(perfiles -> {
            listaCompleta = new ArrayList<>();
            for (PerfilUsuario p : perfiles) listaCompleta.add(UsuarioVista.desde(p));
            filtrarUsuarios();
        }, e -> {
            Log.e(TAG, "No se pudieron leer los usuarios", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    /** RF-USR-007: activar o desactivar la cuenta desde el switch de la lista. */
    private void cambiarEstado(Usuario usuario, boolean activo) {
        if (usuario.getUid() == null) return;
        if (usuario.getUid().equals(SesionFirebase.uidActual())) {
            Toast.makeText(this, R.string.superadmin_no_desactivarse, Toast.LENGTH_SHORT).show();
            cargarUsuarios();
            return;
        }
        repositorio.cambiarEstado(usuario.getUid(), EstadoCuenta.desde(activo), ignorado -> {
            RegistroEventos.registrar(RegistroEventos.USUARIO_ESTADO, RolUsuario.SUPERADMIN,
                    usuario.getName() + " " + (activo ? "activado" : "desactivado"));
            Toast.makeText(this, getString(activo ? R.string.superadmin_usuario_activado
                    : R.string.superadmin_usuario_desactivado, usuario.getName()), Toast.LENGTH_SHORT).show();
        }, e -> {
            Log.e(TAG, "No se pudo cambiar el estado de " + usuario.getUid(), e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
            cargarUsuarios();
        });
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

            // Guardar preferencia en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("SuperadminPrefs", MODE_PRIVATE);
            prefs.edit().putString("ultimo_filtro_rol", filtroRol).apply();

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
        cargarUsuarios();   // al volver del detalle puede haber cambios
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
