package com.example.roomtrip.superadmin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Usuario;
import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    public interface OnUsuarioClickListener {
        void onUsuarioClick(Usuario usuario);
    }

    private final List<Usuario> usuarios;
    private final OnUsuarioClickListener listener;

    public UsuariosAdapter(List<Usuario> usuarios, OnUsuarioClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_superadmin_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario u = usuarios.get(position);
        holder.tvNombreUsuario.setText(u.getName());
        holder.tvEmailUsuario.setText(u.getEmail());
        holder.tvRol.setText(u.getRole());

        Context context = holder.itemView.getContext();
        int colorBg, colorText;

        switch (u.getRole().toLowerCase()) {
            case "admin":
            case "admin hotel":
                colorBg = Color.parseColor("#E3F2FD");
                colorText = Color.parseColor("#1976D2");
                break;

            case "taxista":
                colorBg = Color.parseColor("#FFF3E0");
                colorText = Color.parseColor("#E65100");
                break;

            case "cliente":
            default:
                colorBg = Color.parseColor("#E8F5E9");
                colorText = Color.parseColor("#2E7D32");
                break;
        }

        holder.tvRol.setBackgroundTintList(ColorStateList.valueOf(colorBg));
        holder.tvRol.setTextColor(colorText);

        holder.switchActivo.setOnCheckedChangeListener(null);
        holder.switchActivo.setChecked(u.isActive());

        holder.switchActivo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            u.setActive(isChecked);
            String status = isChecked ? "activado" : "desactivado";
            Toast.makeText(context, u.getName() + " ha sido " + status, Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUsuarioClick(u);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvEmailUsuario, tvRol;
        SwitchCompat switchActivo;

        UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmailUsuario = itemView.findViewById(R.id.tvEmailUsuario);
            tvRol = itemView.findViewById(R.id.tvRol);
            switchActivo = itemView.findViewById(R.id.switchActivo);
        }
    }
}