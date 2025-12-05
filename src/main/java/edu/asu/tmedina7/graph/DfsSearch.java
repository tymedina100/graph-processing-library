package edu.asu.tmedina7.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayDeque;
import java.util.Deque;

final class DfsSearch extends GraphSearchTemplate {
    DfsSearch(Graph<String, DefaultEdge> graph) {
        super(graph);
    }

    @Override
    protected Deque<String> createFrontier() {
        return new ArrayDeque<>();
    }

    @Override
    protected void addToFrontier(Deque<String> frontier, String node) {
        frontier.push(node); // stack push
    }

    @Override
    protected String popFromFrontier(Deque<String> frontier) {
        return frontier.pop(); // stack pop
    }
}
