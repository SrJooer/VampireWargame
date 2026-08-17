package vampirewargamejt.modelo.juego.tablero.fichas;

public final class TipoFicha {
    public static final TipoFicha VAMPIRO = new TipoFicha("Vampiro", 4, 3, 5, 1);
    public static final TipoFicha MUERTE  = new TipoFicha("Muerte", 3, 4, 1, 1);
    public static final TipoFicha LOBO    = new TipoFicha("Lobo", 5, 5, 2, 2);
    public static final TipoFicha ZOMBIE  = new TipoFicha("Zombie", 1, 1, 0, 0);

    public final String nombre;
    public final int vida;
    public final int ataque;
    public final int escudo;
    public final int alcance;

    private TipoFicha(String nombre, int vida, int ataque, int escudo, int alcance) {
        this.nombre = nombre;
        this.vida = vida;
        this.ataque = ataque;
        this.escudo = escudo;
        this.alcance = alcance;
    }

    public String getUrlAsset(boolean delJugador) {
        return "/assets/" + nombre.toLowerCase() + (delJugador ? "_blanco" : "_negro") + ".png";
    }
}
