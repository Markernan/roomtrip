package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.BuildConfig;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.api.TaxistaApi;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ActivityEscaneoBinding;
import com.example.roomtrip.data.firebase.Callbacks;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.taxista.data.SincronizadorDisponibilidad;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.example.roomtrip.utils.SesionFirebase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Cierre del servicio por QR (RN-011): EN_TRASLADO → FINALIZADO.
 *
 * <p>Lab 7: el taxista lee con la cámara el QR que muestra el cliente ({@code MostrarQrActivity}, cuyo
 * contenido es el id del servicio) usando zxing-android-embedded. La validación (que el código sea el
 * id del servicio y que esté EN_TRASLADO) la hace {@link ServicioTaxiRepository#finalizarConQr} en una
 * transacción. En compilaciones de depuración, una pulsación larga del botón simula la lectura, para
 * poder probar el flujo en un emulador sin cámara.
 */
public class EscaneoActivity extends AppCompatActivity {

    private static final String TAG = "EscaneoActivity";

    private ActivityEscaneoBinding binding;
    private ServicioTaxiRepository repositorio;
    private String uid;
    /** Servicio EN_TRASLADO que se está cerrando mientras la cámara está abierta. */
    private String servicioEnCursoId;

    private final ActivityResultLauncher<ScanOptions> lectorQr =
            registerForActivityResult(new ScanContract(), resultado -> {
                String leido = resultado.getContents();
                if (leido == null || servicioEnCursoId == null) {
                    binding.btnEscanearQR.setEnabled(true);
                    Toast.makeText(this, R.string.escaneo_cancelado, Toast.LENGTH_SHORT).show();
                    return;
                }
                finalizarServicio(servicioEnCursoId, leido);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;

        binding = ActivityEscaneoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repositorio = new ServicioTaxiRepository();

        binding.btnEscanearQR.setOnClickListener(v -> conServicioEnTraslado(servicio -> {
            servicioEnCursoId = servicio.getId();
            lectorQr.launch(new ScanOptions()
                    .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    .setPrompt(getString(R.string.escaneo_prompt_camara))
                    .setBeepEnabled(false)
                    .setOrientationLocked(false));
        }));

        if (BuildConfig.DEBUG) {
            // Solo depuración: el emulador no tiene un QR delante de la cámara
            binding.btnEscanearQR.setOnLongClickListener(v -> {
                conServicioEnTraslado(servicio -> {
                    Toast.makeText(this, R.string.escaneo_simulado_debug, Toast.LENGTH_SHORT).show();
                    finalizarServicio(servicio.getId(), servicio.getId());
                });
                return true;
            });
        }
    }

    /** Busca el servicio del taxista y sigue solo si está EN_TRASLADO (el pasajero va en el taxi). */
    private void conServicioEnTraslado(Callbacks.Exito<ServicioTaxi> siguiente) {
        binding.btnEscanearQR.setEnabled(false);
        repositorio.obtenerServicioEnCurso(uid, servicio -> {
            if (servicio == null || servicio.getEstado() != EstadoServicioTaxi.EN_TRASLADO) {
                Toast.makeText(this, R.string.toast_taxista_sin_servicio, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            siguiente.onExito(servicio);
        }, e -> {
            Log.e(TAG, "No se pudo consultar el servicio en curso", e);
            binding.btnEscanearQR.setEnabled(true);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    /** Valida el código leído contra el servicio en Firestore y, si corresponde, registra FINALIZADO. */
    private void finalizarServicio(String servicioId, String codigoLeido) {
        binding.btnEscanearQR.setEnabled(false);
        repositorio.finalizarConQr(servicioId, codigoLeido, uid, finalizado -> {
            if (finalizado == null) {
                binding.btnEscanearQR.setEnabled(true);
                Toast.makeText(this, R.string.toast_taxista_qr_invalido, Toast.LENGTH_LONG).show();
                return;
            }
            RegistroEventos.registrar(RegistroEventos.SERVICIO_TAXI_FINALIZADO, RolUsuario.TAXISTA,
                    "Servicio " + finalizado.getId() + " finalizado con QR");
            new TaxiNotificationHelper(this).mostrarServicioFinalizado(finalizado);
            new SesionTaxistaManager(this).setDisponible(true);
            SincronizadorDisponibilidad.enviar(this, TaxistaApi.DISPONIBLE);

            startActivity(new Intent(EscaneoActivity.this, FinalizadoActivity.class));
            finish();
        }, e -> {
            Log.e(TAG, "No se pudo finalizar el servicio " + servicioId, e);
            binding.btnEscanearQR.setEnabled(true);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }
}
