# Framework de Selección Algorítmica por Restricciones

**Problema:** Servicio de gestión de eventos que procesa listas de n eventos con
las siguientes operaciones requeridas:

- (a) Inserción al final frecuente
- (b) Acceso por índice ocasional (top-10 por prioridad)
- (c) Iteración completa una vez por segundo
- Contenedor con 512 MB de RAM límite
- n típico = 50.000, n máximo = 200.000

---

## Paso 1 — Caracterizar la entrada

| Parámetro    | Valor                     |
| ------------ | ------------------------- |
| n típico     | 50.000                    |
| n máximo     | 200.000                   |
| Tipo de dato | Integer (boxed)           |
| Operaciones  | add-tail, get(i), forEach |
| Frecuencia   | iteración 1 vez/segundo   |

---

## Paso 2 — Restricciones de entorno

| Restricción | Valor                                          |
| ----------- | ---------------------------------------------- |
| RAM límite  | 512 MB (contenedor)                            |
| Latencia    | No crítica (1s entre iteraciones)              |
| GC          | Contenedor → evitar alta presión de allocación |
| Plataforma  | JVM 64-bit con punteros comprimidos            |

---

## Paso 3 — Candidatos asintóticos viables

| Estructura | add-tail    | get(i) | iterate | Espacio      |
| ---------- | ----------- | ------ | ------- | ------------ |
| ArrayList  | O(1) amort. | O(1)   | O(n)    | O(n)         |
| LinkedList | O(1)        | O(n)   | O(n)    | O(n) + nodos |
| ArrayDeque | O(1) amort. | ✗      | O(n)    | O(n)         |

ArrayDeque se descarta porque no soporta acceso por índice get(i),
requerido por la operación (b).

---

## Paso 4 — Constantes y caché (evidencia empírica)

Los benchmarks de ListBenchmark con n = 100.000 muestran:

**Iteración secuencial:**

| Estructura | Tiempo (μs) | gc.alloc.rate.norm (B/op) | Factor          |
| ---------- | ----------- | ------------------------- | --------------- |
| ArrayList  | 63.579      | 0.434                     | 1.0x base       |
| LinkedList | 137.613     | 0.938                     | 2.16x más lento |

LinkedList es **2.16x más lento** en iteración para n = 100.000,
con mayor presión de allocación por sus nodos dispersos en heap.

**Acceso aleatorio (get al elemento central):**

| Estructura | n=1.000 (μs) | n=10.000 (μs) | n=100.000 (μs) | Complejidad |
| ---------- | ------------ | ------------- | -------------- | ----------- |
| ArrayList  | 0.001        | 0.001         | 0.001          | O(1)        |
| LinkedList | 0.560        | 5.997         | 63.129         | O(n)        |

ArrayList mantiene tiempo **constante** independientemente de n.
LinkedList escala **linealmente**: de 0.560 μs (n=1.000) a
63.129 μs (n=100.000), confirmando O(n) empíricamente.

Para n = 50.000 el acceso aleatorio de LinkedList tomaría
aproximadamente 31 μs vs 0.001 μs de ArrayList — una diferencia
de **~31.000x**.

---

## Paso 5 — Decisión justificada

**SELECCIÓN: `ArrayList<Integer>`**

| Criterio                 | ArrayList   | LinkedList               |
| ------------------------ | ----------- | ------------------------ |
| add-tail                 | O(1) amort. | O(1)                     |
| get(i) n=50.000          | ~0.001 μs   | ~31 μs                   |
| iteración n=100.000      | 63.579 μs   | 137.613 μs (2.16x peor)  |
| gc.alloc.rate.norm iter. | 0.434 B/op  | 0.938 B/op               |
| Presión GC               | Baja        | Alta (nodo por elemento) |
| Memoria adicional        | Ninguna     | ~48 bytes por nodo       |

**Justificación:** Aunque ambas estructuras tienen la misma clase
asintótica O(1) para inserción al final y O(n) para iteración,
los datos empíricos muestran que ArrayList es 2.16x más rápido en
iteración y aproximadamente 31.000x más rápido en acceso aleatorio
para n = 50.000. La penalización de LinkedList en caché e iteración
supera completamente su ventaja teórica en add-tail. Adicionalmente,
LinkedList genera mayor presión de GC por la creación de un objeto
nodo por cada elemento, lo cual es crítico en un contenedor con
512 MB de RAM límite.

**Conclusión:** La notación asintótica O() no captura las constantes
ocultas ni los efectos de localidad de caché. Dos estructuras con la
misma clase O() pueden diferir en más de 2x en rendimiento real,
como demuestran los benchmarks empíricos de este laboratorio.
