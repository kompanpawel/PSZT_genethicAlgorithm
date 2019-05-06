package pl.pawelmiskiewicz;

import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;

import java.util.List;
import java.util.Random;


public class Probe {
    private double x, y;
    private int d;
    private int direction;// 0-up, 1-right, 2-down, 3-left
    Polygon2D polygon;
    private Random random = new Random();

    private Vector2D position;
    private Vector2D velocity = new Vector2D(random.nextInt(5), random.nextInt(5));
    private Vector2D acceleration = new Vector2D();


    Probe(double x, double y, int d, int dir) {
        this.x = x;
        this.y = y;
        this.d = d;
        this.direction = dir;
        position = new Vector2D(x, y);
    }

    private Polygon2D createTriangle() {
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

    private Vector2D align(List<Probe> probes) {
        int perceptionRadius = 50;
        Vector2D steering = new Vector2D();
        int total = 0;
        for (Probe probe: probes) {
            double distance = Point2D.distance(this.getX(), this.getY(), probe.getX(), probe.getY());
            if(probe != this && distance < perceptionRadius) {
                steering.plus(probe.getVelocity());
                total++;
            }
        }
        if(total > 0) {
           double x = steering.x();
           double y = steering.y();
           x = x/total;
           y = y/total;
           steering = new Vector2D(x, y);
           steering.minus(this.velocity);
        }

        return steering;
    }

    private Vector2D separation(List<Probe> probes) {
        int perceptionRadius = 50;
        Vector2D steering = new Vector2D();
        int total = 0;
        for(Probe probe: probes) {
            double distance = Point2D.distance(this.getX(), this.getY(), probe.getX(), probe.getY());
            if(probe != this && distance < perceptionRadius) {
                Vector2D diff = this.getPosition().minus(probe.getPosition());
                double x = diff.x();
                double y = diff.y();
                x = x / (distance*distance);
                y = y / (distance*distance);
                diff = new Vector2D(x, y);
                steering.plus(diff);
                total++;
            }
        }
        if(total > 0) {
            double x = steering.x();
            double y = steering.y();
            x = x/total;
            y = y/total;
            steering = new Vector2D(x, y);
            steering.minus(this.velocity);
        }
        return steering;
    }

    private Vector2D cohesion(List<Probe> probes) {
        int perceptionRadius = 100;
        Vector2D steering = new Vector2D();
        int total = 0;
        for(Probe probe: probes) {
            double distance = Point2D.distance(this.getX(), this.getY(), probe.getX(), probe.getY());
            if(probe != this && distance < perceptionRadius) {
                steering.plus(probe.getPosition());
                total++;
            }
        }
        if (total > 0) {
            double x = steering.x();
            double y = steering.y();
            x = x/total;
            y = y/total;
            steering = new Vector2D(x, y);
            steering.minus(this.position);
            steering.minus(this.velocity);
        }

        return steering;
    }

    void bindPosition() {

    }

    void flock(List<Probe> probes) {
        Vector2D aligment = this.align(probes);
        Vector2D cohesion = this.cohesion(probes);
        Vector2D separation = this.separation(probes);

        setAcceleration(this.acceleration.plus(aligment));
        setAcceleration(this.acceleration.plus(cohesion));
        setAcceleration(this.acceleration.plus(separation).times(2));
    }

    void update() {
        this.setPosition(this.position.plus(this.velocity));
        this.setX(this.position.x());
        this.setY(this.position.y());
        setVelocity(this.velocity.plus(this.acceleration));
        this.acceleration = new Vector2D();
        this.direction = random.nextInt(3);
    }

    Polygon2D getPolygon() {
        return createTriangle();
    }

    double getX() {
        return x;
    }

    private void setX(double x) {
        this.x = x;
    }

    double getY() {
        return y;
    }

    private void setY(double y) {
        this.y = y;
    }

    int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    Vector2D getPosition() {
        return position;
    }

    private void setPosition(Vector2D position) {
        this.position = position;
    }

    Vector2D getVelocity() {
        return velocity;
    }

    void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }

    private void setAcceleration(Vector2D acceleration) {
        this.acceleration = acceleration;
    }


}
