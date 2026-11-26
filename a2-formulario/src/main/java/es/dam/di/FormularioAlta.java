package es.dam.di;

import java.util.function.Predicate;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*
 * Ejecutar con mvn clean javafx:run desde esta carpeta.
 * El alta solicita nombre, correo y teléfono.
 * Validamos al escribir porque son tres reglas locales y baratas.
 * A cambio, algunos avisos aparecen mientras se completa el campo.
 * Los avisos quedan ocultos hasta la primera edición de cada campo.
 * Un listener actualiza el mensaje de cada campo.
 * El filtro de teclado bloquea caracteres incompatibles.
 * TextFormatter aplica la misma restricción al pegar con el ratón.
 * El enlace del botón depende de la validez de los tres campos.
 * El diálogo modal pertenece a la ventana del formulario.
 * Demostración: demo-formulario.mp4, en esta carpeta.
 */
public class FormularioAlta extends Application {
    @Override
    public void start(Stage stage) {
        TextField nombre = new TextField();
        TextField correo = new TextField();
        TextField telefono = new TextField();
        configurarTelefono(telefono);

        Label errorNombre = new Label();
        Label errorCorreo = new Label();
        Label errorTelefono = new Label();
        BooleanBinding nombreValido = validar(nombre, errorNombre, texto -> texto.strip().length() >= 2,
                "Escribe al menos dos caracteres.");
        BooleanBinding correoValido = validar(correo, errorCorreo,
                texto -> texto.strip().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"),
                "Escribe un correo con formato nombre@dominio.es.");
        BooleanBinding telefonoValido = validar(telefono, errorTelefono, texto -> texto.matches("[0-9]{9}"),
                "Introduce exactamente nueve dígitos.");

        Button guardar = new Button("Guardar");
        guardar.setDefaultButton(true);
        guardar.disableProperty().bind(nombreValido.and(correoValido).and(telefonoValido).not());
        Label resultado = new Label();
        resultado.setWrapText(true);
        guardar.setOnAction(evento -> confirmar(stage, nombre, correo, telefono, resultado));

        VBox raiz = new VBox(10, campo("Nombre", nombre, errorNombre), campo("Correo", correo, errorCorreo),
                campo("Teléfono", telefono, errorTelefono), guardar, resultado);
        raiz.setPadding(new Insets(24));
        stage.setTitle("DentalGest · Formulario de alta");
        stage.setScene(new Scene(raiz, 480, 395));
        stage.show();
    }

    private VBox campo(String titulo, TextField control, Label error) {
        Label etiqueta = new Label(titulo);
        etiqueta.setLabelFor(control);
        error.setStyle("-fx-text-fill: #B42318;");
        error.visibleProperty().bind(error.textProperty().isNotEmpty());
        error.managedProperty().bind(error.visibleProperty());
        return new VBox(4, etiqueta, control, error);
    }

    private BooleanBinding validar(TextField campo, Label error, Predicate<String> regla, String mensaje) {
        campo.textProperty().addListener((observable, anterior, actual) ->
                error.setText(regla.test(actual) ? "" : mensaje));
        return Bindings.createBooleanBinding(() -> regla.test(campo.getText()), campo.textProperty());
    }

    private void configurarTelefono(TextField telefono) {
        telefono.addEventFilter(KeyEvent.KEY_TYPED, evento -> {
            String texto = evento.getCharacter();
            if (!evento.isShortcutDown() && !texto.isEmpty() && !Character.isISOControl(texto.charAt(0))
                    && !texto.matches("[0-9]+")) {
                evento.consume();
            }
        });
        telefono.setTextFormatter(new TextFormatter<>(cambio ->
                cambio.getControlNewText().matches("[0-9]*") ? cambio : null));
    }

    private void confirmar(Stage stage, TextField nombre, TextField correo, TextField telefono, Label resultado) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION,
                nombre.getText().strip() + "\n" + correo.getText().strip() + "\n" + telefono.getText(),
                ButtonType.OK, ButtonType.CANCEL);
        dialogo.initOwner(stage);
        dialogo.initModality(Modality.WINDOW_MODAL);
        dialogo.setHeaderText("¿Guardar este paciente?");
        dialogo.showAndWait().filter(ButtonType.OK::equals).ifPresent(aceptado ->
                resultado.setText("Paciente guardado: " + nombre.getText().strip() + " · "
                        + correo.getText().strip() + " · " + telefono.getText()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
