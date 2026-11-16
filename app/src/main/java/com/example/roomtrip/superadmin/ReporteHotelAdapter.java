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
import java.util.Locale;

public class ReporteHotelAdapter extends RecyclerView.Adapter<ReporteHotelAdapter.ViewHolder> {

    public interface OnReporteClickListener {
        void onReporteClick(Hotel hotel);
    }

    private final Context context;
    private final List<Hotel> hoteles;
    private final OnReporteClickListener listener;
    private double maxMonto = 1.0;
    private double totalMonto = 0.0;

    public ReporteHotelAdapter(Context context, List<Hotel> hoteles, OnReporteClickListener listener) {
        this.context = context;
        this.hoteles = hoteles;
        this.listener = listener;
    }

    public void actualizarDatos(List<Hotel> nuevosHoteles, double maxM, double totalM) {
        this.hoteles.clear();
        this.hoteles.addAll(nuevosHoteles);
        this.maxMonto = maxM > 0 ? maxM : 1.0;
        this.totalMonto = totalM;
        notifyDataSetChanged();
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
        holder.tvReservationsCount.setText(context.getResources().getQuantityString(
                R.plurals.superadmin_reservas, hotel.getReservasReporte(), hotel.getReservasReporte()));
        holder.tvAmount.setText(context.getString(R.string.superadmin_monto_soles, hotel.getMontoReporte()));

        int progress = (int) ((hotel.getMontoReporte() / maxMonto) * 100);
        holder.pbPerformance.setProgress(Math.min(100, Math.max(0, progress)));

        double porcentajeTotal = totalMonto > 0 ? (hotel.getMontoReporte() / totalMonto) * 100 : 0.0;
        holder.tvPercentageTotal.setText(String.format(Locale.US, "%.0f%% del total", porcentajeTotal));

        if (hotel.getImagenResId() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(hotel.getImagenResId())
                    .placeholder(R.drawable.foto_hotel_miraflores)
                    .into(holder.ivHotelThumbnail);
        } else {
            holder.ivHotelThumbnail.setImageResource(R.drawable.foto_hotel_miraflores);
        }
    }

    @Override
    public int getItemCount() {
        return hoteles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotelThumbnail;
        TextView tvHotelName, tvReservationsCount, tvAmount, tvPercentageTotal;
        ProgressBar pbPerformance;
        Hotel hotel;

        ViewHolder(@NonNull View itemView, OnReporteClickListener listener) {
            super(itemView);
            ivHotelThumbnail = itemView.findViewById(R.id.ivHotelThumbnail);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvReservationsCount = itemView.findViewById(R.id.tvReservationsCount);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPercentageTotal = itemView.findViewById(R.id.tvPercentageTotal);
            pbPerformance = itemView.findViewById(R.id.pbPerformance);

            itemView.setOnClickListener(v -> {
                if (listener != null && hotel != null) {
                    listener.onReporteClick(hotel);
                }
            });
        }
    }
}
