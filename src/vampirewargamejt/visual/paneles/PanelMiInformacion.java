package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelMiInformacion extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel tablaPanel;
    private JPanel botonesPanel;

    public PanelMiInformacion(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararTabla();
        prepararBotones();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Mi Información"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(tablaPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararTabla() {
        tablaPanel = agregarPanel(new GridLayout(5, 2, 12, 12));
        tablaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tablaPanel.add(agregarLabel("Nombre: "));
        tablaPanel.add(agregarLabel(GestorUsuarios.getUsuarioActual().getNombre()));
        tablaPanel.add(agregarLabel("Contraseña: "));
        tablaPanel.add(agregarLabel(GestorUsuarios.getUsuarioActual().getClave()));
        tablaPanel.add(agregarLabel("Puntuaje: "));
        tablaPanel.add(agregarLabel(String.valueOf(GestorUsuarios.getUsuarioActual().getPuntos())));
        tablaPanel.add(agregarLabel("Partidas Ganadas: "));
        tablaPanel.add(agregarLabel(String.valueOf(GestorUsuarios.getUsuarioActual().getGanadas())));
        tablaPanel.add(agregarLabel("Partidas Perdidas: "));
        tablaPanel.add(agregarLabel(String.valueOf(GestorUsuarios.getUsuarioActual().getPerdidas())));
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 12, 0));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMiCuenta(gestorPaneles))));
    }
}
