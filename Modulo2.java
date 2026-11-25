package sistemagestionpedidos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sistemagestionpedidos.SistemaGestionPedidos.Producto;

/**
 * MÓDULO 2 -> Andree Wilfredo Chipana Rojas
 * Validaciones de Datos con Regex, Filtros y Búsquedas Avanzadas.
 */
public class Modulo2 {

    private static final Pattern PATRON_CODIGO_PRODUCTO = Pattern.compile("^PROD-\\d{3,6}$");
    private static final Pattern PATRON_NOMBRE_PRODUCTO = Pattern.compile("^[\\p{L}0-9 .\\-()]{2,60}$");
    private static final Pattern PATRON_PRECIO = Pattern.compile("^(0|[1-9]\\d*)(\\.\\d{1,2})?$");
    private static final Pattern PATRON_ENTERO_POSITIVO = Pattern.compile("^(0|[1-9]\\d*)$");
    private static final Pattern PATRON_DNI = Pattern.compile("^\\d{8}$");
    private static final Pattern PATRON_TELEFONO_PE = Pattern.compile("^9\\d{8}$");
    private static final Pattern PATRON_CORREO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static void modulo2ValidacionesYBusquedas() {
        int op;
        do {
            System.out.println("\n--- MÓDULO 2: VALIDACIONES Y BÚSQUEDAS AVANZADAS (ANDREE CHIPANA) ---");
            System.out.println("1. Validar dato con expresiones regulares (Regex)");
            System.out.println("2. Buscar producto por texto (nombre o código)");
            System.out.println("3. Filtrar productos por stock bajo");
            System.out.println("4. Filtrar productos por rango de precio");
            System.out.println("5. Filtrar productos por prefijo de nombre");
            System.out.println("6. Volver al menú principal");
            op = SistemaGestionPedidos.leerEnteroEnRango("Elija una opción (1-6): ", 1, 6);

            List<Producto> inventarioActual = obtenerInventarioActual();

            switch (op) {
                case 1 -> menuValidarRegex();
                case 2 -> {
                    String consulta = SistemaGestionPedidos.leerTextoNoVacio("Ingrese texto a buscar (nombre o código): ");
                    mostrarResultados("Búsqueda por \"" + consulta + "\"", buscarPorTexto(inventarioActual, consulta));
                    SistemaGestionPedidos.registrarLog("MODULO_2", "Búsqueda por texto: " + consulta);
                }
                case 3 -> {
                    int maxStock = SistemaGestionPedidos.leerEnteroEnRango("Stock máximo de alerta (0-100000): ", 0, 100000);
                    mostrarResultados("Productos con stock <= " + maxStock, filtrarStockBajo(inventarioActual, maxStock));
                    SistemaGestionPedidos.registrarLog("MODULO_2", "Filtro stock bajo <= " + maxStock);
                }
                case 4 -> {
                    double min = SistemaGestionPedidos.leerDecimalPositivo("Precio mínimo (S/): ");
                    double max = SistemaGestionPedidos.leerDecimalPositivo("Precio máximo (S/): ");
                    if (max < min) {
                        System.out.println("[ERROR] El precio máximo no puede ser menor que el mínimo.");
                    } else {
                        mostrarResultados(String.format(Locale.ROOT, "Productos entre S/ %.2f y S/ %.2f", min, max),
                                filtrarPorRangoPrecio(inventarioActual, min, max));
                        SistemaGestionPedidos.registrarLog("MODULO_2", String.format(Locale.ROOT, "Filtro precio [%.2f - %.2f]", min, max));
                    }
                }
                case 5 -> {
                    String prefijo = SistemaGestionPedidos.leerTextoNoVacio("Ingrese prefijo del nombre (ej. Arr, Lec): ");
                    mostrarResultados("Productos que empiezan con \"" + prefijo + "\"", filtrarPorPrefijoNombre(inventarioActual, prefijo));
                    SistemaGestionPedidos.registrarLog("MODULO_2", "Filtro prefijo nombre: " + prefijo);
                }
                case 6 -> System.out.println("Regresando al menú principal...");
            }
        } while (op != 6);
    }

    private static List<Producto> obtenerInventarioActual() {
        int limite = Math.min(SistemaGestionPedidos.totalProductos, SistemaGestionPedidos.catalogo.length);
        return Arrays.asList(Arrays.copyOf(SistemaGestionPedidos.catalogo, limite));
    }

    private static void mostrarResultados(String titulo, List<Producto> lista) {
        System.out.println("\n--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) {
            System.out.println("[AVISO] No se encontraron productos.");
            return;
        }
        for (Producto p : lista) {
            System.out.printf(Locale.ROOT, "  [%s] %s - S/ %.2f | Stock: %d | Vendidos: %d%n",
                    p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock(), p.getUnidadesVendidas());
        }
    }

    private static void menuValidarRegex() {
        System.out.println("\n1. Código (PROD-001)  2. Nombre producto  3. Precio  4. Cantidad (>=0)  5. DNI  6. Teléfono PE (9xxxxxxxx)  7. Correo");
        int tipo = SistemaGestionPedidos.leerEnteroEnRango("Seleccione qué validar (1-7): ", 1, 7);
        String valor = SistemaGestionPedidos.leerTextoNoVacio("Ingrese el valor: ");

        boolean valido = switch (tipo) {
            case 1 -> esCodigoProductoValido(valor);
            case 2 -> esNombreProductoValido(valor);
            case 3 -> esPrecioValido(valor);
            case 4 -> esCantidadValida(valor);
            case 5 -> esDniValido(valor);
            case 6 -> esTelefonoValido(valor);
            case 7 -> esCorreoValido(valor);
            default -> false;
        };

        System.out.println(valido ? "[OK] Dato válido según Regex." : "[ERROR] Dato inválido según Regex.");
        SistemaGestionPedidos.registrarLog("MODULO_2", "Validación regex tipo=" + tipo + " resultado=" + valido);
    }

    public static boolean validarConPatron(String valor, Pattern patron) {
        if (valor == null) {
            return false;
        }
        Matcher matcher = patron.matcher(valor.trim());
        return matcher.matches();
    }

    public static boolean esCodigoProductoValido(String codigo) {
        return validarConPatron(codigo, PATRON_CODIGO_PRODUCTO);
    }

    public static boolean esNombreProductoValido(String nombre) {
        return validarConPatron(nombre, PATRON_NOMBRE_PRODUCTO);
    }

    public static boolean esPrecioValido(String textoPrecio) {
        if (!validarConPatron(textoPrecio, PATRON_PRECIO)) {
            return false;
        }
        double precio = Double.parseDouble(textoPrecio.trim());
        return precio > 0.0;
    }

    public static boolean esCantidadValida(String textoCantidad) {
        if (!validarConPatron(textoCantidad, PATRON_ENTERO_POSITIVO)) {
            return false;
        }
        int cantidad = Integer.parseInt(textoCantidad.trim());
        return cantidad >= 0;
    }

    public static boolean esDniValido(String dni) {
        return validarConPatron(dni, PATRON_DNI);
    }

    public static boolean esTelefonoValido(String telefono) {
        return validarConPatron(telefono, PATRON_TELEFONO_PE);
    }

    public static boolean esCorreoValido(String correo) {
        return validarConPatron(correo, PATRON_CORREO);
    }

    public static String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String limpio = texto.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        limpio = limpio.replace('á', 'a')
                       .replace('é', 'e')
                       .replace('í', 'i')
                       .replace('ó', 'o')
                       .replace('ú', 'u')
                       .replace('ü', 'u');
        return limpio;
    }

    public static List<Producto> buscarPorTexto(List<Producto> inventario, String consulta) {
        List<Producto> resultados = new ArrayList<>();
        if (inventario == null || consulta == null || consulta.trim().isEmpty()) {
            return resultados;
        }
        String criterio = normalizarTexto(consulta);
        for (Producto p : inventario) {
            if (p == null) continue;
            String nombreNorm = normalizarTexto(p.getNombre());
            String codigoNorm = normalizarTexto(p.getCodigo());
            if (nombreNorm.contains(criterio) || codigoNorm.contains(criterio)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    public static List<Producto> filtrarPorCategoria(List<Producto> inventario, String categoria) {
        return buscarPorTexto(inventario, categoria);
    }

    public static List<Producto> filtrarStockBajo(List<Producto> inventario, int stockMaximo) {
        List<Producto> resultados = new ArrayList<>();
        if (inventario == null || stockMaximo < 0) {
            return resultados;
        }
        for (Producto p : inventario) {
            if (p != null && p.getStock() <= stockMaximo) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    public static List<Producto> filtrarPorRangoPrecio(List<Producto> inventario, double minPrecio, double maxPrecio) {
        List<Producto> resultados = new ArrayList<>();
        if (inventario == null || minPrecio < 0 || maxPrecio < minPrecio) {
            return resultados;
        }
        for (Producto p : inventario) {
            if (p != null && p.getPrecio() >= minPrecio && p.getPrecio() <= maxPrecio) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    public static List<Producto> filtrarPorPrefijoNombre(List<Producto> inventario, String prefijo) {
        List<Producto> resultados = new ArrayList<>();
        if (inventario == null || prefijo == null || prefijo.trim().isEmpty()) {
            return resultados;
        }
        String prefNorm = normalizarTexto(prefijo);
        for (Producto p : inventario) {
            if (p != null && normalizarTexto(p.getNombre()).startsWith(prefNorm)) {
                resultados.add(p);
            }
        }
        return resultados;
    }
}