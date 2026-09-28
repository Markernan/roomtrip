package com.example.roomtrip.superadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.LogEvento;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private final List<LogEvento> logs;

    public LogAdapter(List<LogEvento> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_superadmin_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEvento log = logs.get(position);
        holder.tvLogEvent.setText(log.getTitulo());
        holder.tvLogDetail.setText(log.getDetalle());
        holder.tvLogTime.setText(log.getHora());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogEvent, tvLogDetail, tvLogTime;

        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogEvent = itemView.findViewById(R.id.tvLogEvent);
            tvLogDetail = itemView.findViewById(R.id.tvLogDetail);
            tvLogTime = itemView.findViewById(R.id.tvLogTime);
        }
    }
}