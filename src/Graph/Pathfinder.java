package Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Pathfinder {
    private AirportGraph parent;

    public Pathfinder(AirportGraph parent) {
        this.parent = parent;
    }

    public WeightedPath findShortestPath(String fromIATA, String toIATA, boolean weighted) {

        Map<String, AirportNode> graph = parent.getGraph();
        WeightedPath returnPath;
        if (!graph.containsKey(fromIATA) || !graph.containsKey(toIATA)) return null;

        if (!weighted) {
            returnPath = bfs(graph, fromIATA, toIATA);
        } else {
            returnPath = dijkstra(graph, fromIATA, toIATA);
        }
        return returnPath;
    }

    private WeightedPath bfs(Map<String, AirportNode> graph, String fromIATA, String toIATA) {
        // --- UNGEWICHTETE SUCHE (BFS) ---
        Map<String, String> predecessor = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(fromIATA);
        visited.add(fromIATA);

        while (!queue.isEmpty()) {
            String currentIATA = queue.poll();
            AirportNode currentNode = graph.get(currentIATA);
            if (currentNode != null) {
                for (AirportNode neighbor : currentNode.getNeighbors().keySet()) {
                    String neighborIATA = neighbor.getFromAirport().getIATA();
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
        }

        return null;
    }

    private WeightedPath dijkstra(Map<String, AirportNode> graph, String fromIATA, String toIATA) {

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
            if (currentNode != null) {
                for (Map.Entry<AirportNode, Float> entry : currentNode.getNeighbors().entrySet()) {
                    AirportNode neighbor = entry.getKey();
                    float weight = entry.getValue();
                    String neighborIATA = neighbor.getFromAirport().getIATA();

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
        }

        if (distance.get(toIATA) != Float.POSITIVE_INFINITY) {
            List<WeightedPath.Step> steps = WeightedPath.reconstructSteps(predecessor, edgeWeights, graph, fromIATA, toIATA);
            List<AirportNode> nodePath = WeightedPath.extractNodePath(steps, graph.get(toIATA));
            return new WeightedPath(nodePath, steps, distance.get(toIATA));
        }

        return null;
    }

    private List<AirportNode> reconstructNodePath(Map<String, String> predecessor, String start, String end) {
        LinkedList<AirportNode> path = new LinkedList<>();
        String current = end;

        while (current != null) {
            path.addFirst(parent.getGraph().get(current));
            current = predecessor.get(current);
        }

        return path;
    }

    private List<WeightedPath.Step> buildSteps(List<AirportNode> nodePath) {
        List<WeightedPath.Step> steps = new ArrayList<>();

        for (int i = 0; i < nodePath.size() - 1; i++) {
            AirportNode from = nodePath.get(i);
            AirportNode to = nodePath.get(i + 1);
            Map<AirportNode, Float> neighbors = from.getNeighbors();
            Float weight = neighbors.get(to);
            if (weight == null) {
                throw new IllegalStateException("Keine Kante von " + from.getFromAirport().getIATA() + " zu " + to.getFromAirport().getIATA());
            }
            steps.add(new WeightedPath.Step(from, to, weight));
        }

        return steps;
    }
}
