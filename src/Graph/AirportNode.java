package Graph;

import Entities.Airport;
import Entities.Route;
import Entities.RouteList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AirportNode {
    private static final Map<String, AirportNode> nodeRegistry = new ConcurrentHashMap<>();

    private AirportGraph            parent;
    private Airport                 fromAiport;
    private Map<AirportNode, Float> neighbors;
    private RouteList               neighborRoutes;

    private AirportNode(Airport fromAirport, AirportGraph parent) {
        this.fromAiport = fromAirport;
        this.parent = parent;
        neighbors = new HashMap<>();
        neighborRoutes = new RouteList();
    }

    public static AirportNode getOrCreate(Airport fromAirport, AirportGraph parent) {
        String key = generateKey(fromAirport);
        return nodeRegistry.computeIfAbsent(key, k -> new AirportNode(fromAirport, parent));
    }

    public static AirportNode getOrCreate(Route r, AirportGraph parent) {
        String key = generateKey(r.getFromAirport());
        return nodeRegistry.computeIfAbsent(key, k -> new AirportNode(r.getFromAirport(), parent));
    }

    public static String generateKey(Airport airport) {
        return airport.getIATA();
    }

    public String getAirportNodeKey() {
        return generateKey(fromAiport);
    }

    public void discoverNeighbors() {
        this.neighborRoutes.clear();
        RouteList routes = parent.getRouteList().findRoutesBySource(this.fromAiport);
        this.neighborRoutes.addAll(routes);

        for (Route r : this.neighborRoutes) {
            this.addNeighbor(r.getToAirport(), r.getWeight());
        }
    }

    public void addNeighbor(Airport neighborAirport, float weight) {
        neighbors.putIfAbsent(getOrCreate(neighborAirport, this.parent), weight);
    }

    public Airport getFromAirport() {
        return this.fromAiport;
    }

    public RouteList getNeighborRoutes() {
        return this.neighborRoutes;
    }

    public Map<AirportNode, Float> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return getAirportNodeKey();
    }
}