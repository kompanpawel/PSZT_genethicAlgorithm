package pl.pawelmiskiewicz;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;

public class Main {
    private Random random = new Random();
    private Area area = new Area(100, 600, 10);
    private Polygon2D newArea = buildArea();
    private int probeSize = 30;
    private double areaOfPolygon = newArea.area();
    private long n = Math.round(areaOfPolygon / ((probeSize*probeSize)/2));
    private List<Probe> probes = generateProbes((int) n);

    private Polygon2D buildArea() {
        area.generateRandomPoints();
        Point leftmostPoint = area.findLeftmostPoint();
        Point rightmostPoint = area.findRightmostPoint();
        Point[] linePoints = new Point[]{leftmostPoint, rightmostPoint};
        Object[] arrays = area.sortArrayIntoABC(linePoints);
        area.sortAndMergeABCArrays((ArrayList<Point>) arrays[0], (ArrayList<Point>) arrays[1], (ArrayList<Point>) arrays[2]);
        area.closeLineToPolygon();
        return area.buildPolygon();
    }

    private List generateProbes(Integer n) {
        return Stream.iterate(0, i -> i + 1)
                .map(index -> {
                    int a = random.nextInt(600-100);
                    int b = random.nextInt(600-100);
                    return new Point2D(a, b);
                })
                .filter(newArea::contains)
                .limit(n+50)
                .map(point -> new Probe(point.x(), point.y(), probeSize, random.nextInt(4)))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Main main = new Main();
        Algorithm algorithm = new Algorithm(main.newArea, main.probes);
        BoidAlgorithm boidAlgorithm = new BoidAlgorithm(main.newArea, main.probes);
        JFrame frame = new JFrame();
        frame.getContentPane().add(algorithm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setVisible(true);
        algorithm.findSolution();
    }
}
