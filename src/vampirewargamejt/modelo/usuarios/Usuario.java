package vampirewargamejt.modelo.usuarios;

import java.util.ArrayList;
import java.util.Calendar;

public class Usuario {

    private final String nombre;
    private String clave;
    private final Calendar fechaCreacion = Calendar.getInstance();
    private final ArrayList<String> historial = new ArrayList<>();

    private int puntos;
    private boolean activo;

    private int ganadas;
    private int perdidas;

    public Usuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
        this.puntos = 0;
        this.ganadas = 0;
        this.perdidas = 0;
        this.activo = true;
    }

    public void desactivar() { activo = false; }
    public void sumarPuntos(int puntos) { this.puntos += puntos; }
    public void agregarHistorial(String mensaje) {
        historial.add(mensaje.concat(" - ").concat(Calendar.getInstance().getTime().toString()));
    }

    public void setClave(String nuevaClave) { this.clave = nuevaClave; }

    public String getNombre() { return nombre; }
    public String getClave() { return clave; }
    public int getPuntos() { return puntos; }
    public boolean isActivo() { return activo; }
    public Calendar getFechaCreacion() { return fechaCreacion; }
    public ArrayList<String> getHistorial() { return historial; }
    public int getGanadas() { return ganadas; }
    public int getPerdidas() { return perdidas; }
}
