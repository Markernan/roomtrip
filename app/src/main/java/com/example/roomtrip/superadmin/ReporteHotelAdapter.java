package com.example.roomtrip.superadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Hotel;
import java.util.List;

public class ReporteHotelAdapter extends RecyclerView.Adapter<ReporteHotelAdapter.ViewHolder> {

    public interface OnReporteClickListener {
        void onReporteClick(Hotel hotel);
    }

    private final Context context;
    private final List<Hotel> hoteles;
    private final OnReporteClickListener listener;

    public ReporteHotelAdapter(Context context, List<Hotel> hoteles, OnReporteClickListener listener) {
        this.context = context;
        this.hoteles = hoteles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_superadmin_reporte_hotel, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hoteles.get(position);
        holder.hotel = hotel;
        holder.tvHotelName.setText(hotel.getNombre());

        if (hotel.getImagenResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(hotel.getImagenResId())
                    .placeholder(R.drawable.foto_hotel_miraflores)
                    .into(holder.ivHotelThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return hoteles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotelThumbnail;
        TextView tvHotelName, tvReservationsCount, tvAmount;
        ProgressBar pbPerformance;
        Hotel hotel;

        ViewHolder(@NonNull View itemView, OnReporteClickListener listener) {
            super(itemView);
            ivHotelThumbnail = itemView.findViewById(R.id.ivHotelThumbnail);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvReservationsCount = itemView.findViewById(R.id.tvReservationsCount);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            pbPerformance = itemView.findViewById(R.id.pbPerformance);

            itemView.setOnClickListener(v -> {
                if (listener != null && hotel != null) {
                    listener.onReporteClick(hotel);
                }
            });
        }
    }
}
