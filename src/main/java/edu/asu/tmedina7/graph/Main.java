package edu.asu.tmedina7.graph;

public class Main {
    public static void main(String[] args) {
        // Input DOT (arg0 optional)
        String in = args.length > 0 ? args[0] : "src/main/resources/samples/input.dot";

        GraphService gs = new GraphService();

        // ===== Feature 1: parse -> print -> write text =====
        gs.parseGraph(in);
        System.out.println("== Feature 1 ==");
        System.out.println(gs.toString());
        gs.outputGraph("target/graph.txt");
        System.out.println("Wrote target/graph.txt");

        // ===== Feature 2: addNode / addNodes (de-dupe, skip null/empty) =====
        System.out.println("== Feature 2 ==");
        System.out.println("addNode(\"X\"): " + gs.addNode("X"));
        int added = gs.addNodes(new String[]{"Y","Z","Y",null,""});
        System.out.println("addNodes({Y,Z,Y,null,\"\")}) added: " + added);

        // ===== Feature 3: addEdge (de-dupe, auto-create vertices) =====
        System.out.println("== Feature 3 ==");
        System.out.println("addEdge(\"X\",\"Y\"): " + gs.addEdge("X","Y")); // true
        System.out.println("addEdge(\"X\",\"Y\"): " + gs.addEdge("X","Y")); // false (dup)
        System.out.println("addEdge(\"Y\",\"Z\"): " + gs.addEdge("Y","Z")); // true
        System.out.println("addEdge(\"Z\",\"Z\"): " + gs.addEdge("Z","Z")); // self-loop allowed

        // Show updated summary after F2+F3
        System.out.println("-- After F2+F3 --");
        System.out.println(gs.toString());
        gs.outputGraph("target/graph_after_F3.txt");
        System.out.println("Wrote target/graph_after_F3.txt");

        // ===== Feature 4: export DOT + render PNG via Graphviz =====
        System.out.println("== Feature 4 ==");
        gs.outputDOTGraph("target/graph.dot");
        System.out.println("Wrote target/graph.dot");
        gs.outputGraphics("target/graph.png", "png");
        System.out.println("Wrote target/graph.png");

        // Done
        System.out.println("All features executed.");
    }
}
