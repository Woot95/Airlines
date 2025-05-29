package Graph;


import Entities.Airport;

import java.util.HashMap;
import java.util.Map;

public class AirportRegistry {

    private static final Map<Integer, Airport> byId   = new HashMap<>();
    private static final Map<String, Airport>  byIata = new HashMap<>();

    public static Airport getOrCreate(int id, float lat, float lon, String name, String iata) {
        if (byId.containsKey(id)) {
            return byId.get(id);
        }

        Airport airport = new Airport(id, lat, lon, name, iata);
        byId.put(id, airport);
        byIata.put(iata, airport);
        return airport;
    }

    public static Airport getById(int id) {
        return byId.get(id);
    }

    public static Airport getByIATA(String iata) {
        return byIata.get(iata);
    }

    public static void clear() {
        byId.clear();
        byIata.clear();
    }
}

