package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayDeque;
import java.util.Deque;

final class BfsSearch extends GraphSearchTemplate {
    BfsSearch(Graph<String, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected Deque<String> createFrontier() {
        return new ArrayDeque<>();
    }

    @Override
    protected void addToFrontier(Deque<String> frontier, String node) {
        frontier.addLast(node); // queue enqueue
    }

    @Override
    protected String popFromFrontier(Deque<String> frontier) {
        return frontier.removeFirst(); // queue dequeue
    }
}
