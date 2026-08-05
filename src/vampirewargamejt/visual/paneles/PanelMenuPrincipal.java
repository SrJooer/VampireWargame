package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelMenuPrincipal extends PanelAbstracto{

    private JPanel menuPanel;
    private JPanel botonesPanel;

    public PanelMenuPrincipal(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
    }

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
                    if (GestorUsuarios.obtenerJugadoresDisponibles().length == 0) {
                        mostrarPanelTexto("No hay más jugadores disponibles para jugar.", new PanelMenuPrincipal(gestorPaneles));
                    } else {
                        gestorPaneles.mostrarPanel(new PanelSelector(gestorPaneles));
                    }
                }));
        botonesPanel.add(agregarBoton("Mi Cuenta", () -> gestorPaneles.mostrarPanel(new PanelMiCuenta(gestorPaneles))));
        botonesPanel.add(agregarBoton("Reportes", () -> gestorPaneles.mostrarPanel(new PanelReportes(gestorPaneles))));
        botonesPanel.add(agregarBoton("Cerrar sesión", () -> {
            gestorPaneles.mostrarPanel(new PanelInicio(gestorPaneles));
            GestorUsuarios.cerrarSesion();
        }));
        menuPanel.add(botonesPanel);
    }
}
