# Demo de seguridad de datos (Android)

App educativa de ciberseguridad que muestra, con **datos de prueba**, la diferencia
entre guardar información sensible (contraseñas y tarjetas) **en texto plano** o
**cifrada** en el dispositivo, y cómo esto cambia lo que vería un atacante que
logra leer los archivos de la app.

> ⚠️ Proyecto 100% defensivo y educativo. Usa solo datos inventados. **No** lee
> contraseñas reales del usuario ni de otras aplicaciones instaladas (Android no
> lo permite, y esta app tampoco lo intenta).

## Qué demuestra

| | 🔴 Modo inseguro | 🟢 Modo seguro |
|---|---|---|
| Credenciales | `SharedPreferences` en texto plano | `EncryptedSharedPreferences` (AES-256-GCM) |
| Tarjetas | archivo `.txt` plano | cifradas en el mismo almacén |
| Clave de cifrado | — | Android Keystore (no sale del hardware) |
| Al "extraer" el archivo | se lee **todo legible** | solo **texto cifrado** |

## Cómo usarla

1. Elige el modo (**🔴 Inseguro** o **🟢 Seguro**).
2. **Guardar datos de ejemplo**.
3. **Extraer archivo del disco**: la app lee el archivo tal cual está guardado y
   te muestra lo que vería el atacante. En inseguro aparece la contraseña y el
   número de tarjeta; en seguro, solo caracteres cifrados.
4. Cambia de modo y repite para comparar.

## Cómo abrir y ejecutar

1. Abre la carpeta en **Android Studio** (ya trae el wrapper de Gradle).
2. Deja que sincronice Gradle.
3. Ejecuta en un emulador o teléfono con **Android 8.0 (API 26) o superior**.

## Tecnologías

- Kotlin + Jetpack Compose (Material 3)
- `androidx.security:security-crypto` (EncryptedSharedPreferences + Keystore)
- minSdk 26 · targetSdk 35 · compileSdk 36

## Documentación

- [`docs/vulnerabilidad-y-mitigacion.md`](docs/vulnerabilidad-y-mitigacion.md):
  explicación de la vulnerabilidad, el riesgo y las medidas de mitigación.
