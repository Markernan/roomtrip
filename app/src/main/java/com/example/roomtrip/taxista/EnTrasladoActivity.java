package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.databinding.ActivityEnTrasladoBinding;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.NavegacionHelper;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

/** Estado EN_TRASLADO: el pasajero va en el taxi rumbo al aeropuerto. */
public class EnTrasladoActivity extends AppCompatActivity {

    private static final String TAG = "EnTrasladoActivity";

    private ActivityEnTrasladoBinding binding;
    private ServicioTaxiRepository repositorio;
    private ListenerRegistration registroServicio;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivityEnTrasladoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configuración de la barra de navegación inferior
        NavegacionHelper.configurarBarra(this, "TRABAJO");

        repositorio = new ServicioTaxiRepository();

        binding.btnEscanear.setOnClickListener(v -> {
            Intent intent = new Intent(EnTrasladoActivity.this, EscaneoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid == null) return;
        // Si el servicio se finaliza (QR validado), el listener devuelve null y se cierra esta pantalla
        registroServicio = repositorio.escucharServicioEnCurso(uid, servicio -> {
            if (servicio == null) {
                finish();
                return;
            }
            binding.tvNombrePasajero.setText(servicio.getClienteNombre());
            binding.tvRatingPasajero.setText(getString(R.string.en_camino_rating, servicio.getCalificacionCliente()));
            binding.tvOrigen.setText(getString(R.string.pedido_origen, servicio.getHotelNombre()));
            binding.tvDestino.setText(getString(R.string.pedido_destino, servicio.getAeropuerto()));
            binding.tvEstado.setText(servicio.getEstado().getEtiqueta());
        }, e -> {
            Log.e(TAG, "No se pudo escuchar el servicio en curso", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registroServicio != null) {
            registroServicio.remove();
            registroServicio = null;
        }
    }
}
