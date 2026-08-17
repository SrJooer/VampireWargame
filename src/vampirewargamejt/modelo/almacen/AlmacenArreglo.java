package vampirewargamejt.modelo.almacen;

import java.lang.reflect.Array;

public class AlmacenArreglo<T> implements Almacen<T> {
    private static final int CAPACIDAD_INICIAL = 8;

    private final Class<T> tipo;
    private T[] elementos;
    private int tamano;

    public AlmacenArreglo(Class<T> tipo) {
        this.tipo = tipo;
        this.elementos = crear(CAPACIDAD_INICIAL);
        this.tamano = 0;
    }

    @SuppressWarnings("unchecked")
    private T[] crear(int capacidad) {
        return (T[]) Array.newInstance(tipo, capacidad);
    }

    private void ampliar() {
        T[] mayor = crear(elementos.length * 2);
        for (int i = 0; i < tamano; i++) {
            mayor[i] = elementos[i];
        }
        elementos = mayor;
    }

    @Override
    public void agregar(T elemento) {
        if (elemento == null) { return; }
        if (tamano == elementos.length) { ampliar(); }
        elementos[tamano] = elemento;
        tamano++;
    }

    @Override
    public boolean eliminar(T elemento) {
        for (int i = 0; i < tamano; i++) {
            if (!elementos[i].equals(elemento)) { continue; }
            for (int j = i; j < tamano - 1; j++) {
                elementos[j] = elementos[j + 1];
            }
            tamano--;
            elementos[tamano] = null;
            return true;
        }
        return false;
    }

    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) { return null; }
        return elementos[indice];
    }

    @Override
    public int tamano() {
        return tamano;
    }

    @Override
    public boolean estaVacio() {
        return tamano == 0;
    }

    @Override
    public T[] aArreglo() {
        T[] copia = crear(tamano);
        for (int i = 0; i < tamano; i++) {
            copia[i] = elementos[i];
        }
        return copia;
    }
}
