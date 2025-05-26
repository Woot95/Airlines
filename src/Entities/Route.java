package Entities;

public class Route {
    int id;
    int airlineID;
    int startAirportID;
    int destinationAirportID;

    public Route(int id, int airlineID, int startAirportID, int destinationAirportID) {
        this.id = id;
        this.airlineID = airlineID;
        this.startAirportID = startAirportID;
        this.destinationAirportID = destinationAirportID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAirlineID() {
        return airlineID;
    }

    public void setAirlineID(int airlineID) {
        this.airlineID = airlineID;
    }

    public int getStartAirportID() {
        return startAirportID;
    }

    public void setStartAirportID(int startAirportID) {
        this.startAirportID = startAirportID;
    }

    public int getDestinationAirportID() {
        return destinationAirportID;
    }

    public void setDestinationAirportID(int destinationAirportID) {
        this.destinationAirportID = destinationAirportID;
    }
}
