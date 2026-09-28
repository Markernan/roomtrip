package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.roomtrip.R;
import com.example.roomtrip.utils.PerfilDialogHelper;

public class InicioFragment extends Fragment {

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View cardPerfilAdmin = view.findViewById(R.id.cardPerfilAdmin);
        if (cardPerfilAdmin != null) {
            cardPerfilAdmin.setOnClickListener(v -> {
                if (getActivity() != null) {
                    PerfilDialogHelper.mostrarDialogoPerfil(getActivity(), "Admin", "Admin Hotel", nuevoNombre -> {
                        // Nombre actualizado
                    });
                }
            });
        }

        view.findViewById(R.id.ic_notifications).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_notificacionesFragment));

        view.findViewById(R.id.btnCompletarConfiguracion).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_configuracionHotelFragment));

        view.findViewById(R.id.cardCheckoutMaria).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_checkoutHabitacionFragment));

        view.findViewById(R.id.cardTrasladosEnCurso).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_trasladosEnCursoFragment));

        view.findViewById(R.id.cardCheckout).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_checkoutsFragment));

        view.findViewById(R.id.cardEditarHotel).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_configuracionHotelFragment));

        view.findViewById(R.id.cardValoraciones).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_inicioFragment_to_valoracionesFragment));
    }
}