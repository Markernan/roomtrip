package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Traslado;

import java.util.ArrayList;
import java.util.List;

public class TrasladosEnCursoFragment extends Fragment {

    public TrasladosEnCursoFragment() {
        super(R.layout.fragment_traslados_en_curso);
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

        RecyclerView rvTraslados = findViewByName(view, "rvTraslados", "rv_traslados", "rvListaTraslados");
        View layoutEmptyState = findViewByName(view, "layoutEmptyState", "emptyState", "llEmptyState");

        if (rvTraslados != null) {
            rvTraslados.setLayoutManager(new LinearLayoutManager(getContext()));

            List<Traslado> lista = new ArrayList<>();
            lista.add(new Traslado(
                    "Carlos Ruiz",
                    "308",
                    "Hotel ➔ Aeropuerto Jorge Chávez",
                    "14:30 PM",
                    "En Curso",
                    "Pedro V. (ABC-123)",
                    60.0
            ));
            lista.add(new Traslado(
                    "María Fernández",
                    "Suite 2",
                    "Terminal Cruz del Sur ➔ Hotel",
                    "15:00 PM",
                    "Pendiente",
                    "Asignando conductor...",
                    45.0
            ));
            lista.add(new Traslado(
                    "Elena Rostova",
                    "402",
                    "Hotel ➔ Centro Histórico",
                    "16:15 PM",
                    "En Curso",
                    "Jorge M. (XYZ-987)",
                    35.0
            ));

            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            }

            if (getContext() != null) {
                TrasladoAdapter adapter = new TrasladoAdapter(lista, getContext());
                rvTraslados.setAdapter(adapter);
            }
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