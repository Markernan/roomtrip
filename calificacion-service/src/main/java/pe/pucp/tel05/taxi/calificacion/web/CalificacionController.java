package pe.pucp.tel05.taxi.calificacion.web;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.pucp.tel05.taxi.calificacion.domain.Calificacion;
import pe.pucp.tel05.taxi.calificacion.service.CalificacionService;
import pe.pucp.tel05.taxi.calificacion.web.dto.RegistrarCalificacionRequest;
import pe.pucp.tel05.taxi.calificacion.web.dto.ValoracionResponse;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService servicio;

    public CalificacionController(CalificacionService servicio) {
        this.servicio = servicio;
    }

    /** RF-TAX-017: solo la app movil registra calificaciones. */
    @PostMapping
    @PreAuthorize("hasRole('APP_MOVIL')")
    public ResponseEntity<Calificacion> registrar(@Valid @RequestBody RegistrarCalificacionRequest req) {
        Calificacion creada = servicio.registrar(
                req.taxistaId(), req.servicioTaxiId(), req.puntuacion(), req.observacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /** RF-WTX-006 / RN-013. */
    @GetMapping("/taxista/{taxistaId}/valoracion")
    @PreAuthorize("hasAnyRole('SUPERADMIN','APP_MOVIL') or #taxistaId == authentication.name")
    public ValoracionResponse valoracion(@PathVariable String taxistaId) {
        return servicio.valoracion(taxistaId);
    }

    @GetMapping("/taxista/{taxistaId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN') or #taxistaId == authentication.name")
    public List<Calificacion> listar(@PathVariable String taxistaId) {
        return servicio.listarPorTaxista(taxistaId);
    }

    @ExceptionHandler(CalificacionService.ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(CalificacionService.ReglaNegocioException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", Instant.now().toString());
        cuerpo.put("status", HttpStatus.CONFLICT.value());
        cuerpo.put("error", "REGLA_NEGOCIO");
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo);
    }
}
