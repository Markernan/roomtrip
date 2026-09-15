package pe.pucp.tel05.taxi.taxista.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pe.pucp.tel05.taxi.taxista.domain.Administrador;
import pe.pucp.tel05.taxi.taxista.repository.AdministradorRepository;

/**
 * Crea al arrancar el Superadmin y la credencial de servicio de la app movil,
 * si todavia no existen. Sin esto no habria forma de entrar al portal la
 * primera vez.
 *
 * <p>Es idempotente: si el correo ya existe no toca nada, asi que se puede
 * reiniciar el servicio sin efectos raros.
 */
@Component
public class CargaInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicial.class);

    private final AdministradorRepository repositorio;
    private final PasswordEncoder passwordEncoder;
    private final String superadminCorreo;
    private final String superadminPassword;
    private final String appMovilCorreo;
    private final String appMovilPassword;

    public CargaInicial(AdministradorRepository repositorio,
                        PasswordEncoder passwordEncoder,
                        @Value("${app.carga-inicial.superadmin-correo}") String superadminCorreo,
                        @Value("${app.carga-inicial.superadmin-password}") String superadminPassword,
                        @Value("${app.carga-inicial.app-movil-correo}") String appMovilCorreo,
                        @Value("${app.carga-inicial.app-movil-password}") String appMovilPassword) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
        this.superadminCorreo = superadminCorreo;
        this.superadminPassword = superadminPassword;
        this.appMovilCorreo = appMovilCorreo;
        this.appMovilPassword = appMovilPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        crearSiNoExiste(superadminCorreo, superadminPassword, "SUPERADMIN");
        crearSiNoExiste(appMovilCorreo, appMovilPassword, "APP_MOVIL");
    }

    private void crearSiNoExiste(String correo, String password, String rol) {
        String normalizado = correo.toLowerCase().trim();
        if (repositorio.findByCorreo(normalizado).isPresent()) {
            return;
        }
        repositorio.save(new Administrador(normalizado, passwordEncoder.encode(password), rol));
        log.info("Usuario inicial creado: {} con rol {}", normalizado, rol);
    }
}
