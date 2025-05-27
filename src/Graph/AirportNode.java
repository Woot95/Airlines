package Graph;

import Entities.Airport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirportNode {
    final private Airport           startAirport;
    private Map<AirportNode, Float> neighbors = new HashMap<>();
    private AirportGraph            parent;

    AirportNode(Airport startAirport, AirportGraph parent) {
        this.startAirport = startAirport;
        this.parent = parent;
    }

    public Airport getStartAirport(){
        return this.startAirport;
    }

    public void addNeighbor(AirportNode neighbor, float weight){
        neighbors.put(neighbor, weight);
    }

    Map<AirportNode, Float> getNeighbors(Airport breakAt) {
        if (neighbors.isEmpty()) {
            List<AirportNode> neighborsList = parent.loadNeighborsFromDatabase(this.startAirport, breakAt);
            if (!neighborsList.isEmpty()){
                for(AirportNode neighbor : neighborsList ){
                    neighbors.put(neighbor, calucateWeight(this.getStartAirport(),neighbor.getStartAirport()));
                    parent.addConnectionWeighted(this.startAirport,neighbor.getStartAirport(),
                                                calucateWeight(this.getStartAirport(),neighbor.getStartAirport()));
                }
            }
        }
        return neighbors;
    }

    public static float calucateWeight(Airport a1, Airport a2){
        float dLat = a2.getLatitude() - a1.getLatitude();
        float dLon = a2.getLongitude() - a1.getLongitude();

        dLon = Math.abs(dLon);
        if (dLon > 180) {
            dLon = 360 - dLon;
        }

        return (float) Math.sqrt(dLat * dLat + dLon * dLon);
    }


}