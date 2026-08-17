# Serialización en Java — guía de 4 minutos

## Qué es

Serializar es **convertir un objeto en una secuencia de bytes**, y deserializar es reconstruir
el objeto a partir de esos bytes. Es como aplanar un mueble para meterlo en una caja y volver
a montarlo idéntico en otro sitio.

Sirve para tres cosas: guardar en archivo, mandar por red, y **clonar un objeto entero en
memoria**. Esa tercera es la que te interesa a ti, y la vemos al final.

---

## 1. Lo mínimo que funciona

Java ya trae todo. Solo necesitas dos cosas:

```java
// 1) marcar la clase con la interfaz Serializable (no tiene métodos, es un permiso)
public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private int puntos;
    private transient String clave;      // transient = NO se guarda
    private List<String> historial = new ArrayList<>();
}
```

```java
// 2) escribir y leer con los dos streams de objetos
try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("jugador.dat"))) {
    out.writeObject(jugador);
}

try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("jugador.dat"))) {
    Jugador recuperado = (Jugador) in.readObject();   // hay que castear
}
```

`readObject()` devuelve `Object`, así que siempre casteas. Y lanza dos excepciones que tienes
que capturar: `IOException` y `ClassNotFoundException`.

Lo bueno: **arrastra todo el objeto de golpe**. Si guardas una `List<Jugador>`, se guardan la
lista, los jugadores y los historiales de cada uno, en una sola línea.

---

## 2. `transient`: lo que no quieres guardar

Un campo marcado `transient` se salta la serialización. Al deserializar vuelve con su valor por
defecto (`null` para objetos, `0` para números, `false` para booleanos).

```
antes de guardar : julio | puntos=9 | clave=12345
después de leer  : julio | puntos=9 | clave=null      <- transient
```

Úsalo para contraseñas, conexiones, y cualquier cosa que no tenga sentido conservar.

---

## 3. `serialVersionUID`: el número de versión

Es la huella digital de la clase. Si guardas un objeto y **después le añades un campo** a la
clase, Java calcula una huella distinta y al leer el archivo viejo revienta con
`InvalidClassException`.

Declarándolo tú a mano, le dices a Java «confía, sigue siendo la misma clase»:

```java
private static final long serialVersionUID = 1L;
```

Ponlo siempre. Es una línea y te ahorra un error rarísimo de depurar.

---

## 4. Las tres reglas que lo rompen todo

1. **Todo el grafo debe ser serializable.** Si `Tablero` tiene `Celda`, y `Celda` tiene `Ficha`,
   las tres necesitan `implements Serializable`. Si una sola no lo es →
   `NotSerializableException` en tiempo de ejecución.
2. **Los campos `static` no viajan.** Pertenecen a la clase, no al objeto.
3. **Nunca serialices componentes de Swing.** Técnicamente `JPanel` es serializable, pero
   arrastra media librería gráfica. Serializa el **modelo**, nunca la interfaz.

---

## 5. ⚠️ Tu proyecto no puede usarla para archivos

El requisito 5 del enunciado dice, literal:

> Toda la información del sistema debe almacenarse en **arreglos**. **No se debe persistir
> información en archivos.**

O sea que el uso clásico —guardar los jugadores en `usuarios.dat`— **te resta puntos**. No lo
hagas. Pero hay un uso que sí es legal, porque nunca toca el disco.

---

## 6. El uso que sí te sirve: copia profunda del tablero

Si escribes `Tablero copia = tablero;` no copias nada: las dos variables apuntan al mismo objeto
y modificar una modifica la otra. Copiar a mano un `Tablero` con 36 `Celda` y sus `Ficha`
significa escribir bastante código.

Serializar a **memoria** (`ByteArrayOutputStream` en vez de `FileOutputStream`) te da la copia
completa gratis:

```java
public static <T extends Serializable> T copiaProfunda(T original) throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
        out.writeObject(original);
    }
    try (ObjectInputStream in = new ObjectInputStream(
            new ByteArrayInputStream(bytes.toByteArray()))) {
        return (T) in.readObject();
    }
}
```

Con eso ya puedes:

- **Simular una jugada** antes de aplicarla, para marcar en verde las casillas válidas.
- **Deshacer**: guardas una copia antes de cada turno.
- **Probar la lógica** sin ensuciar el tablero real.

```java
Tablero copia = copiaProfunda(tablero);
copia.getCelda(5, 0).removeFicha();

tablero.getCelda(5, 0).tieneFicha();   // true  <- el original no se tocó
copia.getCelda(5, 0).tieneFicha();     // false
```

Solo tienes que añadir `implements Serializable` a `Tablero`, `Celda`, `Ficha` y `TipoFicha`.

---

## 7. La trampa que te va a morder: `TipoFicha`

Tus `TipoFicha.LOBO`, `VAMPIRO`, etc. son **constantes únicas**, y en `Juego.action()` las
comparas por identidad:

```java
if (selectedFicha.getTipoFicha() != fichaPorMover) { ... }
```

Al deserializar, Java **crea un objeto `TipoFicha` nuevo**. Ya no es la misma instancia que
`TipoFicha.LOBO`, así que ese `!=` da `true` siempre y ninguna ficha «corresponde». Probado:

```
original.getTipoFicha() == TipoFicha.LOBO  ->  true
copia.getTipoFicha()    == TipoFicha.LOBO  ->  false     ← se rompe action()
```

La solución es un método especial que Java llama automáticamente al deserializar: si devuelves
la constante original, la copia se descarta.

```java
// dentro de TipoFicha
private Object readResolve() {
    if (nombre.equals(VAMPIRO.nombre)) { return VAMPIRO; }
    if (nombre.equals(MUERTE.nombre))  { return MUERTE; }
    if (nombre.equals(LOBO.nombre))    { return LOBO; }
    return ZOMBIE;
}
```

Con eso, el `==` vuelve a dar `true`. Los `enum` de Java hacen esto solos — es una razón de peso
para convertir `TipoFicha` en un `enum` más adelante.

---

## Chuleta

| Cosa | Para qué |
|---|---|
| `implements Serializable` | Permiso. Sin métodos que implementar |
| `serialVersionUID` | Versión de la clase. Ponlo siempre |
| `transient` | Este campo no se guarda |
| `ObjectOutputStream.writeObject` | Objeto → bytes |
| `ObjectInputStream.readObject` | Bytes → objeto (castear) |
| `ByteArrayOutputStream` | Serializar a memoria = copia profunda |
| `readResolve()` | Devolver la constante original en vez de una copia |
| `NotSerializableException` | Alguna clase del grafo no es serializable |

**Para tu proyecto:** nada de archivos (requisito 5). Usa la copia profunda en memoria si
implementas «deshacer» o el resaltado de movimientos válidos, y no olvides `readResolve()` en
`TipoFicha`.
