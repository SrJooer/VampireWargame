package vampirewargamejt.visual.paneles;

import javax.swing.*;
import java.awt.*;

public class PanelConfirmar extends PanelAbstracto {
    private PanelAbstracto panelAnterior;
    private Runnable accionAceptar;
    private String texto;

    private JPanel menuPanel;
    private JPanel botonesPanel;

    public PanelConfirmar(PanelAbstracto panelAnterior, Runnable accionAceptar, String texto) {
        super();
        this.panelAnterior = panelAnterior;
        this.accionAceptar = accionAceptar;
        this.texto = texto;
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
        menuPanel.add(agregarTitulo(texto));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 12, 0));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Cancelar", () -> gestorPaneles.mostrarPanel(panelAnterior)));
        botonesPanel.add(agregarBoton("Aceptar", () -> { accionAceptar.run(); }));
    }
}
