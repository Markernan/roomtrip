package com.example.roomtrip.data.api;

import androidx.annotation.NonNull;

import com.example.roomtrip.data.firebase.Callbacks;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Único punto por donde las pantallas hablan con la API de taxistas ("toda llamada a taxi-service pasa por
 * {@code TaxiRepository}", {@code docs/arquitectura.md} sección 6). Sigue el mismo estilo que los
 * repositorios de Firebase: métodos asíncronos con {@link Callbacks.Exito} y {@link Callbacks.Fallo}.
 *
 * <p>En Android, Retrofit entrega los callbacks en el hilo principal, así que pueden tocar la interfaz.
 * Todo fallo llega a {@code Fallo} como {@link ApiException}: si {@link ApiException#esNoDisponible()}, la
 * pantalla muestra el aviso (RF-TAX-018) <b>y el resto de la app sigue funcionando</b>.
 *
 * <p>Las operaciones de taxistas se agrupan aquí; el pedido del taxi en tiempo real, los estados del
 * servicio y la ubicación siguen en Firestore (ver {@code docs/integracion-api-taxistas.md}).
 */
public class TaxiRepository {

    /** Observador opcional de los fallos, por ejemplo para registrarlos en los logs de auditoría. */
    public interface AlFallar {
        void onFallo(@NonNull ApiException error);
    }

    private final TaxiApiCliente cliente;
    private final AlFallar observador;

    public TaxiRepository(TaxiApiCliente cliente, AlFallar observador) {
        this.cliente = cliente;
        this.observador = observador;
    }

    /** Taxistas aprobados y disponibles (RF-API-001), por ejemplo para asignar un traslado. */
    public void taxistasDisponibles(Callbacks.Exito<List<TaxistaApi>> exito, Callbacks.Fallo fallo) {
        ejecutar(true, () -> cliente.api().taxistasDisponibles(), exito, fallo);
    }

    /**
     * Resuelve al taxista a partir del correo de su cuenta de Firebase. Falla con
     * {@link ApiException.Tipo#NO_ENCONTRADO} si ese correo no se registró en el portal de taxistas.
     */
    public void buscarPorCorreo(String correo, Callbacks.Exito<TaxistaApi> exito, Callbacks.Fallo fallo) {
        ejecutar(true, () -> cliente.api().buscarPorCorreo(correo), exito, fallo);
    }

    /** Datos del taxista asignado: foto, vehículo, placa y valoración. */
    public void obtenerTaxista(String id, Callbacks.Exito<TaxistaApi> exito, Callbacks.Fallo fallo) {
        ejecutar(true, () -> cliente.api().obtenerTaxista(id), exito, fallo);
    }

    /** {@link TaxistaApi#EN_SERVICIO} al asignar un traslado y {@link TaxistaApi#DISPONIBLE} al terminarlo. */
    public void cambiarDisponibilidad(String id, String estado, Callbacks.Exito<TaxistaApi> exito, Callbacks.Fallo fallo) {
        ejecutar(true, () -> cliente.api().cambiarDisponibilidad(id, new DisponibilidadApi(estado)), exito, fallo);
    }

    /** Califica al taxista al finalizar el servicio (RF-TAX-017). Un servicio solo se califica una vez. */
    public void calificar(String taxistaId, String servicioTaxiId, int puntuacion, String observacion,
                          Callbacks.Exito<CalificacionApi> exito, Callbacks.Fallo fallo) {
        ejecutar(true, () -> cliente.api().calificar(
                new CalificacionApi(taxistaId, servicioTaxiId, puntuacion, observacion)), exito, fallo);
    }

    /** Valoración promedio del taxista (RN-013). */
    public void valoracion(String taxistaId, Callbacks.Exito<ValoracionApi> exito, Callbacks.Fallo fallo) {
        ejecutar(true, () -> cliente.api().valoracion(taxistaId), exito, fallo);
    }

    /** Crea la llamada de Retrofit. Existe para que {@code ejecutar} no la cree si falta configuración. */
    private interface Fabrica<T> {
        Call<T> crear();
    }

    private <T> void ejecutar(boolean requiereConfiguracion, Fabrica<T> fabrica,
                              Callbacks.Exito<T> exito, Callbacks.Fallo fallo) {
        if (requiereConfiguracion && !cliente.estaConfigurada()) {
            notificar(fallo, new ApiException(ApiException.Tipo.NO_CONFIGURADA,
                    "La conexión con el servicio de taxistas no está configurada "
                            + "(TAXI_API_URL, TAXI_API_CORREO y TAXI_API_PASSWORD en local.properties).", 0));
            return;
        }
        fabrica.crear().enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> llamada, @NonNull Response<T> respuesta) {
                if (respuesta.isSuccessful()) {
                    exito.onExito(respuesta.body());
                } else {
                    notificar(fallo, ApiException.desdeRespuesta(respuesta));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> llamada, @NonNull Throwable t) {
                notificar(fallo, ApiException.desdeFallo(t));
            }
        });
    }

    private void notificar(Callbacks.Fallo fallo, ApiException error) {
        if (observador != null) {
            observador.onFallo(error);
        }
        fallo.onFallo(error);
    }
}
