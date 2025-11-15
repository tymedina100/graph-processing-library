package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class GraphServiceBfsTest {

    private GraphService seeded() {
        GraphService s = new GraphService();
        assertTrue(s.addNode("A"));
        assertTrue(s.addNode("B"));
        assertTrue(s.addNode("C"));
        assertTrue(s.addNode("E"));
        assertTrue(s.addEdge("A","B"));
        assertTrue(s.addEdge("B","C"));
        assertTrue(s.addEdge("A","C")); // makes A->C shortest to C
        assertTrue(s.addEdge("C","E"));
        assertTrue(s.addEdge("E","A"));
        return s;
    }

    @Test
    void bfsFindsShortestHop() {
        GraphService s = seeded();
        Path p = s.graphSearchBFS("A","C");
        assertNotNull(p);
        assertEquals(2, p.length());
        assertEquals("A", p.start());
        assertEquals("C", p.end());
    }

    @Test
    void bfsWorksThroughCycle() {
        GraphService s = seeded();
        Path p = s.graphSearchBFS("E","C"); // E->A->C
        assertNotNull(p);
        assertEquals(3, p.length());
    }

    @Test
    void bfsNullWhenUnreachableOrMissing() {
        GraphService s = seeded();
        assertTrue(s.removeEdge("A","C"));
        assertTrue(s.removeEdge("A","B"));
        assertTrue(s.removeEdge("E","A"));
        assertNull(s.graphSearchBFS("E","C"));
        assertNull(s.graphSearchBFS("X","C"));
        assertNull(s.graphSearchBFS("A","Z"));
    }

    @Test
    void bfsSingletonWhenSameNode() {
        GraphService s = seeded();
        Path p = s.graphSearchBFS("A","A");
        assertNotNull(p);
        assertEquals(1, p.length());
        assertEquals("A", p.start());
    }
}
