package com.example.roomtrip.cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.EstadoCheckout;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.utils.ImagenesHotel;

import java.time.LocalDate;
import java.util.List;

/** Reservas del cliente (Lab 6: documentos de la colección reservas). */
public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    public interface OnAccionReservaListener {
        void onAccion(Reserva reserva);
    }

    private final List<Reserva> reservas;
    private final OnAccionReservaListener listenerContactar;
    private final OnAccionReservaListener listenerCheckout;
    private final OnAccionReservaListener listenerTaxi;

    public ReservaAdapter(List<Reserva> reservas,
                          OnAccionReservaListener listenerContactar,
                          OnAccionReservaListener listenerCheckout,
                          OnAccionReservaListener listenerTaxi) {
        this.reservas = reservas;
        this.listenerContactar = listenerContactar;
        this.listenerCheckout = listenerCheckout;
        this.listenerTaxi = listenerTaxi;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva_card, parent, false);
        return new ReservaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);
        Context context = holder.itemView.getContext();

        holder.tvNombreHotelReserva.setText(reserva.getHotelNombre());
        String estado = reserva.getEstado() != null ? reserva.getEstado().getEtiqueta() : "";
        holder.tvUbicacionReserva.setText(context.getString(R.string.cliente_reserva_resumen,
                reserva.getHabitacionTipo(), reserva.getFechaEntrada(), reserva.getFechaSalida(), estado));
        holder.ivFotoReserva.setImageResource(ImagenesHotel.recurso(reserva.getHotelImagen()));

        // RN-006: el chat solo existe con la reserva activa
        holder.btnContactar.setEnabled(reserva.chatDisponible());
        holder.btnContactar.setOnClickListener(v -> listenerContactar.onAccion(reserva));

        // Un solo botón según la etapa: checkout → seguimiento del taxi (si lo pidió) → finalizada
        if (reserva.getServicioTaxiId() != null) {
            holder.btnHacerCheckout.setText(R.string.cliente_ver_taxi);
            holder.btnHacerCheckout.setEnabled(true);
            holder.btnHacerCheckout.setOnClickListener(v -> listenerTaxi.onAccion(reserva));
            return;
        }
        if (reserva.getEstadoCheckout() == EstadoCheckout.PROCESADO) {
            holder.btnHacerCheckout.setText(R.string.cliente_reserva_finalizada);
            holder.btnHacerCheckout.setEnabled(false);
        } else if (reserva.getEstadoCheckout() == EstadoCheckout.PENDIENTE) {
            holder.btnHacerCheckout.setText(R.string.cliente_checkout_enviado);
            holder.btnHacerCheckout.setEnabled(false);
        } else {
            holder.btnHacerCheckout.setText(R.string.cliente_hacer_checkout);
            holder.btnHacerCheckout.setEnabled(reserva.puedeHacerCheckout(LocalDate.now()));
        }
        holder.btnHacerCheckout.setOnClickListener(v -> listenerCheckout.onAccion(reserva));
    }

    @Override
    public int getItemCount() {
        return reservas != null ? reservas.size() : 0;
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFotoReserva;
        TextView tvNombreHotelReserva, tvUbicacionReserva;
        Button btnContactar, btnHacerCheckout;

        ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFotoReserva = itemView.findViewById(R.id.ivFotoReserva);
            tvNombreHotelReserva = itemView.findViewById(R.id.tvNombreHotelReserva);
            tvUbicacionReserva = itemView.findViewById(R.id.tvUbicacionReserva);
            btnContactar = itemView.findViewById(R.id.btnContactar);
            btnHacerCheckout = itemView.findViewById(R.id.btnHacerCheckout);
        }
    }
}
