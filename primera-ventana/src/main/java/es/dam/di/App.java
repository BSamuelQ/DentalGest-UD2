package es.dam.di;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Label saludo = new Label("El entorno funciona");
        VBox raiz = new VBox(saludo);
        raiz.setStyle("-fx-padding: 40;");
        stage.setTitle("Comprobación");
        stage.setScene(new Scene(raiz, 320, 160));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
