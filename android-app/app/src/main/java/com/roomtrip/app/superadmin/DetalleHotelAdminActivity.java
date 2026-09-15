package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.roomtrip.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetalleHotelAdminActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgHotelDetalle;
    private TextView tvNombreHotel, tvDireccionHotel, tvCalificacionHotel, tvEstadoHotel;
    private TextView tvNombreAdmin, tvEmailAdmin;
    private CardView cardAdmin;
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

        hotel = (Hotel) getIntent().getSerializableExtra("hotel");
        int imagenResId = getIntent().getIntExtra("IMAGEN_RES_ID", R.drawable.hotel_italia_fachada_calle);

        if (hotel != null) {
            tvNombreHotel.setText(hotel.getName());
            tvDireccionHotel.setText(hotel.getLocation());
            tvCalificacionHotel.setText(String.valueOf(hotel.getRating()));
            
            if (hotel.isActive()) {
                tvEstadoHotel.setText("ACTIVO");
                tvEstadoHotel.setTextColor(getResources().getColor(R.color.verde_texto));
                tvEstadoHotel.setBackgroundResource(R.drawable.bg_caja_verde_claro);
            } else {
                tvEstadoHotel.setText("INACTIVO");
                tvEstadoHotel.setTextColor(getResources().getColor(R.color.rojo_alerta_texto));
                tvEstadoHotel.setBackgroundResource(R.drawable.bg_notificacion_oscura); // uses background or alert
            }

            tvNombreAdmin.setText(hotel.getAdminName());
            tvEmailAdmin.setText(hotel.getAdminEmail());

            imgHotelDetalle.setImageResource(imagenResId);
        }

        btnBack.setOnClickListener(v -> finish());

        cardAdmin.setOnClickListener(v -> {
            if (hotel != null) {
                Usuario adminUser = new Usuario(
                        hotel.getAdminName(),
                        hotel.getAdminEmail(),
                        "Admin Hotel",
                        hotel.getAdminPhone(),
                        hotel.isActive()
                );
                Intent intent = new Intent(DetalleHotelAdminActivity.this, DetalleUsuarioActivity.class);
                intent.putExtra("usuario", adminUser);
                startActivity(intent);
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
                finish();
                return true;
            } else if (id == R.id.nav_hoteles) {
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