package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/** Pantalla 1: Bienvenida / inicio de sesión del cliente. */
public class BienvenidaActivity extends AppCompatActivity {

    private EditText etCorreo;
    private EditText etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);

        findViewById(R.id.btnIniciarSesion).setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.tvRegistrarse).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
        findViewById(R.id.tvOlvideContrasena).setOnClickListener(v ->
                Toast.makeText(this, "Recuperación de contraseña pendiente de implementar", Toast.LENGTH_SHORT).show());
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: reemplazar por Firebase Authentication (Lab 6).
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
