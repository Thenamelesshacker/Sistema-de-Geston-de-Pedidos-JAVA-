# Sistema Integrado de Gestión para Minimarket (Java)

Aplicación de consola desarrollada en **Java** para la gestión operativa de un minimarket: administración de catálogo y clientes, validación de datos, registro de ventas con actualización de stock, matriz semanal de ventas por turno, emisión de comprobantes (boletas), reportes y bitácora de operaciones.

---

## Arquitectura y Módulos

El sistema se coordina desde la clase principal `SistemaGestionPedidos` (paquete `sistemagestionpedidos`), que centraliza la entrada por consola, el control de flujo/excepciones y las estructuras de datos compartidas (`catalogo`, `clientes`, `matrizVentas` de `7 x 3` y `bitacora`).

| Módulo | Componente / Responsable | Funciones principales |
| :--- | :--- | :--- |
| **Módulo 1** | Catálogo y Registros *(Danna Mendoza)* | Gestión del catálogo de productos y registros base del sistema. |
| **Módulo 2** | Validaciones y Búsquedas *(Andree Chipana)* | Validaciones con expresiones regulares (`Pattern` / `Matcher`) para códigos, precios, cantidades, DNI, teléfonos y correos; normalización de texto; búsquedas por texto/código y filtros por stock bajo o rango de precio. |
| **Módulo 3** | Lógica de Negocio y Matriz 2D *(Christian Melgarejo)* | Registro de ventas/pedidos con cálculo de subtotal, IGV y total, descuento de stock, acumulación de unidades vendidas y actualización de la matriz de ventas (`7 días x 3 turnos`: Mañana, Tarde, Noche), además de resúmenes y picos de venta. |
| **Módulo 4** | Comprobantes, Reportes y Logs *(Alexandra Contreras)* | Emisión de boletas con redondeo financiero (`BigDecimal` / `RoundingMode.HALF_UP`), reportes de catálogo/stock y matriz de ventas, visualización de bitácora y registro manual de logs. |
| **Módulo 5** | Integración General, Menú Principal y QA *(Piero Juárez)* | Orquestación del menú principal, lectura centralizada por consola, manejo global de excepciones, utilidades de bitácora y herramientas de verificación/QA. |

---

## Estructura del Proyecto

```text
sistemagestionpedidos/
├── SistemaGestionPedidos.java   # Menú principal, estructuras compartidas, lectura y QA
├── Modulo1.java                   # Catálogo y registros
├── Modulo2.java                   # Validaciones (Regex), filtros y búsquedas
├── Modulo3.java                   # Lógica de ventas y matriz 2D (7x3)
└── Modulo4.java                   # Comprobantes, reportes y bitácora (logs)
```

> **Nota de integración:** Todos los módulos comparten el paquete `sistemagestionpedidos` y acceden a las estructuras estáticas de `SistemaGestionPedidos` para mantener consistencia en los datos durante la ejecución.

---

## Ejecución en NetBeans

1. Abre el proyecto en **Apache NetBeans** y verifica que todas las clases estén ubicadas en el paquete `sistemagestionpedidos`.
2. Guarda los cambios (`Ctrl + S`).
3. Ejecuta **Clean and Build** (`Shift + F11`) para compilar todos los módulos.
4. Ejecuta `SistemaGestionPedidos` (`F6`) y navega por el menú principal (`1` a `6`).

---

## Buenas Prácticas y Detalles Técnicos

* **Codificación UTF-8:** Configura el proyecto y el entorno de ejecución en **UTF-8** para evitar caracteres alterados en tildes, `ñ` y símbolos de moneda (`S/`).
* **Consola única:** Utiliza los métodos `leer...` centralizados en `SistemaGestionPedidos` para evitar conflictos de buffer con `Scanner`.
* **Cálculos monetarios:** Emplea `BigDecimal` con `RoundingMode.HALF_UP` al emitir comprobantes para mantener precisión en op. gravada, IGV y total.
