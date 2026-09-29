package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Chat;
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

            List<Chat> lista = MockData.getChatsAdminEjemplo();

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