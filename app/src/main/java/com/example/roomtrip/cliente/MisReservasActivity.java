package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.notificaciones.AvisosCobro;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionClienteHelper;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/** Historial de reservas del cliente en tiempo real (Lab 6). Desde aquí abre el chat o hace checkout. */
public class MisReservasActivity extends AppCompatActivity {

    private static final String TAG = "MisReservasActivity";

    private final List<Reserva> reservas = new ArrayList<>();
    private ReservaAdapter adapter;
    private ListenerRegistration registroReservas;
    private ListenerRegistration registroCobros;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        setContentView(R.layout.activity_mis_reservas);

        RecyclerView rv = findViewById(R.id.rvMisReservas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservaAdapter(reservas, this::abrirChat, this::abrirCheckout, this::abrirTaxi);
        rv.setAdapter(adapter);

        NavegacionClienteHelper.configurarBarra(this, "RESERVAS");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid == null) return;
        registroReservas = new ReservaRepository().escucharDeCliente(uid, lista -> {
            reservas.clear();
            reservas.addAll(lista);
            adapter.notifyDataSetChanged();
            if (lista.isEmpty()) {
                Toast.makeText(this, R.string.cliente_sin_reservas, Toast.LENGTH_SHORT).show();
            }
        }, e -> {
            Log.e(TAG, "No se pudieron escuchar las reservas", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
        registroCobros = AvisosCobro.escuchar(this, uid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registroReservas != null) registroReservas.remove();
        if (registroCobros != null) registroCobros.remove();
        registroReservas = null;
        registroCobros = null;
    }

    private void abrirChat(Reserva reserva) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_RESERVA_ID, reserva.getId());
        intent.putExtra(ChatActivity.EXTRA_TITULO, reserva.getHotelNombre());
        startActivity(intent);
    }

    private void abrirCheckout(Reserva reserva) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(Constants.EXTRA_RESERVA_ID, reserva.getId());
        startActivity(intent);
    }

    /** Seguimiento del taxi gratuito pedido en el checkout (la pantalla decide si aún se busca taxista). */
    private void abrirTaxi(Reserva reserva) {
        Intent intent = new Intent(this, SeguimientoTaxiActivity.class);
        intent.putExtra(Constants.EXTRA_SERVICIO_TAXI_ID, reserva.getServicioTaxiId());
        startActivity(intent);
    }
}
