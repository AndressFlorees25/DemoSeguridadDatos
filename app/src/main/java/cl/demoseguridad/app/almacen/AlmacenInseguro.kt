package cl.demoseguridad.app.almacen

import android.content.Context
import cl.demoseguridad.app.modelo.Credencial
import cl.demoseguridad.app.modelo.DatosDemo
import cl.demoseguridad.app.modelo.Tarjeta
import java.io.File

/**
 * Almacenamiento INSEGURO (el "antes" de la demo).
 *
 * Guarda las credenciales en un SharedPreferences normal y las tarjetas en un
 * archivo de texto plano. En ambos casos los datos quedan LEGIBLES en el disco:
 * cualquiera con acceso a los archivos de la app (un dispositivo con root, un
 * backup mal configurado o malware con permisos) puede leerlos tal cual.
 *
 * Esto es intencional: es lo que la demo quiere exponer como vulnerabilidad.
 */
class AlmacenInseguro(private val context: Context) {

    companion object {
        const val PREFS = "almacen_inseguro"
        const val ARCHIVO_TARJETAS = "tarjetas_inseguro.txt"
    }

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun guardarDatosDemo() {
        // Credenciales en texto plano dentro del XML de SharedPreferences.
        prefs.edit().apply {
            clear()
            DatosDemo.credenciales.forEach { c ->
                putString("servicio_${c.servicio}", "${c.usuario}|${c.password}")
            }
            apply()
        }
        // Tarjetas en un .txt plano dentro de filesDir.
        val texto = buildString {
            DatosDemo.tarjetas.forEach { t ->
                appendLine("${t.titular};${t.numero};${t.expiracion};${t.cvv}")
            }
        }
        File(context.filesDir, ARCHIVO_TARJETAS).writeText(texto)
    }

    fun borrar() {
        prefs.edit().clear().apply()
        File(context.filesDir, ARCHIVO_TARJETAS).delete()
    }

    fun credenciales(): List<Credencial> =
        prefs.all.mapNotNull { (clave, valor) ->
            if (clave.startsWith("servicio_") && valor is String) {
                val partes = valor.split("|")
                Credencial(clave.removePrefix("servicio_"), partes.getOrElse(0) { "" }, partes.getOrElse(1) { "" })
            } else null
        }

    fun tarjetas(): List<Tarjeta> {
        val archivo = File(context.filesDir, ARCHIVO_TARJETAS)
        if (!archivo.exists()) return emptyList()
        return archivo.readLines().filter { it.isNotBlank() }.map { linea ->
            val p = linea.split(";")
            Tarjeta(p.getOrElse(0) { "" }, p.getOrElse(1) { "" }, p.getOrElse(2) { "" }, p.getOrElse(3) { "" })
        }
    }

    /** Rutas reales de los archivos en disco, para mostrarlas en la demo. */
    fun rutaPrefs() = File(File(context.dataDir, "shared_prefs"), "$PREFS.xml")
    fun rutaTarjetas() = File(context.filesDir, ARCHIVO_TARJETAS)
}
