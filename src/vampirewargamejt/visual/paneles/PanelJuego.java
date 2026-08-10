package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PanelJuego extends PanelAbstracto {

    private Usuario contricante = gestorUsuarios.getUsuarioContricante();

    private JPanel contenedorPrincipal;
    private JPanel interfazPanel;
    private JPanel escenarioPanel;
    private JPanel tablaPanel;
    private JPanel panelInferior;

    private HashMap<JButton, Point> coordenadasBotones;
    private final int TAMANIO_TABLERO = 8;

    @Override
    public void iniciarPanel() {
        prepararContenido();
        coordenadasBotones = new HashMap<>();

        contenedorPrincipal = agregarPanel(new BorderLayout());

        JPanel panelSuperior = agregarPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Interfaz - 20% del ancho
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        prepararInterfaz();
        panelSuperior.add(interfazPanel, gbc);

        // Escenario - 80% del ancho
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        prepararEscenario();
        panelSuperior.add(escenarioPanel, gbc);

        // Panel inferior - 100% de ancho, 20% de alto
        prepararPanelInferior();

        contenedorPrincipal.add(panelSuperior, BorderLayout.CENTER);
        contenedorPrincipal.add(panelInferior, BorderLayout.SOUTH);

        contenidoPanel.add(contenedorPrincipal, BorderLayout.CENTER);
        add(contenidoPanel, BorderLayout.CENTER);
    }

    private void prepararInterfaz() {
        interfazPanel = agregarPanel(new GridBagLayout());
        interfazPanel.setOpaque(true);
        interfazPanel.setBackground(new Color(60, 60, 100));
        interfazPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    }

    private void prepararEscenario() {
        escenarioPanel = agregarPanel(new GridBagLayout()) {
            @Override
            public void doLayout() {
                super.doLayout();
                if (tablaPanel != null) {
                    // Calcular el tamaño cuadrado máximo que cabe en el escenario
                    int anchoDisponible = getWidth() - 40;
                    int altoDisponible = getHeight() - 40;
                    int tamanioCuadrado = Math.min(anchoDisponible, altoDisponible);

                    Dimension tamanioCuadradoDim = new Dimension(tamanioCuadrado, tamanioCuadrado);
                    tablaPanel.setPreferredSize(tamanioCuadradoDim);
                    tablaPanel.setMaximumSize(tamanioCuadradoDim);
                    tablaPanel.setMinimumSize(tamanioCuadradoDim);
                }
            }
        };
        escenarioPanel.setOpaque(true);
        escenarioPanel.setBackground(new Color(40, 40, 40));
        escenarioPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        escenarioPanel.add(prepararTabla(), gbc);
    }

    private JPanel prepararTabla() {
        tablaPanel = agregarPanel(new GridLayout(TAMANIO_TABLERO, TAMANIO_TABLERO, 0, 0));
        tablaPanel.setOpaque(true);
        tablaPanel.setBackground(Color.BLACK);
        tablaPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));

        // Crear botones con patrón de ajedrez y asignar coordenadas
        for (int fila = 0; fila < TAMANIO_TABLERO; fila++) {
            for (int columna = 0; columna < TAMANIO_TABLERO; columna++) {
                JButton boton = new JButton();

                // Patrón de colores alternos (como tablero de ajedrez)
                if ((fila + columna) % 2 == 0) {
                    boton.setBackground(Color.WHITE);
                } else {
                    boton.setBackground(Color.BLACK);
                }

                boton.setOpaque(true);
                boton.setBorderPainted(true);
                boton.setFocusPainted(false);
                boton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                // Asignar coordenada al botón
                Point coordenada = new Point(fila, columna);
                coordenadasBotones.put(boton, coordenada);

                // Opcional: mostrar coordenadas en el botón (puedes quitarlo después)
                boton.setFont(new Font("Arial", Font.PLAIN, 10));
                boton.setForeground(Color.RED);
                boton.setText(fila + "," + columna);

                tablaPanel.add(boton);
            }
        }

        return tablaPanel;
    }

    private void prepararPanelInferior() {
        panelInferior = agregarPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                // El panel inferior ocupará el 20% del alto total
                Dimension parentSize = getParent().getSize();
                return new Dimension(parentSize.width, (int)(parentSize.height * 0.25));
            }
        };
        panelInferior.setOpaque(true);
        panelInferior.setBackground(new Color(80, 40, 40));
        panelInferior.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    }

    // Método para obtener las coordenadas de un botón
    public Point obtenerCoordenadas(JButton boton) {
        return coordenadasBotones.get(boton);
    }

}
