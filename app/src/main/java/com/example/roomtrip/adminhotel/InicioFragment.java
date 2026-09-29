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
        enlazarNavegacion(binding.btnCompletarConfiguracion, R.id.action_inicioFragment_to_configuracionHotelFragment);

        // Tarjeta "Editar hotel" (Accesos rápidos)
        enlazarNavegacion(binding.cardEditarHotel, R.id.action_inicioFragment_to_configuracionHotelFragment);

        // Notificaciones
        enlazarNavegacion(binding.icNotifications, R.id.action_inicioFragment_to_notificacionesFragment);

        // Checkout María
        enlazarNavegacion(binding.cardCheckoutMaria, R.id.action_inicioFragment_to_checkoutHabitacionFragment);

        // Traslados en Curso
        enlazarNavegacion(binding.cardTrasladosEnCurso, R.id.action_inicioFragment_to_trasladosEnCursoFragment);

        // Checkouts
        enlazarNavegacion(binding.cardCheckout, R.id.action_inicioFragment_to_checkoutsFragment);

        // Valoraciones
        enlazarNavegacion(binding.cardValoraciones, R.id.action_inicioFragment_to_valoracionesFragment);
    }

    private void enlazarNavegacion(View destino, int actionId) {
        destino.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            // Un doble toque rápido dispara la acción dos veces; la segunda ya no parte de Inicio
            // y navigate() lanzaría IllegalArgumentException. Solo navegamos si seguimos aquí.
            NavDestination actual = navController.getCurrentDestination();
            if (actual != null && actual.getId() == R.id.inicioFragment) {
                navController.navigate(actionId);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
