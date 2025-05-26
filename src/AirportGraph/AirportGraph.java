package AirportGraph;
import Entities.Airport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AirportGraph {
    Map<Integer, AirportGraphNode> graph;
    AirportGraphNode root;

    public AirportGraph(AirportGraphNode root) {
        this.graph = new HashMap<>();
        this.root = root;

        this.graph.put(root.getValue().getId(), root);
    }

    public AirportGraphNode findNode(AirportGraphNode nodeToFind){
        return this.graph.get(nodeToFind.getValue().getId());
    }

    public void addNode(AirportGraphNode nodeToAdd, Float weight){
        AirportGraphNode foundNode = this.findNode(nodeToAdd);

        if ( foundNode == null){
            nodeToAdd.getParent().addChild(nodeToAdd, weight);
        }else{
            nodeToAdd.getParent().addChild(foundNode, weight);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AirportGraph:\n");
        traverseGraph(this.root, sb, new HashSet<>(), 0);
        return sb.toString();
    }

    private void traverseGraph(AirportGraphNode node, StringBuilder sb, Set<AirportGraphNode> visited, int level) {
        if (node == null || visited.contains(node)) return;
        visited.add(node);

        // Indentierung je nach Tiefe im Graphen
        sb.append("  ".repeat(level))
            .append("- Node: ").append(node.getValue()).append("\n");

        // Edges ausgeben
        for (Map.Entry<AirportGraphNode, Float> edge : node.getEdges().entrySet()) {
            sb.append("  ".repeat(level + 1))
                .append("-> ").append(edge.getKey().getValue())
                .append(" (Weight: ").append(edge.getValue()).append(")\n");

            // Rekursiver Traversal
            traverseGraph(edge.getKey(), sb, visited, level + 2);
        }
    }


    public static void main(String[] args) {
        Airport testAirport = new Airport(1, 15f, 15f, "Test", "VIE");
        Airport testAirport2 = new Airport(2, 15f, 15f, "Test", "SAW");
        Airport testAirport3 = new Airport(3, 15f, 15f, "Test", "FUJ");
        Airport testAirport4 = new Airport(4, 15f, 15f, "Test", "XXX");

        AirportGraphNode root = new AirportGraphNode(testAirport, null);

        AirportGraphNode node1 = new AirportGraphNode(testAirport2, root);
        AirportGraphNode node2 = new AirportGraphNode(testAirport3, root);
        AirportGraphNode node3 = new AirportGraphNode(testAirport4, node2);
        AirportGraphNode node4 = new AirportGraphNode(testAirport2, node2);
        AirportGraphNode node5 = new AirportGraphNode(testAirport3, node4);

        AirportGraph graph = new AirportGraph(root);

        graph.addNode(node1,15f);
        graph.addNode(node2,15f);
        graph.addNode(node3,15f);
        graph.addNode(node4,15f);
        graph.addNode(node5,15f);

        System.out.println(graph.toString());
    }


}
