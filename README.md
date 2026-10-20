# mi-app

# OptiPC

**OptiPC** es una aplicación móvil nativa para Android, desarrollada en **Kotlin**, que ayuda a los usuarios a elegir la mejor configuración de PC para armar según su presupuesto y sus necesidades.

La app solo procesa información (no vende ni ensambla equipos), por lo que es un proyecto de gama baja y compatible desde **Android 8**.

---

## Cómo funciona

El usuario responde una encuesta sencilla de **7 pasos** sobre lo que busca. Al terminar, un botón lo lleva a una pantalla con las computadoras recomendadas según sus respuestas.

Desde ahí puede:

- Ver el detalle de cada opción.
- Compararlas.
- Guardar sus favoritas.

---

## Arquitectura en tres capas

| Capa | Nombre | Descripción |
|:----:|--------|-------------|
| 1 | Interfaz | Pantallas tipo encuesta, resultados, detalle, comparador y favoritos. |
| 2 | Lógica y datos | Motor de recomendación y base de datos que guarda la información junto con los cálculos hechos para recomendar. |
| 3 | Persistencia local | Base de datos local con caché para almacenar los resultados. |

---

## Identidad visual

Estética elegante y tecnológica, con fondo oscuro y los siguientes colores:

| Uso | Color | Código |
|-----|-------|--------|
| Fondo | Azul noche | `#080D1A` |
| Primario | Cian | `#00CFFF` |
| Secundario | Violeta | `#7B2CFF` |
| Acento | Verde menta | `#19E6B5` |

---

## Tecnologías

- **Lenguaje:** Kotlin
- **Plataforma:** Android 8 o superior
- **Almacenamiento:** base de datos local con caché
