package Graph;

import DatabaseHandler.DatabaseHandler;
import Entities.Airport;
import Entities.RouteList;

import java.util.*;

public class AirportGraph {
    private Map<String, AirportNode> graph;
    private DatabaseHandler          dbhandler = new DatabaseHandler();
    private RouteList                routeList;

    public AirportGraph() {
        graph = new HashMap<>();
        routeList = new RouteList();
        this.routeList = dbhandler.readAllRoutes(this);
    }

    public static void main(String[] args) {
        DatabaseHandler dbhandler = new DatabaseHandler();
        AirportGraph graph = new AirportGraph();
        Airport Vienna = dbhandler.getAirportByIATA("VIE");
        graph.buildSpanningTree(Vienna);

        Pathfinder pf = new Pathfinder(graph);
        WeightedPath wp = pf.findShortestPath("VIE", "SAW", false);
        System.out.println(wp);

        WeightedPath wp2 = pf.findShortestPath("VIE", "SAW", true);
        System.out.println(wp2);
    }

    public Map<String, AirportNode> getGraph() {
        return this.graph;
    }

    public RouteList getRouteList() {
        return this.routeList;
    }

    public void addAirportNode(AirportNode airportNode) {
        AirportNode airportNodeToAdd = AirportNode.getOrCreate(airportNode.getFromAirport(), this);
        graph.put(AirportNode.generateKey(airportNode.getFromAirport()), airportNodeToAdd);
    }

    public void buildSpanningTree(Airport from) {
        graph.clear();
        Set<String> visited = new HashSet<>();
        AirportNode current = AirportNode.getOrCreate(from, this);

        graph.putIfAbsent(current.getAirportNodeKey(), current);
        dfs(graph.get(current.getAirportNodeKey()), visited);
    }

    private void dfs(AirportNode current, Set<String> visited) {
        String currentKey = current.getAirportNodeKey();

        if (visited.contains(currentKey)) return;
        visited.add(currentKey);

        current.discoverNeighbors();

        for (AirportNode neighbor : current.getNeighbors().keySet()) {
            String neighborKey = neighbor.getAirportNodeKey();

            // Verbindung hinzufügen
            float weight = current.getNeighbors().get(neighbor);
            graph.putIfAbsent(currentKey, current);
            graph.get(currentKey).addNeighbor(neighbor.getFromAirport(), weight);

            // Rekursiver DFS-Aufruf
            if (!visited.contains(neighborKey)) {
                dfs(neighbor, visited);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AirportGraph:\n");

        for (Map.Entry<String, AirportNode> entry : graph.entrySet()) {
            AirportNode node = entry.getValue();

            sb.append("  ").append(entry.getKey()).append(" → ");

            if (node.getNeighbors().isEmpty()) {
                sb.append("keine Nachbarn");
            } else {
                for (Map.Entry<AirportNode, Float> neighbor : node.getNeighbors().entrySet()) {
                    sb.append(neighbor.getKey())
                        .append(" (")
                        .append(neighbor.getValue())
                        .append("), ");
                }
                sb.setLength(sb.length() - 2);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
