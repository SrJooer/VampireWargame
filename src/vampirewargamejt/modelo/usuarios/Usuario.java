package vampirewargamejt.modelo.usuarios;

import java.time.LocalDateTime;

public class Usuario {

    private final String nombre;
    private String clave;
    private final LocalDateTime fechaCreacion = LocalDateTime.now();

    private int puntos;
    private boolean activo;

    public Usuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
        this.puntos = 0;
        this.activo = true;
    }

    public void desactivar() { activo = false; }
    public void sumarPuntos(int puntos) { this.puntos += puntos; }

    public void setClave(String nuevaClave) { this.clave = nuevaClave; }

    public String getNombre() { return nombre; }
    public String getClave() { return clave; }
    public int getPuntos() { return puntos; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
