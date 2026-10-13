package cl.demoseguridad.app.almacen

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import cl.demoseguridad.app.modelo.Credencial
import cl.demoseguridad.app.modelo.DatosDemo
import cl.demoseguridad.app.modelo.Tarjeta
import java.io.File

/**
 * Almacenamiento SEGURO (el "después" de la demo).
 *
 * Usa EncryptedSharedPreferences con una clave maestra AES-256-GCM protegida por
 * el Android Keystore. La clave NUNCA sale del hardware seguro del dispositivo:
 * ni la app puede leerla directamente. En disco, tanto las claves como los valores
 * quedan cifrados, así que extraer el archivo solo entrega texto ilegible.
 *
 * Mismos datos que el almacén inseguro, para comparar el "antes y después".
 */
class AlmacenSeguro(private val context: Context) {

    companion object {
        const val PREFS = "almacen_seguro"
    }

    private val prefs: SharedPreferences by lazy {
        val claveMaestra = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS,
            claveMaestra,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun guardarDatosDemo() {
        prefs.edit().apply {
            clear()
            DatosDemo.credenciales.forEach { c ->
                putString("servicio_${c.servicio}", "${c.usuario}|${c.password}")
            }
            DatosDemo.tarjetas.forEachIndexed { i, t ->
                putString("tarjeta_$i", "${t.titular}|${t.numero}|${t.expiracion}|${t.cvv}")
            }
            apply()
        }
    }

    /** Agrega una credencial escrita por el usuario (sin borrar las anteriores). */
    fun agregarCredencial(c: Credencial) {
        prefs.edit().putString("servicio_${c.servicio}", "${c.usuario}|${c.password}").apply()
    }

    fun borrar() = prefs.edit().clear().apply()

    fun credenciales(): List<Credencial> =
        prefs.all.keys.filter { it.startsWith("servicio_") }.mapNotNull { clave ->
            val valor = prefs.getString(clave, null) ?: return@mapNotNull null
            val p = valor.split("|")
            Credencial(clave.removePrefix("servicio_"), p.getOrElse(0) { "" }, p.getOrElse(1) { "" })
        }

    fun tarjetas(): List<Tarjeta> =
        prefs.all.keys.filter { it.startsWith("tarjeta_") }.sorted().mapNotNull { clave ->
            val valor = prefs.getString(clave, null) ?: return@mapNotNull null
            val p = valor.split("|")
            Tarjeta(p.getOrElse(0) { "" }, p.getOrElse(1) { "" }, p.getOrElse(2) { "" }, p.getOrElse(3) { "" })
        }

    /** Ruta real del XML cifrado en disco, para mostrarla en la demo. */
    fun rutaPrefs() = File(File(context.dataDir, "shared_prefs"), "$PREFS.xml")
}
