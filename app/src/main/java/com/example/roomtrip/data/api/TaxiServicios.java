package com.example.roomtrip.data.api;

import com.example.roomtrip.BuildConfig;

/**
 * Punto de acceso de las pantallas al {@link TaxiRepository}, ya configurado con los valores de
 * {@code local.properties} (URL del gateway y credencial {@code APP_MOVIL}, ver
 * {@code docs/integracion-api-taxistas.md}). Se crea una sola vez y se comparte, para reutilizar el token.
 *
 * <p>Uso: {@code TaxiServicios.repositorio().buscarPorCorreo(correo, taxista -> ..., e -> ...)}.
 */
public final class TaxiServicios {

    private static TaxiRepository repositorio;

    private TaxiServicios() {
    }

    public static synchronized TaxiRepository repositorio() {
        if (repositorio == null) {
            repositorio = new TaxiRepository(
                    new TaxiApiCliente(BuildConfig.TAXI_API_URL, BuildConfig.TAXI_API_CORREO,
                            BuildConfig.TAXI_API_PASSWORD),
                    null);
        }
        return repositorio;
    }
}
