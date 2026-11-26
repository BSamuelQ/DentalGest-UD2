package es.dam.di.componente;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampoValidadoTest {
    @BeforeAll
    static void iniciarJavaFx() {
        Platform.startup(() -> {});
    }

    @Test
    void valoresInicialesYPropiedades() throws Exception {
        enJavaFx(() -> {
            CampoValidado campo = new CampoValidado();
            assertEquals("Campo", campo.getEtiqueta());
            assertFalse(campo.isObligatorio());
            assertTrue(campo.isValido());
            campo.etiquetaProperty().set("Paciente");
            campo.obligatorioProperty().set(true);
            assertEquals("Paciente", campo.getEtiqueta());
            assertFalse(campo.isValido());
        });
    }

    @Test
    void emiteEventosSoloAlCambiarLaValidez() throws Exception {
        enJavaFx(() -> {
            CampoValidado campo = new CampoValidado();
            List<Boolean> cambios = new ArrayList<>();
            campo.addEventHandler(ValidezCambiada.TIPO, evento -> cambios.add(evento.isValido()));
            campo.setObligatorio(true);
            campo.setTexto(" ");
            campo.setTexto("Ana");
            campo.setTexto("Ana López");
            campo.setEtiqueta("Nombre completo");
            assertEquals(List.of(false, true), cambios);
            campo.setTexto(null);
            assertEquals("", campo.getTexto());
            assertEquals(List.of(false, true, false), cambios);
        });
    }

    private void enJavaFx(Runnable prueba) throws Exception {
        FutureTask<Void> tarea = new FutureTask<>(prueba, null);
        Platform.runLater(tarea);
        tarea.get(10, TimeUnit.SECONDS);
    }
}

