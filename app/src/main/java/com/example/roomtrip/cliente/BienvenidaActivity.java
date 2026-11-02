package com.example.roomtrip.cliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.login.LoginActivity;

/**
 * Antigua bienvenida / login del cliente. Desde el Lab 6 todos los roles inician sesión en
 * {@link LoginActivity} (FirebaseUI); esta pantalla solo redirige allí por si algo aún la abre.
 */
public class BienvenidaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
