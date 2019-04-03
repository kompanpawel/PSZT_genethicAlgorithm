import math.geom2d.polygon.Polygon2D;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class Main extends JPanel{
    private Area area = new Area(100, 600, 10);

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

    private ArrayList<Polygon2D> generateProbes() {
        Probe probe = new Probe(150, 140, 20, 0);
        Probe probe_1 = new Probe(250, 140, 20, 2);
        Polygon2D probe1 = probe.createTriangle();
        Polygon2D probe2 = probe_1.createTriangle();
        ArrayList<Polygon2D> probes = new ArrayList<>();
        probes.add(probe1);
        probes.add(probe2);
        return probes;
    }

    public void paint(Graphics g) {
        Polygon2D area = buildArea();
        ArrayList<Polygon2D> probes = generateProbes();
        Graphics2D g2d = (Graphics2D) g;
        area.draw(g2d);
        probes.get(0).draw(g2d);
        probes.get(1).draw(g2d);
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Main());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setVisible(true);

    }
}
