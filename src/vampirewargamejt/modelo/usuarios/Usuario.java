package vampirewargamejt.modelo.usuarios;

public class Usuario {

    private final String nombre;
    private final String clave;

    private int puntos;
    private boolean activo;

    public Usuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
        this.puntos = 0;
        this.activo = false;
    }

    public void activar() { activo = true; }
    public void desactivar() { activo = false; }
    public void sumarPuntos(int puntos) { this.puntos += puntos; }

    public String getNombre() { return nombre; }
    public String getClave() { return clave; }
    public int getPuntos() { return puntos; }
    public boolean isActivo() { return activo; }
}
