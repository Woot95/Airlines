package Graph;

import DatabaseHandler.DatabaseHandler;
import Entities.Airport;
import Entities.Route;
import Graph.WeightedPath.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class AirportGraph {

    Map<String, AirportNode> graph = new HashMap<>();
    private DatabaseHandler dbhandler = new DatabaseHandler();

    public static void main(String[] args) {
        AirportGraph graph = new AirportGraph();

        Airport startAirport = graph.dbhandler.getAirportByIATA("SML");
        Airport destinationAirport = graph.dbhandler.getAirportByIATA("SVO");
        //Airport destinationAirport = null;

        System.out.println("started.");
        if (startAirport != null) {
            graph.addAirport(startAirport); // Startknoten registrieren
            graph.buildSpanningTree(startAirport, destinationAirport);
        } else {
            System.out.println("Startairport nicht gefunden.");
        }
        //System.out.println(graph);
        assert startAirport != null;
        System.out.println(graph.findShortestPath(startAirport.getIATA(),destinationAirport.getIATA(),true));
        System.out.println("finished.");
    }

    public void addAirport(Airport startAirport) {
        graph.put(startAirport.getIATA(), new AirportNode(startAirport, this));
    }


    public void addConnectionWeighted(Airport a1, Airport a2, float weight) {
        addAirport(a1);
        addAirport(a2);

        AirportNode n1 = graph.get(a1.getIATA());
        AirportNode n2 = graph.get(a2.getIATA());

        n1.addNeighbor(n2, weight);
    }

    public void buildSpanningTree(Airport start, Airport end) {
        Set<String> visited = new HashSet<>();
        if (end == null) {
            System.out.println("Kanten im Spannbaum:");
            dfs(graph.get(start.getIATA()), visited);
        } else {
            System.out.printf("Kanten im Spannbaum mit %s:", end.getIATA());
            dfs(graph.get(start.getIATA()), end, visited);
        }
    }

    private void dfs(AirportNode current, Set<String> visited) {
        visited.add(current.getStartAirport().getIATA());
        for (AirportNode neighbor : current.getNeighbors(current.getStartAirport()).keySet()) {
            if (!visited.contains(neighbor.getStartAirport().getIATA())) {
                //System.out.println(current.airport.getIATA() + " - " + neighbor.airport.getIATA());
                dfs(neighbor, visited);
            }
        }

    }

    private boolean dfs(AirportNode current, Airport end, Set<String> visited) {
        visited.add(current.getStartAirport().getIATA());
        if (end.getIATA().trim().equals(current.getStartAirport().getIATA().trim())) {
            return true;
        } else {
            for (AirportNode neighbor : current.getNeighbors(end).keySet()) {
                if (!visited.contains(neighbor.getStartAirport().getIATA())) {
                    //System.out.println(current.airport.getIATA() + " - " + neighbor.airport.getIATA());
                    return dfs(neighbor, end, visited); // Abbrechen, wenn Ziel im Nachbarn gefunden wurde
                }
            }
        }
        return false;
    }

    public List<AirportNode> loadNeighborsFromDatabase(Airport current, Airport breakAt) {
        List<Route> routes = dbhandler.getPossibleRoutes(current.getId()); // DAO-Zugriff
        List<AirportNode> result = new ArrayList<>();

        for (Route r : routes) {
            Airport destination = dbhandler.getAirportById(r.getDestinationAirport());
            if (destination == null) {
                continue;
            }

            float weight = AirportNode.calucateWeight(current, destination);
            addConnectionWeighted(current, destination, weight); // ➤ wichtig!

            AirportNode neighborNode = graph.get(destination.getIATA());
            result.add(neighborNode);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AirportGraph:\n");

        for (Map.Entry<String, AirportNode> entry : graph.entrySet()) {
            AirportNode node = entry.getValue();
            sb.append("  ").append(node.getStartAirport().getIATA()).append(" -> ");

            Map<AirportNode, Float> neighborMap;
            try {
                neighborMap = node.getNeighbors(null);
            } catch (Exception e) {
                sb.append("[Fehler beim Laden der Nachbarn: ").append(e.getMessage()).append("]\n");
                continue;
            }

            if (neighborMap == null || neighborMap.isEmpty()) {
                sb.append("[keine Verbindungen]\n");
            } else {
                List<String> connections = new ArrayList<>();
                for (Map.Entry<AirportNode, Float> neighborEntry : neighborMap.entrySet()) {
                    String targetIATA = neighborEntry.getKey().getStartAirport().getIATA();
                    float distance = neighborEntry.getValue();
                    connections.add(targetIATA + " (" + String.format("%.2f", distance) + ")");
                }
                sb.append(String.join(", ", connections)).append("\n");
            }
        }

        return sb.toString();
    }

    public WeightedPath findShortestPath(String fromIATA, String toIATA, boolean weighted) {
        if (!graph.containsKey(fromIATA) || !graph.containsKey(toIATA)) return null;

        if (!weighted) {
            // --- UNGEWICHTETE SUCHE (BFS) ---
            Map<String, String> predecessor = new HashMap<>();
            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();

            queue.add(fromIATA);
            visited.add(fromIATA);

            while (!queue.isEmpty()) {
                String currentIATA = queue.poll();
                AirportNode currentNode = graph.get(currentIATA);

                for (AirportNode neighbor : currentNode.getNeighbors(null).keySet()) {
                    String neighborIATA = neighbor.getStartAirport().getIATA();
                    if (!visited.contains(neighborIATA)) {
                        visited.add(neighborIATA);
                        predecessor.put(neighborIATA, currentIATA);
                        queue.add(neighborIATA);
                        if (neighborIATA.equals(toIATA)) {
                            List<AirportNode> path = reconstructNodePath(predecessor, fromIATA, toIATA);
                            List<WeightedPath.Step> steps = buildSteps(path);
                            float totalWeight = steps.stream().map(WeightedPath.Step::getWeight).reduce(0f, Float::sum);
                            return new WeightedPath(path, steps, totalWeight);
                        }
                    }
                }
            }

            return null;
        } else {
            // --- GEWICHTETE SUCHE (DIJKSTRA) ---
            Map<String, Float> distance = new HashMap<>();
            Map<String, String> predecessor = new HashMap<>();
            Map<String, Float> edgeWeights = new HashMap<>();

            PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparing(iata -> distance.getOrDefault(iata, Float.POSITIVE_INFINITY)));

            for (String iata : graph.keySet()) {
                distance.put(iata, Float.POSITIVE_INFINITY);
            }

            distance.put(fromIATA, 0f);
            queue.add(fromIATA);

            while (!queue.isEmpty()) {
                String currentIATA = queue.poll();
                AirportNode currentNode = graph.get(currentIATA);

                for (Map.Entry<AirportNode, Float> entry : currentNode.getNeighbors(null).entrySet()) {
                    AirportNode neighbor = entry.getKey();
                    float weight = entry.getValue();
                    String neighborIATA = neighbor.getStartAirport().getIATA();

                    distance.putIfAbsent(neighborIATA, Float.POSITIVE_INFINITY);
                    float currentDist = distance.get(currentIATA);
                    float newDist = currentDist + weight;

                    if (newDist < distance.get(neighborIATA)) {
                        distance.put(neighborIATA, newDist);
                        predecessor.put(neighborIATA, currentIATA);
                        edgeWeights.put(neighborIATA, weight);
                        queue.remove(neighborIATA);
                        queue.add(neighborIATA);
                    }
                }
            }

            if (distance.get(toIATA) != Float.POSITIVE_INFINITY) {
                List<WeightedPath.Step> steps = WeightedPath.reconstructSteps(predecessor, edgeWeights, graph, fromIATA, toIATA);
                List<AirportNode> nodePath = WeightedPath.extractNodePath(steps, graph.get(toIATA));
                return new WeightedPath(nodePath, steps, distance.get(toIATA));
            }

            return null;
        }
    }

    private List<AirportNode> reconstructNodePath(Map<String, String> predecessor, String start, String end) {
        LinkedList<AirportNode> path = new LinkedList<>();
        String current = end;

        while (current != null) {
            path.addFirst(graph.get(current));
            current = predecessor.get(current);
        }

        return path;
    }

    private List<WeightedPath.Step> buildSteps(List<AirportNode> nodePath) {
        List<WeightedPath.Step> steps = new ArrayList<>();

        for (int i = 0; i < nodePath.size() - 1; i++) {
            AirportNode from = nodePath.get(i);
            AirportNode to = nodePath.get(i + 1);
            Map<AirportNode, Float> neighbors = from.getNeighbors(null);
            Float weight = neighbors.get(to);
            if (weight == null) {
                throw new IllegalStateException("Keine Kante von " + from.getStartAirport().getIATA() + " zu " + to.getStartAirport().getIATA());
            }
            steps.add(new WeightedPath.Step(from, to, weight));
        }

        return steps;
    }

    private List<Step> reconstructSteps(Map<String, String> predecessor, Map<String, Float> edgeWeights, String start, String end) {
        LinkedList<Step> steps = new LinkedList<>();
        String current = end;

        while (!current.equals(start)) {
            String prev = predecessor.get(current);
            AirportNode from = graph.get(prev);
            AirportNode to = graph.get(current);
            float weight = edgeWeights.getOrDefault(current, 1f);
            steps.addFirst(new Step(from, to, weight));
            current = prev;
        }

        return steps;
    }


}
