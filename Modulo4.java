package sistemagestionpedidos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import sistemagestionpedidos.SistemaGestionPedidos.Cliente;
import sistemagestionpedidos.SistemaGestionPedidos.Producto;

/**
 * MÓDULO 4: COMPROBANTES, REPORTES Y LOGS
 * Responsable: Alexandra Maria Fe Contreras Gutierrez
 */
public class Modulo4 {

    private static final String[] DIAS = {
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    };

    private static final BigDecimal FACTOR_IGV = new BigDecimal("1.18");

    public static void modulo4ComprobantesReportesYLogs() {
        int opcion;
        do {
            imprimirSubmenu();
            opcion = SistemaGestionPedidos.leerEnteroEnRango("Seleccione una opción (1-6): ", 1, 6);

            switch (opcion) {
                case 1 -> emitirComprobanteRapido();
                case 2 -> reporteCatalogoYStock();
                case 3 -> reporteMatrizVentasBase();
                case 4 -> mostrarBitacoraBase();
                case 5 -> registrarEntradaManualBitacora();
                case 6 -> System.out.println("Volviendo al menú principal...");
            }
        } while (opcion != 6);
    }

    private static void imprimirSubmenu() {
        System.out.println("\n--- MÓDULO 4: COMPROBANTES, REPORTES Y LOGS (ALEXANDRA CONTRERAS) ---");
        System.out.println("1. Emitir comprobante de venta (Boleta)");
        System.out.println("2. Reporte de catálogo, stock y unidades vendidas");
        System.out.println("3. Reporte de matriz de ventas (7 días x 3 turnos)");
        System.out.println("4. Ver bitácora del sistema (Logs)");
        System.out.println("5. Registrar log manual en bitácora");
        System.out.println("6. Volver al menú principal");
    }

    // =========================================================================
    // 1. EMISIÓN DE COMPROBANTES CON REDONDEO FINANCIERO PRECISO
    // =========================================================================
    public static void emitirComprobanteRapido() {
        if (SistemaGestionPedidos.totalProductos == 0) {
            System.out.println("[AVISO] No hay productos en el catálogo para emitir comprobante.");
            return;
        }

        int limiteProd = Math.min(SistemaGestionPedidos.totalProductos, SistemaGestionPedidos.catalogo.length);
        System.out.println("\n--- SELECCIÓN DE PRODUCTO ---");
        for (int i = 0; i < limiteProd; i++) {
            Producto p = SistemaGestionPedidos.catalogo[i];
            if (p != null) {
                System.out.printf(Locale.ROOT, "  %s | %-22s | S/ %6.2f | Stock: %d%n",
                        p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        }

        String codigo = SistemaGestionPedidos.leerTextoNoVacio("Código del producto: ");
        Producto seleccionado = null;
        for (int i = 0; i < limiteProd; i++) {
            Producto p = SistemaGestionPedidos.catalogo[i];
            if (p != null && p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(codigo)) {
                seleccionado = p;
                break;
            }
        }

        if (seleccionado == null) {
            System.out.println("[AVISO] Producto no encontrado.");
            return;
        }

        if (seleccionado.getStock() <= 0) {
            System.out.println("[AVISO] Stock insuficiente para este producto.");
            return;
        }

        int cantidad = SistemaGestionPedidos.leerEnteroEnRango(
                "Cantidad (máx. " + seleccionado.getStock() + "): ", 1, seleccionado.getStock());

        String dniIngresado = SistemaGestionPedidos.leerTexto("DNI cliente (opcional, Enter para Público General): ");
        Cliente clienteEncontrado = buscarClientePorDni(dniIngresado);

        String nombreCliente = "Público General";
        String dniCliente = "00000000";

        if (clienteEncontrado != null) {
            if (clienteEncontrado.getNombre() != null && !clienteEncontrado.getNombre().trim().isEmpty()) {
                nombreCliente = clienteEncontrado.getNombre().trim();
            }
            if (clienteEncontrado.getDni() != null && !clienteEncontrado.getDni().trim().isEmpty()) {
                dniCliente = clienteEncontrado.getDni().trim();
            }
        } else if (!dniIngresado.isEmpty()) {
            dniCliente = dniIngresado;
        }

        imprimirComprobanteVenta(nombreCliente, dniCliente, seleccionado.getNombre(), cantidad, seleccionado.getPrecio());
    }

    public static void imprimirComprobanteVenta(
            String nombreCliente, String dniCliente, String nombreProducto, int cantidad, double precioUnitario) {

        String cliNombre = (nombreCliente == null || nombreCliente.trim().isEmpty())
                ? "Público General" : nombreCliente.trim();
        String cliDni = (dniCliente == null || dniCliente.trim().isEmpty())
                ? "00000000" : dniCliente.trim();
        String prodNombre = (nombreProducto == null || nombreProducto.trim().isEmpty())
                ? "Producto" : nombreProducto.trim();

        BigDecimal bdPrecio = BigDecimal.valueOf(Math.max(0.0, precioUnitario));
        BigDecimal bdCant = BigDecimal.valueOf(Math.max(0, cantidad));

        BigDecimal total = bdPrecio.multiply(bdCant).setScale(2, RoundingMode.HALF_UP);
        BigDecimal opGravada = total.divide(FACTOR_IGV, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(opGravada).setScale(2, RoundingMode.HALF_UP);

        String linea = "==============================================";
        System.out.println("\n" + linea);
        System.out.println("       MINIMARKET - BOLETA DE VENTA         ");
        System.out.println(linea);
        System.out.printf(Locale.ROOT, "%-14s: %-28s%n", "Cliente", cliNombre);
        System.out.printf(Locale.ROOT, "%-14s: %-28s%n", "DNI / Doc.", cliDni);
        System.out.println("----------------------------------------------");
        System.out.printf(Locale.ROOT, "%-24s %4s %14s%n", "Descripción", "Cant", "Importe");
        System.out.println("----------------------------------------------");
        System.out.printf(Locale.ROOT, "%-24s %4d S/ %11.2f%n",
                truncarTexto(prodNombre, 24), Math.max(0, cantidad), total.doubleValue());
        System.out.println("----------------------------------------------");
        System.out.printf(Locale.ROOT, "%-30s S/ %11.2f%n", "OP. GRAVADA", opGravada.doubleValue());
        System.out.printf(Locale.ROOT, "%-30s S/ %11.2f%n", "IGV (18%)", igv.doubleValue());
        System.out.printf(Locale.ROOT, "%-30s S/ %11.2f%n", "TOTAL A PAGAR", total.doubleValue());
        System.out.println(linea);

        registrarLogSeguro("MODULO_4", String.format(Locale.ROOT,
                "Boleta emitida | Cliente: %s (%s) | %s x%d | Total: S/ %.2f",
                cliNombre, cliDni, prodNombre, cantidad, total.doubleValue()));
    }

    // =========================================================================
    // 2. REPORTES DE CATÁLOGO Y MATRIZ DE VENTAS
    // =========================================================================
    public static void reporteCatalogoYStock() {
        System.out.println("\n================ REPORTE DE CATÁLOGO ================");
        System.out.printf(Locale.ROOT, "%-8s | %-22s | %-9s | %-6s | %-8s%n",
                "Código", "Producto", "Precio", "Stock", "Vendidos");
        System.out.println("----------------------------------------------------------------");

        int limite = Math.min(SistemaGestionPedidos.totalProductos, SistemaGestionPedidos.catalogo.length);
        if (limite == 0) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (int i = 0; i < limite; i++) {
            Producto p = SistemaGestionPedidos.catalogo[i];
            if (p != null) {
                System.out.printf(Locale.ROOT, "%-8s | %-22s | S/ %6.2f | %6d | %8d%n",
                        safeString(p.getCodigo(), "N/A"),
                        truncarTexto(safeString(p.getNombre(), "Sin nombre"), 22),
                        p.getPrecio(),
                        p.getStock(),
                        p.getUnidadesVendidas());
            }
        }
        System.out.println("----------------------------------------------------------------");
    }

    public static void reporteMatrizVentasBase() {
        double[][] matriz = SistemaGestionPedidos.matrizVentas;
        String separador = "================================================================";

        System.out.println("\n" + separador);
        System.out.println("        REPORTE DE MATRIZ DE VENTAS (7 DÍAS x 3 TURNOS)       ");
        System.out.println(separador);

        if (matriz == null || matriz.length == 0) {
            System.out.println("[AVISO] Matriz de ventas vacía o no inicializada.");
            System.out.println(separador);
            return;
        }

        System.out.printf(Locale.ROOT, "%-12s | %-12s | %-12s | %-12s%n",
                "Día", "Mañana", "Tarde", "Noche");
        System.out.println("----------------------------------------------------------------");

        int filas = Math.min(DIAS.length, matriz.length);
        for (int i = 0; i < filas; i++) {
            double col0 = (matriz[i] != null && matriz[i].length > 0) ? matriz[i][0] : 0.0;
            double col1 = (matriz[i] != null && matriz[i].length > 1) ? matriz[i][1] : 0.0;
            double col2 = (matriz[i] != null && matriz[i].length > 2) ? matriz[i][2] : 0.0;

            System.out.printf(Locale.ROOT, "%-12s | S/ %9.2f | S/ %9.2f | S/ %9.2f%n",
                    DIAS[i], col0, col1, col2);
        }

        System.out.println(separador);
    }

    // =========================================================================
    // 3. BITÁCORA / LOGS
    // =========================================================================
    public static void mostrarBitacoraBase() {
        String[] logs = SistemaGestionPedidos.bitacora;
        int total = SistemaGestionPedidos.totalLogs;
        String separador = "================================================================";

        System.out.println("\n" + separador);
        System.out.println("               BITÁCORA DE OPERACIONES (LOGS)                 ");
        System.out.println(separador);

        if (logs == null || logs.length == 0 || total <= 0) {
            System.out.println("No hay registros en la bitácora.");
            System.out.println(separador);
            return;
        }

        int limite = Math.min(total, logs.length);
        for (int i = 0; i < limite; i++) {
            String entrada = (logs[i] == null || logs[i].trim().isEmpty())
                    ? "[SIN_DETALLE] Registro vacío" : logs[i];
            System.out.printf("%3d. %s%n", i + 1, entrada);
        }

        System.out.println(separador);
    }

    public static void registrarEntradaManualBitacora() {
        String modulo = SistemaGestionPedidos.leerTextoNoVacio("Módulo / etiqueta: ");
        String detalle = SistemaGestionPedidos.leerTextoNoVacio("Descripción del evento: ");
        registrarLogSeguro(modulo, detalle);
        System.out.println("[OK] Evento registrado en bitácora.");
    }

    public static void registrarLogSeguro(String modulo, String detalle) {
        String modLimpio = safeString(modulo, "MODULO_4").toUpperCase(Locale.ROOT);
        String detLimpio = safeString(detalle, "Evento sin descripción");
        String mensaje = "[" + modLimpio + "] " + detLimpio;

        String[] logs = SistemaGestionPedidos.bitacora;
        if (logs == null || logs.length == 0) {
            return;
        }

        int total = SistemaGestionPedidos.totalLogs;
        if (total < logs.length) {
            logs[total] = mensaje;
            SistemaGestionPedidos.totalLogs = total + 1;
        } else {
            for (int i = 1; i < logs.length; i++) {
                logs[i - 1] = logs[i];
            }
            logs[logs.length - 1] = mensaje;
            SistemaGestionPedidos.totalLogs = logs.length;
        }
    }

    // =========================================================================
    // 4. HELPERS INTERNOS DEL MÓDULO 4
    // =========================================================================
    private static Cliente buscarClientePorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return null;
        }
        String buscado = dni.trim();
        int limite = Math.min(SistemaGestionPedidos.totalClientes, SistemaGestionPedidos.clientes.length);
        for (int i = 0; i < limite; i++) {
            Cliente c = SistemaGestionPedidos.clientes[i];
            if (c != null && buscado.equals(c.getDni())) {
                return c;
            }
        }
        return null;
    }

    private static String safeString(String valor, String porDefecto) {
        if (valor == null || valor.trim().isEmpty()) {
            return porDefecto;
        }
        return valor.trim();
    }

    private static String truncarTexto(String texto, int maxLen) {
        if (texto == null) return "";
        if (texto.length() <= maxLen) return texto;
        return texto.substring(0, Math.max(0, maxLen - 3)) + "...";
    }
}