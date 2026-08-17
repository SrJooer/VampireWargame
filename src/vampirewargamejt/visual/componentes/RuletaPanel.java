package vampirewargamejt.visual.componentes;

import vampirewargamejt.modelo.juego.tablero.fichas.TipoFicha;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class RuletaPanel extends JPanel {
    private static final int GRADOS_POR_SECTOR = 60;
    private static final int DURACION_MS = 4000;
    private static final int FRENAZO_MS = 600;
    private static final int REFRESCO_MS = 16;
    private static final int VUELTAS = 5;

    private static final TipoFicha[] ORDEN = {
            TipoFicha.VAMPIRO, TipoFicha.LOBO, TipoFicha.MUERTE,
            TipoFicha.VAMPIRO, TipoFicha.LOBO, TipoFicha.MUERTE
    };

    private static final Color ORO = new Color(198, 152, 52);

    private final Image imagen;
    private final Timer temporizador;

    private double anguloActual;
    private double anguloInicio;
    private double anguloFinal;
    private long tiempoInicio;
    private int duracion = DURACION_MS;
    private Runnable alTerminar;

    public RuletaPanel() {
        setOpaque(false);
        imagen = CargadorAssets.cargar("/assets/ruleta.png");
        temporizador = new Timer(REFRESCO_MS, e -> avanzarAnimacion());
    }

    public boolean estaGirando() {
        return temporizador.isRunning();
    }

    public void girarHasta(TipoFicha tipo, Runnable alTerminar) {
        if (estaGirando() || tipo == null) { return; }

        this.alTerminar = alTerminar;
        anguloInicio = anguloActual;
        anguloFinal = calcularAnguloFinal(tipo);
        tiempoInicio = System.currentTimeMillis();
        duracion = DURACION_MS;
        temporizador.restart();
    }

    public void cancelar() {
        temporizador.stop();
    }

    public void detener() {
        if (!estaGirando()) { return; }

        anguloInicio = anguloActual;
        tiempoInicio = System.currentTimeMillis();
        duracion = FRENAZO_MS;
    }

    private double calcularAnguloFinal(TipoFicha tipo) {
        double centroSector = elegirSector(tipo) * GRADOS_POR_SECTOR + GRADOS_POR_SECTOR / 2.0;
        double destino = (360 - centroSector) % 360;
        double vueltasYaDadas = anguloInicio - (anguloInicio % 360);
        return vueltasYaDadas + VUELTAS * 360 + destino;
    }

    private int elegirSector(TipoFicha tipo) {
        int[] candidatos = new int[ORDEN.length];
        int encontrados = 0;
        for (int i = 0; i < ORDEN.length; i++) {
            if (ORDEN[i] == tipo) {
                candidatos[encontrados] = i;
                encontrados++;
            }
        }
        if (encontrados == 0) { return 0; }
        return candidatos[(int) (Math.random() * encontrados)];
    }

    private void avanzarAnimacion() {
        double avance = (double) (System.currentTimeMillis() - tiempoInicio) / duracion;
        if (avance > 1) { avance = 1; }

        anguloActual = anguloInicio + (anguloFinal - anguloInicio) * suavizado(avance);
        repaint();

        if (avance >= 1) {
            temporizador.stop();
            anguloActual = anguloFinal % 360;
            if (alTerminar != null) { alTerminar.run(); }
        }
    }

    private double suavizado(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int lado = Math.min(getWidth(), getHeight()) - 22;
        if (lado < 40) { g2.dispose(); return; }

        int cx = getWidth() / 2;
        int cy = getHeight() / 2 + 8;

        dibujarRueda(g2, cx, cy, lado);
        dibujarAguja(g2, cx, cy - lado / 2);

        g2.dispose();
    }

    private void dibujarRueda(Graphics2D g2, int cx, int cy, int lado) {
        Graphics2D gr = (Graphics2D) g2.create();
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gr.rotate(Math.toRadians(anguloActual), cx, cy);

        if (imagen != null) {
            gr.drawImage(imagen, cx - lado / 2, cy - lado / 2, lado, lado, this);
        } else {
            dibujarRespaldo(gr, cx, cy, lado);
        }
        gr.dispose();
    }

    private void dibujarRespaldo(Graphics2D g2, int cx, int cy, int lado) {
        int x = cx - lado / 2;
        int y = cy - lado / 2;
        for (int i = 0; i < ORDEN.length; i++) {
            g2.setColor(i % 2 == 0 ? new Color(70, 60, 88) : new Color(96, 84, 118));

            g2.fillArc(x, y, lado, lado, 90 - (i + 1) * GRADOS_POR_SECTOR, GRADOS_POR_SECTOR);
        }
        g2.setColor(ORO);
        g2.setStroke(new BasicStroke(4f));
        g2.drawOval(x, y, lado, lado);
    }

    private void dibujarAguja(Graphics2D g2, int cx, int bordeSuperior) {
        Polygon aguja = new Polygon();
        aguja.addPoint(cx, bordeSuperior + 16);
        aguja.addPoint(cx - 13, bordeSuperior - 12);
        aguja.addPoint(cx + 13, bordeSuperior - 12);

        g2.setColor(ORO);
        g2.fillPolygon(aguja);
        g2.setColor(new Color(30, 24, 34));
        g2.setStroke(new BasicStroke(2f));
        g2.drawPolygon(aguja);
    }
}
