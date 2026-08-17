package vampirewargamejt.visual.paneles;

import vampirewargamejt.modelo.usuarios.Usuario;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PanelMiInformacion extends PanelAbstracto {
    private JPanel menuPanel;
    private JPanel tablaPanel;
    private JPanel botonesPanel;

    @Override
    public void iniciarPanel() {
        prepararContenido();
        prepararTabla();
        prepararBotones();
        prepararMenu();
        contenidoPanel.add(menuPanel);
        add(contenidoPanel);
    }

    private void prepararMenu() {
        menuPanel = agregarPanel(1);
        menuPanel.add(agregarTitulo("Mi Información"));
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(tablaPanel);
        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(botonesPanel);
    }

    private void prepararTabla() {
        tablaPanel = agregarPanel(new GridLayout(6, 2, 12, 12));
        tablaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Usuario usuario = gestorUsuarios.getUsuarioActual();
        if (usuario == null) {
            tablaPanel.add(agregarLabel("No hay ninguna sesión activa."));
            return;
        }

        agregarFila("Nombre: ", usuario.getNombre());
        agregarFila("Contraseña: ", ocultar(usuario.getClave()));
        agregarFila("Miembro desde: ", formatearFecha(usuario.getFechaCreacion()));
        agregarFila("Puntaje: ", String.valueOf(usuario.getPuntos()));
        agregarFila("Partidas Ganadas: ", String.valueOf(usuario.getGanadas()));
        agregarFila("Partidas Perdidas: ", String.valueOf(usuario.getPerdidas()));
    }

    private void agregarFila(String titulo, String valor) {
        tablaPanel.add(agregarLabel(titulo));
        tablaPanel.add(agregarLabel(valor));
    }

    private String ocultar(String clave) {
        return clave == null ? "" : "•".repeat(clave.length());
    }

    private String formatearFecha(Calendar fecha) {
        if (fecha == null) { return "-"; }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha.getTime());
    }

    private void prepararBotones() {
        botonesPanel = agregarPanel(new GridLayout(1, 2, 12, 0));
        botonesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonesPanel.add(agregarBoton("Volver", () -> gestorPaneles.mostrarPanel(new PanelMiCuenta())));
    }
}
