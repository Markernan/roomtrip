package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.databinding.FragmentInicioBinding;
import com.example.roomtrip.utils.PerfilDialogHelper;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentInicioBinding.bind(view);

        // --- 1. Perfil del Admin ---
        binding.cardPerfilAdmin.setOnClickListener(v ->
                PerfilDialogHelper.mostrarDialogoPerfil(requireActivity(), "Admin", "Admin Hotel", nuevoNombre -> {
                    // Nombre actualizado
                }));

        // --- 2. Navegaciones individuales ---
        // Botón Verde Superior "Completar configuración"
        enlazarNavegacion(binding.btnCompletarConfiguracion, R.id.action_inicioFragment_to_configuracionHotelFragment, null);

        // Tarjeta "Editar hotel" (Accesos rápidos)
        enlazarNavegacion(binding.cardEditarHotel, R.id.action_inicioFragment_to_configuracionHotelFragment, null);

        // Notificaciones
        enlazarNavegacion(binding.icNotifications, R.id.action_inicioFragment_to_notificacionesFragment, null);

        // Checkout María (Hab 204)
        Bundle argsMaria = new Bundle();
        argsMaria.putString("id_checkout", "204");
        enlazarNavegacion(binding.cardCheckoutMaria, R.id.action_inicioFragment_to_checkoutHabitacionFragment, argsMaria);

        // Checkout Carlos (Hab 308)
        Bundle argsCarlos = new Bundle();
        argsCarlos.putString("id_checkout", "308");
        enlazarNavegacion(binding.cardCheckoutCarlos, R.id.action_inicioFragment_to_checkoutHabitacionFragment, argsCarlos);

        // Traslados en Curso
        enlazarNavegacion(binding.cardTrasladosEnCurso, R.id.action_inicioFragment_to_trasladosEnCursoFragment, null);

        // Checkouts
        enlazarNavegacion(binding.cardCheckout, R.id.action_inicioFragment_to_checkoutsFragment, null);

        // Valoraciones
        enlazarNavegacion(binding.cardValoraciones, R.id.action_inicioFragment_to_valoracionesFragment, null);
    }

    private void enlazarNavegacion(View destino, int actionId, @Nullable Bundle args) {
        destino.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            NavDestination actual = navController.getCurrentDestination();
            if (actual != null && actual.getId() == R.id.inicioFragment) {
                navController.navigate(actionId, args);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
