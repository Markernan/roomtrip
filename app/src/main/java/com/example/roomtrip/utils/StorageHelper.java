package com.example.roomtrip.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

/**
 * Helper para persistencia en archivos internos usando JSON (Gson).
 */
public final class StorageHelper {
    private static final String TAG = "StorageHelper";
    private static final Gson GSON = new Gson();

    private StorageHelper() {}

    public static void guardarJson(Context ctx, String archivo, Object data) {
        String json = GSON.toJson(data);
        try (FileOutputStream fos = ctx.openFileOutput(archivo, Context.MODE_PRIVATE);
             OutputStreamWriter w = new OutputStreamWriter(fos)) {
            w.write(json);
        } catch (IOException e) {
            Log.e(TAG, "Error al guardar " + archivo, e);
        }
    }

    /** Devuelve null si no existe, está vacío o está corrupto. */
    public static <T> T leerJson(Context ctx, String archivo, Class<T> tipo) {
        return leerJson(ctx, archivo, (Type) tipo);
    }

    /** Sobrecarga para soportar tipos genéricos (ej: List<Objeto>) usando TypeToken. */
    public static <T> T leerJson(Context ctx, String archivo, Type tipo) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(ctx.openFileInput(archivo)))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = r.readLine()) != null) sb.append(linea);
            return GSON.fromJson(sb.toString(), tipo);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | JsonParseException e) {
            Log.e(TAG, "Error al leer " + archivo, e);
            return null;
        }
    }
}
