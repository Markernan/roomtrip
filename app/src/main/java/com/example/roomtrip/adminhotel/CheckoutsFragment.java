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
import com.example.roomtrip.data.model.Checkout;
import java.util.ArrayList;
import java.util.List;

public class CheckoutsFragment extends Fragment {

    public CheckoutsFragment() {
        super(R.layout.fragment_checkouts);
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

        // RecyclerView Dinámico
        RecyclerView rvCheckouts = findViewByName(view, "rvCheckouts", "rv_checkouts", "rvListaCheckouts");

        if (rvCheckouts != null) {
            rvCheckouts.setLayoutManager(new LinearLayoutManager(getContext()));

            List<Checkout> lista = new ArrayList<>();
            lista.add(new Checkout("María González", "204", "11:00 AM", "Pendiente"));
            lista.add(new Checkout("Carlos Ruiz", "308", "09:30 AM", "Procesado", 450.0));
            lista.add(new Checkout("Ana López", "105", "Ayer, 12:00 PM", "Pendiente"));
            lista.add(new Checkout("Juan Pérez", "201", "Ayer, 10:15 AM", "Procesado", 320.0));

            if (getContext() != null) {
                CheckoutAdapter adapter = new CheckoutAdapter(lista, getContext());
                rvCheckouts.setAdapter(adapter);
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