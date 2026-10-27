/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Zones are numbered in the order in which the second pass finds them (a topological order of the zone graph).
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);

        // ---- pass 1: DFS on the slides, remember the cells in the order they are finished ----
        boolean[] seen = new boolean[n];
        int[] pos = new int[n];        // how many slides of this cell we have already looked at
        int[] finished = new int[n];
        int fcount = 0;
        int[] stack = new int[n];
        int sp = 0;

        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            seen[start] = true;
            stack[sp++] = start;
            while (sp > 0) {
                int u = stack[sp - 1];
                if (pos[u] < out[u].length) {
                    int slide = out[u][pos[u]++];
                    int v = s.to()[slide];
                    if (!seen[v]) {
                        seen[v] = true;
                        stack[sp++] = v;
                    }
                } else {
                    sp--;                     // u is finished
                    finished[fcount++] = u;
                }
            }
        }

        // ---- pass 2: from the last finished cell to the first, DFS on the reversed slides ----
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf, -1);
        int zones = 0;
        sp = 0;
        for (int i = n - 1; i >= 0; i--) {
            int c = finished[i];
            if (zoneOf[c] != -1) continue;   // already in a zone
            zoneOf[c] = zones;
            stack[sp++] = c;
            while (sp > 0) {
                int u = stack[--sp];
                for (int slide : in[u]) {
                    int v = s.from()[slide];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zones;
                        stack[sp++] = v;
                    }
                }
            }
            zones++;   // this DFS found one whole zone
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);

        // put the cells into groups by zone (counting sort): cells of zone z are cells[first[z] .. first[z+1]-1]
        int[] first = new int[zoneCount + 1];
        for (int c = 0; c < n; c++) first[zoneOf[c] + 1]++;
        for (int z = 0; z < zoneCount; z++) first[z + 1] += first[z];
        int[] fill = java.util.Arrays.copyOf(first, zoneCount);
        int[] cells = new int[n];
        for (int c = 0; c < n; c++) cells[fill[zoneOf[c]]++] = c;

        long INF = Long.MAX_VALUE / 4;
        long[] chain = new long[zoneCount];   // number of zones on the longest chain that ends in this zone
        long[] dist = new long[zoneCount];    // cheapest cost from the zone of cell 0
        java.util.Arrays.fill(chain, 1);
        java.util.Arrays.fill(dist, INF);
        dist[zoneOf[0]] = 0;

        long longest = 1;
        // zones are numbered in topological order, so when we reach zone z
        // every slide that enters z has already been looked at: chain[z] and dist[z] are final
        for (int z = 0; z < zoneCount; z++) {
            if (chain[z] > longest) longest = chain[z];
            for (int i = first[z]; i < first[z + 1]; i++) {
                int u = cells[i];
                for (int slide : out[u]) {
                    int y = zoneOf[s.to()[slide]];
                    if (y == z) continue;   // a slide inside the zone is free and changes nothing
                    if (chain[z] + 1 > chain[y]) chain[y] = chain[z] + 1;
                    if (dist[z] < INF) {
                        long t = dist[z] + s.time()[slide];
                        if (t < dist[y]) dist[y] = t;
                    }
                }
            }
        }
        return new long[]{longest, dist[zoneOf[n - 1]]};
    }
}
