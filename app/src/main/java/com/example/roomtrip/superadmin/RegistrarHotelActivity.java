package com.example.roomtrip.superadmin;

import android.util.Log;
import android.util.Patterns;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.ImagenesHotel;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import android.content.Intent;
import android.os.Bundle;
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

        // Lab 6: el administrador se asigna por correo. Al iniciar sesión por primera vez con ese
        // correo, la app crea su cuenta de administrador de este hotel (invitaciones/{correo}).
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;

        // Botón Volver
        binding.btnBack.setOnClickListener(v -> finish());

        // Acción Guardar
        binding.btnRegistrarHotel.setOnClickListener(v -> validarYRegistrar());

        setupBottomNavigation();
    }

    private void validarYRegistrar() {
        String nombre = texto(binding.etNombreHotel.getText());
        String direccion = texto(binding.etDireccionHotel.getText());
        String telefono = texto(binding.etTelefonoHotel.getText());
        String correoHotel = texto(binding.etCorreoHotel.getText());
        String correoAdmin = texto(binding.autoCompleteAdmin.getText());

        if (nombre.isEmpty() || direccion.isEmpty() || correoAdmin.isEmpty()) {
            Toast.makeText(this, R.string.superadmin_registrar_hotel_obligatorios, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correoAdmin).matches()
                || (!correoHotel.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(correoHotel).matches())) {
            Toast.makeText(this, R.string.cliente_correo_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        Hotel hotel = new Hotel();
        hotel.setNombre(nombre);
        hotel.setUbicacion(direccion);
        hotel.setContacto(telefono);
        hotel.setCorreo(correoHotel);
        hotel.setPrecio(getString(R.string.superadmin_hotel_sin_tarifa));
        // Foto de demostración mientras se define Storage o Cloudinary
        hotel.setImagen(ImagenesHotel.CLAVES[(int) (System.currentTimeMillis() % ImagenesHotel.CLAVES.length)]);

        binding.btnRegistrarHotel.setEnabled(false);
        new HotelRepository().registrar(hotel, correoAdmin, hotelId -> {
            RegistroEventos.registrar(RegistroEventos.HOTEL_REGISTRADO, RolUsuario.SUPERADMIN,
                    "Hotel " + nombre + " registrado; administrador invitado: " + correoAdmin);
            // Navegar a HotelesActivity con flag de éxito
            Intent intent = new Intent(this, HotelesActivity.class);
            intent.putExtra("REGISTRO_EXITOSO", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }, e -> {
            binding.btnRegistrarHotel.setEnabled(true);
            Log.e("RegistrarHotelActivity", "No se pudo registrar el hotel", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    private static String texto(CharSequence texto) {
        return texto != null ? texto.toString().trim() : "";
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
