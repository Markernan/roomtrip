package com.roomtrip.app.superadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.roomtrip.app.R;
import java.util.List;

public class ReporteHotelAdapter extends RecyclerView.Adapter<ReporteHotelAdapter.ViewHolder> {

    public interface OnReporteClickListener {
        void onReporteClick(Hotel hotel);
    }

    private final List<Hotel> hoteles;
    private final OnReporteClickListener listener;

    public ReporteHotelAdapter(List<Hotel> hoteles, OnReporteClickListener listener) {
        this.hoteles = hoteles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_superadmin_reporte_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hoteles.get(position);
        holder.tvHotelName.setText(hotel.getName());
        
        // Mocking report data since it's not in the Hotel class
        int reservations = 150 + (position * 20);
        double amount = 5000 + (position * 1200);
        int progress = 60 + (position * 5);
        
        holder.tvReservationsCount.setText(reservations + " Reservas");
        holder.tvAmount.setText("S/ " + String.format("%,.0f", amount));
        holder.pbPerformance.setProgress(progress);

        if (hotel.getPhotoResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(hotel.getPhotoResId())
                    .placeholder(R.drawable.ic_home_pin)
                    .into(holder.ivHotelThumbnail);
        } else {
            holder.ivHotelThumbnail.setImageResource(R.drawable.ic_home_pin);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReporteClick(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hoteles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotelThumbnail;
        TextView tvHotelName, tvReservationsCount, tvAmount;
        ProgressBar pbPerformance;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHotelThumbnail = itemView.findViewById(R.id.ivHotelThumbnail);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvReservationsCount = itemView.findViewById(R.id.tvReservationsCount);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            pbPerformance = itemView.findViewById(R.id.pbPerformance);
        }
    }
}