package edu.sistemas.u2.algorithms;

/**
 * Ordenamiento por heap (Heap Sort).
 *
 * <p>
 * Complejidad temporal: Θ(n log n) en todos los casos.
 * Complejidad espacial: O(1) adicional (in-place).
 *
 * <p>
 * A diferencia de MergeSort, HeapSort presenta peor localidad de caché
 * porque accede a posiciones no contiguas (hijos en 2i+1 y 2i+2),
 * lo que genera más cache misses para n grande.
 */
public final class HeapSort {

    private HeapSort() {
    }

    /**
     * Ordena una copia del array de forma ascendente.
     *
     * @param arr array de entrada (no se modifica)
     * @return nuevo array ordenado
     */
    public static int[] sort(int[] arr) {
        int[] a = arr.clone();
        int n = a.length;

        // Fase 1: construir max-heap en O(n)
        for (int i = n / 2 - 1; i >= 0; i--)
            siftDown(a, n, i);

        // Fase 2: extraer elementos del heap en O(n log n)
        for (int i = n - 1; i > 0; i--) {
            int tmp = a[0];
            a[0] = a[i];
            a[i] = tmp;
            siftDown(a, i, 0);
        }
        return a;
    }

    /**
     * Hunde el elemento en la posición {@code root} hasta restaurar
     * la propiedad de max-heap en el subárbol de tamaño {@code n}.
     *
     * @param a    array que representa el heap
     * @param n    tamaño efectivo del heap
     * @param root índice del nodo raíz del subárbol a ajustar
     */
    private static void siftDown(int[] a, int n, int root) {
        while (true) {
            int largest = root;
            int left = 2 * root + 1;
            int right = 2 * root + 2;
            if (left < n && a[left] > a[largest])
                largest = left;
            if (right < n && a[right] > a[largest])
                largest = right;
            if (largest == root)
                break;
            int tmp = a[root];
            a[root] = a[largest];
            a[largest] = tmp;
            root = largest;
        }
    }
}