package com.example.roomtrip.data.firebase;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.roomtrip.utils.ImagenesStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fotos en Firebase Storage (Clase 11): hoteles en {@code hoteles/{hotelId}/...jpg} y perfiles en
 * {@code perfiles/{uid}.jpg}. La imagen se comprime en un hilo de fondo; los resultados llegan al hilo principal.
 * Quién puede escribir cada ruta lo decide {@code storage.rules}.
 */
public class FotosRepository {

    private static final String TAG = "FotosRepository";
    private static final ExecutorService HILO_FONDO = Executors.newFixedThreadPool(2);
    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    /** Sube una foto del hotel y devuelve su URL de descarga. */
    public void subirFotoHotel(@NonNull Context context, @NonNull String hotelId, @NonNull Uri origen,
                               @NonNull Callbacks.Exito<String> exito, @NonNull Callbacks.Fallo fallo) {
        String ruta = "hoteles/" + hotelId + "/" + System.currentTimeMillis() + "_"
                + Integer.toHexString(origen.hashCode()) + ".jpg";
        subir(context, origen, ruta, exito, fallo);
    }

    /** Sube la foto de perfil del usuario (una sola, se reemplaza) y devuelve su URL de descarga. */
    public void subirFotoPerfil(@NonNull Context context, @NonNull String uid, @NonNull Uri origen,
                                @NonNull Callbacks.Exito<String> exito, @NonNull Callbacks.Fallo fallo) {
        subir(context, origen, "perfiles/" + uid + ".jpg", exito, fallo);
    }

    /** Borra un archivo a partir de su URL de descarga. Si falla solo se registra: sobra un archivo, no se rompe nada. */
    public void eliminar(String url) {
        String ruta = RutaStorage.desdeUrl(url);
        if (ruta == null) {
            Log.w(TAG, "URL que no es de Storage: " + url);
            return;
        }
        storage.getReference().child(ruta).delete()
                .addOnFailureListener(e -> Log.w(TAG, "No se pudo borrar " + ruta, e));
    }

    private void subir(Context context, Uri origen, String ruta,
                       Callbacks.Exito<String> exito, Callbacks.Fallo fallo) {
        Context app = context.getApplicationContext();
        HILO_FONDO.execute(() -> {
            final byte[] datos;
            try {
                datos = ImagenesStorage.comprimirJpeg(app.getContentResolver(), origen,
                        ImagenesStorage.LADO_MAXIMO, ImagenesStorage.CALIDAD_JPEG);
            } catch (Exception e) {
                PRINCIPAL.post(() -> fallo.onFallo(e));
                return;
            }
            PRINCIPAL.post(() -> {
                StorageReference ref = storage.getReference().child(ruta);
                ref.putBytes(datos, new StorageMetadata.Builder().setContentType("image/jpeg").build())
                        .continueWithTask(tarea -> {
                            if (!tarea.isSuccessful()) throw tarea.getException();
                            return ref.getDownloadUrl();
                        })
                        .addOnSuccessListener(url -> exito.onExito(url.toString()))
                        .addOnFailureListener(fallo::onFallo);
            });
        });
    }
}
