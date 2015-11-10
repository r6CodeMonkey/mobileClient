package oddymobstar.util.graphics.opengles;

/**
 * Created by timmytime on 10/11/15.
 */
public class Geometry {

    public static class Point{
        public float x, y, z;
        public Point(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;

        }

        public Point translateY(float distance){
            return new Point(x, y+distance,z);
        }

    }

    public static class Circle{

        public Point centre;
        public float radius;

        public Circle(Point centre, float radius){
            this.centre = centre;
            this.radius = radius;
        }

        public Circle scale(float scale){
            return new Circle(centre, radius*scale);
        }
    }

    public static class Cylinder{
        public Point centre;
        public float radius, height;

        public Cylinder(Point centre, float radius, float height){
            this.centre = centre;
            this.radius = radius;
            this.height = height;
        }
    }
}
