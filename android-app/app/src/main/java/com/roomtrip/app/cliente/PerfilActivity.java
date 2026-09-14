package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/** Consulta y edición del perfil del cliente autenticado (RNF-03: solo sus propios datos). */
public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        EditText etNombres = configurarCampo(R.id.campoNombres, R.drawable.ic_person, "Nombres");
        EditText etApellidos = configurarCampo(R.id.campoApellidos, R.drawable.ic_person, "Apellidos");
        configurarCampo(R.id.campoDocumento, R.drawable.ic_badge, "DNI, pasaporte o carnet de extranjería");
        configurarCampo(R.id.campoFechaNacimiento, R.drawable.ic_calendar, "Fecha de nacimiento");
        configurarCampo(R.id.campoCorreo, R.drawable.ic_email, "Correo electrónico");
        configurarCampo(R.id.campoTelefono, R.drawable.ic_phone, "Teléfono");
        configurarCampo(R.id.campoDomicilio, R.drawable.ic_home_pin, "Domicilio");
        configurarCampo(R.id.campoContrasena, R.drawable.ic_lock, "Contraseña");

        // TODO: precargar con los datos reales del cliente autenticado desde Firestore (Lab 6).
        etNombres.setText("William");
        etApellidos.setText("Antaurco");

        findViewById(R.id.btnGuardarCambios).setOnClickListener(v ->
                Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show());

        findViewById(R.id.tvCerrarSesion).setOnClickListener(v -> {
            Intent intent = new Intent(this, BienvenidaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        findViewById(R.id.navExplorar).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.navReservas).setOnClickListener(v ->
                startActivity(new Intent(this, MisReservasActivity.class)));
    }

    private EditText configurarCampo(int idInclude, int idIcono, String hint) {
        View campo = findViewById(idInclude);
        ((ImageView) campo.findViewById(R.id.ivIconoCampo)).setImageResource(idIcono);
        EditText editText = campo.findViewById(R.id.etCampo);
        editText.setHint(hint);
        return editText;
    }
}
