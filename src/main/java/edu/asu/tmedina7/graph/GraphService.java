package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTImporter;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
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
        try (Reader r = Files.newBufferedReader(Path.of(filepath))) {
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
            Files.writeString(Path.of(filepath), this.toString());
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

    public void outputDOTGraph(String path) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        try (Writer w = Files.newBufferedWriter(Path.of(path))) {
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
            Path dotPath = Files.createTempFile("graph_", ".dot");
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


