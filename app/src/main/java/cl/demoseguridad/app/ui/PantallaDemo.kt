package cl.demoseguridad.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.demoseguridad.app.almacen.AlmacenInseguro
import cl.demoseguridad.app.almacen.AlmacenSeguro
import cl.demoseguridad.app.almacen.LectorArchivos
import cl.demoseguridad.app.modelo.Credencial
import cl.demoseguridad.app.modelo.Tarjeta

private enum class Modo { INSEGURO, SEGURO }

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PantallaDemo() {
    val context = LocalContext.current
    val inseguro = remember { AlmacenInseguro(context) }
    val seguro = remember { AlmacenSeguro(context) }

    var modo by remember { mutableStateOf(Modo.INSEGURO) }
    var credenciales by remember { mutableStateOf<List<Credencial>>(emptyList()) }
    var tarjetas by remember { mutableStateOf<List<Tarjeta>>(emptyList()) }
    var volcado by remember { mutableStateOf("") }

    // Formulario para que el usuario escriba sus propios datos.
    var servicio by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun refrescar() {
        credenciales = if (modo == Modo.INSEGURO) inseguro.credenciales() else seguro.credenciales()
        tarjetas = if (modo == Modo.INSEGURO) inseguro.tarjetas() else seguro.tarjetas()
    }

    fun cambiarModo(nuevo: Modo) {
        modo = nuevo
        volcado = ""
        refrescar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Demo de seguridad de datos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Mismos datos de ejemplo, guardados de dos formas. Compara qué ve " +
                    "un atacante que logra leer los archivos de la app.",
                style = MaterialTheme.typography.bodyMedium,
            )

            // Selector de modo
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = modo == Modo.INSEGURO,
                    onClick = { cambiarModo(Modo.INSEGURO) },
                    label = { Text("🔴 Inseguro") },
                )
                FilterChip(
                    selected = modo == Modo.SEGURO,
                    onClick = { cambiarModo(Modo.SEGURO) },
                    label = { Text("🟢 Seguro") },
                )
            }

            val color = if (modo == Modo.INSEGURO) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))) {
                Text(
                    if (modo == Modo.INSEGURO)
                        "Guardado en texto plano (SharedPreferences + archivo .txt). " +
                            "Todo queda legible en el disco."
                    else
                        "Guardado con EncryptedSharedPreferences y clave AES-256-GCM del " +
                            "Android Keystore. En el disco solo hay texto cifrado.",
                    Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Acciones
            Button(
                onClick = {
                    if (modo == Modo.INSEGURO) inseguro.guardarDatosDemo() else seguro.guardarDatosDemo()
                    volcado = ""
                    refrescar()
                },
                Modifier.fillMaxWidth(),
            ) { Text("1 · Guardar datos de ejemplo") }

            // Formulario para agregar datos propios
            SeccionTitulo("Agregar tu propio dato")
            OutlinedTextField(
                value = servicio,
                onValueChange = { servicio = it },
                label = { Text("Servicio (ej: Instagram)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario / correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    if (servicio.isNotBlank() && password.isNotBlank()) {
                        val c = Credencial(servicio.trim(), usuario.trim(), password)
                        if (modo == Modo.INSEGURO) inseguro.agregarCredencial(c) else seguro.agregarCredencial(c)
                        servicio = ""; usuario = ""; password = ""
                        volcado = ""
                        refrescar()
                    }
                },
                Modifier.fillMaxWidth(),
            ) { Text("Agregar credencial al modo actual") }

            if (modo == Modo.INSEGURO) {
                Text(
                    "⚠️ En modo inseguro esto se guarda en texto plano. No uses una " +
                        "contraseña real que te importe.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedButton(
                onClick = {
                    val archivo = if (modo == Modo.INSEGURO) inseguro.rutaPrefs() else seguro.rutaPrefs()
                    val tarjetasTxt = if (modo == Modo.INSEGURO)
                        "\n\n----- " + inseguro.rutaTarjetas().name + " -----\n" +
                            LectorArchivos.leerCrudo(inseguro.rutaTarjetas())
                    else ""
                    volcado = LectorArchivos.leerCrudo(archivo) + tarjetasTxt
                },
                Modifier.fillMaxWidth(),
            ) { Text("2 · Extraer archivo del disco (ojos del atacante)") }

            OutlinedButton(
                onClick = {
                    if (modo == Modo.INSEGURO) inseguro.borrar() else seguro.borrar()
                    volcado = ""
                    refrescar()
                },
                Modifier.fillMaxWidth(),
            ) { Text("Borrar datos") }

            // Datos tal como los lee la app (siempre legibles para la app dueña)
            if (credenciales.isNotEmpty() || tarjetas.isNotEmpty()) {
                SeccionTitulo("Datos que la app lee correctamente")
                credenciales.forEach { c ->
                    Tarjetita("${c.servicio}", "${c.usuario}  ·  ${c.password}")
                }
                tarjetas.forEach { t ->
                    Tarjetita(t.titular, "${t.numero}  ·  ${t.expiracion}  ·  CVV ${t.cvv}")
                }
            }

            // Volcado del disco
            if (volcado.isNotEmpty()) {
                SeccionTitulo(
                    if (modo == Modo.INSEGURO)
                        "⚠️ Lo que se ve en el disco: LEGIBLE"
                    else
                        "✅ Lo que se ve en el disco: CIFRADO",
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (modo == Modo.INSEGURO)
                            MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                    ),
                ) {
                    Text(
                        volcado,
                        Modifier.padding(12.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Datos 100% de prueba. No se leen contraseñas reales del usuario ni de otras apps.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(texto, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun Tarjetita(titulo: String, detalle: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(titulo, fontWeight = FontWeight.SemiBold)
            Text(detalle, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
        }
    }
}
