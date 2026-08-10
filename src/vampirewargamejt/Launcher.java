package vampirewargamejt;

import vampirewargamejt.modelo.usuarios.GestorUsuarios;
import vampirewargamejt.visual.GestorPaneles;
import vampirewargamejt.visual.paneles.PanelSelector;

public class Launcher {

    public Launcher() {
        GestorPaneles gestorPaneles = GestorPaneles.getInstance();
        gestorPaneles.iniciarPanelPrincipal();

        // TODO: JUEGO RAPIDO POR TESTEO
        GestorUsuarios gestorUsuarios = GestorUsuarios.getInstance();
        gestorUsuarios.registrarUsuario("Julio", "12345");
        gestorUsuarios.registrarUsuario("Keren", "12345");
        gestorUsuarios.iniciarSesion("Julio", "12345");
        gestorPaneles.mostrarPanel(new PanelSelector());
    }
}
