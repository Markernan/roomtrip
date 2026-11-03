package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.CobroAdicional;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.databinding.FragmentConfiguracionHotelBinding;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Configuración del hotel (Lab 6): ubicación, lugares históricos cercanos, monto mínimo para el taxi
 * gratis y alta de habitaciones. Se lee y se guarda en hoteles/{id}. Las fotos quedan pendientes
 * hasta definir Storage o Cloudinary.
 */
public class ConfiguracionHotelFragment extends Fragment {

    private static final String TAG = "ConfiguracionHotel";

    private FragmentConfiguracionHotelBinding binding;
    private final HotelRepository repositorio = new HotelRepository();
    private final List<String> lugares = new ArrayList<>();
    private List<Habitacion> habitaciones = new ArrayList<>();
    private String hotelId;

    public ConfiguracionHotelFragment() {
        super(R.layout.fragment_configuracion_hotel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentConfiguracionHotelBinding.bind(view);
        hotelId = AdminSession.getHotelId(requireContext());

        // Botón Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.chipAgregarLugar.setOnClickListener(v -> pedirLugar());
        binding.btnAgregarHabitacion.setOnClickListener(v -> {
            if (!hotelId.isEmpty()) {
                DialogosInventario.agregarHabitacion(requireContext(), hotelId, precioMinimo(), this::cargarHabitaciones);
            }
        });

        // Botón Guardar
        binding.btnGuardar.setOnClickListener(v -> guardar());

        cargarHotel();
        cargarHabitaciones();
    }

    private void cargarHotel() {
        if (hotelId.isEmpty()) return;
        repositorio.obtener(hotelId, hotel -> {
            if (binding == null || hotel == null) return;
            binding.etUbicacionHotel.setText(hotel.getUbicacion());
            binding.etMontoMinimoTaxi.setText(String.format(Locale.US, "%.2f", hotel.getMontoMinimoTaxi()));
            lugares.clear();
            if (hotel.getLugaresCercanos() != null) lugares.addAll(hotel.getLugaresCercanos());
            mostrarLugares();
        }, this::mostrarError);
    }

    private void cargarHabitaciones() {
        if (hotelId.isEmpty()) return;
        repositorio.obtenerHabitaciones(hotelId, lista -> {
            if (binding == null) return;
            habitaciones = lista;
            binding.tvHabitacionesRegistradas.setText(getResources().getQuantityString(
                    R.plurals.config_hotel_habitaciones_registradas, lista.size(), lista.size()));
        }, this::mostrarError);
    }

    /** Un chip por lugar; la X lo quita de la lista (se guarda al tocar "Guardar"). */
    private void mostrarLugares() {
        binding.chipGroupLugares.removeAllViews();
        for (String lugar : lugares) {
            Chip chip = new Chip(requireContext());
            chip.setText(lugar);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                lugares.remove(lugar);
                mostrarLugares();
            });
            binding.chipGroupLugares.addView(chip);
        }
    }

    private void pedirLugar() {
        EditText campo = new EditText(requireContext());
        campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        campo.setHint(R.string.config_hotel_hint_lugar);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.config_hotel_agregar_lugar)
                .setView(campo)
                .setPositiveButton(R.string.inventario_guardar, (d, w) -> {
                    String lugar = campo.getText().toString().trim();
                    if (!lugar.isEmpty() && !lugares.contains(lugar)) {
                        lugares.add(lugar);
                        mostrarLugares();
                    }
                })
                .setNegativeButton(R.string.login_verificacion_cancelar, null)
                .show();
    }

    private void guardar() {
        if (hotelId.isEmpty()) return;
        String ubicacion = binding.etUbicacionHotel.getText().toString().trim();
        Double montoMinimo = CobroAdicional.parsearMonto(binding.etMontoMinimoTaxi.getText().toString());
        if (ubicacion.isEmpty()) {
            binding.etUbicacionHotel.setError(getString(R.string.cliente_campo_obligatorio));
            return;
        }
        if (montoMinimo == null || montoMinimo < 0) {
            binding.etMontoMinimoTaxi.setError(getString(R.string.cobro_error_monto_invalido));
            return;
        }
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("ubicacion", ubicacion);
        cambios.put("lugaresCercanos", new ArrayList<>(lugares));
        cambios.put("montoMinimoTaxi", montoMinimo);

        binding.btnGuardar.setEnabled(false);
        repositorio.actualizar(hotelId, cambios, ignorado -> {
            RegistroEventos.registrar(RegistroEventos.HOTEL_ACTUALIZADO, RolUsuario.ADMIN_HOTEL,
                    "Configuración del hotel actualizada");
            if (binding == null) return;
            Toast.makeText(requireContext(), R.string.toast_config_hotel_guardada, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(binding.getRoot()).navigateUp();
        }, e -> {
            if (binding != null) binding.btnGuardar.setEnabled(true);
            mostrarError(e);
        });
    }

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
