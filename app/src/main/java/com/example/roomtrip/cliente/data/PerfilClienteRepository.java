package com.example.roomtrip.cliente.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Cuenta del cliente guardada en el dispositivo:
 *  - Datos del formulario en SharedPreferences ("cliente_perfil").
 *  - Foto de perfil como JPEG reducido en el almacenamiento interno (filesDir/foto_cliente.jpg).
 */
public class PerfilClienteRepository {

    private static final String PREFS = "cliente_perfil";
    private static final String ARCHIVO_FOTO = "foto_cliente.jpg";
    private static final int LADO_MAXIMO_FOTO = 512;
    private static final int CALIDAD_JPEG = 85;

    private static final String KEY_REGISTRADO = "registrado";
    private static final String KEY_NOMBRES = "nombres";
    private static final String KEY_APELLIDOS = "apellidos";
    private static final String KEY_TIPO_DOCUMENTO = "tipoDocumento";
    private static final String KEY_NUMERO_DOCUMENTO = "numeroDocumento";
    private static final String KEY_FECHA_NACIMIENTO = "fechaNacimiento";
    private static final String KEY_CORREO = "correo";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_DOMICILIO = "domicilio";

    private final Context context;
    private final SharedPreferences prefs;

    public PerfilClienteRepository(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean hayCuenta() {
        return prefs.getBoolean(KEY_REGISTRADO, false);
    }

    public void guardar(PerfilCliente perfil) {
        prefs.edit()
                .putBoolean(KEY_REGISTRADO, true)
                .putString(KEY_NOMBRES, perfil.nombres)
                .putString(KEY_APELLIDOS, perfil.apellidos)
                .putInt(KEY_TIPO_DOCUMENTO, perfil.tipoDocumento)
                .putString(KEY_NUMERO_DOCUMENTO, perfil.numeroDocumento)
                .putString(KEY_FECHA_NACIMIENTO, perfil.fechaNacimiento)
                .putString(KEY_CORREO, perfil.correo)
                .putString(KEY_TELEFONO, perfil.telefono)
                .putString(KEY_DOMICILIO, perfil.domicilio)
                .apply();
    }

    /** Devuelve null si todavía no hay una cuenta registrada. */
    public PerfilCliente obtener() {
        if (!hayCuenta()) {
            return null;
        }
        return new PerfilCliente(
                prefs.getString(KEY_NOMBRES, ""),
                prefs.getString(KEY_APELLIDOS, ""),
                prefs.getInt(KEY_TIPO_DOCUMENTO, 0),
                prefs.getString(KEY_NUMERO_DOCUMENTO, ""),
                prefs.getString(KEY_FECHA_NACIMIENTO, ""),
                prefs.getString(KEY_CORREO, ""),
                prefs.getString(KEY_TELEFONO, ""),
                prefs.getString(KEY_DOMICILIO, ""));
    }

    /** Primer nombre para el saludo, o cadena vacía si no hay cuenta. */
    public String getPrimerNombre() {
        String nombres = prefs.getString(KEY_NOMBRES, "").trim();
        int espacio = nombres.indexOf(' ');
        return espacio > 0 ? nombres.substring(0, espacio) : nombres;
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
