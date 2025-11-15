package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphServiceDfsTest {

    @Test
    void dfsFindsPathWhenReachable() {
        GraphService svc = new GraphService();
        // Graph: A -> B, B -> C, A -> D, D -> E
        svc.addEdge("A", "B");
        svc.addEdge("B", "C");
        svc.addEdge("A", "D");
        svc.addEdge("D", "E");

        Path result = svc.graphSearch("A", "E");
        assertNotNull(result, "DFS should find a path from A to E");

        Path expected = new Path(List.of("A", "D", "E"));
        assertEquals(expected, result, "DFS path should follow A -> D -> E in this traversal");
    }

    @Test
    void dfsReturnsNullWhenUnreachable() {
        GraphService svc = new GraphService();
        // Two disconnected components: A-B and C-D
        svc.addEdge("A", "B");
        svc.addEdge("C", "D");

        Path p = svc.graphSearch("A", "D");
        assertNull(p, "DFS should return null when there is no path");
    }

    @Test
    void dfsReturnsNullWhenSourceOrDestinationMissing() {
        GraphService svc = new GraphService();
        svc.addEdge("A", "B");

        assertNull(svc.graphSearch("A", "Z"), "Missing destination should yield null");
        assertNull(svc.graphSearch("Z", "B"), "Missing source should yield null");
    }
}
