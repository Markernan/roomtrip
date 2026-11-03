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
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.databinding.FragmentCheckoutsBinding;
import com.example.roomtrip.utils.ErroresFirebase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Checkouts que pidieron los clientes del hotel (Lab 6): se escuchan en tiempo real las reservas del
 * hotel con checkout PENDIENTE o PROCESADO. Al tocar uno se abre el cobro.
 */
public class CheckoutsFragment extends Fragment {

    private static final String TAG = "CheckoutsFragment";

    private FragmentCheckoutsBinding binding;
    private ListenerRegistration registro;
    private final List<Checkout> checkouts = new ArrayList<>();
    private CheckoutAdapter adapter;

    public CheckoutsFragment() {
        super(R.layout.fragment_checkouts);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCheckoutsBinding.bind(view);

        // Flecha Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new CheckoutAdapter(checkouts, requireContext(), checkout -> {
            Bundle args = new Bundle();
            args.putString("id_checkout", checkout.getReservaId());
            Navigation.findNavController(view).navigate(R.id.checkoutHabitacionFragment, args);
        });
        binding.rvCheckouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCheckouts.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        String hotelId = AdminSession.getHotelId(requireContext());
        if (hotelId.isEmpty()) return;
        registro = new ReservaRepository().escucharDeHotel(hotelId, reservas -> {
            checkouts.clear();
            // Primero los pendientes de cobro
            for (Reserva r : reservas) {
                if (r.getEstadoCheckout() != null && !r.getEstadoCheckout().estaProcesado()) checkouts.add(VistasAdmin.checkout(r));
            }
            for (Reserva r : reservas) {
                if (r.getEstadoCheckout() != null && r.getEstadoCheckout().estaProcesado()) checkouts.add(VistasAdmin.checkout(r));
            }
            adapter.notifyDataSetChanged();
        }, e -> {
            Log.e(TAG, "No se pudieron escuchar los checkouts", e);
            if (isAdded()) Toast.makeText(requireContext(), ErroresFirebase.mensaje(requireContext(), e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registro != null) {
            registro.remove();
            registro = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
