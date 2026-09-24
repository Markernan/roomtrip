package com.roomtrip.app.superadmin;

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
import com.roomtrip.app.R;
import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private final List<Usuario> usuarios;
    private final OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onUsuarioClick(Usuario usuario);
    }

    public UsuariosAdapter(List<Usuario> usuarios, OnUsuarioClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_superadmin_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario, listener);
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvRol;
        SwitchCompat switchActivo;
        Context context;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmail = itemView.findViewById(R.id.tvEmailUsuario);
            tvRol = itemView.findViewById(R.id.tvRol);
            switchActivo = itemView.findViewById(R.id.switchActivo);
        }

        public void bind(Usuario usuario, OnUsuarioClickListener listener) {
            tvNombre.setText(usuario.getName());
            tvEmail.setText(usuario.getEmail());
            tvRol.setText(usuario.getRole());
            
            // Cancel switch listener temporarily to avoid trigger during binding
            switchActivo.setOnCheckedChangeListener(null);
            switchActivo.setChecked(usuario.isActive());

            // Set role pill colors programmatically
            String role = usuario.getRole() != null ? usuario.getRole() : "";
            if (role.equalsIgnoreCase("Taxista")) {
                tvRol.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1F7F7")));
                tvRol.setTextColor(Color.parseColor("#18C0C1"));
            } else if (role.equalsIgnoreCase("Cliente")) {
                tvRol.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
                tvRol.setTextColor(Color.parseColor("#1976D2"));
            } else if (role.equalsIgnoreCase("Admin Hotel") || role.equalsIgnoreCase("Admin")) {
                tvRol.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFE0B2")));
                tvRol.setTextColor(Color.parseColor("#E65100"));
            } else {
                tvRol.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
                tvRol.setTextColor(Color.parseColor("#2E7D32"));
            }

            switchActivo.setOnCheckedChangeListener((buttonView, isChecked) -> {
                usuario.setActive(isChecked);
                String estado = isChecked ? "activo" : "inactivo";
                Toast.makeText(context, "Usuario " + usuario.getName() + " está ahora " + estado, Toast.LENGTH_SHORT).show();
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUsuarioClick(usuario);
                }
            });
        }
    }
}