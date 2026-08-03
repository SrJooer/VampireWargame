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
    private JLabel responseLabel;

    public PanelCambiarClave(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
        iniciarPanel();
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
        responseLabel = agregarLabel(" ");
        menuPanel.add(responseLabel);
        menuPanel.add(formularioPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararFormulario() {
        formularioPanel = agregarPanel(new GridLayout(2, 2, 0, 12));
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
                responseLabel.setText("Error al cambiar la contraseña.");
                break;
            case 2:
                responseLabel.setText("Contraseña cambiada con éxito.");
                break;
            case 3:
                responseLabel.setText("La nueva contraseña debe tener exactamente 5 caracteres.");
                break;
            case 4:
                responseLabel.setText("La nueva contraseña no puede estar vacía.");
                break;
        }
    }

}
