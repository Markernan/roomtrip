package com.example.roomtrip.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Reduce y comprime una foto elegida de la galería antes de subirla a Firebase Storage. */
public final class ImagenesStorage {

    /** Lado máximo en píxeles: suficiente para una pantalla de teléfono y ligero para subir. */
    public static final int LADO_MAXIMO = 1280;
    public static final int CALIDAD_JPEG = 80;

    private ImagenesStorage() {
    }

    /** Escala a {@code maxLado}, corrige la rotación EXIF y devuelve los bytes del JPEG. Llamar fuera del hilo principal. */
    public static byte[] comprimirJpeg(ContentResolver cr, Uri uri, int maxLado, int calidad) throws IOException {
        BitmapFactory.Options limites = new BitmapFactory.Options();
        limites.inJustDecodeBounds = true;
        try (InputStream in = cr.openInputStream(uri)) {
            BitmapFactory.decodeStream(in, null, limites);
        }
        int muestra = 1;
        while (Math.max(limites.outWidth, limites.outHeight) / (muestra * 2) >= maxLado) {
            muestra *= 2;
        }
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = muestra;
        Bitmap bmp;
        try (InputStream in = cr.openInputStream(uri)) {
            bmp = BitmapFactory.decodeStream(in, null, op);
        }
        if (bmp == null) {
            throw new IOException("No se pudo leer la imagen");
        }
        int grados = 0;
        try (InputStream in = cr.openInputStream(uri)) {
            if (in != null) {
                int o = new ExifInterface(in).getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                grados = o == ExifInterface.ORIENTATION_ROTATE_90 ? 90
                        : o == ExifInterface.ORIENTATION_ROTATE_180 ? 180
                        : o == ExifInterface.ORIENTATION_ROTATE_270 ? 270 : 0;
            }
        } catch (IOException ignorada) {
            // sin EXIF
        }
        float escala = Math.min(1f, (float) maxLado / Math.max(bmp.getWidth(), bmp.getHeight()));
        Matrix m = new Matrix();
        m.postScale(escala, escala);
        m.postRotate(grados);
        Bitmap ajustada = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ajustada.compress(Bitmap.CompressFormat.JPEG, calidad, bos);
        return bos.toByteArray();
    }
}
