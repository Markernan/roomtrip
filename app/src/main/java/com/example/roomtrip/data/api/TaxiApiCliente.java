package com.example.roomtrip.data.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente HTTP de la API de taxistas. Inicia sesión solo con la credencial de servicio {@code APP_MOVIL},
 * guarda el token y lo agrega a cada petición; si el servidor responde 401 (token vencido), vuelve a
 * iniciar sesión y reintenta una vez.
 *
 * <p>Los tiempos de espera son generosos a propósito: tras un rato sin uso, Cloud Run tarda 10 a 15 s en
 * despertar el servicio, y el gateway espera hasta 20 s antes de rendirse. Con un tiempo menor, la primera
 * petición fallaría aunque el servicio estuviera sano.
 *
 * <p>La credencial viaja dentro del APK, de donde puede extraerse. Es una limitación aceptada de usar una
 * credencial de servicio: por eso la API le entrega a la app solo una vista reducida de los taxistas y
 * limita los intentos de login.
 */
public class TaxiApiCliente {

    /** Margen antes del vencimiento real del token para renovarlo. */
    private static final long MARGEN_MS = 60_000L;

    private final String correo;
    private final String password;
    private final boolean configurada;
    private final TaxiApi apiConToken;
    private final TaxiApi apiSinToken;
    private final Object candado = new Object();

    private String token;
    private long venceEnMs;

    public TaxiApiCliente(String urlBase, String correo, String password) {
        // 45 s de lectura: el gateway en Cloud Run puede tardar ~30 s en despertar tras un rato sin uso
        this(urlBase, correo, password, 15, 45);
    }

    /**
     * @param conectarSegundos tiempo máximo para abrir la conexión
     * @param leerSegundos     tiempo máximo esperando la respuesta (debe superar al del gateway, 20 s)
     */
    public TaxiApiCliente(String urlBase, String correo, String password, long conectarSegundos, long leerSegundos) {
        this.correo = correo == null ? "" : correo.trim();
        this.password = password == null ? "" : password;
        String url = urlBase == null ? "" : urlBase.trim();
        this.configurada = !url.isEmpty() && !this.correo.isEmpty() && !this.password.isEmpty();

        // Retrofit exige una URL válida que termine en "/"; sin configuración se usa una de relleno y
        // las llamadas fallan antes de salir a la red (ver TaxiRepository).
        String urlValida = url.isEmpty() ? "http://localhost/" : (url.endsWith("/") ? url : url + "/");

        OkHttpClient sinToken = new OkHttpClient.Builder()
                .connectTimeout(conectarSegundos, TimeUnit.SECONDS)
                .readTimeout(leerSegundos, TimeUnit.SECONDS)
                .writeTimeout(leerSegundos, TimeUnit.SECONDS)
                .build();
        OkHttpClient conToken = sinToken.newBuilder().addInterceptor(new InterceptorToken()).build();

        this.apiSinToken = construirRetrofit(urlValida, sinToken).create(TaxiApi.class);
        this.apiConToken = construirRetrofit(urlValida, conToken).create(TaxiApi.class);
    }

    /** Falso si falta la URL o la credencial en {@code local.properties}. */
    public boolean estaConfigurada() {
        return configurada;
    }

    /** La API ya autenticada: lo que usa {@link TaxiRepository}. */
    public TaxiApi api() {
        return apiConToken;
    }

    private static Retrofit construirRetrofit(String urlBase, OkHttpClient cliente) {
        return new Retrofit.Builder()
                .baseUrl(urlBase)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private String tokenVigente(boolean forzarRenovacion) throws IOException {
        synchronized (candado) {
            if (forzarRenovacion || token == null || System.currentTimeMillis() >= venceEnMs) {
                iniciarSesion();
            }
            return token;
        }
    }

    private void iniciarSesion() throws IOException {
        retrofit2.Response<TokenApi> respuesta = apiSinToken.login(new LoginApi(correo, password)).execute();
        if (!respuesta.isSuccessful() || respuesta.body() == null || respuesta.body().getToken() == null) {
            token = null;
            ApiException causa = respuesta.isSuccessful()
                    ? new ApiException(ApiException.Tipo.ERROR, "El servicio de taxistas no devolvió un token.", 0)
                    : ApiException.desdeRespuesta(respuesta);
            throw new ApiException.ErrorDeAutenticacion(causa);
        }
        token = respuesta.body().getToken();
        venceEnMs = System.currentTimeMillis() + respuesta.body().getExpiraEnSegundos() * 1000L - MARGEN_MS;
    }

    private final class InterceptorToken implements Interceptor {
        @Override
        public Response intercept(Chain cadena) throws IOException {
            Response respuesta = cadena.proceed(conToken(cadena.request(), tokenVigente(false)));
            if (respuesta.code() == 401) {
                respuesta.close();
                respuesta = cadena.proceed(conToken(cadena.request(), tokenVigente(true)));
            }
            return respuesta;
        }

        private Request conToken(Request peticion, String valor) {
            return peticion.newBuilder().header("Authorization", "Bearer " + valor).build();
        }
    }
}
