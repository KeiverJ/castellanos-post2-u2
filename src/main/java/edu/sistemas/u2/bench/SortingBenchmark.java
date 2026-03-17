package edu.sistemas.u2.bench;

import edu.sistemas.u2.algorithms.HeapSort;
import edu.sistemas.u2.algorithms.InsertionSort;
import edu.sistemas.u2.algorithms.MergeSort;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark comparativo de tres algoritmos de ordenamiento.
 *
 * <p>
 * Configuración JMH:
 * <ul>
 * <li>Modo: tiempo promedio por operación (AverageTime)</li>
 * <li>Unidad: microsegundos</li>
 * <li>Warm-up: 5 iteraciones de 1 segundo cada una</li>
 * <li>Medición: 10 iteraciones de 1 segundo cada una</li>
 * <li>Forks: 2 procesos JVM independientes</li>
 * </ul>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
@State(Scope.Benchmark)
public class SortingBenchmark {

    /**
     * Tamaño del array de entrada.
     * JMH inyecta cada valor en ejecuciones separadas.
     */
    @Param({ "500", "1000", "2000", "4000", "8000", "16000" })
    public int n;

    /** Array de datos generado antes de cada medición. */
    private int[] data;

    /**
     * Genera un array aleatorio de tamaño {@code n} antes de cada
     * invocación individual, garantizando que cada medición parte
     * de datos sin ordenar.
     *
     * <p>
     * Se usa semilla fija (42) para reproducibilidad entre forks.
     */
    @Setup(Level.Invocation)
    public void setup() {
        data = new Random(42).ints(n, 0, Integer.MAX_VALUE).toArray();
    }

    /**
     * Benchmark de Insertion Sort — O(n²).
     *
     * @param bh Blackhole que consume el resultado para evitar
     *           que el JIT elimine el cómputo (dead code elimination)
     */
    @Benchmark
    public void insertionSort(Blackhole bh) {
        bh.consume(InsertionSort.sort(data));
    }

    /**
     * Benchmark de Merge Sort — O(n log n).
     *
     * @param bh Blackhole que consume el resultado
     */
    @Benchmark
    public void mergeSort(Blackhole bh) {
        bh.consume(MergeSort.sort(data));
    }

    /**
     * Benchmark de Heap Sort — O(n log n).
     *
     * @param bh Blackhole que consume el resultado
     */
    @Benchmark
    public void heapSort(Blackhole bh) {
        bh.consume(HeapSort.sort(data));
    }
}