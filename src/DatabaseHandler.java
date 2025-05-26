import AirportGraph.AirportGraph;
import AirportGraph.AirportGraphNode;
import Entities.Airport;
import Entities.Route;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class DatabaseHandler {

    private final String url = "jdbc:derby:./Airlines/Airlines;create=false";

    public static void main(String[] args) {
//        DatabaseHandler dbhandler = new DatabaseHandler();
//
//        AirportGraph graph = new AirportGraph();
//
//        Airport StartAirport;
//        Airport DestinationAirport;
//
//        StartAirport = dbhandler.getAirportById(4908);
//        LinkedList<Route> data = dbhandler.getPossibleRoutes(StartAirport.getId());
//
//        AirportGraphNode root = new AirportGraphNode(StartAirport);
//        graph.addNode(root, null, 0);
//
//        for(Route r : data){
//            DestinationAirport = dbhandler.getAirportById(r.getDestinationAirportID());
//
//        }

    }

    public LinkedList<Airport> searchAirport(String SearchTerm) {
        LinkedList<Airport> resultList = new LinkedList<Airport>();
        Airport result;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM APP.AIRPORT WHERE " +
                     "UPPER(IATA) = UPPER(?) " +
                     "OR UPPER(NAME) LIKE '%' || UPPER(?) || '%'")) {

            stmt.setString(1, SearchTerm);
            stmt.setString(2, SearchTerm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = new Airport(rs.getInt("ID"), rs.getFloat("LATITUDE"),
                    rs.getFloat("LONGITUDE"), rs.getString("NAME"),
                    rs.getString("IATA"));
                resultList.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public Airport getAirportById(int ID){
        Airport result = null;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM APP.AIRPORT WHERE WHERE ID = ? ")) {

            stmt.setInt(1, ID);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = new Airport(rs.getInt("ID"), rs.getFloat("LATITUDE"),
                    rs.getFloat("LONGITUDE"), rs.getString("NAME"),
                    rs.getString("IATA"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public LinkedList<Route> getPossibleRoutes(int AirportID) {
        LinkedList<Route> resultList = new LinkedList<Route>();
        Route result;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM APP.ROUTE WHERE AIRPORT1 = ? " ) ) {

            stmt.setInt(1, AirportID);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = new Route(
                  rs.getInt("ID"),
                  rs.getInt("AIRLINE"),
                  rs.getInt("AIRPORT1"),
                    rs.getInt("AIRPORT2")
                );
                resultList.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;

    }


}
