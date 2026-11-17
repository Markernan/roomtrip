package com.example.roomtrip.adminhotel;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.bumptech.glide.Glide;
import com.example.roomtrip.data.firebase.FotosRepository;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.CobroAdicional;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Hotel;
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
 * gratis, alta de habitaciones y fotos del hotel (Firebase Storage, mínimo {@link Hotel#FOTOS_MINIMAS}).
 * Se lee y se guarda en hoteles/{id}.
 */
public class ConfiguracionHotelFragment extends Fragment {

    private static final String TAG = "ConfiguracionHotel";

    private FragmentConfiguracionHotelBinding binding;
    private final HotelRepository repositorio = new HotelRepository();
    private final List<String> lugares = new ArrayList<>();
    private List<Habitacion> habitaciones = new ArrayList<>();
    private String hotelId;

    // Fotos (RF-AH-02): URLs de Storage; las quitadas se borran de Storage solo al guardar
    private final FotosRepository fotosRepo = new FotosRepository();
    private final List<String> fotos = new ArrayList<>();
    private final List<String> fotosQuitadas = new ArrayList<>();
    private int subidasPendientes;
    private final ActivityResultLauncher<PickVisualMediaRequest> selectorFotos =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(10), this::subirFotos);

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

        binding.btnAgregarFoto.setOnClickListener(v -> selectorFotos.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

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
            fotos.clear();
            if (hotel.getFotos() != null) fotos.addAll(hotel.getFotos());
            mostrarFotos();
        }, this::mostrarError);
    }

    /** Miniaturas con una X para quitar cada foto, y el contador "cargadas / mínimo". */
    private void mostrarFotos() {
        if (binding == null) return;
        binding.llFotos.removeAllViews();
        float d = getResources().getDisplayMetrics().density;
        int lado = Math.round(104 * d);
        for (String url : new ArrayList<>(fotos)) {
            FrameLayout celda = new FrameLayout(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(lado, lado);
            lp.setMarginEnd(Math.round(8 * d));
            celda.setLayoutParams(lp);

            ImageView foto = new ImageView(requireContext());
            foto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            foto.setBackgroundResource(R.drawable.hotel_placeholder);
            Glide.with(this).load(url).centerCrop().into(foto);
            celda.addView(foto, new FrameLayout.LayoutParams(lado, lado));

            TextView quitar = new TextView(requireContext());
            quitar.setText("✕");
            quitar.setTextColor(0xFFFFFFFF);
            quitar.setTextSize(14);
            quitar.setGravity(Gravity.CENTER);
            quitar.setBackgroundColor(0x99000000);
            quitar.setContentDescription(getString(R.string.config_hotel_foto_quitar));
            FrameLayout.LayoutParams qp = new FrameLayout.LayoutParams(Math.round(28 * d), Math.round(28 * d),
                    Gravity.TOP | Gravity.END);
            quitar.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.config_hotel_foto_quitar_pregunta)
                    .setPositiveButton(R.string.config_hotel_foto_quitar, (dlg, w) -> {
                        fotos.remove(url);
                        fotosQuitadas.add(url);
                        mostrarFotos();
                    })
                    .setNegativeButton(R.string.login_verificacion_cancelar, null)
                    .show());
            celda.addView(quitar, qp);
            binding.llFotos.addView(celda);
        }
        binding.tvFotosContador.setText(getString(R.string.config_hotel_fotos_contador, fotos.size(), Hotel.FOTOS_MINIMAS));
        binding.tvFotosContador.setTextColor(Hotel.tieneFotosSuficientes(fotos) ? 0xFF2E7D32 : 0xFFD32F2F);
    }

    /** Sube cada foto elegida a Storage (hoteles/{hotelId}/...) y la agrega a la lista cuando termina. */
    private void subirFotos(List<Uri> elegidas) {
        if (elegidas == null || elegidas.isEmpty() || hotelId.isEmpty() || binding == null) return;
        subidasPendientes += elegidas.size();
        actualizarSubida();
        for (Uri uri : elegidas) {
            fotosRepo.subirFotoHotel(requireContext(), hotelId, uri, url -> {
                subidasPendientes--;
                fotos.add(url);
                mostrarFotos();
                actualizarSubida();
            }, e -> {
                subidasPendientes--;
                Log.e(TAG, "No se pudo subir la foto", e);
                actualizarSubida();
                if (isAdded()) Toast.makeText(requireContext(),
                        getString(R.string.config_hotel_foto_error, ErroresFirebase.mensaje(requireContext(), e)),
                        Toast.LENGTH_LONG).show();
            });
        }
    }

    private void actualizarSubida() {
        if (binding == null) return;
        binding.progresoFotos.setVisibility(subidasPendientes > 0 ? View.VISIBLE : View.GONE);
        binding.btnAgregarFoto.setEnabled(subidasPendientes == 0);
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
        if (subidasPendientes > 0) {
            Toast.makeText(requireContext(), R.string.config_hotel_fotos_subiendo, Toast.LENGTH_SHORT).show();
            return;
        }
        // RF-AH-02: el formulario no permite guardar con menos de 4 fotos
        if (!Hotel.tieneFotosSuficientes(fotos)) {
            Toast.makeText(requireContext(),
                    getString(R.string.config_hotel_fotos_faltan, Hotel.FOTOS_MINIMAS, fotos.size()),
                    Toast.LENGTH_LONG).show();
            return;
        }
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("fotos", new ArrayList<>(fotos));
        cambios.put("ubicacion", ubicacion);
        cambios.put("lugaresCercanos", new ArrayList<>(lugares));
        cambios.put("montoMinimoTaxi", montoMinimo);

        binding.btnGuardar.setEnabled(false);
        repositorio.actualizar(hotelId, cambios, ignorado -> {
            for (String quitada : fotosQuitadas) fotosRepo.eliminar(quitada);
            fotosQuitadas.clear();
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
