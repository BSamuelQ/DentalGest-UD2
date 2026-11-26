package es.dam.di;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
 * Ejecutar desde esta carpeta con mvn clean javafx:run.
 * La ventana presenta los controles mínimos de alta.
 * Scene contiene un VBox como raíz.
 * El VBox apila los campos y una fila de acciones.
 * El HBox mantiene juntas las dos acciones.
 * Los textos guía sugieren qué introducir.
 * El ancho preferido de los campos se expresa en columnas.
 * La ayuda del desplegable explica su finalidad.
 * Guardar es el botón por defecto.
 * El contador depende directamente del nombre.
 * stop informa del cierre de la aplicación.
 */
public class PrimeraVentana extends Application {
    @Override
    public void start(Stage stage) {
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");
        nombre.setPrefColumnCount(24);
        TextField apellidos = new TextField();
        apellidos.setPromptText("Apellidos");

        ComboBox<String> especialidad = new ComboBox<>();
        especialidad.getItems().addAll("Revisión", "Limpieza", "Ortodoncia");
        especialidad.setPromptText("Especialidad");
        especialidad.setTooltip(new Tooltip("Selecciona el servicio dental"));

        CheckBox privacidad = new CheckBox("Acepto la política de privacidad");
        Label contador = new Label();
        contador.textProperty().bind(Bindings.length(nombre.textProperty()).asString("Nombre: %d caracteres"));

        Button guardar = new Button("Guardar");
        guardar.setDefaultButton(true);
        Button cancelar = new Button("Cancelar");
        cancelar.setCancelButton(true);
        cancelar.setOnAction(evento -> stage.close());

        VBox raiz = new VBox(12, new Label("Alta de paciente"), nombre, contador, apellidos,
                especialidad, privacidad, new HBox(8, guardar, cancelar));
        raiz.setPadding(new Insets(24));
        stage.setTitle("Alta de paciente");
        stage.setScene(new Scene(raiz, 420, 340));
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Se ha cerrado el alta de paciente.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
