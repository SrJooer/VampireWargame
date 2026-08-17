package vampirewargamejt.visual.componentes;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

public final class CargadorAssets {
    private static final HashMap<String, Image> CACHE = new HashMap<>();
    private static final HashMap<String, ImageIcon> ICONOS = new HashMap<>();

    private CargadorAssets() { }

    public static ImageIcon icono(String ruta, int lado) {
        if (ruta == null || ruta.isEmpty() || lado <= 0) { return null; }

        String clave = ruta + "@" + lado;
        if (ICONOS.containsKey(clave)) { return ICONOS.get(clave); }

        Image original = cargar(ruta);
        ImageIcon icono = original == null ? null : new ImageIcon(escalar(original, lado));

        ICONOS.put(clave, icono);
        return icono;
    }

    private static BufferedImage escalar(Image original, int lado) {
        BufferedImage lienzo = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = lienzo.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(original, 0, 0, lado, lado, null);
        g.dispose();
        return lienzo;
    }

    public static Image cargar(String ruta) {
        if (ruta == null || ruta.isEmpty()) { return null; }
        if (CACHE.containsKey(ruta)) { return CACHE.get(ruta); }

        Image imagen = null;
        try {
            URL url = CargadorAssets.class.getResource(ruta);
            if (url != null) {
                imagen = new ImageIcon(url).getImage();
            } else {
                System.err.println("[assets] no se encontro: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("[assets] error al cargar " + ruta + ": " + e.getMessage());
        }

        CACHE.put(ruta, imagen);
        return imagen;
    }
}
