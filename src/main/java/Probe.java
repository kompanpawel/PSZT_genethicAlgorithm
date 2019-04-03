import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;

import java.awt.*;


public class Probe {
    private int x, y, d;
    private int direction; // 0-up, 1-right, 2-down, 3-left


    Probe(int x, int y, int d, int dir) {
        this.x = x;
        this.y = y;
        this.d = d;
        this.direction = dir;
    }

    Polygon2D createTriangle() {
        Polygon2D triangle = new SimplePolygon2D();
        triangle.addVertex(new Point2D(this.x, this.y));
        switch(this.direction) {
            case 0:
                triangle.addVertex(new Point2D(this.x, this.y - this.d));
                triangle.addVertex(new Point2D(this.x + this.d, this.y));
                break;
            case 1:
                triangle.addVertex(new Point2D(this.x + this.d, this.y));
                triangle.addVertex(new Point2D(this.x, this.y + this.d));
                break;
            case 2:
                triangle.addVertex(new Point2D(this.x, this.y + this.d));
                triangle.addVertex(new Point2D(this.x - this.d, this.y));
                break;
            case 3:
                triangle.addVertex(new Point2D(this.x - this.d, this.y));
                triangle.addVertex(new Point2D(this.x, this.y - this.d));
                break;
        }
        return triangle;
    }

    double calculateArea() {
        return (this.d*this.d)/2;
    }
}
