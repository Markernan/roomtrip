package com.example.roomtrip.cliente;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.PerfilClienteRepository;
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionClienteHelper;
import com.example.roomtrip.utils.SesionFirebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Perfil del cliente (Lab 6): los datos viven en {@code usuarios/{uid}} de Firestore. El correo es
 * el de Firebase Authentication y no se edita aquí; la contraseña se cambia desde el login
 * ("¿Olvidaste tu contraseña?" de FirebaseUI). La foto sigue guardada en el teléfono.
 */
public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "PerfilActivity";

    private EditText etNombres, etApellidos, etDocumento, etFechaNacimiento,
            etCorreo, etTelefono, etDomicilio;
    private Spinner spinnerTipoDocumento;
    private View btnGuardar;
    private final UsuarioRepository usuarios = new UsuarioRepository();
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        setContentView(R.layout.activity_perfil);

        new FotoPerfilSelector(this, new PerfilClienteRepository(this));

        etNombres = configurarCampo(R.id.campoNombres, R.drawable.ic_person, getString(R.string.cliente_hint_nombres));
        etApellidos = configurarCampo(R.id.campoApellidos, R.drawable.ic_person, getString(R.string.cliente_hint_apellidos));
        etDocumento = configurarCampo(R.id.campoDocumento, R.drawable.ic_badge, getString(R.string.cliente_hint_numero_documento));
        etFechaNacimiento = configurarCampo(R.id.campoFechaNacimiento, R.drawable.ic_calendar, getString(R.string.cliente_hint_fecha_nacimiento));
        etCorreo = configurarCampo(R.id.campoCorreo, R.drawable.ic_email, getString(R.string.cliente_hint_correo));
        etTelefono = configurarCampo(R.id.campoTelefono, R.drawable.ic_phone, getString(R.string.cliente_hint_telefono));
        etDomicilio = configurarCampo(R.id.campoDomicilio, R.drawable.ic_home_pin, getString(R.string.cliente_hint_domicilio));
        // El correo identifica la cuenta de Firebase Auth y la contraseña no se guarda en Firestore
        etCorreo.setEnabled(false);
        findViewById(R.id.campoContrasena).setVisibility(View.GONE);

        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        ArrayAdapter<CharSequence> tipos = ArrayAdapter.createFromResource(
                this, R.array.cliente_tipos_documento, R.layout.item_spinner_cliente);
        tipos.setDropDownViewResource(R.layout.item_spinner_cliente);
        spinnerTipoDocumento.setAdapter(tipos);

        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnGuardar.setEnabled(false);

        findViewById(R.id.tvCerrarSesion).setOnClickListener(v ->
                SesionFirebase.cerrarSesion(this, RolUsuario.CLIENTE));

        NavegacionClienteHelper.configurarBarra(this, "PERFIL");
        cargarPerfil();
    }

    private EditText configurarCampo(int idInclude, int idIcono, String hint) {
        View campo = findViewById(idInclude);
        ((ImageView) campo.findViewById(R.id.ivIconoCampo)).setImageResource(idIcono);
        EditText editText = campo.findViewById(R.id.etCampo);
        editText.setHint(hint);
        return editText;
    }

    private void cargarPerfil() {
        usuarios.obtener(uid, perfil -> {
            if (perfil == null) {
                Toast.makeText(this, R.string.cliente_perfil_no_encontrado, Toast.LENGTH_LONG).show();
                return;
            }
            etNombres.setText(perfil.getNombres());
            etApellidos.setText(perfil.getApellidos());
            seleccionarTipoDocumento(perfil.getTipoDocumento());
            etDocumento.setText(perfil.getNumeroDocumento());
            etFechaNacimiento.setText(perfil.getFechaNacimiento());
            etCorreo.setText(perfil.getCorreo());
            etTelefono.setText(perfil.getTelefono());
            etDomicilio.setText(perfil.getDomicilio());
            btnGuardar.setEnabled(true);
        }, e -> {
            Log.e(TAG, "No se pudo leer el perfil", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    /** El tipo de documento se guarda como texto ("DNI"); se busca su posición en el spinner. */
    private void seleccionarTipoDocumento(String tipo) {
        if (tipo == null) return;
        for (int i = 0; i < spinnerTipoDocumento.getCount(); i++) {
            if (tipo.equals(String.valueOf(spinnerTipoDocumento.getItemAtPosition(i)))) {
                spinnerTipoDocumento.setSelection(i);
                return;
            }
        }
    }

    private void guardarCambios() {
        EditText[] obligatorios = {etNombres, etApellidos, etDocumento, etTelefono};
        for (EditText campo : obligatorios) {
            if (texto(campo).isEmpty()) {
                campo.setError(getString(R.string.cliente_campo_obligatorio));
                campo.requestFocus();
                return;
            }
        }
        // Solo campos personales: las reglas no dejan al cliente tocar rol, estado ni correo
        Map<String, Object> cambios = new HashMap<>();
        cambios.put(PerfilUsuario.CAMPO_NOMBRES, texto(etNombres));
        cambios.put(PerfilUsuario.CAMPO_APELLIDOS, texto(etApellidos));
        cambios.put(PerfilUsuario.CAMPO_TIPO_DOCUMENTO, String.valueOf(spinnerTipoDocumento.getSelectedItem()));
        cambios.put(PerfilUsuario.CAMPO_NUMERO_DOCUMENTO, texto(etDocumento));
        cambios.put(PerfilUsuario.CAMPO_FECHA_NACIMIENTO, texto(etFechaNacimiento));
        cambios.put(PerfilUsuario.CAMPO_TELEFONO, texto(etTelefono));
        cambios.put(PerfilUsuario.CAMPO_DOMICILIO, texto(etDomicilio));

        btnGuardar.setEnabled(false);
        usuarios.actualizar(uid, cambios, ignorado -> {
            btnGuardar.setEnabled(true);
            new SesionClienteManager(this).setNombre((texto(etNombres) + " " + texto(etApellidos)).trim());
            Toast.makeText(this, R.string.cliente_perfil_guardado, Toast.LENGTH_SHORT).show();
        }, e -> {
            btnGuardar.setEnabled(true);
            Log.e(TAG, "No se pudo guardar el perfil", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    private String texto(EditText campo) {
        return campo.getText().toString().trim();
    }
}
