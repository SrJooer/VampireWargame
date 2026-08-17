# Cómo funciona una partida

### Guía de lectura de `Juego.java` y `PanelJuego.java`

Este documento no explica Java ni Swing. Explica **por qué tu código está escrito así**:
qué problema resuelve cada método, por qué existe, y cómo encaja con los demás. La idea es
que cuando abras los archivos ya sepas lo que vas a encontrar.

---

## Índice

1. [La idea de fondo: dos mundos separados](#1-la-idea-de-fondo-dos-mundos-separados)
2. [El reparto de responsabilidades](#2-el-reparto-de-responsabilidades)
3. [Juego.java — el árbitro](#3-juegojava--el-árbitro)
4. [PanelJuego.java — la mesa](#4-paneljuegojava--la-mesa)
5. [El recorrido completo de una jugada](#5-el-recorrido-completo-de-una-jugada)
6. [Las cuatro decisiones de diseño que explican todo](#6-las-cuatro-decisiones-de-diseño-que-explican-todo)
7. [Lo que falta y dónde va](#7-lo-que-falta-y-dónde-va)

---

## 1. La idea de fondo: dos mundos separados

Imagina una partida de ajedrez por correspondencia. Hay dos cosas completamente distintas:

- **Las reglas.** Quién mueve, a dónde puede llegar cada pieza, cuándo se acaba. Existen
  aunque no haya tablero delante: podrías jugar de memoria.
- **El tablero físico.** Madera, piezas, un cronómetro. Es *una forma* de mirar la partida,
  pero no es la partida.

Tu código está partido exactamente por ahí:

- **`Juego`** son las reglas. No sabe que existe una ventana. No imprime nada. No lee el
  teclado. Le preguntas cosas y te contesta.
- **`PanelJuego`** es el tablero físico. No sabe ninguna regla. No decide si un movimiento
  es legal. Lo único que hace es **preguntar y dibujar**.

Esta separación no es un capricho académico. Es la razón por la que:

- Pudiste probar la lógica por consola antes de tener interfaz.
- Puedes cambiar los colores, las imágenes o el layout sin tocar una sola regla.
- Cuando algo falla, sabes en qué archivo mirar: si una ficha se mueve donde no debe, es
  `Juego`; si se mueve bien pero se ve mal, es `PanelJuego`.

Hay una regla de oro que tu código cumple y conviene no romper:

> **La interfaz nunca guarda estado del juego.** No hay una copia de dónde está cada ficha
> dentro de `PanelJuego`. Cada vez que hay que dibujar, se le vuelve a preguntar a `Juego`.

La única excepción es `origenSeleccionado`, y hay una buena razón para ello que veremos
más adelante.

---

## 2. El reparto de responsabilidades

Antes de entrar en los métodos, conviene tener claro el reparto completo, porque hay más
clases metidas en esto:

| Clase | De qué se ocupa | Qué NO hace |
|---|---|---|
| `Ficha` | Sus puntos de vida, ataque, escudo, y a qué distancias alcanza | No sabe dónde está |
| `Celda` | Su fila, su columna, y qué ficha hay encima | No sabe de reglas |
| `Tablero` | La matriz de 36 celdas y las búsquedas sobre ella | No sabe de turnos |
| `Juego` | Turnos, ruleta, validación, combate, historial, fin de partida | No dibuja ni lee entrada |
| `CasillaBoton` | Dibujar una casilla y avisar cuando la pulsan | No sabe qué significa lo que dibuja |
| `CargadorAssets` | Leer los PNG y cachearlos | Nada más |
| `PanelJuego` | Montar la pantalla y traducir modelo ↔ pantalla | No decide nada del juego |

Fíjate en un detalle importante: **`Ficha` no sabe en qué casilla está**. Solo conoce sus
desplazamientos posibles. Por eso `dentroRango` recibe *dos* celdas y resta una de otra —
la ficha aporta el "hasta dónde llego", y el tablero aporta el "dónde estoy".

---

## 3. `Juego.java` — el árbitro

### 3.1 Qué guarda y por qué

Los campos de `Juego` se dividen en cuatro grupos, y entenderlos hace que el resto del
archivo se lea solo:

**Los jugadores y de quién es el turno.**

```java
private Usuario jugador1;
private Usuario jugador2;
private boolean jugador1Turno;
```

Aquí hay una convención silenciosa que atraviesa todo el proyecto: **`true` significa
jugador 1**. Cuando `Ficha.delJugador()` devuelve `true`, esa ficha es de las blancas.
Cuando `jugador1Turno` es `true`, le toca a las blancas. Por eso la comprobación de
propiedad es tan corta:

```java
ficha.delJugador() != jugador1Turno   // "esta ficha no es del que juega ahora"
```

Es elegante, pero tiene un coste: si algún día metes un tercer jugador o un modo
espectador, ese `boolean` se te queda corto y habrá que cambiarlo en muchos sitios. Para
este proyecto está bien.

**El tablero y el candado.**

```java
private Tablero tablero;
private boolean interactuable;
```

`interactuable` es un **candado de entrada**. Sirve para que, cuando más adelante añadas
una animación de movimiento o un diálogo de "¿ataque normal o especial?", puedas
bloquear los clics mientras tanto: pones `setInteractuable(false)`, haces lo tuyo, y lo
vuelves a poner en `true`. Ahora mismo siempre está en `true`, así que no hace nada —
pero el gancho ya existe y `jugar()` lo respeta.

**La ruleta.**

```java
private TipoFicha[] fichas = { VAMPIRO, LOBO, MUERTE };
private TipoFicha fichaPorMover;
```

`fichas` es el conjunto de resultados posibles. El Zombie **no está** a propósito: el
enunciado dice que su icono no forma parte de la ruleta. `fichaPorMover` es el resultado
del giro actual, es decir, la restricción que pesa sobre este turno.

**El final y la memoria.**

```java
private boolean juegoTerminado;
private Usuario ganador;
private final List<String> historial = new ArrayList<>();
private String ultimoMensaje = "";
```

Los dos últimos son la parte que más gente confunde, así que merece su propio apartado.

### 3.2 Los dos canales de mensajes

Cuando quitamos la consola, había que decidir dónde iban a parar todos esos
`System.out.println`. La respuesta no es "todos a una lista", porque **no todos los
mensajes son iguales**:

- *"julio movió su Lobo de 5:0 a 4:0"* es un **hecho de la partida**. Pasó. Debe quedar
  registrado para siempre.
- *"Esa ficha no es tuya"* es un **error del usuario**. No pasó nada en la partida; el
  jugador simplemente pulsó donde no debía. Si esto fuera al historial, después de diez
  clics torpes el registro sería ilegible.

De ahí los dos métodos del final del archivo:

```java
private void registrar(String texto) {   // hechos
    historial.add(texto);
    ultimoMensaje = texto;
}

private void avisar(String texto) {      // errores
    ultimoMensaje = texto;
}
```

Los dos actualizan `ultimoMensaje`, porque **ambos hay que mostrarlos en pantalla**. Solo
uno de los dos toca el historial. Cuando leas `ejecutar()`, verás que todas las
comprobaciones que devuelven `false` usan `avisar`, y todas las ramas que devuelven `true`
usan `registrar`. Es un patrón muy regular: **si la jugada no ocurrió, no se registra**.

Los dos helpers de abajo, `pos()` y `nombre()`, solo existen para que los mensajes no se
conviertan en concatenaciones ilegibles:

```java
private String pos(Celda celda) { return celda.getFila() + ":" + celda.getColumna(); }
private String nombre(Usuario usuario) { return usuario == null ? "Jugador" : usuario.getNombre(); }
```

`nombre()` además te protege de un `NullPointerException` si la partida arranca sin
oponente configurado. Un detalle pequeño que evita que el programa muera por sorpresa.

### 3.3 El constructor: dejar la mesa lista

```java
public Juego() {
    jugador1 = gestorUsuarios.getUsuarioActual();
    jugador2 = gestorUsuarios.getUsuarioContricante();
    tablero = new Tablero();
    jugador1Turno = true;      // siempre empiezan las blancas
    interactuable = true;
    juegoTerminado = false;

    registrar("Comienza la partida: ...");
    girarRuletaValida();
}
```

Lo interesante es la última línea. El constructor **no se limita a inicializar**: gira la
ruleta antes de terminar. ¿Por qué?

Porque si no lo hiciera, `fichaPorMover` quedaría en `null` y el primer `redibujar()` de
la interfaz reventaría al hacer `juego.getFichaPorMover().nombre`. Girar aquí garantiza
una invariante muy útil:

> **Desde el momento en que el objeto `Juego` existe, siempre hay un turno válido en
> curso.** La interfaz nunca se encuentra el juego a medio arrancar.

Esta es la diferencia clave con la versión de consola. Antes el constructor llamaba a
`iniciarJuego()`, que era un bucle infinito leyendo del teclado — el constructor no
retornaba hasta que la partida acababa. Eso, en una aplicación Swing, **congela la
ventana**, porque estarías bloqueando el hilo de eventos. Ahora el constructor prepara y
se va, y quien conduce la partida es el usuario a base de clics.

### 3.4 Las dos preguntas que la interfaz hace todo el rato

Estos dos métodos son la razón por la que `PanelJuego` puede pintar bordes sin saber
absolutamente nada de las reglas.

**`esSeleccionable(celda)` — "¿puedo agarrar esta ficha?"**

```java
public boolean esSeleccionable(Celda celda) {
    if (juegoTerminado || celda == null || !celda.tieneFicha()) { return false; }
    Ficha ficha = celda.getFicha();
    return ficha.delJugador() == jugador1Turno && ficha.getTipoFicha() == fichaPorMover;
}
```

Tres condiciones, en orden de coste: primero descarta lo barato (partida acabada, celda
vacía), y solo entonces mira quién es la dueña y si coincide con la ruleta. Devuelve
`true` para exactamente las fichas que deben salir con borde dorado.

**`esDestinoValido(origen, destino)` — "¿puedo soltarla aquí?"**

```java
public boolean esDestinoValido(Celda origen, Celda destino) {
    if (juegoTerminado || origen == null || destino == null) { return false; }
    if (origen == destino || !esSeleccionable(origen)) { return false; }
    if (!origen.getFicha().dentroRango(origen, destino)) { return false; }

    if (!destino.tieneFicha()) { return true; }              // casilla libre: moverse
    return destino.getFicha().delJugador() != origen.getFicha().delJugador();  // enemiga: atacar
}
```

Fíjate en que **reutiliza `esSeleccionable`**. No repite la comprobación de propiedad ni
la de la ruleta: si el origen no era agarrable, ningún destino es válido. Eso evita que
las dos reglas se desincronicen si algún día cambias una.

Las dos últimas líneas son la regla del juego en su forma más pura: si la casilla está
vacía te mueves, si hay un enemigo atacas, y si hay una ficha tuya no puedes hacer nada.

Un punto importante: **estos dos métodos no cambian nada**. Son preguntas puras. La
interfaz los llama 36 veces por repintado sin ningún efecto secundario, lo cual es
justamente lo que permite usarlos para colorear el tablero.

### 3.5 `jugar()` — el único botón rojo

```java
public boolean jugar(Celda origen, Celda destino) {
    if (juegoTerminado || !interactuable) { return false; }

    boolean exito = ejecutar(origen, destino);

    if (exito) {
        comprobarFinDelJuego();
        if (!juegoTerminado) {
            jugador1Turno = !jugador1Turno;
            girarRuletaValida();
        }
    }
    return exito;
}
```

Este método es corto porque no hace el trabajo: lo **coordina**. Es el guion de lo que
significa "completar un turno", y el orden importa muchísimo.

1. **Ejecutar la jugada.** `ejecutar()` valida y aplica el cambio al tablero.
2. **Si falló, no pasa nada más.** El turno no cambia, la ruleta no gira. El jugador
   sigue teniendo su oportunidad. Esto es lo que hace que un clic torpe no te cueste el
   turno.
3. **Comprobar si alguien ganó** — *antes* de cambiar de turno. Si el jugador acaba de
   destruir la última ficha enemiga, la partida termina aquí mismo y sería absurdo pasar
   el turno a alguien que ya no tiene piezas.
4. **Cambiar de turno y girar.** Solo si la partida sigue viva.

Ese orden entre los pasos 3 y 4 es el tipo de detalle que produce bugs raros si se
invierte: la partida terminaría, pero mostrando el turno del perdedor.

`jugar()` es también **el único punto de entrada que modifica la partida**. `ejecutar()`
es privado justamente para forzar esto: no se puede mover una ficha sin pasar por el
control de turnos. Si `ejecutar` fuese público, sería posible mover dos veces seguidas
sin que la ruleta girara.

### 3.6 `ejecutar()` — el embudo de validación

Este es el método más largo, pero tiene una estructura muy simple: es un **embudo de
guardas**. Cada `if` descarta un caso imposible y sale; si sobrevives a todos, la jugada
es legal.

```java
if (origen == null || destino == null)          -> fuera del tablero
if (origen == destino)                          -> no te puedes mover a ti mismo
if (atacante == null)                           -> la casilla de origen está vacía
if (atacante.delJugador() != jugador1Turno)     -> no es tu ficha
if (atacante.getTipoFicha() != fichaPorMover)   -> la ruleta dijo otra cosa
if (!atacante.dentroRango(origen, destino))     -> no llegas
```

El orden no es arbitrario. Va **de lo más barato y general a lo más específico**: primero
lo que evitaría un `NullPointerException`, después la propiedad, y al final el cálculo
geométrico del rango. Además, así el mensaje de error que recibe el jugador es siempre el
más relevante: si la ficha ni siquiera es suya, no tiene sentido decirle "fuera de rango".

Superado el embudo, hay exactamente **dos caminos**:

**Camino A — la casilla está vacía: movimiento.**

```java
origen.removeFicha();
destino.setFicha(atacante);
```

Dos líneas. Fíjate en que es una operación de *quitar y poner*, no de "mover": la `Ficha`
no tiene coordenadas, así que mover consiste en cambiar de qué celda cuelga. Por eso el
orden da igual aquí, pero conviene mantenerlo así por claridad.

**Camino B — hay un enemigo: ataque.**

```java
if (defensora.recibirAtaque(dano)) {
    destino.removeFicha();
    registrar("Se destruyó la pieza ...");
} else {
    registrar("Se atacó la pieza ... le quedan X de escudo y Y de vida");
}
```

Toda la aritmética del combate está dentro de `Ficha.recibirAtaque`, no aquí. `Juego` solo
pregunta: *"le pego 5, ¿se muere?"*. Ese `boolean` de retorno es lo que decide si la ficha
se retira del tablero. Mantener el cálculo del daño dentro de `Ficha` es lo que después te
va a permitir que el Vampiro tenga su absorción de sangre y el Necrómante su lanza que
ignora escudos, sin tocar `Juego`.

Entre los dos caminos hay una guarda más:

```java
if (defensora.delJugador() == atacante.delJugador()) -> "en 3:4 hay una ficha tuya"
```

Está *después* de comprobar si la casilla está vacía, y tiene que ser así: solo tiene
sentido preguntar de quién es la ficha si hay una ficha.

### 3.7 La ruleta y el problema del turno imposible

```java
private void girarRuletaValida() {
    for (int intento = 0; intento < MAX_GIROS; intento++) {
        girarRuleta();
        if (tablero.tieneFichaDelTipo(jugador1Turno, fichaPorMover)) {
            registrar("Turno de ... La ruleta saca: ...");
            return;
        }
        registrar("... no tiene fichas de tipo ...: pierde el turno.");
        jugador1Turno = !jugador1Turno;
    }
}
```

Aquí hay un problema real que resolver. La ruleta puede sacar "Muerte" cuando al jugador
ya le destruyeron sus dos Muertes. En ese caso **ninguna jugada sería legal**, y si nos
limitáramos a esperar, la partida se quedaría congelada para siempre.

La solución de este método: si el resultado no sirve, se registra que pierde el turno, se
pasa al contrario y **se vuelve a girar**. El bucle sigue hasta que sale un tipo que el
jugador de turno sí tiene.

El `MAX_GIROS = 20` es un **cinturón de seguridad**. En teoría nunca hace falta: si un
jugador se queda sin fichas, `comprobarFinDelJuego` ya habrá terminado la partida antes.
Pero un bucle potencialmente infinito dentro de un programa con interfaz es un riesgo que
no compensa correr — si algún día una condición rara se cuela, prefieres que el juego se
comporte raro a que la ventana se quede colgada sin responder.

Ojo: esto es una **versión simplificada** de la regla del enunciado. La regla real dice
que tienes derecho a giros extra según cuántas fichas hayas perdido (2 perdidas → 2 giros,
4 perdidas → 3 giros), y solo si agotas todos pierdes el turno. Ahí está el `TODO`.

### 3.8 El final de la partida

```java
private void comprobarFinDelJuego() {
    if (!tablero.tieneFichas(false)) { terminar(jugador1, "..."); }
    else if (!tablero.tieneFichas(true)) { terminar(jugador2, "..."); }
}
```

Lee con cuidado los booleanos, porque son fáciles de confundir: `tieneFichas(false)`
pregunta *"¿le quedan fichas al jugador 2?"*. Si la respuesta es no, gana el jugador 1.
La negación al principio invierte la lectura, y es el sitio donde más fácil se cuela un
error de signo.

`terminar()` concentra todo lo que hay que hacer al cerrar la partida:

```java
private void terminar(Usuario queGana, String mensajeFinal) {
    juegoTerminado = true;
    ganador = queGana;
    registrar(mensajeFinal);
    if (queGana != null) { queGana.sumarPuntos(PUNTOS_VICTORIA); }
    if (jugador1 != null) { jugador1.agregarHistorial(mensajeFinal); }
    if (jugador2 != null) { jugador2.agregarHistorial(mensajeFinal); }
}
```

Existe como método aparte porque hay **dos maneras de terminar**: por eliminación y por
retirada. Las dos tienen que hacer exactamente lo mismo (marcar el fin, repartir puntos,
guardar el resumen en ambos jugadores), solo cambia el texto. Si esto estuviera copiado en
dos sitios, tarde o temprano uno de los dos se olvidaría de sumar los puntos.

Ese `agregarHistorial` en **ambos** jugadores es lo que alimenta la pantalla "Historial de
mis últimos juegos": cada uno debe ver la partida en su propio registro, gane o pierda.

### 3.9 Los tres sabores del historial

```java
public List<String> getHistorial()        // lista completa, solo lectura
public String[] getHistorialArreglo()     // lo mismo como String[]
public String[] getUltimasJugadas(int n)  // las n últimas, la más reciente primero
```

Tres métodos para lo mismo puede parecer excesivo, pero cada uno tiene su cliente:

- `getHistorial()` devuelve `Collections.unmodifiableList(...)`. Eso significa que si
  alguien intenta hacer `juego.getHistorial().add("trampa")`, salta una excepción. Es una
  forma de decir *"puedes mirar, no puedes tocar"*: el historial solo lo escribe `Juego`,
  a través de `registrar`.
- `getHistorialArreglo()` existe porque el enunciado exige arreglos, y las pantallas de
  reportes te van a pedir un `String[]`.
- `getUltimasJugadas(n)` invierte el orden a propósito, porque en una lista de "últimas
  jugadas" lo que más interesa es lo que acaba de pasar.

---

## 4. `PanelJuego.java` — la mesa

### 4.1 La estructura de paneles

Antes de los métodos, el esqueleto. Es todo `BorderLayout` anidado, que es la forma más
corta de conseguir zonas fijas:

```
PanelJuego                     (BorderLayout)
└── menuPanel                  (BorderLayout)
     ├── lateralPanel   WEST   400 px de ancho fijo — hoy solo el historial
     ├── inferiorPanel  SOUTH  200 px de alto fijo  — todavía vacío (piezas comidas)
     └── escenarioPanel CENTER se come el resto
          ├── tituloPanel      NORTH  100 px — turno, ruleta, mensaje
          └── centrador        CENTER
               └── tableroPanel  cuadrado, GridLayout(6,6)
```

Las claves de por qué se ve bien:

- `lateralPanel` usa `setPreferredSize(new Dimension(400, 0))`. Ese **0 en el alto no es
  un error**: `BorderLayout` ignora el alto de las zonas WEST y EAST (les da todo el alto
  disponible) y solo respeta el ancho. Lo mismo al revés en `inferiorPanel`, con
  `Dimension(0, 200)`.
- El tablero **no va directo al CENTER**. Va dentro de `agregarPanelCentrador(...)`, que
  es un panel con `GridBagLayout` y un solo hijo. Si lo pusieras directo en el CENTER, se
  estiraría hasta ser un rectángulo. El centrador respeta su tamaño preferido, y
  `agregarPanelCuadrado` hace que ese tamaño preferido siempre sea un cuadrado.

### 4.2 `iniciarPanel()` — el orden de montaje

```java
juego = new Juego();

prepararContenido();
prepararLateral();
prepararInferior();
    prepararTituloEscenario();
    prepararTablero();
prepararEscenario();
prepararMenu();

add(menuPanel, BorderLayout.CENTER);
redibujar();
```

El orden **no es decorativo**. Swing exige que un panel exista antes de que puedas
metérselo a otro, así que esto se construye de dentro hacia fuera. La sangría que usaste
lo refleja: `tituloPanel` y `tableroPanel` van indentados porque son hijos de
`escenarioPanel`, que se construye justo después de ellos.

Dos cosas más:

- `juego = new Juego()` va **lo primero**. Tiene que ser así, porque `prepararTablero`
  crea los botones y `redibujar` pregunta el estado — sin motor, no hay nada que dibujar.
- `redibujar()` va **lo último**. Los paneles ya están montados y vacíos; esta llamada es
  la que los llena con el estado inicial de la partida. Sin ella verías un tablero de 36
  casillas de colores sin una sola ficha.

### 4.3 Por qué las casillas son botones

```java
private CasillaBoton crearCasilla(int fila, int columna) {
    boolean clara = (fila + columna) % 2 == 0;
    Color base = clara ? new Color(110, 110, 125) : new Color(45, 45, 58);

    CasillaBoton casilla = new CasillaBoton(fila, columna, base);
    casilla.setToolTipText("fila " + fila + ", columna " + columna);
    casilla.addActionListener(e -> alPulsarCasilla(fila, columna));
    return casilla;
}
```

El `(fila + columna) % 2` es el truco clásico del damero: la suma de coordenadas alterna
par e impar en cada paso, en las dos direcciones.

Sobre usar `JButton` en vez de `JPanel` con un `MouseListener`: `JButton` te da gratis el
`ActionListener`, el foco por teclado, el estado deshabilitado y la accesibilidad. Lo que
no querías de él es su aspecto, y por eso `CasillaBoton` desactiva todo lo visual
(`setContentAreaFilled(false)`, `setBorderPainted(false)`) y se dibuja entero en
`paintComponent`. Te quedas con el comportamiento y descartas la apariencia.

El detalle que hace que el lambda funcione: `fila` y `columna` son parámetros del método,
así que son *effectively final* y cada casilla captura **sus propias** coordenadas. Es
decir, cada uno de los 36 botones lleva grabado a qué celda corresponde. Por eso el
listener no necesita averiguar quién lo llamó.

Y algo que **no** está aquí y es importante: ninguna casilla tiene `setPreferredSize`. El
`GridLayout(6,6)` reparte el lado del tablero entre las 36; ponerles tamaño individual
sería pelearse con el layout.

### 4.4 `alPulsarCasilla()` — la máquina de dos estados

Este es el corazón de la interacción, y toda su complejidad viene de una cosa: **un
movimiento necesita dos clics, pero un clic no sabe si es el primero o el segundo**.

La solución es el único estado que `PanelJuego` guarda:

```java
private Celda origenSeleccionado;   // null = todavía no hay ficha agarrada
```

Con ese campo, cada clic cae en uno de cinco casos:

```java
if (origenSeleccionado == null) {
    // PRIMER CLIC: intentar agarrar
    if (juego.esSeleccionable(celda)) origenSeleccionado = celda;
    else avisar("Elige una ficha tuya del tipo ...");

} else if (celda == origenSeleccionado) {
    origenSeleccionado = null;                       // soltar la ficha

} else if (juego.esDestinoValido(origenSeleccionado, celda)) {
    juego.jugar(origenSeleccionado, celda);          // SEGUNDO CLIC: ejecutar
    origenSeleccionado = null;

} else if (juego.esSeleccionable(celda)) {
    origenSeleccionado = celda;                      // cambiar de ficha

} else {
    avisar("Esa casilla no es un destino valido");
}

redibujar();
```

Los casos cuarto y quinto son los que separan una interfaz agradable de una frustrante:

- **Cambiar de ficha sin deseleccionar.** Si tienes el Vampiro de 5:1 agarrado y pulsas el
  Vampiro de 5:4, lo natural es que cambies de pieza, no que te salte un error. Sin este
  caso, tendrías que pulsar dos veces: una para soltar y otra para agarrar.
- **Soltar pulsando la misma.** Un escape para cuando te arrepientes.

Y fíjate en dónde está `redibujar()`: **al final, una sola vez, fuera de todos los `if`**.
Cualquier cosa que haya pasado —agarrar, soltar, mover, o simplemente equivocarse— cambia
lo que hay que pintar. Ponerlo dentro de cada rama sería repetirlo cinco veces y arriesgar
olvidarse en una.

También merece atención lo que este método **no** hace: no comprueba si la ficha es del
color correcto, ni si llega hasta el destino, ni si el tipo coincide con la ruleta. Todo
eso lo delega en `esSeleccionable` y `esDestinoValido`. `PanelJuego` no conoce ni una sola
regla del juego, y eso es exactamente lo que buscábamos.

### 4.5 `redibujar()` — volcar el modelo sobre la pantalla

```java
public void redibujar() {
    if (casillas == null || juego == null) { return; }

    for (int fila = 0; fila < LADO; fila++)
        for (int columna = 0; columna < LADO; columna++)
            actualizarCasilla(casillas[fila][columna],
                              juego.getTablero().getCelda(fila, columna));

    actualizarCabecera();
    actualizarHistorial();
    tableroPanel.repaint();
}
```

La filosofía aquí es **"repintar todo"**. No se intenta averiguar qué casillas cambiaron;
se recorren las 36 y cada una se pone al día. Con un tablero de 6×6 el coste es
despreciable, y a cambio te ahorras toda una categoría de bugs: los de "la pantalla se
quedó desactualizada porque olvidé refrescar esa casilla concreta".

La guarda del principio protege del orden de arranque: si algo llamara a `redibujar` antes
de que existan los botones, sale sin hacer nada en vez de lanzar un `NullPointerException`.

### 4.6 `actualizarCasilla()` y `calcularResaltado()`

`actualizarCasilla` traduce una `Celda` del modelo a un `CasillaBoton` de la pantalla, y
tiene tres partes:

```java
boton.setAssetCelda(CargadorAssets.cargar(celda.getUrlAsset()));   // fondo
```

Si la celda no tiene asset, `cargar(null)` devuelve `null` y el botón pinta su color liso.
La lógica de "¿tiene textura o no?" no está aquí, está dentro de `CasillaBoton`. Eso es
deliberado: quien pinta decide cómo pintar.

```java
boton.setAssetFicha(CargadorAssets.cargar(ficha.getUrlAsset()));
boton.setRespaldo(ficha.getNombre().substring(0, 1).toUpperCase(), ...);
```

Se le dan **las dos cosas a la vez**: la imagen y un respaldo por si la imagen no existe.
El botón usará el PNG si lo tiene, y si no dibujará un disco con la inicial. Gracias a eso
puedes borrar la carpeta de assets y el juego sigue siendo jugable, solo que más feo.
Vampiro, Lobo, Muerte y Zombie tienen iniciales distintas, así que el respaldo nunca es
ambiguo.

`calcularResaltado` es el que decide el borde, y su estructura cuenta una historia:

```java
if (origenSeleccionado == null) {
    // Nada agarrado: enseñar qué se puede agarrar.
    return juego.esSeleccionable(celda) ? SELECCIONABLE : NINGUNO;
}
// Con algo agarrado: enseñar a dónde puede ir.
if (celda == origenSeleccionado) return SELECCIONADA;
if (juego.esDestinoValido(origenSeleccionado, celda)) return DESTINO;
return NINGUNO;
```

Son **dos modos visuales distintos**, no una lista de estados independientes. Antes de
elegir, la pantalla responde a "¿qué puedo mover?". Después de elegir, responde a "¿a
dónde puedo ir?". Por eso, en cuanto agarras una ficha, los bordes dorados de las demás
desaparecen: ya no son la pregunta relevante.

El segundo argumento de `setResaltado(..., celda.tieneFicha())` es lo que hace que un
destino con enemigo salga en rojo y uno vacío en verde. La interfaz distingue visualmente
"moverte aquí" de "atacar aquí" sin que tengas que leer nada.

### 4.7 `actualizarHistorial()` — por qué lleva un contador

```java
private int historialPintado;

private void actualizarHistorial() {
    List<String> historial = juego.getHistorial();
    for (int i = historialPintado; i < historial.size(); i++) {
        areaHistorial.append(historial.get(i) + "\n");
    }
    historialPintado = historial.size();
    areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
}
```

Este es el único sitio donde `PanelJuego` se aparta de la filosofía de "repintar todo", y
hay una razón concreta.

Lo simple sería `areaHistorial.setText(todo el historial junto)`. Funciona, pero cada
repintado reconstruye el documento entero, y `redibujar()` se llama **en cada clic**,
incluidos los que no hacen nada. Con 238 entradas en una partida normal, eso es tirar y
reconstruir todo el texto decenas de veces. Además, reescribir el texto hace saltar el
scroll y se pierde la posición si el jugador estaba leyendo hacia arriba.

`historialPintado` recuerda cuántas líneas ya se volcaron, así que cada llamada solo añade
las nuevas — que normalmente son cero, una o dos. La última línea baja el cursor al final
para que siempre se vea lo último.

### 4.8 `actualizarCabecera()` — el estado en palabras

Tres etiquetas, tres niveles de información:

- **`labelTurno`** — de quién es el turno. Lo grande, lo que se mira sin pensar.
- **`labelRuleta`** — qué tipo de ficha toca. La restricción del turno.
- **`labelMensaje`** — qué hacer ahora, o qué acaba de pasar.

Ese tercero es más sutil de lo que parece. Su texto cambia según el estado de selección:

```java
if (origenSeleccionado == null) labelMensaje.setText("Pulsa una ficha con borde dorado");
else labelMensaje.setText("Pulsa una casilla marcada, o la misma ficha para soltarla");
```

Es decir, la instrucción **se adapta al momento**, en lugar de ser un texto fijo que el
jugador aprende a ignorar. Y cuando algo sale mal, `avisar()` sobrescribe esa etiqueta con
el error concreto.

Hay un detalle a tener presente: `avisar()` de `PanelJuego` escribe directamente en la
etiqueta, y `redibujar()` la vuelve a poner justo después con el texto genérico. Como en
`alPulsarCasilla` el orden es "avisar, luego redibujar", **el aviso se pierde**. Es un
pequeño fallo pendiente: la solución limpia sería guardar el aviso en un campo y que
`actualizarCabecera` lo respete si es reciente, o usar `juego.getUltimoMensaje()` en su
lugar.

`etiquetaOscura()` existe por un motivo tonto pero real: `agregarLabel` de
`PanelAbstracto` pinta el texto en blanco, porque casi todos tus paneles tienen fondo
negro. Pero `tituloPanel` es gris claro, así que ahí el texto blanco sería invisible. Este
helper lo corrige en un solo sitio en vez de repetir `setForeground` tres veces.

---

## 5. El recorrido completo de una jugada

Vamos a seguir un clic desde el píxel hasta el repintado. Situación: es el turno de julio,
la ruleta sacó **Vampiro**, y va a atacar a un Lobo enemigo.

**Antes del clic.** El tablero ya está pintado. En algún momento anterior, `redibujar()`
recorrió las 36 celdas y `calcularResaltado` devolvió `SELECCIONABLE` para las dos celdas
que contienen Vampiros de julio. Sus botones tienen el borde dorado.

**Clic 1: julio pulsa el Vampiro de 5:1.**

1. Swing dispara el `ActionListener` de ese botón, que llama a `alPulsarCasilla(5, 1)`.
2. Se pide la celda: `juego.getTablero().getCelda(5, 1)`.
3. Como `origenSeleccionado` es `null`, estamos en el primer clic. Se pregunta
   `juego.esSeleccionable(celda)`: hay ficha, es de julio, y es un Vampiro como dijo la
   ruleta. Devuelve `true`.
4. `origenSeleccionado = celda`. **Nada ha cambiado en el modelo.** `Juego` ni se ha
   enterado; agarrar una ficha es un concepto puramente visual.
5. `redibujar()`. Ahora `calcularResaltado` entra por la otra rama: la celda 5:1 devuelve
   `SELECCIONADA` (borde dorado grueso), las celdas alcanzables devuelven `DESTINO`
   (verdes si están vacías, rojas si hay enemigo), y el resto `NINGUNO`. El otro Vampiro
   pierde su borde: ya no es la pregunta relevante.

**Clic 2: julio pulsa el Lobo enemigo en 4:2.**

6. `alPulsarCasilla(4, 2)`. Ahora `origenSeleccionado` no es `null`.
7. No es la misma celda, así que se pregunta `esDestinoValido(5:1, 4:2)`: el Vampiro llega
   a distancia 1 en diagonal, y la ficha de destino es del otro jugador. `true`.
8. **`juego.jugar(origen, destino)`.** Aquí es donde por fin cambia el modelo:
   - `ejecutar()` pasa el embudo de guardas sin salirse.
   - La casilla de destino tiene ficha, y es enemiga → camino de ataque.
   - `defensora.recibirAtaque(5)`: el escudo del Lobo absorbe lo que puede, el resto va a
     la vida. Devuelve `false` porque sobrevive.
   - `registrar("Se atacó la pieza Lobo en 4:2 y se le quitaron 5 puntos; le quedan
     0 puntos de escudo y 5 de vida.")` — al historial y a `ultimoMensaje`.
   - Vuelve `true`.
   - `comprobarFinDelJuego()`: al rival le quedan fichas, no pasa nada.
   - `jugador1Turno = false`.
   - `girarRuletaValida()`: sale "Lobo", el rival tiene Lobos, se registra el nuevo turno.
9. `origenSeleccionado = null`.
10. `redibujar()`:
    - Las 36 casillas se actualizan. La de 4:2 sigue teniendo su Lobo (sobrevivió), pero
      con menos vida — algo que ahora mismo no se ve en pantalla, y que sería un buen
      siguiente paso.
    - `calcularResaltado` vuelve al primer modo. Como ahora es turno del rival y la ruleta
      dice Lobo, se iluminan **sus** Lobos.
    - `actualizarCabecera` escribe "Turno de rival (negras)" y "Ruleta: Lobo".
    - `actualizarHistorial` añade las dos líneas nuevas (el ataque y el cambio de turno).
    - `repaint()` y Swing repinta.

**Lo que hay que quedarse de todo esto:**

- Los clics 1 y 2 son radicalmente distintos. El primero **no toca el modelo**; solo
  cambia qué se resalta. El segundo es el único que ejecuta algo.
- `Juego` nunca supo que hubo dos clics. Recibió una sola llamada a `jugar()`.
- `PanelJuego` nunca supo qué es un escudo, ni cuánto daño hace un Vampiro.

---

## 6. Las cuatro decisiones de diseño que explican todo

Si te quedas solo con cuatro ideas de este documento, que sean estas.

**1. Preguntar en vez de recordar.**

`PanelJuego` no guarda dónde está cada ficha. Cada repintado vuelve a preguntarle a
`Juego`. Es un poco más de trabajo por repintado, y a cambio es **imposible** que la
pantalla y el modelo se desincronicen. La clase entera de bugs "la ficha se movió pero se
sigue viendo en el sitio viejo" no puede ocurrir.

**2. Preguntas puras separadas de acciones.**

`esSeleccionable` y `esDestinoValido` no cambian nada, así que se pueden llamar cientos de
veces sin miedo. `jugar` sí cambia cosas, y por eso hay uno solo y es explícito. Esta
separación es lo que permite que el resaltado sea gratis: pintar el tablero es hacer 36
preguntas inocuas.

**3. Una sola puerta de entrada.**

`ejecutar()` es privado. La única forma de modificar la partida es `jugar()`, que se
encarga del turno, del final y de la ruleta. Es imposible mover una ficha y "olvidarse" de
pasar el turno, porque no hay forma de llamar a lo uno sin lo otro.

**4. Degradar en vez de romper.**

Falta un PNG → se dibuja la inicial. El oponente es `null` → se escribe "Jugador". La
coordenada está fuera del tablero → `getCelda` devuelve `null` y la guarda lo detecta. Ni
uno solo de estos casos lanza una excepción, lo cual además cubre el requisito del
enunciado de que el programa nunca termine abruptamente.

---

## 7. Lo que falta y dónde va

Para que sepas dónde tocar cuando sigas:

| Falta | Dónde va |
|---|---|
| Habilidades especiales (absorber sangre, lanza, invocar Zombie) | `Ficha` como método polimórfico; `ejecutar()` decide cuándo ofrecerlas |
| Elegir entre ataque normal y especial | Diálogo en `PanelJuego`; `Juego` expone una variante de `jugar()` |
| Giros extra según fichas perdidas | `girarRuletaValida()`, donde está el `TODO` |
| El Lobo no debe saltar por encima de fichas | `esDestinoValido`, comprobando el camino con el `Tablero` |
| Botón de retirarse | `lateralPanel`; el motor ya tiene `retirarse()` listo |
| Ver vida y escudo de cada ficha | `CasillaBoton`, dibujando barras encima; o el panel de info al seleccionar |
| Piezas comidas | `inferiorPanel`, leyendo del historial o de un contador nuevo |
| El aviso que se pierde | `actualizarCabecera`, usando `juego.getUltimoMensaje()` |

Y dos cosas del enunciado que siguen pendientes y valen nota:

- **`Ficha` no es abstracta.** El requisito 2 pide una clase abstracta con al menos un
  método abstracto, y ahora mismo `FichaLobo`, `FichaVampiro` y `FichaMuerte` no añaden
  nada a su padre. Las habilidades especiales son la excusa perfecta para arreglarlo:
  `ataqueEspecial()` abstracto en `Ficha`, implementado distinto en cada hija. Eso te da
  herencia, polimorfismo y clase abstracta de una sola vez.
- **No hay funciones recursivas.** El requisito 4 pide dos. Candidatas naturales: validar
  el camino libre del Lobo avanzando casilla a casilla, y recorrer el tablero buscando
  fichas alcanzables.
