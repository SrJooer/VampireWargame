package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.juego.Juego;
import vampirewargamejt.modelo.juego.tablero.Celda;
import vampirewargamejt.modelo.juego.tablero.fichas.Accion;
import vampirewargamejt.modelo.juego.tablero.fichas.Ficha;
import vampirewargamejt.modelo.usuarios.Usuario;
import vampirewargamejt.visual.componentes.CargadorAssets;
import vampirewargamejt.visual.componentes.CasillaBoton;
import vampirewargamejt.visual.componentes.DialogoAccion;
import vampirewargamejt.visual.componentes.RuletaPanel;

import javax.swing.*;
import java.awt.*;

public class PanelJuego extends PanelAbstracto {
    private static final int LADO = 6;

    private static final Color TINTE_SELECCIONABLE = new Color(232, 169, 46);
    private static final Color TINTE_SELECCIONADA = new Color(255, 214, 110);
    private static final Color TINTE_MOVER = new Color(76, 175, 96);
    private static final Color TINTE_ATACAR = new Color(214, 64, 64);
    private static final Color TINTE_LANZA = new Color(150, 92, 196);
    private static final Color TINTE_ZOMBIE = new Color(214, 132, 46);
    private static final Color TINTE_INVOCAR = new Color(58, 122, 206);

    private static final int MAX_GIROS_SIN_JUGADA = 8;

    private static final String[] CAMPOS = { "Ficha", "Bando", "Ataque", "Vida", "Escudo" };

    private static final Accion[] PRIORIDAD = {
            Accion.ATACAR, Accion.ABSORBER, Accion.LANZA,
            Accion.ATAQUE_ZOMBIE, Accion.MOVER, Accion.INVOCAR
    };

    private Juego juego;
    private Celda origenSeleccionado;

    private CasillaBoton[][] casillas;

    private JLabel labelTurno;
    private JLabel labelRuleta;
    private JLabel labelMensaje;
    private JLabel labelGiros;
    private JTextArea areaHistorial;
    private RuletaPanel ruleta;
    private JButton botonDetener;

    private JLabel iconoSeleccionada;
    private JLabel[] datosSeleccionada = new JLabel[CAMPOS.length];
    private JLabel iconoRaton;
    private JLabel[] datosRaton = new JLabel[CAMPOS.length];
    private JPanel tiraComidas1;
    private JPanel tiraComidas2;

    private Celda celdaBajoRaton;

    private boolean ruletaGirando;
    private int girosSinJugada;
    private String avisoPendiente;

    private int historialPintado;

    private JPanel menuPanel;
    private JPanel lateralPanel;
    private JPanel inferiorPanel;
    private JPanel escenarioPanel;
        private JPanel tituloPanel;
        private JPanel tableroPanel;

    @Override
    public void iniciarPanel() {
        juego = new Juego();

        prepararContenido();

        prepararLateral();

        prepararInferior();

            prepararTituloEscenario();
            prepararTablero();
        prepararEscenario();

        prepararMenu();

        add(menuPanel, BorderLayout.CENTER);

        redibujar();
        girarRuleta();
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(new BorderLayout());
        menuPanel.add(escenarioPanel, BorderLayout.CENTER);
        menuPanel.add(lateralPanel, BorderLayout.WEST);
        menuPanel.add(inferiorPanel, BorderLayout.SOUTH);
    }

    private void prepararEscenario() {
        escenarioPanel = agregarPanel(new BorderLayout());
        escenarioPanel.setBackground(new Color(0, 0, 0));
        escenarioPanel.setOpaque(true);
        escenarioPanel.add(tituloPanel, BorderLayout.NORTH);

        escenarioPanel.add(agregarPanelCentrador(tableroPanel), BorderLayout.CENTER);
    }

    private void prepararTituloEscenario() {
        tituloPanel = agregarPanel(new BorderLayout());
        tituloPanel.setBackground(new Color(240, 240, 240));
        tituloPanel.setPreferredSize(new Dimension(0, 100));
        tituloPanel.setOpaque(true);

        labelTurno = etiquetaOscura("", 16);
        labelRuleta = etiquetaOscura("", 16);
        labelMensaje = etiquetaOscura("", 16);

        JPanel filas = agregarPanel(new GridLayout(3, 1));
        filas.setOpaque(true);
        filas.setBackground(new Color(0, 0, 0));
        filas.add(labelTurno);
        filas.add(labelRuleta);
        filas.add(labelMensaje);
        tituloPanel.add(filas, BorderLayout.CENTER);
    }

    private JLabel etiquetaOscura(String texto, int tam) {
        JLabel l = agregarLabel(texto);
        l.setForeground(new Color(255, 255, 255));
        l.setFont(new Font("Arial", Font.BOLD, tam));
        return l;
    }

    private void prepararLateral() {
        lateralPanel = agregarPanel(new BorderLayout());
        lateralPanel.setOpaque(true);
        lateralPanel.setBackground(new Color(5, 5, 5));
        lateralPanel.setPreferredSize(new Dimension(400, 0));

        lateralPanel.add(prepararRuleta(), BorderLayout.NORTH);
        lateralPanel.add(prepararHistorial(), BorderLayout.CENTER);
        lateralPanel.add(agregarBoton("Retirarse", this::confirmarRetiro), BorderLayout.SOUTH);
    }

    private void confirmarRetiro() {
        mostrarPanelConfirmacion(this, this::retirarse,
                "¿Estás seguro de que quieres retirarte?");
    }

    private void retirarse() {
        if (ruleta != null) { ruleta.cancelar(); }
        ruletaGirando = false;

        juego.retirarse();
        mostrarPanelTexto(juego.getUltimoMensaje(), new PanelMenuPrincipal());
    }

    private JPanel prepararRuleta() {
        JPanel panel = agregarPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        panel.setPreferredSize(new Dimension(0, 300));

        ruleta = new RuletaPanel();
        panel.add(ruleta, BorderLayout.CENTER);

        labelGiros = agregarLabel("");

        botonDetener = agregarBoton("Detener", this::detenerRuleta);
        botonDetener.setEnabled(false);

        JPanel pie = agregarPanel(new GridLayout(2, 1, 0, 6));
        pie.add(labelGiros);
        pie.add(botonDetener);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel prepararHistorial() {
        JPanel panel = agregarPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = agregarLabel("Historial de jugadas");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        areaHistorial = agregarAreaTexto();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(areaHistorial);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void prepararInferior() {
        inferiorPanel = agregarPanel(new GridLayout(1, 4, 10, 0));
        inferiorPanel.setOpaque(true);
        inferiorPanel.setBackground(new Color(5, 5, 5));
        inferiorPanel.setPreferredSize(new Dimension(0, 200));
        inferiorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        iconoSeleccionada = crearIconoGrande();
        inferiorPanel.add(crearRecuadroFicha("Ficha seleccionada",
                iconoSeleccionada, datosSeleccionada));

        iconoRaton = crearIconoGrande();
        inferiorPanel.add(crearRecuadroFicha("Bajo el cursor", iconoRaton, datosRaton));

        tiraComidas1 = crearTira();
        inferiorPanel.add(crearRecuadroTira("Comidas por " + nombreDe(true), tiraComidas1));

        tiraComidas2 = crearTira();
        inferiorPanel.add(crearRecuadroTira("Comidas por " + nombreDe(false), tiraComidas2));
    }

    private JPanel crearRecuadroFicha(String titulo, JLabel icono, JLabel[] valores) {
        JPanel panel = crearRecuadro(titulo);
        panel.add(icono, BorderLayout.WEST);
        panel.add(crearTablaDatos(valores), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTablaDatos(JLabel[] valores) {
        JPanel tabla = agregarPanel(new GridLayout(CAMPOS.length, 2, 8, 2));

        for (int i = 0; i < CAMPOS.length; i++) {
            tabla.add(crearEtiquetaDato(CAMPOS[i], new Color(255, 255, 255)));
            valores[i] = crearEtiquetaDato("-", Color.WHITE);
            tabla.add(valores[i]);
        }
        return tabla;
    }

    private JLabel crearEtiquetaDato(String texto, Color color) {
        JLabel etiqueta = agregarLabel(texto);
        etiqueta.setFont(new Font("Arial", Font.PLAIN, 13));
        etiqueta.setHorizontalAlignment(JLabel.LEFT);
        etiqueta.setForeground(color);
        return etiqueta;
    }

    private JPanel crearRecuadroTira(String titulo, JPanel tira) {
        JPanel panel = crearRecuadro(titulo);
        panel.add(tira, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearRecuadro(String titulo) {
        JPanel panel = agregarPanel(new BorderLayout(8, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel etiqueta = agregarLabel(titulo);
        etiqueta.setHorizontalAlignment(JLabel.LEFT);
        panel.add(etiqueta, BorderLayout.NORTH);

        return panel;
    }

    private JLabel crearIconoGrande() {
        JLabel icono = new JLabel();
        icono.setPreferredSize(new Dimension(74, 74));
        icono.setHorizontalAlignment(JLabel.CENTER);
        icono.setForeground(Color.WHITE);
        return icono;
    }


    private JPanel crearTira() {
        return agregarPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
    }

    private String nombreDe(boolean jugador1) {
        Usuario usuario = jugador1 ? juego.getJugador1() : juego.getJugador2();
        return usuario != null ? usuario.getNombre() : (jugador1 ? "Jugador 1" : "Jugador 2");
    }

    private void prepararTablero() {
        tableroPanel = agregarPanelCuadrado(new GridLayout(LADO, LADO, 0, 0), 40, 240);
        casillas = new CasillaBoton[LADO][LADO];
        for (int fila = 0; fila < LADO; fila++) {
            for (int columna = 0; columna < LADO; columna++) {
                casillas[fila][columna] = crearCasilla(fila, columna);
                tableroPanel.add(casillas[fila][columna]);
            }
        }
    }

    private CasillaBoton crearCasilla(int fila, int columna) {
        boolean clara = (fila + columna) % 2 == 0;
        Color base = clara ? new Color(110, 110, 125) : new Color(45, 45, 58);

        CasillaBoton casilla = new CasillaBoton(fila, columna, base);

        casilla.addActionListener(e -> alPulsarCasilla(fila, columna));

        casilla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                Celda entrada = juego.getTablero().getCelda(fila, columna);
                if (celdaBajoRaton == entrada) { return; }
                celdaBajoRaton = entrada;
                actualizarRaton();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (celdaBajoRaton == null) { return; }
                celdaBajoRaton = null;
                actualizarRaton();
            }
        });
        return casilla;
    }

    private void alPulsarCasilla(int fila, int columna) {
        if (juego.isJuegoTerminado() || !juego.isInteractuable()) { return; }

        Celda celda = juego.getTablero().getCelda(fila, columna);
        if (celda == null) { return; }

        if (origenSeleccionado == null) {
            if (juego.esSeleccionable(celda)) {
                origenSeleccionado = celda;
            } else {
                avisar("Elige una ficha tuya del tipo " + juego.getFichaPorMover().nombre);
            }
        } else if (celda == origenSeleccionado) {
            origenSeleccionado = null;
        } else if (juego.esDestinoValido(origenSeleccionado, celda)) {
            ejecutarJugada(origenSeleccionado, celda);
            return;
        } else if (juego.esSeleccionable(celda)) {
            origenSeleccionado = celda;
        } else {
            avisar("Esa casilla no es un destino valido");
        }

        redibujar();
    }

    private void ejecutarJugada(Celda origen, Celda destino) {
        Accion[] acciones = juego.accionesPosibles(origen, destino);
        Accion elegida = DialogoAccion.preguntar(this, "Acción sobre "
                + destino.getFila() + ":" + destino.getColumna(),
                descripcionDestino(origen, destino), acciones);

        if (elegida == null) {
            redibujar();
            return;
        }

        juego.jugar(origen, destino, elegida);
        origenSeleccionado = null;
        redibujar();

        if (juego.isJuegoTerminado()) {
            mostrarPanelTexto(juego.getUltimoMensaje(), new PanelMenuPrincipal());
            return;
        }
        girarRuleta();
    }

    private String descripcionDestino(Celda origen, Celda destino) {
        String atacante = origen.getFicha().getNombre();
        if (!destino.tieneFicha()) {
            return "¿Qué hace tu " + atacante + " en la casilla vacía "
                    + destino.getFila() + ":" + destino.getColumna() + "?";
        }
        return "¿Cómo ataca tu " + atacante + " al " + destino.getFicha().getNombre()
                + " enemigo de " + destino.getFila() + ":" + destino.getColumna() + "?";
    }

    private void avisar(String texto) {
        avisoPendiente = texto;
        if (labelMensaje != null) { labelMensaje.setText(texto); }
    }

    private void girarRuleta() {
        if (juego.isJuegoTerminado() || ruleta == null) { return; }

        juego.setInteractuable(false);
        ruletaGirando = true;
        origenSeleccionado = null;
        redibujar();
        ruleta.girarHasta(juego.getFichaPorMover(), this::alDetenerseLaRuleta);
    }

    private void detenerRuleta() {
        if (ruleta != null) { ruleta.detener(); }
    }

    private void alDetenerseLaRuleta() {
        ruletaGirando = false;
        if (juego.isJuegoTerminado()) { return; }

        if (juego.puedeUsarResultado()) {
            girosSinJugada = 0;
            juego.setInteractuable(true);
            redibujar();
            return;
        }

        girosSinJugada++;
        if (girosSinJugada >= MAX_GIROS_SIN_JUGADA) {
            redibujar();
            avisar("Ningún jugador puede mover: la partida quedó bloqueada.");
            return;
        }

        if (juego.quedanGiros()) {
            juego.girar();
        } else {
            juego.perderTurno();
        }
        girarRuleta();
    }

    public void redibujar() {
        if (casillas == null || juego == null) { return; }

        for (int fila = 0; fila < LADO; fila++) {
            for (int columna = 0; columna < LADO; columna++) {
                actualizarCasilla(casillas[fila][columna],
                                  juego.getTablero().getCelda(fila, columna));
            }
        }
        actualizarCabecera();
        actualizarGiros();
        actualizarHistorial();
        actualizarInferior();
        tableroPanel.repaint();
    }

    private void actualizarInferior() {
        if (iconoSeleccionada == null) { return; }

        mostrarFicha(iconoSeleccionada, datosSeleccionada,
                origenSeleccionado == null ? null : origenSeleccionado.getFicha());
        actualizarRaton();

        mostrarComidas(tiraComidas1, juego.getComidasPor(true));
        mostrarComidas(tiraComidas2, juego.getComidasPor(false));
    }

    private void actualizarRaton() {
        if (iconoRaton == null) { return; }
        mostrarFicha(iconoRaton, datosRaton,
                celdaBajoRaton == null ? null : celdaBajoRaton.getFicha());
    }

    private void mostrarFicha(JLabel icono, JLabel[] valores, Ficha ficha) {
        if (ficha == null) {
            icono.setIcon(null);
            icono.setText("");
            for (JLabel valor : valores) {
                valor.setText("-");
            }
            return;
        }

        icono.setIcon(CargadorAssets.icono(ficha.getUrlAsset(), 70));
        icono.setText(icono.getIcon() == null
                ? ficha.getNombre().substring(0, 1).toUpperCase() : "");

        valores[0].setText(ficha.getNombre());
        valores[1].setText(ficha.delJugador() ? "Blancas" : "Negras");
        valores[2].setText(String.valueOf(ficha.getAtaque()));
        valores[3].setText(ficha.getVida() + " / " + ficha.getTipoFicha().vida);
        valores[4].setText(ficha.getEscudo() + " / " + ficha.getTipoFicha().escudo);
    }

    private void mostrarComidas(JPanel tira, Ficha[] comidas) {
        tira.removeAll();

        if (comidas.length == 0) {
            JLabel vacio = agregarLabel("Ninguna todavía");
            tira.add(vacio);
        } else {
            for (Ficha ficha : comidas) {
                tira.add(crearMiniatura(ficha));
            }
        }

        tira.revalidate();
        tira.repaint();
    }

    private JLabel crearMiniatura(Ficha ficha) {
        JLabel miniatura = new JLabel();
        miniatura.setIcon(CargadorAssets.icono(ficha.getUrlAsset(), 34));
        if (miniatura.getIcon() == null) {
            miniatura.setText(ficha.getNombre().substring(0, 1).toUpperCase());
            miniatura.setForeground(Color.WHITE);
        }
        return miniatura;
    }


    private void actualizarGiros() {
        if (labelGiros == null) { return; }

        if (juego.isJuegoTerminado()) {
            labelGiros.setText("");
        } else if (ruletaGirando) {
            labelGiros.setText("Girando...");
        } else {
            labelGiros.setText("Oportunidades: " + juego.getGirosRestantes()
                    + " de " + juego.getGirosDelTurno());
        }

        if (botonDetener != null) {
            botonDetener.setEnabled(ruletaGirando && !juego.isJuegoTerminado());
        }
    }

    private void actualizarHistorial() {
        if (areaHistorial == null) { return; }
        if (ruletaGirando) { return; }

        String[] historial = juego.getHistorial();
        for (int i = historialPintado; i < historial.length; i++) {
            areaHistorial.append(historial[i] + "\n");
        }
        historialPintado = historial.length;
        areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
    }

    private void actualizarCasilla(CasillaBoton boton, Celda celda) {
        boton.setAssetCelda(CargadorAssets.cargar(celda.getUrlAsset()));

        Ficha ficha = celda.getFicha();
        if (ficha == null) {
            boton.setAssetFicha(null);
            boton.setRespaldo(null, null);
        } else {
            boton.setAssetFicha(CargadorAssets.cargar(ficha.getUrlAsset()));
            boton.setRespaldo(ficha.getNombre().substring(0, 1).toUpperCase(),
                    ficha.delJugador() ? new Color(240, 236, 228) : new Color(150, 140, 170));
        }

        boton.setTinte(calcularTinte(celda));
    }

    private Color calcularTinte(Celda celda) {
        if (juego.isJuegoTerminado() || ruletaGirando) { return null; }

        if (origenSeleccionado == null) {
            return juego.esSeleccionable(celda) ? TINTE_SELECCIONABLE : null;
        }
        if (celda == origenSeleccionado) { return TINTE_SELECCIONADA; }

        return colorDe(accionPrincipal(juego.accionesPosibles(origenSeleccionado, celda)));
    }

    private Accion accionPrincipal(Accion[] acciones) {
        for (Accion candidata : PRIORIDAD) {
            for (Accion disponible : acciones) {
                if (disponible == candidata) { return candidata; }
            }
        }
        return null;
    }

    private Color colorDe(Accion accion) {
        if (accion == null) { return null; }
        switch (accion) {
            case ATACAR:
            case ABSORBER:      return TINTE_ATACAR;
            case LANZA:         return TINTE_LANZA;
            case ATAQUE_ZOMBIE: return TINTE_ZOMBIE;
            case MOVER:         return TINTE_MOVER;
            case INVOCAR:       return TINTE_INVOCAR;
            default:            return null;
        }
    }

    private void actualizarCabecera() {
        if (labelTurno == null) { return; }

        if (juego.isJuegoTerminado()) {
            labelTurno.setText("Partida terminada");
            labelRuleta.setText("");
            labelMensaje.setText(juego.getUltimoMensaje());
            return;
        }

        Usuario enTurno = juego.getJugadorEnTurno();
        labelTurno.setText("Turno de "
                + (enTurno != null ? enTurno.getNombre()
                                   : (juego.isJugador1Turno() ? "Jugador 1" : "Jugador 2"))
                + (juego.isJugador1Turno() ? "  (blancas)" : "  (negras)"));

        if (ruletaGirando) {
            labelRuleta.setText("Ruleta: girando...");
            labelMensaje.setText("Espera a que se detenga, o pulsa Detener");
            return;
        }

        labelRuleta.setText("Ruleta: " + juego.getFichaPorMover().nombre);

        if (avisoPendiente != null) {
            labelMensaje.setText(avisoPendiente);
            avisoPendiente = null;
        } else if (origenSeleccionado == null) {
            labelMensaje.setText("Pulsa una ficha resaltada en dorado");
        } else {
            labelMensaje.setText("Pulsa una casilla de color, o la misma ficha para soltarla");
        }
    }
}
