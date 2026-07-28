package vampirewargamejt.visual.paneles;

import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelPreJuego extends PanelAbstracto {


    public PanelPreJuego(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
        iniciarPanel();
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        JPanel contenido = agregarPanel(1);
        contenido.add(agregarLabel("Panel de pre-juego"));
        add(contenido, BorderLayout.CENTER);
    }
}
