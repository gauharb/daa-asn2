import java.util.Arrays;
import java.util.NoSuchElementException;

public class MinHeap {

    private int[] heap;
    private int size;

    public long comparisons = 0;

    public MinHeap() {
        heap = new int[16];
        size = 0;
    }

    public MinHeap(int initialCapacity) {
        heap = new int[Math.max(initialCapacity, 1)];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int right(int i) {
        return 2 * i + 2;
    }

    private void ensureCapacity() {
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
    }

    private void swap(int i, int j) {
        int t = heap[i];
        heap[i] = heap[j];
        heap[j] = t;
    }

    public void insert(int x) {
        ensureCapacity();
        heap[size] = x;
        int i = size;
        size++;

        while (i > 0) {
            int p = parent(i);
            comparisons++;
            if (heap[p] <= heap[i]) {
                break;
            }
            swap(p, i);
            i = p;
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        return heap[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        int min = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = 0;

        int i = 0;
        while (true) {
            int l = left(i);
            int r = right(i);
            int smallest = i;

            if (l < size) {
                comparisons++;
                if (heap[l] < heap[smallest]) {
                    smallest = l;
                }
            }
            if (r < size) {
                comparisons++;
                if (heap[r] < heap[smallest]) {
                    smallest = r;
                }
            }

            if (smallest == i) {
                break;
            }
            swap(i, smallest);
            i = smallest;
        }

        return min;
    }

    public boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            if (heap[parent(i)] > heap[i]) {
                return false;
            }
        }
        return true;
    }
}