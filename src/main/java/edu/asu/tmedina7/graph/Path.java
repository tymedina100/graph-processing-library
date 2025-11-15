package edu.asu.tmedina7.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a path in the graph, e.g., n1 -> n2 -> n3.
 */
public class Path {

    private final List<String> nodes;

    public Path(List<String> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Path must contain at least one node");
        }
        this.nodes = new ArrayList<>(nodes);
    }

    /**
     * Returns an unmodifiable view of the nodes on this path.
     */
    public List<String> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    @Override
    public String toString() {
        return String.join(" -> ", nodes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;
        Path other = (Path) o;
        return nodes.equals(other.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }
}
