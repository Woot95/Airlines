package Graph;

import GUI.CrossMapMarker;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeightedPath {

    private final List<AirportNode> nodePath;
    private final List<Step>        steps;
    private final float             totalWeight;

    public WeightedPath(List<AirportNode> nodePath, List<Step> steps, float totalWeight) {
        this.nodePath = nodePath;
        this.steps = steps;
        this.totalWeight = totalWeight;
    }

    public static List<Step> reconstructSteps(Map<String, String> predecessor, Map<String, Float> edgeWeights, Map<String, AirportNode> graph, String start, String end) {
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

    public static List<AirportNode> extractNodePath(List<Step> steps, AirportNode lastNode) {
        List<AirportNode> path = steps.stream()
            .map(Step::getFrom)
            .collect(Collectors.toCollection(LinkedList::new));
        path.add(lastNode);
        return path;
    }

    public List<AirportNode> getNodePath() {
        return nodePath;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Step s : steps) {
            sb.append(s).append("\n");
        }
        sb.append("Gesamtdistanz: ").append(totalWeight);
        return sb.toString();
    }

    public static class Step {
        private final AirportNode from;
        private final AirportNode to;
        private final float       weight;

        public Step(AirportNode from, AirportNode to, float weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public AirportNode getFrom() {
            return from;
        }

        public AirportNode getTo() {
            return to;
        }

        public float getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return from.getAirportNodeKey() + " → " + to.getAirportNodeKey() + " (" + weight + ")";
        }
    }
}
