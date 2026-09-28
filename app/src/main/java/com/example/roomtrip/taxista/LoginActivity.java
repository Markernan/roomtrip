package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.roomtrip.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Se usan únicamente los IDs existentes en activity_login.xml
        etPassword = findViewById(R.id.etPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        if (btnIniciarSesion != null) {
            btnIniciarSesion.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, SolicitadoActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}