package algorithm.routing;

import java.util.*;

public class AStarAlgorithm {
    static class Node implements Comparable<Node> {
        public String value;
        public double g;
        public double h;
        public Node parent;
        public Node(String value, double h) {
            this.value = value;
            this.h = h;
        }
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.g + this.h, other.g + other.h);
        }
    }

    public static void main(String[] args) {
        System.out.println("A* Algorithm functionality can be implemented here.");
    }
}
