package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.taxista.data.ServicioLocalRepository;
import com.example.roomtrip.taxista.data.SesionTaxistaManager;
import com.example.roomtrip.taxista.notificaciones.TaxiNotificationHelper;

public class EscaneoActivity extends AppCompatActivity {

    private Button btnEscanearQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo);

        btnEscanearQR = findViewById(R.id.btnEscanearQR);

        // "Validar QR" simulado: la lectura real con cámara se implementa en un lab posterior
        btnEscanearQR.setOnClickListener(v -> finalizarServicio());
    }

    /** EN_TRASLADO -> FINALIZADO (RN-011): pasa al historial, se borra el servicio activo y se cierra la notificación persistente. */
    private void finalizarServicio() {
        btnEscanearQR.setEnabled(false);
        ServicioLocalRepository repositorio = new ServicioLocalRepository(this);

        repositorio.obtenerServicioActivo(servicio -> {
            if (servicio == null || !servicio.getEstado().puedePasarA(EstadoServicioTaxi.FINALIZADO)) {
                Toast.makeText(this, R.string.toast_taxista_sin_servicio, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            servicio.avanzarEstado();
            servicio.setFechaHora(System.currentTimeMillis());

            repositorio.finalizarServicio(servicio, ignorado -> {
                new TaxiNotificationHelper(this).mostrarServicioFinalizado(servicio);
                new SesionTaxistaManager(this).setDisponible(true);

                startActivity(new Intent(EscaneoActivity.this, FinalizadoActivity.class));
                finish();
            });
        });
    }
}
