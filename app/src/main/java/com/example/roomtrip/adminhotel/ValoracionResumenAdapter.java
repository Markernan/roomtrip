package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.databinding.ItemReviewCardBinding;

import java.util.List;

/** Tarjetas compactas de "Valoraciones recientes" en el reporte del admin. */
public class ValoracionResumenAdapter extends RecyclerView.Adapter<ValoracionResumenAdapter.ViewHolder> {

    private final List<Valoracion> valoraciones;

    public ValoracionResumenAdapter(List<Valoracion> valoraciones) {
        this.valoraciones = valoraciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemReviewCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Valoracion valoracion = valoraciones.get(position);
        Context context = holder.itemView.getContext();

        holder.binding.tvAvatarInitials.setText(valoracion.getInicialesUsuario());
        holder.binding.tvReviewerName.setText(valoracion.getUsuario());
        holder.binding.tvReviewComment.setText(context.getString(R.string.reporte_resena_comentario, valoracion.getComentario()));
        holder.binding.tvReviewRating.setText(context.getString(R.string.reporte_resena_calificacion, valoracion.getCalificacion()));
    }

    @Override
    public int getItemCount() {
        return valoraciones != null ? valoraciones.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemReviewCardBinding binding;

        ViewHolder(@NonNull ItemReviewCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
