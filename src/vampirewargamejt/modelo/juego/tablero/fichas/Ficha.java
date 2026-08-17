package vampirewargamejt.modelo.juego.tablero.fichas;

import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.Tablero;

import vampirewargamejt.modelo.almacen.Almacen;
import vampirewargamejt.modelo.almacen.AlmacenArreglo;

public abstract class Ficha {
    private final TipoFicha tipoFicha;
    private final String nombre;
    private final int alcance;
    private final boolean delJugador;

    private int vida;
    private int ataque;
    private int escudo;
    private String urlAsset;

    protected Ficha(TipoFicha tipoFicha, boolean delJugador) {
        this.tipoFicha = tipoFicha;
        this.nombre = tipoFicha.nombre;
        this.vida = tipoFicha.vida;
        this.ataque = tipoFicha.ataque;
        this.escudo = tipoFicha.escudo;
        this.alcance = tipoFicha.alcance;
        this.delJugador = delJugador;
        this.urlAsset = tipoFicha.getUrlAsset(delJugador);
    }

    protected abstract void agregarEspeciales(Almacen<Accion> acciones, Celda origen,
                                              Celda destino, Tablero tablero);

    public final Accion[] accionesSobre(Celda origen, Celda destino, Tablero tablero) {
        Almacen<Accion> acciones = new AlmacenArreglo<>(Accion.class);

        if (puedeMoverA(origen, destino, tablero)) {
            acciones.agregar(Accion.MOVER);
        }
        if (puedeAtacarA(origen, destino)) {
            acciones.agregar(Accion.ATACAR);
        }
        agregarEspeciales(acciones, origen, destino, tablero);

        return acciones.aArreglo();
    }

    public final boolean puedeMoverA(Celda origen, Celda destino, Tablero tablero) {
        if (alcance == 0 || destino.tieneFicha()) {
            return false;
        }
        int distancia = Tablero.distancia(origen, destino);
        if (distancia == 0 || distancia > alcance) {
            return false;
        }
        return Tablero.enLinea(origen, destino) && tablero.caminoLibre(origen, destino);
    }

    public final boolean puedeAtacarA(Celda origen, Celda destino) {
        return esEnemiga(destino) && Tablero.distancia(origen, destino) == 1;
    }

    protected final boolean esEnemiga(Celda celda) {
        return celda.tieneFicha() && celda.getFicha().delJugador() != delJugador;
    }

    public int danoDe(Accion accion) {
        return ataque;
    }

    public boolean ignoraEscudo(Accion accion) {
        return false;
    }

    public int vidaRobadaPor(Accion accion) {
        return 0;
    }

    public final boolean recibirAtaque(int cantidad) {
        int sobrante = cantidad;
        if (escudo > 0) {
            int absorbido = Math.min(escudo, sobrante);
            escudo -= absorbido;
            sobrante -= absorbido;
        }
        vida = Math.max(0, vida - sobrante);
        return estaMuerta();
    }

    public final boolean recibirAtaqueDirecto(int cantidad) {
        vida = Math.max(0, vida - cantidad);
        return estaMuerta();
    }

    public final boolean estaMuerta() {
        return vida <= 0;
    }

    public final void curar(int cantidad) {
        vida = Math.min(tipoFicha.vida, vida + cantidad);
    }

    public final TipoFicha getTipoFicha() { return tipoFicha; }
    public final String getNombre() { return nombre; }
    public final int getVida() { return vida; }
    public final int getAtaque() { return ataque; }
    public final int getEscudo() { return escudo; }
    public final int getAlcance() { return alcance; }
    public final boolean delJugador() { return delJugador; }

    public final String getUrlAsset() { return urlAsset; }
    public final void setUrlAsset(String urlAsset) { this.urlAsset = urlAsset; }
}
