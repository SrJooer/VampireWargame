"""
Parte la lamina 2x2 generada por ChatGPT en los 4 PNG que usa el juego.

Uso:
    python recortar_fichas.py lamina.png negro
    python recortar_fichas.py lamina.png blanco

Orden de los cuadrantes, el mismo de la Figura 2 del enunciado:

    arriba izquierda  ->  lobo        arriba derecha  ->  vampiro
    abajo  izquierda  ->  muerte      abajo  derecha  ->  zombie

Los nombres de salida son los que construye TipoFicha.getUrlAsset(),
asi que no se pueden cambiar.
"""

import sys
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    sys.exit("Falta Pillow. Instalalo con:  pip install pillow")

CUADRANTES = ["lobo", "vampiro", "muerte", "zombie"]
LADO_FINAL = 512
MARGEN = 8


def recortar_sobrante(imagen):
    """Deja el medallon pegado a los bordes, quitando el alfa vacio de alrededor."""
    if imagen.mode != "RGBA":
        return imagen
    caja = imagen.getbbox()
    return imagen.crop(caja) if caja else imagen


def cuadrar(imagen, lado, margen):
    """Mete el medallon centrado en un lienzo cuadrado transparente."""
    disponible = lado - margen * 2
    copia = imagen.copy()
    copia.thumbnail((disponible, disponible), Image.LANCZOS)

    lienzo = Image.new("RGBA", (lado, lado), (0, 0, 0, 0))
    lienzo.paste(copia, ((lado - copia.width) // 2, (lado - copia.height) // 2), copia)
    return lienzo


def main():
    if len(sys.argv) != 3 or sys.argv[2] not in ("negro", "blanco"):
        sys.exit("Uso:  python recortar_fichas.py <lamina.png> <negro|blanco>")

    origen = Path(sys.argv[1])
    color = sys.argv[2]

    if not origen.exists():
        sys.exit("No encuentro el archivo: " + str(origen))

    destino = Path(__file__).resolve().parent.parent / "src" / "assets"
    destino.mkdir(parents=True, exist_ok=True)

    lamina = Image.open(origen).convert("RGBA")
    mitad_ancho = lamina.width // 2
    mitad_alto = lamina.height // 2

    print("lamina: %dx%d  ->  cuadrantes de %dx%d"
          % (lamina.width, lamina.height, mitad_ancho, mitad_alto))

    for indice, pieza in enumerate(CUADRANTES):
        columna = indice % 2
        fila = indice // 2
        caja = (columna * mitad_ancho, fila * mitad_alto,
                (columna + 1) * mitad_ancho, (fila + 1) * mitad_alto)

        trozo = lamina.crop(caja)
        recortado = recortar_sobrante(trozo)

        if recortado.width < 32 or recortado.height < 32:
            print("  AVISO: el cuadrante de %s salio casi vacio, revisa la lamina" % pieza)

        final = cuadrar(recortado, LADO_FINAL, MARGEN)
        ruta = destino / ("%s_%s.png" % (pieza, color))
        final.save(ruta)

        print("  %-16s %4dx%-4d  ->  %s" % (pieza, recortado.width, recortado.height, ruta.name))

    print("\nListo. Los 4 archivos estan en:", destino)


if __name__ == "__main__":
    main()
