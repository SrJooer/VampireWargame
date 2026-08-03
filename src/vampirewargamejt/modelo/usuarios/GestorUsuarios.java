package vampirewargamejt.modelo.usuarios;

import java.util.ArrayList;

public class GestorUsuarios {

    public static ArrayList<Usuario> usuarios = new ArrayList<>();
    public static Usuario usuarioActual;

    public static int iniciarSesion(String nombre, String clave) {

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

    public static int cambiarClave(String nuevaClave) {
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

    public static void cerrarCuenta() {
        if (usuarioActual != null) {
            usuarioActual.desactivar();
            usuarioActual = null;
        }
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static int registrarUsuario(String nombre, String clave) {

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

    public static Usuario encontrarUsuario(String nombre) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }
}
