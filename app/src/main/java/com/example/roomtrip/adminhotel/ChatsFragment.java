package com.example.roomtrip.adminhotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.cliente.ChatActivity;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.Chat;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.utils.ErroresFirebase;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

/**
 * Chats con huéspedes (Lab 6): una conversación por cada reserva activa del hotel, desde la
 * confirmación hasta que se cobra el checkout (RN-006). Los mensajes viven en Firestore.
 */
public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    private final List<Chat> chats = new ArrayList<>();
    private ChatListaAdapter adapter;
    private View layoutEmptyState;
    private ListenerRegistration registro;

    public ChatsFragment() {
        super(R.layout.fragment_chats);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvChats = view.findViewById(R.id.rvChats);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListaAdapter(chats, requireContext(), chat -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_RESERVA_ID, chat.getReservaId());
            intent.putExtra(ChatActivity.EXTRA_COMO_HOTEL, true);
            intent.putExtra(ChatActivity.EXTRA_TITULO, chat.getNombreHuesped());
            startActivity(intent);
        });
        rvChats.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        String hotelId = AdminSession.getHotelId(requireContext());
        if (hotelId.isEmpty()) return;
        registro = new ReservaRepository().escucharDeHotel(hotelId, reservas -> {
            if (!isAdded()) return;
            chats.clear();
            for (Reserva r : reservas) {
                if (r.chatDisponible()) chats.add(VistasAdmin.chat(requireContext(), r));
            }
            adapter.notifyDataSetChanged();
            // Si hay conversaciones, se oculta la tarjeta de "No tienes conversaciones activas"
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(chats.isEmpty() ? View.VISIBLE : View.GONE);
        }, e -> {
            Log.e(TAG, "No se pudieron escuchar las reservas", e);
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
}
