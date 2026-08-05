package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PanelHistorial extends PanelAbstracto{

    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JScrollPane scrollPane;

    public PanelHistorial(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararHistorialMenu();
        prepararBotones();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel, BorderLayout.CENTER);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Historial"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(scrollPane);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararHistorialMenu() {
        JTextArea historial = agregarAreaTexto();

        ArrayList<String> historialJugadores = GestorUsuarios.usuarioActual.getHistorial();

        for (String mensaje : historialJugadores) {
            historial.append(mensaje + "\n");
        }

        historial.setEditable(false);
        scrollPane = agregarScrollPanel();
        scrollPane.setViewportView(historial);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelReportes(gestorPaneles))));
    }
}
