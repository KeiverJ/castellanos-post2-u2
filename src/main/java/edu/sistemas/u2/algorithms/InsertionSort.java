package edu.sistemas.u2.algorithms;

/**
 * Ordenamiento por inserción.
 *
 * <p>
 * Complejidad temporal:
 * <ul>
 * <li>Mejor caso: Θ(n) — array ya ordenado</li>
 * <li>Caso promedio: Θ(n²)</li>
 * <li>Peor caso: Θ(n²) — array en orden inverso</li>
 * </ul>
 * Complejidad espacial: O(1) adicional (in-place).
 */
public final class InsertionSort {

    private InsertionSort() {
    }

    /**
     * Ordena una copia del array de forma ascendente.
     *
     * @param arr array de entrada (no se modifica)
     * @return nuevo array ordenado
     */
    public static int[] sort(int[] arr) {
        int[] a = arr.clone();
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
        return a;
    }
}