package com.example.roomtrip.superadmin;

import android.util.Log;
import android.widget.Toast;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.EstadoAprobacion;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import java.util.ArrayList;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class TaxistasActivity extends AppCompatActivity {

    private RecyclerView rvTaxistas;
    private ImageButton btnBack;
    private MaterialButton btnIrPanelWeb;
    private UsuariosAdapter adapter;
    private final List<Usuario> listaTaxistas = new ArrayList<>();
    private final UsuarioRepository repositorio = new UsuarioRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_taxistas);

        rvTaxistas = findViewById(R.id.rvTaxistas);
        btnBack = findViewById(R.id.btnBack);
        btnIrPanelWeb = findViewById(R.id.btnIrPanelWeb);

        rvTaxistas.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsuariosAdapter(this, listaTaxistas, usuario -> {
            Intent intent = new Intent(TaxistasActivity.this, DetalleUsuarioActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        });
        adapter.setOnEstadoCambiadoListener((usuario, activo) ->
                repositorio.cambiarEstado(usuario.getUid(), EstadoCuenta.desde(activo), ignorado -> {
                    RegistroEventos.registrar(RegistroEventos.USUARIO_ESTADO, RolUsuario.SUPERADMIN,
                            usuario.getName() + " " + (activo ? "activado" : "desactivado"));
                }, this::mostrarError));
        rvTaxistas.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        
        if (btnIrPanelWeb != null) {
            btnIrPanelWeb.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://roomtrip-taxistas.web.app/pendientes"));
                startActivity(intent);
            });
        }

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTaxistas();
    }

    /** Lab 6: taxistas de Firestore; las solicitudes PENDIENTES primero para aprobarlas rápido. */
    private void cargarTaxistas() {
        repositorio.obtenerTodos(perfiles -> {
            listaTaxistas.clear();
            for (PerfilUsuario p : perfiles) {
                if (p.getRol() == RolUsuario.TAXISTA) listaTaxistas.add(UsuarioVista.desde(p));
            }
            listaTaxistas.sort((a, b) -> Boolean.compare(!EstadoAprobacion.PENDIENTE.name().equals(a.getEstadoAprobacion()),
                    !EstadoAprobacion.PENDIENTE.name().equals(b.getEstadoAprobacion())));
            adapter.notifyDataSetChanged();
        }, this::mostrarError);
    }

    private void mostrarError(Exception e) {
        Log.e("TaxistasActivity", "Error de Firestore", e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
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
