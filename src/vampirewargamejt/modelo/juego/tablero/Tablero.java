package vampirewargamejt.modelo.juego.tablero;

import vampirewargamejt.modelo.juego.tablero.fichas.Ficha;
import vampirewargamejt.modelo.juego.tablero.fichas.FichaLobo;
import vampirewargamejt.modelo.juego.tablero.fichas.FichaMuerte;
import vampirewargamejt.modelo.juego.tablero.fichas.FichaVampiro;
import vampirewargamejt.modelo.juego.tablero.fichas.TipoFicha;

public class Tablero {
    public static final int PIEZAS_INICIALES = 6;

    int longitud = 6;
    private Celda[][] tablero = new Celda[longitud][longitud];

    public Tablero() {
        rellenarTablero();
    }

    private void rellenarTablero() {
        for (int fila = 0; fila < longitud; fila++) {
            for (int columna = 0; columna < longitud; columna++) {
                tablero[fila][columna] = new Celda(fila, columna);
            }
        }
        rellenarFichas();
    }

    private void rellenarFichas() {
        addFicha(new FichaLobo(true), new int[]{5, 0});
        addFicha(new FichaVampiro(true), new int[]{5, 1});
        addFicha(new FichaMuerte(true), new int[]{5, 2});
        addFicha(new FichaMuerte(true), new int[]{5, 3});
        addFicha(new FichaVampiro(true), new int[]{5, 4});
        addFicha(new FichaLobo(true), new int[]{5, 5});

        addFicha(new FichaLobo(false), new int[]{0, 0});
        addFicha(new FichaVampiro(false), new int[]{0, 1});
        addFicha(new FichaMuerte(false), new int[]{0, 2});
        addFicha(new FichaMuerte(false), new int[]{0, 3});
        addFicha(new FichaVampiro(false), new int[]{0, 4});
        addFicha(new FichaLobo(false), new int[]{0, 5});
    }

    private void addFicha(Ficha ficha, int[] coordenadas) {
        tablero[coordenadas[0]][coordenadas[1]].setFicha(ficha);
    }

    public static int distancia(Celda origen, Celda destino) {
        return Math.max(Math.abs(destino.getFila() - origen.getFila()),
                        Math.abs(destino.getColumna() - origen.getColumna()));
    }

    public static boolean enLinea(Celda origen, Celda destino) {
        int avanceFilas = destino.getFila() - origen.getFila();
        int avanceColumnas = destino.getColumna() - origen.getColumna();
        return avanceFilas == 0 || avanceColumnas == 0
                || Math.abs(avanceFilas) == Math.abs(avanceColumnas);
    }

    public static boolean enLineaRecta(Celda origen, Celda destino) {
        return origen.getFila() == destino.getFila()
                || origen.getColumna() == destino.getColumna();
    }

    public boolean caminoLibre(Celda origen, Celda destino) {
        if (!enLinea(origen, destino)) { return false; }

        int pasoFila = Integer.signum(destino.getFila() - origen.getFila());
        int pasoColumna = Integer.signum(destino.getColumna() - origen.getColumna());

        return caminoLibre(origen.getFila() + pasoFila, origen.getColumna() + pasoColumna,
                           destino.getFila(), destino.getColumna(), pasoFila, pasoColumna);
    }

    private boolean caminoLibre(int fila, int columna, int filaFinal, int columnaFinal,
                                int pasoFila, int pasoColumna) {
        if (fila == filaFinal && columna == columnaFinal) { return true; }

        Celda celda = getCelda(fila, columna);
        if (celda == null || celda.tieneFicha()) { return false; }

        return caminoLibre(fila + pasoFila, columna + pasoColumna,
                           filaFinal, columnaFinal, pasoFila, pasoColumna);
    }

    public boolean hayZombieAdyacente(Celda celda, boolean delJugador) {
        for (int avanceFilas = -1; avanceFilas <= 1; avanceFilas++) {
            for (int avanceColumnas = -1; avanceColumnas <= 1; avanceColumnas++) {
                if (avanceFilas == 0 && avanceColumnas == 0) { continue; }

                Celda vecina = getCelda(celda.getFila() + avanceFilas,
                                        celda.getColumna() + avanceColumnas);
                if (vecina == null || !vecina.tieneFicha()) { continue; }

                Ficha ficha = vecina.getFicha();
                if (ficha.getTipoFicha() == TipoFicha.ZOMBIE
                        && ficha.delJugador() == delJugador) { return true; }
            }
        }
        return false;
    }

    public boolean dentroTablero(int fila, int columna) {
        return fila >= 0 && fila < longitud && columna >= 0 && columna < longitud;
    }

    public Celda getCelda(int fila, int columna) {
        if (!dentroTablero(fila, columna)) { return null; }
        return tablero[fila][columna];
    }

    public Celda[] getCeldas() {
        Celda[] celdas = new Celda[longitud * longitud];
        int i = 0;
        for (int fila = 0; fila < longitud; fila++) {
            for (int columna = 0; columna < longitud; columna++) {
                celdas[i] = tablero[fila][columna];
                i++;
            }
        }
        return celdas;
    }

    private int contarFichas(boolean delJugador, boolean sinZombies) {
        int total = 0;
        for (Celda celda : getCeldas()) {
            Ficha ficha = celda.getFicha();
            if (ficha == null || ficha.delJugador() != delJugador) { continue; }
            if (sinZombies && ficha.getTipoFicha() == TipoFicha.ZOMBIE) { continue; }
            total++;
        }
        return total;
    }

    public boolean tieneFichas(boolean delJugador) {
        return contarFichas(delJugador, false) > 0;
    }

    public int piezasPerdidas(boolean delJugador) {
        return Math.max(0, PIEZAS_INICIALES - contarFichas(delJugador, true));
    }

    public int getLongitud() { return longitud; }
}
