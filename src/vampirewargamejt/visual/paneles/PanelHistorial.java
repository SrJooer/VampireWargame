package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.Usuario;

import javax.swing.*;
import java.awt.*;

public class PanelHistorial extends PanelAbstracto{
    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JScrollPane scrollPane;

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

        Usuario usuario = gestorUsuarios.getUsuarioActual();
        String[] partidas = usuario == null ? new String[0] : usuario.getHistorialArreglo();

        if (partidas.length == 0) {
            historial.append("Todavía no has terminado ninguna partida.\n");
        }
        for (int i = partidas.length - 1; i >= 0; i--) {
            historial.append(partidas[i] + "\n");
        }

        historial.setEditable(false);
        scrollPane = agregarScrollPanel();
        scrollPane.setViewportView(historial);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelReportes())));
    }
}
