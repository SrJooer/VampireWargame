package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.excepciones.VampireWargameException;

import javax.swing.*;
import java.awt.*;

public class PanelCrearJugador extends PanelAbstracto {
    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JPanel formularioPanel;

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
        clave = agregarCampoClave("Ingrese una clave");
        formularioPanel.add(clave);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 24, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelInicio())));
        botonesPanel.add(agregarBoton("Crear Jugador", this::intentarCrearJugador));
    }

    private void intentarCrearJugador() {
        String nombre = nombreUsuario.getText();
        String textoClave = new String(clave.getPassword());

        try {
            gestorUsuarios.registrarUsuario(nombre, textoClave);
            gestorUsuarios.iniciarSesion(nombre, textoClave);
            gestorPaneles.mostrarPanel(new PanelMenuPrincipal());
        } catch (VampireWargameException e) {
            mostrarPanelTexto(e.getMessage(), new PanelInicio());
        }
    }
}
