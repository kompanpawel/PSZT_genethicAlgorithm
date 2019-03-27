import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class Main extends JPanel{

    public void paint(Graphics g) {
        Area area = new Area(100, 600, 20);

        area.generateRandomPoints();
        Point leftmostPoint = area.findLeftmostPoint();
        Point rightmostPoint = area.findRightmostPoint();
        Point[] linePoints = new Point[]{leftmostPoint, rightmostPoint};
        Object[] arrays = area.sortArrayIntoABC(linePoints);
        area.sortAndMergeABCArrays((ArrayList<Point>) arrays[0], (ArrayList<Point>) arrays[1], (ArrayList<Point>) arrays[2]);
        area.closeLineToPolygon();
        Polygon newArea = area.buildPolygon();
        Probe probe = new Probe(150, 140, 20, 0);
        Probe probe_1 = new Probe(250, 140, 20, 2);
        Polygon probe1 = probe.createTriangle();
        Polygon probe2 = probe_1.createTriangle();
        g.drawPolygon(newArea);
        g.drawPolygon(probe1);
        g.drawPolygon(probe2);
        System.out.println(area.polygonArea());
        System.out.println(probe.calculateArea());
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Main());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setVisible(true);

    }
}
