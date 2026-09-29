package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.databinding.FragmentNotificacionesBinding;

import java.util.List;

public class NotificacionesFragment extends Fragment {

    private FragmentNotificacionesBinding binding;

    public NotificacionesFragment() {
        super(R.layout.fragment_notificaciones);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentNotificacionesBinding.bind(view);

        // Botón de regreso
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        List<Notificacion> lista = MockData.getNotificacionesEjemplo();

        binding.layoutEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotificaciones.setAdapter(new NotificacionAdapter(lista, requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
