package vampirewargamejt.modelo.usuarios;

import java.util.ArrayList;

public class GestorUsuarios {

    private static GestorUsuarios instancia;

    private ArrayList<Usuario> usuarios = new ArrayList<>();
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
            usuarios.add(new Usuario(nombre, clave));
            return 1;
        }
        return 0;
    }

    public Usuario encontrarUsuario(String nombre) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }

    public String[] getUsuariosOrdenados(int cantidad) {
        ArrayList<Usuario> usuariosActivos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.isActivo()) {
                usuariosActivos.add(usuario);
            }
        }

        ordenarRecursivo(usuariosActivos, 0, usuariosActivos.size() - 1);

        String[] resultado = new String[cantidad];

        for (int i = 0; i < cantidad; i++) {
            if (i < usuariosActivos.size()) {
                Usuario usuario = usuariosActivos.get(i);
                resultado[i] = usuario.getNombre() + " - " + usuario.getPuntos() + " puntos";
            } else {
                resultado[i] = " -- jugador null --";
            }
        }
        return resultado;
    }

    public String[] obtenerJugadoresDisponibles() {
        ArrayList<Usuario> jugadoresDisponibles = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.isActivo() && usuario != usuarioActual) {
                jugadoresDisponibles.add(usuario);
            }
        }
        String[] resultado = new String[jugadoresDisponibles.size()];
        for (int i = 0; i < jugadoresDisponibles.size(); i++) {
            resultado[i] = jugadoresDisponibles.get(i).getNombre();
        }
        return resultado;
    }

    private void ordenarRecursivo(ArrayList<Usuario> lista, int inicio, int fin) {
        if (inicio < fin) {
            int indicePivote = particion(lista, inicio, fin);
            ordenarRecursivo(lista, inicio, indicePivote - 1);
            ordenarRecursivo(lista, indicePivote + 1, fin);
        }
    }

    private int particion(ArrayList<Usuario> lista, int inicio, int fin) {
        int pivote = lista.get(fin).getPuntos();
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            if (lista.get(j).getPuntos() > pivote) {
                i++;
                Usuario temp = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, temp);
            }
        }

        Usuario temp = lista.get(i + 1);
        lista.set(i + 1, lista.get(fin));
        lista.set(fin, temp);

        return i + 1;
    }

    public void establecerContricante(String usuario) {
        Usuario u = encontrarUsuario(usuario);
        if (u != null) { usuarioContricante = u; }
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public Usuario getUsuarioContricante() { return usuarioContricante; }
}
