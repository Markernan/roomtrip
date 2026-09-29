package com.example.roomtrip.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    public interface OnAccionReservaListener {
        void onAccion(Hotel hotel);
    }

    private final List<Hotel> reservas;
    private final OnAccionReservaListener listenerContactar;
    private final OnAccionReservaListener listenerCheckout;

    public ReservaAdapter(List<Hotel> reservas,
                          OnAccionReservaListener listenerContactar,
                          OnAccionReservaListener listenerCheckout) {
        this.reservas = reservas;
        this.listenerContactar = listenerContactar;
        this.listenerCheckout = listenerCheckout;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva_card, parent, false);
        return new ReservaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Hotel hotel = reservas.get(position);

        // Reemplazo de acceso directo a variables por getters
        holder.tvNombreHotelReserva.setText(hotel.getNombre());
        holder.tvUbicacionReserva.setText(hotel.getUbicacion());
        holder.ivFotoReserva.setImageResource(hotel.getImagenResId());

        holder.btnContactar.setOnClickListener(v -> listenerContactar.onAccion(hotel));
        holder.btnHacerCheckout.setOnClickListener(v -> listenerCheckout.onAccion(hotel));
    }

    @Override
    public int getItemCount() {
        return reservas != null ? reservas.size() : 0;
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFotoReserva;
        TextView tvNombreHotelReserva, tvUbicacionReserva;
        View btnContactar, btnHacerCheckout;

        ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFotoReserva = itemView.findViewById(R.id.ivFotoReserva);
            tvNombreHotelReserva = itemView.findViewById(R.id.tvNombreHotelReserva);
            tvUbicacionReserva = itemView.findViewById(R.id.tvUbicacionReserva);
            btnContactar = itemView.findViewById(R.id.btnContactar);
            btnHacerCheckout = itemView.findViewById(R.id.btnHacerCheckout);
        }
    }
}