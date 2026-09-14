package com.roomtrip.app.cliente;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomtrip.app.R;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private final List<Mensaje> mensajes;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.tvTexto.setText(mensaje.texto);
        holder.tvHora.setText(mensaje.hora);

        LinearLayout raiz = (LinearLayout) holder.itemView;
        if (mensaje.esMio) {
            raiz.setGravity(Gravity.END);
            holder.tvTexto.setBackgroundResource(R.drawable.bg_burbuja_enviada);
            holder.tvTexto.setTextColor(holder.itemView.getContext().getColor(R.color.blanco));
        } else {
            raiz.setGravity(Gravity.START);
            holder.tvTexto.setBackgroundResource(R.drawable.bg_burbuja_recibida);
            holder.tvTexto.setTextColor(holder.itemView.getContext().getColor(R.color.texto_principal));
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTexto, tvHora;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTexto = itemView.findViewById(R.id.tvTextoMensaje);
            tvHora = itemView.findViewById(R.id.tvHoraMensaje);
        }
    }
}
