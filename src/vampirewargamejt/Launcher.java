package vampirewargamejt;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;
import vampirewargamejt.visual.paneles.PanelSelector;

public class Launcher {
    public Launcher() {
        GestorPaneles gestorPaneles = GestorPaneles.getInstance();
        gestorPaneles.iniciarPanelPrincipal();
        testMode(true);
    }

    private void testMode(boolean modoTest) {
        if (modoTest) {
            GestorUsuarios.getInstance().registrarUsuario("julio", "12345");
            GestorUsuarios.getInstance().registrarUsuario("keren", "12345");
            GestorUsuarios.getInstance().iniciarSesion("julio", "12345");
            GestorPaneles.getInstance().mostrarPanel(new PanelSelector());
        }
    }
}
