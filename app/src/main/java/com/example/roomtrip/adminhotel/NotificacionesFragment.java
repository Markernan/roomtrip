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
import com.example.roomtrip.data.model.EstadoReserva;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.databinding.FragmentNotificacionesBinding;
import com.example.roomtrip.utils.ErroresFirebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Avisos del hotel (Lab 6) a partir de sus reservas en Firestore: reservas nuevas y alertas de
 * checkout que esperan el cobro.
 */
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
        binding.rvNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));

        String hotelId = AdminSession.getHotelId(requireContext());
        if (hotelId.isEmpty()) return;
        new ReservaRepository().obtenerDeHotel(hotelId, reservas -> {
            if (binding == null) return;
            List<Notificacion> lista = new ArrayList<>();
            for (Reserva r : reservas) {
                if (r.getEstado() == EstadoReserva.FINALIZADA || r.getEstado() == EstadoReserva.CANCELADA) continue;
                lista.add(VistasAdmin.notificacion(requireContext(), r));
            }
            binding.layoutEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvNotificaciones.setAdapter(new NotificacionAdapter(lista, requireContext()));
        }, e -> {
            Log.e("NotificacionesFragment", "No se pudieron leer las reservas", e);
            if (isAdded()) Toast.makeText(requireContext(), ErroresFirebase.mensaje(requireContext(), e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
