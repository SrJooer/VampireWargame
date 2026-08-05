package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelSelector extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;

    public PanelSelector(GestorPaneles gestorPaneles) {
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
        menuPanel.add(agregarTitulo("Seleccionar Contricante"));
        menuPanel.add(Box.createVerticalStrut(40));
        String[] jugadores = GestorUsuarios.obtenerJugadoresDisponibles();
        menuPanel.add(agregarComboBox(jugadores));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 12, 0));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Jugar", () -> {
            // Acción para iniciar el juego con el contrincante seleccionado
            JOptionPane.showMessageDialog(this, "Iniciando juego...");
        }));
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMenuPrincipal(gestorPaneles))));
    }
}
