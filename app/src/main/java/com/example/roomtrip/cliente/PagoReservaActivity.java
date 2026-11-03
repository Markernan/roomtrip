package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.SesionClienteManager;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.EstadoHabitacion;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;

/**
 * Registro de tarjeta (simulada) y confirmación de la reserva (RF-CL-03). La reserva se crea en
 * Firestore con una transacción que impide fechas superpuestas (RN-001 y RN-002). De la tarjeta solo
 * se guardan los últimos 4 dígitos: es una simulación y no se procesa ningún pago real.
 */
public class PagoReservaActivity extends AppCompatActivity {

    private static final String TAG = "PagoReservaActivity";

    public static final String EXTRA_HABITACION_TIPO = "habitacionTipo";
    public static final String EXTRA_HABITACION_CAPACIDAD = "habitacionCapacidad";
    public static final String EXTRA_HABITACION_PRECIO = "habitacionPrecio";

    private EditText etTitular, etNumeroTarjeta, etFechaCaducidad, etCvc;
    private View btnConfirmar;
    private Hotel hotel;
    private Habitacion habitacion;
    private String fechaEntrada, fechaSalida, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        setContentView(R.layout.activity_pago_reserva);

        hotel = (Hotel) getIntent().getSerializableExtra(Constants.EXTRA_HOTEL);
        habitacion = new Habitacion(getIntent().getStringExtra(EXTRA_HABITACION_TIPO),
                getIntent().getStringExtra(EXTRA_HABITACION_CAPACIDAD),
                getIntent().getDoubleExtra(EXTRA_HABITACION_PRECIO, 0), EstadoHabitacion.DISPONIBLE);
        habitacion.setId(getIntent().getStringExtra(Constants.EXTRA_HABITACION_ID));
        fechaEntrada = getIntent().getStringExtra(Constants.EXTRA_FECHA_ENTRADA);
        fechaSalida = getIntent().getStringExtra(Constants.EXTRA_FECHA_SALIDA);

        etTitular = configurarCampo(R.id.campoTitular, R.drawable.ic_person, getString(R.string.pago_hint_titular));
        etNumeroTarjeta = configurarCampo(R.id.campoNumeroTarjeta, R.drawable.ic_card, getString(R.string.pago_hint_numero));
        etFechaCaducidad = configurarCampo(R.id.campoFechaCaducidad, R.drawable.ic_calendar, getString(R.string.pago_hint_caducidad));
        etCvc = configurarCampo(R.id.campoCvc, R.drawable.ic_lock, getString(R.string.pago_hint_cvc));
        etNumeroTarjeta.setInputType(InputType.TYPE_CLASS_NUMBER);
        etCvc.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        btnConfirmar = findViewById(R.id.btnConfirmarReserva);
        btnConfirmar.setOnClickListener(v -> confirmarReserva());

        if (hotel == null || habitacion.getId() == null || fechaEntrada == null || fechaSalida == null) {
            Toast.makeText(this, R.string.cliente_fechas_invalidas, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private EditText configurarCampo(int idInclude, int idIcono, String hint) {
        View campo = findViewById(idInclude);
        ((ImageView) campo.findViewById(R.id.ivIconoCampo)).setImageResource(idIcono);
        EditText editText = campo.findViewById(R.id.etCampo);
        editText.setHint(hint);
        return editText;
    }

    /** Validación básica de la tarjeta simulada: titular, 13 a 19 dígitos, MM/AA y CVC de 3 o 4. */
    private boolean tarjetaValida() {
        String numero = etNumeroTarjeta.getText().toString().replace(" ", "");
        if (etTitular.getText().toString().trim().isEmpty()) {
            etTitular.setError(getString(R.string.cliente_campo_obligatorio));
            return false;
        }
        if (!numero.matches("\\d{13,19}")) {
            etNumeroTarjeta.setError(getString(R.string.pago_error_numero));
            return false;
        }
        if (!etFechaCaducidad.getText().toString().trim().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            etFechaCaducidad.setError(getString(R.string.pago_error_caducidad));
            return false;
        }
        if (!etCvc.getText().toString().trim().matches("\\d{3,4}")) {
            etCvc.setError(getString(R.string.pago_error_cvc));
            return false;
        }
        return true;
    }

    private void confirmarReserva() {
        if (!tarjetaValida()) return;
        String numero = etNumeroTarjeta.getText().toString().replace(" ", "");
        String nombreCliente = new SesionClienteManager(this).getNombre();
        Reserva reserva = Reserva.nueva(uid, nombreCliente, hotel, habitacion, fechaEntrada, fechaSalida,
                numero.substring(numero.length() - 4));

        btnConfirmar.setEnabled(false);
        new ReservaRepository().reservar(reserva, resultado -> {
            switch (resultado) {
                case CONFIRMADA:
                    RegistroEventos.registrar(RegistroEventos.RESERVA_CREADA, RolUsuario.CLIENTE,
                            "Reserva " + reserva.getId() + " en " + hotel.getNombre());
                    Intent intent = new Intent(this, ReservaExitosaActivity.class);
                    intent.putExtra("nombreHotel", hotel.getNombre());
                    startActivity(intent);
                    finish();
                    return;
                case HABITACION_OCUPADA:
                    Toast.makeText(this, R.string.cliente_conflicto_habitacion, Toast.LENGTH_LONG).show();
                    break;
                case CLIENTE_CON_RESERVA:
                    Toast.makeText(this, R.string.cliente_conflicto_cliente, Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, R.string.cliente_fechas_invalidas, Toast.LENGTH_LONG).show();
                    break;
            }
            btnConfirmar.setEnabled(true);
        }, e -> {
            btnConfirmar.setEnabled(true);
            Log.e(TAG, "No se pudo crear la reserva", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }
}
