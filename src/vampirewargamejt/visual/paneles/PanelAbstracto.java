package vampirewargamejt.visual.paneles;

import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public abstract class PanelAbstracto extends JPanel {

    protected final GestorPaneles gestorPaneles;

    protected JPanel contenidoPanel;

    public PanelAbstracto(GestorPaneles gestorPaneles) {
        this.gestorPaneles = gestorPaneles;
    }

    public abstract void iniciarPanel();

    public JButton agregarBoton(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(Color.WHITE);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.setPreferredSize(new Dimension(200, 30));
        b.setMaximumSize(new Dimension(200, 30));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setForeground(Color.WHITE);
                b.setBackground(Color.BLACK);
                b.setBorderPainted(true);
                b.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setForeground(Color.BLACK);
                b.setBackground(Color.WHITE);
            }
        });
        return b;
    }

    public JButton agregarBoton(String texto, Runnable accion) {
        JButton b = agregarBoton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }

    public JLabel agregarLabel(String t) {
        JLabel l = new JLabel(t);
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    public JLabel agregarTitulo(String t) {
        JLabel l = agregarLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 40));
        l.setForeground(Color.WHITE);
        return l;
    }

    public JTextField agregarCampoTexto(String texto) {
        JTextField campoTexto = new JTextField();
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 16));
        campoTexto.setToolTipText(texto);
        campoTexto.setMaximumSize(new Dimension(200, 30));
        return campoTexto;
    }

    public JPanel agregarPanel(LayoutManager l) {
        JPanel p = new JPanel(l);
        p.setOpaque(false);
        return p;
    }

    public JPanel agregarPanel(int n) {
        JPanel p = agregarPanel(new FlowLayout());
        p.setLayout(new BoxLayout(p, (n == 0? BoxLayout.X_AXIS : BoxLayout.Y_AXIS)));
        return p;
    }

    protected void prepararContenido() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new GridBagLayout());
        contenidoPanel.setBackground(Color.BLACK);
    }
}
