import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        int total = w.a().length;
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] tmp = new int[total];
        int broken = 0;
        for (int k = 0; k < total; k++) {
            // union is true only when the two cells were in different sets -> break the wall
            if (dsu.union(w.a()[k], w.b()[k])) {
                tmp[broken++] = k;
            }
        }
        return Arrays.copyOf(tmp, broken);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int total = w.a().length;
        var cost = w.cost();

        // sort the wall indexes by cost
        Integer[] order = new Integer[total];
        for (int k = 0; k < total; k++) order[k] = k;
        Arrays.sort(order, (x, y) -> Long.compare(cost[x], cost[y]));

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] tmp = new int[total];
        int broken = 0;
        for (int i = 0; i < total; i++) {
            int k = order[i];
            if (dsu.union(w.a()[k], w.b()[k])) {
                tmp[broken++] = k;
            }
        }
        return Arrays.copyOf(tmp, broken);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int m = w.a().length;
        var cost = w.cost();

        // adjacency lists in arrays: edge 2k goes a->b, edge 2k+1 goes b->a, both have the cost of wall k
        int[] head = new int[n];
        Arrays.fill(head, -1);
        int[] next = new int[2 * m];
        int[] to = new int[2 * m];
        for (int k = 0; k < m; k++) {
            int a = w.a()[k];
            int b = w.b()[k];
            to[2 * k] = b;
            next[2 * k] = head[a];
            head[a] = 2 * k;
            to[2 * k + 1] = a;
            next[2 * k + 1] = head[b];
            head[b] = 2 * k + 1;
        }

        // queue holds {cost of the wall, cell we reach through it}, the cheapest on top
        PriorityQueue<long[]> pq = new PriorityQueue<>((x, y) -> Long.compare(x[0], y[0]));
        boolean[] inTree = new boolean[n];
        long total = 0;
        pq.add(new long[]{0, 0});   // start from cell 0 with cost 0

        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            int v = (int) top[1];
            if (inTree[v]) continue;   // old entry, the cell is already in the maze
            inTree[v] = true;
            total += top[0];
            for (int e = head[v]; e != -1; e = next[e]) {
                if (!inTree[to[e]]) {
                    pq.add(new long[]{cost[e / 2], to[e]});
                }
            }
        }
        return total;
    }
}
