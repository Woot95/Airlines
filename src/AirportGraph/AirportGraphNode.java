package AirportGraph;

import Entities.Airport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AirportGraphNode {
    private Airport                      Airport;
    private AirportGraphNode             parent;
    private Set<AirportGraphNode>        children;
    private Map<AirportGraphNode, Float> edges;

    public AirportGraphNode(Airport value, AirportGraphNode parent) {
        this.Airport = value;
        this.parent = parent;

        this.children = new HashSet<>();
        this.edges = new HashMap<>();
    }

    public void addChild(AirportGraphNode child, Float weight) {
        this.children.add(child);
        this.edges.put(child, weight);
    }

    public Airport getValue() {
        return this.Airport;
    }

    public AirportGraphNode getParent(){
        return this.parent;
    }

    public Map<AirportGraphNode, Float> getEdges(){
        return this.edges;
    }
}
