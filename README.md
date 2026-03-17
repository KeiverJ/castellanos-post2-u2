# Costos Ocultos y Selección de Algoritmo

**Curso:** Diseño de Algoritmos y Sistemas — Unidad 2  
**Actividad:** Post-Contenido 2  
**Programa:** Ingeniería de Sistemas — Universidad de Santander (UDES)  
**Año:** 2026

---

## Descripción del experimento

Se cuantifica el impacto de los costos ocultos —efectos de caché,
localidad de acceso y uso de memoria— en el rendimiento real de
algoritmos con la misma complejidad teórica. Se realizan tres
experimentos con JMH y GC profiler:

1. **Experimento de localidad de caché en matrices 2D** — compara
   acceso row-major vs column-major sobre matrices cuadradas de
   hasta 2048×2048 enteros.
2. **Experimento ArrayList vs LinkedList** — compara ambas estructuras
   bajo patrones de acceso aleatorio e iteración, con perfil de GC.
3. **Framework de selección por restricciones** — aplica los 5 pasos
   del framework para justificar la selección de estructura de datos
   para un servicio de gestión de eventos con restricciones reales.

---

## Requisitos

- Java 17+, Maven 3.8+

```bash
java --version
mvn --version
```

---

## Instrucciones de build y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/<usuario>/castellanos-post2-u2.git
cd castellanos-post2-u2

# 2. Compilar
mvn clean package -q

# 3. Ejecutar benchmark de matrices
java -jar target/sorting-benchmark-1.0.0.jar "MatrixBenchmark" \
  -rf json -rff results/matrix-results.json

# 4. Ejecutar benchmark de listas con GC profiler
java -jar target/sorting-benchmark-1.0.0.jar "ListBenchmark" \
  -prof gc -rf json -rff results/list-results.json
```

---

## Experimento 1: Localidad de caché en matrices 2D

### Resultados

| n    | rowMajor (ms) | columnMajor (ms) | Factor degradación |
| ---- | ------------- | ---------------- | ------------------ |
| 256  | 0.006         | 0.032            | 5.33x              |
| 512  | 0.023         | 0.279            | 12.13x             |
| 1024 | 0.096         | 1.203            | 12.53x             |
| 2048 | 0.861         | 12.374           | 14.37x             |

### Análisis

Ambos métodos tienen complejidad Θ(n²) pero el factor de degradación
crece de 5.33x para n=256 hasta 14.37x para n=2048. El acceso
row-major recorre elementos contiguos en memoria (stride = 4 bytes),
aprovechando completamente cada línea de caché de 64 bytes cargada.
El acceso column-major salta entre filas con stride = n×4 bytes,
generando un cache miss por cada acceso una vez que la matriz supera
el tamaño de la caché L2/L3. Para n=2048 la matriz ocupa 16 MB
(2048×2048×4 bytes), superando la caché L3 típica de 8-12 MB, lo
que explica el factor máximo de 14.37x observado.

---

## Experimento 2: ArrayList vs LinkedList

### Acceso aleatorio (get al elemento central)

| n       | ArrayList (μs) | LinkedList (μs) | Factor  |
| ------- | -------------- | --------------- | ------- |
| 1.000   | 0.001          | 0.560           | 560x    |
| 10.000  | 0.001          | 5.997           | 5.997x  |
| 100.000 | 0.001          | 63.129          | 63.129x |

### Iteración secuencial con GC profiler

| n       | ArrayList (μs) | gc.alloc (B/op) | LinkedList (μs) | gc.alloc (B/op) | Factor |
| ------- | -------------- | --------------- | --------------- | --------------- | ------ |
| 1.000   | 0.522          | 0.004           | 1.316           | 0.009           | 2.52x  |
| 10.000  | 6.017          | 0.041           | 13.126          | 0.089           | 2.18x  |
| 100.000 | 63.579         | 0.434           | 137.613         | 0.938           | 2.16x  |

### Análisis

El acceso aleatorio de ArrayList es O(1) real: mantiene 0.001 μs
independientemente de n. LinkedList escala linealmente confirmando
O(n): de 0.560 μs (n=1.000) a 63.129 μs (n=100.000). La iteración
de LinkedList es consistentemente 2.16-2.52x más lenta que ArrayList,
porque sus nodos están dispersos en el heap y generan cache misses
frecuentes. La métrica gc.alloc.rate.norm confirma que LinkedList
asigna más de 2x bytes por operación de iteración, incrementando la
presión sobre el recolector de basura.

---

## Experimento 3: Framework de selección

Ver análisis completo en [docs/decision-framework.md](docs/decision-framework.md).

**Decisión:** `ArrayList<Integer>` para el servicio de gestión de
eventos con n=50.000, justificada por acceso aleatorio O(1) vs O(n),
iteración 2.16x más rápida, y menor presión de GC en contenedor
con 512 MB de RAM.

---

## Conclusión general

La notación asintótica O() es necesaria pero no suficiente para
tomar decisiones de diseño en producción. Los experimentos demuestran
que dos algoritmos o estructuras con la misma clase O() pueden
diferir hasta 14x en rendimiento real (matrices) o 2x en iteración
(listas), debido a efectos de localidad de caché, presión de GC y
constantes ocultas que la notación asintótica abstrae por definición.
