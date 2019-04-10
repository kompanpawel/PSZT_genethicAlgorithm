import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;


public class Probe {
    private double x, y;
    private int d;
    private int direction;// 0-up, 1-right, 2-down, 3-left
    Polygon2D polygon;


    Probe(double x, double y, int d, int dir) {
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

    public Polygon2D getPolygon() {
        if(polygon == null) {
            this.polygon = createTriangle();
        }
        return this.polygon;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
