package vampirewargamejt.modelo.juego.tablero.fichas;

import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.Tablero;

import vampirewargamejt.modelo.almacen.Almacen;

public final class FichaLobo extends Ficha {
    public FichaLobo(boolean delJugador) {
        super(TipoFicha.LOBO, delJugador);
    }

    @Override
    protected void agregarEspeciales(Almacen<Accion> acciones, Celda origen,
                                     Celda destino, Tablero tablero) {
    }
}
