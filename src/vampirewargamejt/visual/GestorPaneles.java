package vampirewargamejt.visual;

import vampirewargamejt.visual.paneles.PanelAbstracto;
import vampirewargamejt.visual.paneles.PanelInicio;

import javax.swing.*;
import java.awt.*;

public class GestorPaneles {

    private static GestorPaneles gestorPaneles;

    private final JFrame ventana;
    private JPanel panelPrincipal;

    private GestorPaneles(JFrame ventana) {
        this.ventana = ventana;
    }

    public static GestorPaneles getInstance() {
        if (gestorPaneles == null) {
            gestorPaneles = new GestorPaneles(crearVentana());
        }
        return gestorPaneles;
    }

    private static JFrame crearVentana() {
        JFrame ventana = new JFrame();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1000, 800);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        return ventana;
    }

    public void iniciarPanelPrincipal() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new CardLayout());
        ventana.add(panelPrincipal);
        mostrarPanel(new PanelInicio());
    }

    public void mostrarPanel(JPanel panel) {
        if (panel instanceof PanelAbstracto) {
            ((PanelAbstracto) panel).iniciarPanel();
        }
        panelPrincipal.removeAll();
        panelPrincipal.add(panel);
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
    }
}
