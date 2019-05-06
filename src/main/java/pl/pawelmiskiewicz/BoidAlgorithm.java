package pl.pawelmiskiewicz;

import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoidAlgorithm extends JPanel {
    private Polygon2D area;
    private List<Probe> probes;
    private Map<Integer, Double> fitnessScores = new HashMap<>();
    private boolean[][] adjMatrix;
    private double minX, minY, maxX, maxY;
    private Point2D center;
    private int minProbes;

    BoidAlgorithm (Polygon2D area, List<Probe> probes) {
        this.area = area;
        this.probes = probes;
        this.adjMatrix = new boolean[probes.size()][probes.size()];
        minProbes = (int) Math.round((0.9 * this.area.area()) / this.probes.get(0).getPolygon().area());
        this.minX = this.area.vertices().stream().mapToDouble(Point2D::x).min().getAsDouble();
        this.minY = this.area.vertices().stream().mapToDouble(Point2D::y).min().getAsDouble();
        this.maxX = this.area.vertices().stream().mapToDouble(Point2D::x).max().getAsDouble();
        this.maxY = this.area.vertices().stream().mapToDouble(Point2D::y).max().getAsDouble();
        center = this.area.centroid();
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
        for (Probe probe: this.probes) {
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

    List<Probe> getListNeighboutProbes(int probeIndex) {
        return this.probes
                .stream()
                .filter(probe-> isEdge(probeIndex, this.probes.indexOf(probe)))
                .collect(Collectors.toList());
    }

    public void calculateFitness(List<Probe> probes) {
        for (Probe probe: probes) {
            List<Probe> neighbours = getListNeighboutProbes(probes.indexOf(probe));
            double score = fitnessScore(probe, neighbours);
            fitnessScores.put(this.probes.indexOf(probe), score);
        }
    }

    double fitnessScore(Probe probe, List<Probe> neighbours) {
        Polygon2D difference = probe.getPolygon();
        Polygon2D union = Polygons2D.intersection(this.area, probe.getPolygon());
        double score = union.area();
        if(union.area() == 0) {
            return score;
        }
        for(int i = 1; i < neighbours.size(); i++) {
            difference = Polygons2D.difference(difference, neighbours.get(i).getPolygon());
        }
        score += difference.area();
        return score;
    }

    public void bindPosition(Probe probe) {
        if(probe.getX() < this.minX) {
            probe.setVelocity(probe.getVelocity().plus(new Vector2D(20, 0)));
        }
        else if(probe.getX() > this.maxX) {
            probe.setVelocity(probe.getVelocity().minus(new Vector2D(20, 0)));
        }
        if(probe.getY() < this.minY) {
            probe.setVelocity(probe.getVelocity().plus(new Vector2D(0, 20)));
        }
        else if(probe.getY() > this.maxY) {
            probe.setVelocity(probe.getVelocity().minus(new Vector2D(0, 20)));
        }
    }

    public void tendToPlace(Probe probe) {
        Vector2D tend = new Vector2D(this.center).minus(probe.getPosition());
        double x = tend.x() / 200;
        double y = tend.y() / 200;
        probe.setVelocity(probe.getVelocity().plus(new Vector2D(x, y)));
    }

    public void findSolution() {
        generateAdjacencyMatrix();
        calculateFitness(this.probes);
        double maxFitnessScore = this.area.area() * 2;
        double bestFitnessScore = this.fitnessScores.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
        int bestIteration = 0;
        int reduceIteration = 0;
        for (int i = 0; i < 1000; i++) {
            for (Probe probe : this.probes) {
                probe.flock(this.probes);
                bindPosition(probe);
                tendToPlace(probe);
                probe.update();
            }
            calculateFitness(this.probes);
            double totalFitnessScore = this.fitnessScores.entrySet().stream().mapToDouble(Map.Entry::getValue).sum();
            if (totalFitnessScore > bestFitnessScore) {
                bestFitnessScore = totalFitnessScore;
                bestIteration = i;
                System.out.println("zmiana");
            }
            this.adjMatrix = new boolean[this.probes.size()][this.probes.size()];
            generateAdjacencyMatrix();
            repaint( 1);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(i - bestIteration > 30 && i - reduceIteration > 40) {
                reducePopulation();
                reduceIteration = i;
            }
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
}
