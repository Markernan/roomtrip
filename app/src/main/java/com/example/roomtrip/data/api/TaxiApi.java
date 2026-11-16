package com.example.roomtrip.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Contrato REST de la API de taxistas (gateway). El detalle de cada ruta, los roles y los errores están
 * en {@code docs/API.md} del repositorio web ({@code roomtrip-taxistas-web}). Todas las rutas menos
 * {@code login} exigen el token de la credencial de servicio {@code APP_MOVIL}, que agrega
 * {@link TaxiApiCliente} solo.
 *
 * <p>Las pantallas no usan esta interfaz directamente: pasan por {@link TaxiRepository}.
 */
public interface TaxiApi {

    @POST("api/auth/login")
    Call<TokenApi> login(@Body LoginApi cuerpo);

    /** Taxistas {@code APROBADO} y {@code DISPONIBLE} (RF-API-001). */
    @GET("api/taxistas/disponibles")
    Call<List<TaxistaApi>> taxistasDisponibles();

    /**
     * Puente con Firebase: la app conoce al taxista por su cuenta de Firebase, no por el id de la API,
     * y el correo es lo que tienen en común. 404 si ese correo no tiene un taxista registrado.
     */
    @GET("api/taxistas/buscar")
    Call<TaxistaApi> buscarPorCorreo(@Query("correo") String correo);

    @GET("api/taxistas/{id}")
    Call<TaxistaApi> obtenerTaxista(@Path("id") String id);

    /** La app marca {@code EN_SERVICIO} al asignar un traslado y {@code DISPONIBLE} al terminarlo (RF-WTX-007). */
    @PATCH("api/taxistas/{id}/disponibilidad")
    Call<TaxistaApi> cambiarDisponibilidad(@Path("id") String id, @Body DisponibilidadApi cuerpo);

    /** Calificación del cliente al finalizar el servicio (RF-TAX-017). Un servicio se califica una sola vez. */
    @POST("api/calificaciones")
    Call<CalificacionApi> calificar(@Body CalificacionApi cuerpo);

    /** Valoración promedio del taxista (RN-013). */
    @GET("api/calificaciones/taxista/{id}/valoracion")
    Call<ValoracionApi> valoracion(@Path("id") String id);
}
