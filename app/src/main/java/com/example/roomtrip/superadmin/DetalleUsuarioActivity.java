package com.example.roomtrip.superadmin;

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
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetalleUsuarioActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvNombre, tvEmail, tvRol, tvTelefono;
    private LinearLayout layoutTaxista, layoutAdminHotel, layoutCliente;
    private TextView tvLicencia, tvPlacaAuto, tvModeloVehiculo, tvHotelAsignado, tvDireccion, tvHistorialReservas;
    private SwitchCompat switchEstado;
    private Button btnGuardarCambios, btnResetPassword, btnEliminarUsuario;
    private BottomNavigationView bottomNav;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_detalle_usuario);

        btnBack = findViewById(R.id.btnBack);
        tvNombre = findViewById(R.id.tvNombre);
        tvEmail = findViewById(R.id.tvEmail);
        tvRol = findViewById(R.id.tvRol);
        tvTelefono = findViewById(R.id.tvTelefono);

        layoutTaxista = findViewById(R.id.layoutTaxista);
        layoutAdminHotel = findViewById(R.id.layoutAdminHotel);
        layoutCliente = findViewById(R.id.layoutCliente);

        tvLicencia = findViewById(R.id.tvLicencia);
        tvPlacaAuto = findViewById(R.id.tvPlacaAuto);
        tvModeloVehiculo = findViewById(R.id.tvModeloVehiculo);
        tvHotelAsignado = findViewById(R.id.tvHotelAsignado);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvHistorialReservas = findViewById(R.id.tvHistorialReservas);

        switchEstado = findViewById(R.id.switchEstado);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);
        bottomNav = findViewById(R.id.bottomNav);

        btnBack.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("usuario")) {
            usuario = (Usuario) getIntent().getSerializableExtra("usuario");
        }

        if (usuario != null) {
            tvNombre.setText(usuario.getName());
            tvEmail.setText(usuario.getEmail());
            tvRol.setText(usuario.getRole());
            tvTelefono.setText(usuario.getPhone());
            switchEstado.setChecked(usuario.isActive());

            configurarSeccionRol(usuario.getRole());
        }

        btnGuardarCambios.setOnClickListener(v -> {
            if (usuario != null) {
                usuario.setActive(switchEstado.isChecked());
                Toast.makeText(this, "Estado del usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnResetPassword.setOnClickListener(v -> 
            Toast.makeText(this, "Enlace para restablecer contraseña enviado al correo", Toast.LENGTH_SHORT).show()
        );

        btnEliminarUsuario.setOnClickListener(v -> {
            Toast.makeText(this, "Usuario eliminado del sistema", Toast.LENGTH_SHORT).show();
            finish();
        });

        setupBottomNavigation();
    }

    private void configurarSeccionRol(String rol) {
        layoutTaxista.setVisibility(View.GONE);
        layoutAdminHotel.setVisibility(View.GONE);
        layoutCliente.setVisibility(View.GONE);

        switch (rol.toLowerCase()) {
            case "taxista":
                layoutTaxista.setVisibility(View.VISIBLE);
                tvLicencia.setText(usuario.getLicencia() != null ? usuario.getLicencia() : "A-I-88492");
                tvPlacaAuto.setText(usuario.getPlacaAuto() != null ? usuario.getPlacaAuto() : "ABC-123");
                tvModeloVehiculo.setText(usuario.getModeloVehiculo() != null ? usuario.getModeloVehiculo() : "Toyota Yaris 2021");
                break;

            case "admin":
            case "admin hotel":
                layoutAdminHotel.setVisibility(View.VISIBLE);
                tvHotelAsignado.setText(usuario.getHotelAsignado() != null ? usuario.getHotelAsignado() : "Hotel Marriott Lima");
                break;

            case "cliente":
            default:
                layoutCliente.setVisibility(View.VISIBLE);
                tvDireccion.setText(usuario.getDireccion() != null ? usuario.getDireccion() : "Av. Primavera 123, Surco");
                tvHistorialReservas.setText(usuario.getHistorialReservas() != null ? usuario.getHistorialReservas() : "4 reservaciones completadas");
                break;
        }
    }

    private void setupBottomNavigation() {
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