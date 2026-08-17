package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelSesion extends PanelAbstracto {
    private JPanel menuPanel;
    private JPanel formularioPanel;
    private JPanel botonesPanel;

    private JTextField nombreUsuario;
    private JPasswordField clave;

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
        menuPanel.add(agregarTitulo("Iniciar Sesión"));
        menuPanel.add(Box.createVerticalStrut(80));
        menuPanel.add(formularioPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararFormulario() {
        formularioPanel = agregarPanel(new GridLayout(2, 2, 0, 12));
        formularioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formularioPanel.add(agregarLabel("Usuario:"));
        nombreUsuario = agregarCampoTexto("Ingrese su nombre de usuario");
        formularioPanel.add(nombreUsuario);
        formularioPanel.add(agregarLabel("Clave:"));
        clave = agregarCampoClave("Ingrese su clave");
        formularioPanel.add(clave);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 24, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelInicio())));
        botonesPanel.add(agregarBoton("Iniciar sesión", () -> {
            int response = gestorUsuarios.iniciarSesion(nombreUsuario.getText(), new String(clave.getPassword()));
            showResponse(response);
        }));
    }

    private void showResponse(int response) {
        switch (response) {
            case 5,4 -> mostrarPanelTexto("Rellene todos los campos", new PanelInicio());
            case 3 -> mostrarPanelTexto("Usuario desactivado", new PanelInicio());
            case 2 -> gestorPaneles.mostrarPanel(new PanelMenuPrincipal());
            case 1, 0 -> mostrarPanelTexto("Usuario o clave incorrectos", new PanelInicio());
            default -> mostrarPanelTexto("Error desconocido", new PanelInicio());
        }
    }
}
