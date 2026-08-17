package vampirewargamejt.modelo.excepciones;

public abstract class VampireWargameException extends Exception {
    protected VampireWargameException(String mensaje) {
        super(mensaje);
    }
}
