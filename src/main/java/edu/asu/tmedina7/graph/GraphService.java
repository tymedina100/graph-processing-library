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
    private final Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    public void parseGraph(String dotFilePath) {
        DOTImporter<String, DefaultEdge> importer = new DOTImporter<>();
        importer.setVertexFactory(label -> label); // use label as vertex id
        try (Reader reader = Files.newBufferedReader(java.nio.file.Path.of(dotFilePath))) {
            importer.importGraph(graph, reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse DOT: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        List<String> nodes = new ArrayList<>(graph.vertexSet());
        Collections.sort(nodes);

        List<String> edges = graph.edgeSet().stream()
                .map(edge -> graph.getEdgeSource(edge) + " -> " + graph.getEdgeTarget(edge))
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
            throw new RuntimeException("Failed to write graph text: " + e.getMessage(), e);
        }
    }

    public boolean addNode(String label) {
        Objects.requireNonNull(label, "label");
        if (label.isEmpty()) return false;
        if (graph.containsVertex(label)) return false;
        return graph.addVertex(label);
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
        if (!graph.containsVertex(srclabel)) graph.addVertex(srclabel);
        if (!graph.containsVertex(dstLabel)) graph.addVertex(dstLabel);
        if (graph.containsEdge(srclabel, dstLabel)) return false;
        return graph.addEdge(srclabel, dstLabel) != null;
    }

    // === Removals (Part 2) ===

    /** Remove a single node (and any incident edges). Returns true if it existed. */
    public boolean removeNode(String label) {
        Objects.requireNonNull(label, "label");
        if (label.isEmpty()) return false;
        return graph.removeVertex(label);
    }

    /** Remove multiple nodes; returns how many were actually removed. */
    public int removeNodes(String[] labels) {
        if (labels == null) return 0;
        int removed = 0;
        for (String l : labels) {
            if (l != null && !l.isEmpty() && graph.removeVertex(l)) removed++;
        }
        return removed;
    }

    /** Remove exactly one edge src->dst. Returns true if the edge existed and was removed. */
    public boolean removeEdge(String srcLabel, String dstLabel) {
        Objects.requireNonNull(srcLabel, "src");
        Objects.requireNonNull(dstLabel, "dst");
        if (srcLabel.isEmpty() || dstLabel.isEmpty()) return false;
        if (!graph.containsVertex(srcLabel) || !graph.containsVertex(dstLabel)) return false;
        if (!graph.containsEdge(srcLabel, dstLabel)) return false;
        graph.removeEdge(srcLabel, dstLabel);
        return true;
    }

    /**
     * Finds a path from srcLabel to dstLabel using the specified algorithm.
     * Returns a Path if one exists, or null otherwise.
     */
    public Path GraphSearch(String srcLabel, String dstLabel, edu.asu.tmedina7.graph.Algorithm algo) {
        Objects.requireNonNull(algo, "algo");
        switch (algo) {
            case BFS:
                return bfsSearch(srcLabel, dstLabel);
            case DFS:
                return dfsSearch(srcLabel, dstLabel);
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algo);
        }
    }

    // --- internal helpers for BFS and DFS ---

    private Path bfsSearch(String srcLabel, String dstLabel) {
        // If either node is missing from the graph, no path can exist.
        if (canSearch(srcLabel, dstLabel)) return null;

        // Special case: src == dst
        if (srcLabel.equals(dstLabel)) {
            return new Path(Collections.singletonList(srcLabel));
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>(); // child -> parent

        visited.add(srcLabel);
        queue.add(srcLabel);

        boolean pathFound = false;

        while (!queue.isEmpty()) {
            String current = queue.remove();

            if (current.equals(dstLabel)) {
                pathFound = true;
                break;
            }

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                String neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!pathFound) {
            return null;
        }

        return buildPathFromParents(dstLabel, parent);
    }

    private static Path buildPathFromParents(String dstLabel, Map<String, String> parent) {
        List<String> pathNodes = new ArrayList<>();
        String current = dstLabel;
        while (current != null) {
            pathNodes.add(current);
            current = parent.get(current);
        }

        Collections.reverse(pathNodes);
        return new Path(pathNodes);
    }

    private boolean canSearch(String srcLabel, String dstLabel) {
        if (!graph.containsVertex(srcLabel) || !graph.containsVertex(dstLabel)) {
            return true;
        }
        return false;
    }

    private Path dfsSearch(String srcLabel, String dstLabel) {
        if (canSearch(srcLabel, dstLabel)) return null;

        if (srcLabel.equals(dstLabel)) {
            return new Path(Collections.singletonList(srcLabel));
        }

        Deque<String> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        visited.add(srcLabel);
        stack.push(srcLabel);

        boolean found = false;

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (current.equals(dstLabel)) {
                found = true;
                break;
            }

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                String neighbor = graph.getEdgeTarget(edge);
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

        return buildPathFromParents(dstLabel, parent);
    }

    public void outputDOTGraph(String dotOutPath) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        try (Writer w = Files.newBufferedWriter(java.nio.file.Path.of(dotOutPath))) {
            exporter.exportGraph(graph, w);
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
            boolean finished = p.waitFor(20, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                throw new RuntimeException("dot timed out");
            }
            int exitCode = p.exitValue();
            if (exitCode != 0) {
                String output = new String(p.getInputStream().readAllBytes()); // merged stdout/stderr
                throw new RuntimeException("dot exit " + exitCode + ": " + output);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to render graphics: " + e.getMessage(), e);
        }
    }
}
