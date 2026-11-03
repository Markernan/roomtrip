package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoServicioTaxi;
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

        b.tvGuestName.setText(context.getString(R.string.adapter_huesped_hab, item.getNombreHuesped(), item.getHabitacion()));
        b.tvRoute.setText(item.getRuta());
        b.tvDriverAndPickup.setText(context.getString(R.string.adapter_conductor_hora, item.getInfoConductor(), item.getHoraPickup()));
        b.tvPrice.setText(context.getString(R.string.adapter_monto_entero, (int) item.getPrecio()));

        // Estilizado dinámico según el estado
        b.tvStatus.setText(context.getString(R.string.adapter_estado, item.getEstado().getEtiqueta()));
        if (item.getEstado() == EstadoServicioTaxi.SOLICITADO) {
            // Aún sin conductor: se ve como pendiente
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pill_red_text));
        } else {
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text));
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, context.getString(R.string.toast_traslado_detalle, item.getNombreHuesped(), item.getEstado().getEtiqueta()), Toast.LENGTH_SHORT).show()
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
