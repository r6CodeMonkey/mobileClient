package oddymobstar.util.graphics.opengles;

import android.util.FloatMath;

/**
 * Created by timmytime on 10/11/15.
 */
public class Geometry {

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray) {

        return distanceBetween(sphere.centre, ray) < sphere.radius;
    }

    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal);

        return ray.point.translate(ray.vector.scale(scaleFactor));
    }

    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        return areaOfTriangleTimesTwo / lengthOfBase;
    }

    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;

        }

        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }

        public Point translate(Vector vector) {
            return new Point(x + vector.x, y + vector.y, z + vector.z);
        }

    }

    public static class Circle {

        public final Point centre;
        public final float radius;

        public Circle(Point centre, float radius) {
            this.centre = centre;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(centre, radius * scale);
        }
    }

    public static class Cylinder {
        public final Point centre;
        public final float radius, height;

        public Cylinder(Point centre, float radius, float height) {
            this.centre = centre;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    //needs in own class once i get into the proper stuff (post demo) ie normalization and physics etc
    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return FloatMath.sqrt(x * x + y * y + z * z);
        }

        public Vector crossProduct(Vector other) {
            return new Vector((y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        public float dotProduct(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        public Vector scale(float f) {
            return new Vector(x * f, y * f, z * f);
        }

        public Vector normalize() {
            return scale(1f / length());
        }
    }

    public static class Sphere {
        public final Point centre;
        public final float radius;

        public Sphere(Point centre, float radius) {
            this.centre = centre;
            this.radius = radius;
        }
    }

    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;

        }
    }
}
