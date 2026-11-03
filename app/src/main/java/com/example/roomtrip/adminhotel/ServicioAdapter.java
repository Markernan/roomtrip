package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Servicio;
import com.example.roomtrip.databinding.ItemServicioCardBinding;
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
        return new ViewHolder(ItemServicioCardBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio item = listaServicios.get(position);
        ItemServicioCardBinding b = holder.binding;

        b.tvServiceName.setText(item.getNombre());
        b.tvCategory.setText(item.getCategoria());
        b.tvPrice.setText(context.getString(R.string.adapter_monto_decimales, item.getPrecio()));

        if (item.isDisponible()) {
            b.tvStatus.setText(R.string.servicio_estado_disponible);
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text));
        } else {
            b.tvStatus.setText(R.string.servicio_estado_agotado);
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pill_red_text));
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, context.getString(R.string.toast_servicio_detalle, item.getNombre(), item.getPrecio()), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaServicios != null ? listaServicios.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemServicioCardBinding binding;

        ViewHolder(@NonNull ItemServicioCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
