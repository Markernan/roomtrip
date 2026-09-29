package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.IngresoServicio;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.databinding.FragmentReportesBinding;

import java.util.List;

public class ReportesFragment extends Fragment {

    private FragmentReportesBinding binding;

    public ReportesFragment() {
        super(R.layout.fragment_reportes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentReportesBinding.bind(view);

        // RF-REP-004: los ingresos por servicios adicionales se muestran de menor a mayor monto
        binding.rvIngresosServicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvIngresosServicios.setAdapter(new IngresoServicioAdapter(
                IngresoServicio.ordenarDeMenorAMayor(MockData.getIngresosServiciosEjemplo())));

        // Las 3 valoraciones más recientes (la lista de ejemplo ya viene de la más nueva a la más antigua)
        List<Valoracion> valoraciones = MockData.getValoracionesEjemplo();
        binding.rvValoracionesRecientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvValoracionesRecientes.setAdapter(new ValoracionResumenAdapter(
                valoraciones.subList(0, Math.min(3, valoraciones.size()))));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
