package com.roomtrip.app.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/** Pantalla de autoregistro del cliente (RF-CL-01). */
public class RegistroActivity extends AppCompatActivity {

    private EditText etNombres, etApellidos, etDocumento, etFechaNacimiento,
            etCorreo, etTelefono, etDomicilio, etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombres = configurarCampo(R.id.campoNombres, R.drawable.ic_person, "Nombres");
        etApellidos = configurarCampo(R.id.campoApellidos, R.drawable.ic_person, "Apellidos");
        etDocumento = configurarCampo(R.id.campoDocumento, R.drawable.ic_badge, "DNI, pasaporte o carnet de extranjería");
        etFechaNacimiento = configurarCampo(R.id.campoFechaNacimiento, R.drawable.ic_calendar, "Fecha de nacimiento");
        etCorreo = configurarCampo(R.id.campoCorreo, R.drawable.ic_email, "Correo electrónico");
        etTelefono = configurarCampo(R.id.campoTelefono, R.drawable.ic_phone, "Teléfono");
        etDomicilio = configurarCampo(R.id.campoDomicilio, R.drawable.ic_home_pin, "Domicilio");
        etContrasena = configurarCampo(R.id.campoContrasena, R.drawable.ic_lock, "Contraseña");
        etCorreo.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etContrasena.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD | android.text.InputType.TYPE_CLASS_TEXT);

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
        if (etNombres.getText().toString().trim().isEmpty()
                || etCorreo.getText().toString().trim().isEmpty()
                || etContrasena.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: registrar en Firebase Authentication + Firestore (Lab 6).
        // RF-CL-01: el cliente queda habilitado automáticamente, sin aprobación.
        Toast.makeText(this, "Cuenta creada. Ya puedes iniciar sesión", Toast.LENGTH_SHORT).show();
        finish();
    }
}
