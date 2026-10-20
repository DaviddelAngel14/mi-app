package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.app.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

// ============================================================
// PANTALLAS DISPONIBLES
// Un enum es una lista de opciones fijas. Aquí lo usamos para saber
// qué "página" se está mostrando en este momento.
// ============================================================
enum class Pantalla {
    PRINCIPAL,
    SEGUNDA
}

// ============================================================
// ACTIVIDAD PRINCIPAL
// ============================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                // --- ESTADOS COMPARTIDOS ENTRE PÁGINAS ---
                // Se guardan aquí (y no dentro de Greeting) para que no se pierdan
                // al cambiar de página y poder mostrarlos también en la segunda.

                // Página que se está mostrando
                var pantallaActual by remember { mutableStateOf(Pantalla.PRINCIPAL) }

                // Texto aplicado: el que se muestra con contorno después de presionar Cambiar
                var textoUsuario by remember { mutableStateOf("") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // "when" elige qué pantalla dibujar según el estado actual
                    when (pantallaActual) {
                        Pantalla.PRINCIPAL -> Greeting(
                            name = "Android",
                            texto = textoUsuario,
                            onTextoChange = { textoUsuario = it },            // se aplica al presionar Cambiar
                            onIrASegunda = { pantallaActual = Pantalla.SEGUNDA }, // cambia de página
                            modifier = Modifier.padding(innerPadding)
                        )

                        Pantalla.SEGUNDA -> SegundaPantalla(
                            onVolver = { pantallaActual = Pantalla.PRINCIPAL },   // regresa a la principal
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// TEXTO SOBRE TEXTO (efecto de contorno)
// Box = "stack": dibuja los hijos uno encima del otro.
//   - Capa de abajo: el texto solo con borde (Stroke)
//   - Capa de arriba: el texto con su relleno normal
// ============================================================
@Composable
fun TextoConContorno(
    texto: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 32.sp,              // tamaño de la letra
    colorRelleno: Color = Color.White,       // color del interior de las letras
    colorContorno: Color = Color.Black,      // color del borde
    grosorContorno: Float = 12f              // más alto = borde más grueso
) {
    // contentAlignment = Center hace que las dos capas queden exactamente alineadas
    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        // Capa de abajo: solo dibuja el borde de las letras
        Text(
            text = texto,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = colorContorno,
                // Stroke = dibuja la línea del contorno en lugar de rellenar;
                // StrokeJoin.Round redondea las esquinas para evitar picos
                drawStyle = Stroke(width = grosorContorno, join = StrokeJoin.Round)
            )
        )

        // Capa de arriba: el relleno, que tapa la mitad interior del borde
        Text(
            text = texto,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = colorRelleno
        )
    }
}

// ============================================================
// PANTALLA PRINCIPAL
// Recibe el texto y las acciones desde MainActivity (así los estados
// viven arriba y esta pantalla solo los muestra y avisa de los cambios).
// ============================================================
@Composable
fun Greeting(
    name: String,
    texto: String,                       // texto aplicado (el que se muestra con contorno)
    onTextoChange: (String) -> Unit,     // se llama al presionar el botón Cambiar
    onIrASegunda: () -> Unit,            // se llama al presionar el botón de otra página
    modifier: Modifier = Modifier
) {
    // --- ESTADOS PROPIOS DE ESTA PANTALLA ---

    // Color de fondo actual (cambia cada 100 ms)
    var fondo by remember { mutableStateOf(Color.White) }

    // true = la alerta se está mostrando
    var mostrarAlerta by remember { mutableStateOf(false) }

    // false = estilo Android, true = estilo iOS
    var estiloIos by remember { mutableStateOf(false) }

    // Lo que se está escribiendo en la caja. Es independiente del texto con contorno:
    // solo se copia a "texto" cuando se presiona el botón Cambiar.
    var textoCaja by remember { mutableStateOf(texto) }

    // --- CAMBIO DE COLOR CADA 100 ms ---
    LaunchedEffect(Unit) {
        while (true) {
            delay(100) // espera 100 milisegundos sin bloquear la app
            // Genera un color aleatorio con valores RGB de 0 a 255
            fondo = Color(
                red = Random.nextInt(256),
                green = Random.nextInt(256),
                blue = Random.nextInt(256)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()          // ocupa toda la pantalla
            .background(fondo)      // usa el color que cambia cada 100 ms
            .padding(24.dp),        // margen interior
        horizontalAlignment = Alignment.CenterHorizontally,   // centra todo en horizontal
        // SpaceBetween: el primer bloque arriba, el último abajo y el del medio al centro
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // ---------- BLOQUE 1: ARRIBA (título) ----------
        TextoConContorno(
            texto = "BIENVENIDO",
            fontSize = 60.sp,
            colorRelleno = Color.White,
            colorContorno = Color(0xFF1A1A1A)   // gris casi negro
        )

        // ---------- BLOQUE 2: EN MEDIO (imagen + texto + caja de texto) ----------
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.mi_imagen),
                contentDescription = "Mi imagen",
                // Se redujo de 400 a 220 dp para que quepa también la caja de texto
                modifier = Modifier.size(220.dp)
            )
            Spacer(Modifier.height(16.dp))

            // Este es el "texto sobre texto". Muestra el texto que se aplicó con el
            // botón Cambiar; si está vacío, muestra el texto original.
            TextoConContorno(
                texto = if (texto.isEmpty()) "Minecraft Best" else texto,
                fontSize = 34.sp,
                colorRelleno = Color(0xFFFFEB3B),   // amarillo
                colorContorno = Color(0xFFB71C1C)   // rojo oscuro
            )
            Spacer(Modifier.height(12.dp))

            // CAJA DE TEXTO + BOTÓN CAMBIAR (uno al lado del otro)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoCaja,                          // lo que se ve dentro de la caja
                    onValueChange = { textoCaja = it },         // solo actualiza la caja, NO el texto con contorno
                    label = { Text("Escribe tu texto") },
                    singleLine = true,                          // una sola línea
                    modifier = Modifier.weight(1f),             // ocupa el espacio que deja el botón
                    // Fondo blanco y letras oscuras para que se lea sobre el fondo de colores
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray
                    )
                )
                Spacer(Modifier.width(8.dp))

                // Aquí sí se aplica el cambio: copia lo escrito al texto con contorno
                Button(onClick = { onTextoChange(textoCaja) }) {
                    Text("Cambiar")
                }
            }
        }

        // ---------- BLOQUE 3: ABAJO (botones) ----------
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)   // 12 dp entre botones
        ) {
            // Botón que abre la alerta
            Button(onClick = { mostrarAlerta = true }) {
                Text("Botón de prueba")
            }

            // Botón que alterna entre estilo Android e iOS
            OutlinedButton(
                onClick = { estiloIos = !estiloIos },   // invierte true <-> false
                modifier = Modifier.background(
                    Color.White.copy(alpha = 0.8f),     // fondo blanco semitransparente
                    RoundedCornerShape(50)              // esquinas totalmente redondeadas
                )
            ) {
                // El texto cambia según el estado actual
                Text("Estilo de alerta: ${if (estiloIos) "iOS" else "Android"}")
            }

            // Botón que lleva a la otra página
            Button(onClick = onIrASegunda) {
                Text("Ir a otra página")
            }
        }
    }

    // --- ALERTA ---
    // Solo se dibuja si mostrarAlerta es true.
    // Según estiloIos, se elige cuál de las dos alertas mostrar.
    if (mostrarAlerta) {
        if (estiloIos) {
            CupertinoAlert(
                titulo = "Aviso",
                mensaje = "Esta es una alerta estilo iOS",
                onAceptar = { mostrarAlerta = false },    // cierra la alerta
                onCancelar = { mostrarAlerta = false }
            )
        } else {
            AndroidAlert(
                titulo = "Aviso",
                mensaje = "Esta es una alerta estilo Android",
                onAceptar = { mostrarAlerta = false },
                onCancelar = { mostrarAlerta = false }
            )
        }
    }
}

// ============================================================
// SEGUNDA PANTALLA (la "otra página")
// ============================================================
@Composable
fun SegundaPantalla(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onVolver() }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SEGUNDA PÁGINA")

        Spacer(Modifier.height(24.dp))

        Button(onClick = onVolver) {
            Text("Volver")
        }
    }
}

// ============================================================
// ALERTA ESTILO ANDROID (Material Design)
// ============================================================
@Composable
fun AndroidAlert(
    titulo: String,
    mensaje: String,
    onAceptar: () -> Unit,     // qué hacer al presionar Aceptar
    onCancelar: () -> Unit     // qué hacer al presionar Cancelar
) {
    AlertDialog(
        // Se llama al tocar fuera de la alerta o presionar "atrás"
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = onAceptar) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

// ============================================================
// ALERTA ESTILO iOS (Cupertino)
// ============================================================
@Composable
fun CupertinoAlert(
    titulo: String,
    mensaje: String,
    onAceptar: () -> Unit,
    onCancelar: () -> Unit
) {
    val azulIos = Color(0xFF007AFF)

    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF2F2F2)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Parte de arriba: título y mensaje
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = titulo,
                        color = Color.Black,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = mensaje,
                        color = Color.Black,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Línea horizontal entre el mensaje y los botones
                HorizontalDivider(color = Color.LightGray)

                // Fila de botones.
                // IntrinsicSize.Min hace que la línea vertical tome solo la altura de los botones
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    // weight(1f) reparte el ancho a partes iguales entre los dos botones
                    TextButton(onClick = onCancelar, modifier = Modifier.weight(1f)) {
                        Text("Cancelar", color = azulIos)
                    }
                    // Línea vertical entre los dos botones
                    VerticalDivider(color = Color.LightGray)
                    TextButton(onClick = onAceptar, modifier = Modifier.weight(1f)) {
                        // En iOS la acción principal va en negrita
                        Text("Aceptar", color = azulIos, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ============================================================
// VISTA PREVIA
// ============================================================
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        Greeting(
            name = "Android",
            texto = "",
            onTextoChange = {},
            onIrASegunda = {}
        )
    }
}