package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.data.model.Notificacion;
import com.example.roomtrip.databinding.ItemNotificacionCardBinding;
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
        return new ViewHolder(ItemNotificacionCardBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion item = listaNotificaciones.get(position);
        ItemNotificacionCardBinding b = holder.binding;

        b.tvTitle.setText(item.getTitulo());
        b.tvMessage.setText(item.getMensaje());
        b.tvTime.setText(item.getHora());

        // Mostrar / Ocultar punto de no leído
        b.dotUnread.setVisibility(item.isLeida() ? View.GONE : View.VISIBLE);

        // Iconos según el tipo de alerta
        if (item.getTipo() != null) {
            switch (item.getTipo()) {
                case RESERVA:
                    b.ivIcon.setImageResource(android.R.drawable.ic_menu_my_calendar);
                    break;
                case PAGO:
                    b.ivIcon.setImageResource(android.R.drawable.ic_menu_save);
                    break;
                case CANCELACION:
                    b.ivIcon.setImageResource(android.R.drawable.ic_delete);
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
        final ItemNotificacionCardBinding binding;

        ViewHolder(@NonNull ItemNotificacionCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
