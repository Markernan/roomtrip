package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.data.model.Notificacion.TipoNotificacion;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    public NotificacionesFragment() {
        super(R.layout.fragment_notificaciones);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón de regreso
        View btnBack = findViewByName(view, "btnBack", "btn_back", "ivBack");
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(v).navigateUp();
                } catch (Exception e) {
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }

        RecyclerView rvNotificaciones = findViewByName(view, "rvNotificaciones", "rv_notificaciones", "rvListaNotificaciones");
        View layoutEmptyState = findViewByName(view, "layoutEmptyState", "emptyState", "llEmptyState");

        if (rvNotificaciones != null) {
            rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));

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

            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            }

            if (getContext() != null) {
                NotificacionAdapter adapter = new NotificacionAdapter(lista, getContext());
                rvNotificaciones.setAdapter(adapter);
            }
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