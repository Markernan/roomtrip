package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Notificacion;
import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    private final List<Notificacion> listaNotificaciones;
    private final Context context;

    public NotificacionAdapter(List<Notificacion> listaNotificaciones, Context context) {
        this.listaNotificaciones = listaNotificaciones;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notificacion_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion item = listaNotificaciones.get(position);

        if (holder.tvTitle != null) {
            holder.tvTitle.setText(item.getTitulo());
        }
        if (holder.tvMessage != null) {
            holder.tvMessage.setText(item.getMensaje());
        }
        if (holder.tvTime != null) {
            holder.tvTime.setText(item.getHora());
        }

        // Mostrar / Ocultar punto de no leído
        if (holder.dotUnread != null) {
            holder.dotUnread.setVisibility(item.isLeida() ? View.GONE : View.VISIBLE);
        }

        // Iconos según el tipo de alerta
        if (holder.ivIcon != null && item.getTipo() != null) {
            switch (item.getTipo()) {
                case RESERVA:
                    holder.ivIcon.setImageResource(android.R.drawable.ic_menu_my_calendar);
                    break;
                case PAGO:
                    holder.ivIcon.setImageResource(android.R.drawable.ic_menu_save);
                    break;
                case CANCELACION:
                    holder.ivIcon.setImageResource(android.R.drawable.ic_delete);
                    break;
            }
        }

        holder.itemView.setOnClickListener(v -> {
            item.setLeida(true);
            notifyItemChanged(position);
            Toast.makeText(context, item.getTitulo(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones != null ? listaNotificaciones.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivIcon;
        View dotUnread;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = findViewByName(itemView, "tvTitle", "tv_title", "tvTitulo");
            tvMessage = findViewByName(itemView, "tvMessage", "tv_message", "tvMensaje");
            tvTime = findViewByName(itemView, "tvTime", "tv_time", "tvHora");
            ivIcon = findViewByName(itemView, "ivIcon", "iv_icon", "imgIcon");
            dotUnread = findViewByName(itemView, "dotUnread", "dot_unread", "dotNotRead");
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