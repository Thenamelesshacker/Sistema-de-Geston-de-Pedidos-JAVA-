package sistemagestionpedidos;

import java.util.Scanner;

/**
 * MÓDULO 5: INTEGRACIÓN GENERAL Y MENÚ PRINCIPAL
 * Responsable: Piero Sebastián Juárez Gómez
 * Proyecto: Sistema de Ventas y Control de Inventario (Minimarket)
 */
public class SistemaGestionPedidos {

    // =========================================================================
    // ESTRUCTURAS GLOBALES COMPARTIDAS ENTRE MÓDULOS
    // =========================================================================
    public static Producto[] catalogo = new Producto[50];
    public static int totalProductos = 0;

    public static Cliente[] clientes = new Cliente[50];
    public static int totalClientes = 0;

    // Matriz 2D [7 días: 0=Lun..6=Dom] x [3 turnos: 0=Mañana, 1=Tarde, 2=Noche]
    public static double[][] matrizVentas = new double[7][3];

    // Bitácora en memoria de operaciones ejecutadas
    public static String[] bitacora = new String[200];
    public static int totalLogs = 0;

    // Único Scanner centralizado para evitar conflictos de buffer en consola
    private static final Scanner SCANNER = new Scanner(System.in);

    // =========================================================================
    // PUNTO DE ENTRADA (MAIN) - CONTROL DE FLUJO Y EXCEPCIONES
    // =========================================================================
    public static void main(String[] args) {
        inicializarEntorno();

        int opcion = -1;
        do {
            imprimirMenuPrincipal();
            opcion = leerEnteroEnRango("Seleccione una opción (1-6): ", 1, 6);

            try {
                switch (opcion) {
                    case 1 -> {
                        registrarLog("MODULO_5", "Ingreso a Módulo 1 (Catálogo - Danna Mendoza)");
                        modulo1Catalogo();
                    }
                    case 2 -> {
                        registrarLog("MODULO_5", "Ingreso a Módulo 2 (Validaciones/Búsquedas - Andree Chipana)");
                        modulo2ValidacionesYBusquedas();
                    }
                    case 3 -> {
                        registrarLog("MODULO_5", "Ingreso a Módulo 3 (Lógica/Matriz 2D - Christian Melgarejo)");
                        modulo3LogicaYMatriz2D();
                    }
                    case 4 -> {
                        registrarLog("MODULO_5", "Ingreso a Módulo 4 (Comprobantes/Reportes/Logs - Alexandra Contreras)");
                        modulo4ComprobantesReportesYLogs();
                    }
                    case 5 -> {
                        registrarLog("MODULO_5", "Ingreso a Herramientas de Integración y QA");
                        menuIntegracionYQA();
                    }
                    case 6 -> {
                        registrarLog("SISTEMA", "Cierre ordenado del sistema por el usuario.");
                        System.out.println("\nCerrando sistema... ¡Buen trabajo, equipo!");
                    }
                    default -> System.out.println("[AVISO] Seleccione una opción válida.");
                }
            } catch (Exception ex) {
                System.out.println("\n[ERROR DE EJECUCIÓN] Se capturó una excepción en el flujo: " + ex.getMessage());
                registrarLog("ERROR_MAIN", "Excepción capturada: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            }

        } while (opcion != 6);

        SCANNER.close();
    }

    // =========================================================================
    // MÉTODOS PROPIOS DEL MÓDULO 5 (MENÚS, QA Y LECTURA CENTRALIZADA)
    // =========================================================================

    private static void inicializarEntorno() {
        cargarDatosEjemplo();
        registrarLog("SISTEMA", "Inicialización de estructuras y datos base completada.");
    }

    private static void imprimirMenuPrincipal() {
        System.out.println("\n================================================================");
        System.out.println("        MINIMARKET - SISTEMA INTEGRADO DE GESTIÓN (MÓDULO 5)   ");
        System.out.println("================================================================");
        System.out.println("  1. Módulo 1 | Catálogo y Registros     (Danna Mendoza)");
        System.out.println("  2. Módulo 2 | Validaciones y Búsquedas (Andree Chipana)");
        System.out.println("  3. Módulo 3 | Lógica y Matriz 2D       (Christian Melgarejo)");
        System.out.println("  4. Módulo 4 | Boletas, Reportes y Logs (Alexandra Contreras)");
        System.out.println("  5. Módulo 5 | Estado del Sistema y QA  (Piero Juárez)");
        System.out.println("  6. Salir");
        System.out.println("================================================================");
    }

    private static void menuIntegracionYQA() {
        int subOp;
        do {
            System.out.println("\n--- MÓDULO 5: CONTROL DE INTEGRACIÓN Y QA (PIERO JUÁREZ) ---");
            System.out.println("1. Ver resumen de capacidad y registros cargados");
            System.out.println("2. Recargar datos de prueba iniciales");
            System.out.println("3. Verificar estado de la Matriz 2D (7x3)");
            System.out.println("4. Volver al menú principal");

            subOp = leerEnteroEnRango("Elija una opción (1-4): ", 1, 4);

            switch (subOp) {
                case 1 -> {
                    System.out.printf("Productos registrados : %d / %d%n", totalProductos, catalogo.length);
                    System.out.printf("Clientes registrados  : %d / %d%n", totalClientes, clientes.length);
                    System.out.printf("Entradas en bitácora  : %d / %d%n", totalLogs, bitacora.length);
                }
                case 2 -> {
                    cargarDatosEjemplo();
                    registrarLog("QA", "Datos de prueba recargados manualmente.");
                    System.out.println("[OK] Datos de prueba cargados.");
                }
                case 3 -> {
                    double suma = 0;
                    for (int i = 0; i < matrizVentas.length; i++) {
                        for (int j = 0; j < matrizVentas[i].length; j++) {
                            suma += matrizVentas[i][j];
                        }
                    }
                    System.out.printf("Matriz 2D operativa (%dx%d). Suma acumulada actual: S/ %.2f%n",
                            matrizVentas.length, matrizVentas[0].length, suma);
                }
                case 4 -> System.out.println("Regresando al menú principal...");
            }
        } while (subOp != 4);
    }

    // -------------------------------------------------------------------------
    // Helpers de lectura centralizada (reutilizables por todos los módulos)
    // -------------------------------------------------------------------------

    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return SCANNER.nextLine().trim();
    }

    public static String leerTextoNoVacio(String mensaje) {
        while (true) {
            String entrada = leerTexto(mensaje);
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("[AVISO] El campo no puede estar vacío.");
        }
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Ingrese un número entero válido.");
            }
        }
    }

    public static int leerEnteroEnRango(String mensaje, int min, int max) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.printf("[AVISO] Ingrese un valor entre %d y %d.%n", min, max);
        }
    }

    public static double leerDecimalPositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = SCANNER.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(linea);
                if (valor > 0) {
                    return valor;
                }
                System.out.println("[AVISO] El monto debe ser mayor que 0.");
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Ingrese un número decimal válido (ej. 12.50).");
            }
        }
    }

    // =========================================================================
    // CLASES ENTIDAD BASE (Módulo 1 - Danna Mendoza)
    // =========================================================================
    public static class Producto {
        private String codigo;
        private String nombre;
        private double precio;
        private int stock;
        private int unidadesVendidas;

        public Producto(String codigo, String nombre, double precio, int stock) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
            this.unidadesVendidas = 0;
        }

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }

        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }

        public int getUnidadesVendidas() { return unidadesVendidas; }
        public void setUnidadesVendidas(int unidadesVendidas) { this.unidadesVendidas = unidadesVendidas; }
    }

    public static class Cliente {
        private String dni;
        private String nombre;
        private String telefono;

        public Cliente(String dni, String nombre, String telefono) {
            this.dni = dni;
            this.nombre = nombre;
            this.telefono = telefono;
        }

        public String getDni() { return dni; }
        public void setDni(String dni) { this.dni = dni; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
    }

    // =========================================================================
    // CONEXIONES HACIA LOS MÓDULOS 1 A 4
    // =========================================================================

    // Módulo 1 -> Danna Nicolle Belén Mendoza Moya
    public static void modulo1Catalogo() {
        Modulo1.modulo1Catalogo();
    }

    // Módulo 2 -> Andree Wilfredo Chipana Rojas
    public static void modulo2ValidacionesYBusquedas() {
        Modulo2.modulo2ValidacionesYBusquedas();
    }

    // Módulo 3 -> Christian Eduardo Melgarejo Montes
    public static void modulo3LogicaYMatriz2D() {
        Modulo3.modulo3LogicaYMatriz2D();
    }

    // Módulo 4 -> Alexandra Maria Fe Contreras Gutierrez
    public static void modulo4ComprobantesReportesYLogs() {
        Modulo4.modulo4ComprobantesReportesYLogs();
    }

    // =========================================================================
    // UTILIDADES COMPARTIDAS
    // =========================================================================
    public static void registrarLog(String modulo, String detalle) {
        if (totalLogs < bitacora.length) {
            bitacora[totalLogs++] = "[" + modulo + "] " + detalle;
        }
    }

    private static void cargarDatosEjemplo() {
        if (totalProductos == 0) {
            catalogo[totalProductos++] = new Producto("P001", "Arroz Costeño 1kg", 4.50, 30);
            catalogo[totalProductos++] = new Producto("P002", "Aceite Primor 1L", 8.90, 20);
            catalogo[totalProductos++] = new Producto("P003", "Leche Gloria Tarro", 4.20, 50);
        }
        if (totalClientes == 0) {
            clientes[totalClientes++] = new Cliente("70112233", "Cliente Prueba", "999888777");
        }
    }
}