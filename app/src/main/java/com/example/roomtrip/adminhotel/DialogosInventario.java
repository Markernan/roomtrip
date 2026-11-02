package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.HotelRepository;
import com.example.roomtrip.data.model.CobroAdicional;
import com.example.roomtrip.data.model.EstadoHabitacion;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.data.model.Servicio;
import com.example.roomtrip.databinding.DialogAgregarHabitacionBinding;
import com.example.roomtrip.databinding.DialogAgregarServicioBinding;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Diálogos del administrador de hotel para registrar habitaciones y servicios en Firestore. Los usan
 * Inventario y Configuración del hotel.
 */
public final class DialogosInventario {

    private static final String TAG = "DialogosInventario";

    /** Se llama cuando el elemento quedó guardado, para recargar la lista. */
    public interface AlGuardar {
        void listo();
    }

    private DialogosInventario() {
    }

    /** Registra una habitación: tipo, capacidad (adultos/niños), área en m² y precio por noche. */
    public static void agregarHabitacion(@NonNull Context context, @NonNull String hotelId, double precioMinimoActual,
                                         @NonNull AlGuardar alGuardar) {
        DialogAgregarHabitacionBinding b = DialogAgregarHabitacionBinding.inflate(LayoutInflater.from(context));
        AlertDialog dialogo = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.inventario_nueva_habitacion)
                .setView(b.getRoot())
                .setPositiveButton(R.string.inventario_guardar, null)  // se valida antes de cerrar
                .setNegativeButton(R.string.login_verificacion_cancelar, null)
                .show();
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String tipo = texto(b.etTipoHabitacion);
            String capacidad = texto(b.etCapacidadHabitacion);
            Double precio = CobroAdicional.parsearMonto(texto(b.etPrecioHabitacion));
            if (tipo.isEmpty()) {
                b.etTipoHabitacion.setError(context.getString(R.string.cliente_campo_obligatorio));
                return;
            }
            if (capacidad.isEmpty()) {
                b.etCapacidadHabitacion.setError(context.getString(R.string.cliente_campo_obligatorio));
                return;
            }
            if (precio == null || precio <= 0) {
                b.etPrecioHabitacion.setError(context.getString(R.string.cobro_error_monto_invalido));
                return;
            }
            Habitacion habitacion = new Habitacion(tipo, capacidad, precio, EstadoHabitacion.DISPONIBLE);
            habitacion.setAreaM2(entero(texto(b.etAreaHabitacion)));
            v.setEnabled(false);
            new HotelRepository().agregarHabitacion(hotelId, habitacion, precioMinimoActual, ignorado -> {
                RegistroEventos.registrar(RegistroEventos.HOTEL_ACTUALIZADO, RolUsuario.ADMIN_HOTEL,
                        "Habitación agregada: " + tipo);
                Toast.makeText(context, R.string.inventario_habitacion_guardada, Toast.LENGTH_SHORT).show();
                dialogo.dismiss();
                alGuardar.listo();
            }, e -> {
                v.setEnabled(true);
                Log.e(TAG, "No se pudo guardar la habitación", e);
                Toast.makeText(context, ErroresFirebase.mensaje(context, e), Toast.LENGTH_LONG).show();
            });
        });
    }

    /** Registra un servicio del hotel: nombre, descripción, precio (0 = sin costo). */
    public static void agregarServicio(@NonNull Context context, @NonNull String hotelId, @NonNull AlGuardar alGuardar) {
        DialogAgregarServicioBinding b = DialogAgregarServicioBinding.inflate(LayoutInflater.from(context));
        AlertDialog dialogo = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.inventario_nuevo_servicio)
                .setView(b.getRoot())
                .setPositiveButton(R.string.inventario_guardar, null)
                .setNegativeButton(R.string.login_verificacion_cancelar, null)
                .show();
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = texto(b.etNombreServicio);
            String textoPrecio = texto(b.etPrecioServicio);
            // Un servicio puede ser gratuito (wifi): precio vacío o 0
            double precio = 0;
            if (!textoPrecio.isEmpty() && !textoPrecio.equals("0")) {
                Double p = CobroAdicional.parsearMonto(textoPrecio);
                if (p == null || p < 0) {
                    b.etPrecioServicio.setError(context.getString(R.string.cobro_error_monto_invalido));
                    return;
                }
                precio = p;
            }
            if (nombre.isEmpty()) {
                b.etNombreServicio.setError(context.getString(R.string.cliente_campo_obligatorio));
                return;
            }
            String categoria = texto(b.etCategoriaServicio);
            Servicio servicio = new Servicio(nombre, categoria.isEmpty() ? context.getString(R.string.inventario_categoria_general)
                    : categoria, precio, true);
            servicio.setDescripcion(texto(b.etDescripcionServicio));
            v.setEnabled(false);
            new HotelRepository().agregarServicio(hotelId, servicio, ignorado -> {
                RegistroEventos.registrar(RegistroEventos.HOTEL_ACTUALIZADO, RolUsuario.ADMIN_HOTEL,
                        "Servicio agregado: " + nombre);
                Toast.makeText(context, R.string.inventario_servicio_guardado, Toast.LENGTH_SHORT).show();
                dialogo.dismiss();
                alGuardar.listo();
            }, e -> {
                v.setEnabled(true);
                Log.e(TAG, "No se pudo guardar el servicio", e);
                Toast.makeText(context, ErroresFirebase.mensaje(context, e), Toast.LENGTH_LONG).show();
            });
        });
    }

    private static String texto(EditText campo) {
        return campo.getText() != null ? campo.getText().toString().trim() : "";
    }

    private static int entero(String texto) {
        try {
            return texto.isEmpty() ? 0 : Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
