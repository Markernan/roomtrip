package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.roomtrip.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetalleUsuarioActivity extends AppCompatActivity {

    private TextView tvNombre, tvEmail, tvRol, tvTelefono;
    private SwitchCompat switchEstado;
    private Button btnGuardarCambios, btnResetPassword, btnEliminarUsuario;
    private ImageButton btnBack;
    private Usuario usuario;

    // Campos de la sección dinámica
    private LinearLayout layoutTaxista, layoutAdminHotel, layoutCliente;
    private TextView tvLicencia, tvPlacaAuto, tvModeloVehiculo;
    private TextView tvHotelAsignado;
    private TextView tvDireccion, tvHistorialReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_detalle_usuario);

        tvNombre = findViewById(R.id.tvNombre);
        tvEmail = findViewById(R.id.tvEmail);
        tvRol = findViewById(R.id.tvRol);
        tvTelefono = findViewById(R.id.tvTelefono);
        switchEstado = findViewById(R.id.switchEstado);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);
        btnBack = findViewById(R.id.btnBack);

        // Inicializar vistas de secciones dinámicas
        layoutTaxista = findViewById(R.id.layoutTaxista);
        layoutAdminHotel = findViewById(R.id.layoutAdminHotel);
        layoutCliente = findViewById(R.id.layoutCliente);

        tvLicencia = findViewById(R.id.tvLicencia);
        tvPlacaAuto = findViewById(R.id.tvPlacaAuto);
        tvModeloVehiculo = findViewById(R.id.tvModeloVehiculo);

        tvHotelAsignado = findViewById(R.id.tvHotelAsignado);

        tvDireccion = findViewById(R.id.tvDireccion);
        tvHistorialReservas = findViewById(R.id.tvHistorialReservas);

        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        if (usuario != null) {
            tvNombre.setText(usuario.getName());
            tvEmail.setText(usuario.getEmail());
            tvRol.setText(usuario.getRole());
            tvTelefono.setText(usuario.getPhone());
            switchEstado.setChecked(usuario.isActive());

            // Configurar visibilidad y poblar campos según el rol
            String role = usuario.getRole() != null ? usuario.getRole() : "";
            if (role.equalsIgnoreCase("Taxista")) {
                layoutTaxista.setVisibility(View.VISIBLE);
                tvLicencia.setText(usuario.getLicencia());
                tvPlacaAuto.setText(usuario.getPlacaAuto());
                tvModeloVehiculo.setText(usuario.getModeloVehiculo());
            } else if (role.equalsIgnoreCase("Admin Hotel")) {
                layoutAdminHotel.setVisibility(View.VISIBLE);
                tvHotelAsignado.setText(usuario.getHotelAsignado());
            } else if (role.equalsIgnoreCase("Cliente")) {
                layoutCliente.setVisibility(View.VISIBLE);
                tvDireccion.setText(usuario.getDireccion());
                tvHistorialReservas.setText(usuario.getHistorialReservas());
            }
        }

        btnBack.setOnClickListener(v -> finish());

        btnGuardarCambios.setOnClickListener(v -> {
            if (usuario != null) {
                usuario.setActive(switchEstado.isChecked());
            }
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnResetPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Enlace de restablecimiento enviado", Toast.LENGTH_SHORT).show();
        });

        btnEliminarUsuario.setOnClickListener(v -> {
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            finish();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_usuarios);
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