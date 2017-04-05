package kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KdTree {
    private int size;
    private Node root;


    public KdTree() {
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        checkForNull(p);
        addPoint(p);
    }

    public boolean contains(Point2D p) {
        checkForNull(p);
        return getNode(p) != null;
    }

    public void draw() {
        drawAllNodes();
    }

    public Iterable<Point2D> range(RectHV rect) {
        checkForNull(rect);
        return findInRange(rect);
    }

    public Point2D nearest(Point2D p) {
        checkForNull(p);
        return getNearest(p);
    }

    private Point2D getNearest(Point2D p) {
        if (root == null) return null;
        ArrayList<Point2D> list = new ArrayList<>();
        double startDistance = root.point.distanceSquaredTo(p);
        list.add(root.point);
        getNearest(root, p, startDistance, list);
        return list.isEmpty() ? null : list.get(0);
    }

    private double getNearest(Node node, Point2D p, double minDistance, List<Point2D> list) {
        if (node == null)
            return minDistance;
        RectHV nodeRect = getRectHV(node);
        double distanceToRect = nodeRect.distanceSquaredTo(p);
        if (distanceToRect > minDistance)
            return minDistance;
        double distanceToPoint = node.point.distanceSquaredTo(p);
        if (distanceToPoint < minDistance) {
            minDistance = distanceToPoint;
            list.add(0, node.point);
        }
        if (less(p, node.point, node.position)) {
            minDistance = getNearest(node.left, p, minDistance, list);
            minDistance = getNearest(node.right, p, minDistance, list);
        } else {
            minDistance = getNearest(node.right, p, minDistance, list);
            minDistance = getNearest(node.left, p, minDistance, list);
        }
        return minDistance;
    }

    private RectHV getRectHV(Node node) {
        double x0 = node.bounds[0][0];
        double y0 = node.bounds[1][0];
        double x1 = node.bounds[0][1];
        double y1 = node.bounds[1][1];
        return new RectHV(x0, y0, x1, y1);
    }

    private void drawAllNodes() {
        drawSubtree(root);
    }

    private List<Point2D> findInRange(RectHV rect) {
        List<Point2D> result = new LinkedList<>();
        findAllIntersection(rect, root, result);
        return result;
    }

    private void findAllIntersection(RectHV rect, Node node, List<Point2D> list) {
        if (node == null)
            return;
        Point2D p = node.point;
        if (rect.contains(p))
            list.add(p);
        RectHV nodeRect = getRectHV(node);
        if (!rect.intersects(nodeRect))
            return;
        findAllIntersection(rect, node.left, list);
        findAllIntersection(rect, node.right, list);
    }

    private void drawSubtree(Node node) {
        if (node == null)
            return;
        double x0 = node.bounds[0][0];
        double y0 = node.bounds[1][0];
        double x1 = node.bounds[0][1];
        double y1 = node.bounds[1][1];
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.point.x(), node.point.y());
        StdDraw.setPenRadius();
        if (node.position == Node.Position.VERTICAL) {
            StdDraw.setPenColor(Color.RED);
            StdDraw.line(node.point.x(), y0, node.point.x(), y1);
        } else {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.line(x0, node.point.y(), x1, node.point.y());
        }

        drawSubtree(node.left);
        drawSubtree(node.right);
    }

    private void addPoint(Point2D p) {
        if (root == null) {
            addPointInEmpty(p);
            return;
        }
        Node current = root;
        Node parent = null;
        while (current != null) {
            parent = current;
            if (equal(p, current.point))
                return;
            else if (less(p, current.point, current.position))
                current = current.left;
            else
                current = current.right;
        }
        Node.Position pos = newPosition(parent);
        Node newNode = new Node(p, parent, pos);
        setChild(parent, newNode);
        size++;
    }

    private Node getNode(Point2D p) {
        Node current = root;
        while (current != null) {
            if (equal(p, current.point))
                return current;
            else if (less(p, current.point, current.position))
                current = current.left;
            else
                current = current.right;
        }
        return null;
    }

    private Node.Position newPosition(Node parent) {
        if (parent == null)
            return Node.Position.VERTICAL;
        return parent.reversePosition();
    }

    private void setChild(Node parent, Node child) {
        if (parent.position == Node.Position.VERTICAL) {
            if (parent.point.x() - child.point.x() > 0)
                parent.left = child;
            else
                parent.right = child;
        } else {
            if (parent.point.y() - child.point.y() > 0)
                parent.left = child;
            else
                parent.right = child;
        }
    }

    private void addPointInEmpty(Point2D p) {
        root = new Node(p, null, Node.Position.VERTICAL);
        size++;
    }

    private boolean equal(Point2D p1, Point2D p2) {
        return p1.x() == p2.x() && p1.y() == p2.y();
    }

    private boolean less(Point2D p1, Point2D p2, Node.Position position) {
        if (position == Node.Position.VERTICAL)
            return p1.x() - p2.x() < 0;
        else
            return p1.y() - p2.y() < 0;
    }

    private static class Node {
        private Point2D point;
        private double[][] bounds;
        private Node left;
        private Node right;
        private Node parent;
        private Position position;

        private Node(Point2D point, Node parent, Position position) {
            this.point = point;
            this.parent = parent;
            this.position = position;
            this.bounds = bounds();
        }

        private Position reversePosition() {
            if (position == Position.HORIZONTAL)
                return Position.VERTICAL;
            return Position.HORIZONTAL;
        }

        private double[][] bounds() {
            if (this.parent == null)
                return new double[][]{{0.0, 1.0}, {0.0, 1.0}};
            double[][] parentBounds = parent.bounds;
            double parentX0 = parentBounds[0][0];
            double parentX1 = parentBounds[0][1];
            double parentY0 = parentBounds[1][0];
            double parentY1 = parentBounds[1][1];
            double xMin;
            double xMax;
            double yMin;
            double yMax;
            if (this.position == Position.HORIZONTAL) {
                if (this.point.x() < parent.point.x()) {
                    xMin = parentX0;
                    xMax = parent.point.x();
                } else {
                    xMin = parent.point.x();
                    xMax = parentX1;
                }
                yMin = parentY0;
                yMax = parentY1;
            } else {
                if (this.point.y() < parent.point.y()) {
                    yMin = parentY0;
                    yMax = parent.point.y();
                } else {
                    yMin = parent.point.y();
                    yMax = parentY1;
                }
                xMin = parentX0;
                xMax = parentX1;
            }
            return new double[][]{{xMin, xMax}, {yMin, yMax}};
        }

        private enum Position {
            HORIZONTAL, VERTICAL;
        }
    }

    private void checkForNull(Object p) {
        if (p == null)
            throw new NullPointerException("the passed argument equal to NULL");
    }

}
