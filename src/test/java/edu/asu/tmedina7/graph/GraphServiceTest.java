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
}
