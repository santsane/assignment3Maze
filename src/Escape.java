/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The DFS uses its own stack (arrays), not recursion, so it does not overflow on a 1000 by 1000 maze.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int exit = n - 1;
        int cols = w.cols();
        int[][] g = Graphs.open(w, broken);

        // the four steps in the needed order: up, right, down, left
        int[] step = {-cols, 1, cols, -1};

        int[] parent = new int[n];     // from which cell we came into this cell
        int[] nextDir = new int[n];    // which of the 4 directions this cell tries next
        boolean[] seen = new boolean[n];
        int[] stack = new int[n];
        int sp = 0;

        seen[0] = true;
        int visited = 1;
        stack[sp++] = 0;

        while (sp > 0 && !seen[exit]) {
            int u = stack[sp - 1];
            if (nextDir[u] == 4) {
                sp--;              // all 4 directions tried, go back
                continue;
            }
            int v = u + step[nextDir[u]];
            nextDir[u]++;
            if (v < 0 || v >= n) continue;       // outside of the maze
            if (!contains(g[u], v)) continue;    // there is a wall (not broken)
            if (seen[v]) continue;
            seen[v] = true;                      // mark the cell
            visited++;
            parent[v] = u;
            stack[sp++] = v;
        }

        // build the path from the exit back to the start, then turn it around
        int len = 1;
        for (int c = exit; c != 0; c = parent[c]) len++;
        int[] path = new int[len];
        int c = exit;
        for (int i = len - 1; i >= 0; i--) {
            path[i] = c;
            c = parent[c];
        }

        int deadEnds = 0;
        for (int i = 0; i < n; i++) {
            if (g[i].length == 1) deadEnds++;
        }
        return new Result(path, visited, deadEnds);
    }

    /** true if v is in the (short) list of open neighbours */
    private static boolean contains(int[] list, int v) {
        for (int x : list) {
            if (x == v) return true;
        }
        return false;
    }
}
