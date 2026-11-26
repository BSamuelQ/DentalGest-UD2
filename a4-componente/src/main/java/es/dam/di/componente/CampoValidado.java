package es.dam.di.componente;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Campo de texto reutilizable. La etiqueta inicial es «Campo» y el texto
 * vacío es válido hasta que se marca como obligatorio. Admite hasta 60
 * caracteres por defecto. Emite
 * {@link ValidezCambiada} cuando cambia su validez, incluso al cambiar
 * la obligatoriedad o el límite. Cambiar el rótulo no dispara el evento.
 */
public class CampoValidado extends VBox {
    private final TextField entrada = new TextField();
    private final StringProperty etiqueta = new SimpleStringProperty(this, "etiqueta", "Campo");
    private final BooleanProperty obligatorio = new SimpleBooleanProperty(this, "obligatorio", false);
    private final IntegerProperty longitudMaxima = new SimpleIntegerProperty(this, "longitudMaxima", 60);
    private final ReadOnlyBooleanWrapper valido = new ReadOnlyBooleanWrapper(this, "valido", true);

    /** Construye el campo con sus valores por defecto. */
    public CampoValidado() {
        Label rotulo = new Label();
        rotulo.textProperty().bind(etiqueta);
        rotulo.setLabelFor(entrada);
        Label error = new Label();
        error.getStyleClass().add("error");
        error.textProperty().bind(Bindings.createStringBinding(() -> {
            String texto = getTexto();
            int limite = Math.max(0, getLongitudMaxima());
            if (texto.codePointCount(0, texto.length()) > limite) {
                return "Introduce como máximo " + limite + " caracteres.";
            }
            return isObligatorio() && texto.isBlank() ? "Completa este campo." : "";
        }, obligatorio, longitudMaxima, entrada.textProperty()));
        valido.bind(error.textProperty().isEmpty());
        error.visibleProperty().bind(valido.not());
        error.managedProperty().bind(error.visibleProperty());
        valido.addListener((observable, anterior, actual) -> fireEvent(new ValidezCambiada(actual)));
        setSpacing(8);
        getChildren().addAll(rotulo, entrada, error);
    }

    /** {@return texto mostrado como etiqueta} */
    public final String getEtiqueta() { return etiqueta.get(); }

    /** Cambia el texto de la etiqueta.
     * @param valor nuevo rótulo
     */
    public final void setEtiqueta(String valor) { etiqueta.set(valor); }

    /** {@return propiedad de etiqueta, inicialmente «Campo»} */
    public final StringProperty etiquetaProperty() { return etiqueta; }

    /** {@return si el campo exige texto} */
    public final boolean isObligatorio() { return obligatorio.get(); }

    /** Configura si el campo es obligatorio.
     * @param valor si se exige texto distinto de espacios
     */
    public final void setObligatorio(boolean valor) { obligatorio.set(valor); }

    /** {@return propiedad de obligatoriedad, inicialmente falsa} */
    public final BooleanProperty obligatorioProperty() { return obligatorio; }

    /** {@return límite configurado de caracteres Unicode, inicialmente 60} */
    public final int getLongitudMaxima() { return longitudMaxima.get(); }

    /**
     * Ajusta el límite sin modificar el texto del campo.
     * @param valor máximo de caracteres, con valores negativos interpretados como cero
     */
    public final void setLongitudMaxima(int valor) { longitudMaxima.set(valor); }

    /** {@return propiedad observable del límite de caracteres Unicode} */
    public final IntegerProperty longitudMaximaProperty() { return longitudMaxima; }

    /** {@return texto introducido} */
    public final String getTexto() { return entrada.getText(); }

    /**
     * Sustituye el contenido. Tanto null como la cadena vacía limpian la entrada.
     * @param texto nuevo contenido
     */
    public final void setTexto(String texto) { entrada.setText(texto == null ? "" : texto); }

    /** {@return si el contenido cumple la regla actual} */
    public final boolean isValido() { return valido.get(); }

    /** {@return propiedad de solo lectura para enlazar otras acciones} */
    public final ReadOnlyBooleanProperty validoProperty() { return valido.getReadOnlyProperty(); }
}


