package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.IngresoServicio;
import com.example.roomtrip.databinding.ItemServiceReportBinding;

import java.util.List;

/** Filas del reporte de ingresos por servicios adicionales; la barra es proporcional al mayor monto. */
public class IngresoServicioAdapter extends RecyclerView.Adapter<IngresoServicioAdapter.ViewHolder> {

    private final List<IngresoServicio> ingresos;
    private final double montoMaximo;

    public IngresoServicioAdapter(List<IngresoServicio> ingresos) {
        this.ingresos = ingresos;
        double maximo = 0;
        for (IngresoServicio ingreso : ingresos) {
            maximo = Math.max(maximo, ingreso.getMonto());
        }
        this.montoMaximo = maximo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemServiceReportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IngresoServicio ingreso = ingresos.get(position);
        Context context = holder.itemView.getContext();

        holder.binding.tvServiceName.setText(ingreso.getNombre());
        holder.binding.tvServiceAmount.setText(context.getString(R.string.reporte_monto, ingreso.getMonto()));
        holder.binding.progressService.setProgress(
                montoMaximo > 0 ? (int) Math.round(ingreso.getMonto() / montoMaximo * 100) : 0);
    }

    @Override
    public int getItemCount() {
        return ingresos != null ? ingresos.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemServiceReportBinding binding;

        ViewHolder(@NonNull ItemServiceReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
