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
import com.example.roomtrip.data.model.Servicio;
import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ViewHolder> {

    private final List<Servicio> listaServicios;
    private final Context context;

    public ServicioAdapter(List<Servicio> listaServicios, Context context) {
        this.listaServicios = listaServicios;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_servicio_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio item = listaServicios.get(position);

        if (holder.tvServiceName != null) {
            holder.tvServiceName.setText(item.getNombre());
        }
        if (holder.tvCategory != null) {
            holder.tvCategory.setText(item.getCategoria());
        }
        if (holder.tvPrice != null) {
            holder.tvPrice.setText(String.format("S/ %.2f", item.getPrecio()));
        }

        if (holder.tvStatus != null) {
            if (item.isDisponible()) {
                holder.tvStatus.setText("• Disponible");
                try {
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
                    holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text));
                } catch (Exception ignored) {}
            } else {
                holder.tvStatus.setText("• Agotado");
                try {
                    holder.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
                    holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pill_red_text));
                } catch (Exception ignored) {}
            }
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Servicio: " + item.getNombre() + " (S/ " + item.getPrecio() + ")", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaServicios != null ? listaServicios.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvCategory, tvPrice, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = findViewByName(itemView, "tvServiceName", "tv_service_name", "tvNombreServicio", "tvNombre");
            tvCategory = findViewByName(itemView, "tvCategory", "tv_category", "tvCategoria");
            tvPrice = findViewByName(itemView, "tvPrice", "tv_price", "tvPrecio");
            tvStatus = findViewByName(itemView, "tvStatus", "tv_status", "tvEstado");
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