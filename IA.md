# Registro de uso de IA

Se utilizó Codex para preparar y comprobar el borrador de la práctica. Los materiales de partida fueron el cuaderno de la UD2, la guía de instalación y el diseño de DentalGest de la UD1.

## Resumen de las instrucciones dadas a Codex

- Preparar un borrador de las cuatro actividades de la UD2 para DentalGest con JavaFX y Maven, tomando como referencia el cuaderno de actividades, la guía de instalación y el diseño de la UD1. Ejecutar la aplicación y obtener las capturas solicitadas.
- Añadir a `CampoValidado` un límite configurable de 60 caracteres por defecto y comprobar la validación justo en el límite y al superarlo.

Se conservaron las versiones de Java, JavaFX y JUnit de la guía. Para la actividad 4, el borrador incluye un campo reutilizable de texto con etiqueta y obligatoriedad configurables, un evento de cambio de validez, dos pruebas y la integración en el alta rápida de la agenda.

## Cambios aplicados durante la revisión con Codex

| Elemento | Cambio | Comprobación |
| --- | --- | --- |
| Documentación del evento | Se precisó que el evento responde a cambios de validez, incluidos los producidos por la obligatoriedad. Cambiar la etiqueta por sí solo no lo emite. | La prueba cambia la etiqueta entre dos transiciones y comprueba la lista de eventos. |
| Limpieza del campo | Se documentó la normalización de un valor nulo a texto vacío. | La prueba limpia el campo con un valor nulo, comprueba que queda vacío y verifica que se emite la transición a inválido. |
| Integración | Se incluyó Liberation Sans en los recursos de la agenda y se cargaron las variantes normal y negrita. | Se obtuvieron capturas de la agenda en JavaFX a 800 y 1600 píxeles. |
| Longitud máxima | Codex añadió la propiedad `longitudMaxima` y un mensaje al superar el límite. El campo conserva el texto y actualiza la validez si se cambia el límite. | Una prueba comprueba los casos de 60 y 61 caracteres y los eventos al modificar el límite. |

Este registro recoge los cambios efectuados con Codex. La revisión personal del estudiante y sus propias correcciones quedan pendientes de añadir.
