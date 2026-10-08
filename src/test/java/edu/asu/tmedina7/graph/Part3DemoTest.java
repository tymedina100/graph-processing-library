package edu.asu.tmedina7.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class Part3DemoTest {
    @Test
    @Timeout(10)
    void demoLoadsItsSampleAndCompletesEverySearchSection() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.out;
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            Part3Demo.main(new String[0]);
        } finally {
            System.setOut(previous);
        }
        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("BFS demo: a -> h"));
        assertTrue(text.contains("DFS demo: a -> h"));
        assertTrue(text.contains("random testing #5"));
        assertTrue(text.contains("Canvas graph demo: a -> c"));
        assertTrue(text.contains("Node{h}"));
        assertFalse(text.contains("\nnull\n"));
    }
}
