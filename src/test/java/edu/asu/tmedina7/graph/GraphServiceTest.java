package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class GraphServiceTest {
    @Test
    void feature1_parse_and_text_output() throws Exception {
        GraphService gs = new GraphService();
        gs.parseGraph("src/main/resources/samples/input.dot");

        String expected = Files.readString(Path.of("src/test/resources/samples/expected.txt"));
        assertEquals(expected, gs.toString());

        Path out = Path.of("target/graph.txt");
        gs.outputGraph(out.toString());
        assertTrue(Files.exists(out));
        assertTrue(Files.size(out) > 0);
    }

    @Test
    void feature2_add_nodes() {
        GraphService gs = new GraphService();
        assertTrue(gs.addNode("X"));
        assertFalse(gs.addNode("X"));
        assertEquals(2, gs.addNodes(new String[]{"A","B","A",null,""}));
    }

    @Test
    void feature3_add_edges_dedupe() {
        GraphService gs = new GraphService();
        gs.addNode("A"); gs.addNode("B");
        assertTrue(gs.addEdge("A","B"));   // first time
        assertFalse(gs.addEdge("A","B"));  // duplicate
        assertTrue(gs.addEdge("B","A"));   // new, opposite direction
        assertTrue(gs.addEdge("X","Y"));   // auto-add missing vertices
    }

    @Test
    void feature4_dot_and_png() throws Exception {
        GraphService gs = new GraphService();
        gs.parseGraph("src/main/resources/samples/input.dot");

        Path dotOut = Path.of("target", "test-graph.dot");
        gs.outputDOTGraph(dotOut.toString());
        assertTrue(Files.exists(dotOut));
        assertTrue(Files.size(dotOut) > 0);

        Path pngOut = Path.of("target", "test-graph.png");
        gs.outputGraphics(pngOut.toString(), "png");
        assertTrue(Files.exists(pngOut));
        assertTrue(Files.size(pngOut) > 0);
    }
}
