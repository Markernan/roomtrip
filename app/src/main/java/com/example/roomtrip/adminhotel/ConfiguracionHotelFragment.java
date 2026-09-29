package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.databinding.FragmentConfiguracionHotelBinding;

public class ConfiguracionHotelFragment extends Fragment {

    private FragmentConfiguracionHotelBinding binding;

    public ConfiguracionHotelFragment() {
        super(R.layout.fragment_configuracion_hotel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentConfiguracionHotelBinding.bind(view);

        // Botón Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Botón Guardar
        binding.btnGuardar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), R.string.toast_config_hotel_guardada, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
