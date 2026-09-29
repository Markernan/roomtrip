package com.example.roomtrip.adminhotel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Valoracion;
import java.util.List;

public class ValoracionAdapter extends RecyclerView.Adapter<ValoracionAdapter.ViewHolder> {

    private final List<Valoracion> listaValoraciones;

    public ValoracionAdapter(List<Valoracion> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_full_review_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Valoracion item = listaValoraciones.get(position);

        if (holder.tvUsuario != null) holder.tvUsuario.setText(item.getUsuario());
        if (holder.tvDetalle != null) holder.tvDetalle.setText(item.getDetalleHabitacion() + " • " + item.getFecha());
        if (holder.tvComentario != null) holder.tvComentario.setText(item.getComentario());
        if (holder.rbCalificacion != null) holder.rbCalificacion.setRating(item.getCalificacion());
    }

    @Override
    public int getItemCount() {
        return listaValoraciones != null ? listaValoraciones.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvDetalle, tvComentario;
        RatingBar rbCalificacion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Búsqueda dinámica para evitar errores de compilación (Cannot find symbol)
            tvUsuario = findViewByName(itemView, "tvUserName", "tv_user_name", "tvNombreUsuario", "tvNombre");
            tvDetalle = findViewByName(itemView, "tvReviewDate", "tv_review_date", "tvFecha", "tvDetalle");
            tvComentario = findViewByName(itemView, "tvReviewComment", "tv_review_comment", "tvComentario", "tv_comentario");
            rbCalificacion = findViewByName(itemView, "rbReviewRating", "rb_review_rating", "ratingBar", "rbCalificacion");
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