package cl.demoseguridad.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Azul = Color(0xFF0D47A1)
private val Rojo = Color(0xFFC62828)
private val Verde = Color(0xFF2E7D32)

private val ColoresClaros = lightColorScheme(
    primary = Azul,
    secondary = Verde,
    error = Rojo,
)

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF81C784),
    error = Color(0xFFEF9A9A),
)

@Composable
fun DemoSeguridadDatosTheme(
    oscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (oscuro) ColoresOscuros else ColoresClaros,
        content = content,
    )
}
