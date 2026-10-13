package cl.demoseguridad.app.almacen

import java.io.File

/**
 * Simula lo que vería un atacante que logra leer los archivos de la app
 * (dispositivo con root, backup inseguro, análisis forense del almacenamiento).
 *
 * No hay nada "hacker" aquí: solo leemos el contenido crudo del archivo tal cual
 * está en el disco. La diferencia la hace CÓMO se guardó el dato, no cómo se lee.
 */
object LectorArchivos {

    /** Devuelve el contenido del archivo tal cual está en disco (texto crudo). */
    fun leerCrudo(archivo: File): String {
        if (!archivo.exists()) {
            return "(todavía no existe: ${archivo.name})\nGuarda primero los datos de ejemplo."
        }
        val bytes = archivo.readBytes()
        val texto = String(bytes, Charsets.UTF_8)
        return "// Archivo: ${archivo.absolutePath}\n" +
            "// Tamaño: ${bytes.size} bytes\n\n" +
            texto
    }
}
