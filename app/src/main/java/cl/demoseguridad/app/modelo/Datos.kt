package cl.demoseguridad.app.modelo

/**
 * Modelos de datos de la demo.
 *
 * IMPORTANTE: todos los valores son DE PRUEBA, inventados para la demostración.
 * No se usan contraseñas reales del usuario ni de otras apps instaladas.
 */

data class Credencial(
    val servicio: String,
    val usuario: String,
    val password: String,
)

data class Tarjeta(
    val titular: String,
    val numero: String,
    val expiracion: String,
    val cvv: String,
)

/** Datos de ejemplo que la demo guarda en los almacenes. */
object DatosDemo {

    val credenciales = listOf(
        Credencial("Correo", "constanza.demo@ejemplo.cl", "Verano2026!"),
        Credencial("Banco", "cliente_demo", "Caja$1234"),
        Credencial("RedSocial", "demo_user", "P4ssw0rd.demo"),
    )

    val tarjetas = listOf(
        // Número de prueba (prefijo 4111... es el clásico de testing, no corresponde a nadie).
        Tarjeta("CONSTANZA DEMO", "4111 1111 1111 1111", "08/29", "123"),
        Tarjeta("CONSTANZA DEMO", "5500 0000 0000 0004", "11/27", "456"),
    )
}
