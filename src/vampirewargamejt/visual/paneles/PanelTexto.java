package vampirewargamejt.visual.paneles;

import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelTexto extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;
    private Runnable accionAceptar;
    private JLabel texto;

    public PanelTexto(GestorPaneles gestorPaneles, String texto, Runnable accionAceptar) {
        super(gestorPaneles);
        this.accionAceptar = accionAceptar;
        this.texto = agregarLabel(texto);
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararBotones();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(texto);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 1, 12, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Aceptar", () -> { accionAceptar.run(); }));
    }
}
