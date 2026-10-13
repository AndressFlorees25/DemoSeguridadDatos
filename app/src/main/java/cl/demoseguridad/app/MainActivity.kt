package cl.demoseguridad.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.demoseguridad.app.ui.PantallaDemo
import cl.demoseguridad.app.ui.theme.DemoSeguridadDatosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoSeguridadDatosTheme {
                PantallaDemo()
            }
        }
    }
}
