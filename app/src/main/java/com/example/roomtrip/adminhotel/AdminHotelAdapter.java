package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;

import java.util.List;

public class AdminHotelAdapter extends RecyclerView.Adapter<AdminHotelAdapter.HotelViewHolder> {

    private final List<Hotel> listaHoteles;
    private final Context context;

    public AdminHotelAdapter(List<Hotel> listaHoteles, Context context) {
        this.listaHoteles = listaHoteles;
        this.context = context;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel_admin, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = listaHoteles.get(position);
        holder.tvNombre.setText(hotel.getNombre());
        holder.tvUbicacion.setText(hotel.getUbicacion());
        holder.tvContacto.setText(hotel.getContacto());
        holder.tvEstado.setText(hotel.getEstado());

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Gestionando: " + hotel.getNombre(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaHoteles != null ? listaHoteles.size() : 0;
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion, tvContacto, tvEstado;
        View imgHotel;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreHotel);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacionHotel);
            tvContacto = itemView.findViewById(R.id.tvContactoHotel);
            tvEstado = itemView.findViewById(R.id.tvEstadoHotel);
            imgHotel = itemView.findViewById(R.id.imgHotel);
        }
    }
}