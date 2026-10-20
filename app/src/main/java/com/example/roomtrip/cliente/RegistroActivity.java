package com.example.roomtrip.cliente;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.PerfilCliente;
import com.example.roomtrip.cliente.data.PerfilClienteRepository;

/** Pantalla de autoregistro del cliente (RF-CL-01). */
public class RegistroActivity extends AppCompatActivity {

    private static final int LARGO_MINIMO_CONTRASENA = 6;

    private EditText etNombres, etApellidos, etDocumento, etFechaNacimiento,
            etCorreo, etTelefono, etDomicilio, etContrasena;
    private Spinner spinnerTipoDocumento;
    private PerfilClienteRepository repositorio;
    private FotoPerfilSelector fotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        repositorio = new PerfilClienteRepository(this);
        fotoPerfil = new FotoPerfilSelector(this, repositorio);

        etNombres = configurarCampo(R.id.campoNombres, R.drawable.ic_person, "Nombres");
        etApellidos = configurarCampo(R.id.campoApellidos, R.drawable.ic_person, "Apellidos");
        etDocumento = configurarCampo(R.id.campoDocumento, R.drawable.ic_badge, getString(R.string.cliente_hint_numero_documento));
        etFechaNacimiento = configurarCampo(R.id.campoFechaNacimiento, R.drawable.ic_calendar, "Fecha de nacimiento");
        etCorreo = configurarCampo(R.id.campoCorreo, R.drawable.ic_email, "Correo electrónico");
        etTelefono = configurarCampo(R.id.campoTelefono, R.drawable.ic_phone, "Teléfono");
        etDomicilio = configurarCampo(R.id.campoDomicilio, R.drawable.ic_home_pin, "Domicilio");
        etContrasena = configurarCampo(R.id.campoContrasena, R.drawable.ic_lock, "Contraseña");
        etCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etContrasena.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD | android.text.InputType.TYPE_CLASS_TEXT);

        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        ArrayAdapter<CharSequence> tipos = ArrayAdapter.createFromResource(
                this, R.array.cliente_tipos_documento, R.layout.item_spinner_cliente);
        tipos.setDropDownViewResource(R.layout.item_spinner_cliente);
        spinnerTipoDocumento.setAdapter(tipos);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        findViewById(R.id.btnRegistrarse).setOnClickListener(v -> registrar());
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
        // La contraseña no se guarda en el dispositivo (RNF-01): en el Lab 6 la gestiona Firebase Auth.
        repositorio.guardar(new PerfilCliente(
                texto(etNombres), texto(etApellidos),
                spinnerTipoDocumento.getSelectedItemPosition(), texto(etDocumento),
                texto(etFechaNacimiento), texto(etCorreo), texto(etTelefono), texto(etDomicilio)));
        if (!fotoPerfil.seElegioFoto()) {
            repositorio.borrarFoto();
        }
        // RF-CL-01: el cliente queda habilitado automáticamente, sin aprobación.
        Toast.makeText(this, R.string.cliente_registro_ok, Toast.LENGTH_SHORT).show();
        finish();
    }

    /** Todos los campos son obligatorios; la foto es opcional. Marca el primero que falle. */
    private boolean formularioValido() {
        EditText[] obligatorios = {etNombres, etApellidos, etDocumento, etFechaNacimiento,
                etCorreo, etTelefono, etDomicilio, etContrasena};
        for (EditText campo : obligatorios) {
            if (texto(campo).isEmpty()) {
                return marcarError(campo, R.string.cliente_campo_obligatorio);
            }
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(texto(etCorreo)).matches()) {
            return marcarError(etCorreo, R.string.cliente_correo_invalido);
        }
        if (etContrasena.getText().length() < LARGO_MINIMO_CONTRASENA) {
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
