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
import com.example.roomtrip.data.model.Traslado;
import java.util.List;

public class TrasladoAdapter extends RecyclerView.Adapter<TrasladoAdapter.ViewHolder> {

    private final List<Traslado> listaTraslados;
    private final Context context;

    public TrasladoAdapter(List<Traslado> listaTraslados, Context context) {
        this.listaTraslados = listaTraslados;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_traslado_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Traslado item = listaTraslados.get(position);

        if (holder.tvGuestName != null) {
            holder.tvGuestName.setText(item.getNombreHuesped() + " · Hab. " + item.getHabitacion());
        }
        if (holder.tvRoute != null) {
            holder.tvRoute.setText(item.getRuta());
        }
        if (holder.tvDriverAndPickup != null) {
            holder.tvDriverAndPickup.setText(item.getInfoConductor() + " • Hora: " + item.getHoraPickup());
        }
        if (holder.tvPrice != null) {
            holder.tvPrice.setText("S/ " + (int) item.getPrecio());
        }

        // Estilizado dinámico según el estado
        if (holder.tvStatus != null) {
            holder.tvStatus.setText("• " + item.getEstado());
            if ("En Curso".equalsIgnoreCase(item.getEstado())) {
                try {
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
                    holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text));
                } catch (Exception ignored) {}
            } else if ("Pendiente".equalsIgnoreCase(item.getEstado())) {
                try {
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
                    holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pill_red_text));
                } catch (Exception ignored) {}
            }
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Traslado de " + item.getNombreHuesped() + " (" + item.getEstado() + ")", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaTraslados != null ? listaTraslados.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvRoute, tvDriverAndPickup, tvStatus, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = findViewByName(itemView, "tvGuestName", "tv_guest_name", "tvNombreHuesped");
            tvRoute = findViewByName(itemView, "tvRoute", "tv_route", "tvRuta");
            tvDriverAndPickup = findViewByName(itemView, "tvDriverAndPickup", "tv_driver_and_pickup", "tvConductor");
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