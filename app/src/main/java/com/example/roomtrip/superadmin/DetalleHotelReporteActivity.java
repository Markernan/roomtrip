package com.example.roomtrip.superadmin;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;

public class DetalleHotelReporteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivHotelHeader;
    private TextView tvHotelTitle, tvHotelLocation;
    private ImageButton btnExportSmall;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_detalle_hotel_reporte);

        toolbar = findViewById(R.id.toolbar);
        ivHotelHeader = findViewById(R.id.ivHotelHeader);
        tvHotelTitle = findViewById(R.id.tvHotelTitle);
        tvHotelLocation = findViewById(R.id.tvHotelLocation);
        btnExportSmall = findViewById(R.id.btnExportSmall);

        toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra("hotel")) {
            hotel = (Hotel) getIntent().getSerializableExtra("hotel");
        }

        if (hotel != null) {
            tvHotelTitle.setText(hotel.getNombre());
            tvHotelLocation.setText(hotel.getUbicacion());

            if (hotel.getImagenResId() != 0) {
                Glide.with(this)
                        .load(hotel.getImagenResId())
                        .placeholder(R.drawable.foto_hotel_miraflores)
                        .into(ivHotelHeader);
            }
        }

        btnExportSmall.setOnClickListener(v ->
                Toast.makeText(this, "Exportando reporte individual de " + (hotel != null ? hotel.getNombre() : "Hotel"), Toast.LENGTH_SHORT).show()
        );
    }
}