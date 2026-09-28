package com.example.roomtrip.cliente;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Mensaje;

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
        Mensaje msj = mensajes.get(position);
        holder.tvTextoMensaje.setText(msj.texto);
        holder.tvHoraMensaje.setText(msj.hora);

        LinearLayout.LayoutParams paramsTexto = (LinearLayout.LayoutParams) holder.tvTextoMensaje.getLayoutParams();
        LinearLayout.LayoutParams paramsHora = (LinearLayout.LayoutParams) holder.tvHoraMensaje.getLayoutParams();

        if (msj.esMio) {
            paramsTexto.gravity = Gravity.END;
            paramsHora.gravity = Gravity.END;
            holder.tvTextoMensaje.setBackgroundResource(R.drawable.bg_burbuja_enviada);
            holder.tvTextoMensaje.setTextColor(holder.itemView.getContext().getColor(R.color.blanco));
        } else {
            paramsTexto.gravity = Gravity.START;
            paramsHora.gravity = Gravity.START;
            holder.tvTextoMensaje.setBackgroundResource(R.drawable.bg_burbuja_recibida);
            holder.tvTextoMensaje.setTextColor(holder.itemView.getContext().getColor(R.color.texto_principal));
        }

        holder.tvTextoMensaje.setLayoutParams(paramsTexto);
        holder.tvHoraMensaje.setLayoutParams(paramsHora);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTextoMensaje, tvHoraMensaje;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTextoMensaje = itemView.findViewById(R.id.tvTextoMensaje);
            tvHoraMensaje = itemView.findViewById(R.id.tvHoraMensaje);
        }
    }
}