package com.roomtrip.app.cliente;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.roomtrip.app.R;

import java.util.UUID;

/**
 * Código QR que el taxista escanea para registrar el estado FINALIZADO del
 * servicio (RF-TX-07 / RF-TAX-015). El QR codifica el ID del servicio de taxi.
 */
public class MostrarQrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_qr);

        // TODO: usar el ID real del documento serviciosTaxi de Firestore, no uno aleatorio (Lab 7).
        String idServicioTaxi = UUID.randomUUID().toString();

        ImageView ivQr = findViewById(R.id.ivCodigoQr);
        Bitmap bitmap = generarQr(idServicioTaxi, 600, 600);
        if (bitmap != null) {
            ivQr.setImageBitmap(bitmap);
        }

        findViewById(R.id.btnVolverQr).setOnClickListener(v -> finish());
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
            return null;
        }
    }
}
