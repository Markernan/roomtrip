package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Traslado;
import com.example.roomtrip.databinding.FragmentTrasladosEnCursoBinding;

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

        List<Traslado> lista = MockData.getTrasladosEjemplo();

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
