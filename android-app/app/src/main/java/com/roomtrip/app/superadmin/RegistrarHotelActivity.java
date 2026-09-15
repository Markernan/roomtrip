package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.roomtrip.app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RegistrarHotelActivity extends AppCompatActivity {

    private TextInputEditText etNombreHotel, etDireccion;
    private Spinner spinnerAdmin;
    private Button btnGuardarHotel;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_registrar_hotel);

        etNombreHotel = findViewById(R.id.etNombreHotel);
        etDireccion = findViewById(R.id.etDireccion);
        spinnerAdmin = findViewById(R.id.spinnerAdmin);
        btnGuardarHotel = findViewById(R.id.btnGuardarHotel);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnGuardarHotel.setOnClickListener(v -> {
            String nombre = etNombreHotel.getText().toString();
            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hotel registrado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
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