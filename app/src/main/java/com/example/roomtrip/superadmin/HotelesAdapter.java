package com.example.roomtrip.superadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;
import java.util.ArrayList;
import java.util.List;

public class HotelesAdapter extends RecyclerView.Adapter<HotelesAdapter.HotelViewHolder> {

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    private final List<Hotel> originalList;
    private List<Hotel> filteredList;
    private final OnHotelClickListener listener;

    public HotelesAdapter(List<Hotel> hoteles, OnHotelClickListener listener) {
        this.originalList = hoteles;
        this.filteredList = new ArrayList<>(hoteles);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_superadmin_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = filteredList.get(position);
        holder.tvNombreHotel.setText(hotel.getNombre());
        holder.tvDireccionHotel.setText(hotel.getUbicacion());
        holder.tvRatingHotel.setText(String.valueOf(hotel.getCalificacion()));
        holder.tvAdminHotel.setText("Admin: " + (hotel.getAdminNombre() != null ? hotel.getAdminNombre() : "Sin asignar"));

        if (hotel.getImagenResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(hotel.getImagenResId())
                    .placeholder(R.drawable.ic_home_pin)
                    .into(holder.ivHotel);
        } else {
            holder.ivHotel.setImageResource(R.drawable.ic_home_pin);
        }

        boolean esActivo = "Activo".equalsIgnoreCase(hotel.getEstado());

        holder.switchActivoHotel.setOnCheckedChangeListener(null);
        holder.switchActivoHotel.setChecked(esActivo);

        holder.switchActivoHotel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hotel.setEstado(isChecked ? "Activo" : "Inactivo");
            String status = isChecked ? "activado" : "desactivado";
            Toast.makeText(buttonView.getContext(),
                    hotel.getNombre() + " ha sido " + status,
                    Toast.LENGTH_SHORT).show();
        });

        holder.btnMenuOpciones.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Opciones de " + hotel.getNombre(), Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHotelClick(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String query = text.toLowerCase().trim();
            for (Hotel hotel : originalList) {
                boolean coincideNombre = hotel.getNombre() != null && hotel.getNombre().toLowerCase().contains(query);
                boolean coincideUbicacion = hotel.getUbicacion() != null && hotel.getUbicacion().toLowerCase().contains(query);
                if (coincideNombre || coincideUbicacion) {
                    filteredList.add(hotel);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotel;
        TextView tvNombreHotel, tvDireccionHotel, tvRatingHotel, tvAdminHotel;
        SwitchCompat switchActivoHotel;
        ImageView btnMenuOpciones;

        HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHotel = itemView.findViewById(R.id.ivHotel);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvDireccionHotel = itemView.findViewById(R.id.tvDireccionHotel);
            tvRatingHotel = itemView.findViewById(R.id.tvRatingHotel);
            tvAdminHotel = itemView.findViewById(R.id.tvAdminHotel);
            switchActivoHotel = itemView.findViewById(R.id.switchActivoHotel);
            btnMenuOpciones = itemView.findViewById(R.id.btnMenuOpciones);
        }
    }
}