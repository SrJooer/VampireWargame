package vampirewargamejt.visual;

import vampirewargamejt.visual.paneles.PanelAbstracto;
import vampirewargamejt.visual.paneles.PanelInicio;

import javax.swing.*;
import java.awt.*;

public class GestorPaneles {

    private final JFrame ventana;
    private JPanel panelPrincipal;
    private JPanel panelActual;

    public GestorPaneles(JFrame ventana) {
        this.ventana = ventana;
        iniciarPanelPrincipal();
        mostrarPanel(new PanelInicio(this));
    }

    private void iniciarPanelPrincipal() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new CardLayout());
        ventana.add(panelPrincipal);
    }

    public void mostrarPanel(JPanel panel) {
        panelPrincipal.removeAll();
        panelPrincipal.add(panel);
        panelPrincipal.revalidate();
        panelPrincipal.repaint();
        panelActual = panel;
    }

    public JFrame getVentana() { return ventana; }
    public PanelAbstracto getPanelActual() { return (PanelAbstracto) panelActual; }
}
