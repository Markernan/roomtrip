package com.example.roomtrip.superadmin;

import android.util.Log;
import android.widget.Toast;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.EstadoCuenta;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import java.util.ArrayList;
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
    private final List<Hotel> listaHoteles = new ArrayList<>();
    private final HotelRepository repositorio = new HotelRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_hoteles);

        rvHoteles = findViewById(R.id.rvHoteles);
        btnBack = findViewById(R.id.btnBack);
        fabAddHotel = findViewById(R.id.fabAddHotel);
        etBuscarHotel = findViewById(R.id.etBuscarHotel);

        rvHoteles.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HotelesAdapter(this, listaHoteles, hotel -> {
            Intent intent = new Intent(HotelesActivity.this, DetalleHotelAdminActivity.class);
            intent.putExtra("hotel", hotel);
            intent.putExtra("IMAGEN_RES_ID", hotel.getImagenResId());
            startActivity(intent);
        });
        adapter.setOnEstadoHotelListener(this::cambiarEstado);
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

    /**
     * RegistrarHotelActivity nos abre con CLEAR_TOP | SINGLE_TOP: como esta pantalla ya está en la
     * pila, Android la reutiliza y entrega el Intent aquí en vez de llamar a onCreate.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        verificarRegistroExitoso();
    }

    private void verificarRegistroExitoso() {
        if (getIntent().getBooleanExtra("REGISTRO_EXITOSO", false)) {
            Snackbar.make(rvHoteles, "Hotel registrado con éxito", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.turquesa))
                    .setTextColor(getResources().getColor(R.color.blanco))
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHoteles();   // al volver de Registrar hotel aparece el nuevo
    }

    /** Lab 6: todos los hoteles de Firestore. */
    private void cargarHoteles() {
        repositorio.obtenerTodos(hoteles -> adapter.actualizar(hoteles), this::mostrarError);
    }

    /** RF-USR-007: activar o desactivar el hotel (un hotel INACTIVO no aparece al cliente). */
    private void cambiarEstado(Hotel hotel, boolean activo) {
        repositorio.cambiarEstado(hotel.getId(), EstadoCuenta.desde(activo), ignorado -> {
            RegistroEventos.registrar(RegistroEventos.HOTEL_ESTADO, RolUsuario.SUPERADMIN,
                    hotel.getNombre() + " " + (activo ? "activado" : "desactivado"));
            Toast.makeText(this, getString(activo ? R.string.superadmin_hotel_activado
                    : R.string.superadmin_hotel_desactivado, hotel.getNombre()), Toast.LENGTH_SHORT).show();
        }, e -> {
            mostrarError(e);
            cargarHoteles();
        });
    }

    private void mostrarError(Exception e) {
        Log.e("HotelesActivity", "Error de Firestore", e);
        Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
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
