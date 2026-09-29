package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Traslado;
import com.example.roomtrip.databinding.ItemTrasladoCardBinding;
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
        return new ViewHolder(ItemTrasladoCardBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Traslado item = listaTraslados.get(position);
        ItemTrasladoCardBinding b = holder.binding;

        b.tvGuestName.setText(item.getNombreHuesped() + " · Hab. " + item.getHabitacion());
        b.tvRoute.setText(item.getRuta());
        b.tvDriverAndPickup.setText(item.getInfoConductor() + " • Hora: " + item.getHoraPickup());
        b.tvPrice.setText("S/ " + (int) item.getPrecio());

        // Estilizado dinámico según el estado
        b.tvStatus.setText("• " + item.getEstado());
        if ("En Curso".equalsIgnoreCase(item.getEstado())) {
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text));
        } else if ("Pendiente".equalsIgnoreCase(item.getEstado())) {
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pill_red_text));
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
        final ItemTrasladoCardBinding binding;

        ViewHolder(@NonNull ItemTrasladoCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
