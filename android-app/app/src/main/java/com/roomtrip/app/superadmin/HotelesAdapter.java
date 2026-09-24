package com.roomtrip.app.superadmin;

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
import com.roomtrip.app.R;
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
        holder.tvNombreHotel.setText(hotel.getName());
        holder.tvDireccionHotel.setText(hotel.getLocation());
        holder.tvRatingHotel.setText(String.valueOf(hotel.getRating()));
        holder.tvAdminHotel.setText("Admin: " + hotel.getAdminName());

        // Use Glide to load the hotel photo
        if (hotel.getPhotoResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(hotel.getPhotoResId())
                    .placeholder(R.drawable.ic_home_pin)
                    .into(holder.ivHotel);
        } else {
            holder.ivHotel.setImageResource(R.drawable.ic_home_pin);
        }

        // Avoid triggering the listener while binding the state
        holder.switchActivoHotel.setOnCheckedChangeListener(null);
        holder.switchActivoHotel.setChecked(hotel.isActive());
        
        holder.switchActivoHotel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hotel.setActive(isChecked);
            String status = isChecked ? "activado" : "desactivado";
            Toast.makeText(buttonView.getContext(), 
                    hotel.getName() + " ha sido " + status, 
                    Toast.LENGTH_SHORT).show();
        });

        holder.btnMenuOpciones.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Opciones de " + hotel.getName(), Toast.LENGTH_SHORT).show();
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
                if (hotel.getName().toLowerCase().contains(query) || 
                    hotel.getLocation().toLowerCase().contains(query)) {
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