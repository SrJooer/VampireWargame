package vampirewargamejt.modelo.juego;

import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.Tablero;
import vampirewargamejt.modelo.juego.tablero.fichas.Accion;
import vampirewargamejt.modelo.juego.tablero.fichas.Ficha;
import vampirewargamejt.modelo.juego.tablero.fichas.FichaZombie;
import vampirewargamejt.modelo.juego.tablero.fichas.TipoFicha;
import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.modelo.usuarios.Usuario;

import vampirewargamejt.modelo.almacen.Almacen;
import vampirewargamejt.modelo.almacen.AlmacenArreglo;

import vampirewargamejt.modelo.excepciones.JugadaInvalidaException;

public class Juego {
    private static final int PUNTOS_VICTORIA = 3;

    private static final int PERDIDAS_PARA_DOS_GIROS = 2;
    private static final int PERDIDAS_PARA_TRES_GIROS = 4;

    private GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance();

    private Usuario jugador1;
    private Usuario jugador2;
    private boolean jugador1Turno;

    private Tablero tablero;
    private boolean interactuable;

    private TipoFicha[] fichas = new TipoFicha[]{TipoFicha.VAMPIRO, TipoFicha.LOBO, TipoFicha.MUERTE};
    private TipoFicha fichaPorMover;

    private int girosRestantes;

    private boolean juegoTerminado;
    private Usuario ganador;

    private final Almacen<String> historial = new AlmacenArreglo<>(String.class);

    private final Almacen<Ficha> comidasPorJugador1 = new AlmacenArreglo<>(Ficha.class);
    private final Almacen<Ficha> comidasPorJugador2 = new AlmacenArreglo<>(Ficha.class);

    private String ultimoMensaje = "";

    public Juego() {
        jugador1 = gestorUsuarios.getUsuarioActual();
        jugador2 = gestorUsuarios.getUsuarioContricante();
        tablero = new Tablero();
        jugador1Turno = true;
        interactuable = true;
        juegoTerminado = false;

        registrar("Comienza la partida: " + nombre(jugador1) + " (blancas) contra "
                + nombre(jugador2) + " (negras).");
        iniciarTurno();
    }

    public Tablero getTablero() { return tablero; }
    public TipoFicha getFichaPorMover() { return fichaPorMover; }
    public boolean isJugador1Turno() { return jugador1Turno; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public Usuario getJugadorEnTurno() { return jugador1Turno ? jugador1 : jugador2; }
    public Usuario getJugador1() { return jugador1; }
    public Usuario getJugador2() { return jugador2; }
    public Usuario getGanador() { return ganador; }
    public String getUltimoMensaje() { return ultimoMensaje; }

    public boolean isInteractuable() { return interactuable; }
    public void setInteractuable(boolean interactuable) { this.interactuable = interactuable; }

    public String[] getHistorial() {
        return historial.aArreglo();
    }

    public Ficha[] getComidasPor(boolean delJugador1) {
        Almacen<Ficha> comidas = delJugador1 ? comidasPorJugador1 : comidasPorJugador2;
        return comidas.aArreglo();
    }

    private void apuntarComida(Ficha ficha) {
        if (ficha.delJugador()) {
            comidasPorJugador2.agregar(ficha);
        } else {
            comidasPorJugador1.agregar(ficha);
        }
    }

    public boolean esSeleccionable(Celda celda) {
        if (juegoTerminado || celda == null || !celda.tieneFicha()) { return false; }
        Ficha ficha = celda.getFicha();
        return ficha.delJugador() == jugador1Turno && ficha.getTipoFicha() == fichaPorMover;
    }

    public Accion[] accionesPosibles(Celda origen, Celda destino) {
        if (juegoTerminado || origen == null || destino == null) { return new Accion[0]; }
        if (origen == destino || !esSeleccionable(origen)) { return new Accion[0]; }

        return origen.getFicha().accionesSobre(origen, destino, tablero);
    }

    public boolean esDestinoValido(Celda origen, Celda destino) {
        return accionesPosibles(origen, destino).length > 0;
    }

    public void jugar(Celda origen, Celda destino, Accion accion)
            throws JugadaInvalidaException {
        if (juegoTerminado) {
            throw new JugadaInvalidaException("La partida ya terminó.");
        }
        if (!interactuable) {
            throw new JugadaInvalidaException("Espera a que la ruleta se detenga.");
        }
        if (accion == null || !contiene(accionesPosibles(origen, destino), accion)) {
            throw new JugadaInvalidaException("Esa acción no está permitida ahora mismo.");
        }

        ejecutar(origen, destino, accion);

        comprobarFinDelJuego();
        if (!juegoTerminado) { pasarTurno(); }
    }

    public void retirarse() {
        if (juegoTerminado) { return; }

        Usuario queSeRetira = getJugadorEnTurno();
        Usuario queGana = jugador1Turno ? jugador2 : jugador1;
        terminar(queGana, nombre(queSeRetira) + " se ha retirado. ¡Felicidades, "
                + nombre(queGana) + ", has ganado " + PUNTOS_VICTORIA + " puntos!");
    }

    private void ejecutar(Celda origen, Celda destino, Accion accion) {
        if (accion == Accion.MOVER) {
            mover(origen, destino);
        } else if (accion == Accion.INVOCAR) {
            invocar(origen, destino);
        } else {
            atacar(origen, destino, accion);
        }
    }

    private boolean contiene(Accion[] acciones, Accion buscada) {
        for (Accion accion : acciones) {
            if (accion == buscada) { return true; }
        }
        return false;
    }

    private void mover(Celda origen, Celda destino) {
        Ficha ficha = origen.getFicha();
        origen.removeFicha();
        destino.setFicha(ficha);

        registrar(nombre(getJugadorEnTurno()) + " movio su " + ficha.getNombre()
                + " de " + pos(origen) + " a " + pos(destino) + ".");
    }

    private void invocar(Celda origen, Celda destino) {
        destino.setFicha(new FichaZombie(jugador1Turno, origen.getFicha()));

        registrar(nombre(getJugadorEnTurno()) + " invocó un Zombie en " + pos(destino)
                + " con su Muerte de " + pos(origen) + ".");
    }

    private void retirarZombiesDe(Ficha invocador) {
        for (Celda celda : tablero.getCeldas()) {
            Ficha ficha = celda.getFicha();
            if (!(ficha instanceof FichaZombie)) { continue; }
            if (((FichaZombie) ficha).getInvocador() != invocador) { continue; }

            celda.removeFicha();
            apuntarComida(ficha);
            registrar("El Zombie de " + pos(celda) + " se desvanece al caer el Necrómante"
                    + " que lo invocó.");
        }
    }

    private void atacar(Celda origen, Celda destino, Accion accion) {
        Ficha atacante = origen.getFicha();
        Ficha defensora = destino.getFicha();

        int dano = atacante.danoDe(accion);
        if (atacante.ignoraEscudo(accion)) {
            defensora.recibirAtaqueDirecto(dano);
        } else {
            defensora.recibirAtaque(dano);
        }
        boolean destruida = defensora.estaMuerta();

        robarVida(atacante, accion, origen);

        if (destruida) {
            destino.removeFicha();
            apuntarComida(defensora);
            registrar("Se destruyó la pieza " + defensora.getNombre() + " del jugador "
                    + nombre(duenoDe(defensora)) + " en " + pos(destino) + ", con "
                    + accion.getEtiqueta().toLowerCase() + " desde " + pos(origen) + ".");

            if (defensora.getTipoFicha() == TipoFicha.MUERTE) {
                retirarZombiesDe(defensora);
            }
        } else {
            registrar("Se atacó la pieza " + defensora.getNombre() + " en " + pos(destino)
                    + " con " + accion.getEtiqueta().toLowerCase()
                    + " y se le quitaron " + dano + " puntos; le quedan "
                    + defensora.getEscudo() + " puntos de escudo y "
                    + defensora.getVida() + " de vida.");
        }
    }

    private void robarVida(Ficha atacante, Accion accion, Celda origen) {
        int robada = atacante.vidaRobadaPor(accion);
        if (robada == 0) { return; }

        atacante.curar(robada);
        registrar("El " + atacante.getNombre() + " de " + pos(origen)
                + " absorbió sangre y recuperó " + robada + " punto de vida ("
                + atacante.getVida() + " de vida).");
    }

    private Usuario duenoDe(Ficha ficha) {
        return ficha.delJugador() ? jugador1 : jugador2;
    }

    private void comprobarFinDelJuego() {
        if (!tablero.tieneFichas(false)) {
            terminar(jugador1, mensajeVictoria(jugador1, jugador2));
        } else if (!tablero.tieneFichas(true)) {
            terminar(jugador2, mensajeVictoria(jugador2, jugador1));
        }
    }

    private String mensajeVictoria(Usuario queGana, Usuario quePierde) {
        return nombre(queGana) + " venció a " + nombre(quePierde)
                + ". ¡Felicidades, has ganado " + PUNTOS_VICTORIA + " puntos!";
    }

    private void terminar(Usuario queGana, String mensajeFinal) {
        juegoTerminado = true;
        ganador = queGana;
        registrar(mensajeFinal);

        if (queGana != null) {
            queGana.sumarPuntos(PUNTOS_VICTORIA);
            queGana.sumarGanadas();
        }

        Usuario quePerdio = (queGana == jugador1) ? jugador2 : jugador1;
        if (quePerdio != null) {
            quePerdio.sumarPerdidas();
        }

        if (jugador1 != null) { jugador1.agregarHistorial(mensajeFinal); }
        if (jugador2 != null) { jugador2.agregarHistorial(mensajeFinal); }
    }

    public int getGirosDelTurno() {
        int perdidas = tablero.piezasPerdidas(jugador1Turno);
        if (perdidas >= PERDIDAS_PARA_TRES_GIROS) { return 3; }
        if (perdidas >= PERDIDAS_PARA_DOS_GIROS) { return 2; }
        return 1;
    }

    public int getGirosRestantes() { return girosRestantes; }

    public boolean quedanGiros() { return girosRestantes > 0; }

    public boolean puedeUsarResultado() {
        return !juegoTerminado && hayJugadaDisponible();
    }

    private boolean hayJugadaDisponible() {
        for (Celda origen : tablero.getCeldas()) {
            if (esSeleccionable(origen) && tieneAlgunDestino(origen)) { return true; }
        }
        return false;
    }

    private boolean tieneAlgunDestino(Celda origen) {
        for (Celda destino : tablero.getCeldas()) {
            if (accionesPosibles(origen, destino).length > 0) { return true; }
        }
        return false;
    }

    public TipoFicha girar() {
        fichaPorMover = fichas[(int) (Math.random() * fichas.length)];
        girosRestantes--;
        registrar("La ruleta saca: " + fichaPorMover.nombre + ".");
        return fichaPorMover;
    }

    public void perderTurno() {
        registrar(nombre(getJugadorEnTurno()) + " agoto sus giros sin sacar una ficha"
                + " disponible: pierde el turno.");
        pasarTurno();
    }

    public void pasarTurno() {
        jugador1Turno = !jugador1Turno;
        iniciarTurno();
    }

    private void iniciarTurno() {
        girosRestantes = getGirosDelTurno();
        registrar("Turno de " + nombre(getJugadorEnTurno())
                + (jugador1Turno ? " (blancas)" : " (negras)")
                + ". Giros disponibles: " + girosRestantes + ".");
        girar();
    }

    private void registrar(String texto) {
        historial.agregar(texto);
        ultimoMensaje = texto;
    }

    private String pos(Celda celda) {
        return celda.getFila() + ":" + celda.getColumna();
    }

    private String nombre(Usuario usuario) {
        return usuario == null ? "Jugador" : usuario.getNombre();
    }
}
