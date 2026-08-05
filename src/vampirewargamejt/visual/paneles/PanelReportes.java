package vampirewargamejt.visual.paneles;

import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelReportes extends PanelAbstracto{

    private JPanel menuPanel;
    private JPanel botonesPanel;

    public PanelReportes(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararBotones();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Reportes"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(3, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.setOpaque(false);
        botonesPanel.add(agregarBoton("Ranking", () -> gestorPaneles.mostrarPanel(new PanelRanking(gestorPaneles))));
        botonesPanel.add(agregarBoton("Historial", () -> gestorPaneles.mostrarPanel(new PanelHistorial(gestorPaneles))));
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMenuPrincipal(gestorPaneles))));
    }
}
