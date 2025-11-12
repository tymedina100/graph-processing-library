package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GraphSearchBfsTest {

  @Disabled("BFS not implemented yet; scaffolding only")
  @Test
  void findsPath_whenReachable() {
    GraphService s = new GraphService();
    s.addNode("A"); s.addNode("B"); s.addNode("C");
    s.addEdge("A","B"); s.addEdge("B","C");
    assertTrue(s.graphSearchBfs("A","C").isPresent());
  }

  @Disabled("BFS not implemented yet; scaffolding only")
  @Test
  void empty_whenUnreachable() {
    GraphService s = new GraphService();
    s.addNode("A"); s.addNode("B");
    // no edge between them
    assertTrue(s.graphSearchBfs("A","B").isEmpty());
  }
}
