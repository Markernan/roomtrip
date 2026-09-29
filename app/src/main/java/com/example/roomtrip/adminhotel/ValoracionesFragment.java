package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Valoracion;
import java.util.ArrayList;
import java.util.List;

public class ValoracionesFragment extends Fragment {

    public ValoracionesFragment() {
        super(R.layout.fragment_valoraciones);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Flecha atrás
        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(v).navigateUp();
                } catch (Exception e) {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }

        // Configuración de RecyclerView
        RecyclerView rvValoraciones = view.findViewById(R.id.rvValoraciones);
        if (rvValoraciones != null) {
            rvValoraciones.setLayoutManager(new LinearLayoutManager(getContext()));

            List<Valoracion> lista = new ArrayList<>();
            lista.add(new Valoracion("Carlos Ruiz", "Hab. 308", "Hace 2 días", 5.0f, "Excelente atención y limpieza. La habitación estaba impecable y el personal fue muy amable."));
            lista.add(new Valoracion("María Fernández", "Suite Presidencial", "Hace 5 días", 4.5f, "Muy buena vista y comodidades. El desayuno podría mejorar un poco en variedad."));
            lista.add(new Valoracion("Juan Gómez", "Hab. Económica", "Hace 1 semana", 4.0f, "Relación calidad-precio muy justa. Volvería a hospedarme aquí."));

            ValoracionAdapter adapter = new ValoracionAdapter(lista);
            rvValoraciones.setAdapter(adapter);
        }
    }
}