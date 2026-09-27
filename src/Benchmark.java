import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Benchmark {

    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int REPEATS = 5;
    private static final long SEED = 42L;
    private static final String OUT_DIR = "results/tables/";

    public static void main(String[] args) throws IOException {
        new java.io.File(OUT_DIR).mkdirs();

        workload1_RandomAccess();
        workload2_Search();
        workload3_InsertionRemoval();
        workload4_PriorityProcessing();

        System.out.println("All workloads complete. CSV files written to " + OUT_DIR);
    }

    private static void workload1_RandomAccess() throws IOException {
        System.out.println("    Workload 1: Random Access   ");
        try (PrintWriter w = csv("workload1_random_access.csv",
                "structure,n,avg_time_ns,accesses")) {
            for (int n : SIZES) {
                int[] indices = randomIndices(n, 10_000, SEED);

                double daTime = 0;
                for (int rep = 0; rep < REPEATS; rep++) {
                    DynamicArray<Integer> da = buildDynamicArray(n, SEED);
                    long start = System.nanoTime();
                    long sink = 0;
                    for (int idx : indices) {
                        sink += da.get(idx);
                    }
                    long elapsed = System.nanoTime() - start;
                    daTime += elapsed;
                    consume(sink);
                }
                daTime /= REPEATS;
                w.println("DynamicArray," + n + "," + (long) daTime + "," + indices.length);
                System.out.printf("DynamicArray n=%-7d avg=%,d ns%n", n, (long) daTime);

                double llTime = 0;
                for (int rep = 0; rep < REPEATS; rep++) {
                    LinkedList<Integer> ll = buildLinkedList(n, SEED);
                    long start = System.nanoTime();
                    long sink = 0;
                    for (int idx : indices) {
                        sink += ll.get(idx);
                    }
                    long elapsed = System.nanoTime() - start;
                    llTime += elapsed;
                    consume(sink);
                }
                llTime /= REPEATS;
                w.println("LinkedList," + n + "," + (long) llTime + "," + indices.length);
                System.out.printf("LinkedList  n=%-7d avg=%,d ns%n", n, (long) llTime);
            }
        }
    }

    private static void workload2_Search() throws IOException {
        System.out.println("    Workload 2: Search    ");
        try (PrintWriter w = csv("workload2_search.csv",
                "structure,n,avg_time_ns,avg_comparisons")) {
            for (int n : SIZES) {
                Random valueRnd = new Random(SEED + 1);
                int[] searchValues = new int[1_000];
                for (int i = 0; i < searchValues.length; i++) {

                    searchValues[i] = valueRnd.nextInt(n * 2);
                }

                double daTime = 0;
                long daComparisons = 0;
                for (int rep = 0; rep < REPEATS; rep++) {
                    DynamicArray<Integer> da = buildDynamicArray(n, SEED);
                    long[] counter = {0};
                    long start = System.nanoTime();
                    for (int v : searchValues) {
                        da.containsWithCount(v, counter);
                    }
                    long elapsed = System.nanoTime() - start;
                    daTime += elapsed;
                    if (rep == REPEATS - 1) daComparisons = counter[0];
                }
                daTime /= REPEATS;
                w.println("DynamicArray," + n + "," + (long) daTime + "," + daComparisons);
                System.out.printf("DynamicArray n=%-7d avg=%,d ns  comparisons=%,d%n", n, (long) daTime, daComparisons);

                double llTime = 0;
                long llComparisons = 0;
                for (int rep = 0; rep < REPEATS; rep++) {
                    LinkedList<Integer> ll = buildLinkedList(n, SEED);
                    long[] counter = {0};
                    long start = System.nanoTime();
                    for (int v : searchValues) {
                        ll.containsWithCount(v, counter);
                    }
                    long elapsed = System.nanoTime() - start;
                    llTime += elapsed;
                    if (rep == REPEATS - 1) llComparisons = counter[0];
                }
                llTime /= REPEATS;
                w.println("LinkedList," + n + "," + (long) llTime + "," + llComparisons);
                System.out.printf("LinkedList  n=%-7d avg=%,d ns  comparisons=%,d%n", n, (long) llTime, llComparisons);
            }
        }
    }

    private static void workload3_InsertionRemoval() throws IOException {
        System.out.println("    Workload 3: Insertion and Removal    ");
        try (PrintWriter w = csv("workload3_insert_remove.csv",
                "structure,n,position,operation,avg_time_ns,movements")) {
            int m = 1_000;
            for (int n : SIZES) {
                runInsertRemove(w, n, 0, "begin", m);
                runInsertRemove(w, n, n / 2, "middle", m);
            }
        }
    }

    private static void runInsertRemove(PrintWriter w, int n, int index, String label, int m) {
        Random valRnd = new Random(SEED + 2);

        double daInsertTime = 0;
        long daInsertMoves = 0;
        for (int rep = 0; rep < REPEATS; rep++) {
            DynamicArray<Integer> da = buildDynamicArray(n, SEED);
            long start = System.nanoTime();
            for (int i = 0; i < m; i++) {
                da.add(Math.min(index, da.size()), valRnd.nextInt());
            }
            daInsertTime += System.nanoTime() - start;
        }
        daInsertTime /= REPEATS;

        daInsertMoves = estimateArrayShiftCost(n, index, m, true);
        w.println("DynamicArray," + n + "," + label + ",insert," + (long) daInsertTime + "," + daInsertMoves);
        System.out.printf("DynamicArray insert n=%-7d pos=%-6s avg=%,d ns%n", n, label, (long) daInsertTime);

        double llInsertTime = 0;
        for (int rep = 0; rep < REPEATS; rep++) {
            LinkedList<Integer> ll = buildLinkedList(n, SEED);
            long start = System.nanoTime();
            for (int i = 0; i < m; i++) {
                ll.add(Math.min(index, ll.size()), valRnd.nextInt());
            }
            llInsertTime += System.nanoTime() - start;
        }
        llInsertTime /= REPEATS;
        long llInsertTraversal = estimateLinkedTraversalCost(n, index, m);
        w.println("LinkedList," + n + "," + label + ",insert," + (long) llInsertTime + "," + llInsertTraversal);
        System.out.printf("LinkedList   insert n=%-7d pos=%-6s avg=%,d ns%n", n, label, (long) llInsertTime);

        double daRemoveTime = 0;
        for (int rep = 0; rep < REPEATS; rep++) {
            DynamicArray<Integer> da = buildDynamicArray(n, SEED);
            long start = System.nanoTime();
            for (int i = 0; i < m && da.size() > 0; i++) {
                int idx = Math.min(index, da.size() - 1);
                da.remove(idx);
            }
            daRemoveTime += System.nanoTime() - start;
        }
        daRemoveTime /= REPEATS;
        long daRemoveMoves = estimateArrayShiftCost(n, index, m, false);
        w.println("DynamicArray," + n + "," + label + ",remove," + (long) daRemoveTime + "," + daRemoveMoves);
        System.out.printf("DynamicArray remove n=%-7d pos=%-6s avg=%,d ns%n", n, label, (long) daRemoveTime);

        double llRemoveTime = 0;
        for (int rep = 0; rep < REPEATS; rep++) {
            LinkedList<Integer> ll = buildLinkedList(n, SEED);
            long start = System.nanoTime();
            for (int i = 0; i < m && ll.size() > 0; i++) {
                int idx = Math.min(index, ll.size() - 1);
                ll.remove(idx);
            }
            llRemoveTime += System.nanoTime() - start;
        }
        llRemoveTime /= REPEATS;
        long llRemoveTraversal = estimateLinkedTraversalCost(n, index, m);
        w.println("LinkedList," + n + "," + label + ",remove," + (long) llRemoveTime + "," + llRemoveTraversal);
        System.out.printf("LinkedList   remove n=%-7d pos=%-6s avg=%,d ns%n", n, label, (long) llRemoveTime);
    }

    private static long estimateArrayShiftCost(int n, int index, int m, boolean insert) {
        long total = 0;
        int size = n;
        for (int i = 0; i < m; i++) {
            int idx = Math.min(index, insert ? size : Math.max(size - 1, 0));
            total += (size - idx);
            size += insert ? 1 : -1;
            if (size < 0) size = 0;
        }
        return total;
    }

    private static long estimateLinkedTraversalCost(int n, int index, int m) {
        long total = 0;
        int size = n;
        for (int i = 0; i < m; i++) {
            int idx = Math.min(index, size);
            total += Math.min(idx, Math.max(size - idx, 0));
        }
        return total;
    }

    private static void workload4_PriorityProcessing() throws IOException {
        System.out.println("    Workload 4: Priority Processing (Min-Heap)    ");
        try (PrintWriter w = csv("workload4_priority.csv",
                "n,avg_insert_time_ns,avg_extract_time_ns,comparisons,order_ok")) {
            for (int n : SIZES) {
                Random rnd = new Random(SEED);
                int[] values = new int[n];
                for (int i = 0; i < n; i++) values[i] = rnd.nextInt();

                double insertTime = 0;
                double extractTime = 0;
                long comparisons = 0;
                boolean orderOk = true;

                for (int rep = 0; rep < REPEATS; rep++) {
                    MinHeap heap = new MinHeap();

                    long startInsert = System.nanoTime();
                    for (int v : values) {
                        heap.insert(v);
                    }
                    insertTime += System.nanoTime() - startInsert;

                    long startExtract = System.nanoTime();
                    int prev = Integer.MIN_VALUE;
                    boolean okThisRep = true;
                    for (int i = 0; i < n; i++) {
                        int v = heap.extractMin();
                        if (v < prev) okThisRep = false;
                        prev = v;
                    }
                    extractTime += System.nanoTime() - startExtract;

                    if (rep == REPEATS - 1) {
                        comparisons = heap.comparisons;
                        orderOk = okThisRep;
                    }
                }
                insertTime /= REPEATS;
                extractTime /= REPEATS;

                w.println(n + "," + (long) insertTime + "," + (long) extractTime + "," + comparisons + "," + orderOk);
                System.out.printf("MinHeap n=%-7d insert=%,d ns  extract=%,d ns  comparisons=%,d  orderOk=%s%n",
                        n, (long) insertTime, (long) extractTime, comparisons, orderOk);
            }
        }
    }

    private static DynamicArray<Integer> buildDynamicArray(int n, long seed) {
        Random rnd = new Random(seed);
        DynamicArray<Integer> da = new DynamicArray<>(n);
        for (int i = 0; i < n; i++) da.add(rnd.nextInt());
        return da;
    }

    private static LinkedList<Integer> buildLinkedList(int n, long seed) {
        Random rnd = new Random(seed);
        LinkedList<Integer> ll = new LinkedList<>();
        for (int i = 0; i < n; i++) ll.add(rnd.nextInt());
        return ll;
    }

    private static int[] randomIndices(int n, int count, long seed) {
        Random rnd = new Random(seed + 100);
        int[] result = new int[count];
        for (int i = 0; i < count; i++) result[i] = rnd.nextInt(n);
        return result;
    }

    private static void consume(long value) {
        if (value == Long.MIN_VALUE) {
            System.out.println("unreachable: " + value);
        }
    }

    private static PrintWriter csv(String filename, String header) throws IOException {
        PrintWriter w = new PrintWriter(new FileWriter(OUT_DIR + filename));
        w.println(header);
        return w;
    }
}