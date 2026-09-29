package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
import com.example.roomtrip.data.model.Traslado;
import com.example.roomtrip.databinding.FragmentTrasladosEnCursoBinding;

import java.util.ArrayList;
import java.util.List;

public class TrasladosEnCursoFragment extends Fragment {

    private FragmentTrasladosEnCursoBinding binding;

    public TrasladosEnCursoFragment() {
        super(R.layout.fragment_traslados_en_curso);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentTrasladosEnCursoBinding.bind(view);

        // Flecha Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        List<Traslado> lista = new ArrayList<>();
        lista.add(new Traslado(
                "Carlos Ruiz",
                "308",
                "Hotel ➔ Aeropuerto Jorge Chávez",
                "14:30 PM",
                EstadoServicioTaxi.EN_TRASLADO,
                "Pedro V. (ABC-123)",
                60.0
        ));
        lista.add(new Traslado(
                "María Fernández",
                "Suite 2",
                "Terminal Cruz del Sur ➔ Hotel",
                "15:00 PM",
                EstadoServicioTaxi.SOLICITADO,
                "Asignando conductor...",
                45.0
        ));
        lista.add(new Traslado(
                "Elena Rostova",
                "402",
                "Hotel ➔ Centro Histórico",
                "16:15 PM",
                EstadoServicioTaxi.EN_CAMINO,
                "Jorge M. (XYZ-987)",
                35.0
        ));

        binding.layoutEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        binding.rvTraslados.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTraslados.setAdapter(new TrasladoAdapter(lista, requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
