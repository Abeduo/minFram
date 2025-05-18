import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// 取点 -》 筛选出凸包点 -》 旋转卡壳 -》 最小外包矩形
public class MinimumBoundingRectangle {

    // 计算凸包
    public static List<Point2D> computeConvexHull(List<Point2D> points) {
        // 点集小于3个，其本身所构成的图形为其凸包
        if (points.size() < 3) return points;

        // 找到最左下角的点
        Point2D minPoint = points.get(0);
        for (Point2D p : points) {
            if (p.getY() < minPoint.getY() || (p.getY() == minPoint.getY() && p.getX() < minPoint.getX())) {
                minPoint = p;
            }
        }
        System.out.println("最左下角的点为：" + minPoint);

        // 按极角排序
        Point2D finalMinPoint = minPoint;
        points.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - finalMinPoint.getY(), p1.getX() - finalMinPoint.getX());
            double angle2 = Math.atan2(p2.getY() - finalMinPoint.getY(), p2.getX() - finalMinPoint.getX());
            if (angle1 < angle2) return -1;
            if (angle1 > angle2) return 1;
            return Double.compare(p1.distance(finalMinPoint), p2.distance(finalMinPoint));
        });
        System.out.println("按级角排序后的点：" + points);

        // 使用Graham扫描法计算凸包
        Stack<Point2D> hull = new Stack<>();
        hull.push(points.get(0));
        hull.push(points.get(1));

        for (int i = 2; i < points.size(); i++) {
            Point2D top = hull.pop();
            while (cross(hull.peek(), top, points.get(i)) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points.get(i));
        }
        System.out.println("构成凸包的点集：" + hull);

        return new ArrayList<>(hull);
    }

    // 计算点 p 到直线 ab 的距离
    private static double distanceToLine(Point2D a, Point2D b, Point2D p) {
        return Math.abs((b.getX() - a.getX()) * (a.getY() - p.getY()) - (a.getX() - p.getX()) * (b.getY() - a.getY())) /
                Math.sqrt(Math.pow(b.getX() - a.getX(), 2) + Math.pow(b.getY() - a.getY(), 2));
    }

    // 计算最小外包矩形
    public static Point2D[] findMinimumBoundingRectangle(List<Point2D> convexHull) {
        int n = convexHull.size();
        if (n == 0) return null;

        double minArea = Double.MAX_VALUE;
        Point2D[] result = new Point2D[4]; // 存储最小矩形的四个顶点
        double angleF = 0;
        List<Point2D> ddl = new ArrayList<>();

        // 遍历凸包的每一条边
        for (int i = 0, j = 1, k = 1, l = 1; i < n; i++) {
            Point2D a = convexHull.get(i);
            Point2D b = convexHull.get((i + 1) % n);

            //
            double angle = rotationAngle(a, b);
            List<Point2D> ps = rotationCoordinates(convexHull, angle);
            List<Point2D> dd = new ArrayList<>();
            double area2 = rotationArea(ps, dd);
            if (area2 < minArea) {
                minArea = area2;
                angleF = angle;
                //记录外包矩形顶点
                ddl.clear();
                ddl = new ArrayList<>(dd);
            } ;

            // 找到最远的点（卡壳）
            while (cross(a, b, convexHull.get(j)) < cross(a, b, convexHull.get((j + 1) % n))) {
                j = (j + 1) % n;
            }

            // 找到最左和最右的点
            while (dot(a, b, convexHull.get(k)) < dot(a, b, convexHull.get((k + 1) % n))) {
                k = (k + 1) % n;
            }

            // 找到最下的点
            if (i == 0) l = j;
            while (dot(a, b, convexHull.get(l)) > dot(a, b, convexHull.get((l + 1) % n))) {
                l = (l + 1) % n;
            }

            // 计算当前矩形的面积
            double width = distanceToLine(a, b, convexHull.get(j));
            double height = distanceToLine(convexHull.get(k), convexHull.get(l), a);
            double area = width * height;

            // 更新最小矩形
            if (area < minArea) {
                minArea = area;
                // 计算矩形的四个顶点
                result[0] = project(a, b, convexHull.get(k));
                result[1] = project(a, b, convexHull.get(l));
                result[2] = add(result[1], subtract(b, a));
                result[3] = add(result[0], subtract(b, a));
            }
        }
        List<Point2D> finalDd = restoreCoordinates(ddl, angleF);
        System.out.println("外包矩形顶点：" + finalDd);
        System.out.println("最小外包矩形面积：" + minArea);
        return result;
    }

    // 计算旋转角度
    private static double rotationAngle(Point2D a, Point2D b) {
        double k = (b.getY() - a.getY()) / (b.getX() - a.getX());
        System.out.println("斜率：" + k);
        return Math.atan(k);
    }

    // 计算旋转后的坐标
    private static List<Point2D> rotationCoordinates(List<Point2D> lp, double angle) {
        List<Point2D> result = new ArrayList<>();
        for (Point2D p : lp) {
            result.add(new Point2D.Double((p.getX() * Math.cos(angle) - p.getY() * Math.sin(angle)), (p.getX() * Math.sin(angle) + p.getY() * Math.cos(angle))));
        }
        // Point2D a = new Point2D.Double((p.getX() * Math.cos(angle) - p.getY() * Math.sin(angle)), (p.getX() * Math.sin(angle) + p.getY() * Math.cos(angle)));
        System.out.println("原坐标：" + lp + "\n旋转后：" + result);
        return result;
    }

    //计算还原后的坐标
    private static List<Point2D> restoreCoordinates(List<Point2D> convexHull, double angle) {
        List<Point2D> result = new ArrayList<>();
        for (Point2D p : convexHull) {
            result.add(new Point2D.Double(p.getX() * Math.cos(angle) + p.getY() * Math.sin(angle), -p.getX() * Math.sin(angle) + p.getY() * Math.cos(angle)));
        }
        return result;
    }

    //计算外包矩形的面积
    private static double rotationArea(List<Point2D> lp, List<Point2D> dd) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double area = 0;
        double width = 0;
        double height = 0;
        for (Point2D p : lp) {
            if(p.getX() < minX) minX = p.getX();
            if(p.getY() < minY) minY = p.getY();
            if(p.getX() > maxX) maxX = p.getX();
            if(p.getY() > maxY) maxY = p.getY();
        }
        dd.add(new Point2D.Double(minX, minY));
        dd.add(new Point2D.Double(maxX, minY));
        dd.add(new Point2D.Double(maxX, maxY));
        dd.add(new Point2D.Double(minX, maxY));
        width = maxX - minX;
        height = maxY - minY;
        area = width * height;
        System.out.println("长：" + width + " 宽：" + height + " 面积：" + area);
        return area;
    }

    // 计算叉积
    private static double cross(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY()) - (a.getY() - o.getY()) * (b.getX() - o.getX());
    }

    // 计算点积
    private static double dot(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getX() - o.getX()) + (a.getY() - o.getY()) * (b.getY() - o.getY());
    }

    // 计算点 p 在直线 ab 上的投影
    private static Point2D project(Point2D a, Point2D b, Point2D p) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double t = ((p.getX() - a.getX()) * dx + (p.getY() - a.getY()) * dy) / (dx * dx + dy * dy);
        return new Point2D.Double(a.getX() + t * dx, a.getY() + t * dy);
    }

    // 向量相加
    private static Point2D add(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
    }

    // 向量相减
    private static Point2D subtract(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public static void main(String[] args) {
        List<Point2D> points = new ArrayList<>();
//        points.add(new Point2D.Double(0, 0));
//        points.add(new Point2D.Double(4, 0));
//        points.add(new Point2D.Double(4, 4));
//        points.add(new Point2D.Double(0, 4));
//        points.add(new Point2D.Double(2, 2));

        points.add(new Point2D.Double(2, 0));
        points.add(new Point2D.Double(3, 1));
        points.add(new Point2D.Double(1, 3));
        points.add(new Point2D.Double(0, 2));

        List<Point2D> convexHull = computeConvexHull(points);
        Point2D[] mbr = findMinimumBoundingRectangle(convexHull);

        System.out.println("最小外包矩形的四个顶点：");
        for (Point2D p : mbr) {
            System.out.println("(" + p.getX() + ", " + p.getY() + ")");
        }
    }
}