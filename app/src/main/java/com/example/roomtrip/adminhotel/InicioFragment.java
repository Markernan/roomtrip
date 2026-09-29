package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.utils.PerfilDialogHelper;

public class InicioFragment extends Fragment {

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Perfil del Admin ---
        View cardPerfilAdmin = findViewByName(view, "cardPerfilAdmin", "card_perfil_admin", "llPerfilAdmin", "cardPerfil");
        if (cardPerfilAdmin != null) {
            cardPerfilAdmin.setOnClickListener(v -> {
                if (getActivity() != null) {
                    PerfilDialogHelper.mostrarDialogoPerfil(getActivity(), "Admin", "Admin Hotel", nuevoNombre -> {
                        // Nombre actualizado
                    });
                }
            });
        }

        // --- 2. Navegaciones individuales ---
        // Botón Verde Superior "Completar configuración"
        enlazarNavegacion(view, R.id.action_inicioFragment_to_configuracionHotelFragment, "btnCompletarConfiguracion", "btn_completar_configuracion");

        // Tarjeta "Editar hotel" (Accesos rápidos)
        enlazarNavegacion(view, R.id.action_inicioFragment_to_configuracionHotelFragment, "cardEditarHotel", "card_editar_hotel");

        // Notificaciones
        enlazarNavegacion(view, R.id.action_inicioFragment_to_notificacionesFragment, "ic_notifications", "btnNotifications", "ivNotificaciones");

        // Checkout María
        enlazarNavegacion(view, R.id.action_inicioFragment_to_checkoutHabitacionFragment, "cardCheckoutMaria", "card_checkout_maria");

        // Traslados en Curso
        enlazarNavegacion(view, R.id.action_inicioFragment_to_trasladosEnCursoFragment, "cardTrasladosEnCurso", "card_traslados_en_curso");

        // Checkouts
        enlazarNavegacion(view, R.id.action_inicioFragment_to_checkoutsFragment, "cardCheckout", "card_checkout");

        // Valoraciones
        enlazarNavegacion(view, R.id.action_inicioFragment_to_valoracionesFragment, "cardValoraciones", "card_valoraciones");
    }

    private void enlazarNavegacion(View rootView, int actionId, String... possibleNames) {
        View targetView = findViewByName(rootView, possibleNames);
        if (targetView != null) {
            targetView.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(v).navigate(actionId);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al navegar: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findViewByName(View rootView, String... possibleNames) {
        if (getContext() == null) return null;
        String packageName = getContext().getPackageName();
        for (String name : possibleNames) {
            int id = getResources().getIdentifier(name, "id", packageName);
            if (id != 0) {
                View v = rootView.findViewById(id);
                if (v != null) return (T) v;
            }
        }
        return null;
    }
}