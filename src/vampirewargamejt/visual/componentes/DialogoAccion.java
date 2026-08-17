package vampirewargamejt.visual.componentes;

import vampirewargamejt.modelo.juego.tablero.fichas.Accion;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public final class DialogoAccion {
    private static final Color NEGRO = new Color(15, 15, 15);
    private static final Color BLANCO = Color.WHITE;

    private DialogoAccion() {
    }

    public static Accion preguntar(Component padre, String titulo, String descripcion,
                                   Accion[] acciones) {
        if (acciones == null || acciones.length == 0) {
            return null;
        }
        if (acciones.length == 1) {
            return acciones[0];
        }

        aplicarEstetica();

        String[] etiquetas = new String[acciones.length];
        for (int i = 0; i < acciones.length; i++) {
            etiquetas[i] = acciones[i].getEtiqueta();
        }

        int elegida = JOptionPane.showOptionDialog(padre, crearMensaje(descripcion), titulo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                etiquetas, etiquetas[0]);

        if (elegida < 0 || elegida >= acciones.length) {
            return null;
        }
        return acciones[elegida];
    }

    private static JPanel crearMensaje(String descripcion) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(NEGRO);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 10, 12, 10));

        JLabel texto = new JLabel(descripcion);
        texto.setForeground(BLANCO);
        texto.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(texto, BorderLayout.CENTER);

        return panel;
    }

    private static void aplicarEstetica() {
        UIManager.put("OptionPane.background", NEGRO);
        UIManager.put("OptionPane.messageForeground", BLANCO);
        UIManager.put("Panel.background", NEGRO);
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 15));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 14));
        UIManager.put("Button.background", BLANCO);
        UIManager.put("Button.foreground", NEGRO);
        UIManager.put("Button.select", new Color(90, 90, 90));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
    }
}
