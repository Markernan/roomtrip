package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.databinding.FragmentValoracionesBinding;
import java.util.ArrayList;
import java.util.List;

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
        binding.btnBack.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        List<Valoracion> lista = new ArrayList<>();
        lista.add(new Valoracion("Carlos Ruiz", "Hab. 308", "Hace 2 días", 5.0f, "Excelente atención y limpieza. La habitación estaba impecable y el personal fue muy amable."));
        lista.add(new Valoracion("María Fernández", "Suite Presidencial", "Hace 5 días", 4.5f, "Muy buena vista y comodidades. El desayuno podría mejorar un poco en variedad."));
        lista.add(new Valoracion("Juan Gómez", "Hab. Económica", "Hace 1 semana", 4.0f, "Relación calidad-precio muy justa. Volvería a hospedarme aquí."));

        binding.rvValoraciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvValoraciones.setAdapter(new ValoracionAdapter(lista));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
