package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.notificaciones.ClienteNotificationHelper;
import com.example.roomtrip.utils.Constants;

/**
 * Checkout del cliente: valoración obligatoria (RF-CL-04), notificación de cobro
 * (RF-CL-05) y taxi gratuito si la reserva alcanza el monto mínimo del hotel
 * (RF-CL-08 / RF-AH-10 / RN-005).
 */
public class CheckoutActivity extends AppCompatActivity {

    // TODO: el total real de la reserva llega con el modelo Reserva y Firebase (Lab 6).
    private static final double MONTO_COBRO_DEMO = 83.0;

    private RatingBar ratingBarEstadia;
    private EditText etObservaciones;
    private ClienteNotificationHelper notificaciones;
    private String hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        notificaciones = new ClienteNotificationHelper(this); // crea el canal "cliente_cobros"
        hotel = getIntent().getStringExtra(Constants.EXTRA_NOMBRE_HOTEL);
        if (hotel == null) {
            hotel = getString(R.string.cliente_hotel_desconocido);
        }

        ((TextView) findViewById(R.id.tvNotificacionCobro)).setText(getString(
                R.string.cliente_checkout_banner_cobro,
                ClienteNotificationHelper.formatearMonto(MONTO_COBRO_DEMO)));

        ratingBarEstadia = findViewById(R.id.ratingBarEstadia);
        etObservaciones = findViewById(R.id.etObservaciones);

        Spinner spinnerAeropuerto = findViewById(R.id.spinnerAeropuerto);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Aeropuerto Jorge Chávez"});
        spinnerAeropuerto.setAdapter(adapter);

        // TODO: RF-AH-10 — mostrar/ocultar cajaTaxiGratis según el monto mínimo
        // configurado por el hotel, comparado contra el total real de la reserva (Lab 6).

        findViewById(R.id.btnSolicitarTaxi).setOnClickListener(v ->
                startActivity(new Intent(this, BuscandoTaxistaActivity.class)));

        findViewById(R.id.btnFinalizarCheckout).setOnClickListener(v -> finalizarCheckout());
    }

    private void finalizarCheckout() {
        if (etObservaciones.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Las observaciones de la estadía son obligatorias", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: registrar valoración + observaciones y pasar la reserva a FINALIZADA (Lab 6).
        notificaciones.notificarCobro(hotel, MONTO_COBRO_DEMO);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
