package vampirewargamejt.modelo.usuarios;

import vampirewargamejt.modelo.almacen.Almacen;
import vampirewargamejt.modelo.almacen.AlmacenArreglo;

import vampirewargamejt.modelo.excepciones.CredencialesInvalidasException;
import vampirewargamejt.modelo.excepciones.DatosInvalidosException;
import vampirewargamejt.modelo.excepciones.UsuarioDuplicadoException;

public class GestorUsuarios {
    private static final int LARGO_CLAVE = 5;

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

    public void iniciarSesion(String nombre, String clave)
            throws DatosInvalidosException, CredencialesInvalidasException {
        exigirTexto(nombre, "Rellene todos los campos");
        exigirTexto(clave, "Rellene todos los campos");

        Usuario usuario = encontrarUsuario(nombre);

        if (usuario == null) {
            throw new CredencialesInvalidasException("Usuario o clave incorrectos");
        }
        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("Usuario desactivado");
        }
        if (!usuario.getClave().equals(clave)) {
            throw new CredencialesInvalidasException("Usuario o clave incorrectos");
        }

        usuarioActual = usuario;
    }

    public void cambiarClave(String nuevaClave)
            throws DatosInvalidosException, CredencialesInvalidasException {
        exigirTexto(nuevaClave, "La nueva contraseña no puede estar vacía.");
        exigirLargoDeClave(nuevaClave, "La nueva contraseña debe tener exactamente "
                + LARGO_CLAVE + " caracteres.");

        if (usuarioActual == null) {
            throw new CredencialesInvalidasException("Error al cambiar la contraseña.");
        }
        usuarioActual.setClave(nuevaClave);
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

    public void registrarUsuario(String nombre, String clave)
            throws DatosInvalidosException, UsuarioDuplicadoException {
        exigirTexto(nombre, "Rellene todos los campos");
        exigirTexto(clave, "Rellene todos los campos");
        exigirLargoDeClave(clave, "La clave debe tener " + LARGO_CLAVE + " caracteres");

        if (encontrarUsuario(nombre) != null) {
            throw new UsuarioDuplicadoException("El usuario ya existe");
        }
        usuarios.agregar(new Usuario(nombre, clave));
    }

    private void exigirTexto(String valor, String mensaje) throws DatosInvalidosException {
        if (valor == null || valor.isEmpty()) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    private void exigirLargoDeClave(String clave, String mensaje)
            throws DatosInvalidosException {
        if (clave.length() != LARGO_CLAVE) {
            throw new DatosInvalidosException(mensaje);
        }
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
