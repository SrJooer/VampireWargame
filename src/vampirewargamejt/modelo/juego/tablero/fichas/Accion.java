package vampirewargamejt.modelo.juego.tablero.fichas;

public enum Accion {
    MOVER("Mover aquí"),
    ATACAR("Ataque normal"),
    ABSORBER("Absorber sangre"),
    LANZA("Lanzar la lanza"),
    ATAQUE_ZOMBIE("Atacar con el Zombie"),
    INVOCAR("Invocar un Zombie");

    private final String etiqueta;

    Accion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
