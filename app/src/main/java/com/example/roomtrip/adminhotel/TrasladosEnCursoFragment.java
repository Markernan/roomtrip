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
import com.example.roomtrip.data.firebase.ServicioTaxiRepository;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.data.model.Traslado;
import com.example.roomtrip.databinding.FragmentTrasladosEnCursoBinding;
import com.example.roomtrip.utils.ErroresFirebase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Traslados al aeropuerto de los huéspedes del hotel (Lab 6): el estado del taxista se ve en tiempo
 * real (asignado, en camino, en traslado, finalizado) desde la colección serviciosTaxi.
 */
public class TrasladosEnCursoFragment extends Fragment {

    private static final String TAG = "TrasladosEnCurso";

    private FragmentTrasladosEnCursoBinding binding;
    private final List<Traslado> traslados = new ArrayList<>();
    private TrasladoAdapter adapter;
    private ListenerRegistration registro;

    public TrasladosEnCursoFragment() {
        super(R.layout.fragment_traslados_en_curso);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentTrasladosEnCursoBinding.bind(view);

        // Flecha Atrás
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new TrasladoAdapter(traslados, requireContext());
        binding.rvTraslados.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTraslados.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        String hotelId = AdminSession.getHotelId(requireContext());
        if (hotelId.isEmpty()) return;
        registro = new ServicioTaxiRepository().escucharDeHotel(hotelId, servicios -> {
            if (binding == null) return;
            traslados.clear();
            for (ServicioTaxi s : servicios) {
                if (s.getEstado() != null) traslados.add(VistasAdmin.traslado(requireContext(), s));
            }
            adapter.notifyDataSetChanged();
            binding.layoutEmptyState.setVisibility(traslados.isEmpty() ? View.VISIBLE : View.GONE);
        }, e -> {
            Log.e(TAG, "No se pudieron escuchar los traslados", e);
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
