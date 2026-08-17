package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.Usuario;

import javax.swing.*;
import java.awt.*;

public class PanelRanking extends PanelAbstracto {
    private JPanel menuPanel;
    private JPanel botonesPanel;
    private JPanel rankingPanel;

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
        Usuario[] ranking = gestorUsuarios.getRanking();

        rankingPanel = agregarPanel(new GridLayout(0, 3, 32, 10));

        rankingPanel.add(agregarCabecera("Posición"));
        rankingPanel.add(agregarCabecera("Usuario"));
        rankingPanel.add(agregarCabecera("Puntos"));

        for (int i = 0; i < ranking.length; i++) {
            rankingPanel.add(agregarLabel(String.valueOf(i + 1)));
            rankingPanel.add(agregarLabel(ranking[i].getNombre()));
            rankingPanel.add(agregarLabel(String.valueOf(ranking[i].getPuntos())));
        }

        rankingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JLabel agregarCabecera(String texto) {
        JLabel etiqueta = agregarLabel(texto);
        etiqueta.setForeground(new Color(232, 169, 46));
        return etiqueta;
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 1, 0, 12));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelReportes())));
    }
}
