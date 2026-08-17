package vampirewargamejt;

import vampirewargamejt.modelo.excepciones.VampireWargameException;
import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;
import vampirewargamejt.visual.paneles.PanelInicio;
import vampirewargamejt.visual.paneles.PanelSelector;

import javax.swing.JOptionPane;

public class Launcher {
    public Launcher() {
        instalarRedDeSeguridad();
        GestorPaneles gestorPaneles = GestorPaneles.getInstance();
        gestorPaneles.iniciarPanelPrincipal();
        testMode(false);
    }

    private void instalarRedDeSeguridad() {
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> reportar(error));
    }

    private void reportar(Throwable error) {
        String detalle = error.getMessage() == null
                ? error.getClass().getSimpleName()
                : error.getMessage();

        JOptionPane.showMessageDialog(null,
                "Ocurrió un problema inesperado, pero el programa sigue abierto:\n" + detalle,
                "Vampire Wargame", JOptionPane.ERROR_MESSAGE);
    }

    private void testMode(boolean modoTest) {
        if (!modoTest) { return; }
        try {
            GestorUsuarios.getInstance().registrarUsuario("julio", "12345");
            GestorUsuarios.getInstance().registrarUsuario("keren", "12345");
            GestorUsuarios.getInstance().iniciarSesion("julio", "12345");
            GestorPaneles.getInstance().mostrarPanel(new PanelSelector());
        } catch (VampireWargameException e) {
            GestorPaneles.getInstance().mostrarPanel(new PanelInicio());
        }
    }
}
