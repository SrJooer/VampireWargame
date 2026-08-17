package vampirewargamejt.modelo.juego.tablero.fichas;

import vampirewargamejt.modelo.almacen.Almacen;
import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.Tablero;

public final class FichaZombie extends Ficha {
    private final Ficha invocador;

    public FichaZombie(boolean delJugador, Ficha invocador) {
        super(TipoFicha.ZOMBIE, delJugador);
        this.invocador = invocador;
    }

    public Ficha getInvocador() {
        return invocador;
    }

    @Override
    protected void agregarEspeciales(Almacen<Accion> acciones, Celda origen,
                                     Celda destino, Tablero tablero) {
    }
}
