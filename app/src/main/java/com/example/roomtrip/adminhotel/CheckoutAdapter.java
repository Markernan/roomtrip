package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.databinding.ItemCheckoutCardBinding;
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    /** Lab 6: al tocar un checkout se abre su detalle para procesar el cobro. */
    public interface OnCheckoutClickListener {
        void onCheckoutClick(Checkout checkout);
    }

    private final List<Checkout> listaCheckouts;
    private final Context context;
    private final OnCheckoutClickListener listener;

    public CheckoutAdapter(List<Checkout> listaCheckouts, Context context, OnCheckoutClickListener listener) {
        this.listaCheckouts = listaCheckouts;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCheckoutCardBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checkout item = listaCheckouts.get(position);
        ItemCheckoutCardBinding b = holder.binding;

        b.tvInitials.setText(item.getIniciales());
        b.tvGuestName.setText(item.getNombreHuesped());
        b.tvRoomAndTime.setText(context.getString(R.string.adapter_hab_y_hora, item.getHabitacion(), item.getHoraLimite()));

        b.tvStatus.setText(context.getString(R.string.adapter_estado, item.getEstado().getEtiqueta()));
        if (item.getEstado().estaProcesado()) {
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_green);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green_text));
        } else {
            b.tvStatus.setBackgroundResource(R.drawable.bg_pill_red);
            b.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.pill_red_text));
        }

        if (item.getMonto() > 0) {
            b.tvPrice.setVisibility(View.VISIBLE);
            b.tvPrice.setText(context.getString(R.string.adapter_monto_entero, (int) item.getMonto()));
        } else {
            b.tvPrice.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onCheckoutClick(item));
    }

    @Override
    public int getItemCount() {
        return listaCheckouts != null ? listaCheckouts.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCheckoutCardBinding binding;

        ViewHolder(@NonNull ItemCheckoutCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
