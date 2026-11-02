package com.example.roomtrip.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.MainActivity;
import com.example.roomtrip.R;
import com.example.roomtrip.adminhotel.AdminSession;
import com.example.roomtrip.cliente.HomeActivity;
import com.example.roomtrip.cliente.RegistroActivity;
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.data.model.EstadoAprobacion;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.PerfilUsuario;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.databinding.ActivityLoginBinding;
import com.example.roomtrip.superadmin.DashboardActivity;
import com.example.roomtrip.taxista.SolicitadoActivity;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Login único de la app (Lab 6).
 *
 * <ol>
 *   <li>Firebase Authentication con FirebaseUI: correo y contraseña o Google (Clase 10).</li>
 *   <li>Si se registró con correo, debe verificarlo (sendEmailVerification / reload).</li>
 *   <li>Se lee {@code usuarios/{uid}} en Firestore y se navega a la pantalla de su {@code rol}.</li>
 *   <li>Si no tiene documento: si el Superadmin lo invitó como administrador de un hotel se crea su
 *       perfil; si no, elige registrarse como cliente o pedir acceso como taxista.</li>
 * </ol>
 * La sesión queda abierta (Firebase la recuerda) y se vuelve a validar cada vez que se abre la app,
 * por si el Superadmin desactivó la cuenta.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Clase 10: idioma de los correos de Firebase (español de América Latina)
    private static final String IDIOMA_CORREOS = "es-419";

    private ActivityLoginBinding binding;
    private UsuarioRepository usuarios;
    private boolean correoVerificacionEnviado;

    // Resultado de las pantallas de FirebaseUI. Es un campo: debe registrarse antes de onStart
    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this::alVolverDeFirebaseUi);

    // "Completar registro" de un usuario autenticado sin perfil (por ejemplo, entró con Google)
    private final ActivityResultLauncher<Intent> completarRegistroLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultado -> {
                if (resultado.getResultCode() == RESULT_OK) {
                    validarUsuario(true);
                } else {
                    cerrarSesionFirebase();
                    mostrarCargando(false);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usuarios = new UsuarioRepository();

        binding.btnIniciarSesion.setOnClickListener(v -> abrirFirebaseUi());
        binding.tvRegistrarse.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Firebase recuerda la sesión aunque se cierre la app (Clase 10, "Usuario logueado")
            validarUsuario(false);
        } else {
            // Las sesiones simuladas de los labs anteriores (sin cuenta de Firebase) ya no valen
            SesionFirebase.limpiarSesionesLocales(this);
        }
    }

    // ---------- Firebase Authentication (FirebaseUI) ----------

    /** Lanza las pantallas de FirebaseUI con los proveedores habilitados en la consola. */
    private void abrirFirebaseUi() {
        List<AuthUI.IdpConfig> proveedores = new ArrayList<>();
        proveedores.add(new AuthUI.IdpConfig.EmailBuilder().build());
        try {
            proveedores.add(new AuthUI.IdpConfig.GoogleBuilder().build());
        } catch (IllegalStateException e) {
            // google-services.json se descargó antes de habilitar Google en la consola: falta
            // default_web_client_id. Se ofrece solo correo (ver docs/manual-instalacion.md, sección 5)
            Log.w(TAG, "Inicio con Google no configurado; solo se ofrece correo y contraseña", e);
        }

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(proveedores)
                // En FirebaseUI 9.x reemplaza a setIsSmartLockEnabled(false) de la Clase 10
                .setCredentialManagerEnabled(false)
                .setTheme(R.style.Theme_RoomTrip_FirebaseUI)
                .build();
        signInLauncher.launch(intent);
    }

    private void alVolverDeFirebaseUi(FirebaseAuthUIAuthenticationResult resultado) {
        if (resultado.getResultCode() == RESULT_OK && FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d(TAG, "Firebase uid: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
            correoVerificacionEnviado = false;
            validarUsuario(true);
            return;
        }
        IdpResponse respuesta = resultado.getIdpResponse();
        if (respuesta == null) {
            Log.d(TAG, "El usuario canceló el login");
            return;
        }
        Log.e(TAG, "Error en el login de FirebaseUI", respuesta.getError());
        Toast.makeText(this, R.string.login_error_firebase_ui, Toast.LENGTH_LONG).show();
    }

    /**
     * Valida al usuario autenticado: correo verificado → documento en Firestore → rol y estado.
     * {@code inicioExplicito} es true si acaba de iniciar sesión (y no al reabrir la app).
     */
    private void validarUsuario(boolean inicioExplicito) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            mostrarCargando(false);
            return;
        }
        mostrarCargando(true);

        // reload() trae el estado actual de la cuenta (por ejemplo, si ya verificó el correo). Clase 10
        usuario.reload().addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                Exception e = tarea.getException();
                if (e instanceof FirebaseAuthInvalidUserException) {
                    // La cuenta se eliminó o deshabilitó en la consola de Firebase
                    Log.w(TAG, "La cuenta ya no es válida", e);
                    rechazar(R.string.login_cuenta_desactivada);
                    return;
                }
                // Sin conexión: se continúa con los datos de la cuenta guardados en el teléfono
                Log.w(TAG, "No se pudo recargar el usuario; se usan los datos guardados", e);
            }
            FirebaseUser actual = FirebaseAuth.getInstance().getCurrentUser();
            if (actual == null) {
                mostrarCargando(false);
                return;
            }
            // Con Google el correo ya viene verificado; con correo y contraseña hay que confirmarlo
            if (!actual.isEmailVerified()) {
                pedirVerificacionDeCorreo(actual, inicioExplicito);
                return;
            }
            validarPerfil(actual, inicioExplicito);
        });
    }

    // ---------- Verificación de correo (Clase 10) ----------

    private void pedirVerificacionDeCorreo(FirebaseUser usuario, boolean inicioExplicito) {
        mostrarCargando(false);
        if (correoVerificacionEnviado) {
            // Tocó "Ya verifiqué" pero el enlace aún no se abrió
            Toast.makeText(this, R.string.login_verificacion_pendiente, Toast.LENGTH_SHORT).show();
        } else if (inicioExplicito) {
            enviarCorreoVerificacion(usuario, false);
        }
        mostrarDialogoVerificacion(usuario, inicioExplicito);
    }

    private void enviarCorreoVerificacion(FirebaseUser usuario, boolean avisarAlEnviar) {
        correoVerificacionEnviado = true;
        FirebaseAuth.getInstance().setLanguageCode(IDIOMA_CORREOS);
        usuario.sendEmailVerification().addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Log.d(TAG, "Correo de verificación enviado");
                if (avisarAlEnviar) {
                    Toast.makeText(this, R.string.login_verificacion_reenviada, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "No se pudo enviar el correo de verificación", tarea.getException());
                Toast.makeText(this, R.string.login_verificacion_no_enviada, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoVerificacion(FirebaseUser usuario, boolean inicioExplicito) {
        if (isFinishing() || isDestroyed()) return;
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_verificacion_titulo)
                .setMessage(getString(R.string.login_verificacion_mensaje, usuario.getEmail()))
                .setCancelable(false)
                .setPositiveButton(R.string.login_verificacion_ya_verifique, (d, w) -> {
                    correoVerificacionEnviado = true;
                    validarUsuario(inicioExplicito);
                })
                .setNeutralButton(R.string.login_verificacion_reenviar, (d, w) -> {
                    enviarCorreoVerificacion(usuario, true);
                    mostrarDialogoVerificacion(usuario, inicioExplicito);
                })
                .setNegativeButton(R.string.login_verificacion_cancelar, (d, w) -> {
                    cerrarSesionFirebase();
                    mostrarCargando(false);
                })
                .show();
    }

    // ---------- Perfil y rol en Firestore ----------

    private void validarPerfil(FirebaseUser usuario, boolean inicioExplicito) {
        usuarios.obtener(usuario.getUid(), perfil -> {
            if (perfil == null) {
                buscarInvitacion(usuario);
            } else {
                entrarSegunRol(usuario, perfil, inicioExplicito);
            }
        }, e -> fallar("No se pudo leer usuarios/" + usuario.getUid(), e));
    }

    /** Usuario sin perfil: ¿el Superadmin lo registró como administrador de un hotel? */
    private void buscarInvitacion(FirebaseUser usuario) {
        usuarios.obtenerInvitacion(usuario.getEmail(), invitacion -> {
            if (invitacion == null || invitacion.hotelId == null) {
                elegirTipoDeCuenta(usuario);
                return;
            }
            String nombre = usuario.getDisplayName() != null ? usuario.getDisplayName() : "";
            PerfilUsuario admin = PerfilUsuario.nuevoAdminHotel(nombre,
                    UsuarioRepository.claveCorreo(usuario.getEmail()), invitacion.hotelId, invitacion.hotelNombre);
            usuarios.crear(usuario.getUid(), admin, ignorado -> {
                RegistroEventos.registrar(RegistroEventos.ADMIN_HOTEL_ALTA, RolUsuario.ADMIN_HOTEL,
                        "Administrador del hotel " + invitacion.hotelNombre + " activó su cuenta");
                entrarSegunRol(usuario, admin, true);
            }, e -> fallar("No se pudo crear el perfil del administrador", e));
        }, e -> fallar("No se pudo leer la invitación", e));
    }

    /** Cuenta nueva sin invitación: el cliente completa sus datos; el taxista pide acceso. */
    private void elegirTipoDeCuenta(FirebaseUser usuario) {
        mostrarCargando(false);
        if (isFinishing() || isDestroyed()) return;
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_cuenta_nueva_titulo)
                .setMessage(getString(R.string.login_cuenta_nueva_mensaje, usuario.getEmail()))
                .setCancelable(false)
                .setPositiveButton(R.string.login_cuenta_nueva_cliente, (d, w) -> {
                    mostrarCargando(true);
                    Intent intent = new Intent(this, RegistroActivity.class);
                    intent.putExtra(RegistroActivity.EXTRA_COMPLETAR_REGISTRO, true);
                    completarRegistroLauncher.launch(intent);
                })
                .setNeutralButton(R.string.login_cuenta_nueva_taxista, (d, w) -> crearSolicitudTaxista(usuario))
                .setNegativeButton(R.string.login_verificacion_cancelar, (d, w) -> cerrarSesionFirebase())
                .show();
    }

    /** El taxista queda PENDIENTE hasta que el Superadmin lo apruebe (Taxistas › Detalle). */
    private void crearSolicitudTaxista(FirebaseUser usuario) {
        mostrarCargando(true);
        PerfilUsuario solicitud = PerfilUsuario.nuevaSolicitudTaxista(usuario.getDisplayName(), usuario.getEmail());
        usuarios.crear(usuario.getUid(), solicitud, ignorado -> {
            RegistroEventos.registrar(RegistroEventos.TAXISTA_SOLICITUD_HABILITACION, RolUsuario.TAXISTA,
                    "Nueva solicitud de habilitación de taxista");
            rechazar(R.string.login_solicitud_creada);
        }, e -> fallar("No se pudo crear la solicitud del taxista", e));
    }

    private void entrarSegunRol(FirebaseUser usuario, PerfilUsuario perfil, boolean inicioExplicito) {
        if (perfil.getRol() == null) {
            rechazar(R.string.login_sin_rol);
            return;
        }
        if (perfil.getEstado() != EstadoCuenta.ACTIVO) {
            rechazar(R.string.login_cuenta_desactivada);
            return;
        }
        String nombre = perfil.nombreCompleto();
        if (nombre.isEmpty()) {
            nombre = usuario.getDisplayName() != null ? usuario.getDisplayName() : String.valueOf(usuario.getEmail());
        }

        Intent destino;
        switch (perfil.getRol()) {
            case TAXISTA:
                if (perfil.getEstadoAprobacion() == EstadoAprobacion.RECHAZADO) {
                    rechazar(R.string.login_solicitud_rechazada);
                    return;
                }
                if (!perfil.puedeOperar()) {
                    rechazar(R.string.login_solicitud_pendiente);
                    return;
                }
                SesionTaxistaManager sesionTaxista = new SesionTaxistaManager(this);
                if (usuario.getUid().equals(sesionTaxista.getIdTaxista())) {
                    sesionTaxista.setNombre(nombre);                       // conserva su disponibilidad
                } else {
                    sesionTaxista.iniciarSesion(usuario.getUid(), nombre); // SharedPreferences (Lab 5)
                }
                destino = new Intent(this, SolicitadoActivity.class);
                break;

            case ADMIN_HOTEL:
                if (!perfil.puedeOperar()) {
                    rechazar(R.string.login_admin_sin_hotel);
                    return;
                }
                AdminSession.guardarHotel(this, perfil.getHotelId(), perfil.getHotelNombre());
                // MainActivity es el contenedor del admin de hotel (nav_graph con sus fragments)
                destino = new Intent(this, MainActivity.class);
                break;

            case CLIENTE:
                new SesionClienteManager(this).iniciarSesion(usuario.getUid(), nombre);
                destino = new Intent(this, HomeActivity.class);
                break;

            case SUPERADMIN:
            default:
                destino = new Intent(this, DashboardActivity.class);
                break;
        }

        if (inicioExplicito) {
            RegistroEventos.registrar(RegistroEventos.INICIO_SESION, perfil.getRol(), "Inicio de sesión");
        }
        startActivity(destino);
        finish();
    }

    // ---------- Helpers ----------

    /** No puede entrar: se cierra la sesión de Firebase y se explica el motivo. */
    private void rechazar(@StringRes int mensaje) {
        cerrarSesionFirebase();
        mostrarCargando(false);
        if (isFinishing() || isDestroyed()) return;
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_no_puedes_ingresar)
                .setMessage(mensaje)
                .setPositiveButton(R.string.login_entendido, null)
                .show();
    }

    private void fallar(String detalle, Exception e) {
        Log.e(TAG, detalle, e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        cerrarSesionFirebase();
        mostrarCargando(false);
    }

    /** Logout de Firebase (Clase 10: AuthUI.signOut) y limpieza de las sesiones locales. */
    private void cerrarSesionFirebase() {
        SesionFirebase.limpiarSesionesLocales(this);
        AuthUI.getInstance().signOut(this).addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                Log.w(TAG, "No se pudo cerrar la sesión de Firebase", tarea.getException());
            }
        });
    }

    private void mostrarCargando(boolean cargando) {
        binding.progressLogin.setVisibility(cargando ? View.VISIBLE : View.GONE);
        binding.btnIniciarSesion.setEnabled(!cargando);
        binding.tvRegistrarse.setEnabled(!cargando);
    }
}
