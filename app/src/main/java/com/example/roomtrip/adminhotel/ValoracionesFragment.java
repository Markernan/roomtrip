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
import com.example.roomtrip.databinding.FragmentValoracionesBinding;

public class ValoracionesFragment extends Fragment {

    private FragmentValoracionesBinding binding;

    public ValoracionesFragment() {
        super(R.layout.fragment_valoraciones);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentValoracionesBinding.bind(view);

        // Flecha atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.rvValoraciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvValoraciones.setAdapter(new ValoracionAdapter(MockData.getValoracionesEjemplo()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
