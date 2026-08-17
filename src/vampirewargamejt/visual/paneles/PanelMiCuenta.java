package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelMiCuenta extends PanelAbstracto {
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

    public void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Mi Cuenta"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    public void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(4, 1, 24, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Ver mi información", () -> gestorPaneles.mostrarPanel(new PanelMiInformacion())));
        botonesPanel.add(agregarBoton("Cambiar contraseña", () -> gestorPaneles.mostrarPanel(new PanelCambiarClave())));
        botonesPanel.add(agregarBoton("Cerrar mi cuenta", () -> {
            gestorUsuarios.cerrarCuenta();
            gestorPaneles.mostrarPanel(new PanelInicio());
        }));
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMenuPrincipal())));
    }
}
