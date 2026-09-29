package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Chat;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = listaChats.get(position);

        if (holder.tvGuestName != null) {
            holder.tvGuestName.setText(chat.getNombreHuesped() + " · Hab. " + chat.getHabitacion());
        }
        if (holder.tvLastMessage != null) {
            holder.tvLastMessage.setText(chat.getUltimoMensaje());
        }
        if (holder.tvTime != null) {
            holder.tvTime.setText(chat.getHora());
        }

        // Control del punto verde de mensajes no leídos
        if (holder.dotUnread != null) {
            if (chat.getMensajesSinLeer() > 0) {
                holder.dotUnread.setVisibility(View.VISIBLE);
            } else {
                holder.dotUnread.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Abriendo chat con " + chat.getNombreHuesped(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaChats != null ? listaChats.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvLastMessage, tvTime;
        View dotUnread;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = findViewByName(itemView, "tvGuestName", "tv_guest_name");
            tvLastMessage = findViewByName(itemView, "tvLastMessage", "tv_last_message");
            tvTime = findViewByName(itemView, "tvTime", "tv_time");
            dotUnread = findViewByName(itemView, "dotUnread", "dot_unread");
        }

        @SuppressWarnings("unchecked")
        private <T extends View> T findViewByName(View rootView, String... possibleNames) {
            String packageName = rootView.getContext().getPackageName();
            for (String name : possibleNames) {
                int id = rootView.getResources().getIdentifier(name, "id", packageName);
                if (id != 0) {
                    View v = rootView.findViewById(id);
                    if (v != null) return (T) v;
                }
            }
            return null;
        }
    }
}