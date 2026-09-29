package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.roomtrip.R;
import com.example.roomtrip.databinding.ActivitySuperadminRegistrarHotelBinding;

public class RegistrarHotelActivity extends AppCompatActivity {

    private ActivitySuperadminRegistrarHotelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperadminRegistrarHotelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar Dropdown de Administradores
        String[] admins = {"Juan Pérez", "María García", "Carlos López", "Ana Martínez"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, admins);
        binding.autoCompleteAdmin.setAdapter(adapter);

        // Botón Volver
        binding.btnBack.setOnClickListener(v -> finish());

        // Acción Guardar
        binding.btnRegistrarHotel.setOnClickListener(v -> validarYRegistrar());

        setupBottomNavigation();
    }

    private void validarYRegistrar() {
        String nombre = binding.etNombreHotel.getText() != null ? binding.etNombreHotel.getText().toString().trim() : "";
        String direccion = binding.etDireccionHotel.getText() != null ? binding.etDireccionHotel.getText().toString().trim() : "";

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
        binding.bottomNav.setSelectedItemId(R.id.nav_hoteles);
        binding.bottomNav.setOnItemSelectedListener(item -> {
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
