import java.util.Random;

public class Tests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testDynamicArray();
        testLinkedList();
        testMinHeap();
        crossCheckAgainstJavaCollections();

        System.out.println();
        System.out.println("=== SUMMARY: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void expectException(String name, Runnable r) {
        try {
            r.run();
            failed++;
            System.out.println("[FAIL] " + name + " (expected exception, none thrown)");
        } catch (IndexOutOfBoundsException e) {
            passed++;
            System.out.println("[PASS] " + name);
        }
    }

    // DynamicArray

    private static void testDynamicArray() {
        System.out.println("   DynamicArray   ");

        DynamicArray<Integer> a = new DynamicArray<>();
        check("empty: size == 0", a.size() == 0);
        check("empty: isEmpty true", a.isEmpty());
        check("empty: contains false", !a.contains(1));
        expectException("empty: get(0) throws", () -> a.get(0));

        a.add(42);
        check("one element: size == 1", a.size() == 1);
        check("one element: get(0) == 42", a.get(0) == 42);
        check("one element: contains(42)", a.contains(42));
        check("one element: !contains(7)", !a.contains(7));

        DynamicArray<Integer> b = new DynamicArray<>();
        for (int i = 0; i < 10; i++) {
            b.add(i);
        }
        check("multiple: size == 10", b.size() == 10);
        check("multiple: get(0) == 0 (boundary low)", b.get(0) == 0);
        check("multiple: get(9) == 9 (boundary high)", b.get(9) == 9);
        expectException("multiple: get(-1) throws", () -> b.get(-1));
        expectException("multiple: get(10) throws", () -> b.get(10));

        b.add(0, -1);
        check("add(0,x): new first element", b.get(0) == -1);
        b.add(b.size(), 999);
        check("add(size,x): new last element", b.get(b.size() - 1) == 999);
        int midSizeBefore = b.size();
        b.add(midSizeBefore / 2, 12345);
        check("add(mid,x): correct placement", b.get(midSizeBefore / 2) == 12345);

        DynamicArray<Integer> c = new DynamicArray<>();
        for (int i = 0; i < 5; i++) c.add(i);
        int removedFirst = c.remove(0);
        check("remove(0): returns 0", removedFirst == 0);
        check("remove(0): shifts left", c.get(0) == 1);
        int removedLast = c.remove(c.size() - 1);
        check("remove(last): returns correct value", removedLast == 4);

        expectException("add: negative index throws", () -> c.add(-1, 100));
        expectException("add: index > size throws", () -> c.add(c.size() + 1, 100));
        expectException("remove: negative index throws", () -> c.remove(-1));
        expectException("remove: index == size throws", () -> c.remove(c.size()));

        DynamicArray<Integer> d = new DynamicArray<>();
        d.add(7); d.add(7); d.add(7);
        check("duplicates: contains(7)", d.contains(7));
        check("duplicates: size == 3", d.size() == 3);
        d.remove(0);
        check("duplicates: still contains(7) after removing one", d.contains(7));
        check("duplicates: size == 2 after remove", d.size() == 2);

        DynamicArray<Integer> e = new DynamicArray<>();
        int n = 100_000;
        for (int i = 0; i < n; i++) e.add(i);
        check("large: size == n", e.size() == n);
        check("large: get(n/2) correct", e.get(n / 2) == n / 2);
        check("large: contains(n-1)", e.contains(n - 1));
        check("large: !contains(n)", !e.contains(n));
    }

    // LinkedList

    private static void testLinkedList() {
        System.out.println("   LinkedList   ");

        LinkedList<Integer> a = new LinkedList<>();
        check("empty: size == 0", a.size() == 0);
        check("empty: isEmpty true", a.isEmpty());
        check("empty: contains false", !a.contains(1));
        expectException("empty: get(0) throws", () -> a.get(0));

        a.add(42);
        check("one element: size == 1", a.size() == 1);
        check("one element: get(0) == 42", a.get(0) == 42);
        check("one element: remove(0) returns 42", a.remove(0) == 42);
        check("one element: empty again after remove", a.isEmpty());

        LinkedList<Integer> b = new LinkedList<>();
        for (int i = 0; i < 10; i++) b.add(i);
        check("multiple: size == 10", b.size() == 10);
        check("multiple: get(0) boundary low", b.get(0) == 0);
        check("multiple: get(9) boundary high", b.get(9) == 9);
        expectException("multiple: get(-1) throws", () -> b.get(-1));
        expectException("multiple: get(10) throws", () -> b.get(10));

        b.add(0, -1);
        check("add(0,x): new first element", b.get(0) == -1);
        b.add(b.size(), 999);
        check("add(size,x): new last element", b.get(b.size() - 1) == 999);
        int mid = b.size() / 2;
        b.add(mid, 12345);
        check("add(mid,x): correct placement", b.get(mid) == 12345);

        LinkedList<Integer> c = new LinkedList<>();
        for (int i = 0; i < 5; i++) c.add(i);
        int removedFirst = c.remove(0);
        check("remove(0): returns 0", removedFirst == 0);
        check("remove(0): new head correct", c.get(0) == 1);
        int removedLast = c.remove(c.size() - 1);
        check("remove(last): returns correct value", removedLast == 4);

        expectException("add: negative index throws", () -> c.add(-1, 100));
        expectException("add: index > size throws", () -> c.add(c.size() + 1, 100));
        expectException("remove: negative index throws", () -> c.remove(-1));
        expectException("remove: index == size throws", () -> c.remove(c.size()));

        LinkedList<Integer> d = new LinkedList<>();
        d.add(7); d.add(7); d.add(7);
        check("duplicates: contains(7)", d.contains(7));
        d.remove(0);
        check("duplicates: still contains(7)", d.contains(7));
        check("duplicates: size == 2", d.size() == 2);

        LinkedList<Integer> e = new LinkedList<>();
        int n = 100_000;
        for (int i = 0; i < n; i++) e.add(i);
        check("large: size == n", e.size() == n);
        check("large: get(n/2) correct", e.get(n / 2) == n / 2);
        check("large: contains(n-1)", e.contains(n - 1));
        check("large: !contains(n)", !e.contains(n));
    }

    // MinHeap

    private static void testMinHeap() {
        System.out.println("   MinHeap   ");

        MinHeap h = new MinHeap();
        check("empty: size == 0", h.size() == 0);
        check("empty: isEmpty true", h.isEmpty());
        try {
            h.peekMin();
            check("empty: peekMin throws", false);
        } catch (java.util.NoSuchElementException ex) {
            check("empty: peekMin throws", true);
        }

        h.insert(5);
        check("one element: peekMin == 5", h.peekMin() == 5);
        check("one element: isValidHeap", h.isValidHeap());
        check("one element: extractMin == 5", h.extractMin() == 5);
        check("one element: empty after extract", h.isEmpty());

        int[] values = {7, 3, 9, 3, 1, 8, 1, 20, -5, 0};
        MinHeap h2 = new MinHeap();
        for (int v : values) {
            h2.insert(v);
            check("insert(" + v + "): heap property holds", h2.isValidHeap());
        }
        check("multiple: size correct", h2.size() == values.length);

        int[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        boolean nonDecreasing = true;
        int prev = Integer.MIN_VALUE;
        int[] extracted = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            extracted[i] = h2.extractMin();
            if (extracted[i] < prev) nonDecreasing = false;
            prev = extracted[i];
            check("extractMin " + i + ": heap property holds after extraction", h2.isValidHeap());
        }
        check("extraction order matches sorted array", java.util.Arrays.equals(extracted, sorted));
        check("extraction order is non-decreasing", nonDecreasing);

        MinHeap h3 = new MinHeap();
        Random rnd = new Random(42);
        int n = 100_000;
        for (int i = 0; i < n; i++) {
            h3.insert(rnd.nextInt(1_000_000));
        }
        check("large: size == n", h3.size() == n);
        check("large: isValidHeap after n inserts", h3.isValidHeap());
        int last = Integer.MIN_VALUE;
        boolean ok = true;
        for (int i = 0; i < n; i++) {
            int v = h3.extractMin();
            if (v < last) { ok = false; }
            last = v;
        }
        check("large: n extractions non-decreasing", ok);
        check("large: empty after n extractions", h3.isEmpty());
    }


    private static void crossCheckAgainstJavaCollections() {
        System.out.println(" Cross-check vs java.util ");

        Random rnd = new Random(42);
        java.util.ArrayList<Integer> reference = new java.util.ArrayList<>();
        DynamicArray<Integer> mine = new DynamicArray<>();

        for (int i = 0; i < 2000; i++) {
            int op = rnd.nextInt(3);
            if (op == 0 || reference.isEmpty()) {
                int v = rnd.nextInt(10000);
                reference.add(v);
                mine.add(v);
            } else if (op == 1) {
                int idx = rnd.nextInt(reference.size());
                reference.remove(idx);
                mine.remove(idx);
            } else {
                int idx = rnd.nextInt(reference.size() + 1);
                int v = rnd.nextInt(10000);
                reference.add(idx, v);
                mine.add(idx, v);
            }
        }
        boolean same = reference.size() == mine.size();
        if (same) {
            for (int i = 0; i < reference.size(); i++) {
                if (!reference.get(i).equals(mine.get(i))) { same = false; break; }
            }
        }
        check("DynamicArray matches ArrayList after randomized ops", same);

        java.util.PriorityQueue<Integer> pq = new java.util.PriorityQueue<>();
        MinHeap heap = new MinHeap();
        for (int i = 0; i < 5000; i++) {
            int v = rnd.nextInt(100000);
            pq.add(v);
            heap.insert(v);
        }
        boolean sameOrder = true;
        while (!pq.isEmpty()) {
            if (!pq.poll().equals(heap.extractMin())) { sameOrder = false; break; }
        }
        check("MinHeap matches PriorityQueue extraction order", sameOrder);
    }
}
