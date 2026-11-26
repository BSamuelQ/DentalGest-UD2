package es.dam.di.componente;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Campo de texto reutilizable. La etiqueta inicial es «Campo» y el texto
 * vacío es válido hasta que se marca como obligatorio. Emite
 * {@link ValidezCambiada} cuando cambia su validez, incluso al cambiar
 * la obligatoriedad. Cambiar el rótulo no dispara el evento.
 */
public class CampoValidado extends VBox {
    private final TextField entrada = new TextField();
    private final StringProperty etiqueta = new SimpleStringProperty(this, "etiqueta", "Campo");
    private final BooleanProperty obligatorio = new SimpleBooleanProperty(this, "obligatorio", false);
    private final ReadOnlyBooleanWrapper valido = new ReadOnlyBooleanWrapper(this, "valido", true);

    /** Construye el campo con sus valores por defecto. */
    public CampoValidado() {
        Label rotulo = new Label();
        rotulo.textProperty().bind(etiqueta);
        rotulo.setLabelFor(entrada);
        Label error = new Label("Completa este campo.");
        error.getStyleClass().add("error");
        error.visibleProperty().bind(valido.not());
        error.managedProperty().bind(error.visibleProperty());
        valido.bind(Bindings.createBooleanBinding(
                () -> !isObligatorio() || !getTexto().isBlank(), obligatorio, entrada.textProperty()));
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


