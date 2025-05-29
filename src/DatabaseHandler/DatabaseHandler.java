package DatabaseHandler;

import Entities.Airport;
import Entities.Route;
import Entities.RouteList;
import Graph.AirportGraph;
import Graph.AirportNode;
import Graph.AirportRegistry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class DatabaseHandler {

    private final String url = "jdbc:derby:./Airlines/Airlines;create=false";

    public static void main(String[] args) {
    }

    public LinkedList<Airport> searchAirport(String SearchTerm) {
        LinkedList<Airport> resultList = new LinkedList<Airport>();
        Airport result;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
                 """
                     SELECT ID, LATITUDE, LONGITUDE, NAME, IATA FROM APP.AIRPORT 
                        WHERE 
                            UPPER(IATA) = UPPER(?)  
                            OR UPPER(NAME) LIKE '%' || UPPER(?) || '%'""")) {

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

    public Airport getAirportByIATA(String iata) {
        Airport result = null;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT ID, LATITUDE, LONGITUDE, NAME, IATA FROM APP.AIRPORT WHERE IATA = ? ")) {

            stmt.setString(1, iata);

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

    public Airport getAirportById(int ID) {
        Airport result = null;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM APP.AIRPORT WHERE ID = ? ")) {

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

    public RouteList readAllRoutes(AirportGraph parent) {
        String sqlStatment = """
            SELECT
            DISTINCT
                r.ID,
                r.airport1,
                r.airport2,
                a1.ID AS a1ID,
                a1.NAME AS a1NAME,
                a1.LATITUDE AS a1LATITUDE,
                a1.LONGITUDE AS a1LONGITUDE,
                a1.IATA AS a1IATA,
                a2.ID AS a2ID,
                a2.NAME AS a2NAME,
                a2.LATITUDE AS a2LATITUDE,
                a2.LONGITUDE AS a2LONGITUDE,
                a2.IATA AS a2IATA,
                SQRT(
                    EXP(2 * LN(ABS(a2.LATITUDE - a1.LATITUDE))) +
                        EXP(2 * LN(
                            CASE
                            WHEN ABS(a2.LONGITUDE - a1.LONGITUDE) > 180
                            THEN 360 - ABS(a2.LONGITUDE - a1.LONGITUDE)
                            ELSE ABS(a2.LONGITUDE - a1.LONGITUDE)
                            END
                        ))
                ) weight
                FROM
                ( SELECT DISTINCT ID, airport1, airport2
                    FROM
                    app.ROUTE ) r
                JOIN AIRPORT a1 ON
                    r.AIRPORT1 = a1.ID
                JOIN AIRPORT a2 ON
                    r.AIRPORT2 = a2.ID
            """;

        RouteList routesList = new RouteList();
        Route route;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sqlStatment)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                route = new Route(
                    rs.getInt("ID"),
                    AirportRegistry.getOrCreate(rs.getInt("a1ID"),
                        rs.getFloat("a1LATITUDE"),
                        rs.getFloat("a1LONGITUDE"),
                        rs.getString("a1NAME"),
                        rs.getString("a1IATA")),
                    AirportRegistry.getOrCreate(rs.getInt("a2ID"),
                        rs.getFloat("a2LATITUDE"),
                        rs.getFloat("a2LONGITUDE"),
                        rs.getString("a2NAME"),
                        rs.getString("a2IATA")),
                    rs.getFloat("weight"));

                routesList.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return routesList;
    }
}
