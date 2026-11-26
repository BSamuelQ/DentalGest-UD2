package es.dam.di.componente;

import javafx.event.Event;
import javafx.event.EventType;

/** Evento emitido por CampoValidado al cambiar su validez. */
public final class ValidezCambiada extends Event {
    /** Tipo para registrar manejadores de cambios de validez. */
    public static final EventType<ValidezCambiada> TIPO = new EventType<>(Event.ANY, "VALIDEZ_CAMBIADA");
    /** Validez del campo en el momento del cambio. */
    private final boolean valido;

    /** Crea un evento con el nuevo estado de validez.
     * @param valido nueva validez del campo
     */
    public ValidezCambiada(boolean valido) {
        super(TIPO);
        this.valido = valido;
    }

    /** {@return nueva validez del campo} */
    public boolean isValido() { return valido; }
}

