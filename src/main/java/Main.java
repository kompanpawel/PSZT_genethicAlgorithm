import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;

public class Main extends JPanel{
    private Random random = new Random();
    private Area area = new Area(100, 600, 10);
    private Polygon2D newArea = buildArea();
    private int probeSize = 20;
    private double areaOfPolygon = newArea.area();
    private long n = Math.round(areaOfPolygon / ((probeSize*probeSize)/2));
    private List<Polygon2D> probes = generateProbes((int) n);

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
                .limit(n)
                .map(point -> new Probe(point.x(), point.y(), probeSize, random.nextInt(4)))
                .map(Probe::createTriangle)
                .collect(Collectors.toList());
    }

   /*private List<Polygon2D> executeAlgorithm() {
        Algorithm algorithm = new Algorithm(newArea, probes);
        while(!algorithm.foundSolution) {
            algorithm.findSolution();
            if (!algorithm.foundSolution()) {
                probes = generateProbes((int) n + 1);
                algorithm = new Algorithm(newArea, probes);
            }
        }
        return probes;
    }*/

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        newArea.draw(g2d);
        for (Polygon2D probe: probes) {
            probe.draw(g2d);
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Main());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setVisible(true);

    }
}
