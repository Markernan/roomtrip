package com.example.roomtrip.superadmin;

import android.content.Context;
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

    private final Context context;
    private final List<Hotel> originalList;
    private List<Hotel> filteredList;
    private final OnHotelClickListener listener;

    public HotelesAdapter(Context context, List<Hotel> hoteles, OnHotelClickListener listener) {
        this.context = context;
        this.originalList = hoteles;
        this.filteredList = new ArrayList<>(hoteles);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_superadmin_hotel, parent, false);
        return new HotelViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = filteredList.get(position);
        holder.hotel = hotel;

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
        // El listener vive en el ViewHolder (constructor). No hay que quitarlo aquí: si se hace,
        // se pierde para siempre. Ignora este setChecked() porque el switch no está presionado.
        holder.switchActivoHotel.setChecked(esActivo);
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
        Hotel hotel;

        HotelViewHolder(@NonNull View itemView, OnHotelClickListener listener) {
            super(itemView);
            ivHotel = itemView.findViewById(R.id.ivHotel);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvDireccionHotel = itemView.findViewById(R.id.tvDireccionHotel);
            tvRatingHotel = itemView.findViewById(R.id.tvRatingHotel);
            tvAdminHotel = itemView.findViewById(R.id.tvAdminHotel);
            switchActivoHotel = itemView.findViewById(R.id.switchActivoHotel);
            btnMenuOpciones = itemView.findViewById(R.id.btnMenuOpciones);

            switchActivoHotel.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Solo cuenta el toque del usuario, no el setChecked() que hace onBindViewHolder
                if (!buttonView.isPressed()) return;
                if (hotel != null) {
                    hotel.setEstado(isChecked ? "Activo" : "Inactivo");
                    String status = isChecked ? "activado" : "desactivado";
                    Toast.makeText(buttonView.getContext(),
                            hotel.getNombre() + " ha sido " + status,
                            Toast.LENGTH_SHORT).show();
                }
            });

            btnMenuOpciones.setOnClickListener(v -> {
                if (hotel != null) {
                    Toast.makeText(v.getContext(), "Opciones de " + hotel.getNombre(), Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null && hotel != null) {
                    listener.onHotelClick(hotel);
                }
            });
        }
    }
}
