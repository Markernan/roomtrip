package com.example.roomtrip.taxista.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.roomtrip.data.model.ServicioTaxi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Persistencia local del taxista en internal storage (data/data/com.example.roomtrip/files):
 *  - servicio_activo.json      -> el pedido aceptado y su estado actual
 *  - historial_servicios.json  -> lista de servicios FINALIZADOS
 * La E/S de archivos corre en un ExecutorService (hilo de fondo) y el resultado
 * se entrega en el hilo principal mediante un Handler.
 */
public class ServicioLocalRepository {

    private static final String TAG = "ServicioLocalRepo";

    private static final String ARCHIVO_ACTIVO = "servicio_activo.json";
    private static final String ARCHIVO_HISTORIAL = "historial_servicios.json";

    // Un solo hilo: las escrituras se ejecutan en orden y no se pisan entre sí
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public interface Callback<T> {
        void onResult(T resultado);
    }

    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ServicioLocalRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    // ---------- Servicio activo ----------

    public void guardarServicioActivo(ServicioTaxi servicio, Callback<Void> callback) {
        EXECUTOR.execute(() -> {
            try {
                escribir(ARCHIVO_ACTIVO, servicio.toJson().toString());
            } catch (JSONException e) {
                Log.e(TAG, "No se pudo convertir el servicio a JSON", e);
            }
            entregar(callback, null);
        });
    }

    /** Devuelve null si no hay servicio en curso. */
    public void obtenerServicioActivo(Callback<ServicioTaxi> callback) {
        EXECUTOR.execute(() -> {
            String json = leer(ARCHIVO_ACTIVO);
            ServicioTaxi servicio = null;
            if (json != null) {
                try {
                    servicio = ServicioTaxi.fromJson(new JSONObject(json));
                } catch (JSONException | IllegalArgumentException e) {
                    Log.e(TAG, "servicio_activo.json inválido", e);
                }
            }
            entregar(callback, servicio);
        });
    }

    // ---------- Historial ----------

    public void obtenerHistorial(Callback<List<ServicioTaxi>> callback) {
        EXECUTOR.execute(() -> entregar(callback, leerHistorial()));
    }

    /** Agrega el servicio al historial (el más reciente primero) y borra el servicio activo. */
    public void finalizarServicio(ServicioTaxi servicio, Callback<Void> callback) {
        EXECUTOR.execute(() -> {
            List<ServicioTaxi> historial = leerHistorial();
            historial.add(0, servicio);
            try {
                JSONArray array = new JSONArray();
                for (ServicioTaxi s : historial) {
                    array.put(s.toJson());
                }
                escribir(ARCHIVO_HISTORIAL, array.toString());
            } catch (JSONException e) {
                Log.e(TAG, "No se pudo convertir el historial a JSON", e);
            }
            context.deleteFile(ARCHIVO_ACTIVO);
            entregar(callback, null);
        });
    }

    // ---------- Helpers de archivos ----------

    private List<ServicioTaxi> leerHistorial() {
        List<ServicioTaxi> lista = new ArrayList<>();
        String json = leer(ARCHIVO_HISTORIAL);
        if (json == null) return lista;
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                lista.add(ServicioTaxi.fromJson(array.getJSONObject(i)));
            }
        } catch (JSONException | IllegalArgumentException e) {
            Log.e(TAG, "historial_servicios.json inválido", e);
        }
        return lista;
    }

    private void escribir(String archivo, String contenido) {
        try (FileOutputStream fos = context.openFileOutput(archivo, Context.MODE_PRIVATE);
             Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            writer.write(contenido);
        } catch (IOException e) {
            Log.e(TAG, "No se pudo escribir " + archivo, e);
        }
    }

    private String leer(String archivo) {
        File file = new File(context.getFilesDir(), archivo);
        if (!file.exists()) return null;
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = context.openFileInput(archivo);
             Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int leidos;
            while ((leidos = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, leidos);
            }
        } catch (IOException e) {
            Log.e(TAG, "No se pudo leer " + archivo, e);
            return null;
        }
        return sb.toString();
    }

    private <T> void entregar(Callback<T> callback, T resultado) {
        if (callback != null) {
            mainHandler.post(() -> callback.onResult(resultado));
        }
    }
}
