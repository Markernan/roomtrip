package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Chat;
import com.example.roomtrip.databinding.ItemChatCardBinding;
import java.util.List;

public class ChatListaAdapter extends RecyclerView.Adapter<ChatListaAdapter.ViewHolder> {

    private final List<Chat> listaChats;
    private final Context context;

    public ChatListaAdapter(List<Chat> listaChats, Context context) {
        this.listaChats = listaChats;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemChatCardBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = listaChats.get(position);

        holder.binding.tvGuestName.setText(
                context.getString(R.string.adapter_huesped_hab, chat.getNombreHuesped(), chat.getHabitacion()));
        holder.binding.tvLastMessage.setText(chat.getUltimoMensaje());
        holder.binding.tvTime.setText(chat.getHora());

        // Control del punto verde de mensajes no leídos
        holder.binding.dotUnread.setVisibility(chat.getMensajesSinLeer() > 0 ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, context.getString(R.string.toast_chat_abriendo, chat.getNombreHuesped()), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaChats != null ? listaChats.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemChatCardBinding binding;

        ViewHolder(@NonNull ItemChatCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
