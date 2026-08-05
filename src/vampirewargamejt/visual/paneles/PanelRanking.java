package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;

import javax.swing.*;
import java.awt.*;

public class PanelRanking extends PanelAbstracto {

    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JPanel rankingPanel;

    public PanelRanking(GestorPaneles gestorPaneles) {
        super(gestorPaneles);
    }

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararBotones();
        prepararRankingPanel();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel, BorderLayout.CENTER);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(agregarTitulo("Ranking"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(rankingPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararRankingPanel() {

        int numJugadores = 10;

        rankingPanel = agregarPanel(new GridLayout(numJugadores, 2, 12, 12));
        String[] UsuariosOrdenados = GestorUsuarios.getUsuariosOrdenados(numJugadores);

        for (int i = 0; i < numJugadores; i++) {
            rankingPanel.add(agregarLabel((i + 1) + " .- "));
            rankingPanel.add(agregarLabel(UsuariosOrdenados[i]));
        }
        rankingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelReportes(gestorPaneles))));
    }


}
