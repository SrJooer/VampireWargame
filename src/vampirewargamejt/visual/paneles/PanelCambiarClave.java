package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelCambiarClave extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel formularioPanel;
    private JPanel botonesPanel;

    private JTextField nuevaClave;

    public PanelCambiarClave(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararFormulario();
        prepararBotones();
        prepararMenu();

        contenidoPanel.add(menuPanel);
        add(contenidoPanel, BorderLayout.CENTER);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Cambiar Contraseña"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(formularioPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararFormulario() {
        formularioPanel = agregarPanel(new GridLayout(1, 2, 12, 12));
        formularioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formularioPanel.add(agregarLabel("Nueva Contraseña:"));
        nuevaClave = agregarCampoTexto("Ingrese su nueva contraseña");
        formularioPanel.add(nuevaClave);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 24, 12));
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMiCuenta(gestorPaneles))));
        botonesPanel.add(agregarBoton("Cambiar", () -> {
            mostrarRepuesta(GestorUsuarios.cambiarClave(nuevaClave.getText()));
        }));
    }

    private void mostrarRepuesta(int repuesta) {
        switch (repuesta) {
            case 1:
                mostrarPanelTexto("Error al cambiar la contraseña.", new PanelMiCuenta(gestorPaneles));
                break;
            case 2:
                mostrarPanelTexto("Contraseña cambiada con éxito.", new PanelMiCuenta(gestorPaneles));
                break;
            case 3:
                mostrarPanelTexto("La nueva contraseña debe tener exactamente 5 caracteres.", new PanelMiCuenta(gestorPaneles));
                break;
            case 4:
                mostrarPanelTexto("La nueva contraseña no puede estar vacía.", new PanelMiCuenta(gestorPaneles));
                break;
        }
    }

}
