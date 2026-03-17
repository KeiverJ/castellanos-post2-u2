package edu.sistemas.u2.algorithms;

/**
 * Ordenamiento por mezcla (Merge Sort).
 *
 * <p>
 * Complejidad temporal: Θ(n log n) en todos los casos.
 * Complejidad espacial: O(n) por el array auxiliar en cada mezcla.
 */
public final class MergeSort {

    private MergeSort() {
    }

    /**
     * Ordena una copia del array de forma ascendente.
     *
     * @param arr array de entrada (no se modifica)
     * @return nuevo array ordenado
     */
    public static int[] sort(int[] arr) {
        int[] a = arr.clone();
        mergeSort(a, 0, a.length - 1);
        return a;
    }

    /**
     * Divide recursivamente el subarray [lo, hi] y lo ordena.
     *
     * @param a  array a ordenar (se modifica in-place)
     * @param lo índice inferior del subarray
     * @param hi índice superior del subarray
     */
    private static void mergeSort(int[] a, int lo, int hi) {
        if (lo >= hi)
            return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, lo, mid);
        mergeSort(a, mid + 1, hi);
        merge(a, lo, mid, hi);
    }

    /**
     * Combina dos subarrays ordenados en uno solo.
     *
     * @param a   array que contiene ambos subarrays
     * @param lo  inicio del primer subarray
     * @param mid fin del primer subarray
     * @param hi  fin del segundo subarray
     */
    private static void merge(int[] a, int lo, int mid, int hi) {
        int[] tmp = new int[hi - lo + 1];
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi)
            tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i <= mid)
            tmp[k++] = a[i++];
        while (j <= hi)
            tmp[k++] = a[j++];
        System.arraycopy(tmp, 0, a, lo, tmp.length);
    }
}