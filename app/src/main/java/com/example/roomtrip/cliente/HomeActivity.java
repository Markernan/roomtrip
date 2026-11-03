package com.example.roomtrip.cliente;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.cliente.notificaciones.AvisosCobro;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionClienteHelper;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/** Pantalla 2: inicio del cliente con los hoteles activos de Firestore (Lab 6). */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private final List<Hotel> hoteles = new ArrayList<>();
    private HotelAdapter adapter;
    private ListenerRegistration registroCobros;
    private String uid;

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
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        setContentView(R.layout.activity_home);

        // El nombre se guardó al iniciar sesión (usuarios/{uid}), así el saludo no espera a Firestore
        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText(getString(R.string.cliente_saludo, new SesionClienteManager(this).getPrimerNombre()));

        RecyclerView rvHoteles = findViewById(R.id.rvHoteles);
        rvHoteles.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HotelAdapter(hoteles, this::abrirHotel);
        rvHoteles.setAdapter(adapter);

        findViewById(R.id.campoBuscar).setOnClickListener(v ->
                startActivity(new Intent(this, BuscarHotelActivity.class)));

        NavegacionClienteHelper.configurarBarra(this, "EXPLORAR");

        if (savedInstanceState == null) {
            pedirPermisoNotificaciones();
        }
        cargarHoteles();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid != null) registroCobros = AvisosCobro.escuchar(this, uid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registroCobros != null) {
            registroCobros.remove();
            registroCobros = null;
        }
    }

    private void cargarHoteles() {
        new HotelRepository().obtenerActivos(lista -> {
            hoteles.clear();
            hoteles.addAll(lista);
            adapter.notifyDataSetChanged();
            if (lista.isEmpty()) {
                Toast.makeText(this, R.string.cliente_sin_hoteles, Toast.LENGTH_LONG).show();
            }
        }, e -> {
            Log.e(TAG, "No se pudieron leer los hoteles", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    private void abrirHotel(Hotel hotel) {
        Intent intent = new Intent(this, DetalleHotelActivity.class);
        intent.putExtra(Constants.EXTRA_HOTEL_ID, hotel.getId());
        startActivity(intent);
    }

    // POST_NOTIFICATIONS es obligatorio desde Android 13; minSdk de la app es 34, así que siempre se pide
    private void pedirPermisoNotificaciones() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
