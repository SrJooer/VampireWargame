package vampirewargamejt.modelo.usuarios;

import java.util.ArrayList;

public class GestorUsuarios {

    public static ArrayList<Usuario> usuarios = new ArrayList<>();

    public static int iniciarSesion(String nombre, String clave) {

        if (nombre == null || clave == null) {
            return 4;
        }

        if (nombre.isEmpty() || clave.isEmpty()) {
            return 3;
        }

        Usuario usuario = encontrarUsuario(nombre);
        if (usuario != null) {
            if (usuario.getClave().equals(clave)) {
                usuario.activar();
                return 2;
            }
            return 1;
        }
        return 0;
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

    private static Usuario encontrarUsuario(String nombre) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }
}
