package com.example.roomtrip.cliente;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.PerfilClienteRepository;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Autoregistro del cliente (RF-CL-01): queda habilitado automáticamente, sin aprobación.
 *
 * <p>Dos modos:
 * <ul>
 *   <li><b>Cuenta nueva</b> (desde "Regístrate"): crea la cuenta con Firebase Authentication
 *       ({@code createUserWithEmailAndPassword}), guarda {@code usuarios/{uid}} con rol CLIENTE y
 *       envía el correo de verificación. La contraseña nunca se guarda en Firestore.</li>
 *   <li><b>Completar registro</b> ({@link #EXTRA_COMPLETAR_REGISTRO}): el usuario ya inició sesión
 *       con FirebaseUI (por ejemplo, con Google) y solo faltan sus datos.</li>
 * </ul>
 * La foto de perfil sigue guardándose en el teléfono (Lab 5) hasta definir Storage o Cloudinary.
 */
public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";
    public static final String EXTRA_COMPLETAR_REGISTRO = "completarRegistro";

    private static final int LARGO_MINIMO_CONTRASENA = 6;
    private static final String IDIOMA_CORREOS = "es-419";

    private EditText etNombres, etApellidos, etDocumento, etFechaNacimiento,
            etCorreo, etTelefono, etDomicilio, etContrasena;
    private Spinner spinnerTipoDocumento;
    private PerfilClienteRepository repositorioFoto;
    private FotoPerfilSelector fotoPerfil;
    private UsuarioRepository usuarios;
    private boolean completarRegistro;
    private View btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        repositorioFoto = new PerfilClienteRepository(this);
        fotoPerfil = new FotoPerfilSelector(this, repositorioFoto);
        usuarios = new UsuarioRepository();

        etNombres = configurarCampo(R.id.campoNombres, R.drawable.ic_person, getString(R.string.cliente_hint_nombres));
        etApellidos = configurarCampo(R.id.campoApellidos, R.drawable.ic_person, getString(R.string.cliente_hint_apellidos));
        etDocumento = configurarCampo(R.id.campoDocumento, R.drawable.ic_badge, getString(R.string.cliente_hint_numero_documento));
        etFechaNacimiento = configurarCampo(R.id.campoFechaNacimiento, R.drawable.ic_calendar, getString(R.string.cliente_hint_fecha_nacimiento));
        etCorreo = configurarCampo(R.id.campoCorreo, R.drawable.ic_email, getString(R.string.cliente_hint_correo));
        etTelefono = configurarCampo(R.id.campoTelefono, R.drawable.ic_phone, getString(R.string.cliente_hint_telefono));
        etDomicilio = configurarCampo(R.id.campoDomicilio, R.drawable.ic_home_pin, getString(R.string.cliente_hint_domicilio));
        etContrasena = configurarCampo(R.id.campoContrasena, R.drawable.ic_lock, getString(R.string.cliente_hint_contrasena));
        etCorreo.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etContrasena.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD | android.text.InputType.TYPE_CLASS_TEXT);

        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        ArrayAdapter<CharSequence> tipos = ArrayAdapter.createFromResource(
                this, R.array.cliente_tipos_documento, R.layout.item_spinner_cliente);
        tipos.setDropDownViewResource(R.layout.item_spinner_cliente);
        spinnerTipoDocumento.setAdapter(tipos);

        completarRegistro = getIntent().getBooleanExtra(EXTRA_COMPLETAR_REGISTRO, false);
        FirebaseUser actual = FirebaseAuth.getInstance().getCurrentUser();
        if (completarRegistro && actual != null) {
            // Ya tiene cuenta (FirebaseUI): el correo es el de la cuenta y no hace falta contraseña
            etCorreo.setText(actual.getEmail());
            etCorreo.setEnabled(false);
            findViewById(R.id.campoContrasena).setVisibility(View.GONE);
            if (actual.getDisplayName() != null) etNombres.setText(actual.getDisplayName());
        }

        findViewById(R.id.btnVolver).setOnClickListener(v -> cancelar());
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(v -> registrar());
    }

    /** El botón atrás del sistema también termina con RESULT_CANCELED (el resultado por defecto). */
    private void cancelar() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /** Inyecta el ícono y el hint en una instancia de campo_registro_generico.xml. */
    private EditText configurarCampo(int idInclude, int idIcono, String hint) {
        View campo = findViewById(idInclude);
        ((ImageView) campo.findViewById(R.id.ivIconoCampo)).setImageResource(idIcono);
        EditText editText = campo.findViewById(R.id.etCampo);
        editText.setHint(hint);
        return editText;
    }

    private void registrar() {
        if (!formularioValido()) {
            return;
        }
        btnRegistrarse.setEnabled(false);
        if (!fotoPerfil.seElegioFoto()) {
            repositorioFoto.borrarFoto();
        }
        FirebaseUser actual = FirebaseAuth.getInstance().getCurrentUser();
        if (completarRegistro && actual != null) {
            guardarPerfil(actual, true);
            return;
        }
        // Cuenta nueva: la contraseña la guarda Firebase Authentication (nunca en texto plano)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(texto(etCorreo), etContrasena.getText().toString())
                .addOnSuccessListener(resultado -> {
                    FirebaseUser usuario = resultado.getUser();
                    if (usuario == null) {
                        btnRegistrarse.setEnabled(true);
                        return;
                    }
                    usuario.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(texto(etNombres) + " " + texto(etApellidos)).build());
                    guardarPerfil(usuario, false);
                })
                .addOnFailureListener(e -> {
                    btnRegistrarse.setEnabled(true);
                    Log.e(TAG, "No se pudo crear la cuenta", e);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        marcarError(etCorreo, R.string.cliente_correo_en_uso);
                    } else if (e instanceof FirebaseAuthWeakPasswordException) {
                        marcarError(etContrasena, R.string.cliente_contrasena_debil);
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        marcarError(etCorreo, R.string.cliente_correo_invalido);
                    } else {
                        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /** Crea usuarios/{uid} con rol CLIENTE (habilitado automáticamente). */
    private void guardarPerfil(FirebaseUser usuario, boolean completando) {
        PerfilUsuario perfil = PerfilUsuario.nuevoCliente(
                texto(etNombres), texto(etApellidos),
                spinnerTipoDocumento.getSelectedItem().toString(), texto(etDocumento),
                texto(etFechaNacimiento), usuario.getEmail(), texto(etTelefono), texto(etDomicilio));

        usuarios.crear(usuario.getUid(), perfil, ignorado -> {
            RegistroEventos.registrar(RegistroEventos.CLIENTE_REGISTRO, RolUsuario.CLIENTE, "Nuevo cliente registrado");
            if (completando) {
                // El login continúa y lo lleva a su inicio
                setResult(RESULT_OK);
                finish();
                return;
            }
            // Cuenta nueva con correo: debe verificarlo antes de entrar (Clase 10)
            FirebaseAuth.getInstance().setLanguageCode(IDIOMA_CORREOS);
            usuario.sendEmailVerification().addOnCompleteListener(tarea -> {
                if (!tarea.isSuccessful()) {
                    Log.w(TAG, "No se pudo enviar el correo de verificación", tarea.getException());
                }
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, R.string.cliente_registro_ok, Toast.LENGTH_LONG).show();
                finish();
            });
        }, e -> {
            btnRegistrarse.setEnabled(true);
            Log.e(TAG, "No se pudo guardar el perfil del cliente", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    /** Todos los campos son obligatorios; la foto es opcional. Marca el primero que falle. */
    private boolean formularioValido() {
        EditText[] obligatorios = {etNombres, etApellidos, etDocumento, etFechaNacimiento,
                etCorreo, etTelefono, etDomicilio};
        for (EditText campo : obligatorios) {
            if (texto(campo).isEmpty()) {
                return marcarError(campo, R.string.cliente_campo_obligatorio);
            }
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(texto(etCorreo)).matches()) {
            return marcarError(etCorreo, R.string.cliente_correo_invalido);
        }
        if (!completarRegistro && etContrasena.getText().length() < LARGO_MINIMO_CONTRASENA) {
            return marcarError(etContrasena, R.string.cliente_contrasena_corta);
        }
        return true;
    }

    private boolean marcarError(EditText campo, int mensaje) {
        campo.setError(getString(mensaje));
        campo.requestFocus();
        return false;
    }

    private String texto(EditText campo) {
        return campo.getText().toString().trim();
    }
}
