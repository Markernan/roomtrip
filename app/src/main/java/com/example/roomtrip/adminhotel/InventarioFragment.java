package com.example.roomtrip.adminhotel;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.databinding.FragmentInventarioBinding;

public class InventarioFragment extends Fragment {

    private FragmentInventarioBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInventarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvInventario.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pestañas / Tabs
        binding.tabHabitaciones.setOnClickListener(v -> {
            actualizarEstadoTabs(true);
            cargarHabitacionesMock();
        });
        binding.tabServicios.setOnClickListener(v -> {
            actualizarEstadoTabs(false);
            cargarServiciosMock();
        });

        // Estado inicial (Habitaciones seleccionado)
        actualizarEstadoTabs(true);
        cargarHabitacionesMock();

        // FAB / Botón Agregar
        binding.fabAdd.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Agregar nuevo elemento", Toast.LENGTH_SHORT).show()
        );
    }

    private void actualizarEstadoTabs(boolean esHabitaciones) {
        int colorGris = ContextCompat.getColor(requireContext(), R.color.text_gray);

        if (esHabitaciones) {
            // Habitaciones Activo: Fondo turquesa + Texto Blanco
            binding.tabHabitaciones.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.tabHabitaciones.setTextColor(Color.WHITE);

            // Servicios Inactivo: Fondo neutro + Texto Gris
            binding.tabServicios.setBackgroundResource(R.drawable.bg_tab_unselected);
            binding.tabServicios.setTextColor(colorGris);
        } else {
            // Servicios Activo: Fondo turquesa + Texto Blanco
            binding.tabServicios.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.tabServicios.setTextColor(Color.WHITE);

            // Habitaciones Inactivo: Fondo neutro + Texto Gris
            binding.tabHabitaciones.setBackgroundResource(R.drawable.bg_tab_unselected);
            binding.tabHabitaciones.setTextColor(colorGris);
        }
    }

    private void cargarHabitacionesMock() {
        binding.rvInventario.setAdapter(new HabitacionAdapter(MockData.getHabitacionesEjemplo(), requireContext()));
    }

    private void cargarServiciosMock() {
        binding.rvInventario.setAdapter(new ServicioAdapter(MockData.getServiciosEjemplo(), requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
