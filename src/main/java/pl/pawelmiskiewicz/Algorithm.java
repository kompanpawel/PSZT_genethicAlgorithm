package pl.pawelmiskiewicz;

import com.google.common.base.Strings;
import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom3d.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Algorithm extends JPanel{

    private Polygon2D area;
    private List<Probe> probes;
    private List<Probe> nextProbes = new LinkedList<>();
    private int minProbes;

    private boolean solutionFound = false;

    private boolean[][] adjMatrix;
    private Random random = new Random();
    private Map<Integer, Double> fitnessScores = new HashMap<>();

    private double minX, minY, maxX, maxY;

    Algorithm(Polygon2D area, List<Probe> probes) {
        this.area = area;
        this.probes = probes;
        this.adjMatrix = new boolean[probes.size()][probes.size()];
        minProbes = (int) Math.round((0.9 * this.area.area()) / this.probes.get(0).getPolygon().area());
        this.minX = this.area.vertices().stream().mapToDouble(Point2D::x).min().getAsDouble();
        this.minY = this.area.vertices().stream().mapToDouble(Point2D::y).min().getAsDouble();
        this.maxX = this.area.vertices().stream().mapToDouble(Point2D::x).max().getAsDouble();
        this.maxY = this.area.vertices().stream().mapToDouble(Point2D::y).max().getAsDouble();
    }

    public void addEdge(int i, int j) {
        this.adjMatrix[i][j] = true;
        this.adjMatrix[j][i] = true;
    }

    public boolean isEdge(int i, int j) {
        return this.adjMatrix[i][j];
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        area.draw(g2d);
        for (Probe probe: probes) {
            probe.getPolygon().draw(g2d);
        }
    }

    void generateAdjacencyMatrix () {
        IntStream.range(0, this.probes.size())
                .forEach(index -> {
                    Polygon2D probe = this.probes.get(index).getPolygon();
                    Circle2D circle = new Circle2D(probe.centroid().x(), probe.centroid().y(), 40);
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

    public void calculateFitness(List<Probe> probes) {
        for (Probe probe: probes) {
            List<Probe> neighbours = getListNeighboutProbes(probes.indexOf(probe));
            double score = fitnessScore(probe, neighbours);
            fitnessScores.put(this.probes.indexOf(probe), score);
        }
    }

    public void findSolution () {
        generateAdjacencyMatrix();
        calculateFitness(this.probes);
        double maxFitnessScore = this.area.area()*2;
        double bestFitnessScore = this.fitnessScores.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
        int bestIteration = 0;
        int reduceIteration = 0;
        for(int i = 0; i < 10000; i++) {
            for (Probe probe: this.probes) {
                List<Probe> neighbours = getListNeighboutProbes(this.probes.indexOf(probe));
                int number;
                Probe bestNeighbour;
                if(neighbours.size() == 0) {
                    number = random.nextInt(this.probes.size());
                    bestNeighbour = this.probes.get(number);
                } else {
//                    number = random.nextInt(neighbours.size());
                    bestNeighbour = neighbours.get(getBestNeighbour(neighbours));
                }
                Vector3D newProbeCoords = crossoverParents(probe, bestNeighbour);
                Probe child = new Probe(newProbeCoords.getX(), newProbeCoords.getY(), 30, (int) newProbeCoords.getZ());
                this.nextProbes.add(child);
            }
            calculateFitness(this.nextProbes);
            double totalFitnessScore = this.fitnessScores.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
//            if(totalFitnessScore > bestFitnessScore) {
                bestFitnessScore = totalFitnessScore;
                bestIteration = i;
                this.probes = this.nextProbes;
                this.nextProbes = new LinkedList<>();
                this.adjMatrix = new boolean[this.probes.size()][this.probes.size()];
                generateAdjacencyMatrix();
                System.out.println("zmiana");
//            }
            if(bestFitnessScore >= 0.9 * maxFitnessScore) {
                System.out.println("pokrywa");
            }
            if(i - bestIteration > 30 && i - reduceIteration > 40) {
                reducePopulation();
                reduceIteration = i;
            }
            this.nextProbes = new LinkedList<>();
            repaint();

            System.out.println(i);
        }
    }

    private void reducePopulation() {
        if(this.probes.size() <= minProbes) {
            return;
        }
        int toDie = 3;
        List<Integer> forDie = new LinkedList<>();
        for(Probe probe: this.probes) {
            if(!area.contains(new Point2D(probe.getX(), probe.getY()))) {
                forDie.add(this.probes.indexOf(probe));
                toDie -= 1;
                if(toDie == 0)
                    break;
            }
        }
        for(Integer index: forDie) {
            this.probes.remove(probes.get(index));
        }
    }

    private int getBestNeighbour(List<Probe> neighbours) {
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
                .filter(probe-> isEdge(probeIndex, this.probes.indexOf(probe)))
                .collect(Collectors.toList());
    }

    Vector3D crossoverParents(Probe probe, Probe neighbour) {
        Integer px = (int) probe.getX();
        Integer py = (int) probe.getY();
        Integer pDir = probe.getDirection();

        Integer nx = (int) neighbour.getX();
        Integer ny = (int) neighbour.getY();
        Integer nDir = neighbour.getDirection();

        int[] PX = parseStringToInt(Strings.padStart(Integer.toBinaryString(px), 10, '0').split(""));
        int[] PY = parseStringToInt(Strings.padStart(Integer.toBinaryString(py), 10, '0').split(""));
        int[] PDIR = parseStringToInt(Strings.padStart(Integer.toBinaryString(pDir), 2, '0').split(""));

        int[] NX = parseStringToInt(Strings.padStart(Integer.toBinaryString(nx), 10, '0').split(""));
        int[] NY = parseStringToInt(Strings.padStart(Integer.toBinaryString(ny), 10, '0').split(""));
        int[] NDIR = parseStringToInt(Strings.padStart(Integer.toBinaryString(nDir), 2, '0').split(""));

        int[] CX = crossover(PX, NX);
        int[] CY = crossover(PY, NY);
        int[] CDIR = smallCrossover(PDIR, NDIR);

        int cx = Integer.parseInt(parseIntToString(CX), 2);
        int cy = Integer.parseInt(parseIntToString(CY), 2);
        int cDir = Integer.parseInt(parseIntToString(CDIR), 2);

//        if(cx < minX) {
//            cx = (int) minX;
//            cx += 100;
//            cx = random.nextInt((int) maxX - (int) minX);
//        } else if(cx > maxX) {
//            cx = (int) maxX;
//            cx -= 100;
//            cx = random.nextInt((int) maxX - (int) minX);
//        }
//
//        if(cy < minY) {
//            cy = (int) minY;
//            cy += 100;
//            cy = random.nextInt((int) maxY - (int) minY);
//        } else if(cy > maxY) {
//            cy = (int) maxY;
//            cy -= 100;
//            cy = random.nextInt((int) maxY - (int) minY);
//        }

        return new Vector3D(cx, cy, cDir);
    }

    double fitnessScore(Probe probe, List<Probe> neighbours) {
        Polygon2D difference = probe.getPolygon();
        Polygon2D union = Polygons2D.intersection(this.area, probe.getPolygon());
        double score = union.area();
        if(union.area() == 0) {
            return 0;
        }
        for(int i = 1; i < neighbours.size(); i++) {
            difference = Polygons2D.difference(difference, neighbours.get(i).getPolygon());
        }
        score += difference.area();
        return score;
    }

    int[] crossover(int[] parent1, int[] parent2) {
        int xoverpoint = random.nextInt(5);
        int xoverpoint2 = 5 + random.nextInt(5);

        int[] child = new int[parent1.length];
        for (int i=0; i<parent1.length; i++){
            if(i <= xoverpoint || i>= xoverpoint2)
                child[i] = parent1[i];
            else
                child[i] = parent2[i];
        }

        if(random.nextInt(100) < 5) {
           child = mutate(child);
        }
        return child;
    }

    private int[] smallCrossover(int[] parent1, int[] parent2) {
        int[] child = new int[parent1.length];
        for(int i = 0; i < parent1.length; i++) {
            if(random.nextBoolean())
                child[i] = 1;
            else
                child[i] = 0;
        }
        return child;
    }

    private int[] mutate(int[] poorChild) {
        int mut = random.nextInt(poorChild.length);
        poorChild[mut] = poorChild[mut] == 0 ? 1 : 0;
        return poorChild;
    }

    private int[] parseStringToInt(String[] input) {
        int size = input.length;
        int[] arr = new int [size];
        for(int i=0; i<size; i++) {
            arr[i] = Integer.parseInt(input[i]);
        }
        return arr;
    }

    private String parseIntToString(int[] input) {
        return Arrays.stream(input).mapToObj(String::valueOf).reduce((a, b) -> a.concat("".concat(b))).get();
    }

    public boolean[][] getAdjMatrix() {
        return adjMatrix;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }

}
