package es.dam.di;

import es.dam.di.componente.CampoValidado;
import es.dam.di.componente.ValidezCambiada;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*
 * Instalar primero a4-componente con mvn install.
 * Ejecutar desde esta carpeta con mvn clean javafx:run.
 * BorderPane asigna regiones a cabecera, navegación, contenido y estado.
 * Un VBox lateral apila las opciones de navegación.
 * El VBox central distribuye título, filtros, agenda y alta rápida.
 * Un HBox da al buscador el ancho libre de la fila de filtros.
 * VBox asigna a la tabla el alto libre de la zona central.
 * Otro HBox coloca el resumen y la fecha en extremos opuestos.
 * El alta rápida queda debajo de la tabla para conservar el foco en la agenda.
 * Los estilos proceden de la guía de DentalGest de la UD1.
 * Los datos de ejemplo son ficticios y se conservan durante la sesión.
 */
public class PantallaPrincipal extends Application {
    private static final LocalDate DIA_EJEMPLO = LocalDate.of(2026, 11, 26);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ObservableList<Cita> citas = FXCollections.observableArrayList(
            cita("09:00", "Ana López", "Revisión", "Confirmada"),
            cita("10:00", "Carlos Pérez", "Limpieza", "Pendiente"),
            cita("11:30", "Laura Díaz", "Ortodoncia", "Confirmada"),
            cita("12:00", "Pablo García", "Dolor dental", "Urgente"));
    private final ObservableList<String> pacientes = FXCollections.observableArrayList(
            "Ana López", "Carlos Pérez", "Laura Díaz", "Pablo García");
    private final ObservableList<String> historial = FXCollections.observableArrayList();
    private final FilteredList<Cita> visibles = new FilteredList<>(citas);
    private final Label resumen = new Label();
    private final Label fechaEstado = new Label();
    private final DatePicker dia = new DatePicker(DIA_EJEMPLO);
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        javafx.scene.text.Font.loadFont(getClass().getResource("/fonts/LiberationSans-Regular.ttf").toExternalForm(), 16);
        javafx.scene.text.Font.loadFont(getClass().getResource("/fonts/LiberationSans-Bold.ttf").toExternalForm(), 16);
        BorderPane raiz = new BorderPane();
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label marca = new Label("DentalGest");
        marca.getStyleClass().add("marca");
        HBox cabecera = fila(marca, espacio, new Label("Recepción · " + FECHA.format(DIA_EJEMPLO)));
        cabecera.getStyleClass().add("cabecera");
        raiz.setTop(cabecera);

        VBox agenda = crearAgenda();
        Button agendaBtn = new Button("Agenda");
        Button pacientesBtn = new Button("Pacientes");
        Button historialBtn = new Button("Historial");
        agendaBtn.setOnAction(evento -> mostrar(raiz, agenda));
        pacientesBtn.setOnAction(evento -> mostrar(raiz, listado("Pacientes", pacientes)));
        historialBtn.setOnAction(evento -> mostrar(raiz, listado("Historial de cambios", historial)));
        VBox lateral = new VBox(8, agendaBtn, pacientesBtn, historialBtn);
        lateral.getStyleClass().add("lateral");
        raiz.setLeft(lateral);
        mostrar(raiz, agenda);

        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);
        HBox estado = fila(resumen, separador, fechaEstado);
        estado.getStyleClass().add("estado");
        raiz.setBottom(estado);
        actualizarResumen();

        Scene escena = new Scene(raiz, 1000, 700);
        escena.getStylesheets().add(getClass().getResource("/dentalgest.css").toExternalForm());
        stage.setTitle("DentalGest · Agenda de citas");
        stage.setMinWidth(800);
        stage.setMinHeight(650);
        stage.setScene(escena);
        stage.show();
    }

    private VBox crearAgenda() {
        Button nueva = new Button("+ Nueva cita");
        nueva.getStyleClass().add("accion");
        nueva.setOnAction(evento -> nuevaCita());
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        TextField buscar = new TextField();
        buscar.setPromptText("Buscar por nombre");
        Label rotuloPaciente = new Label("Paciente");
        rotuloPaciente.setLabelFor(buscar);
        Label rotuloDia = new Label("Día");
        rotuloDia.setLabelFor(dia);
        VBox porPaciente = new VBox(8, rotuloPaciente, buscar);
        HBox.setHgrow(porPaciente, Priority.ALWAYS);
        dia.setEditable(false);
        HBox filtros = fila(porPaciente, new VBox(8, rotuloDia, dia));
        buscar.textProperty().addListener((o, anterior, actual) -> filtrar(actual));
        dia.valueProperty().addListener((o, anterior, actual) -> filtrar(buscar.getText()));
        filtrar("");

        TableView<Cita> tabla = new TableView<>(visibles);
        tabla.setPlaceholder(new Label("No hay citas con estos filtros."));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<Cita, String> estado = columna("Estado", Cita::estado);
        estado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String texto, boolean vacia) {
                super.updateItem(texto, vacia);
                setText(vacia ? null : texto);
                getStyleClass().removeAll("aviso", "error");
                if ("Pendiente".equals(texto)) getStyleClass().add("aviso");
                if ("Urgente".equals(texto)) getStyleClass().add("error");
            }
        });
        tabla.getColumns().setAll(List.of(columna("Hora", c -> c.hora().toString()), columna("Paciente", Cita::paciente),
                columna("Tratamiento", Cita::tratamiento), estado, acciones()));
        visibles.addListener((javafx.collections.ListChangeListener<Cita>) cambio -> actualizarResumen());
        VBox.setVgrow(tabla, Priority.ALWAYS);
        return new VBox(16, fila(titulo("Agenda de citas"), espacio, nueva),
                new Label("Consulta y organiza las citas del día."), filtros, tabla, altaRapida());
    }

    private VBox altaRapida() {
        CampoValidado campo = new CampoValidado();
        campo.setEtiqueta("Nombre del paciente");
        campo.setObligatorio(true);
        Button alta = new Button("Registrar paciente");
        alta.disableProperty().bind(campo.validoProperty().not());
        Label respuesta = new Label();
        campo.addEventHandler(ValidezCambiada.TIPO, evento -> {
            if (evento.isValido()) respuesta.setText("");
        });
        alta.setOnAction(evento -> {
            String nombre = campo.getTexto().strip();
            if (pacientes.stream().anyMatch(nombre::equalsIgnoreCase)) {
                respuesta.setText("Ese paciente ya está registrado.");
            } else {
                pacientes.add(nombre);
                campo.setTexto("");
                respuesta.setText("Paciente registrado.");
            }
        });
        HBox.setHgrow(campo, Priority.ALWAYS);
        HBox formulario = fila(campo, alta);
        formulario.setAlignment(Pos.TOP_LEFT);
        return new VBox(8, new Label("Alta rápida"), formulario, respuesta);
    }

    private void filtrar(String texto) {
        String consulta = texto.strip().toLowerCase(Locale.ROOT);
        visibles.setPredicate(cita -> cita.dia().equals(dia.getValue())
                && cita.paciente().toLowerCase(Locale.ROOT).contains(consulta));
        actualizarResumen();
    }

    private void actualizarResumen() {
        long pendientes = visibles.stream().filter(c -> c.estado().equals("Pendiente")).count();
        long urgentes = visibles.stream().filter(c -> c.estado().equals("Urgente")).count();
        resumen.setText(visibles.size() + (visibles.size() == 1 ? " cita" : " citas")
                + " · " + pendientes + (pendientes == 1 ? " pendiente" : " pendientes")
                + " · " + urgentes + (urgentes == 1 ? " urgente" : " urgentes"));
        fechaEstado.setText(dia.getValue() == null ? "" : "Agenda del " + FECHA.format(dia.getValue()));
    }

    private TableColumn<Cita, Void> acciones() {
        TableColumn<Cita, Void> acciones = new TableColumn<>("Acciones");
        acciones.setMinWidth(138);
        acciones.setSortable(false);
        acciones.setCellFactory(columna -> new TableCell<>() {
            private final Hyperlink mover = new Hyperlink("Mover");
            private final Hyperlink anular = new Hyperlink("Anular");
            private final HBox botones = fila(mover, anular);
            {
                mover.setOnAction(evento -> mover(getTableRow().getItem()));
                anular.setOnAction(evento -> anular(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean vacia) {
                super.updateItem(item, vacia);
                setGraphic(vacia ? null : botones);
            }
        });
        return acciones;
    }

    private void nuevaCita() {
        Dialog<Cita> dialogo = new Dialog<>();
        dialogo.initOwner(stage);
        dialogo.setTitle("Nueva cita");
        ComboBox<String> paciente = new ComboBox<>(pacientes);
        paciente.setPromptText("Paciente");
        DatePicker fecha = new DatePicker(dia.getValue());
        fecha.setEditable(false);
        TextField hora = new TextField();
        hora.setPromptText("Hora (HH:mm)");
        TextField tratamiento = new TextField();
        tratamiento.setPromptText("Tratamiento");
        dialogo.getDialogPane().setContent(new VBox(8,
                new Label("Paciente"), paciente, new Label("Fecha"), fecha,
                new Label("Hora (HH:mm)"), hora, new Label("Tratamiento"), tratamiento));
        dialogo.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        dialogo.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                javafx.beans.binding.Bindings.createBooleanBinding(() -> paciente.getValue() == null
                                || fecha.getValue() == null || !horaValida(hora.getText())
                                || tratamiento.getText().isBlank(),
                        paciente.valueProperty(), fecha.valueProperty(), hora.textProperty(), tratamiento.textProperty()));
        dialogo.setResultConverter(boton -> boton == ButtonType.OK
                ? new Cita(fecha.getValue(), LocalTime.parse(hora.getText()), paciente.getValue(),
                        tratamiento.getText().strip(), "Confirmada") : null);
        dialogo.showAndWait().ifPresent(cita -> {
            citas.add(cita);
            ordenar();
            historial.add("Cita guardada · " + detalle(cita));
            dia.setValue(cita.dia());
            informar("Cita guardada", detalle(cita));
        });
    }

    private void mover(Cita cita) {
        TextInputDialog dialogo = new TextInputDialog(cita.hora().toString());
        dialogo.initOwner(stage);
        dialogo.setTitle("Mover cita");
        dialogo.setHeaderText(detalle(cita));
        dialogo.setContentText("Nueva hora (HH:mm)");
        dialogo.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                javafx.beans.binding.Bindings.createBooleanBinding(() -> !horaValida(dialogo.getEditor().getText()),
                        dialogo.getEditor().textProperty()));
        dialogo.showAndWait().ifPresent(texto -> {
            Cita movida = new Cita(cita.dia(), LocalTime.parse(texto), cita.paciente(), cita.tratamiento(), cita.estado());
            citas.set(citas.indexOf(cita), movida);
            ordenar();
            historial.add("Cita movida · " + detalle(movida));
        });
    }

    private void anular(Cita cita) {
        ButtonType anular = new ButtonType("Anular cita", ButtonBar.ButtonData.OK_DONE);
        ButtonType conservar = new ButtonType("Conservar cita", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, detalle(cita), conservar, anular);
        confirmacion.initOwner(stage);
        confirmacion.setHeaderText("¿Anular esta cita?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.filter(anular::equals).isPresent()) {
            citas.remove(cita);
            historial.add("Cita anulada · " + detalle(cita));
        }
    }

    private void informar(String titulo, String detalle) {
        Alert aviso = new Alert(Alert.AlertType.INFORMATION, detalle);
        aviso.initOwner(stage);
        aviso.setHeaderText(titulo);
        aviso.showAndWait();
    }

    private void ordenar() {
        FXCollections.sort(citas, java.util.Comparator.comparing(Cita::dia).thenComparing(Cita::hora));
    }

    private static boolean horaValida(String texto) {
        return texto.matches("(?:[01][0-9]|2[0-3]):[0-5][0-9]");
    }

    private static String detalle(Cita cita) {
        return cita.paciente() + " · " + FECHA.format(cita.dia()) + " · " + cita.hora() + " · " + cita.tratamiento();
    }

    private static Cita cita(String hora, String paciente, String tratamiento, String estado) {
        return new Cita(DIA_EJEMPLO, LocalTime.parse(hora), paciente, tratamiento, estado);
    }

    private static TableColumn<Cita, String> columna(String titulo, Function<Cita, String> valor) {
        TableColumn<Cita, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(fila -> new ReadOnlyStringWrapper(valor.apply(fila.getValue())));
        return columna;
    }

    private static VBox listado(String titulo, ObservableList<String> datos) {
        ListView<String> lista = new ListView<>(datos);
        VBox.setVgrow(lista, Priority.ALWAYS);
        return new VBox(16, titulo(titulo), lista);
    }

    private static void mostrar(BorderPane raiz, VBox pantalla) {
        pantalla.setPadding(new Insets(16));
        raiz.setCenter(pantalla);
    }

    private static Label titulo(String texto) {
        Label titulo = new Label(texto);
        titulo.getStyleClass().add("titulo");
        return titulo;
    }

    private static HBox fila(javafx.scene.Node... nodos) {
        HBox fila = new HBox(8, nodos);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private record Cita(LocalDate dia, LocalTime hora, String paciente, String tratamiento, String estado) {}
}

