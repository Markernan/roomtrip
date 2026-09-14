package com.roomtrip.app.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomtrip.app.R;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    public interface OnContactarListener {
        void onContactar(Hotel hotel);
    }

    public interface OnCheckoutListener {
        void onCheckout(Hotel hotel);
    }

    private final List<Hotel> reservas;
    private final OnContactarListener listenerContactar;
    private final OnCheckoutListener listenerCheckout;

    public ReservaAdapter(List<Hotel> reservas, OnContactarListener listenerContactar,
                           OnCheckoutListener listenerCheckout) {
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
        holder.tvNombre.setText(hotel.nombre);
        holder.tvUbicacion.setText(hotel.ubicacion);
        holder.ivFoto.setImageResource(hotel.fotoResId);
        holder.btnContactar.setOnClickListener(v -> listenerContactar.onContactar(hotel));
        holder.btnHacerCheckout.setOnClickListener(v -> listenerCheckout.onCheckout(hotel));
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre, tvUbicacion;
        View btnContactar, btnHacerCheckout;

        ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFotoReserva);
            tvNombre = itemView.findViewById(R.id.tvNombreHotelReserva);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacionReserva);
            btnContactar = itemView.findViewById(R.id.btnContactar);
            btnHacerCheckout = itemView.findViewById(R.id.btnHacerCheckout);
        }
    }
}
