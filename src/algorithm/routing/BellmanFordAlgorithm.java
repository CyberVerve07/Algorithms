
package algorithm.routing;

import java.util.*;

 class BellmanFordAlgorithm {

    static class Edge {
        int src, dest, weight;
        Edge(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }
    }

    public void findShortestPath(int numNodes, List<Edge> edges, int startNode) {
        int[] distances = new int[numNodes];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[startNode] = 0;

        // Relaxation step: V-1 times
        for (int i = 1; i < numNodes; ++i) {
            for (Edge edge : edges) {
                int u = edge.src;
                int v = edge.dest;
                int weight = edge.weight;
                if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                }
            }
        }

        // Negative cycle check
        for (Edge edge : edges) {
            if (distances[edge.src] != Integer.MAX_VALUE && distances[edge.src] + edge.weight < distances[edge.dest]) {
                System.out.println("Graph contains a negative weight cycle!");
                return;
            }
        }

        // Print distances
        System.out.println("Vertex \t\t Distance from Source");
        for (int i = 0; i < numNodes; ++i) {
            System.out.println(i + "\t\t" + (distances[i] == Integer.MAX_VALUE ? "Infinity" : distances[i]));
        }
    }

    public static void main(String[] args) {
        int numNodes = 5;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 6));
        edges.add(new Edge(0, 2, 7));
        edges.add(new Edge(1, 2, 8));
        edges.add(new Edge(1, 3, 5));
        edges.add(new Edge(1, 4, -4)); // Negative edge
        edges.add(new Edge(2, 3, -3)); // Negative edge
        edges.add(new Edge(2, 4, 9));
        edges.add(new Edge(3, 1, -2)); // Negative edge
        edges.add(new Edge(4, 0, 2));
        edges.add(new Edge(4, 3, 7));

        BellmanFordAlgorithm graph = new BellmanFordAlgorithm();
        graph.findShortestPath(numNodes, edges, 0);
    }
}
