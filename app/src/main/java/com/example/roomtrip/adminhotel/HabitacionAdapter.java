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
import com.example.roomtrip.data.model.Habitacion;

import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.ViewHolder> {

    private final List<Habitacion> listaHabitaciones;
    private final Context context;

    public HabitacionAdapter(List<Habitacion> listaHabitaciones, Context context) {
        this.listaHabitaciones = listaHabitaciones;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habitacion hab = listaHabitaciones.get(position);
        holder.tvNombre.setText(hab.getTipo());
        holder.tvUbicacion.setText(context.getString(R.string.habitacion_capacidad, hab.getCapacidad()));
        holder.tvContacto.setText(context.getString(R.string.habitacion_precio_noche, hab.getPrecio()));
        holder.tvEstado.setText(hab.getEstado().getEtiqueta());

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, context.getString(R.string.toast_habitacion_detalle, hab.getTipo()), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return listaHabitaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion, tvContacto, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreHotel);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacionHotel);
            tvContacto = itemView.findViewById(R.id.tvContactoHotel);
            tvEstado = itemView.findViewById(R.id.tvEstadoHotel);
        }
    }
}