package edu.asu.tmedina7.graph;

import java.util.List;
import java.util.Objects;

public final class Path {
    private final List<String> nodes;

    public Path(List<String> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("path must contain at least one node");
        }
        this.nodes = List.copyOf(nodes);
    }

    public List<String> nodes() { return nodes; }
    public String start() { return nodes.get(0); }
    public String end() { return nodes.get(nodes.size() - 1); }
    public int length() { return nodes.size(); }

    @Override public String toString() { return String.join(" -> ", nodes); }
    @Override public boolean equals(Object o){ return o instanceof Path p && nodes.equals(p.nodes); }
    @Override public int hashCode(){ return Objects.hash(nodes); }
}
