package com.liuy.helloopengles.util;

import java.util.Vector;

/**
 * description:
 * author: freed on 2020/1/31
 * email: 674919909@qq.com
 * version: 1.0
 */
public class Geometry {


    /**
     * 点
     */
    public static class Point{
        public float x,y,z;//创建点

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance){
            return new Point(x,y+distance,z);
        }

        public Point translate(Vector vector) {
            return new Point(
			x+vector.x,
			y+vector.y,
			z+vector.z);
        }
    }

   public static class Vector{
        public float x,y,z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float)Math.sqrt(
			x*x
			+y*y
			+z*z);
        }
        public Vector crossProduct(Vector other) {
            return new Vector(
                    (y*other.z)-(z*other.y),
                    (z*other.x)-(x*other.z),
                    (x*other.y)-(y*other.x)
            );
        }



        public float dotProduct(Vector vector) {
            return x*vector.x
            +y*vector.y
            +z*vector.z;
        }

        public Vector scale(float f) {
            return new Vector(
			x*f,
			y*f,
			z*f);
        }
    }
    /**
     * 面
     */
    public static class Ray{
        public Point point;//一个点
        public Vector vector;//一个射线

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }
    public static class Circle{
        public Point center;
        public float radius;//半径

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale){
            return new Circle(center,radius*scale);
        }
    }

    /**
     * 圆柱
     */
    public static class CyLinder{
        public Point center;
        public float radius;//半径
        public float height;//高

        public CyLinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }






    public static class Sphere{
        public  Point center;
        public float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static class Plane{
        public Point point;
        public Vector normal;

        public Plane(Point point,Vector vector) {
            this.point = point;
            this.normal=vector;
        }
    }
    public static Vector vectorBetween(Point from,Point to){
        return new Vector(
		to.x-from.x,
		to.y-from.y,
		to.z-from.z);
    }
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center,ray)<sphere.radius;
    }

    private static float distanceBetween(Point point, Ray ray) {
        Vector p1=vectorBetween(ray.point,point);
        Vector p2=vectorBetween(ray.point.translate(ray.vector),point);

        float areaOfTriangleTimesTwo=p1.crossProduct(p2).length();
        float lengthOfBase=ray.vector.length();

        float distanceFromPointToRay=areaOfTriangleTimesTwo/lengthOfBase;
        return distanceFromPointToRay;
    }


    public static Point intersectionPoint(Ray ray, Plane plne) {
        Vector rayToPlaneVector=vectorBetween(ray.point,plne.point);
        float scaleFactor=rayToPlaneVector.dotProduct(plne.normal)/
		ray.vector.dotProduct(plne.normal);
        Point intersectionPoint=ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }
}
