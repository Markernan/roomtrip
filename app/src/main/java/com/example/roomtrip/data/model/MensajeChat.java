package com.example.roomtrip.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Mensaje del chat cliente–hotel, en {@code reservas/{reservaId}/mensajes}. Los mensajes persisten
 * con fecha y hora del servidor. El chat solo funciona con la reserva activa (RN-006, ver reglas).
 */
public class MensajeChat {

    public static final String CAMPO_FECHA = "fecha";

    private String texto;
    private String emisorId;
    private RolUsuario rolEmisor;
    @ServerTimestamp
    private Date fecha;

    public MensajeChat() {
    }

    public MensajeChat(String texto, String emisorId, RolUsuario rolEmisor) {
        this.texto = texto;
        this.emisorId = emisorId;
        this.rolEmisor = rolEmisor;
    }

    /** Modelo de vista de MensajeAdapter: "mío" es lo que escribió quien mira la pantalla. */
    public Mensaje aVista(String uidActual) {
        String hora = fecha != null
                ? new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(fecha) : "…";
        return new Mensaje(texto, hora, uidActual != null && uidActual.equals(emisorId));
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getEmisorId() { return emisorId; }
    public void setEmisorId(String emisorId) { this.emisorId = emisorId; }
    public RolUsuario getRolEmisor() { return rolEmisor; }
    public void setRolEmisor(RolUsuario rolEmisor) { this.rolEmisor = rolEmisor; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
