package vampirewargamejt.modelo.usuarios;

import vampirewargamejt.modelo.almacen.Almacen;
import vampirewargamejt.modelo.almacen.AlmacenArreglo;

public class GestorUsuarios {
    private static GestorUsuarios instancia;

    private final Almacen<Usuario> usuarios = new AlmacenArreglo<>(Usuario.class);
    private Usuario usuarioActual;
    private Usuario usuarioContricante;

    public static GestorUsuarios getInstance() {
        if (instancia == null) {
            instancia = new GestorUsuarios();
        }
        return instancia;
    }

    public int iniciarSesion(String nombre, String clave) {
        if (nombre == null || clave == null) {
            return 5;
        }

        if (nombre.isEmpty() || clave.isEmpty()) {
            return 4;
        }

        Usuario usuario = encontrarUsuario(nombre);

        if (usuario != null) {
            if (!usuario.isActivo()) {
                return 3;
            }
            if (usuario.getClave().equals(clave)) {
                usuarioActual = usuario;
                return 2;
            }
            return 1;
        }
        return 0;
    }

    public int cambiarClave(String nuevaClave) {
        if (nuevaClave == null || nuevaClave.isEmpty()) {
            return 4;
        }
        if (nuevaClave.length() != 5) {
            return 3;
        }
        if (usuarioActual != null) {
            usuarioActual.setClave(nuevaClave);
            return 2;
        }
        return 1;
    }

    public void cerrarCuenta() {
        if (usuarioActual != null) {
            usuarioActual.desactivar();
            usuarioActual = null;
        }
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public int registrarUsuario(String nombre, String clave) {
        if (nombre == null || clave == null) {
            return 4;
        }

        if (nombre.isEmpty() || clave.isEmpty()) {
            return 3;
        }

        if (clave.length() != 5) {
            return 2;
        }

        if (encontrarUsuario(nombre) == null) {
            usuarios.agregar(new Usuario(nombre, clave));
            return 1;
        }
        return 0;
    }

    public Usuario encontrarUsuario(String nombre) {
        for (Usuario usuario : usuarios.aArreglo()) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario[] getRanking() {
        Almacen<Usuario> activos = new AlmacenArreglo<>(Usuario.class);
        for (Usuario usuario : usuarios.aArreglo()) {
            if (usuario.isActivo()) {
                activos.agregar(usuario);
            }
        }

        Usuario[] ranking = activos.aArreglo();
        ordenarRecursivo(ranking, 0, ranking.length - 1);
        return ranking;
    }

    public String[] obtenerJugadoresDisponibles() {
        Almacen<String> disponibles = new AlmacenArreglo<>(String.class);
        for (Usuario usuario : usuarios.aArreglo()) {
            if (usuario.isActivo() && usuario != usuarioActual) {
                disponibles.agregar(usuario.getNombre());
            }
        }
        return disponibles.aArreglo();
    }

    private void ordenarRecursivo(Usuario[] lista, int inicio, int fin) {
        if (inicio < fin) {
            int indicePivote = particion(lista, inicio, fin);
            ordenarRecursivo(lista, inicio, indicePivote - 1);
            ordenarRecursivo(lista, indicePivote + 1, fin);
        }
    }

    private int particion(Usuario[] lista, int inicio, int fin) {
        int pivote = lista[fin].getPuntos();
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            if (lista[j].getPuntos() > pivote) {
                i++;
                Usuario temp = lista[i];
                lista[i] = lista[j];
                lista[j] = temp;
            }
        }

        Usuario temp = lista[i + 1];
        lista[i + 1] = lista[fin];
        lista[fin] = temp;

        return i + 1;
    }

    public void establecerContricante(String usuario) {
        Usuario u = encontrarUsuario(usuario);
        if (u != null) { usuarioContricante = u; }
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public Usuario getUsuarioContricante() { return usuarioContricante; }
}
