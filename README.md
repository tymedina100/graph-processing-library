# Graph Processing and Search Library

A Java 17 graph library developed for ASU CSE464. The project covers Parts 1–3: DOT parsing and export, graph editing, and reusable search algorithms using Strategy and Template Method patterns. This repository retains the original coursework history and collaborator contributions.

## Capabilities

- Parse directed DOT graphs with JGraphT and produce stable text summaries.
- Add and remove nodes/edges, deduplicate additions, and create missing vertices.
- Export DOT and render PNG through Graphviz.
- Search with breadth-first search, depth-first search, or randomized DFS-style traversal (`RANDOM_WALK`).
- Reuse `GraphSearchStrategy` and `GraphSearchTemplate` with algorithm-specific frontier and neighbor ordering.
- Format search paths and show visited-path history in the Part 3 demo.

## Build and test

Requires Java 17+, Maven 3.9+, and Graphviz (`dot`) for PNG rendering.

```bash
git clone https://github.com/tymedina100/graph-processing-library.git
cd graph-processing-library
mvn clean verify
```

GitHub Actions runs Maven validation on Ubuntu and Windows with JDK 17. JUnit tests cover graph parsing/editing, removal, BFS, DFS, and the Part 3 demo.

## Run the demos

```bash
mvn -q exec:java -Dexec.mainClass=edu.asu.tmedina7.graph.Main
mvn -q exec:java -Dexec.mainClass=edu.asu.tmedina7.graph.Part3Demo
```

Run from the repository root. `Main` writes `target/graph.txt`, `target/graph.dot`, and `target/graph.png`. `Part3Demo` loads `src/main/resources/samples/demo_input.dot`, demonstrates lowercase `a` → `h` searches and `a` → `c`, and attempts at least five randomized traversals. The randomized demo stops after 100 attempts if it has not observed two distinct successful paths; results may vary between runs.

## Source layout

- `GraphService` exposes graph operations and algorithm selection.
- `GraphIO` handles parsing and export.
- `BfsSearch`, `DfsSearch`, and `RandomWalkSearch` implement search strategies.
- `Path` and `PathFormat` represent and display results.
- `src/test/` contains JUnit tests and expected-output fixtures.

The original [Parts 1–2 write-up](README.pdf) and [Part 3 write-up](Project%20Part%203%20%E2%80%94%20README.pdf) remain available.

![Graph parsing and export](docs/images/features1-4.png)
![Graph removal and search](docs/images/features5-6.png)
