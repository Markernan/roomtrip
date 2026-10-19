package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.R;
import com.example.roomtrip.databinding.ActivityHistorialServiciosBinding;
import com.example.roomtrip.taxista.data.ServicioLocalRepository;
import com.example.roomtrip.utils.NavegacionHelper;

/** Historial de servicios FINALIZADOS del taxista (se abre desde su perfil). */
public class HistorialServiciosActivity extends AppCompatActivity {

    private ActivityHistorialServiciosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistorialServiciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavegacionHelper.configurarBarra(this, "PERFIL");
        // "Trabajo" vuelve a la pantalla principal del taxista (no solo a Perfil)
        findViewById(R.id.navTrabajo).setOnClickListener(v -> {
            Intent intent = new Intent(this, SolicitadoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.navPerfil).setOnClickListener(v -> finish());

        binding.btnVolver.setOnClickListener(v -> finish());
        binding.rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Lectura del JSON en segundo plano; el callback llega en el hilo principal
        new ServicioLocalRepository(this).obtenerHistorial(historial -> {
            binding.tvVacio.setVisibility(historial.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvHistorial.setAdapter(new HistorialAdapter(historial));
        });
    }
}
