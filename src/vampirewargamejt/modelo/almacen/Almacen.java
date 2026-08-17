package vampirewargamejt.modelo.almacen;

public interface Almacen<T> {
    void agregar(T elemento);

    boolean eliminar(T elemento);

    T obtener(int indice);

    int tamano();

    boolean estaVacio();

    T[] aArreglo();
}
