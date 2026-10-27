/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // my own checks

        // union of a cell with itself: same set, returns false, count does not change
        DisjointSet s = new DisjointSet(5);
        check(!s.union(3, 3), "union(3, 3) returns false");
        check(s.count() == 5, "count is still 5 after union(3, 3)");
        check(s.connected(3, 3), "a cell is connected with itself");

        // a union of two sets that are already one set (through other cells)
        s.union(0, 1); s.union(1, 2);
        check(!s.union(0, 2), "union(0, 2) is false, they are already together");
        check(s.count() == 3, "3 sets: {0,1,2} {3} {4}");

        // a large chain: 0-1, 1-2, 2-3, ... Must not overflow the stack
        int n = 1_000_000;
        DisjointSet big = new DisjointSet(n);
        boolean allTrue = true;
        for (int i = 0; i + 1 < n; i++) {
            if (!big.union(i, i + 1)) allTrue = false;
        }
        check(allTrue, "every union of the big chain returned true");
        check(big.count() == 1, "1 set after the big chain");
        check(big.connected(0, n - 1), "both ends of the big chain are connected");
        check(big.find(0) == big.find(n - 1) && big.find(12345) == big.find(0), "all cells have one root");

        // the chain built the other way round (the new cell is always the bigger set's neighbour)
        DisjointSet rev = new DisjointSet(n);
        for (int i = n - 1; i > 0; i--) rev.union(i, i - 1);
        check(rev.count() == 1 && rev.connected(0, n - 1), "reverse chain works too");

        // count after many unions: join cells in pairs, then join the pairs
        DisjointSet many = new DisjointSet(1000);
        for (int i = 0; i < 1000; i += 2) many.union(i, i + 1);
        check(many.count() == 500, "500 sets after joining 500 pairs");
        for (int i = 0; i < 1000; i += 4) many.union(i, i + 2);
        check(many.count() == 250, "250 sets after joining the pairs in fours");
        check(many.connected(0, 3) && !many.connected(3, 4), "0 and 3 together, 3 and 4 apart");

        // find twice gives the same root (path compression must not break anything)
        int r1 = many.find(999);
        int r2 = many.find(999);
        check(r1 == r2, "find gives the same root every time");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
