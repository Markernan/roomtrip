package com.example.roomtrip.cliente.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Foto de perfil del cliente, guardada en el dispositivo como JPEG reducido
 * (filesDir/foto_cliente.jpg). Desde el Lab 6 los datos del perfil viven en Firestore
 * ({@code usuarios/{uid}}); subir la foto a Firebase Storage queda para más adelante.
 */
public class PerfilClienteRepository {

    private static final String ARCHIVO_FOTO = "foto_cliente.jpg";
    private static final int LADO_MAXIMO_FOTO = 512;
    private static final int CALIDAD_JPEG = 85;

    private final Context context;

    public PerfilClienteRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Copia la imagen elegida a almacenamiento interno, reducida y ya orientada
     * (ImageDecoder aplica el EXIF). Devuelve false si no se pudo guardar.
     */
    public boolean guardarFoto(Uri origen) {
        try {
            ImageDecoder.Source fuente = ImageDecoder.createSource(context.getContentResolver(), origen);
            Bitmap bitmap = ImageDecoder.decodeBitmap(fuente, (decoder, info, src) -> {
                int mayorLado = Math.max(info.getSize().getWidth(), info.getSize().getHeight());
                decoder.setTargetSampleSize(Math.max(1, mayorLado / LADO_MAXIMO_FOTO));
                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
            });
            try (FileOutputStream salida = new FileOutputStream(archivoFoto())) {
                return bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida);
            }
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    /** Devuelve la foto guardada o null si no hay. */
    public Bitmap cargarFoto() {
        File foto = archivoFoto();
        return foto.exists() ? BitmapFactory.decodeFile(foto.getAbsolutePath()) : null;
    }

    public void borrarFoto() {
        File foto = archivoFoto();
        if (foto.exists()) {
            //noinspection ResultOfMethodCallIgnored
            foto.delete();
        }
    }

    private File archivoFoto() {
        return new File(context.getFilesDir(), ARCHIVO_FOTO);
    }
}
