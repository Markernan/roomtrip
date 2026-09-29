package com.example.roomtrip.data.model;

/**
 * Estado de una habitación en el Inventario del admin de hotel.
 *
 * <p>El ERS no define estos estados: RF-HOT-006 solo habla de dar de baja una habitación y
 * RF-HOT-010 calcula la disponibilidad a partir de las reservas de un rango de fechas. Este enum
 * formaliza lo que ya muestra el Inventario y se puede reemplazar cuando el equipo decida cómo
 * modelar la disponibilidad; el compilador señala cada uso.
 */
public enum EstadoHabitacion {
    DISPONIBLE("Disponible"),
    OCUPADA("Ocupada"),
    MANTENIMIENTO("Mantenimiento");

    private final String etiqueta;

    EstadoHabitacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra al usuario. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
