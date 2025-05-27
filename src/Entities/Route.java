package Entities;


import DatabaseHandler.DatabaseHandler;

public class Route {
    int id;
    int startAirportID;
    int destinationAirportID;


    public Route( int startAirportID, int destinationAirportID) {

        this.startAirportID = startAirportID;
        this.destinationAirportID = destinationAirportID;
    }

    public int getStartAirport() {
        return startAirportID;
    }

    public int getDestinationAirport() {
        return destinationAirportID;
    }

}
