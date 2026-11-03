package com.example.roomtrip.taxista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.ServicioTaxi;
import com.example.roomtrip.databinding.ItemPedidoTaxiBinding;

import java.util.List;

/** Lista de pedidos SOLICITADO que ve el taxista (RF-TAX-005), leídos en tiempo real de Firestore. */
public class PedidoTaxiAdapter extends RecyclerView.Adapter<PedidoTaxiAdapter.PedidoViewHolder> {

    public interface OnPedidoClickListener {
        void onPedidoClick(ServicioTaxi pedido);
    }

    private final List<ServicioTaxi> pedidos;
    private final OnPedidoClickListener listener;

    public PedidoTaxiAdapter(List<ServicioTaxi> pedidos, OnPedidoClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPedidoTaxiBinding binding = ItemPedidoTaxiBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PedidoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        ServicioTaxi pedido = pedidos.get(position);
        Context context = holder.itemView.getContext();

        holder.binding.tvNombrePasajero.setText(pedido.getClienteNombre());
        holder.binding.tvOrigen.setText(context.getString(R.string.pedido_origen, pedido.getHotelNombre()));
        holder.binding.tvDestino.setText(context.getString(R.string.pedido_destino, pedido.getAeropuerto()));
        holder.binding.tvMinutos.setText(context.getString(R.string.pedido_minutos, pedido.getMinutosAlEncuentro()));
        holder.itemView.setOnClickListener(v -> listener.onPedidoClick(pedido));
    }

    @Override
    public int getItemCount() {
        return pedidos != null ? pedidos.size() : 0;
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        final ItemPedidoTaxiBinding binding;

        PedidoViewHolder(@NonNull ItemPedidoTaxiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
