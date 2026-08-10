package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelMenuPrincipal extends PanelAbstracto{

    private JPanel menuPanel;
    private JPanel botonesPanel;

    @Override
    public void iniciarPanel() {
        prepararContenido();
            prepararMenu();
                prepararBotones();

            contenidoPanel.add(menuPanel);
            add(contenidoPanel);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Vampire Wargame"));
        menuPanel.add(Box.createVerticalStrut(40));
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(4, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Jugar Vampire Wargame", () ->
                {
                    if (gestorUsuarios.obtenerJugadoresDisponibles().length == 0) {
                        mostrarPanelTexto("No hay más jugadores disponibles para jugar.", new PanelMenuPrincipal());
                    } else {
                        gestorPaneles.mostrarPanel(new PanelSelector());
                    }
                }));
        botonesPanel.add(agregarBoton("Mi Cuenta", () -> gestorPaneles.mostrarPanel(new PanelMiCuenta())));
        botonesPanel.add(agregarBoton("Reportes", () -> gestorPaneles.mostrarPanel(new PanelReportes())));
        botonesPanel.add(agregarBoton("Cerrar sesión", () -> {
            gestorPaneles.mostrarPanel(new PanelInicio());
            gestorUsuarios.cerrarSesion();
        }));
        menuPanel.add(botonesPanel);
    }
}
