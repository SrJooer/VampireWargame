# Guía de Layouts en Swing — aplicada a Vampire Wargame

Escrita sobre tu código real (`PanelAbstracto`, `PanelJuego`, `GestorPaneles`).

---

## Parte 0 — El modelo mental que hay que tener antes de todo

En Swing **tú nunca decides dónde va un componente**. Lo decide el *layout manager* del
contenedor padre. Lo único que tú haces es:

1. Elegir el layout del contenedor (`panel.setLayout(...)`).
2. Darle al layout **pistas** sobre cómo quieres que trate a cada hijo.

Las pistas son de dos tipos:

| Tipo de pista | Cómo se da | Quién la respeta |
|---|---|---|
| **Tamaño deseado** del componente | `getPreferredSize()` / `setPreferredSize()` | depende del layout |
| **Reglas de colocación** | `GridBagConstraints` (solo en GridBagLayout) | GridBagLayout |

Y hay un tercer actor que la gente olvida: **el tamaño disponible**. Un layout reparte
el espacio que le da su propio padre. Si el padre no le da espacio, no hay
`setPreferredSize` que valga.

La cadena en tu proyecto es:

```
JFrame (1000x800, BorderLayout por defecto)
 └── panelPrincipal (CardLayout)          <- GestorPaneles
      └── PanelJuego (BorderLayout)       <- prepararContenido()
           └── contenidoPanel (GridBagLayout)
                └── menuPanel (GridBagLayout)
                     ├── interfazPanel
                     └── escenarioPanel
                          └── tablaPanel (GridLayout 6x6)
```

Cada nivel puede arruinar el nivel de abajo. Cuando algo "no se ve del tamaño que
puse", el error casi siempre está **uno o dos niveles más arriba**.

---

## Parte 1 — El sistema de tamaños

### 1.1 Los tres tamaños

Todo `JComponent` expone tres dimensiones:

```java
comp.getMinimumSize();     // "por debajo de esto me rompo"
comp.getPreferredSize();   // "este es mi tamaño ideal"
comp.getMaximumSize();     // "más grande que esto no tiene sentido"
```

Por defecto los calcula el componente solo (un `JButton` mide lo que mide su texto +
márgenes + fuente). Con `setPreferredSize(new Dimension(w, h))` **sobreescribes** ese
cálculo con un valor fijo.

### 1.2 Regla crítica: no todos los layouts respetan los tres

Esta tabla es la que resuelve el 80% de la confusión:

| Layout | ¿Respeta preferred? | ¿Respeta maximum? | Notas |
|---|---|---|---|
| `FlowLayout` | Sí, siempre | No lo usa | Nunca estira nada |
| `BorderLayout` | Parcial | No | NORTH/SOUTH respetan **alto**, ignoran ancho. WEST/EAST respetan **ancho**, ignoran alto. CENTER ignora todo y se come el resto |
| `GridLayout` | Solo para calcular el total | No | Todas las celdas quedan **idénticas**; el preferred individual se ignora |
| `BoxLayout` | Sí | **Sí** | Es el único que hace caso a `setMaximumSize` |
| `GridBagLayout` | Sí, como **piso mínimo** | No | Ver abajo |
| `null` (absoluto) | No | No | Tú pones `setBounds`. **No lo uses** |

Ahí ya tienes dos cosas de tu código explicadas:

- En `agregarBoton` pones `setPreferredSize` **y** `setMaximumSize`. El `maximum` solo
  te sirve en los paneles con `BoxLayout` (tu `agregarPanel(int n)`). En los paneles con
  GridBagLayout ese `setMaximumSize` no hace absolutamente nada.
- En `agregarCampoTexto` pones **solo** `setMaximumSize`. Eso funciona en BoxLayout,
  pero en GridBagLayout el campo va a salir con su preferred natural (ancho de 0
  columnas ≈ diminuto). Ahí querrías `new JTextField(15)` o `setPreferredSize`.

### 1.3 `setPreferredSize` vs. sobreescribir `getPreferredSize()`

```java
// Opción A: valor fijo, imperativa
tablaPanel.setPreferredSize(new Dimension(480, 480));

// Opción B: valor calculado, se recalcula solo cuando cambia el contexto
class PanelTablero extends JPanel {
    @Override public Dimension getPreferredSize() {
        Container p = getParent();
        if (p == null) return new Dimension(480, 480);
        int lado = Math.min(p.getWidth(), p.getHeight());
        return new Dimension(lado, lado);
    }
}
```

Usa **A** para tamaños que no cambian (barra lateral de 260 px). Usa **B** cuando el
tamaño depende de algo (mantener el tablero cuadrado al redimensionar la ventana).

Regla práctica: si te encuentras llamando a `setPreferredSize` en 36 casillas, estás
haciéndolo mal. Se lo pones **al contenedor**, y el `GridLayout` reparte.

### 1.4 Cómo se calcula el tamaño de un contenedor

El preferred de un panel = lo que su layout calcula a partir del preferred de sus hijos.
Es recursivo, de abajo hacia arriba. Por eso:

- Un `JPanel` vacío tiene preferred `(0, 0)` y **desaparece**. Tu `interfazPanel` está
  vacío ahora mismo; aunque le pusiste fondo azul, si no tuviera `weightx` no se vería.
- `frame.pack()` fija la ventana al preferred total del árbol. Tú usas
  `setSize(1000, 800)` en `GestorPaneles`, que es lo contrario: impones el tamaño de
  arriba y el árbol se acomoda. Para un juego está bien; solo tenlo consciente.

---

## Parte 2 — GridBagLayout a fondo

### 2.1 Cómo funciona realmente

GridBagLayout es una **tabla con celdas de tamaño variable**. El algoritmo, simplificado:

1. Lee las restricciones de todos los hijos y arma una rejilla virtual de filas/columnas.
2. Calcula el **ancho base** de cada columna = el mayor preferred width de los
   componentes de esa columna (+ sus insets). Igual para el alto de las filas.
3. Suma todo. Si la suma es **menor** que el espacio disponible, hay *espacio sobrante*.
4. Reparte el sobrante entre columnas y filas **según los pesos** (`weightx`/`weighty`).
5. Dentro de cada celda ya dimensionada, coloca el componente según `fill` y `anchor`.

Lo importante de esa secuencia: **el `preferredSize` decide el paso 2 (el piso), y el
`weight` decide el paso 4 (el reparto del extra).** No se contradicen — actúan en
momentos distintos. Un componente con `fill=BOTH` y `weightx=1` termina más grande que
su preferred, pero nunca más chico.

### 2.2 Los campos de GridBagConstraints, uno por uno

```java
GridBagConstraints gbc = new GridBagConstraints();
```

**`gridx`, `gridy`** — columna y fila donde empieza el componente. Base 0. El default es
`GridBagConstraints.RELATIVE` (= -1), que significa "a la derecha del anterior". Es
cómodo pero ilegible; **pon siempre coordenadas explícitas**, como ya haces.

**`gridwidth`, `gridheight`** — cuántas columnas/filas ocupa. Default `1`.

> ⚠️ **Trampa importante:** `GridBagConstraints.REMAINDER` vale **0** y
> `GridBagConstraints.RELATIVE` vale **-1**. Es decir, `gridwidth = 0` **no** significa
> "cero columnas", significa "ocupa hasta el final de la fila".
>
> En tu `PanelJuego` tienes: `escenarioPanel.add(prepararTabla(), agregarGbc(0, 0, 0, 0))`.
> Eso es `gridwidth=REMAINDER, gridheight=REMAINDER`. Funciona por accidente (solo hay un
> hijo), pero no es lo que querías escribir. Debería ser `agregarGbc(0, 0, 1, 1)`.

**`weightx`, `weighty`** — de `0.0` a `1.0` (o más; lo que importa es la proporción).
Responde: *"cuando sobre espacio, ¿qué porción de ese sobrante me toca?"*

Tres cosas que casi nadie te dice:

- El peso **se aplica a la columna/fila, no al componente**. Si dos componentes están en
  la misma columna con pesos distintos, gana el mayor.
- Si **todos** los pesos son `0.0` (el default), el sobrante no se reparte: la rejilla
  entera queda de su tamaño preferido y se **centra** en el contenedor, con márgenes
  vacíos alrededor. Ese es el clásico "mi panel se ve chiquito en medio de la nada".
- Si un componente **abarca varias columnas** (`gridwidth > 1`), GridBagLayout no sabe a
  cuál de esas columnas asignarle el peso, y el reparto se vuelve impredecible. La
  solución estándar es **poner el peso en filas/columnas que contengan componentes de
  una sola celda**, o meter componentes invisibles de referencia.

  > Esto te afecta directo: en `prepararMenu()` agregas `interfazPanel` con
  > `gridwidth=2` y `escenarioPanel` con `gridwidth=6`, ambos con `weightx=1.0`. Tú
  > esperas una proporción 2:6, pero **no la vas a obtener**: hay 8 columnas y el peso
  > queda repartido de forma que no controlas. Para 25%/75% real, usa 2 columnas con
  > `weightx = 0.25` y `weightx = 0.75`.

**`fill`** — qué hace el componente con el espacio de su celda si le queda grande.

| Valor | Efecto |
|---|---|
| `NONE` (default) | Se queda en su preferred size, dentro de la celda |
| `HORIZONTAL` | Estira el ancho hasta llenar la celda; el alto queda en preferred |
| `VERTICAL` | Al revés |
| `BOTH` | Llena la celda completa |

**`anchor`** — si `fill` no llena la celda, ¿dónde se pega el componente dentro de ella?
`CENTER` (default), `NORTH`, `NORTHEAST`, `EAST`, ..., y las variantes lógicas
`LINE_START` / `LINE_END` / `PAGE_START` (recomendadas: respetan idiomas RTL).

**`insets`** — `new Insets(arriba, izquierda, abajo, derecha)`. Espacio **por fuera** del
componente. Es tu herramienta de espaciado. Ojo: los insets se **restan** del espacio de
la celda, no se suman al panel.

**`ipadx`, `ipady`** — padding **interno**: agrandan el tamaño con el que el layout
considera al componente. En la práctica casi nunca se usan; prefiere `insets` o un
`EmptyBorder`.

### 2.3 La matriz fill × weight (memorízala)

Esta combinación es la fuente de casi todos los problemas:

| weight | fill | Resultado |
|---|---|---|
| `0` | `NONE` | Tamaño preferido, celda del tamaño justo. **Máximo control.** |
| `0` | `BOTH` | Tamaño preferido. `fill` no hace nada porque la celda no creció |
| `1` | `NONE` | La celda crece, el componente **no**: queda flotando centrado en una celda grande |
| `1` | `BOTH` | El componente crece con la ventana. **Ignora tu preferred hacia arriba** |

Traducción práctica:

- ¿Quieres que algo respete el tamaño que le pusiste? → `weight = 0` y/o `fill = NONE`.
- ¿Quieres que algo se estire con la ventana? → `weight > 0` **y** `fill` acorde.
- ¿Quieres que algo esté centrado y ocupe todo el hueco de fondo? → `weight = 1`,
  `fill = NONE`, `anchor = CENTER`.

### 2.4 Reutilizar el objeto GridBagConstraints: sí se puede

Muchos tutoriales dicen que hay que crear uno nuevo por componente. **No es cierto.**
`GridBagLayout.setConstraints()` guarda un **clon** internamente. Este código es válido y
es el idiomático:

```java
GridBagConstraints gbc = new GridBagConstraints();
gbc.fill = GridBagConstraints.BOTH;
gbc.insets = new Insets(4, 4, 4, 4);

gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
panel.add(izquierda, gbc);

gbc.gridx = 1; gbc.weightx = 0.75;
panel.add(derecha, gbc);
```

El riesgo real es otro: **arrastrar valores viejos**. Si en un `add` pusiste
`gridwidth = 3` y en el siguiente no lo reseteas, sigue valiendo 3. Por eso tu enfoque de
factory (`crearGbc`) es mejor: cada llamada devuelve un objeto limpio.

---

## Parte 3 — Diagnóstico de tu código actual

### 3.1 El problema de fondo en `crearGbc`

```java
public GridBagConstraints crearGbc(int x, int y) {
    ...
    gbc.fill = GridBagConstraints.BOTH;   // <-- default agresivo
    gbc.weightx = 1.0;                    // <-- default agresivo
    gbc.weighty = 1.0;                    // <-- default agresivo
    ...
}
```

Tu default es exactamente la esquina `weight=1 + fill=BOTH` de la tabla de arriba: **todo
se estira y ningún `setPreferredSize` se respeta**. Por eso sientes que "los preferred
size no hacen nada". Sí hacen — pero solo como mínimo, y como tu mínimo (400x400) es
menor que el espacio disponible (~1000x800), nunca lo notas.

Los defaults de Swing (`weight = 0`, `fill = NONE`) son conservadores a propósito: no
estirar nada salvo que lo pidas explícitamente. Te recomiendo invertir tus helpers:

```java
/** Base: no estira, no crece. Control total. */
protected GridBagConstraints gbc(int x, int y) {
    GridBagConstraints g = new GridBagConstraints();
    g.gridx = x;
    g.gridy = y;
    g.gridwidth = 1;
    g.gridheight = 1;
    g.fill = GridBagConstraints.NONE;
    g.anchor = GridBagConstraints.CENTER;
    g.weightx = 0;
    g.weighty = 0;
    g.insets = new Insets(5, 5, 5, 5);
    return g;
}

/** Ocupa varias celdas. */
protected GridBagConstraints gbc(int x, int y, int ancho, int alto) {
    GridBagConstraints g = gbc(x, y);
    g.gridwidth = ancho;
    g.gridheight = alto;
    return g;
}

/** Encadenables, para pedir el comportamiento explícitamente. */
protected GridBagConstraints estirar(GridBagConstraints g, double wx, double wy) {
    g.weightx = wx;
    g.weighty = wy;
    g.fill = GridBagConstraints.BOTH;
    return g;
}

protected GridBagConstraints margen(GridBagConstraints g, int t, int l, int b, int r) {
    g.insets = new Insets(t, l, b, r);
    return g;
}

protected GridBagConstraints anclar(GridBagConstraints g, int anchor) {
    g.anchor = anchor;
    return g;
}
```

Uso: `panel.add(comp, estirar(gbc(0, 0), 1, 1));` — se lee y se entiende sin adivinar.

### 3.2 Bugs concretos en `PanelJuego`

```java
// 1. gridwidth/gridheight = 0 significa REMAINDER, no 1
escenarioPanel.add(prepararTabla(), agregarGbc(0, 0, 0, 0));

// 2. Proporción 2:6 que no se cumple, por pesos sobre celdas spanned
menuPanel.add(interfazPanel,  agregarGbc(0, 0, 2, 1, 0));
menuPanel.add(escenarioPanel, agregarGbc(2, 0, 6, 1, 50));

// 3. Capa intermedia sin función: contenidoPanel tiene un solo hijo (menuPanel)
//    que a su vez es GridBagLayout. Uno de los dos sobra.
contenidoPanel.add(menuPanel, agregarGbc(0, 0, 1, 1));
```

Sobre el punto 3: capas de más no son un error grave, pero cada `JPanel` extra es una
oportunidad más de que un `weight` mal puesto rompa algo. Si `contenidoPanel` solo va a
contener a `menuPanel` con `fill=BOTH`, elimina uno de los dos.

### 3.3 Por qué el tablero no se ve cuadrado

`tablaPanel` tiene `setPreferredSize(400, 400)` pero está dentro de `escenarioPanel` con
`fill=BOTH` y `weight=1`. Resultado: `escenarioPanel` ocupa todo el ancho sobrante y
`tablaPanel`, con fill BOTH heredado del helper, se estira a un rectángulo. Para tenerlo
cuadrado y centrado:

```java
// escenarioPanel se estira todo lo que quiera (es el "fondo")
// pero el tablero adentro va con weight 0 y fill NONE -> respeta su preferred
escenarioPanel.add(prepararTabla(), gbc(0, 0));   // weight 0, fill NONE, anchor CENTER
```

Y si además quieres que crezca con la ventana **manteniendo la proporción 1:1**, usa la
Opción B de la sección 1.3 en una subclase del panel del tablero.

---

## Parte 4 — Elegir el layout correcto (no todo es GridBagLayout)

GridBagLayout es potente pero verboso. La estrategia profesional es **anidar layouts
simples** y reservar GridBag para donde de verdad hace falta.

| Necesito... | Usa |
|---|---|
| Rejilla de celdas iguales (tablero 6x6) | `GridLayout(6, 6)` |
| Zonas fijas: centro + laterales + barra abajo | `BorderLayout` |
| Fila o columna de botones | `BoxLayout` o `FlowLayout` |
| Formulario etiqueta-campo alineado | `GridBagLayout` |
| Proporciones exactas 25/75 que respondan al resize | `GridBagLayout` con weights |
| Cambiar entre pantallas completas | `CardLayout` (ya lo usas) |
| Centrar una sola cosa en el medio | `GridBagLayout` con un solo hijo sin constraints |

Ese último truco es el más útil de todos: **un panel con `GridBagLayout` y un único hijo
agregado sin restricciones centra perfectamente al hijo, horizontal y verticalmente.**

```java
JPanel centrador = new JPanel(new GridBagLayout());
centrador.add(algo);   // queda centrado, en su preferred size
```

Para tu pantalla de partida, la combinación que recomiendo:

- **`PanelJuego`**: `BorderLayout` (ya lo tienes por `prepararContenido`).
  - `NORTH`: barra de estado (turno, jugadores) → `BorderLayout` interno.
  - `CENTER`: `GridBagLayout` con dos columnas, 70% tablero / 30% panel lateral.
  - `SOUTH`: botones globales (Retirarse) → `FlowLayout`.
- **Tablero**: panel cuadrado + `GridLayout(6,6)`.
- **Panel lateral**: `GridBagLayout` vertical de 3 bloques: ruleta / info de pieza /
  historial.
- **Historial**: `JTextArea` (o `JList`) dentro de `JScrollPane`, con `weighty` alto para
  que se coma el sobrante vertical.

---

## Parte 5 — Plano concreto de la pantalla de partida

### 5.1 El esquema

```
+--------------------------------------------------------------+
|  BARRA SUPERIOR (NORTH)                                       |
|  Jugador1: Julio  |  TURNO: BLANCAS  |  Jugador2: Oponente    |
+--------------------------------------------------------------+
|                                        |                      |
|                                        |   RULETA             |
|         TABLERO 6x6                    |   [ imagen/label ]   |
|         (cuadrado, centrado)           |   [ Girar ]          |
|                                        |   Giros: 1/1         |
|                                        +----------------------+
|                                        |   PIEZA SELECCIONADA |
|                                        |   Tipo / ATK / HP /  |
|                                        |   Escudo             |
|                                        +----------------------+
|                                        |   HISTORIAL          |
|                                        |   [ scroll ]         |
|                                        |                      |
+--------------------------------------------------------------+
|  [ Retirarse ]                        (SOUTH)                 |
+--------------------------------------------------------------+
       weightx 0.7                            weightx 0.3
```

### 5.2 El armazón

> Los métodos `alClicEnCasilla`, `girarRuleta`, `confirmarRetiro`, `iconoDe` y las clases
> `Tablero` / `Pieza` que aparecen en los ejemplos son **marcadores**: todavía no existen
> en tu proyecto. Los escribes tú en la fase 2 del plan de la Parte 8.

```java
public class PanelJuego extends PanelAbstracto {

    private static final int LADO = 6;

    private JPanel barraSuperior;
    private JPanel tableroPanel;
    private JPanel lateralPanel;
    private JPanel barraInferior;

    private final JButton[][] casillas = new JButton[LADO][LADO];
    private JTextArea areaHistorial;
    private JLabel labelRuleta;
    private JLabel labelInfoPieza;

    @Override
    public void iniciarPanel() {
        prepararContenido();               // setLayout(BorderLayout) + contenidoPanel GridBag

        contenidoPanel.add(construirTablero(),
                estirar(gbc(0, 0), 0.7, 1.0));
        contenidoPanel.add(construirLateral(),
                estirar(gbc(1, 0), 0.3, 1.0));

        add(construirBarraSuperior(), BorderLayout.NORTH);
        add(contenidoPanel,           BorderLayout.CENTER);
        add(construirBarraInferior(), BorderLayout.SOUTH);
    }
}
```

Fíjate: la proporción 70/30 se logra con **dos columnas de una celda cada una** y pesos
`0.7` / `0.3`. Sin `gridwidth` raros. Esa es la forma correcta.

### 5.3 El tablero cuadrado

```java
private JPanel construirTablero() {
    // Contenedor que se estira; el tablero adentro se mantiene cuadrado y centrado.
    JPanel contenedor = agregarPanel(new GridBagLayout());
    contenedor.setOpaque(false);

    tableroPanel = new JPanel(new GridLayout(LADO, LADO, 1, 1)) {
        @Override public Dimension getPreferredSize() {
            Container p = getParent();
            if (p == null) return new Dimension(480, 480);
            Insets in = p.getInsets();
            int w = p.getWidth()  - in.left - in.right  - 40;
            int h = p.getHeight() - in.top  - in.bottom - 40;
            int lado = Math.max(240, Math.min(w, h));
            return new Dimension(lado, lado);
        }
    };
    tableroPanel.setBackground(Color.DARK_GRAY);
    tableroPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

    for (int fila = 0; fila < LADO; fila++) {
        for (int col = 0; col < LADO; col++) {
            JButton casilla = crearCasilla(fila, col);
            casillas[fila][col] = casilla;
            tableroPanel.add(casilla);      // GridLayout ignora el orden xy: es secuencial
        }
    }

    // weight 0 + fill NONE => respeta getPreferredSize() y se centra
    contenedor.add(tableroPanel, gbc(0, 0));
    return contenedor;
}

private JButton crearCasilla(int fila, int col) {
    JButton b = new JButton();
    boolean clara = (fila + col) % 2 == 0;
    b.setBackground(clara ? new Color(90, 90, 100) : new Color(35, 35, 45));
    b.setOpaque(true);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setMargin(new Insets(0, 0, 0, 0));   // que el icono no quede apretado
    // OJO: nada de setPreferredSize aquí. GridLayout reparte el tamaño del contenedor.
    b.addActionListener(e -> alClicEnCasilla(fila, col));
    return b;
}
```

Puntos clave:

- **Ninguna casilla tiene `setPreferredSize`.** `GridLayout` divide el espacio del
  contenedor entre 6x6 y punto. Ponerle preferred a las casillas solo afectaría al
  preferred total del tablero, que aquí lo estamos calculando nosotros.
- `GridLayout` coloca los hijos **en el orden en que los agregas**, izquierda→derecha,
  arriba→abajo. Guardar el arreglo `casillas[fila][col]` te da acceso por coordenada
  después.
- Las variables `fila` y `col` dentro del lambda deben ser *effectively final*; como son
  los parámetros del método, funciona.

### 5.4 El panel lateral

```java
private JPanel construirLateral() {
    lateralPanel = agregarPanel(new GridBagLayout());
    lateralPanel.setOpaque(false);

    // Fila 0: ruleta -> alto fijo, no crece
    lateralPanel.add(construirRuleta(),
            estirar(gbc(0, 0), 1.0, 0.0));

    // Fila 1: info de pieza -> alto fijo, no crece
    lateralPanel.add(construirInfoPieza(),
            estirar(gbc(0, 1), 1.0, 0.0));

    // Fila 2: historial -> se come TODO el sobrante vertical
    lateralPanel.add(construirHistorial(),
            estirar(gbc(0, 2), 1.0, 1.0));

    return lateralPanel;
}
```

Ese patrón — `weighty = 0` en los bloques fijos y `weighty = 1` en el que debe crecer —
es el idioma más común de GridBagLayout y resuelve la mayoría de los layouts verticales.

```java
private JPanel construirRuleta() {
    JPanel p = agregarPanel(new GridBagLayout());
    p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Ruleta"));

    labelRuleta = agregarLabel("—");
    labelRuleta.setFont(new Font("Arial", Font.BOLD, 22));
    labelRuleta.setPreferredSize(new Dimension(200, 120));  // reserva el hueco de la imagen

    p.add(labelRuleta,                       estirar(gbc(0, 0), 1.0, 1.0));
    p.add(agregarBoton("Girar", this::girarRuleta), gbc(0, 1));
    p.add(agregarLabel("Giros restantes: 1"),       gbc(0, 2));
    return p;
}

private JPanel construirInfoPieza() {
    JPanel p = agregarPanel(new GridBagLayout());
    p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Pieza seleccionada"));

    labelInfoPieza = agregarLabel("<html>Ninguna</html>");
    // anchor LINE_START + fill HORIZONTAL: el texto se alinea a la izquierda
    GridBagConstraints g = gbc(0, 0);
    g.fill = GridBagConstraints.HORIZONTAL;
    g.weightx = 1.0;
    g.anchor = GridBagConstraints.LINE_START;
    p.add(labelInfoPieza, g);
    return p;
}

private JPanel construirHistorial() {
    JPanel p = agregarPanel(new BorderLayout());   // BorderLayout: el CENTER se come todo
    p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Historial"));

    areaHistorial = agregarAreaTexto();
    areaHistorial.setEditable(false);

    JScrollPane scroll = new JScrollPane(areaHistorial);
    scroll.setPreferredSize(new Dimension(240, 150));   // solo un piso; BorderLayout.CENTER lo estira
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.setBorder(null);

    p.add(scroll, BorderLayout.CENTER);
    return p;
}
```

> Nota sobre tu `agregarScrollPanel()`: crea un `JScrollPane` **vacío**. Un scroll sin
> viewport no muestra nada. Cámbialo a `agregarScrollPanel(Component vista)` que haga
> `new JScrollPane(vista)`, o llama a `scroll.setViewportView(vista)` después.

### 5.5 Barras superior e inferior

```java
private JPanel construirBarraSuperior() {
    barraSuperior = agregarPanel(new BorderLayout());
    barraSuperior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    barraSuperior.add(agregarLabel(gestorUsuarios.getUsuarioActual().getNombre()),
            BorderLayout.WEST);
    barraSuperior.add(agregarLabel("TURNO: BLANCAS"), BorderLayout.CENTER);
    barraSuperior.add(agregarLabel(contricante.getNombre()), BorderLayout.EAST);
    return barraSuperior;
}

private JPanel construirBarraInferior() {
    barraInferior = agregarPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
    barraInferior.add(agregarBoton("Retirarse", this::confirmarRetiro));
    return barraInferior;
}
```

`BorderLayout` con WEST/CENTER/EAST es la forma más corta de tener "izquierda, centro,
derecha" — mucho menos código que GridBagLayout para el mismo resultado.

### 5.6 Actualizar la UI cuando cambia el estado

Los paneles se construyen una vez; después **solo modificas los componentes**:

```java
private void pintarTablero(Tablero tablero) {
    for (int f = 0; f < LADO; f++) {
        for (int c = 0; c < LADO; c++) {
            Pieza p = tablero.getPieza(f, c);
            casillas[f][c].setIcon(p == null ? null : iconoDe(p));
            casillas[f][c].setToolTipText(p == null ? null : p.getDescripcion());
        }
    }
}

private void registrar(String linea) {
    areaHistorial.append(linea + "\n");
    areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength()); // auto-scroll
}
```

Si **agregas o quitas** componentes en tiempo de ejecución (no solo cambiar texto/icono),
tienes que avisar al layout:

```java
panel.revalidate();   // recalcula el layout
panel.repaint();      // redibuja
```

Ese par siempre va junto, y siempre en ese orden. Ya lo haces bien en `GestorPaneles`.

---

## Parte 6 — Iconos que escalan con la casilla

Las piezas van a ser imágenes. Un `ImageIcon` **no** se reescala solo, así que la imagen
se va a ver cortada cuando la casilla cambie de tamaño. Dos opciones:

```java
// Opción simple: escalar una vez al tamaño de casilla esperado
private Icon escalar(Image img, int lado) {
    return new ImageIcon(img.getScaledInstance(lado, lado, Image.SCALE_SMOOTH));
}
```

```java
// Opción buena: dibujar la pieza en paintComponent, escala sola siempre
class Casilla extends JPanel {
    private Image imagen;

    void setImagen(Image img) { this.imagen = img; repaint(); }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen == null) return;
        int lado = Math.min(getWidth(), getHeight()) - 8;
        int x = (getWidth()  - lado) / 2;
        int y = (getHeight() - lado) / 2;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(imagen, x, y, lado, lado, this);
        g2.dispose();
    }
}
```

Reglas de `paintComponent`:

1. Siempre `super.paintComponent(g)` primero (pinta el fondo).
2. Nunca llames a `paintComponent` tú; llama a `repaint()`.
3. Si modificas el `Graphics` (color, transform, hints), usa `g.create()` y `dispose()`.
4. No hagas lógica de juego ahí dentro. Solo dibujar.

Si te vas por `Casilla extends JPanel` en lugar de `JButton`, pierdes el
`ActionListener`: agrégale un `MouseListener` (`mouseClicked`).

---

## Parte 7 — Depuración de layouts

Cuando algo no se ve como esperas, en este orden:

**1. Pinta bordes de colores.** Es el truco #1 y resuelve casi todo:

```java
panelA.setBorder(BorderFactory.createLineBorder(Color.RED));
panelB.setBorder(BorderFactory.createLineBorder(Color.GREEN));
panelC.setBorder(BorderFactory.createLineBorder(Color.CYAN));
```

Así ves de inmediato **quién se está comiendo el espacio**.

**2. Imprime tamaños reales después de mostrar la ventana:**

```java
SwingUtilities.invokeLater(() ->
    System.out.println(panel.getSize() + " pref=" + panel.getPreferredSize()));
```

Si `getSize()` es `0x0`, el panel no fue dispuesto todavía o su padre no le dio espacio.

**3. Checklist de síntomas:**

| Síntoma | Causa típica |
|---|---|
| Todo se apretuja en el centro, con márgenes vacíos | Ningún componente tiene `weight > 0` |
| Un panel no se ve | Está vacío (preferred 0x0), o `setOpaque(false)` sin hijos |
| Mi `setPreferredSize` no se respeta | `fill=BOTH` + `weight>0`, o está en `BorderLayout.CENTER` |
| El componente flota en medio de una celda enorme | `weight>0` pero `fill=NONE` |
| Un panel se come toda la ventana | Es el `CENTER` de un `BorderLayout`, o tiene el único `weight` |
| Agregué algo y no aparece | Falta `revalidate()` + `repaint()` |
| Los colores no se aplican | Falta `setOpaque(true)` (los `JPanel` con `setOpaque(false)` no pintan fondo) |
| El texto largo estira el panel entero | Usa `<html>...</html>` en el `JLabel`, o `JTextArea` con `setLineWrap(true)` |

**4. Un `JPanel` con `setOpaque(false)` es transparente**: se ve el fondo del padre. Tu
`agregarPanel(LayoutManager)` lo hace por defecto, y luego en `PanelJuego` haces
`setOpaque(true)` manualmente. Es correcto, solo hay que recordarlo.

---

## Parte 8 — Orden recomendado de trabajo

Antes de escribir lógica de juego, deja la UI parada:

1. **Maqueta muerta.** Construye `PanelJuego` completo con datos falsos y bordes de
   colores. Sin lógica, sin piezas reales. Ajusta pesos hasta que se vea bien al
   redimensionar la ventana.
2. **Modelo separado.** `Pieza` (abstracta) → `Vampiro`, `HombreLobo`, `Necromante`,
   `Zombie`; `Tablero` con la matriz. Sin una sola línea de Swing dentro del modelo.
   Esto te cubre el requisito de herencia/polimorfismo/clase abstracta del enunciado.
3. **Puente.** Un método `pintarTablero(Tablero)` que traduce modelo → UI. La UI **nunca**
   guarda estado del juego; solo lo refleja.
4. **Interacción.** Clic en casilla → selección origen → destino → validación → acción.
5. **Ruleta, historial, fin de partida.**

Ese orden importa: si mezclas el estado del juego dentro de los componentes Swing, el
requisito 7 del enunciado (main sin lógica) y la depuración se te complican mucho.

---

## Resumen en 10 líneas

1. El layout del padre manda; `setPreferredSize` es una sugerencia.
2. `BorderLayout.CENTER` y `weight>0 + fill=BOTH` **ignoran** el preferred hacia arriba.
3. `setMaximumSize` solo lo respeta `BoxLayout`.
4. En GridBagLayout: `weight` reparte el **sobrante**; el preferred es el **piso**.
5. `weight=0 + fill=NONE` = control total del tamaño.
6. `weight=1 + fill=NONE` = flotando en el centro de una celda grande.
7. `gridwidth = 0` significa REMAINDER, no cero.
8. No pongas `weight` en componentes que abarcan varias columnas.
9. Panel con `GridBagLayout` + un solo hijo = centrado perfecto.
10. Bordes de colores para depurar; `revalidate()` + `repaint()` al modificar la jerarquía.
