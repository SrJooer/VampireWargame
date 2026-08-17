package vampirewargamejt.modelo.juego.tablero.fichas;

import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.Tablero;

import vampirewargamejt.modelo.almacen.Almacen;

public final class FichaVampiro extends Ficha {
    private static final int DANO_ABSORCION = 1;
    private static final int VIDA_ROBADA = 1;

    public FichaVampiro(boolean delJugador) {
        super(TipoFicha.VAMPIRO, delJugador);
    }

    @Override
    protected void agregarEspeciales(Almacen<Accion> acciones, Celda origen,
                                     Celda destino, Tablero tablero) {
        if (puedeAtacarA(origen, destino)) {
            acciones.agregar(Accion.ABSORBER);
        }
    }

    @Override
    public int danoDe(Accion accion) {
        return accion == Accion.ABSORBER ? DANO_ABSORCION : super.danoDe(accion);
    }

    @Override
    public int vidaRobadaPor(Accion accion) {
        return accion == Accion.ABSORBER ? VIDA_ROBADA : super.vidaRobadaPor(accion);
    }

    @Override
    public boolean ignoraEscudo(Accion accion) {
        return accion == Accion.ABSORBER || super.ignoraEscudo(accion);
    }
}
