package asistencia.backend.service

import java.time.LocalDate

/**
 * Feriados nacionales de Argentina que el generador de calendarios marca como "sin actividad".
 * Cada día se puede marcar o desmarcar a mano desde la planilla, así que esta lista es solo un punto de partida.
 *
 * 2026: se cargaron los días en los que coinciden las fuentes consultadas. Quedaron afuera
 * el 23/03 y el 09/11, porque las fuentes no coincidían (se pueden marcar a mano).
 * Para otro año no hay lista: el calendario se genera sin feriados.
 */
object Feriados {
    private val porFecha: Map<LocalDate, String> = mapOf(
        LocalDate.of(2026, 1, 1) to "Año Nuevo",
        LocalDate.of(2026, 2, 16) to "Carnaval",
        LocalDate.of(2026, 2, 17) to "Carnaval",
        LocalDate.of(2026, 3, 24) to "Día Nacional de la Memoria",
        LocalDate.of(2026, 4, 2) to "Día del Veterano y de los Caídos en Malvinas",
        LocalDate.of(2026, 4, 3) to "Viernes Santo",
        LocalDate.of(2026, 5, 1) to "Día del Trabajador",
        LocalDate.of(2026, 5, 25) to "Día de la Revolución de Mayo",
        LocalDate.of(2026, 6, 15) to "Paso a la Inmortalidad del Gral. Güemes",
        LocalDate.of(2026, 6, 20) to "Día de la Bandera",
        LocalDate.of(2026, 7, 9) to "Día de la Independencia",
        LocalDate.of(2026, 7, 10) to "Día no laborable con fines turísticos",
        LocalDate.of(2026, 8, 17) to "Paso a la Inmortalidad del Gral. San Martín",
        LocalDate.of(2026, 10, 12) to "Día del Respeto a la Diversidad Cultural",
        LocalDate.of(2026, 11, 23) to "Día de la Soberanía Nacional",
        LocalDate.of(2026, 12, 7) to "Día no laborable con fines turísticos",
        LocalDate.of(2026, 12, 8) to "Día de la Inmaculada Concepción",
        LocalDate.of(2026, 12, 25) to "Navidad"
    )

    fun nombreDe(fecha: LocalDate): String? = porFecha[fecha]
}
