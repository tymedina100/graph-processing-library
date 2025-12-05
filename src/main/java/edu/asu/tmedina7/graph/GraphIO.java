package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class GraphIO {
    private static final String FAILED_TO_PARSE_DOT = "Failed to parse DOT: ";
    private static final String FAILED_TO_WRITE_DOT = "Failed to write DOT: ";
    private static final String FAILED_TO_RENDER_GRAPHICS = "Failed to render graphics: ";
    private static final String DOT_TIMED_OUT = "dot timed out";
    private static final int DOT_TIMEOUT_SECONDS = 20;
    private static final String DOT_CMD = "dot";
    private static final String DOT_PREFIX = "graph_";
    private static final String DOT_SUFFIX = ".dot";
    private static final String ERR_FORMAT_BLANK = "format must not be blank";
    private static final String DOT_EXIT_PREFIX = "dot exit ";

    private final Graph<String, DefaultEdge> graph;

    GraphIO(Graph<String, DefaultEdge> graph) {
        this.graph = graph;
    }

    void parseGraph(String dotFilePath) {
        DOTImporter<String, DefaultEdge> importer = new DOTImporter<>();
        importer.setVertexFactory(label -> label);
        try (Reader reader = Files.newBufferedReader(java.nio.file.Path.of(dotFilePath))) {
            importer.importGraph(graph, reader);
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_PARSE_DOT + e.getMessage(), e);
        }
    }

    void outputDOTGraph(String dotOutPath) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        try (Writer w = Files.newBufferedWriter(java.nio.file.Path.of(dotOutPath))) {
            exporter.exportGraph(graph, w);
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_WRITE_DOT + e.getMessage(), e);
        }
    }

    void outputGraphics(String outPath, String format) {
        Objects.requireNonNull(outPath, "outPath");
        Objects.requireNonNull(format, "format");
        if (format.isBlank()) throw new IllegalArgumentException(ERR_FORMAT_BLANK);

        try {
            java.nio.file.Path dotPath = Files.createTempFile(DOT_PREFIX, DOT_SUFFIX);
            outputDOTGraph(dotPath.toString());

            Process p = new ProcessBuilder(DOT_CMD, "-T" + format, dotPath.toString(), "-o", outPath)
                    .redirectErrorStream(true)
                    .start();

            boolean finished = p.waitFor(DOT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                throw new RuntimeException(DOT_TIMED_OUT);
            }

            int exitCode = p.exitValue();
            if (exitCode != 0) {
                String output = new String(p.getInputStream().readAllBytes());
                throw new RuntimeException(DOT_EXIT_PREFIX + exitCode + ": " + output);
            }
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_RENDER_GRAPHICS + e.getMessage(), e);
        }
    }
}
