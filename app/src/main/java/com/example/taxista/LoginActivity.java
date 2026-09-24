package com.example.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;
    private Button btnIniciarSesion;
    private TextView tvRegistroWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de vistas
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        tvRegistroWeb = findViewById(R.id.tvRegistroWeb);

        // Botón Iniciar Sesión (valida y lleva a Solicitado)
        btnIniciarSesion.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (correo.isEmpty()) {
                etCorreo.setError("Por favor completa este campo");
                etCorreo.requestFocus();
            } else if (contrasena.isEmpty()) {
                etContrasena.setError("Por favor completa este campo");
                etContrasena.requestFocus();
            } else {
                Intent intent = new Intent(LoginActivity.this, SolicitadoActivity.class);
                startActivity(intent);
                finish(); // Cierra el login para no regresar al presionar 'Atrás'
            }
        });

        // Muestra el emergente con el aviso de implementación web
        tvRegistroWeb.setOnClickListener(v -> {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Próximamente")
                    .setMessage("La conexión con el portal web de registro está en proceso de implementación.")
                    .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}