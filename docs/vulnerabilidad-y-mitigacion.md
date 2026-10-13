# Vulnerabilidad y mitigación: almacenamiento inseguro de datos sensibles en Android

**Proyecto:** Demo de seguridad de datos
**Ámbito:** Ciberseguridad defensiva · almacenamiento local en dispositivos móviles
**Alcance ético:** todas las pruebas se hacen sobre datos de ejemplo, en el propio
dispositivo y la propia app. No se accede a datos de terceros ni de otras apps.

---

## 1. Contexto

Muchas apps guardan en el teléfono datos sensibles del usuario: contraseñas,
tokens de sesión, números de tarjeta, documentos. Si se guardan **sin cifrar**,
quedan expuestos ante cualquiera que consiga leer los archivos de la app.

Esta demo reproduce ese escenario de forma controlada y luego aplica la corrección.

## 2. La vulnerabilidad

**Nombre:** Almacenamiento de datos sensibles en texto plano
**Referencia:** OWASP Mobile Top 10 — **M9: Insecure Data Storage**
(equivalente a CWE-312: *Cleartext Storage of Sensitive Information*).

### Descripción

En el **modo inseguro**, la app guarda:

- Las credenciales en un `SharedPreferences` normal, que Android escribe como un
  archivo XML en texto plano en:
  `/data/data/cl.demoseguridad.app/shared_prefs/almacen_inseguro.xml`
- Las tarjetas en un archivo de texto:
  `/data/data/cl.demoseguridad.app/files/tarjetas_inseguro.txt`

Aunque esa carpeta es privada de la app (sandbox de Android), **el contenido no
está cifrado**. Si se rompe el aislamiento, el dato se lee tal cual.

### ¿Cómo se podría leer ese archivo? (vectores de ataque realistas)

1. **Dispositivo con root o desbloqueado (bootloader).** Con root se accede a
   `/data/data/` de cualquier app.
2. **Copias de seguridad mal configuradas.** Si `android:allowBackup="true"`, el
   backup puede extraerse por USB (`adb backup`) y leerse en un PC.
3. **Análisis forense físico.** Quien tenga el teléfono y herramientas forenses
   puede volcar la partición de datos.
4. **Malware con privilegios** o una vulnerabilidad que permita salir del sandbox.

### Impacto

- Robo de credenciales → acceso a las cuentas del usuario.
- Robo de datos de tarjeta → fraude financiero.
- Incumplimiento legal (en Chile, **Ley 19.628** de protección de datos; a nivel
  internacional, PCI-DSS para datos de tarjeta y GDPR para datos personales).

### Evidencia en la demo

En el modo inseguro, el botón **"Extraer archivo del disco"** muestra el XML con
la contraseña y el `.txt` con el número de tarjeta **completamente legibles**.

## 3. La mitigación

**Principio:** *cifrado en reposo* (encryption at rest) con la clave protegida por
hardware.

En el **modo seguro** la app usa **`EncryptedSharedPreferences`** de Jetpack
Security, que cifra **claves y valores** antes de escribirlos en disco:

- Valores: **AES-256-GCM** (cifrado autenticado).
- Claves: **AES-256-SIV** (determinista, para poder buscar por clave).
- La **clave maestra** se genera y almacena en el **Android Keystore**: vive en un
  entorno seguro (TEE/StrongBox en equipos compatibles) y **nunca sale** de él,
  ni siquiera la app puede exportarla.

Resultado: aunque un atacante copie el archivo
`shared_prefs/almacen_seguro.xml`, solo obtiene **texto cifrado inservible** sin
la clave, que está anclada a ese dispositivo.

### Otras medidas aplicadas en el proyecto

| Medida | Dónde | Para qué |
|---|---|---|
| `android:allowBackup="false"` | `AndroidManifest.xml` | Evita extraer datos vía `adb backup`. |
| Reglas de backup/transfer | `res/xml/backup_rules.xml`, `data_extraction_rules.xml` | Excluyen los almacenes del respaldo en la nube y la transferencia entre equipos. |
| Datos de prueba | `modelo/Datos.kt` | Nunca se usan credenciales reales. |

### Recomendaciones adicionales (buenas prácticas)

- **No guardar el CVV** de tarjetas: PCI-DSS lo prohíbe incluso cifrado. En la demo
  se incluye solo para ilustrar el riesgo.
- **Minimizar datos:** guardar solo lo imprescindible (p. ej. un token en vez de la
  contraseña).
- **Exigir autenticación del usuario** (biometría/PIN) para descifrar, con
  `setUserAuthenticationRequired(true)` en la clave del Keystore.
- **Ofuscar el código** (R8/ProGuard) en release para dificultar el análisis.
- **Detección de root** para advertir o limitar funciones en equipos comprometidos.

## 4. Conclusión

El aislamiento del sistema operativo **no basta** para proteger datos sensibles: en
cuanto se rompe (root, backup, forense), el texto plano queda expuesto. Cifrar en
reposo con una clave respaldada por el Keystore convierte esos mismos archivos en
datos inservibles para un atacante. La demo muestra el **antes y el después** con
los mismos datos, evidenciando que el cambio de almacenamiento es una mitigación
concreta y de bajo costo.

## 5. Referencias

- OWASP Mobile Top 10 — M9: Insecure Data Storage.
- CWE-312: Cleartext Storage of Sensitive Information.
- Android Developers — Jetpack Security (`EncryptedSharedPreferences`) y Android Keystore.
- Ley N.º 19.628 sobre Protección de la Vida Privada (Chile).
