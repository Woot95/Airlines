package Entities;


import Graph.AirportGraph;
import Graph.AirportNode;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Route {
    private final int     routeID;
    private final Airport fromAirport;
    private final Airport toAirport;
    private final Float   weight;

    public Route(int routeID, Airport fromAirport, Airport toAirport, Float weight) {
        this.routeID = routeID;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.weight = weight;
    }

    public int getRouteID() {
        return routeID;
    }

    public Airport getFromAirport() {
        return fromAirport;
    }

    public Airport getToAirport() {
        return toAirport;
    }

    public Float getWeight() {
        return weight;
    }

}
