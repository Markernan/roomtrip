package com.example.roomtrip.data.firebase;

import androidx.annotation.NonNull;

/**
 * Callbacks de los repositorios de Firebase. Firebase responde de forma asíncrona (Task) y siempre
 * en el hilo principal, así que estos callbacks pueden tocar la interfaz directamente.
 */
public final class Callbacks {

    private Callbacks() {
    }

    /** La operación terminó bien. {@code resultado} puede ser null cuando así lo documenta el método. */
    public interface Exito<T> {
        void onExito(T resultado);
    }

    /** La operación falló (sin conexión, permiso denegado por las reglas, etc.). */
    public interface Fallo {
        void onFallo(@NonNull Exception e);
    }
}
