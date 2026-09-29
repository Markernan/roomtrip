package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;

public class ConfiguracionHotelFragment extends Fragment {

    public ConfiguracionHotelFragment() {
        super(R.layout.fragment_configuracion_hotel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón Atrás
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

        // Botón Guardar
        View btnGuardar = findViewByName(view, "btnGuardar", "btn_guardar", "btnSave");
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Configuración del hotel guardada", Toast.LENGTH_SHORT).show();
                try {
                    Navigation.findNavController(v).navigateUp();
                } catch (Exception ignored) {}
            });
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