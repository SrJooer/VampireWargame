package vampirewargamejt.modelo.juego.tablero;

import vampirewargamejt.modelo.juego.tablero.fichas.Ficha;

public class Celda {
    private int fila;
    private int columna;
    private Ficha ficha;

    private String urlAsset;

    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public void setFicha(Ficha ficha) { this.ficha = ficha; }
    public void removeFicha() { this.ficha = null;}

    public Ficha getFicha() { return ficha; }
    public boolean tieneFicha() { return ficha != null; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    public String getUrlAsset() { return urlAsset; }
    public void setUrlAsset(String urlAsset) { this.urlAsset = urlAsset; }
}
