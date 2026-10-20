package com.example.roomtrip.cliente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.PerfilClienteRepository;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.utils.NavegacionClienteHelper;

import java.util.List;

/** Pantalla 2: inicio del cliente, con el hotel recomendado. */
public class HomeActivity extends AppCompatActivity {

    // Debe registrarse antes de que la Activity llegue a STARTED (por eso es un campo)
    private final ActivityResultLauncher<String> permisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, R.string.toast_cliente_permiso_notificaciones, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String nombre = new PerfilClienteRepository(this).getPrimerNombre();
        if (nombre.isEmpty()) {
            nombre = getString(R.string.cliente_demo_nombre);
        }
        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText(getString(R.string.cliente_saludo, nombre));

        List<Hotel> hoteles = MockData.getHotelesClienteEjemplo();

        RecyclerView rvHoteles = findViewById(R.id.rvHoteles);
        rvHoteles.setLayoutManager(new LinearLayoutManager(this));
        rvHoteles.setAdapter(new HotelAdapter(hoteles, hotel -> {
            Intent intent = new Intent(this, DetalleHotelActivity.class);
            intent.putExtra("nombreHotel", hotel.getNombre());
            startActivity(intent);
        }));

        findViewById(R.id.campoBuscar).setOnClickListener(v ->
                startActivity(new Intent(this, BuscarHotelActivity.class)));

        NavegacionClienteHelper.configurarBarra(this, "EXPLORAR");

        if (savedInstanceState == null) {
            pedirPermisoNotificaciones();
        }
    }

    // POST_NOTIFICATIONS es obligatorio desde Android 13; minSdk de la app es 34, así que siempre se pide
    private void pedirPermisoNotificaciones() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
