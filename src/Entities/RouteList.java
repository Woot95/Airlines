package Entities;

import java.util.LinkedList;
import java.util.List;

public class RouteList extends LinkedList<Route> {

    public RouteList() {
        super();
    }

    public RouteList(List<Route> listToConvert) {
        super(listToConvert);
    }

    public RouteList findRoutesBySource(Airport fromAirport) {
        return new RouteList(
            this.stream()
                .filter(r -> r.getFromAirport().getIATA().equals(fromAirport.getIATA()))
                .toList());
    }

    public RouteList findRoutesByDestination(Airport toAirport) {
        return new RouteList(
            this.stream()
                .filter(r -> r.getToAirport().getIATA().equals(toAirport.getIATA()))
                .toList());
    }
}
