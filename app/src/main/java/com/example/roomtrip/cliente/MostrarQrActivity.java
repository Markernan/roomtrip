package com.example.roomtrip.cliente;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Código QR que el taxista escanea para registrar el estado FINALIZADO del servicio
 * (RF-TX-07 / RN-011). El QR codifica el id del documento en {@code serviciosTaxi}; cuando el
 * taxista lo valida, esta pantalla se cierra sola.
 */
public class MostrarQrActivity extends AppCompatActivity {

    private static final String TAG = "MostrarQrActivity";

    private String servicioId;
    private ListenerRegistration registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SesionFirebase.uidOVolverAlLogin(this) == null) return;
        setContentView(R.layout.activity_mostrar_qr);
        findViewById(R.id.btnVolverQr).setOnClickListener(v -> finish());

        servicioId = getIntent().getStringExtra(Constants.EXTRA_SERVICIO_TAXI_ID);
        if (servicioId == null) {
            Toast.makeText(this, R.string.cliente_taxi_no_encontrado, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ImageView ivQr = findViewById(R.id.ivCodigoQr);
        Bitmap bitmap = generarQr(servicioId, 600, 600);
        if (bitmap != null) {
            ivQr.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, R.string.qr_error_generacion, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (servicioId == null) return;
        registro = new ServicioTaxiRepository().escucharServicio(servicioId, servicio -> {
            if (servicio != null && servicio.getEstado() == EstadoServicioTaxi.FINALIZADO) {
                Toast.makeText(this, R.string.cliente_taxi_finalizado, Toast.LENGTH_LONG).show();
                finish();
            }
        }, e -> Log.w(TAG, "No se pudo escuchar el servicio " + servicioId, e));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registro != null) registro.remove();
        registro = null;
    }

    private Bitmap generarQr(String contenido, int ancho, int alto) {
        try {
            BitMatrix matriz = new QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, ancho, alto);
            Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    bitmap.setPixel(x, y, matriz.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            Log.e(TAG, "No se pudo generar el QR del servicio de taxi", e);
            return null;
        }
    }
}
