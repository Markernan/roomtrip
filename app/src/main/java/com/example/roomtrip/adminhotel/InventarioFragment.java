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
import com.example.roomtrip.databinding.FragmentInventarioBinding;

public class InventarioFragment extends Fragment {

    private FragmentInventarioBinding binding;
    private AdminRepository repository;
    private boolean esPestaniaHabitaciones = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInventarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = AdminRepository.getInstance(requireContext());

        binding.rvInventario.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pestañas / Tabs
        binding.tabHabitaciones.setOnClickListener(v -> {
            esPestaniaHabitaciones = true;
            actualizarEstadoTabs(true);
            cargarHabitaciones();
        });
        binding.tabServicios.setOnClickListener(v -> {
            esPestaniaHabitaciones = false;
            actualizarEstadoTabs(false);
            cargarServicios();
        });

        // Recuperar filtro/búsqueda de SharedPreferences
        String busqueda = AdminSession.getFiltroHabitaciones(requireContext());
        if (!busqueda.isEmpty()) {
            // TODO: Aplicar filtro visual si hubiera un EditText de búsqueda
        }

        // Recuperar última pestaña visitada
        String ultima = AdminSession.getUltimaPestania(requireContext());
        esPestaniaHabitaciones = !"servicios".equals(ultima);

        // Estado inicial
        actualizarEstadoTabs(esPestaniaHabitaciones);
        if (esPestaniaHabitaciones) cargarHabitaciones(); else cargarServicios();

        // FAB / Botón Agregar
        binding.fabAdd.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.toast_inventario_agregar, Toast.LENGTH_SHORT).show()
        );
    }

    private void actualizarEstadoTabs(boolean esHabitaciones) {
        int colorGris = ContextCompat.getColor(requireContext(), R.color.text_gray);
        AdminSession.guardarUltimaPestania(requireContext(), esHabitaciones ? "habitaciones" : "servicios");

        if (esHabitaciones) {
            binding.tabHabitaciones.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.tabHabitaciones.setTextColor(Color.WHITE);
            binding.tabServicios.setBackgroundResource(R.drawable.bg_tab_unselected);
            binding.tabServicios.setTextColor(colorGris);
        } else {
            binding.tabServicios.setBackgroundResource(R.drawable.bg_tab_selected);
            binding.tabServicios.setTextColor(Color.WHITE);
            binding.tabHabitaciones.setBackgroundResource(R.drawable.bg_tab_unselected);
            binding.tabHabitaciones.setTextColor(colorGris);
        }
    }

    private void cargarHabitaciones() {
        binding.rvInventario.setAdapter(new HabitacionAdapter(repository.getHabitaciones(), requireContext()));
    }

    private void cargarServicios() {
        binding.rvInventario.setAdapter(new ServicioAdapter(repository.getServicios(), requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
