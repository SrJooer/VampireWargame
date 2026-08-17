package vampirewargamejt.modelo.juego.tablero.fichas;

import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.Tablero;

import vampirewargamejt.modelo.almacen.Almacen;

public final class FichaMuerte extends Ficha {
    private static final int DISTANCIA_LANZA = 2;
    private static final int DANO_LANZA = 2;
    private static final int DANO_ZOMBIE = 1;

    public FichaMuerte(boolean delJugador) {
        super(TipoFicha.MUERTE, delJugador);
    }

    @Override
    protected void agregarEspeciales(Almacen<Accion> acciones, Celda origen,
                                     Celda destino, Tablero tablero) {
        if (puedeInvocarEn(destino)) {
            acciones.agregar(Accion.INVOCAR);
        }
        if (puedeLanzarA(origen, destino, tablero)) {
            acciones.agregar(Accion.LANZA);
        }
        if (puedeOrdenarAlZombie(origen, destino, tablero)) {
            acciones.agregar(Accion.ATAQUE_ZOMBIE);
        }
    }

    public boolean puedeInvocarEn(Celda destino) {
        return !destino.tieneFicha();
    }

    public boolean puedeLanzarA(Celda origen, Celda destino, Tablero tablero) {
        if (!esEnemiga(destino) || Tablero.distancia(origen, destino) != DISTANCIA_LANZA) {
            return false;
        }
        return Tablero.enLineaRecta(origen, destino) && tablero.caminoLibre(origen, destino);
    }

    public boolean puedeOrdenarAlZombie(Celda origen, Celda destino, Tablero tablero) {
        if (!esEnemiga(destino) || Tablero.distancia(origen, destino) <= DISTANCIA_LANZA) {
            return false;
        }
        return tablero.hayZombieAdyacente(destino, delJugador());
    }

    @Override
    public int danoDe(Accion accion) {
        if (accion == Accion.LANZA) {
            return DANO_LANZA;
        }
        if (accion == Accion.ATAQUE_ZOMBIE) {
            return DANO_ZOMBIE;
        }
        return super.danoDe(accion);
    }

    @Override
    public boolean ignoraEscudo(Accion accion) {
        return accion == Accion.LANZA;
    }
}
