package algorithm.routing;

import java.util.*;

class BFSShortestPath {

    // Graph ko represent karne ke liye Adjacency List
    private Map<Integer, List<Integer>> adjList;
    private int numNodes;

    public BFSShortestPath(int numNodes) {
        this.numNodes = numNodes;
        this.adjList = new HashMap<>();
        for (int i = 0; i < numNodes; i++) {
            adjList.put(i, new LinkedList<>());
        }
    }

    // Edge add karne ke liye function
    public void addEdge(int source, int destination) {
        adjList.get(source).add(destination);
        adjList.get(destination).add(source); // Undirected graph ke liye
    }

    // BFS shortest path finder
    public void findShortestPath(int startNode, int endNode) {
        boolean[] visited = new boolean[numNodes];
        int[] parent = new int[numNodes]; // Path track karne ke liye
        Queue<Integer> queue = new LinkedList<>();

        visited[startNode] = true;
        queue.add(startNode);
        parent[startNode] = -1; // Start node ka koi parent nahi

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();

            if (currentNode == endNode) break; // Destination mil gaya, loop end karo

            for (int neighbor : adjList.get(currentNode)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = currentNode;
                    queue.add(neighbor);
                }
            }
        }

        // Path reconstruct karo
        if (!visited[endNode]) {
            System.out.println("Koi path nahi mila " + startNode + " se " + endNode + " tak.");
            return;
        }

        System.out.print("Shortest Path: ");
        int current = endNode;
        List<Integer> path = new ArrayList<>();
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        System.out.println(path);
        System.out.println("Path Length: " + (path.size() - 1));
    }

    public static void main(String[] args) {
        BFSShortestPath graph = new BFSShortestPath(8);
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(3, 4);
        graph.addEdge(3, 7);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(5, 6);
        graph.addEdge(6, 7);

        graph.findShortestPath(0, 6); // 0 se 6 ka path
    }
}
