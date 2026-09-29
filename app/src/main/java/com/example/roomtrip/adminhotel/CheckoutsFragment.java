package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.databinding.FragmentCheckoutsBinding;
import java.util.ArrayList;
import java.util.List;

public class CheckoutsFragment extends Fragment {

    private FragmentCheckoutsBinding binding;

    public CheckoutsFragment() {
        super(R.layout.fragment_checkouts);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCheckoutsBinding.bind(view);

        // Flecha Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        List<Checkout> lista = new ArrayList<>();
        lista.add(new Checkout("María González", "204", "11:00 AM", "Pendiente"));
        lista.add(new Checkout("Carlos Ruiz", "308", "09:30 AM", "Procesado", 450.0));
        lista.add(new Checkout("Ana López", "105", "Ayer, 12:00 PM", "Pendiente"));
        lista.add(new Checkout("Juan Pérez", "201", "Ayer, 10:15 AM", "Procesado", 320.0));

        binding.rvCheckouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCheckouts.setAdapter(new CheckoutAdapter(lista, requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
