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

    Polygon createTriangle() {
        Polygon triangle = new Polygon();
        triangle.addPoint(this.x, this.y);
        switch(this.direction) {
            case 0:
                triangle.addPoint(this.x, this.y - this.d);
                triangle.addPoint(this.x + this.d, this.y);
                break;
            case 1:
                triangle.addPoint(this.x + this.d, this.y);
                triangle.addPoint(this.x, this.y + this.d);
                break;
            case 2:
                triangle.addPoint(this.x, this.y + this.d);
                triangle.addPoint(this.x - this.d, this.y);
                break;
            case 3:
                triangle.addPoint(this.x - this.d, this.y);
                triangle.addPoint(this.x, this.y - this.d);
                break;
        }
        return triangle;
    }

    double calculateArea() {
        return (this.d*this.d)/2;
    }
}
