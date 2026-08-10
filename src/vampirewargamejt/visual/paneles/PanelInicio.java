package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelInicio extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararBotones();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel, BorderLayout.CENTER);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Vampire Wargame"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = new JPanel(new GridLayout(0, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.setOpaque(false);

        botonesPanel.add(agregarBoton("Iniciar Sesión", () -> gestorPaneles.mostrarPanel(new PanelSesion())));
        botonesPanel.add(agregarBoton("Crear Jugador", () -> gestorPaneles.mostrarPanel(new PanelCrearJugador())));
        botonesPanel.add(agregarBoton("Salir", () -> System.exit(0)));

    }
}
