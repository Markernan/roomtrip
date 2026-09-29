package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.data.model.Notificacion.TipoNotificacion;
import com.example.roomtrip.databinding.FragmentNotificacionesBinding;

import java.util.ArrayList;
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
        binding.btnBack.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        List<Notificacion> lista = new ArrayList<>();
        lista.add(new Notificacion(
                "Nueva Reserva Confirmada",
                "Carlos Ruiz reservó la Habitación 308 del 12 al 15 de Octubre.",
                "Hace 10 min",
                TipoNotificacion.RESERVA,
                false
        ));
        lista.add(new Notificacion(
                "Pago Recibido (S/ 450)",
                "Se confirmó el pago por transferencia de María González (Hab. 204).",
                "Hace 1 hora",
                TipoNotificacion.PAGO,
                false
        ));
        lista.add(new Notificacion(
                "Reserva Cancelada",
                "El huésped Fernando Torres canceló la reserva #4021.",
                "Ayer, 18:30",
                TipoNotificacion.CANCELACION,
                true
        ));
        lista.add(new Notificacion(
                "Pago Confirmado (S/ 320)",
                "Pago recibido exitosamente para la reserva de Juan Pérez (Hab. 201).",
                "Ayer, 14:15",
                TipoNotificacion.PAGO,
                true
        ));

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
