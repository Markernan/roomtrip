package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Checkout;
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private final List<Checkout> listaCheckouts;
    private final Context context;

    public CheckoutAdapter(List<Checkout> listaCheckouts, Context context) {
        this.listaCheckouts = listaCheckouts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkout_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checkout item = listaCheckouts.get(position);

        if (holder.tvInitials != null) {
            holder.tvInitials.setText(item.getIniciales());
        }
        if (holder.tvGuestName != null) {
            holder.tvGuestName.setText(item.getNombreHuesped());
        }
        if (holder.tvRoomAndTime != null) {
            holder.tvRoomAndTime.setText("Hab. " + item.getHabitacion() + " • " + item.getHoraLimite());
        }
        if (holder.tvStatus != null) {
            holder.tvStatus.setText("• " + item.getEstado());
            if ("Procesado".equalsIgnoreCase(item.getEstado()) || "Completado".equalsIgnoreCase(item.getEstado())) {
                try {
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
                    int greenColor = ContextCompat.getColor(context, R.color.green_text);
                    holder.tvStatus.setTextColor(greenColor);
                } catch (Exception ignored) {}
            } else {
                try {
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
                    int redColor = ContextCompat.getColor(context, R.color.pill_red_text);
                    holder.tvStatus.setTextColor(redColor);
                } catch (Exception ignored) {}
            }
        }
        if (holder.tvPrice != null) {
            if (item.getMonto() > 0) {
                holder.tvPrice.setVisibility(View.VISIBLE);
                holder.tvPrice.setText("S/ " + (int) item.getMonto());
            } else {
                holder.tvPrice.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Checkout de " + item.getNombreHuesped() + " (Hab. " + item.getHabitacion() + ")", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaCheckouts != null ? listaCheckouts.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvGuestName, tvRoomAndTime, tvStatus, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = findViewByName(itemView, "tvInitials", "tv_initials", "tvAvatar");
            tvGuestName = findViewByName(itemView, "tvGuestName", "tv_guest_name", "tvNombreHuesped");
            tvRoomAndTime = findViewByName(itemView, "tvRoomAndTime", "tv_room_and_time", "tvHabitacion");
            tvStatus = findViewByName(itemView, "tvStatus", "tv_status", "tvEstado");
            tvPrice = findViewByName(itemView, "tvPrice", "tv_price", "tvMonto");
        }

        @SuppressWarnings("unchecked")
        private <T extends View> T findViewByName(View rootView, String... possibleNames) {
            String packageName = rootView.getContext().getPackageName();
            for (String name : possibleNames) {
                int id = rootView.getResources().getIdentifier(name, "id", packageName);
                if (id != 0) {
                    View v = rootView.findViewById(id);
                    if (v != null) return (T) v;
                }
            }
            return null;
        }
    }
}