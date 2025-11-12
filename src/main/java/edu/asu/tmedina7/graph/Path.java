package edu.asu.tmedina7.graph;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Path {
  private final List<String> nodes;
  public Path(List<String> nodes) {
    this.nodes = List.copyOf(Objects.requireNonNull(nodes, "nodes"));
  }
  public List<String> nodes() { return nodes; }
  @Override public String toString() { return String.join(" -> ", nodes); }
  public static Path of(List<String> nodes) { return new Path(nodes); }
}
