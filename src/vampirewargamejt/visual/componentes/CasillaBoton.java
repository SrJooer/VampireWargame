package vampirewargamejt.visual.componentes;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CasillaBoton extends JButton {
    private static final double FUERZA_TINTE = 0.62;
    private static final double PROPORCION_FICHA = 0.80;

    private final int fila;
    private final int columna;
    private final Color colorBase;

    private Image assetCelda;
    private Image assetFicha;
    private String textoRespaldo;
    private Color colorRespaldo = Color.WHITE;
    private Color tinte;
    private boolean raton;

    public CasillaBoton(int fila, int columna, Color colorBase) {
        this.fila = fila;
        this.columna = columna;
        this.colorBase = colorBase;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(false);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { raton = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { raton = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        if (tinte == null && assetCelda != null) {
            g2.drawImage(assetCelda, 0, 0, w, h, this);
        } else {
            g2.setColor(tinte == null ? colorBase : mezclar(colorBase, tinte, FUERZA_TINTE));
            g2.fillRect(0, 0, w, h);
        }

        if (raton && isEnabled()) {
            g2.setColor(new Color(255, 255, 255, 34));
            g2.fillRect(0, 0, w, h);
        }

        dibujarFicha(g2, w, h);
        g2.dispose();
    }

    private void dibujarFicha(Graphics2D g2, int w, int h) {
        int lado = (int) (Math.min(w, h) * PROPORCION_FICHA);
        int x = (w - lado) / 2;
        int y = (h - lado) / 2;

        if (assetFicha != null) {
            g2.drawImage(assetFicha, x, y, lado, lado, this);
            return;
        }
        if (textoRespaldo == null) {
            return;
        }

        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillOval(x, y, lado, lado);
        g2.setColor(colorRespaldo);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x, y, lado, lado);
        g2.setFont(getFont().deriveFont(java.awt.Font.BOLD, Math.max(10f, lado * 0.5f)));

        java.awt.FontMetrics fm = g2.getFontMetrics();
        g2.drawString(textoRespaldo,
                (w - fm.stringWidth(textoRespaldo)) / 2f,
                h / 2f + fm.getAscent() / 2f - fm.getDescent() / 2f);
    }

    private Color mezclar(Color fondo, Color encima, double fuerza) {
        return new Color(
                (int) (fondo.getRed()   * (1 - fuerza) + encima.getRed()   * fuerza),
                (int) (fondo.getGreen() * (1 - fuerza) + encima.getGreen() * fuerza),
                (int) (fondo.getBlue()  * (1 - fuerza) + encima.getBlue()  * fuerza));
    }

    public void setAssetCelda(Image assetCelda) { this.assetCelda = assetCelda; }
    public void setAssetFicha(Image assetFicha) { this.assetFicha = assetFicha; }

    public void setRespaldo(String texto, Color color) {
        this.textoRespaldo = texto;
        this.colorRespaldo = color == null ? Color.WHITE : color;
    }

    public void setTinte(Color tinte) { this.tinte = tinte; }

    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}
