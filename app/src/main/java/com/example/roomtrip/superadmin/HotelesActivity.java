package com.example.roomtrip.superadmin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;

import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Hotel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import com.example.roomtrip.superadmin.RegistrarHotelActivity;

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

        cargarDatosMock();

        adapter = new HotelesAdapter(this, listaHoteles, hotel -> {
            Intent intent = new Intent(HotelesActivity.this, DetalleHotelAdminActivity.class);
            intent.putExtra("hotel", hotel);
            intent.putExtra("IMAGEN_RES_ID", hotel.getImagenResId());
            startActivity(intent);
        });
        rvHoteles.setAdapter(adapter);

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

        verificarRegistroExitoso();
        setupBottomNavigation();
    }

    private void verificarRegistroExitoso() {
        if (getIntent().getBooleanExtra("REGISTRO_EXITOSO", false)) {
            Snackbar.make(rvHoteles, "Hotel registrado con éxito", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.turquesa))
                    .setTextColor(getResources().getColor(R.color.blanco))
                    .show();
        }
    }

    private void cargarDatosMock() {
        listaHoteles = MockData.getHotelesSuperadminEjemplo();
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
