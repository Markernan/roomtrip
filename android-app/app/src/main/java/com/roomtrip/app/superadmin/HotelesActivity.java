package com.roomtrip.app.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roomtrip.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class HotelesActivity extends AppCompatActivity {

    private RecyclerView rvHoteles;
    private ImageButton btnBack;
    private FloatingActionButton fabAddHotel;
    private EditText etBuscarHotel;
    private HotelesAdapter adapter;
    private List<Hotel> listaHoteles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_hoteles);

        rvHoteles = findViewById(R.id.rvHoteles);
        btnBack = findViewById(R.id.btnBack);
        fabAddHotel = findViewById(R.id.fabAddHotel);
        etBuscarHotel = findViewById(R.id.etBuscarHotel);

        rvHoteles.setLayoutManager(new LinearLayoutManager(this));

        // Load mock data
        cargarDatosMock();

        // Setup Adapter
        adapter = new HotelesAdapter(listaHoteles, hotel -> {
            Intent intent = new Intent(HotelesActivity.this, DetalleHotelAdminActivity.class);
            intent.putExtra("hotel", hotel);
            intent.putExtra("IMAGEN_RES_ID", hotel.getPhotoResId());
            startActivity(intent);
        });
        rvHoteles.setAdapter(adapter);

        // Setup Search Filter
        etBuscarHotel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> finish());
        fabAddHotel.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrarHotelActivity.class));
        });

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaHoteles = new ArrayList<>();
        listaHoteles.add(new Hotel("Hotel Italia (Fachada)", "Miraflores, Lima", 4.7f, "Roberto Gómez", "roberto@miraflores.com", "+51 987 654 321", R.drawable.hotel_italia_fachada_calle, true));
        listaHoteles.add(new Hotel("Hotel Italia (Suite Azul)", "Centro de Lima, Lima", 4.5f, "María Mendoza", "maria@granhotellima.com", "+51 912 345 678", R.drawable.hotel_habitacion_azul, true));
        listaHoteles.add(new Hotel("Hotel Italia (Elegante)", "Plaza de Armas, Cusco", 4.9f, "Carlos Inca", "carlos@cuscoplaza.com", "+51 965 432 198", R.drawable.hotel_habitacion_elegante, true));
        listaHoteles.add(new Hotel("Grand Hotel Italia", "Bahía de Paracas, Ica", 4.6f, "Elena Mar", "elena@resortparacas.com", "+51 954 123 789", R.drawable.grand_hotel_italia_fachada, false));
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