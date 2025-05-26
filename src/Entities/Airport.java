package Entities;

public class Airport {

    int id;
    float latitude;
    float longitude;
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

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIATA() {
        return IATA;
    }

    public void setIATA(String IATA) {
        this.IATA = IATA;
    }

    @Override
    public String toString() {
        return this.getId() + " " + this.getIATA() + "-" + this.getName();
    }
}
