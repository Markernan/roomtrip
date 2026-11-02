package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.databinding.FragmentInicioBinding;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.firebase.UsuarioRepository;
import com.example.roomtrip.utils.PerfilDialogHelper;
import com.example.roomtrip.utils.SesionFirebase;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;
    private String nombreAdmin = "Admin Hotel";

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentInicioBinding.bind(view);

        // --- 1. Perfil del Admin ---
        binding.cardPerfilAdmin.setOnClickListener(v ->
                PerfilDialogHelper.mostrarDialogoPerfil(requireActivity(), "Admin", nombreAdmin,
                        nuevoNombre -> nombreAdmin = nuevoNombre));
        cargarDatosHotel();

        // --- 2. Navegaciones individuales ---
        // Botón Verde Superior "Completar configuración"
        enlazarNavegacion(binding.btnCompletarConfiguracion, R.id.action_inicioFragment_to_configuracionHotelFragment, null);

        // Tarjeta "Editar hotel" (Accesos rápidos)
        enlazarNavegacion(binding.cardEditarHotel, R.id.action_inicioFragment_to_configuracionHotelFragment, null);

        // Notificaciones
        enlazarNavegacion(binding.icNotifications, R.id.action_inicioFragment_to_notificacionesFragment, null);

        // Lab 6: las tarjetas de checkout llevan a la lista real de checkouts (Firestore)
        enlazarNavegacion(binding.cardCheckoutMaria, R.id.action_inicioFragment_to_checkoutsFragment, null);
        enlazarNavegacion(binding.cardCheckoutCarlos, R.id.action_inicioFragment_to_checkoutsFragment, null);

        // Traslados en Curso
        enlazarNavegacion(binding.cardTrasladosEnCurso, R.id.action_inicioFragment_to_trasladosEnCursoFragment, null);

        // Checkouts
        enlazarNavegacion(binding.cardCheckout, R.id.action_inicioFragment_to_checkoutsFragment, null);

        // Valoraciones
        enlazarNavegacion(binding.cardValoraciones, R.id.action_inicioFragment_to_valoracionesFragment, null);
    }

    /** Nombre y dirección del hotel del administrador (hoteles/{id}) y su nombre (usuarios/{uid}). */
    private void cargarDatosHotel() {
        String hotelId = AdminSession.getHotelId(requireContext());
        binding.tvNombreHotelInicio.setText(AdminSession.getHotelNombre(requireContext()));
        if (!hotelId.isEmpty()) {
            new HotelRepository().obtener(hotelId, hotel -> {
                if (binding == null || hotel == null) return;
                binding.tvNombreHotelInicio.setText(hotel.getNombre());
                binding.tvDireccionHotelInicio.setText(hotel.getUbicacion());
            }, e -> Log.e("InicioFragment", "No se pudo leer el hotel", e));
        }
        String uid = SesionFirebase.uidActual();
        if (uid != null) {
            new UsuarioRepository().obtener(uid, perfil -> {
                if (perfil != null && !perfil.nombreCompleto().isEmpty()) nombreAdmin = perfil.nombreCompleto();
            }, e -> Log.e("InicioFragment", "No se pudo leer el perfil", e));
        }
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
