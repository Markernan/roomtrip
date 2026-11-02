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
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.ValoracionHotel;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.data.model.IngresoServicio;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.databinding.FragmentReportesBinding;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class ReportesFragment extends Fragment {

    private static final String TAG = "ReportesFragment";
    private FragmentReportesBinding binding;
    private String textoPendienteExportar = "";
    private String periodoSeleccionado = "MENSUAL";
    private final List<Reserva> cobradas = new ArrayList<>();
    private List<IngresoServicio> ingresos = new ArrayList<>();

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

        binding.rvIngresosServicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvValoracionesRecientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        cargarDatos();

        // Configurar botones de pestañas
        binding.tabDiario.setOnClickListener(v -> seleccionarPeriodo("DIARIO"));
        binding.tabMensual.setOnClickListener(v -> seleccionarPeriodo("MENSUAL"));
        binding.tabAnual.setOnClickListener(v -> seleccionarPeriodo("ANUAL"));

        // Configurar botón de exportación SAF
        binding.btnExportarReporte.setOnClickListener(v -> prepararExportacion(ingresos));

        // Estado inicial
        seleccionarPeriodo("MENSUAL");
    }

    /** Lab 6: reservas cobradas y valoraciones del hotel, leídas de Firestore. */
    private void cargarDatos() {
        String hotelId = AdminSession.getHotelId(requireContext());
        if (hotelId.isEmpty()) return;
        ReservaRepository repositorio = new ReservaRepository();
        repositorio.obtenerDeHotel(hotelId, reservas -> {
            if (binding == null) return;
            cobradas.clear();
            for (Reserva r : reservas) {
                if (r.getEstadoCheckout() != null && r.getEstadoCheckout().estaProcesado()) cobradas.add(r);
            }
            seleccionarPeriodo(periodoSeleccionado);
        }, this::mostrarError);
        repositorio.obtenerValoraciones(hotelId, lista -> {
            if (binding == null) return;
            List<Valoracion> vista = new ArrayList<>();
            for (ValoracionHotel v : lista) {
                vista.add(v.aVista());
                if (vista.size() == 3) break;
            }
            binding.rvValoracionesRecientes.setAdapter(new ValoracionResumenAdapter(vista));
        }, this::mostrarError);
    }

    /**
     * Ingresos del periodo: el alojamiento y cada motivo de cobro adicional, ordenados de menor a
     * mayor (RF-REP-004, IngresoServicio.ordenarDeMenorAMayor).
     */
    private void calcularIngresos(String periodo) {
        LocalDate hoy = LocalDate.now();
        Map<String, Double> porConcepto = new LinkedHashMap<>();
        double total = 0;
        for (Reserva r : cobradas) {
            if (r.getFechaCobro() == null) continue;
            LocalDate dia = r.getFechaCobro().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean entra = "DIARIO".equals(periodo) ? dia.equals(hoy)
                    : "MENSUAL".equals(periodo) ? dia.getYear() == hoy.getYear() && dia.getMonth() == hoy.getMonth()
                    : dia.getYear() == hoy.getYear();
            if (!entra) continue;
            String alojamiento = getString(R.string.reportes_concepto_alojamiento);
            porConcepto.put(alojamiento, porConcepto.getOrDefault(alojamiento, 0.0) + r.getMontoTotal());
            if (r.getCobroAdicionalMonto() > 0) {
                String motivo = r.getCobroAdicionalMotivo() != null ? r.getCobroAdicionalMotivo()
                        : getString(R.string.reportes_concepto_adicional);
                porConcepto.put(motivo, porConcepto.getOrDefault(motivo, 0.0) + r.getCobroAdicionalMonto());
            }
            total += r.getMontoCobrado();
        }
        List<IngresoServicio> lista = new ArrayList<>();
        for (Map.Entry<String, Double> e : porConcepto.entrySet()) lista.add(new IngresoServicio(e.getKey(), e.getValue()));
        ingresos = IngresoServicio.ordenarDeMenorAMayor(lista);
        binding.rvIngresosServicios.setAdapter(new IngresoServicioAdapter(ingresos));
        binding.tvTotalAmount.setText(getString(R.string.superadmin_monto_soles, total));
    }

    private void mostrarError(Exception e) {
        Log.e(TAG, "Error de Firestore", e);
        if (isAdded()) Toast.makeText(requireContext(), ErroresFirebase.mensaje(requireContext(), e), Toast.LENGTH_LONG).show();
    }

    private void seleccionarPeriodo(String periodo) {
        periodoSeleccionado = periodo;
        if (binding == null) return;
        
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
                break;
            case "MENSUAL":
                binding.tabMensual.setBackgroundResource(bgActive);
                binding.tabMensual.setTextColor(colorActive);
                break;
            case "ANUAL":
                binding.tabAnual.setBackgroundResource(bgActive);
                binding.tabAnual.setTextColor(colorActive);
                break;
        }
        calcularIngresos(periodo);
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
