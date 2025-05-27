package Entities;

public class Airport {

    int    id;
    float  latitude;
    float  longitude;
    String name;
    String IATA;

    public Airport(int id, float latitude, float longitude, String name, String IATA) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.IATA = IATA;
    }

    public int getId() {
        return id;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getIATA() {
        return IATA;
    }

    @Override
    public String toString() {
        return this.getId() + " " + this.getIATA() + "-" + this.getName().trim();
    }
}
