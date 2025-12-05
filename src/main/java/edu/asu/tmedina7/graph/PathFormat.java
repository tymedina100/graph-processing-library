package edu.asu.tmedina7.graph;

import java.util.StringJoiner;

final class PathFormat {
    private PathFormat() {}

    static String schemeA(Path path) {
        if (path == null) return "null";

        String s = path.toString();
        if (s == null) return "Path{nodes=[]}";
        s = s.trim();
        if (s.isEmpty()) return "Path{nodes=[]}";

        String[] parts = s.split("\\s*->\\s*");
        StringJoiner sj = new StringJoiner(", ");
        for (String p : parts) {
            String label = p.trim();
            if (!label.isEmpty()) sj.add("Node{" + label + "}");
        }
        return "Path{nodes=[" + sj + "]}";
    }
}
