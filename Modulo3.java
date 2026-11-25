package sistemagestionpedidos;

import java.util.Locale;
import sistemagestionpedidos.SistemaGestionPedidos.Cliente;
import sistemagestionpedidos.SistemaGestionPedidos.Producto;

/**
 * MÓDULO 3: LÓGICA DE NEGOCIO (VENTAS / PEDIDOS) Y MATRIZ 2D DE VENTAS
 * Responsable: Christian Eduardo Melgarejo Montes
 */
public class Modulo3 {
    public static void main(String[] args) {
        
    }

    private static final String[] DIAS = {
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    };

    private static final String[] TURNOS = {
        "Mañana", "Tarde", "Noche"
    };

    private static final double IGV = 0.18;

    public static void modulo3LogicaYMatriz2D() {
        int opcion;
        do {
            imprimirSubmenu();
            opcion = SistemaGestionPedidos.leerEnteroEnRango("Seleccione una opción (1-6): ", 1, 6);

            switch (opcion) {
                case 1 -> registrarVentaOPedido();
                case 2 -> registrarIngresoManualMatriz();
                case 3 -> mostrarMatrizVentas();
                case 4 -> mostrarResumenPorDiaYTurno();
                case 5 -> mostrarMejorDiaYTurno();
                case 6 -> System.out.println("Volviendo al menú principal...");
            }
        } while (opcion != 6);
    }

    private static void imprimirSubmenu() {
        System.out.println("\n--- MÓDULO 3: LÓGICA DE NEGOCIO Y MATRIZ 2D (CHRISTIAN MELGAREJO) ---");
        System.out.println("1. Registrar venta / pedido (actualiza stock y Matriz 2D)");
        System.out.println("2. Registrar monto directo en Matriz 2D (Día / Turno)");
        System.out.println("3. Ver Matriz 2D de ventas (7 días x 3 turnos)");
        System.out.println("4. Resumen de totales por día y por turno");
        System.out.println("5. Consultar día y turno con mayores ventas");
        System.out.println("6. Volver al menú principal");
    }

    // =========================================================================
    // 1. LÓGICA DE VENTA / PEDIDO CON ACTUALIZACIÓN DE STOCK Y MATRIZ 2D
    // =========================================================================
    public static void registrarVentaOPedido() {
        if (SistemaGestionPedidos.totalProductos == 0) {
            System.out.println("[AVISO] No hay productos registrados en el catálogo.");
            return;
        }

        listarCatalogoResumido();
        String codigo = SistemaGestionPedidos.leerTextoNoVacio("Ingrese el código del producto: ");
        int indiceProd = buscarIndiceProducto(codigo);

        if (indiceProd == -1) {
            System.out.println("[AVISO] No se encontró un producto con el código ingresado.");
            return;
        }

        Producto prod = SistemaGestionPedidos.catalogo[indiceProd];
        if (prod.getStock() <= 0) {
            System.out.println("[AVISO] El producto no tiene stock disponible.");
            return;
        }

        int cantidad = SistemaGestionPedidos.leerEnteroEnRango(
                "Cantidad a vender (disponible " + prod.getStock() + "): ", 1, prod.getStock());

        int dia = seleccionarDia();
        int turno = seleccionarTurno();

        String dniCliente = SistemaGestionPedidos.leerTexto("DNI del cliente (opcional, Enter para omitir): ");
        String nombreCliente = "Público General";
        if (!dniCliente.isEmpty()) {
            Cliente cli = buscarClientePorDni(dniCliente);
            if (cli != null) {
                nombreCliente = cli.getNombre() + " (" + cli.getDni() + ")";
            } else {
                nombreCliente = "DNI " + dniCliente;
            }
        }

        double subtotal = prod.getPrecio() * cantidad;
        double montoIgv = subtotal * IGV;
        double total = subtotal + montoIgv;

        // Actualizar inventario y estadísticas del producto
        prod.setStock(prod.getStock() - cantidad);
        prod.setUnidadesVendidas(prod.getUnidadesVendidas() + cantidad);

        // Acumular venta en la matriz 2D [dia][turno]
        acumularVentaEnMatriz(dia, turno, total);

        String detalleLog = String.format(Locale.ROOT,
                "Venta %dx %s (%s) | %s %s | Total: S/ %.2f | Cliente: %s",
                cantidad, prod.getNombre(), prod.getCodigo(), DIAS[dia], TURNOS[turno], total, nombreCliente);
        SistemaGestionPedidos.registrarLog("MODULO_3", detalleLog);

        System.out.printf(Locale.ROOT,
                "%n[OK] Venta registrada (%s - %s)%n" +
                "     Producto : %s x%d%n" +
                "     Subtotal : S/ %.2f%n" +
                "     IGV (18%%): S/ %.2f%n" +
                "     Total    : S/ %.2f%n" +
                "     Stock rest.: %d%n",
                DIAS[dia], TURNOS[turno], prod.getNombre(), cantidad,
                subtotal, montoIgv, total, prod.getStock());
    }

    // =========================================================================
    // 2. OPERACIONES DIRECTAS Y CONSULTAS SOBRE MATRIZ 2D [7][3]
    // =========================================================================
    public static void registrarIngresoManualMatriz() {
        int dia = seleccionarDia();
        int turno = seleccionarTurno();
        double monto = SistemaGestionPedidos.leerDecimalPositivo("Ingrese monto a sumar (S/): ");

        acumularVentaEnMatriz(dia, turno, monto);
        SistemaGestionPedidos.registrarLog("MODULO_3",
                String.format(Locale.ROOT, "Ingreso manual en Matriz 2D [%s - %s]: +S/ %.2f",
                        DIAS[dia], TURNOS[turno], monto));

        System.out.printf(Locale.ROOT, "[OK] Se agregaron S/ %.2f a %s (%s). Nuevo acumulado: S/ %.2f%n",
                monto, DIAS[dia], TURNOS[turno], SistemaGestionPedidos.matrizVentas[dia][turno]);
    }

    public static void acumularVentaEnMatriz(int dia, int turno, double monto) {
        double[][] matriz = SistemaGestionPedidos.matrizVentas;
        if (matriz == null || dia < 0 || dia >= matriz.length) {
            return;
        }
        if (turno < 0 || turno >= matriz[dia].length) {
            return;
        }
        if (monto > 0) {
            matriz[dia][turno] += monto;
        }
    }

    public static void mostrarMatrizVentas() {
        double[][] matriz = SistemaGestionPedidos.matrizVentas;
        if (matriz == null || matriz.length == 0) {
            System.out.println("[AVISO] La matriz de ventas no está inicializada.");
            return;
        }

        System.out.println("\n==================== MATRIZ DE VENTAS (S/) ===================");
        System.out.printf("%-12s | %-12s | %-12s | %-12s | %-12s%n",
                "Día", "Mañana", "Tarde", "Noche", "Total Día");
        System.out.println("----------------------------------------------------------------");

        double granTotal = 0.0;
        int filas = Math.min(DIAS.length, matriz.length);

        for (int i = 0; i < filas; i++) {
            double totalFila = 0.0;
            double manana = matriz[i].length > 0 ? matriz[i][0] : 0.0;
            double tarde = matriz[i].length > 1 ? matriz[i][1] : 0.0;
            double noche = matriz[i].length > 2 ? matriz[i][2] : 0.0;

            for (int j = 0; j < matriz[i].length; j++) {
                totalFila += matriz[i][j];
            }
            granTotal += totalFila;

            System.out.printf(Locale.ROOT, "%-12s | S/ %9.2f | S/ %9.2f | S/ %9.2f | S/ %9.2f%n",
                    DIAS[i], manana, tarde, noche, totalFila);
        }

        System.out.println("----------------------------------------------------------------");
        System.out.printf(Locale.ROOT, "TOTAL GENERAL ACUMULADO: S/ %.2f%n", granTotal);
    }

    public static void mostrarResumenPorDiaYTurno() {
        double[][] matriz = SistemaGestionPedidos.matrizVentas;
        int filas = Math.min(DIAS.length, matriz.length);

        System.out.println("\n--- TOTALES POR DÍA ---");
        for (int i = 0; i < filas; i++) {
            double sumaDia = 0.0;
            for (int j = 0; j < matriz[i].length; j++) {
                sumaDia += matriz[i][j];
            }
            System.out.printf(Locale.ROOT, "  %-10s: S/ %.2f%n", DIAS[i], sumaDia);
        }

        System.out.println("\n--- TOTALES POR TURNO ---");
        for (int j = 0; j < TURNOS.length; j++) {
            double sumaTurno = 0.0;
            for (int i = 0; i < filas; i++) {
                if (j < matriz[i].length) {
                    sumaTurno += matriz[i][j];
                }
            }
            System.out.printf(Locale.ROOT, "  %-10s: S/ %.2f%n", TURNOS[j], sumaTurno);
        }
    }

    public static void mostrarMejorDiaYTurno() {
        double[][] matriz = SistemaGestionPedidos.matrizVentas;
        int filas = Math.min(DIAS.length, matriz.length);

        int mejorDiaIdx = 0;
        double mejorDiaMonto = -1.0;
        for (int i = 0; i < filas; i++) {
            double sumaDia = 0.0;
            for (int j = 0; j < matriz[i].length; j++) {
                sumaDia += matriz[i][j];
            }
            if (sumaDia > mejorDiaMonto) {
                mejorDiaMonto = sumaDia;
                mejorDiaIdx = i;
            }
        }

        int mejorTurnoIdx = 0;
        double mejorTurnoMonto = -1.0;
        for (int j = 0; j < TURNOS.length; j++) {
            double sumaTurno = 0.0;
            for (int i = 0; i < filas; i++) {
                if (j < matriz[i].length) {
                    sumaTurno += matriz[i][j];
                }
            }
            if (sumaTurno > mejorTurnoMonto) {
                mejorTurnoMonto = sumaTurno;
                mejorTurnoIdx = j;
            }
        }

        System.out.println("\n--- RENDIMIENTO DESTACADO ---");
        if (mejorDiaMonto <= 0) {
            System.out.println("  Aún no se registran ventas en la matriz.");
        } else {
            System.out.printf(Locale.ROOT, "  Día con mayor venta  : %s (S/ %.2f)%n", DIAS[mejorDiaIdx], mejorDiaMonto);
            System.out.printf(Locale.ROOT, "  Turno con mayor venta: %s (S/ %.2f)%n", TURNOS[mejorTurnoIdx], mejorTurnoMonto);
        }
    }

    // =========================================================================
    // 3. HELPERS INTERNOS DEL MÓDULO 3
    // =========================================================================
    private static int seleccionarDia() {
        System.out.println("Seleccione día:");
        for (int i = 0; i < DIAS.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, DIAS[i]);
        }
        return SistemaGestionPedidos.leerEnteroEnRango("Día (1-7): ", 1, 7) - 1;
    }

    private static int seleccionarTurno() {
        System.out.println("Seleccione turno:");
        for (int j = 0; j < TURNOS.length; j++) {
            System.out.printf("  %d. %s%n", j + 1, TURNOS[j]);
        }
        return SistemaGestionPedidos.leerEnteroEnRango("Turno (1-3): ", 1, 3) - 1;
    }

    private static void listarCatalogoResumido() {
        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");
        int limite = Math.min(SistemaGestionPedidos.totalProductos, SistemaGestionPedidos.catalogo.length);
        for (int i = 0; i < limite; i++) {
            Producto p = SistemaGestionPedidos.catalogo[i];
            if (p != null) {
                System.out.printf(Locale.ROOT, "  %s | %-22s | S/ %6.2f | Stock: %d%n",
                        p.getCodigo(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        }
    }

    private static int buscarIndiceProducto(String codigo) {
        if (codigo == null) return -1;
        String buscado = codigo.trim();
        int limite = Math.min(SistemaGestionPedidos.totalProductos, SistemaGestionPedidos.catalogo.length);
        for (int i = 0; i < limite; i++) {
            Producto p = SistemaGestionPedidos.catalogo[i];
            if (p != null && p.getCodigo() != null && p.getCodigo().equalsIgnoreCase(buscado)) {
                return i;
            }
        }
        return -1;
    }

    private static Cliente buscarClientePorDni(String dni) {
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
}