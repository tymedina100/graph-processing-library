package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class GraphService {
    private static final String FAILED_TO_WRITE_GRAPH_TEXT = "Failed to write graph text: ";
    private static final String EDGE_ARROW = " -> ";
    private final Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    private Graph<String, DefaultEdge> g() {
        return graph;
    }

    private final GraphIO io = new GraphIO(g());

    public void parseGraph(String dotFilePath) {
        io.parseGraph(dotFilePath);
    }

    @Override
    public String toString() {
        List<String> nodes = new ArrayList<>(g().vertexSet());
        Collections.sort(nodes);

        List<String> edges = g().edgeSet().stream()
                .map(edge -> g().getEdgeSource(edge) + EDGE_ARROW + g().getEdgeTarget(edge))
                .sorted() // lexicographic: src then dst
                .collect(Collectors.toList());

        String nl = System.lineSeparator();
        return "nodes=" + nodes.size() + nl +
                "labels=" + String.join(",", nodes) + nl +
                "edges=" + edges.size() + nl +
                String.join(nl, edges) + nl;
    }

    public void outputGraph(String outFilePath) {
        try {
            Files.writeString(java.nio.file.Path.of(outFilePath), this.toString());
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_WRITE_GRAPH_TEXT + e.getMessage(), e);
        }
    }

    public boolean addNode(String label) {
        Objects.requireNonNull(label, "label");
        if (label.isEmpty()) return false;
        if (g().containsVertex(label)) return false;
        return g().addVertex(label);
    }

    public int addNodes(String[] labels) {
        if (labels == null) return 0;
        int added = 0;
        for (String l : labels) if (l != null && addNode(l)) added++;
        return added;
    }

    public boolean addEdge(String srclabel, String dstLabel) {
        Objects.requireNonNull(srclabel); Objects.requireNonNull(dstLabel);
        if (srclabel.isEmpty() || dstLabel.isEmpty()) return false;
        if (!g().containsVertex(srclabel)) g().addVertex(srclabel);
        if (!g().containsVertex(dstLabel)) g().addVertex(dstLabel);
        if (g().containsEdge(srclabel, dstLabel)) return false;
        return g().addEdge(srclabel, dstLabel) != null;
    }

    // === Removals (Part 2) ===

    /** Remove a single node (and any incident edges). Returns true if it existed. */
    public boolean removeNode(String label) {
        Objects.requireNonNull(label, "label");
        if (label.isEmpty()) return false;
        return g().removeVertex(label);
    }

    /** Remove multiple nodes; returns how many were actually removed. */
    public int removeNodes(String[] labels) {
        if (labels == null) return 0;
        int removed = 0;
        for (String l : labels) {
            if (l != null && !l.isEmpty() && g().removeVertex(l)) removed++;
        }
        return removed;
    }

    /** Remove exactly one edge src->dst. Returns true if the edge existed and was removed. */
    public boolean removeEdge(String srcLabel, String dstLabel) {
        Objects.requireNonNull(srcLabel, "src");
        Objects.requireNonNull(dstLabel, "dst");
        if (srcLabel.isEmpty() || dstLabel.isEmpty()) return false;
        if (!g().containsVertex(srcLabel) || !g().containsVertex(dstLabel)) return false;
        if (!g().containsEdge(srcLabel, dstLabel)) return false;
        g().removeEdge(srcLabel, dstLabel);
        return true;
    }

    /**
     * Finds a path from srcLabel to dstLabel using the specified algorithm.
     * Returns a Path if one exists, or null otherwise.
     */
    public Path GraphSearch(String srcLabel, String dstLabel, edu.asu.tmedina7.graph.Algorithm algo) {
        Objects.requireNonNull(algo, "algo");
        GraphSearchStrategy strategy = searchStrategies.get(algo);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algo);
        }
        return strategy.search(srcLabel, dstLabel);
    }


    // --- internal helpers for BFS and DFS (Template Method pattern) ---

    private final GraphSearchTemplate bfsSearcher = new BfsSearch(g());
    private final GraphSearchTemplate dfsSearcher = new DfsSearch(g());

    private Path bfsSearch(String srcLabel, String dstLabel) {
        return bfsSearcher.search(srcLabel, dstLabel);
    }

    private Path dfsSearch(String srcLabel, String dstLabel) {
        return dfsSearcher.search(srcLabel, dstLabel);
    }

    private final Map<edu.asu.tmedina7.graph.Algorithm, GraphSearchStrategy> searchStrategies =
            Map.of(
                    edu.asu.tmedina7.graph.Algorithm.BFS, bfsSearcher,
                    edu.asu.tmedina7.graph.Algorithm.DFS, dfsSearcher
            );


    public void outputDOTGraph(String dotOutPath) {
        io.outputDOTGraph(dotOutPath);
    }

    /**
     * Render a graphic using Graphviz `dot`.
     * @param outPath output file path (e.g., "target/graph.png")
     * @param format  e.g., "png", "svg", "jpg"
     */
    public void outputGraphics(String outPath, String format) {
        io.outputGraphics(outPath, format);
    }
}
