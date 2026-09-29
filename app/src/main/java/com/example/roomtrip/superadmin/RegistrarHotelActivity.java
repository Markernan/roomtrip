package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.roomtrip.R;

public class RegistrarHotelActivity extends AppCompatActivity {

    private TextInputEditText etNombreHotel, etDireccionHotel;
    private Spinner spinnerAdmin;
    private Button btnGuardarHotel;
    private ImageButton btnBack;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_registrar_hotel);

        // Inicializar vistas con los IDs EXACTOS del XML
        etNombreHotel = findViewById(R.id.etNombreHotel);
        etDireccionHotel = findViewById(R.id.etDireccion);
        spinnerAdmin = findViewById(R.id.spinnerAdmin);
        btnGuardarHotel = findViewById(R.id.btnGuardarHotel);
        btnBack = findViewById(R.id.btnBack);
        bottomNav = findViewById(R.id.bottomNav);

        // Configurar Dropdown / Spinner de Administradores
        String[] admins = {"Juan Pérez", "María García", "Carlos López", "Ana Martínez"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, admins);
        spinnerAdmin.setAdapter(adapter);

        // Botón Volver
        btnBack.setOnClickListener(v -> finish());

        // Acción Guardar
        btnGuardarHotel.setOnClickListener(v -> validarYRegistrar());

        setupBottomNavigation();
    }

    private void validarYRegistrar() {
        String nombre = etNombreHotel.getText() != null ? etNombreHotel.getText().toString().trim() : "";
        String direccion = etDireccionHotel.getText() != null ? etDireccionHotel.getText().toString().trim() : "";

        if (nombre.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navegar a HotelesActivity con flag de éxito
        Intent intent = new Intent(this, HotelesActivity.class);
        intent.putExtra("REGISTRO_EXITOSO", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_hoteles);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (id == R.id.nav_hoteles) {
                startActivity(new Intent(this, HotelesActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, UsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_auditoria) {
                startActivity(new Intent(this, AuditoriaActivity.class));
                return true;
            }
            return false;
        });
    }
}