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

        // ===== Feature 5: removeNode / removeNodes / removeEdge =====
        System.out.println("== Feature 5 ==");
        System.out.println("removeNode(\"X\"): " + gs.removeNode("X"));
        int removed = gs.removeNodes(new String[]{"Y", "missing", "Z", null, ""});
        System.out.println("removeNodes({Y,missing,Z,null,\"\"}) removed: " + removed);
        System.out.println("removeEdge(\"Y\",\"Z\"): " + gs.removeEdge("Y", "Z"));
        System.out.println("-- After F5 --");
        System.out.println(gs.toString());
        gs.outputGraph("target/graph_after_F5.txt");
        System.out.println("Wrote target/graph_after_F5.txt");

        // ===== Feature 6: GraphSearch with BFS / DFS =====
        System.out.println("== Feature 6 ==");
        // Use a small demo graph so BFS and DFS are easy to see
        GraphService demo = new GraphService();
        demo.addEdge("A", "B");
        demo.addEdge("B", "C");
        demo.addEdge("A", "D");
        demo.addEdge("D", "C");

        System.out.println("Demo graph edges: A->B, B->C, A->D, D->C");

        Path bfsPath = demo.GraphSearch("A", "C", Algorithm.BFS);
        Path dfsPath = demo.GraphSearch("A", "C", Algorithm.DFS);

        System.out.println("BFS path from A to C: " + bfsPath); // e.g., A -> B -> C
        System.out.println("DFS path from A to C: " + dfsPath); // e.g., A -> D -> C

        // Done
        System.out.println("All features executed.");
    }
}
