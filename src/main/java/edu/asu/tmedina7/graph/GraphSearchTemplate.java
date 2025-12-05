package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

abstract class GraphSearchTemplate {
    private final Graph<String, DefaultEdge> graph;

    GraphSearchTemplate(Graph<String, DefaultEdge> graph) {
        this.graph = graph;
    }

    // Template method
    final Path search(String srcLabel, String dstLabel) {
        if (!graph.containsVertex(srcLabel) || !graph.containsVertex(dstLabel)) {
            return null;
        }

        if (srcLabel.equals(dstLabel)) {
            return new Path(Collections.singletonList(srcLabel));
        }

        Deque<String> frontier = createFrontier();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>(); // child -> parent

        visited.add(srcLabel);
        addToFrontier(frontier, srcLabel);

        while (!frontier.isEmpty()) {
            String current = popFromFrontier(frontier);

            if (current.equals(dstLabel)) {
                return buildPathFromParents(dstLabel, parent);
            }

            for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
                String neighbor = graph.getEdgeTarget(edge);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    addToFrontier(frontier, neighbor);
                }
            }
        }

        return null;
    }

    protected abstract Deque<String> createFrontier();
    protected abstract void addToFrontier(Deque<String> frontier, String node);
    protected abstract String popFromFrontier(Deque<String> frontier);

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
}
