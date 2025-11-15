package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTImporter;

import java.io.Reader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.nio.dot.DOTExporter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

public class GraphService {
    private final Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

    public void parseGraph(String filepath) {
        DOTImporter<String, DefaultEdge> importer = new DOTImporter<>();
        importer.setVertexFactory(label -> label); // use label as vertex id
        try (Reader r = Files.newBufferedReader(java.nio.file.Path.of(filepath))) {
            importer.importGraph(g, r);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DOT: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        List<String> nodes = new ArrayList<>(g.vertexSet());
        Collections.sort(nodes);

        List<String> edges = g.edgeSet().stream()
                .map(e -> g.getEdgeSource(e) + " -> " + g.getEdgeTarget(e))
                .sorted() // lexicographic: src then dst
                .collect(Collectors.toList());

        String nl = System.lineSeparator();
        return "nodes=" + nodes.size() + nl +
                "labels=" + String.join(",", nodes) + nl +
                "edges=" + edges.size() + nl +
                String.join(nl, edges) + nl;
    }

    public void outputGraph(String filepath) {
        try {
            Files.writeString(java.nio.file.Path.of(filepath), this.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write graph text: " + e.getMessage(), e);
        }
    }

    public boolean addNode(String label) {
        Objects.requireNonNull(label, "label");
        if (label.isEmpty()) return false;
        if (g.containsVertex(label)) return false;
        return g.addVertex(label);
    }

    public int addNodes(String[] labels) {
        if (labels == null) return 0;
        int added = 0;
        for (String l : labels) if (l != null && addNode(l)) added++;
        return added;
    }

    public boolean addEdge(String src, String dst) {
        Objects.requireNonNull(src); Objects.requireNonNull(dst);
        if (src.isEmpty() || dst.isEmpty()) return false;
        if (!g.containsVertex(src)) g.addVertex(src);
        if (!g.containsVertex(dst)) g.addVertex(dst);
        if (g.containsEdge(src, dst)) return false;
        return g.addEdge(src, dst) != null;
    }

    // === Removals (Part 2) ===

    /** Remove a single node (and any incident edges). Returns true if it existed. */
    public boolean removeNode(String label) {
        Objects.requireNonNull(label, "label");
        if (label.isEmpty()) return false;
        return g.removeVertex(label);
    }

    /** Remove multiple nodes; returns how many were actually removed. */
    public int removeNodes(String[] labels) {
        if (labels == null) return 0;
        int removed = 0;
        for (String l : labels) {
            if (l != null && !l.isEmpty() && g.removeVertex(l)) removed++;
        }
        return removed;
    }

    /** Remove exactly one edge src->dst. Returns true if the edge existed and was removed. */
    public boolean removeEdge(String src, String dst) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(dst, "dst");
        if (src.isEmpty() || dst.isEmpty()) return false;
        if (!g.containsVertex(src) || !g.containsVertex(dst)) return false;
        if (!g.containsEdge(src, dst)) return false;
        g.removeEdge(src, dst);
        return true;
    }

    /**
     * Finds a path from srcLabel to dstLabel using DFS.
     * Returns a Path if one exists, or null otherwise.
     */
    public Path GraphSearch(String srcLabel, String dstLabel) {
        // If either node is missing from the graph, no path can exist.
        if (!g.containsVertex(srcLabel) || !g.containsVertex(dstLabel)) {
            return null;
        }

        // Special case: src == dst
        if (srcLabel.equals(dstLabel)) {
            return new Path(Collections.singletonList(srcLabel));
        }

        // Depth-first search using an explicit stack
        Deque<String> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>(); // child -> parent

        visited.add(srcLabel);
        stack.push(srcLabel);

        boolean found = false;

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (current.equals(dstLabel)) {
                found = true;
                break;
            }

            // Explore neighbors (outgoing edges)
            for (DefaultEdge edge : g.outgoingEdgesOf(current)) {
                String neighbor = g.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        if (!found) {
            return null;
        }

        // Reconstruct path from dstLabel back to srcLabel using parent map
        List<String> pathNodes = new ArrayList<>();
        String current = dstLabel;
        while (current != null) {
            pathNodes.add(current);
            current = parent.get(current); // null when we reach srcLabel
        }

        Collections.reverse(pathNodes); // now src -> ... -> dst

        return new Path(pathNodes);
    }

    public void outputDOTGraph(String path) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        try (Writer w = Files.newBufferedWriter(java.nio.file.Path.of(path))) {
            exporter.exportGraph(g, w);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write DOT: " + e.getMessage(), e);
        }
    }

    /**
     * Render a graphic using Graphviz `dot`.
     * @param outPath output file path (e.g., "target/graph.png")
     * @param format  e.g., "png", "svg", "jpg"
     */
    public void outputGraphics(String outPath, String format) {
        Objects.requireNonNull(outPath, "outPath");
        Objects.requireNonNull(format, "format");
        if (format.isBlank()) throw new IllegalArgumentException("format must not be blank");

        try {
            // 1) write a temp DOT file
            java.nio.file.Path dotPath = Files.createTempFile("graph_", ".dot");
            outputDOTGraph(dotPath.toString());

            // 2) dot -T{format} dotPath -o outPath
            Process p = new ProcessBuilder("dot", "-T" + format, dotPath.toString(), "-o", outPath)
                    .redirectErrorStream(true)
                    .start();

            // 3) wait with a timeout and capture output if it fails
            boolean ok = p.waitFor(20, TimeUnit.SECONDS);
            if (!ok) {
                p.destroyForcibly();
                throw new RuntimeException("dot timed out");
            }
            int code = p.exitValue();
            if (code != 0) {
                String err = new String(p.getInputStream().readAllBytes()); // merged stdout/stderr
                throw new RuntimeException("dot exit " + code + ": " + err);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to render graphics: " + e.getMessage(), e);
        }
    }
}
