package edu.asu.tmedina7.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public class Part3Demo {
    public static void main(String[] args) {
        System.out.println("Scheme A (prints full visited path history, then final Path)");

        GraphService svc = new GraphService();
        svc.parseGraph("src/main/resources/samples/demo_input.dot");

        System.out.println();
        System.out.println("BFS demo: a -> h");
        Path bfs = svc.GraphSearch("a", "h", Algorithm.BFS);
        System.out.println(PathFormat.schemeA(bfs));

        System.out.println();
        System.out.println("DFS demo: a -> h");
        Path dfs = svc.GraphSearch("a", "h", Algorithm.DFS);
        System.out.println(PathFormat.schemeA(dfs));

        System.out.println();
        System.out.println("Random Walk demo: run >= 5 times from a -> h and show >= 2 different successful paths");

        Set<String> distinctSuccesses = new LinkedHashSet<>();
        int attempt = 0;

        while (attempt < 5 || distinctSuccesses.size() < 2) {
            attempt++;
            System.out.println();
            System.out.println("random testing #" + attempt);

            Path rw = svc.GraphSearch("a", "h", Algorithm.RANDOM_WALK);
            String formatted = PathFormat.schemeA(rw);
            System.out.println(formatted);

            if (rw != null) distinctSuccesses.add(formatted);
        }
    }
}
