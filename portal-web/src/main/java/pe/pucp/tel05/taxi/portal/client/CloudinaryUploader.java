package pe.pucp.tel05.taxi.portal.client;

import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Sube las fotos del formulario de registro directo a Cloudinary (RF-WTX-002).
 *
 * <p>Antes el formulario pedia la URL ya subida a mano; ahora el portal recibe
 * el archivo y hace el upload el mismo, para que el taxista no necesite saber
 * que existe Cloudinary.
 */
@Component
public class CloudinaryUploader {

    private final Cloudinary cloudinary;

    public CloudinaryUploader(@Value("${app.cloudinary-url:}") String cloudinaryUrl) {
        this.cloudinary = (cloudinaryUrl == null || cloudinaryUrl.isBlank())
                ? null
                : new Cloudinary(cloudinaryUrl);
    }

    public String subir(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new SubidaException("Debes adjuntar una imagen");
        }
        if (cloudinary == null) {
            throw new SubidaException(
                    "El portal no tiene configurado Cloudinary (variable CLOUDINARY_URL). "
                    + "Pide al administrador que la configure en el .env.");
        }
        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.emptyMap());
            Object url = resultado.get("secure_url");
            if (url == null) {
                throw new SubidaException("Cloudinary no devolvio la URL de la imagen subida.");
            }
            return url.toString();
        } catch (IOException e) {
            throw new SubidaException("No se pudo leer el archivo para subirlo a Cloudinary.");
        } catch (SubidaException e) {
            throw e;
        } catch (RuntimeException e) {
            // El SDK de Cloudinary lanza distintos tipos de RuntimeException segun la
            // falla (credenciales invalidas, cuenta mal configurada, error de la API):
            // todas se traducen aqui para no dejar pasar un 500 crudo hasta el formulario.
            throw new SubidaException("No se pudo subir la imagen a Cloudinary: " + e.getMessage());
        }
    }

    /** Se traduce en un mensaje legible en el formulario, nunca un stacktrace. */
    public static class SubidaException extends RuntimeException {
        public SubidaException(String mensaje) {
            super(mensaje);
        }
    }
}
