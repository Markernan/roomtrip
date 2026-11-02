package com.example.roomtrip.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.roomtrip.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * Traduce un error de Firebase a un mensaje para el usuario. El detalle técnico se registra con
 * {@code Log.e} en quien llama; aquí solo se elige el texto.
 */
public final class ErroresFirebase {

    private ErroresFirebase() {
    }

    @NonNull
    public static String mensaje(@NonNull Context context, @NonNull Exception e) {
        if (e instanceof FirebaseNetworkException) {
            return context.getString(R.string.error_firebase_sin_conexion);
        }
        if (e instanceof FirebaseFirestoreException) {
            switch (((FirebaseFirestoreException) e).getCode()) {
                case PERMISSION_DENIED:
                case UNAUTHENTICATED:
                    return context.getString(R.string.error_firebase_permiso);
                case UNAVAILABLE:
                case DEADLINE_EXCEEDED:
                    return context.getString(R.string.error_firebase_sin_conexion);
                default:
                    break;
            }
        }
        return context.getString(R.string.error_firebase_generico);
    }
}
