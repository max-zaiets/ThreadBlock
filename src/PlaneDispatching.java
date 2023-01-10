import java.util.HashSet;
import java.util.Set;

/**
 * Методы setLocation класса Plane и getMap класса Dispatcher,
 * являются синхронизированными и вызывают внутри себя синхронизированные
 * методы других классов, что должно вызывать блокировку
 */

public class PlaneDispatching {
    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher();
        Plane plane = new Plane(dispatcher);


        Point point1 = new Point(1154, 3358);
        Point point2 = new Point(1569, 2489);
        Image image = new Image(plane);

        plane.setLocation(point1);
        plane.setDestination(point2);

        dispatcher.requestLanding(plane);
        image.drawMarker(point1);

        Thread1 thread1 = new Thread1(dispatcher, plane);
        Thread2 thread2 = new Thread2(plane, point1);

        thread1.start();
        thread2.start();
    }
}


class Thread1 extends Thread {
    private Dispatcher dis;
    private Plane plane;

    public Thread1(Dispatcher dis, Plane plane) {
        this.dis = dis;
        this.plane = plane;
    }

    @Override
    public void run() {
        dis.getMap(plane);
    }
}


class Thread2 extends Thread {
    private Plane plane;
    private Point location;

    public Thread2(Plane plane, Point location) {
        this.plane = plane;
        this.location = location;
    }

    @Override
    public void run() {
        plane.setLocation(location);
    }
}


class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + "x=" + x +
                ", y=" + y + ")";
    }
}


class Image {
    private Plane plane;

    public Image(Plane plane) {
        this.plane = plane;
    }

    public void drawMarker(Point location) {
        System.out.println("Map drawn for plane number " + plane.getId() + " from point " + plane.getLocation() +
                " to point " + plane.getDestination());
    }
}


class Plane {
    private int id = 0;
    private Point location;
    private Point destination;
    private final Dispatcher dispatcher;

    public Plane(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        id++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public synchronized Point getLocation() {
        return location;
    }

    public synchronized void setLocation(Point location) {
        this.location = location;
        if (location.equals(destination))
            dispatcher.requestLanding(this);
    }
}


class Dispatcher {
    private final Set<Plane> planes;
    private final Set<Plane> planesPendingLanding;

    public Dispatcher() {
        planes = new HashSet<>();
        planesPendingLanding = new HashSet<>();
    }

    public synchronized void requestLanding(Plane plane) {
        planesPendingLanding.add(plane);
    }

    public synchronized Image getMap(Plane plane) {
        Image image = new Image(plane);
        for (Plane plane1 : planes)
            image.drawMarker(plane.getLocation());
        return image;
    }
}
