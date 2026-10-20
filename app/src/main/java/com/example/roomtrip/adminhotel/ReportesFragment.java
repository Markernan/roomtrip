package com.example.roomtrip.adminhotel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.IngresoServicio;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.databinding.FragmentReportesBinding;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ReportesFragment extends Fragment {

    private static final String TAG = "ReportesFragment";
    private FragmentReportesBinding binding;
    private String textoPendienteExportar = "";
    private String periodoSeleccionado = "MENSUAL";

    private final ActivityResultLauncher<Intent> crearDoc = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() != Activity.RESULT_OK || r.getData() == null || r.getData().getData() == null) return;
                if (!isAdded()) return;
                
                try (ParcelFileDescriptor pfd = requireContext().getContentResolver().openFileDescriptor(r.getData().getData(), "w");
                     FileWriter w = new FileWriter(pfd.getFileDescriptor())) {
                    w.write(textoPendienteExportar);
                    Toast.makeText(requireContext(), "Reporte guardado con éxito", Toast.LENGTH_SHORT).show();
                    
                    // Notificación de reporte guardado
                    new AdminNotificationHelper(requireContext()).notificarReporteGuardado("reporte_" + periodoSeleccionado.toLowerCase() + ".txt");
                    
                } catch (IOException | NullPointerException e) {
                    Log.e(TAG, "Error al exportar", e);
                    Toast.makeText(requireContext(), "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
                }
            });

    public ReportesFragment() {
        super(R.layout.fragment_reportes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentReportesBinding.bind(view);

        List<IngresoServicio> ingresos = IngresoServicio.ordenarDeMenorAMayor(MockData.getIngresosServiciosEjemplo());
        
        binding.rvIngresosServicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvIngresosServicios.setAdapter(new IngresoServicioAdapter(ingresos));

        List<Valoracion> valoraciones = MockData.getValoracionesEjemplo();
        binding.rvValoracionesRecientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvValoracionesRecientes.setAdapter(new ValoracionResumenAdapter(
                valoraciones.subList(0, Math.min(3, valoraciones.size()))));

        // Configurar botones de pestañas
        binding.tabDiario.setOnClickListener(v -> seleccionarPeriodo("DIARIO"));
        binding.tabMensual.setOnClickListener(v -> seleccionarPeriodo("MENSUAL"));
        binding.tabAnual.setOnClickListener(v -> seleccionarPeriodo("ANUAL"));

        // Configurar botón de exportación SAF
        binding.btnExportarReporte.setOnClickListener(v -> prepararExportacion(ingresos));
        
        // Estado inicial
        seleccionarPeriodo("MENSUAL");
    }

    private void seleccionarPeriodo(String periodo) {
        periodoSeleccionado = periodo;
        
        // Resetear estilos
        binding.tabDiario.setBackground(null);
        binding.tabDiario.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        binding.tabMensual.setBackground(null);
        binding.tabMensual.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        binding.tabAnual.setBackground(null);
        binding.tabAnual.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

        int bgActive = R.drawable.bg_tab_active;
        int colorActive = ContextCompat.getColor(requireContext(), R.color.primary_teal);

        switch (periodo) {
            case "DIARIO":
                binding.tabDiario.setBackgroundResource(bgActive);
                binding.tabDiario.setTextColor(colorActive);
                binding.tvTotalAmount.setText(R.string.reportes_s_1_250_00);
                break;
            case "MENSUAL":
                binding.tabMensual.setBackgroundResource(bgActive);
                binding.tabMensual.setTextColor(colorActive);
                binding.tvTotalAmount.setText(R.string.reportes_s_12_450_00);
                break;
            case "ANUAL":
                binding.tabAnual.setBackgroundResource(bgActive);
                binding.tabAnual.setTextColor(colorActive);
                binding.tvTotalAmount.setText(R.string.reportes_s_150_800_00);
                break;
        }
    }

    private void prepararExportacion(List<IngresoServicio> ingresos) {
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DE INGRESOS (").append(periodoSeleccionado).append(") - ").append(LocalDate.now()).append("\n");
        sb.append("====================================================\n");
        sb.append("TOTAL: ").append(binding.tvTotalAmount.getText()).append("\n\n");
        sb.append("DESGLOSE POR SERVICIOS:\n");
        for (IngresoServicio i : ingresos) {
            sb.append(String.format("%-40s : S/ %.2f\n", i.getNombre(), i.getMonto()));
        }
        textoPendienteExportar = sb.toString();

        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TITLE, "reporte_" + periodoSeleccionado.toLowerCase() + "_" + LocalDate.now() + ".txt");
        crearDoc.launch(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
