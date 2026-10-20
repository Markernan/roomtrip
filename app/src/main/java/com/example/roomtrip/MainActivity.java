package com.example.roomtrip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.roomtrip.adminhotel.AdminNotificationHelper;
import com.example.roomtrip.adminhotel.AdminSession;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQ_NOTIFICATIONS = 101;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Contador de aperturas (solo si no es rotación)
        if (savedInstanceState == null) {
            int aperturas = AdminSession.incrementarAperturas(this);
            Log.d(TAG, "Apertura número: " + aperturas);
        }

        // Obtener el NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Conectar el BottomNavigationView con el NavController
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);
            }

            // Procesar Deep Links de notificaciones
            if (savedInstanceState == null) {
                manejarIntent(getIntent());
            }
        }

        // Inicializar canales de notificación
        new AdminNotificationHelper(this);

        // Pedir permiso de notificaciones si es necesario
        askNotificationPermission();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        manejarIntent(intent);
    }

    private void manejarIntent(Intent intent) {
        if (intent == null || navController == null) return;
        String deepLink = intent.getStringExtra("deep_link");
        if (deepLink == null) return;

        Bundle args = new Bundle();
        switch (deepLink) {
            case "checkout_cobro":
                String idCheckout = intent.getStringExtra("id_checkout");
                args.putString("id_checkout", idCheckout);
                navController.navigate(R.id.checkoutHabitacionFragment, args);
                break;
            case "chat_detalle":
                String reservaCode = intent.getStringExtra("reserva_code");
                args.putString("reserva_code", reservaCode);
                navController.navigate(R.id.chatsFragment, args);
                break;
            case "reserva_detalle":
                navController.navigate(R.id.notificacionesFragment);
                break;
        }
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                if (!AdminSession.isPermisoPedido(this)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIFICATIONS);
                    AdminSession.setPermisoPedido(this, true);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // Denegado permanentemente
                    new AlertDialog.Builder(this)
                            .setTitle("Activar notificaciones")
                            .setMessage("Las notificaciones están desactivadas. Actívalas en ajustes para recibir alertas de checkouts.")
                            .setPositiveButton("Ir a Ajustes", (d, w) -> {
                                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                startActivity(intent);
                            })
                            .setNegativeButton("Ahora no", null)
                            .show();
                } else {
                    Toast.makeText(this, "No recibirás alertas de checkouts", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
