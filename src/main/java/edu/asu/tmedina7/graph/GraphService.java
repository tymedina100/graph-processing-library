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
}


