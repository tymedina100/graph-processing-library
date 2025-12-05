package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

final class RandomWalkSearch extends GraphSearchTemplate {
    private final Random rng = new Random();

    RandomWalkSearch(Graph<String, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected Deque<String> createFrontier() {
        return new ArrayDeque<>();
    }

    @Override
    protected void addToFrontier(Deque<String> frontier, String node) {
        frontier.push(node); // DFS-style backtracking, but randomized neighbor order
    }

    @Override
    protected String popFromFrontier(Deque<String> frontier) {
        return frontier.pop();
    }

    @Override
    protected List<String> neighborsOf(String current) {
        List<String> neighbors = super.neighborsOf(current);
        Collections.shuffle(neighbors, rng); // random next-neighbor choice
        return neighbors;
    }

    @Override
    protected void onVisit(Path currentPath) {
        System.out.println("visiting " + PathFormat.schemeA(currentPath));
    }
}
