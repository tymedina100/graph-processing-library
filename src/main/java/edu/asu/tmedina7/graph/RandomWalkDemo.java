package edu.asu.tmedina7.graph;

public class RandomWalkDemo {
    public static void main(String[] args) {
        final String dotPath = "src/main/resources/samples/demo_input.dot";

        for (int i = 1; i <= 5; i++) {
            System.out.println("random testing #" + i);

            GraphService gs = new GraphService();     // new RNG seed each run
            gs.parseGraph(dotPath);

            Path p = gs.GraphSearch("a", "h", Algorithm.RANDOM_WALK);
            System.out.println("a -> h: " + p);
        }
    }
}
