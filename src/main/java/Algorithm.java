import com.google.common.base.Strings;
import math.geom2d.Vector2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Algorithm {

    private Polygon2D area;
    private List<Probe> probes;

    private boolean solutionFound = false;

    private boolean[][] adjMatrix;
    private Random random = new Random();
    private Map<Integer, Double> fitnessScores = new HashMap<>();

    Algorithm(Polygon2D area, List<Probe> probes) {
        this.area = area;
        this.probes = probes;
        this.adjMatrix = new boolean[probes.size()][probes.size()];
    }

    public void addEdge(int i, int j) {
        this.adjMatrix[i][j] = true;
        this.adjMatrix[j][i] = true;
    }

    public void removeEdge(int i, int j) {
        adjMatrix[i][j] = false;
        adjMatrix[j][i] = false;
    }

    public boolean isEdge(int i, int j) {
        return this.adjMatrix[i][j];
    }

    void generateAdjacencyMatrix () {
        IntStream.range(0, this.probes.size())
                .forEach(index -> {
                    Polygon2D probe = this.probes.get(index).getPolygon();
                    Circle2D circle = new Circle2D(probe.centroid().x(), probe.centroid().y(), 30);
                    IntStream.range(index + 1, this.probes.size())
                            .forEach(internalIndex -> {
                                if (isEdge(index,internalIndex)) {
                                    return;
                                }

                                if(circle.isInside(this.probes.get(internalIndex).getPolygon().centroid())) {
                                    addEdge(index, internalIndex);
                                }
                            });
                });
    }

    public void calculateFitness() {
        for (Probe probe: this.probes) {
            List<Probe> neighbours = getListNeighboutProbes(this.probes.indexOf(probe));
            double score = fitnessScore(probe, neighbours);
            fitnessScores.put(this.probes.indexOf(probe), score);
        }
    }

    public void findSolution () {
        generateAdjacencyMatrix();
        calculateFitness();
        while(true) {
            for (Probe probe: this.probes) {
                List<Probe> neighbours = getListNeighboutProbes(this.probes.indexOf(probe));
            }
        }
    }

    private int bestNeighbour(List<Polygon2D> neighbours) {
        double best = 0;
        int bestIndex = 0;
        for(int i=0; i<neighbours.size();i++) {
            double temp = fitnessScores.get(this.probes.indexOf(neighbours.get(i)));
            if(temp > best) {
                best = temp;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    List<Probe> getListNeighboutProbes(int probeIndex) {
        return this.probes
                .stream()
                .filter(probe-> isEdge(probeIndex, this.probes.indexOf(probe)) && this.probes.indexOf(probe) != probeIndex)
                .collect(Collectors.toList());
    }

    Vector2D crossoverParents(Probe probe, Probe neighbour) {
        Integer px = (int) probe.getX();
        Integer py = (int) probe.getY();

        Integer nx = (int) neighbour.getX();
        Integer ny = (int) neighbour.getY();

        int[] PX = parseStringToInt(Strings.padStart(Integer.toBinaryString(px), 10, '0').split(""));
        int[] PY = parseStringToInt(Strings.padStart(Integer.toBinaryString(py), 10, '0').split(""));

        int[] NX = parseStringToInt(Strings.padStart(Integer.toBinaryString(nx), 10, '0').split(""));
        int[] NY = parseStringToInt(Strings.padStart(Integer.toBinaryString(ny), 10, '0').split(""));

        int[] CX = crossover(PX, NX);
        int[] CY = crossover(PY, NY);

        int cx = Integer.parseInt(parseIntToString(CX), 2);
        int cy = Integer.parseInt(parseIntToString(CY), 2);

        return new Vector2D(cx, cy);
    }

    double fitnessScore(Probe probe, List<Probe> neighbours) {
        Polygon2D union = Polygons2D.intersection(this.area, probe.getPolygon());
        for(int i = 1; i < neighbours.size(); i++) {
            union = Polygons2D.difference(union, neighbours.get(i).getPolygon());
        }
        return union.area();
    }

    int[] crossover(int[] parent1, int[] parent2) {
        int xoverpoint = random.nextInt(parent1.length);
        int xoverpoint2 = random.nextInt(parent1.length);

        int tmp;

        if (xoverpoint > xoverpoint2){
            tmp = xoverpoint;
            xoverpoint = xoverpoint2;
            xoverpoint2 = tmp;
        }
        int [] child = new int[parent1.length];
        for (int i=0; i<parent1.length; i++){
            if(i <= xoverpoint || i>= xoverpoint2)
                child[i] = parent1[i];
            else
                child[i] = parent2[i];
        }

        return child;
    }

    int[] parseStringToInt(String[] input) {
        int size = input.length;
        int[] arr = new int [size];
        for(int i=0; i<size; i++) {
            arr[i] = Integer.parseInt(input[i]);
        }
        return arr;
    }

    String parseIntToString(int[] input) {
        String string = "";
        for(int i=0; i<input.length; i++) {
            string += string + input[i];
        }
        return string;
    }

    public boolean[][] getAdjMatrix() {
        return adjMatrix;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }
}
