package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelCrearJugador extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JPanel formularioPanel;

    private JTextField nombreUsuario;
    private JTextField clave;

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
        menuPanel.add(agregarTitulo("Crear Jugador"));
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
        clave = agregarCampoTexto("Ingrese una clave");
        formularioPanel.add(clave);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 24, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelInicio())));
        botonesPanel.add(agregarBoton("Crear Jugador", () -> {
            String nombre = nombreUsuario.getText();
            String clave = this.clave.getText();
            int response = gestorUsuarios.registrarUsuario(nombre, clave);
            showResponse(response, nombre, clave);
        }));
    }

    private void showResponse(int response, String nombre, String clave) {

        switch (response) {
            case 4, 3 -> mostrarPanelTexto("Rellene todos los campos", new PanelInicio());
            case 2 -> mostrarPanelTexto("La clave debe tener 5 caracteres", new PanelInicio());
            case 1 ->  {
                gestorUsuarios.iniciarSesion(nombre, clave);
                gestorPaneles.mostrarPanel(new PanelMenuPrincipal());
            }
            case 0 -> mostrarPanelTexto("El usuario ya existe", new PanelInicio());
        }
    }

}
