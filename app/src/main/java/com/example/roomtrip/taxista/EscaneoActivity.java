package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.databinding.ActivityEscaneoBinding;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;

/** Cierre del servicio por QR (RN-011): EN_TRASLADO → FINALIZADO. */
public class EscaneoActivity extends AppCompatActivity {

    private static final String TAG = "EscaneoActivity";

    private ActivityEscaneoBinding binding;
    private ServicioTaxiRepository repositorio;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivityEscaneoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repositorio = new ServicioTaxiRepository();

        // "Validar QR" simulado: la lectura real con cámara se implementa en un lab posterior
        binding.btnEscanearQR.setOnClickListener(v -> finalizarServicio());
    }

    /**
     * Valida el QR contra el servicio en Firestore y registra FINALIZADO. La validación (que el código
     * sea el id del servicio y que esté EN_TRASLADO) ya es la definitiva; lo único simulado es la
     * lectura: mientras no haya cámara, el "código leído" es el id del servicio en curso.
     */
    private void finalizarServicio() {
        binding.btnEscanearQR.setEnabled(false);

        repositorio.obtenerServicioEnCurso(uid, servicio -> {
            if (servicio == null || servicio.getEstado() != EstadoServicioTaxi.EN_TRASLADO) {
                Toast.makeText(this, R.string.toast_taxista_sin_servicio, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String codigoLeido = servicio.getId(); // TODO lab 7: reemplazar por el texto que lea ZXing

            repositorio.finalizarConQr(servicio.getId(), codigoLeido, uid, finalizado -> {
                if (finalizado == null) {
                    binding.btnEscanearQR.setEnabled(true);
                    Toast.makeText(this, R.string.toast_taxista_qr_invalido, Toast.LENGTH_LONG).show();
                    return;
                }
                RegistroEventos.registrar(RegistroEventos.SERVICIO_TAXI_FINALIZADO, RolUsuario.TAXISTA,
                        "Servicio " + finalizado.getId() + " finalizado con QR");
                new TaxiNotificationHelper(this).mostrarServicioFinalizado(finalizado);
                new SesionTaxistaManager(this).setDisponible(true);

                startActivity(new Intent(EscaneoActivity.this, FinalizadoActivity.class));
                finish();
            }, e -> {
                Log.e(TAG, "No se pudo finalizar el servicio " + servicio.getId(), e);
                binding.btnEscanearQR.setEnabled(true);
                Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
            });
        }, e -> {
            Log.e(TAG, "No se pudo consultar el servicio en curso", e);
            binding.btnEscanearQR.setEnabled(true);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }
}
