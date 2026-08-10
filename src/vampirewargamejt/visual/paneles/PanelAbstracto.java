package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public abstract class PanelAbstracto extends JPanel {

    protected final GestorPaneles gestorPaneles;
    protected final GestorUsuarios gestorUsuarios;
    protected JPanel contenidoPanel;

    public PanelAbstracto() {
        this.gestorPaneles = GestorPaneles.getInstance();
        this.gestorUsuarios = GestorUsuarios.getInstance();
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

    public JTextArea agregarAreaTexto() {
        JTextArea areaTexto = new JTextArea(10, 30);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setOpaque(false);
        areaTexto.setFont(new Font("Arial", Font.PLAIN, 16));
        areaTexto.setForeground(Color.WHITE);
        return areaTexto;
    }

    public JScrollPane agregarScrollPanel() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        return scrollPane;
    }

    public JComboBox<String> agregarComboBox(String[] opciones) {
        JComboBox<String> comboBox = new JComboBox<>(opciones);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBackground(Color.BLACK);
        return comboBox;
    }


    protected void prepararContenido() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new GridBagLayout());
        contenidoPanel.setBackground(Color.BLACK);
    }

    public GridBagConstraints crearGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    public GridBagConstraints agregarGbc(int x, int y, int ancho, int alto) {
        GridBagConstraints gbc = crearGbc(x, y);
        gbc.gridwidth = ancho;
        gbc.gridheight = alto;
        return gbc;
    }

    public GridBagConstraints agregarGbc(int x, int y, int ancho, int alto, int margin) {
        GridBagConstraints gbc = agregarGbc(x, y, ancho, alto);
        gbc.insets = new Insets(margin, margin, margin, margin);
        return gbc;
    }

    public void mostrarPanelTexto(String texto, PanelAbstracto siguientePanel) {
        PanelTexto panel = new PanelTexto(texto, () -> gestorPaneles.mostrarPanel(siguientePanel));
        gestorPaneles.mostrarPanel(panel);
    }

    public Color getColor(int r, int g, int b) {
        return new Color(r, g, b);
    }
}