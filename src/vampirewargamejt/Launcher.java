package vampirewargamejt;


import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;

public class Launcher extends JFrame {

    private final int ANCHO = 1280;
    private final int ALTURA = 720;

    private GestorPaneles gestorPaneles;

    public Launcher() {
        super("Vampire Wargame");
        iniciarVentana();
        iniciarGestorPaneles();
        setVisible(true);
    }

    private void iniciarVentana() {
        setSize(ANCHO, ALTURA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void iniciarGestorPaneles() {
        gestorPaneles = new GestorPaneles(this);
    }

    public GestorPaneles getGestorPaneles() { return gestorPaneles; }
}
