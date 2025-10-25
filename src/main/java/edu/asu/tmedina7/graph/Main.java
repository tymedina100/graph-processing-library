package edu.asu.tmedina7.graph;

public class Main {
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "src/main/resources/samples/input.dot";
        GraphService gs = new GraphService();
        gs.parseGraph(path);
        System.out.println(gs.toString());
        gs.outputGraph("target/graph.txt");
        System.out.println("Wrote target/graph.txt");
    }
}
