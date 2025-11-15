package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GraphServiceDfsTest {

    @Test
    void findsPathInSimpleLinearGraph() {
        GraphService service = new GraphService();

        service.addNode("A");
        service.addNode("B");
        service.addNode("C");
        service.addEdge("A", "B");
        service.addEdge("B", "C");

        Path path = service.GraphSearch("A", "C", Algorithm.DFS);

        assertNotNull(path);
        assertEquals(Arrays.asList("A", "B", "C"), path.getNodes());
    }

    @Test
    void returnsNullWhenNoPathExists() {
        GraphService service = new GraphService();

        service.addNode("A");
        service.addNode("B");
        service.addNode("C");
        service.addEdge("A", "B"); // no edge from B to C or C to B

        Path path = service.GraphSearch("A", "C", Algorithm.DFS);

        assertNull(path);
    }

    @Test
    void returnsSingleNodePathWhenSrcEqualsDst() {
        GraphService service = new GraphService();

        service.addNode("A");

        Path path = service.GraphSearch("A", "A", Algorithm.DFS);

        assertNotNull(path);
        assertEquals(Arrays.asList("A"), path.getNodes());
    }

    @Test
    void returnsNullIfEitherNodeDoesNotExist() {
        GraphService service = new GraphService();

        service.addNode("A");
        service.addNode("B");
        service.addEdge("A", "B");

        assertNull(service.GraphSearch("A", "C", Algorithm.DFS)); // dst missing
        assertNull(service.GraphSearch("X", "B", Algorithm.DFS)); // src missing
    }
}
