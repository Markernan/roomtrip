package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.data.model.ValoracionHotel;
import com.example.roomtrip.databinding.FragmentValoracionesBinding;
import com.example.roomtrip.utils.ErroresFirebase;

import java.util.ArrayList;
import java.util.List;

/** Valoraciones que dejaron los clientes en el checkout (colección valoraciones, Lab 6). */
public class ValoracionesFragment extends Fragment {

    private FragmentValoracionesBinding binding;

    public ValoracionesFragment() {
        super(R.layout.fragment_valoraciones);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentValoracionesBinding.bind(view);

        // Flecha atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.rvValoraciones.setLayoutManager(new LinearLayoutManager(requireContext()));

        String hotelId = AdminSession.getHotelId(requireContext());
        if (hotelId.isEmpty()) return;
        new ReservaRepository().obtenerValoraciones(hotelId, lista -> {
            if (binding == null) return;
            List<Valoracion> vista = new ArrayList<>();
            for (ValoracionHotel v : lista) vista.add(v.aVista());
            binding.rvValoraciones.setAdapter(new ValoracionAdapter(vista));
            if (vista.isEmpty()) {
                Toast.makeText(requireContext(), R.string.valoraciones_vacio, Toast.LENGTH_SHORT).show();
            }
        }, e -> {
            Log.e("ValoracionesFragment", "No se pudieron leer las valoraciones", e);
            if (isAdded()) Toast.makeText(requireContext(), ErroresFirebase.mensaje(requireContext(), e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
