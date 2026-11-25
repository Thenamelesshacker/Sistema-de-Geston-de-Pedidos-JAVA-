/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemagestionpedidos;

/**
 * MÓDULO 1 -> Danna Nicolle Belén Mendoza Moya
 * Gestión de Catálogo y Registros Base
 *
 * @author USUARIO
 */
public class Modulo1 {
    public static void main(String[] args) {
        
    }

    // Anchos de columna usados en las tablas
    private static final int ANCHO_CODIGO = 10;
    private static final int ANCHO_NOMBRE_PRODUCTO = 28;
    private static final int ANCHO_NOMBRE_CLIENTE = 30;

    public static void modulo1Catalogo() {
        int op;
        do {
            System.out.println("\n--- MÓDULO 1: CATÁLOGO Y REGISTROS BASE (DANNA MENDOZA) ---");
            System.out.println("1. Registrar producto");
            System.out.println("2. Listar productos");
            System.out.println("3. Buscar producto por código");
            System.out.println("4. Actualizar producto");
            System.out.println("5. Registrar cliente");
            System.out.println("6. Listar clientes");
            System.out.println("7. Actualizar cliente");
            System.out.println("8. Volver al menú principal");
            op = SistemaGestionPedidos.leerEnteroEnRango("Elija una opción (1-8): ", 1, 8);

            switch (op) {
                case 1 -> registrarProducto();
                case 2 -> listarProductos();
                case 3 -> buscarProductoPorCodigo();
                case 4 -> actualizarProducto();
                case 5 -> registrarClienteBase();
                case 6 -> listarClientes();
                case 7 -> actualizarCliente();
                case 8 -> System.out.println("Regresando al menú principal...");
            }
        } while (op != 8);
    }

    // -------------------------------------------------------------------------
    // UTILIDADES DE NORMALIZACIÓN Y VALIDACIÓN
    // -------------------------------------------------------------------------

    public static String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace('\u00A0', ' ').strip().replaceAll("\\s+", " ");
    }

    // Código de producto
    public static String normalizarCodigo(String codigo) {
        return normalizarTexto(codigo).toUpperCase();
    }

    // DNI en formato estándar
    public static String normalizarDni(String dni) {
        if (dni == null) {
            return "";
        }
        return dni.replace('\u00A0', ' ').strip();
    }

    // DNI válido
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    // Teléfono en formato estándar
    public static String normalizarTelefono(String telefono) {
        if (telefono == null) {
            return "";
        }
        return telefono.replaceAll("[\\s\\u00A0\\-()]", "");
    }

    // Teléfono válido con solo dígitos, entre 7 y 9
    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{7,9}");
    }

    // Recorta el texto al ancho de la columna
    public static String ajustarTexto(String texto, int ancho) {
        String t = (texto == null) ? "" : texto;
        if (t.length() <= ancho) {
            return t;
        }
        if (ancho <= 3) {
            return t.substring(0, Math.max(ancho, 0));
        }
        return t.substring(0, ancho - 3) + "...";
    }

    public static String leerTextoNormalizado(String mensaje) {
        while (true) {
            String texto = normalizarTexto(SistemaGestionPedidos.leerTextoNoVacio(mensaje));
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("[ERROR] El valor no puede estar vacío.");
        }
    }

    /** Pide un código de producto (sin espacios internos) y lo devuelve en mayúsculas. */
    public static String leerCodigoProducto(String mensaje) {
        while (true) {
            String codigo = normalizarCodigo(SistemaGestionPedidos.leerTextoNoVacio(mensaje));
            if (codigo.isEmpty()) {
                System.out.println("[ERROR] El código no puede estar vacío.");
            } else if (codigo.contains(" ")) {
                System.out.println("[ERROR] El código no debe contener espacios.");
            } else {
                return codigo;
            }
        }
    }

    /** Pide un DNI hasta que sea válido (8 dígitos, sin espacios) y lo devuelve ya normalizado. */
    public static String leerDniValido(String mensaje) {
        while (true) {
            String dni = normalizarDni(SistemaGestionPedidos.leerTextoNoVacio(mensaje));
            if (esDniValido(dni)) {
                return dni;
            }
            System.out.println("[ERROR] DNI inválido. Debe tener exactamente 8 dígitos, sin espacios ni letras.");
        }
    }

    /** Pide un teléfono hasta que sea válido (7 a 9 dígitos) y lo devuelve ya normalizado. */
    public static String leerTelefonoValido(String mensaje) {
        while (true) {
            String telefono = normalizarTelefono(SistemaGestionPedidos.leerTextoNoVacio(mensaje));
            if (esTelefonoValido(telefono)) {
                return telefono;
            }
            System.out.println("[ERROR] Teléfono inválido. Debe tener entre 7 y 9 dígitos (solo números).");
        }
    }

    // -------------------------------------------------------------------------
    // PRODUCTOS
    // -------------------------------------------------------------------------

    // Alta de un producto
    public static void registrarProducto() {
        if (SistemaGestionPedidos.totalProductos >= SistemaGestionPedidos.catalogo.length) {
            System.out.println("[AVISO] El catálogo está lleno (" + SistemaGestionPedidos.catalogo.length + " productos).");
            SistemaGestionPedidos.registrarLog("MODULO_1", "Intento de alta de producto con catálogo lleno.");
            return;
        }

        String codigo = leerCodigoProducto("Código del producto (ej. P004): ");

        // Validación: no permitir códigos repetidos
        if (buscarIndiceProducto(codigo) != -1) {
            System.out.println("[ERROR] Ya existe un producto con el código " + codigo + ".");
            SistemaGestionPedidos.registrarLog("MODULO_1", "Alta rechazada: código de producto duplicado (" + codigo + ").");
            return;
        }

        String nombre = leerTextoNormalizado("Nombre del producto: ");
        double precio = SistemaGestionPedidos.leerDecimalPositivo("Precio unitario (S/): ");
        int stock = SistemaGestionPedidos.leerEnteroEnRango("Stock inicial (0-100000): ", 0, 100000);

        SistemaGestionPedidos.catalogo[SistemaGestionPedidos.totalProductos++] =
                new SistemaGestionPedidos.Producto(codigo, nombre, precio, stock);
        System.out.println("[OK] Producto registrado correctamente.");
        SistemaGestionPedidos.registrarLog("MODULO_1", "Producto registrado: " + codigo + " - " + nombre);
    }

    // Lista todo el catálogo en formato tabular (columnas de ancho fijo)
    public static void listarProductos() {
        if (SistemaGestionPedidos.totalProductos == 0) {
            System.out.println("[AVISO] No hay productos registrados.");
            return;
        }
        String formato = "%-" + ANCHO_CODIGO + "s %-" + ANCHO_NOMBRE_PRODUCTO + "s %10s %8s %10s%n";
        String linea = "-".repeat(ANCHO_CODIGO + ANCHO_NOMBRE_PRODUCTO + 10 + 8 + 10 + 4);

        System.out.println("\n" + linea);
        System.out.printf(formato, "CÓDIGO", "NOMBRE", "PRECIO", "STOCK", "VENDIDOS");
        System.out.println(linea);

        int mostrados = 0;
        for (int i = 0; i < SistemaGestionPedidos.totalProductos; i++) {
            SistemaGestionPedidos.Producto p = SistemaGestionPedidos.catalogo[i];
            if (p == null) {
                continue; // protección ante posiciones vacías
            }
            System.out.printf(formato,
                    ajustarTexto(p.getCodigo(), ANCHO_CODIGO),
                    ajustarTexto(p.getNombre(), ANCHO_NOMBRE_PRODUCTO),
                    String.format("%.2f", p.getPrecio()),
                    String.valueOf(p.getStock()),
                    String.valueOf(p.getUnidadesVendidas()));
            mostrados++;
        }
        System.out.println(linea);
        System.out.println("Total de productos: " + mostrados);
    }

    // Búsqueda de un producto por su código exacto
    public static void buscarProductoPorCodigo() {
        String codigo = normalizarCodigo(SistemaGestionPedidos.leerTextoNoVacio("Código a buscar: "));
        int idx = buscarIndiceProducto(codigo);
        if (idx == -1) {
            System.out.println("[AVISO] No se encontró ningún producto con el código " + codigo + ".");
            return;
        }
        SistemaGestionPedidos.Producto p = SistemaGestionPedidos.catalogo[idx];
        System.out.println("\nProducto encontrado:");
        System.out.println("  Código : " + p.getCodigo());
        System.out.println("  Nombre : " + p.getNombre());
        System.out.printf("  Precio : S/ %.2f%n", p.getPrecio());
        System.out.println("  Stock  : " + p.getStock());
    }

    // Actualiza nombre, precio y/o stock. Solo informa "actualizado" si realmente cambió algo.
    public static void actualizarProducto() {
        String codigo = normalizarCodigo(SistemaGestionPedidos.leerTextoNoVacio("Código del producto a actualizar: "));
        int idx = buscarIndiceProducto(codigo);
        if (idx == -1) {
            System.out.println("[AVISO] No se encontró ningún producto con el código " + codigo + ".");
            return;
        }
        SistemaGestionPedidos.Producto p = SistemaGestionPedidos.catalogo[idx];
        System.out.println("(Presione Enter para mantener el valor actual)");

        int cambios = 0;
        int errores = 0;
        StringBuilder detalle = new StringBuilder();

        // --- Nombre ---
        String nuevoNombre = normalizarTexto(SistemaGestionPedidos.leerTexto("Nombre [" + p.getNombre() + "]: "));
        if (!nuevoNombre.isEmpty()) {
            if (nuevoNombre.equals(p.getNombre())) {
                System.out.println("[AVISO] El nombre ingresado es igual al actual. Sin cambios.");
            } else {
                p.setNombre(nuevoNombre);
                cambios++;
                detalle.append("nombre ");
            }
        }

        // --- Precio ---
        String textoPrecio = normalizarTexto(
                SistemaGestionPedidos.leerTexto("Precio [" + String.format("%.2f", p.getPrecio()) + "]: "))
                .replace(",", ".");
        if (!textoPrecio.isEmpty()) {
            try {
                double precio = Double.parseDouble(textoPrecio);
                if (Double.isNaN(precio) || Double.isInfinite(precio) || precio <= 0) {
                    System.out.println("[ERROR] Precio inválido (debe ser un número mayor a 0). Se mantiene el anterior.");
                    errores++;
                } else if (Math.abs(precio - p.getPrecio()) < 1e-9) {
                    System.out.println("[AVISO] El precio ingresado es igual al actual. Sin cambios.");
                } else {
                    p.setPrecio(precio);
                    cambios++;
                    detalle.append("precio ");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Precio no válido. Se mantiene el anterior.");
                errores++;
            }
        }

        // --- Stock ---
        String textoStock = normalizarTexto(SistemaGestionPedidos.leerTexto("Stock [" + p.getStock() + "]: "));
        if (!textoStock.isEmpty()) {
            try {
                int stock = Integer.parseInt(textoStock);
                if (stock < 0 || stock > 100000) {
                    System.out.println("[ERROR] El stock debe estar entre 0 y 100000. Se mantiene el anterior.");
                    errores++;
                } else if (stock == p.getStock()) {
                    System.out.println("[AVISO] El stock ingresado es igual al actual. Sin cambios.");
                } else {
                    p.setStock(stock);
                    cambios++;
                    detalle.append("stock ");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Stock no válido (debe ser un número entero). Se mantiene el anterior.");
                errores++;
            }
        }

        // --- Resultado real de la operación ---
        if (cambios > 0) {
            System.out.println("[OK] Producto actualizado (" + cambios + " cambio(s)).");
            if (errores > 0) {
                System.out.println("[AVISO] " + errores + " dato(s) no se actualizaron por ser inválidos.");
            }
            SistemaGestionPedidos.registrarLog("MODULO_1", "Producto actualizado: " + p.getCodigo()
                    + " [" + detalle.toString().trim() + "] - " + p.getNombre());
        } else if (errores > 0) {
            System.out.println("[ERROR] No se actualizó el producto: los datos ingresados no son válidos.");
            SistemaGestionPedidos.registrarLog("MODULO_1", "Actualización de producto fallida (datos inválidos): " + p.getCodigo());
        } else {
            System.out.println("[AVISO] No se realizaron cambios en el producto.");
        }
    }

    // Devuelve la posición del producto en el arreglo, o -1 si no existe (o si el código es null/vacío).
    public static int buscarIndiceProducto(String codigo) {
        if (codigo == null) {
            return -1;
        }
        String buscado = normalizarCodigo(codigo);
        if (buscado.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < SistemaGestionPedidos.totalProductos; i++) {
            SistemaGestionPedidos.Producto p = SistemaGestionPedidos.catalogo[i];
            if (p != null && p.getCodigo() != null && normalizarCodigo(p.getCodigo()).equals(buscado)) {
                return i;
            }
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // CLIENTES
    // -------------------------------------------------------------------------

    public static void registrarClienteBase() {
        if (SistemaGestionPedidos.totalClientes >= SistemaGestionPedidos.clientes.length) {
            System.out.println("[AVISO] El registro de clientes está lleno (" + SistemaGestionPedidos.clientes.length + ").");
            SistemaGestionPedidos.registrarLog("MODULO_1", "Intento de alta de cliente con registro lleno.");
            return;
        }

        String dni = leerDniValido("DNI del cliente (8 dígitos): ");

        if (existeClientePorDni(dni)) {
            System.out.println("[ERROR] Ya existe un cliente registrado con el DNI " + dni + ".");
            SistemaGestionPedidos.registrarLog("MODULO_1", "Alta de cliente rechazada: DNI duplicado (" + dni + ").");
            return;
        }

        String nombre = leerTextoNormalizado("Nombre completo: ");
        String telefono = leerTelefonoValido("Teléfono (7 a 9 dígitos): ");

        SistemaGestionPedidos.clientes[SistemaGestionPedidos.totalClientes++] =
                new SistemaGestionPedidos.Cliente(dni, nombre, telefono);
        System.out.println("[OK] Cliente registrado correctamente.");
        SistemaGestionPedidos.registrarLog("MODULO_1", "Cliente registrado: DNI " + dni + " - " + nombre);
    }

    // Lista todos los clientes en formato tabular (columnas de ancho fijo)
    public static void listarClientes() {
        if (SistemaGestionPedidos.totalClientes == 0) {
            System.out.println("[AVISO] No hay clientes registrados.");
            return;
        }
        String formato = "%-10s %-" + ANCHO_NOMBRE_CLIENTE + "s %-12s%n";
        String linea = "-".repeat(10 + ANCHO_NOMBRE_CLIENTE + 12 + 2);

        System.out.println("\n" + linea);
        System.out.printf(formato, "DNI", "NOMBRE", "TELÉFONO");
        System.out.println(linea);

        int mostrados = 0;
        for (int i = 0; i < SistemaGestionPedidos.totalClientes; i++) {
            SistemaGestionPedidos.Cliente c = SistemaGestionPedidos.clientes[i];
            if (c == null) {
                continue; // protección ante posiciones vacías
            }
            System.out.printf(formato,
                    ajustarTexto(c.getDni(), 10),
                    ajustarTexto(c.getNombre(), ANCHO_NOMBRE_CLIENTE),
                    ajustarTexto(c.getTelefono(), 12));
            mostrados++;
        }
        System.out.println(linea);
        System.out.println("Total de clientes: " + mostrados);
    }

    // Actualiza nombre y/o teléfono de un cliente existente (el DNI es la clave y no se modifica).
    public static void actualizarCliente() {
        String dni = normalizarDni(SistemaGestionPedidos.leerTextoNoVacio("DNI del cliente a actualizar: "));
        if (!esDniValido(dni)) {
            System.out.println("[ERROR] DNI inválido. Debe tener exactamente 8 dígitos, sin espacios ni letras.");
            return;
        }
        int idx = buscarIndiceCliente(dni);
        if (idx == -1) {
            System.out.println("[AVISO] No se encontró ningún cliente con el DNI " + dni + ".");
            return;
        }
        SistemaGestionPedidos.Cliente actual = SistemaGestionPedidos.clientes[idx];
        System.out.println("(Presione Enter para mantener el valor actual)");

        String nombre = actual.getNombre();
        String telefono = actual.getTelefono();
        int cambios = 0;
        int errores = 0;
        StringBuilder detalle = new StringBuilder();

        // --- Nombre ---
        String nuevoNombre = normalizarTexto(SistemaGestionPedidos.leerTexto("Nombre [" + actual.getNombre() + "]: "));
        if (!nuevoNombre.isEmpty()) {
            if (nuevoNombre.equals(actual.getNombre())) {
                System.out.println("[AVISO] El nombre ingresado es igual al actual. Sin cambios.");
            } else {
                nombre = nuevoNombre;
                cambios++;
                detalle.append("nombre ");
            }
        }

        // --- Teléfono ---
        String nuevoTelefono = normalizarTelefono(
                SistemaGestionPedidos.leerTexto("Teléfono [" + actual.getTelefono() + "]: "));
        if (!nuevoTelefono.isEmpty()) {
            if (!esTelefonoValido(nuevoTelefono)) {
                System.out.println("[ERROR] Teléfono inválido (7 a 9 dígitos, solo números). Se mantiene el anterior.");
                errores++;
            } else if (nuevoTelefono.equals(actual.getTelefono())) {
                System.out.println("[AVISO] El teléfono ingresado es igual al actual. Sin cambios.");
            } else {
                telefono = nuevoTelefono;
                cambios++;
                detalle.append("telefono ");
            }
        }

        // --- Resultado real de la operación ---
        if (cambios > 0) {
            SistemaGestionPedidos.clientes[idx] =
                    new SistemaGestionPedidos.Cliente(actual.getDni(), nombre, telefono);
            System.out.println("[OK] Cliente actualizado (" + cambios + " cambio(s)).");
            if (errores > 0) {
                System.out.println("[AVISO] " + errores + " dato(s) no se actualizaron por ser inválidos.");
            }
            SistemaGestionPedidos.registrarLog("MODULO_1", "Cliente actualizado: DNI " + actual.getDni()
                    + " [" + detalle.toString().trim() + "] - " + nombre);
        } else if (errores > 0) {
            System.out.println("[ERROR] No se actualizó el cliente: los datos ingresados no son válidos.");
            SistemaGestionPedidos.registrarLog("MODULO_1", "Actualización de cliente fallida (datos inválidos): DNI " + actual.getDni());
        } else {
            System.out.println("[AVISO] No se realizaron cambios en el cliente.");
        }
    }

    // Devuelve la posición del cliente en el arreglo, o -1 si no existe (o si el DNI es null/vacío).
    public static int buscarIndiceCliente(String dni) {
        String buscado = normalizarDni(dni);
        if (buscado.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < SistemaGestionPedidos.totalClientes; i++) {
            SistemaGestionPedidos.Cliente c = SistemaGestionPedidos.clientes[i];
            if (c != null && c.getDni() != null && normalizarDni(c.getDni()).equals(buscado)) {
                return i;
            }
        }
        return -1;
    }

    // Devuelve true si ya hay un cliente guardado con ese DNI
    public static boolean existeClientePorDni(String dni) {
        return buscarIndiceCliente(dni) != -1;
    }
}