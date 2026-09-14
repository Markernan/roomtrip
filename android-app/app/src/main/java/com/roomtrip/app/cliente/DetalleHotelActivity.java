package com.roomtrip.app.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.roomtrip.app.R;

/** Detalle de un hotel: opiniones, disponibilidad/reserva y servicios (RF-CL-02, RF-CL-03). */
public class DetalleHotelActivity extends AppCompatActivity {

    private View contenidoOpiniones, contenidoDetalles, contenidoServicios;
    private TextView tabOpiniones, tabDetalles, tabServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_hotel);

        String nombreHotel = getIntent().getStringExtra("nombreHotel");
        if (nombreHotel != null) {
            ((TextView) findViewById(R.id.tvNombreHotel)).setText(nombreHotel);
        }

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        contenidoOpiniones = findViewById(R.id.contenidoOpiniones);
        contenidoDetalles = findViewById(R.id.contenidoDetalles);
        contenidoServicios = findViewById(R.id.contenidoServicios);
        tabOpiniones = findViewById(R.id.tabOpiniones);
        tabDetalles = findViewById(R.id.tabDetalles);
        tabServicios = findViewById(R.id.tabServicios);

        tabOpiniones.setOnClickListener(v -> mostrarPestana(0));
        tabDetalles.setOnClickListener(v -> mostrarPestana(1));
        tabServicios.setOnClickListener(v -> mostrarPestana(2));
        mostrarPestana(0);

        contenidoDetalles.findViewById(R.id.btnReservar).setOnClickListener(v -> {
            Intent intent = new Intent(this, PagoReservaActivity.class);
            intent.putExtra("nombreHotel", nombreHotel);
            startActivity(intent);
        });

        cargarServicios();

        findViewById(R.id.navReservas).setOnClickListener(v ->
                startActivity(new Intent(this, MisReservasActivity.class)));
        findViewById(R.id.navPerfil).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));
        findViewById(R.id.navExplorar).setOnClickListener(v -> finish());
    }

    private void mostrarPestana(int indice) {
        contenidoOpiniones.setVisibility(indice == 0 ? View.VISIBLE : View.GONE);
        contenidoDetalles.setVisibility(indice == 1 ? View.VISIBLE : View.GONE);
        contenidoServicios.setVisibility(indice == 2 ? View.VISIBLE : View.GONE);

        activarPestana(tabOpiniones, indice == 0);
        activarPestana(tabDetalles, indice == 1);
        activarPestana(tabServicios, indice == 2);
    }

    private void activarPestana(TextView tab, boolean activa) {
        if (activa) {
            tab.setBackgroundResource(R.drawable.bg_boton_turquesa);
            tab.setTextColor(getColor(R.color.blanco));
        } else {
            tab.setBackground(null);
            tab.setTextColor(getColor(R.color.texto_secundario));
        }
    }

    /** RF-AH-04: nombre, descripción, precio, imágenes y si tiene costo. */
    private void cargarServicios() {
        LinearLayout contenedor = contenidoServicios.findViewById(R.id.contenedorServicios);
        agregarServicio(contenedor, R.drawable.ic_wifi, "Wi-Fi de Alta Velocidad",
                "Señal de alta velocidad en todo el hotel. Gratuito.");
        agregarServicio(contenedor, R.drawable.ic_breakfast, "Servicio de Desayuno (Opcional)",
                "Buffet de desayuno y servicio a la habitación.");
        agregarServicio(contenedor, R.drawable.ic_taxi, "Transporte al Aeropuerto",
                "Traslado gratis al alcanzar el monto mínimo de la reserva.");
        agregarServicio(contenedor, R.drawable.ic_spa, "Spa y Masajes",
                "Masaje de 60 minutos, aceite esencial al elegir servicio.");
    }

    private void agregarServicio(LinearLayout contenedor, int icono, String nombre, String descripcion) {
        View fila = LayoutInflater.from(this).inflate(R.layout.fila_servicio, contenedor, false);
        ((ImageView) fila.findViewById(R.id.ivIconoServicio)).setImageResource(icono);
        ((TextView) fila.findViewById(R.id.tvNombreServicio)).setText(nombre);
        ((TextView) fila.findViewById(R.id.tvDescripcionServicio)).setText(descripcion);
        contenedor.addView(fila);
    }
}
