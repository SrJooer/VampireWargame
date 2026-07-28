package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelSesion extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JPanel formularioPanel;

    private JLabel responseLabel;

    private JTextField nombreUsuario;
    private JTextField clave;

    public PanelSesion(GestorPaneles gestorPaneles) {
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
        menuPanel.add(agregarTitulo("Iniciar Sesión"));
        responseLabel = agregarLabel("");
        menuPanel.add(Box.createVerticalStrut(80));
        menuPanel.add(responseLabel);
        menuPanel.add(Box.createVerticalStrut(12));
        menuPanel.add(formularioPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 24, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelInicio(gestorPaneles))));
        botonesPanel.add(agregarBoton("Iniciar sesión", () -> {
            int response = GestorUsuarios.iniciarSesion(nombreUsuario.getText(), clave.getText());
            showResponse(response);
        }));
    }

    private void prepararFormulario() {
        formularioPanel = agregarPanel(new GridLayout(2, 2, 0, 12));
        formularioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formularioPanel.add(agregarLabel("Usuario:"));
        nombreUsuario = agregarCampoTexto("Ingrese su nombre de usuario");
        formularioPanel.add(nombreUsuario);
        formularioPanel.add(agregarLabel("Clave:"));
        clave = agregarCampoTexto("Ingrese su clave");
        formularioPanel.add(clave);
    }

    private void showResponse(int response) {
        switch (response) {
            case 4,3 -> responseLabel.setText("Rellene todos los campos");
            case 2 -> gestorPaneles.mostrarPanel(new PanelPreJuego(gestorPaneles));
            case 1, 0 -> responseLabel.setText("Usuario o clave incorrectos");
            default -> responseLabel.setText("Error desconocido");
        }
    }
}
