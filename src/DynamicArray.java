import java.util.NoSuchElementException;

public class DynamicArray<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROWTH_NUMERATOR = 2;

    private Object[] data;
    private int size;

    public DynamicArray() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0");
        }
        data = new Object[Math.max(initialCapacity, 1)];
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(T x) {
        ensureCapacity(size + 1);
        data[size] = x;
        size++;
    }

    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        ensureCapacity(size + 1);

        for (int i = size - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }

        data[index] = x;
        size++;
    }


    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        T removed = (T) data[index];

        // Shift elements [index+1, size) one position to the left.
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }

        data[size - 1] = null; // avoid memory leak / stale reference
        size--;
        return removed;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        return (T) data[index];
    }

    public void set(int index, T x) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        data[index] = x;
    }

    public boolean contains(T x) {
        return indexOf(x) >= 0;
    }

    public int indexOf(T x) {
        for (int i = 0; i < size; i++) {
            if (data[i] == null ? x == null : data[i].equals(x)) {
                return i;
            }
        }
        return -1;
    }


    public long containsWithCount(T x, long[] comparisonCounter) {
        for (int i = 0; i < size; i++) {
            comparisonCounter[0]++;
            if (data[i] == null ? x == null : data[i].equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) {
            return;
        }
        int newCapacity = Math.max(data.length * GROWTH_NUMERATOR, minCapacity);
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    public int capacity() {
        return data.length;
    }

    public T removeIfPresentOrThrow(T x) {
        int idx = indexOf(x);
        if (idx < 0) {
            throw new NoSuchElementException("Element not found: " + x);
        }
        return remove(idx);
    }
}
