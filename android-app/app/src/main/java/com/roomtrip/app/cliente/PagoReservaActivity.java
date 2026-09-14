package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/** Registro de tarjeta (simulada) y confirmación de la reserva (RF-CL-03). */
public class PagoReservaActivity extends AppCompatActivity {

    private EditText etTitular, etNumeroTarjeta, etFechaCaducidad, etCvc;
    private String nombreHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_reserva);

        nombreHotel = getIntent().getStringExtra("nombreHotel");

        etTitular = configurarCampo(R.id.campoTitular, R.drawable.ic_person, "Nombre del titular");
        etNumeroTarjeta = configurarCampo(R.id.campoNumeroTarjeta, R.drawable.ic_card, "Número de la tarjeta");
        etFechaCaducidad = configurarCampo(R.id.campoFechaCaducidad, R.drawable.ic_calendar, "MM/AA");
        etCvc = configurarCampo(R.id.campoCvc, R.drawable.ic_lock, "CVC");

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirmarReserva).setOnClickListener(v -> confirmarReserva());
    }

    private EditText configurarCampo(int idInclude, int idIcono, String hint) {
        View campo = findViewById(idInclude);
        ((ImageView) campo.findViewById(R.id.ivIconoCampo)).setImageResource(idIcono);
        EditText editText = campo.findViewById(R.id.etCampo);
        editText.setHint(hint);
        return editText;
    }

    /** RN-001/RN-002: la superposición de fechas ya se validó al elegir la habitación (tab Detalles). */
    private void confirmarReserva() {
        if (etTitular.getText().toString().trim().isEmpty()
                || etNumeroTarjeta.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Completa los datos de la tarjeta", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: registrar la reserva y el cobro simulado en Firestore (Lab 6).
        Intent intent = new Intent(this, ReservaExitosaActivity.class);
        intent.putExtra("nombreHotel", nombreHotel);
        startActivity(intent);
        finish();
    }
}
