package com.example.roomtrip.adminhotel;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.databinding.FragmentInventarioBinding;
import com.example.roomtrip.utils.ErroresFirebase;

import java.util.ArrayList;
import java.util.List;

/** Habitaciones y servicios del hotel, leídos de hoteles/{id}/habitaciones y /servicios (Lab 6). */
public class InventarioFragment extends Fragment {

    private static final String TAG = "InventarioFragment";

    private FragmentInventarioBinding binding;
    private final HotelRepository repositorio = new HotelRepository();
    private String hotelId;
    private boolean esPestaniaHabitaciones = true;
    private List<Habitacion> habitaciones = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInventarioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hotelId = AdminSession.getHotelId(requireContext());

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

        // Recuperar última pestaña visitada (SharedPreferences)
        String ultima = AdminSession.getUltimaPestania(requireContext());
        esPestaniaHabitaciones = !"servicios".equals(ultima);

        // Estado inicial
        actualizarEstadoTabs(esPestaniaHabitaciones);
        if (esPestaniaHabitaciones) cargarHabitaciones(); else cargarServicios();

        // FAB / Botón Agregar: abre el formulario de la pestaña actual
        binding.fabAdd.setOnClickListener(v -> {
            if (hotelId.isEmpty()) return;
            if (esPestaniaHabitaciones) {
                DialogosInventario.agregarHabitacion(requireContext(), hotelId, precioMinimo(), this::cargarHabitaciones);
            } else {
                DialogosInventario.agregarServicio(requireContext(), hotelId, this::cargarServicios);
            }
        });
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
        if (hotelId.isEmpty()) return;
        repositorio.obtenerHabitaciones(hotelId, lista -> {
            if (binding == null) return;   // la vista ya se destruyó
            habitaciones = lista;
            if (esPestaniaHabitaciones) {
                binding.rvInventario.setAdapter(new HabitacionAdapter(lista, requireContext()));
            }
        }, this::mostrarError);
    }

    private void cargarServicios() {
        if (hotelId.isEmpty()) return;
        repositorio.obtenerServicios(hotelId, lista -> {
            if (binding == null) return;
            if (!esPestaniaHabitaciones) {
                binding.rvInventario.setAdapter(new ServicioAdapter(lista, requireContext()));
            }
        }, this::mostrarError);
    }

    /** Tarifa más baja actual, para actualizar el "desde S/X noche" del hotel al agregar otra. */
    private double precioMinimo() {
        double minimo = 0;
        for (Habitacion h : habitaciones) {
            if (minimo == 0 || h.getPrecio() < minimo) minimo = h.getPrecio();
        }
        return minimo;
    }

    private void mostrarError(Exception e) {
        Log.e(TAG, "Error de Firestore", e);
        if (isAdded()) Toast.makeText(requireContext(), ErroresFirebase.mensaje(requireContext(), e), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
