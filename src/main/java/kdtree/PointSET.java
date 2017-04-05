package kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> set;


    public PointSET() {
        this.set = new TreeSet<>();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public void insert(Point2D p) {
        checkForNull(p);
        set.add(p);
    }

    public boolean contains(Point2D p) {
        checkForNull(p);
        return set.contains(p);
    }

    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        checkForNull(rect);
        return findAllInside(rect);
    }

    public Point2D nearest(Point2D p) {
        checkForNull(p);
        double min = Double.MAX_VALUE;
        Point2D result = null;
        double distance;
        for (Point2D point : set) {
            distance = p.distanceTo(point);
            if (distance < min) {
                min = distance;
                result = point;
            }
        }
        return result;
    }

    private List<Point2D> findAllInside(RectHV rect) {
        List<Point2D> result = new LinkedList<>();
        for (Point2D p : set) {
            if (rect.contains(p))
                result.add(p);
        }
        return result;
    }

    private void checkForNull(Object p) {
        if (p == null)
            throw new NullPointerException("the passed argument equal to NULL");
    }

}
