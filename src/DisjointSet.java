/**
 * Mission 0. Disjoint set union (DSU).
 * Cells are numbered 0 .. n-1.
 * Union by size (the smaller tree goes under the larger one) and path compression in find.
 */
public class DisjointSet {

    private final int[] parent;
    private final int[] size;   // size[r] is the number of cells in the tree whose root is r
    private int sets;           // how many sets we have now

    public DisjointSet(int n) {
        parent = new int[n];
        size = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;   // every cell is its own root at the start
            size[i] = 1;
        }
    }

    /** The root of the set that contains x. */
    public int find(int x) {
        // first loop: go up to the root (no recursion, so a long chain is fine)
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
        }
        // second loop: path compression, every cell on the way now points straight to the root
        while (parent[x] != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    /** Joins the sets of a and b. Returns true if they were different sets, false if they were already one set. */
    public boolean union(int a, int b) {
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) {
            return false;   // already together
        }
        // the smaller tree goes under the larger one
        if (size[ra] < size[rb]) {
            int tmp = ra;
            ra = rb;
            rb = tmp;
        }
        parent[rb] = ra;
        size[ra] += size[rb];
        sets--;
        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    /** Number of sets now. */
    public int count() {
        return sets;
    }
}
