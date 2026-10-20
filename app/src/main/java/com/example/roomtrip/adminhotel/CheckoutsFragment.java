package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.databinding.FragmentCheckoutsBinding;

public class CheckoutsFragment extends Fragment {

    private FragmentCheckoutsBinding binding;
    private AdminRepository repository;

    public CheckoutsFragment() {
        super(R.layout.fragment_checkouts);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = AdminRepository.getInstance(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCheckoutsBinding.bind(view);

        // Flecha Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Recuperar filtro de estado de sesión
        String filtroEstado = AdminSession.getFiltroReservaEstado(requireContext());
        // TODO: Aplicar el filtro a la lista del repositorio

        binding.rvCheckouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCheckouts.setAdapter(new CheckoutAdapter(repository.getCheckouts(), requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
