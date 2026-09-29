package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Chat;
import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        super(R.layout.fragment_chats);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvChats = view.findViewById(R.id.rvChats);
        View layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        if (rvChats != null) {
            rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

            List<Chat> lista = new ArrayList<>();
            lista.add(new Chat("Carlos Ruiz", "308", "¿A qué hora sirven el desayuno?", "10:30", 1));
            lista.add(new Chat("María Fernández", "Suite 2", "Necesito toallas extra, por favor.", "09:15", 1));
            lista.add(new Chat("Juan Gómez", "102", "Todo perfecto, gracias.", "Ayer", 0));
            lista.add(new Chat("Ana López", "205", "¿Tienen servicio de taxi al aeropuerto?", "Ayer", 0));

            // Si hay elementos, oculta la tarjeta de "No tienes conversaciones activas"
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
            }

            if (getContext() != null) {
                ChatListaAdapter adapter = new ChatListaAdapter(lista, getContext());
                rvChats.setAdapter(adapter);
            }
        }
    }
}