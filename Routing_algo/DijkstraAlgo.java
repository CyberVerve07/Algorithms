package algorithm;
import java.util.*;
public class DijkstraAlgo {

    private int numNodes;
    private Map<Integer, List<Edge>> adjList;

    public DijkstraAlgo(int numNodes) {
        this.numNodes = numNodes;
        this.adjList = new HashMap<>();
        for (int i = 0; i < numNodes; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination, int weight) {
        adjList.get(source).add(new Edge(destination, weight));
        adjList.get(destination).add(new Edge(source, weight)); // Undirected
    }

    static class Edge {
        int destination;
        int weight;
        Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    public void findShortestPath(int startNode, int endNode) {
        int[] distances = new int[numNodes];
        int[] parent = new int[numNodes];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        distances[startNode] = 0;

        // Priority Queue: (distance, node)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, startNode});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentNode = current[1];
            int currentDistance = current[0];

            if (currentNode == endNode) break;

            if (currentDistance > distances[currentNode]) continue; // Outdated entry

            for (Edge neighbor : adjList.get(currentNode)) {
                int newDist = currentDistance + neighbor.weight;
                if (newDist < distances[neighbor.destination]) {
                    distances[neighbor.destination] = newDist;
                    parent[neighbor.destination] = currentNode;
                    pq.add(new int[]{newDist, neighbor.destination});
                }
            }
        }

        // Path print karna - BFS wala code se same hai
        if (distances[endNode] == Integer.MAX_VALUE) {
            System.out.println("Koi path nahi mila.");
            return;
        }
        System.out.print("Shortest Path: ");
        List<Integer> path = new ArrayList<>();
        int current = endNode;
        while(current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        System.out.println(path);
        System.out.println("Total Weight: " + distances[endNode]);
    }

    public static void main(String[] args) {
        DijkstraAlgo graph = new DijkstraAlgo(6);
        graph.addEdge(0, 1, 7);
        graph.addEdge(0, 2, 9);
        graph.addEdge(0, 5, 14);
        graph.addEdge(1, 2, 10);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 11);
        graph.addEdge(2, 5, 2);
        graph.addEdge(3, 4, 6);
        graph.addEdge(4, 5, 9);

        graph.findShortestPath(0, 4);
    }
}
