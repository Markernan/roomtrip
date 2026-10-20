package com.example.roomtrip.superadmin;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.example.roomtrip.R;
import com.example.roomtrip.utils.PerfilDialogHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private CardView cardHotelesRegistrados, cardUsuariosTotales, cardHotelesActivos, cardTaxistasAprobar;
    private CardView cardGestionHoteles, cardGestionUsuarios, cardReporteGlobal, cardAuditoria;
    private BottomNavigationView bottomNav;

    private static final String CHANNEL_ID = "superadmin_alerts";
    private static final int NOTIFICATION_ID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_dashboard);

        // SharedPreferences - Guardado de estado de sesión
        SharedPreferences prefs = getSharedPreferences("SuperadminPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("role", "SUPERADMIN");
        editor.apply();

        // Registrar canal de notificaciones
        createNotificationChannel();
        verificarPermisoNotificaciones();

        // Panel de control y Gestión
        cardHotelesRegistrados = findViewById(R.id.cardHotelesRegistrados);
        cardUsuariosTotales = findViewById(R.id.cardUsuariosTotales);
        cardHotelesActivos = findViewById(R.id.cardHotelesActivos);
        cardTaxistasAprobar = findViewById(R.id.cardTaxistasAprobar);

        // Accesos rápidos
        cardGestionHoteles = findViewById(R.id.cardGestionHoteles);
        cardGestionUsuarios = findViewById(R.id.cardGestionUsuarios);
        cardReporteGlobal = findViewById(R.id.cardReporteGlobal);
        cardAuditoria = findViewById(R.id.cardAuditoria);

        bottomNav = findViewById(R.id.bottomNav);

        setupClickListeners();
        setupBottomNavigation();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alertas Superadmin";
            String description = "Canal para notificaciones de solicitudes y alertas del sistema";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void verificarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    public void lanzarNotificacionSolicitudTaxista() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                verificarPermisoNotificaciones();
                return;
            }
        }

        Intent intent = new Intent(this, TaxistasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://roomtrip-taxistas.web.app/pendientes"));
        PendingIntent webPendingIntent = PendingIntent.getActivity(this, 1, webIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nueva solicitud")
                .setContentText("Nueva solicitud de taxista por aprobar")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_web, "Ver en Web", webPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        View cardPerfilSuperAdmin = findViewById(R.id.cardPerfilSuperAdmin);
        if (cardPerfilSuperAdmin != null) {
            cardPerfilSuperAdmin.setOnClickListener(v -> {
                PerfilDialogHelper.mostrarDialogoPerfil(this, "SuperAdmin", "SuperAdmin", nuevoNombre -> {
                    TextView tvSaludo = findViewById(R.id.tvSaludoSuperAdmin);
                    if (tvSaludo != null) {
                        tvSaludo.setText("Hola, " + nuevoNombre);
                    }
                });
            });
        }

        // Redirecciones Panel
        cardHotelesRegistrados.setOnClickListener(v -> startActivity(new Intent(this, HotelesActivity.class)));
        cardUsuariosTotales.setOnClickListener(v -> startActivity(new Intent(this, UsuariosActivity.class)));
        cardHotelesActivos.setOnClickListener(v -> startActivity(new Intent(this, HotelesActivity.class)));
        cardTaxistasAprobar.setOnClickListener(v -> {
            lanzarNotificacionSolicitudTaxista();
            startActivity(new Intent(this, TaxistasActivity.class));
        });

        // Redirecciones Accesos rápidos
        cardGestionHoteles.setOnClickListener(v -> startActivity(new Intent(this, HotelesActivity.class)));
        cardGestionUsuarios.setOnClickListener(v -> startActivity(new Intent(this, UsuariosActivity.class)));
        cardReporteGlobal.setOnClickListener(v -> startActivity(new Intent(this, ReportesActivity.class)));
        cardAuditoria.setOnClickListener(v -> startActivity(new Intent(this, AuditoriaActivity.class)));
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_inicio);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_hoteles) {
                startActivity(new Intent(this, HotelesActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, UsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_auditoria) {
                startActivity(new Intent(this, AuditoriaActivity.class));
                return true;
            }
            return false;
        });
    }
}
