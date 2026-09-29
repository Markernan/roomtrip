package com.example.roomtrip.adminhotel;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Valoracion;
import com.example.roomtrip.databinding.ItemFullReviewCardBinding;
import java.util.List;

public class ValoracionAdapter extends RecyclerView.Adapter<ValoracionAdapter.ViewHolder> {

    private final List<Valoracion> listaValoraciones;

    public ValoracionAdapter(List<Valoracion> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemFullReviewCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Valoracion item = listaValoraciones.get(position);
        ItemFullReviewCardBinding b = holder.binding;

        b.tvAvatarInitials.setText(item.getInicialesUsuario());
        b.tvReviewerName.setText(item.getUsuario());
        b.tvReviewMeta.setText(holder.itemView.getContext().getString(
                R.string.adapter_valoracion_meta, item.getDetalleHabitacion(), item.getFecha()));
        b.tvStars.setText(estrellas(item.getCalificacion()));
        b.tvCommentText.setText(item.getComentario());
    }

    @Override
    public int getItemCount() {
        return listaValoraciones != null ? listaValoraciones.size() : 0;
    }

    /** 4.5 -> "★★★★★" (redondeo al entero más cercano, máximo 5). */
    private static String estrellas(float calificacion) {
        int llenas = Math.max(0, Math.min(5, Math.round(calificacion)));
        return "★".repeat(llenas) + "☆".repeat(5 - llenas);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFullReviewCardBinding binding;

        ViewHolder(@NonNull ItemFullReviewCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
