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

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    public interface OnVerDetallesListener {
        void onVerDetalles(Hotel hotel);
    }

    private final List<Hotel> hoteles;
    private final OnVerDetallesListener listener;

    public HotelAdapter(List<Hotel> hoteles, OnVerDetallesListener listener) {
        this.hoteles = hoteles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel_card, parent, false);
        return new HotelViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hoteles.get(position);
        holder.tvNombreHotel.setText(hotel.getNombre());
        holder.tvUbicacion.setText(hotel.getUbicacion());
        holder.tvPrecio.setText(hotel.getPrecio());
        holder.tvCalificacion.setText(String.valueOf(hotel.getCalificacion()));
        holder.ivFotoHotel.setImageResource(hotel.getImagenResId());
        holder.btnVerDetalles.setOnClickListener(v -> listener.onVerDetalles(hotel));
    }

    @Override
    public int getItemCount() {
        return hoteles != null ? hoteles.size() : 0;
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFotoHotel;
        TextView tvNombreHotel, tvUbicacion, tvPrecio, tvCalificacion;
        View btnVerDetalles;

        HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFotoHotel = itemView.findViewById(R.id.ivFotoHotel);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCalificacion = itemView.findViewById(R.id.tvCalificacion);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
        }
    }
}