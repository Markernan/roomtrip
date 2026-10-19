package com.example.roomtrip.taxista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ItemHistorialServicioBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Historial de servicios finalizados leído desde historial_servicios.json */
public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private final List<ServicioTaxi> servicios;
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public HistorialAdapter(List<ServicioTaxi> servicios) {
        this.servicios = servicios;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistorialServicioBinding binding = ItemHistorialServicioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistorialViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        ServicioTaxi servicio = servicios.get(position);
        PedidoTaxi pedido = servicio.getPedido();
        Context context = holder.itemView.getContext();

        holder.binding.tvNombrePasajero.setText(pedido.getNombrePasajero());
        holder.binding.tvEstado.setText(servicio.getEstado().getEtiqueta());
        holder.binding.tvOrigen.setText(context.getString(R.string.pedido_origen, pedido.getOrigen()));
        holder.binding.tvDestino.setText(context.getString(R.string.pedido_destino, pedido.getDestino()));
        holder.binding.tvFecha.setText(formato.format(new Date(servicio.getFechaHora())));
    }

    @Override
    public int getItemCount() {
        return servicios != null ? servicios.size() : 0;
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        final ItemHistorialServicioBinding binding;

        HistorialViewHolder(@NonNull ItemHistorialServicioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
