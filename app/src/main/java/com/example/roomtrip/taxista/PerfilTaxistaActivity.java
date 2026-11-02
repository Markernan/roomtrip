package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.databinding.ActivityPerfilTaxistaBinding;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionHelper;
import com.example.roomtrip.utils.SesionFirebase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Perfil del taxista: se lee y se guarda en usuarios/{uid} de Firestore. */
public class PerfilTaxistaActivity extends AppCompatActivity {

    private static final String TAG = "PerfilTaxistaActivity";

    private ActivityPerfilTaxistaBinding binding;
    private UsuarioRepository repositorio;
    private SesionTaxistaManager sesion;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivityPerfilTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavegacionHelper.configurarBarra(this, "PERFIL");

        repositorio = new UsuarioRepository();
        sesion = new SesionTaxistaManager(this);

        // Mientras carga se muestra el nombre guardado en la sesión (SharedPreferences)
        binding.etNombres.setText(sesion.getNombre());
        // El correo es el de la cuenta de Firebase Authentication: no se edita aquí
        binding.etCorreo.setEnabled(false);
        binding.btnGuardarCambios.setEnabled(false);
        cargarPerfil();

        // Las fotos se suben cuando se defina Storage o Cloudinary (arquitectura.md, pendientes)
        binding.btnSubirFotoAuto.setOnClickListener(v ->
                Toast.makeText(this, R.string.toast_perfil_taxista_foto_vehiculo, Toast.LENGTH_SHORT).show());
        binding.ivFotoPerfil.setOnClickListener(v ->
                Toast.makeText(this, R.string.toast_perfil_taxista_foto_perfil, Toast.LENGTH_SHORT).show());

        binding.btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        binding.btnVerHistorial.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialServiciosActivity.class)));

        binding.tvCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarPerfil() {
        repositorio.obtener(uid, perfil -> {
            binding.btnGuardarCambios.setEnabled(perfil != null);
            if (perfil == null) {
                Toast.makeText(this, R.string.toast_perfil_taxista_no_encontrado, Toast.LENGTH_LONG).show();
                return;
            }
            mostrar(perfil);
        }, e -> {
            Log.e(TAG, "No se pudo leer el perfil", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    private void mostrar(PerfilUsuario perfil) {
        binding.etNombres.setText(perfil.getNombres());
        binding.etApellidos.setText(perfil.getApellidos());
        binding.etTipoDocumento.setText(perfil.getTipoDocumento());
        binding.etNumeroDocumento.setText(perfil.getNumeroDocumento());
        binding.etFechaNacimiento.setText(perfil.getFechaNacimiento());
        binding.etCorreo.setText(perfil.getCorreo());
        binding.etTelefono.setText(perfil.getTelefono());
        binding.etDomicilio.setText(perfil.getDomicilio());
        binding.etPlaca.setText(perfil.getPlaca());
    }

    /** Guarda con update() solo los campos editables; rol, estado y aprobación no se tocan. */
    private void guardarCambios() {
        String nombres = texto(binding.etNombres);
        if (TextUtils.isEmpty(nombres)) {
            binding.etNombres.setError(getString(R.string.error_perfil_taxista_nombres));
            binding.etNombres.requestFocus();
            return;
        }
        String apellidos = texto(binding.etApellidos);

        Map<String, Object> cambios = new HashMap<>();
        cambios.put(PerfilUsuario.CAMPO_NOMBRES, nombres);
        cambios.put(PerfilUsuario.CAMPO_APELLIDOS, apellidos);
        cambios.put(PerfilUsuario.CAMPO_TIPO_DOCUMENTO, texto(binding.etTipoDocumento));
        cambios.put(PerfilUsuario.CAMPO_NUMERO_DOCUMENTO, texto(binding.etNumeroDocumento));
        cambios.put(PerfilUsuario.CAMPO_FECHA_NACIMIENTO, texto(binding.etFechaNacimiento));
        cambios.put(PerfilUsuario.CAMPO_TELEFONO, texto(binding.etTelefono));
        cambios.put(PerfilUsuario.CAMPO_DOMICILIO, texto(binding.etDomicilio));
        cambios.put(PerfilUsuario.CAMPO_PLACA, texto(binding.etPlaca).toUpperCase(Locale.ROOT));

        binding.btnGuardarCambios.setEnabled(false);
        repositorio.actualizar(uid, cambios, ignorado -> {
            // El nombre se usa al aceptar pedidos (taxistaNombre): se actualiza también en la sesión
            sesion.setNombre((nombres + " " + apellidos).trim());
            Toast.makeText(this, R.string.toast_perfil_taxista_guardado, Toast.LENGTH_SHORT).show();
            finish();
        }, e -> {
            Log.e(TAG, "No se pudo guardar el perfil", e);
            binding.btnGuardarCambios.setEnabled(true);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    /** Logout (Clase 10): AuthUI.signOut() cierra Firebase y Google; se limpian las sesiones locales. */
    private void cerrarSesion() {
        binding.tvCerrarSesion.setEnabled(false);
        SesionFirebase.cerrarSesion(this, RolUsuario.TAXISTA);
    }

    private static String texto(EditText campo) {
        return campo.getText() != null ? campo.getText().toString().trim() : "";
    }
}
