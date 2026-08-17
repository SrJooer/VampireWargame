package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelSelector extends PanelAbstracto {
    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JComboBox<String> comboBox;

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
        menuPanel.add(agregarTitulo("Seleccionar Contricante"));
        menuPanel.add(Box.createVerticalStrut(40));
        String[] jugadores = gestorUsuarios.obtenerJugadoresDisponibles();
        comboBox = agregarComboBox(jugadores);
        menuPanel.add(comboBox);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 12, 0));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMenuPrincipal())));
        botonesPanel.add(agregarBoton("Jugar", this::empezarPartida));
    }

    private void empezarPartida() {
        Object elegido = comboBox.getSelectedItem();
        if (elegido == null) {
            mostrarPanelTexto("Selecciona un contrincante para jugar.", new PanelMenuPrincipal());
            return;
        }
        gestorUsuarios.establecerContricante(elegido.toString());
        gestorPaneles.mostrarPanel(new PanelJuego());
    }
}
