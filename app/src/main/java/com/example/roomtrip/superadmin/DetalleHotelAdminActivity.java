package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetalleHotelAdminActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgHotelDetalle;
    private TextView tvNombreHotel, tvDireccionHotel, tvCalificacionHotel, tvEstadoHotel;
    private TextView tvNombreAdmin, tvEmailAdmin;
    private CardView cardAdmin;
    private BottomNavigationView bottomNav;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_detalle_hotel);

        btnBack = findViewById(R.id.btnBack);
        imgHotelDetalle = findViewById(R.id.imgHotelDetalle);
        tvNombreHotel = findViewById(R.id.tvNombreHotel);
        tvDireccionHotel = findViewById(R.id.tvDireccionHotel);
        tvCalificacionHotel = findViewById(R.id.tvCalificacionHotel);
        tvEstadoHotel = findViewById(R.id.tvEstadoHotel);
        tvNombreAdmin = findViewById(R.id.tvNombreAdmin);
        tvEmailAdmin = findViewById(R.id.tvEmailAdmin);
        cardAdmin = findViewById(R.id.cardAdmin);
        bottomNav = findViewById(R.id.bottomNav);

        btnBack.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("hotel")) {
            hotel = (Hotel) getIntent().getSerializableExtra("hotel");
        }

        if (hotel != null) {
            tvNombreHotel.setText(hotel.getNombre());
            tvDireccionHotel.setText(hotel.getUbicacion());
            tvCalificacionHotel.setText(String.valueOf(hotel.getCalificacion()));

            boolean esActivo = "Activo".equalsIgnoreCase(hotel.getEstado()) || "ACTIVO".equalsIgnoreCase(hotel.getEstado());
            tvEstadoHotel.setText(esActivo ? "ACTIVO" : "INACTIVO");
            tvEstadoHotel.setTextColor(esActivo ? getColor(R.color.verde_texto) : getColor(R.color.rojo_alerta_texto));

            String adminNombre = "Admin " + hotel.getNombre();
            String adminCorreo = hotel.getContacto() != null ? hotel.getContacto() : "admin@hotel.com";

            tvNombreAdmin.setText(adminNombre);
            tvEmailAdmin.setText(adminCorreo);

            int imagenResId = getIntent().getIntExtra("IMAGEN_RES_ID", hotel.getImagenResId());
            if (imagenResId != 0) {
                Glide.with(this)
                        .load(imagenResId)
                        .placeholder(R.drawable.hotel_italia_fachada_calle)
                        .into(imgHotelDetalle);
            }
        }

        cardAdmin.setOnClickListener(v -> {
            if (hotel != null) {
                String adminNombre = "Admin " + hotel.getNombre();
                String adminCorreo = hotel.getContacto() != null ? hotel.getContacto() : "admin@hotel.com";

                Usuario adminUser = new Usuario(adminNombre, adminCorreo, "Admin", "999888777", true);
                adminUser.setHotelAsignado(hotel.getNombre());

                Intent intent = new Intent(DetalleHotelAdminActivity.this, DetalleUsuarioActivity.class);
                intent.putExtra("usuario", adminUser);
                startActivity(intent);
            }
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_hoteles) {
                finish();
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, UsuariosActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_auditoria) {
                startActivity(new Intent(this, AuditoriaActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}