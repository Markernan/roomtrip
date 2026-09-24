package com.example.roomtrip.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.MainActivity;
import com.example.roomtrip.taxista.SolicitadoActivity;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinnerRoles;
    private Button btnIniciarSesion;

    private final String[] roles = {"Cliente", "Admin Hotel", "Taxista", "SuperAdmin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinnerRoles = findViewById(R.id.spinnerRoles);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roles
        );
        spinnerRoles.setAdapter(adapter);

        btnIniciarSesion.setOnClickListener(v -> {
            String rolSeleccionado = spinnerRoles.getSelectedItem().toString();
            Toast.makeText(this, "Ingresando como: " + rolSeleccionado, Toast.LENGTH_SHORT).show();

            Intent intent;

            switch (rolSeleccionado) {
                case "Taxista":
                    // Redirige al módulo independiente de Taxista
                    intent = new Intent(LoginActivity.this, SolicitadoActivity.class);
                    break;

                case "Cliente":
                case "Admin Hotel":
                case "SuperAdmin":
                default:
                    // Todos van a MainActivity, enviando el rol seleccionado como un Intent Extra
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("ROL_USUARIO", rolSeleccionado);
                    break;
            }

            startActivity(intent);
            finish();
        });
    }
}
