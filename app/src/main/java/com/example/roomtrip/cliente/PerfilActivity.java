package com.example.roomtrip.cliente;

import android.content.Intent;
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
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.cliente.notificaciones.ClienteNotificationHelper;
import com.example.roomtrip.login.LoginActivity;
import com.example.roomtrip.utils.NavegacionClienteHelper;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombres, etApellidos, etDocumento, etFechaNacimiento,
            etCorreo, etTelefono, etDomicilio;
    private Spinner spinnerTipoDocumento;
    private PerfilClienteRepository repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        repositorio = new PerfilClienteRepository(this);
        new FotoPerfilSelector(this, repositorio);

        etNombres = configurarCampo(R.id.campoNombres, R.drawable.ic_person, "Nombres");
        etApellidos = configurarCampo(R.id.campoApellidos, R.drawable.ic_person, "Apellidos");
        etDocumento = configurarCampo(R.id.campoDocumento, R.drawable.ic_badge, getString(R.string.cliente_hint_numero_documento));
        etFechaNacimiento = configurarCampo(R.id.campoFechaNacimiento, R.drawable.ic_calendar, "Fecha de nacimiento");
        etCorreo = configurarCampo(R.id.campoCorreo, R.drawable.ic_email, "Correo electrónico");
        etTelefono = configurarCampo(R.id.campoTelefono, R.drawable.ic_phone, "Teléfono");
        etDomicilio = configurarCampo(R.id.campoDomicilio, R.drawable.ic_home_pin, "Domicilio");
        configurarCampo(R.id.campoContrasena, R.drawable.ic_lock, "Contraseña");

        spinnerTipoDocumento = findViewById(R.id.spinnerTipoDocumento);
        ArrayAdapter<CharSequence> tipos = ArrayAdapter.createFromResource(
                this, R.array.cliente_tipos_documento, R.layout.item_spinner_cliente);
        tipos.setDropDownViewResource(R.layout.item_spinner_cliente);
        spinnerTipoDocumento.setAdapter(tipos);

        mostrarDatosGuardados();

        findViewById(R.id.btnGuardarCambios).setOnClickListener(v -> guardarCambios());

        findViewById(R.id.tvCerrarSesion).setOnClickListener(v -> cerrarSesion());

        NavegacionClienteHelper.configurarBarra(this, "PERFIL");
    }

    private EditText configurarCampo(int idInclude, int idIcono, String hint) {
        View campo = findViewById(idInclude);
        ((ImageView) campo.findViewById(R.id.ivIconoCampo)).setImageResource(idIcono);
        EditText editText = campo.findViewById(R.id.etCampo);
        editText.setHint(hint);
        return editText;
    }

    /** Sin cuenta registrada se muestra el cliente de demostración, como en el mockup. */
    private void mostrarDatosGuardados() {
        PerfilCliente perfil = repositorio.obtener();
        if (perfil == null) {
            etNombres.setText(R.string.cliente_demo_nombre);
            etApellidos.setText(R.string.cliente_demo_apellidos);
            return;
        }
        etNombres.setText(perfil.nombres);
        etApellidos.setText(perfil.apellidos);
        spinnerTipoDocumento.setSelection(perfil.tipoDocumento);
        etDocumento.setText(perfil.numeroDocumento);
        etFechaNacimiento.setText(perfil.fechaNacimiento);
        etCorreo.setText(perfil.correo);
        etTelefono.setText(perfil.telefono);
        etDomicilio.setText(perfil.domicilio);
    }

    private void guardarCambios() {
        if (texto(etNombres).isEmpty()) {
            marcarError(etNombres, R.string.cliente_campo_obligatorio);
            return;
        }
        if (texto(etApellidos).isEmpty()) {
            marcarError(etApellidos, R.string.cliente_campo_obligatorio);
            return;
        }
        if (!texto(etCorreo).isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(texto(etCorreo)).matches()) {
            marcarError(etCorreo, R.string.cliente_correo_invalido);
            return;
        }
        repositorio.guardar(new PerfilCliente(
                texto(etNombres), texto(etApellidos),
                spinnerTipoDocumento.getSelectedItemPosition(), texto(etDocumento),
                texto(etFechaNacimiento), texto(etCorreo), texto(etTelefono), texto(etDomicilio)));
        Toast.makeText(this, R.string.cliente_perfil_guardado, Toast.LENGTH_SHORT).show();
    }

    private void cerrarSesion() {
        new SesionClienteManager(this).cerrarSesion();
        new ClienteNotificationHelper(this).cancelarTodas();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void marcarError(EditText campo, int mensaje) {
        campo.setError(getString(mensaje));
        campo.requestFocus();
    }

    private String texto(EditText campo) {
        return campo.getText().toString().trim();
    }
}
