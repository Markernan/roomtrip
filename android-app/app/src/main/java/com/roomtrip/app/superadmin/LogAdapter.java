package com.roomtrip.app.superadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.roomtrip.app.R;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogEvento> logs;

    public LogAdapter(List<LogEvento> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log_evento, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEvento log = logs.get(position);
        holder.tvTitulo.setText(log.getTitulo());
        holder.tvFechaHora.setText(log.getFecha() + " | " + log.getHora());
        holder.tvDispositivo.setText(log.getDetalle());
        
        if (log.getIconResId() != 0) {
            holder.ivIcon.setImageResource(log.getIconResId());
        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFechaHora, tvDispositivo;
        ImageView ivIcon;

        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvDescripcion);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvDispositivo = itemView.findViewById(R.id.tvDispositivo);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
