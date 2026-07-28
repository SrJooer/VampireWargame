package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelCrearJugador extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JPanel formularioPanel;

    private JLabel responseLabel;

    private JTextField nombreUsuario;
    private JTextField clave;

    public PanelCrearJugador(GestorPaneles gestorPaneles) {
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
        menuPanel.add(agregarTitulo("Crear Jugador"));
        responseLabel = agregarLabel("");
        menuPanel.add(Box.createVerticalStrut(80));
        menuPanel.add(responseLabel);
        menuPanel.add(Box.createVerticalStrut(12));
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
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelInicio(gestorPaneles))));
        botonesPanel.add(agregarBoton("Crear Jugador", () -> {
            int response = GestorUsuarios.registrarUsuario(nombreUsuario.getText(), clave.getText());
            showResponse(response);
        }));
    }

    private void showResponse(int response) {
        boolean created = response == 1;

        if (created) {
            nombreUsuario.setText("");
            clave.setText("");
        }

        switch (response) {
            case 4, 3 -> responseLabel.setText("Rellene todos los campos");
            case 2 -> responseLabel.setText("La clave debe tener 5 caracteres");
            case 1 ->  responseLabel.setText("Usuario creado con éxito");
            case 0 -> responseLabel.setText("El usuario ya existe");
        }
    }

}
