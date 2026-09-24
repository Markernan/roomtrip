package com.roomtrip.app.superadmin;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.roomtrip.app.R;

public class DetalleHotelReporteActivity extends AppCompatActivity {

    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_detalle_hotel_reporte);

        hotel = (Hotel) getIntent().getSerializableExtra("hotel");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        initViews();
    }

    private void initViews() {
        ImageView ivHotelHeader = findViewById(R.id.ivHotelHeader);
        TextView tvHotelTitle = findViewById(R.id.tvHotelTitle);
        TextView tvHotelLocation = findViewById(R.id.tvHotelLocation);
        ImageButton btnExportSmall = findViewById(R.id.btnExportSmall);

        if (hotel != null) {
            tvHotelTitle.setText(hotel.getName());
            tvHotelLocation.setText(hotel.getLocation());

            if (hotel.getPhotoResId() != 0) {
                Glide.with(this)
                        .load(hotel.getPhotoResId())
                        .placeholder(R.drawable.foto_hotel_miraflores)
                        .into(ivHotelHeader);
            }
        }

        btnExportSmall.setOnClickListener(v -> {
            Toast.makeText(this, "Exportando reporte de " + (hotel != null ? hotel.getName() : "hotel"), Toast.LENGTH_SHORT).show();
        });
    }
}