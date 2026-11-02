package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.EstadoAprobacion;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.Usuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Detalle de una cuenta para el Superadmin (Lab 6, Firestore):
 * activar o desactivar (RF-USR-007), aprobar o rechazar taxistas (RF-WTX-004) y enviar el correo de
 * restablecimiento de contraseña (Firebase Authentication).
 */
public class DetalleUsuarioActivity extends AppCompatActivity {

    private static final String TAG = "DetalleUsuarioActivity";

    private ImageButton btnBack;
    private TextView tvNombre, tvEmail, tvRol, tvTelefono;
    private LinearLayout layoutTaxista, layoutAdminHotel, layoutCliente;
    private TextView tvLicencia, tvPlacaAuto, tvModeloVehiculo, tvHotelAsignado, tvDireccion, tvHistorialReservas;
    private TextView tvAprobacionTaxista;
    private SwitchCompat switchEstado;
    private Button btnGuardarCambios, btnResetPassword, btnEliminarUsuario, btnAprobarTaxista, btnRechazarTaxista;
    private BottomNavigationView bottomNav;

    private final UsuarioRepository repositorio = new UsuarioRepository();
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;
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
        tvAprobacionTaxista = findViewById(R.id.tvAprobacionTaxista);

        switchEstado = findViewById(R.id.switchEstado);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario);
        btnAprobarTaxista = findViewById(R.id.btnAprobarTaxista);
        btnRechazarTaxista = findViewById(R.id.btnRechazarTaxista);
        bottomNav = findViewById(R.id.bottomNav);

        btnBack.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("usuario")) {
            usuario = (Usuario) getIntent().getSerializableExtra("usuario");
        }

        if (usuario != null) {
            tvNombre.setText(usuario.getName());
            tvEmail.setText(usuario.getEmail());
            tvRol.setText(usuario.getRole());
            tvTelefono.setText(textoODash(usuario.getPhone()));
            switchEstado.setChecked(usuario.isActive());

            configurarSeccionRol(usuario.getRole());
        }

        // Guardar: activa o desactiva la cuenta en Firestore
        btnGuardarCambios.setOnClickListener(v -> {
            if (usuario == null || usuario.getUid() == null) return;
            boolean activo = switchEstado.isChecked();
            cambiarEstado(activo, getString(R.string.superadmin_estado_guardado));
        });

        // Firebase Authentication envía el enlace para elegir una contraseña nueva
        btnResetPassword.setOnClickListener(v -> {
            if (usuario == null || usuario.getEmail() == null) return;
            FirebaseAuth.getInstance().setLanguageCode("es-419");
            FirebaseAuth.getInstance().sendPasswordResetEmail(usuario.getEmail())
                    .addOnSuccessListener(unused -> Toast.makeText(this,
                            getString(R.string.superadmin_reset_enviado, usuario.getEmail()), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(this::mostrarError);
        });

        // Las cuentas no se borran (se conserva su historial, RN-015): se desactivan
        btnEliminarUsuario.setText(R.string.superadmin_desactivar_cuenta);
        btnEliminarUsuario.setOnClickListener(v -> {
            if (usuario == null || usuario.getUid() == null) return;
            switchEstado.setChecked(false);
            cambiarEstado(false, getString(R.string.superadmin_cuenta_desactivada));
        });

        btnAprobarTaxista.setOnClickListener(v -> cambiarAprobacion(EstadoAprobacion.APROBADO));
        btnRechazarTaxista.setOnClickListener(v -> cambiarAprobacion(EstadoAprobacion.RECHAZADO));

        setupBottomNavigation();
    }

    private void cambiarEstado(boolean activo, String mensajeExito) {
        if (usuario.getUid().equals(SesionFirebase.uidActual()) && !activo) {
            Toast.makeText(this, R.string.superadmin_no_desactivarse, Toast.LENGTH_SHORT).show();
            switchEstado.setChecked(true);
            return;
        }
        btnGuardarCambios.setEnabled(false);
        repositorio.cambiarEstado(usuario.getUid(), EstadoCuenta.desde(activo), ignorado -> {
            usuario.setActive(activo);
            RegistroEventos.registrar(RegistroEventos.USUARIO_ESTADO, RolUsuario.SUPERADMIN,
                    usuario.getName() + " " + (activo ? "activado" : "desactivado"));
            Toast.makeText(this, mensajeExito, Toast.LENGTH_SHORT).show();
            finish();
        }, e -> {
            btnGuardarCambios.setEnabled(true);
            mostrarError(e);
        });
    }

    /** RF-WTX-004: habilitar o rechazar al taxista. Mientras esté PENDIENTE no puede entrar a la app. */
    private void cambiarAprobacion(EstadoAprobacion aprobacion) {
        if (usuario == null || usuario.getUid() == null) return;
        btnAprobarTaxista.setEnabled(false);
        btnRechazarTaxista.setEnabled(false);
        repositorio.cambiarAprobacion(usuario.getUid(), aprobacion, ignorado -> {
            usuario.setEstadoAprobacion(aprobacion.name());
            RegistroEventos.registrar(RegistroEventos.TAXISTA_APROBACION, RolUsuario.SUPERADMIN,
                    "Taxista " + usuario.getName() + ": " + aprobacion.name());
            mostrarAprobacion();
            Toast.makeText(this, getString(R.string.superadmin_aprobacion_guardada, aprobacion.getEtiqueta()),
                    Toast.LENGTH_SHORT).show();
        }, e -> {
            mostrarAprobacion();
            mostrarError(e);
        });
    }

    private void configurarSeccionRol(String rol) {
        layoutTaxista.setVisibility(View.GONE);
        layoutAdminHotel.setVisibility(View.GONE);
        layoutCliente.setVisibility(View.GONE);

        switch (rol.toLowerCase()) {
            case "taxista":
                layoutTaxista.setVisibility(View.VISIBLE);
                // Licencia y vehículo los registra el portal web de taxistas (API REST, Lab 7)
                tvLicencia.setText(textoODash(usuario.getLicencia()));
                tvPlacaAuto.setText(textoODash(usuario.getPlacaAuto()));
                tvModeloVehiculo.setText(textoODash(usuario.getModeloVehiculo()));
                mostrarAprobacion();
                break;

            case "admin":
            case "admin hotel":
                layoutAdminHotel.setVisibility(View.VISIBLE);
                tvHotelAsignado.setText(textoODash(usuario.getHotelAsignado()));
                break;

            case "cliente":
            default:
                layoutCliente.setVisibility(View.VISIBLE);
                tvDireccion.setText(textoODash(usuario.getDireccion()));
                tvHistorialReservas.setText(textoODash(usuario.getHistorialReservas()));
                break;
        }
    }

    private void mostrarAprobacion() {
        EstadoAprobacion actual = EstadoAprobacion.PENDIENTE;
        if (usuario.getEstadoAprobacion() != null) {
            try {
                actual = EstadoAprobacion.valueOf(usuario.getEstadoAprobacion());
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Estado de aprobación desconocido: " + usuario.getEstadoAprobacion(), e);
            }
        }
        tvAprobacionTaxista.setText(getString(R.string.superadmin_aprobacion_actual, actual.getEtiqueta()));
        btnAprobarTaxista.setEnabled(actual != EstadoAprobacion.APROBADO);
        btnRechazarTaxista.setEnabled(actual != EstadoAprobacion.RECHAZADO);
    }

    private String textoODash(String texto) {
        return texto == null || texto.trim().isEmpty() ? getString(R.string.superadmin_sin_dato) : texto;
    }

    private void mostrarError(Exception e) {
        Log.e(TAG, "Error de Firebase", e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
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
