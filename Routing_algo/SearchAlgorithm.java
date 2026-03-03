package Algorithm;
import java.util.*;

 class AStarAlgorithm {
    // Node class ko modify karna padega heuristic ke liye
    static class Node {
        int id;
        int x, y; // Coordinates for heuristic calculation
        int gCost; // Cost from start
        int hCost; // Heuristic cost to end
        int fCost; // g + h
        Node parent;

        Node(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        void calculateFCost() {
            this.fCost = this.gCost + this.hCost;
        }

        @Override
        public boolean equals(Object o) { // Priority Queue ke liye zaroori
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return id == node.id;
        }
    }

    public List<Node> findPath(Node startNode, Node endNode, Map<Node, List<Node>> graph) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<Node> closedSet = new HashSet<>();

        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startNode, endNode);
        startNode.calculateFCost();
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            if (currentNode == endNode) {
                return reconstructPath(currentNode); // Path banao
            }

            closedSet.add(currentNode);

            for (Node neighbor : graph.get(currentNode)) {
                if (closedSet.contains(neighbor)) continue;

                int tentativeGCost = currentNode.gCost + getDistance(currentNode, neighbor);
                if (tentativeGCost < neighbor.gCost) {
                    neighbor.parent = currentNode;
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = calculateHeuristic(neighbor, endNode);
                    neighbor.calculateFCost();

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null; // No path found
    }

    // Simple Manhattan distance heuristic
    private int calculateHeuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private int getDistance(Node a, Node b) { // Example, graph se nikalna padega
        return 1; // Assume unit cost for simplicity
    }

    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
}

