package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphServiceRemoveTest {

    private GraphService seeded() {
        GraphService s = new GraphService();
        // Nodes: A, B, C, E
        assertTrue(s.addNode("A"));
        assertTrue(s.addNode("B"));
        assertTrue(s.addNode("C"));
        assertTrue(s.addNode("E"));

        // Edges: A->B, B->C, A->C, C->E, E->A
        assertTrue(s.addEdge("A", "B"));
        assertTrue(s.addEdge("B", "C"));
        assertTrue(s.addEdge("A", "C"));
        assertTrue(s.addEdge("C", "E"));
        assertTrue(s.addEdge("E", "A"));
        return s;
    }

    @Test
    void removeEdge_thenRemoveNode_bulkRemove() {
        GraphService s = seeded();

        // removeEdge: existing → true, gone afterward
        assertTrue(s.removeEdge("A", "B"));
        assertFalse(s.removeEdge("A", "B")); // already removed

        // removeNode: removes vertex and all incident edges
        assertTrue(s.removeNode("E"));
        // E is gone; related edges must be gone as well via GraphService.toString() check
        String text = s.toString();
        assertFalse(text.contains("E -> A"));
        assertFalse(text.contains("C -> E"));
        assertFalse(text.contains("E")); // no label E among nodes

        // bulk remove: returns count actually removed
        int removed = s.removeNodes(new String[]{"A", "Z", "", null});
        assertEquals(1, removed, "Only A should be removed");

        // A is gone; A->C edge must be gone too
        String after = s.toString();
        assertFalse(after.contains("A"));
        assertFalse(after.contains("A -> C"));
    }

    @Test
    void removeNonexistentEdgeOrNode_returnsFalseOrZero() {
        GraphService s = seeded();

        // edge cases on removeEdge
        assertFalse(s.removeEdge("X", "Y"));   // missing both nodes
        assertFalse(s.removeEdge("A", "Z"));   // dst missing
        assertFalse(s.removeEdge("B", "A"));   // both exist, but edge doesn't

        // single node remove: nonexistent or invalid inputs
        assertFalse(s.removeNode("Z"));
        assertFalse(s.removeNode(""));         // you chose empty → false

        // bulk remove: mixture
        int removed = s.removeNodes(new String[]{"Z", "Z2", ""});
        assertEquals(0, removed);
    }

    @Test
    void idempotence_multipleRemovalsStable() {
        GraphService s = seeded();

        // remove node twice: first true, then false
        assertTrue(s.removeNode("C"));
        assertFalse(s.removeNode("C"));

        // remove edge twice: first true, then false
        assertTrue(s.removeEdge("E", "A"));
        assertFalse(s.removeEdge("E", "A"));

        // bulk remove with duplicates
        int removed = s.removeNodes(new String[]{"B", "B", "B"});
        assertEquals(1, removed);
    }
}
