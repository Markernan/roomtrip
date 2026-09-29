package com.example.roomtrip.adminhotel;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Servicio;
import java.util.ArrayList;
import java.util.List;

public class InventarioFragment extends Fragment {

    private RecyclerView rvInventario;
    private HabitacionAdapter habitacionAdapter;
    private ServicioAdapter servicioAdapter;
    private List<Habitacion> listaHabitaciones;
    private List<Servicio> listaServicios;

    private TextView btnTabHabitaciones;
    private TextView btnTabServicios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Flecha Atrás
        View btnBack = findViewByName(view, "btnBack", "btn_back", "ivBack");
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(v).navigateUp();
                } catch (Exception e) {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }

        // RecyclerView
        rvInventario = findViewByName(view, "rvInventario", "rv_inventario", "rvHabitaciones", "rv_habitaciones");
        if (rvInventario != null) {
            rvInventario.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        // Pestañas / Tabs
        btnTabHabitaciones = findViewByName(view, "btnTabHabitaciones", "btn_tab_habitaciones", "tabHabitaciones", "tvTabHabitaciones");
        btnTabServicios = findViewByName(view, "btnTabServicios", "btn_tab_servicios", "tabServicios", "tvTabServicios");

        if (btnTabHabitaciones != null) {
            btnTabHabitaciones.setOnClickListener(v -> {
                actualizarEstadoTabs(true);
                cargarHabitacionesMock();
            });
        }

        if (btnTabServicios != null) {
            btnTabServicios.setOnClickListener(v -> {
                actualizarEstadoTabs(false);
                cargarServiciosMock();
            });
        }

        // Estado inicial (Habitaciones seleccionado)
        actualizarEstadoTabs(true);
        cargarHabitacionesMock();

        // FAB / Botón Agregar
        View fabAgregar = findViewByName(view, "fabAgregar", "fab_agregar", "btnAgregar");
        if (fabAgregar != null) {
            fabAgregar.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Agregar nuevo elemento", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void actualizarEstadoTabs(boolean esHabitaciones) {
        if (btnTabHabitaciones == null || btnTabServicios == null || getContext() == null) return;

        int colorGris = Color.parseColor("#6B7280");
        try {
            colorGris = ContextCompat.getColor(getContext(), R.color.text_gray);
        } catch (Exception ignored) {}

        if (esHabitaciones) {
            // Habitaciones Activo: Fondo turquesa + Texto Blanco
            btnTabHabitaciones.setBackgroundResource(R.drawable.bg_tab_selected);
            btnTabHabitaciones.setTextColor(Color.WHITE);

            // Servicios Inactivo: Fondo neutro + Texto Gris
            btnTabServicios.setBackgroundResource(R.drawable.bg_tab_unselected);
            btnTabServicios.setTextColor(colorGris);
        } else {
            // Servicios Activo: Fondo turquesa + Texto Blanco
            btnTabServicios.setBackgroundResource(R.drawable.bg_tab_selected);
            btnTabServicios.setTextColor(Color.WHITE);

            // Habitaciones Inactivo: Fondo neutro + Texto Gris
            btnTabHabitaciones.setBackgroundResource(R.drawable.bg_tab_unselected);
            btnTabHabitaciones.setTextColor(colorGris);
        }
    }

    private void cargarHabitacionesMock() {
        listaHabitaciones = new ArrayList<>();
        listaHabitaciones.add(new Habitacion("Habitación Standard", "2 Adultos", 120.0, "Disponible"));
        listaHabitaciones.add(new Habitacion("Suite Presidencial", "2 Adultos, 2 Niños", 350.0, "Ocupada"));
        listaHabitaciones.add(new Habitacion("Habitación Económica", "1 Adulto", 80.0, "Disponible"));
        listaHabitaciones.add(new Habitacion("Junior Suite", "2 Adultos, 1 Niño", 220.0, "Mantenimiento"));

        if (rvInventario != null && getContext() != null) {
            habitacionAdapter = new HabitacionAdapter(listaHabitaciones, getContext());
            rvInventario.setAdapter(habitacionAdapter);
        }
    }

    private void cargarServiciosMock() {
        listaServicios = new ArrayList<>();
        listaServicios.add(new Servicio("Desayuno Buffet Continental", "Alimentos y Bebidas", 35.00, true));
        listaServicios.add(new Servicio("Lavandería Express (x Prenda)", "Limpieza", 12.00, true));
        listaServicios.add(new Servicio("Masaje Relajante (45 min)", "Bienestar", 90.00, true));
        listaServicios.add(new Servicio("Tour Guiado Centro Histórico", "Tours", 60.00, false));

        if (rvInventario != null && getContext() != null) {
            servicioAdapter = new ServicioAdapter(listaServicios, getContext());
            rvInventario.setAdapter(servicioAdapter);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findViewByName(View rootView, String... possibleNames) {
        if (getContext() == null) return null;
        String packageName = getContext().getPackageName();
        for (String name : possibleNames) {
            int id = getResources().getIdentifier(name, "id", packageName);
            if (id != 0) {
                View v = rootView.findViewById(id);
                if (v != null) return (T) v;
            }
        }
        return null;
    }
}