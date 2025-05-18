package CORE;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

// 取点 -》 筛选出凸包点 -》 旋转卡壳 -》 最小外包矩形
public class MinimumBoundingRectangle {

    //计算拐点
    public static List<Point2D> getCornerPoints(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            return points; // 如果点集少于 3 个点，直接返回
        }

        List<Point2D> cornerPoints = new ArrayList<>();
        cornerPoints.add(points.get(0)); // 添加第一个点

        for (int i = 1; i < points.size(); i++) {
            // 检查当前点是否与前两个点共线
            if (i < points.size() - 1 && cross(points.get(i - 1), points.get(i), points.get(i + 1)) == 0) {
                continue; // 如果共线，跳过当前点
            }
            cornerPoints.add(points.get(i)); // 否则，添加当前点
        }

        return cornerPoints;
    }

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

        // 使用扫描法计算凸包
        Stack<Point2D> hull = new Stack<>();

        hull.push(points.get(0));
        hull.push(points.get(1));

        for (int i = 2; i < points.size(); i++) {
            Point2D top = hull.pop();
            while (!hull.isEmpty() && cross(hull.peek(), top, points.get(i)) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points.get(i));
        }
        System.out.println("构成凸包的点集：" + hull);

        return new ArrayList<>(hull);
    }

    public static List<Point2D> computeConvexHull0(List<Point2D> points) {
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

        // 使用扫描法计算凸包
        Stack<Point2D> hull = new Stack<>();

        hull.push(points.get(0));
        hull.push(points.get(1));

        for (int i = 2; i < points.size(); i++) {
            Point2D top = hull.pop();
            while (!hull.isEmpty() && cross(hull.peek(), top, points.get(i)) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points.get(i));
        }
        System.out.println("构成凸包的点集：" + hull);

        return new ArrayList<>(hull);
    }

    // 计算最小外包矩形
    public static Point2D[] findMinimumBoundingRectangle(List<Point2D> convexHull, int mode) {
        if(convexHull.size() > 1000){
            convexHull = newFilter(convexHull, 50);
        }
        int n = convexHull.size();
        if (n == 0) return null;

        double minArea = Double.MAX_VALUE;
        Point2D[] result = new Point2D[4]; // 存储最小矩形的四个顶点
        double angleF = 0;
        List<Point2D> ddl = new ArrayList<>();
        double weigh = 0;
        double height = 0;

        // 遍历凸包的每一条边
        for (int i = 0, j = 1, k = 1, l = 1; i < n; i++) {
            Point2D a = convexHull.get(i);
            Point2D b = convexHull.get((i + 1) % n);

            //计算旋转的角度
            double angle = rotationAngle(a, b);
            //计算旋转后的凸包点坐标
            List<Point2D> ps = rotationCoordinates(convexHull, angle);
            //记录旋转后的矩形四角坐标
            List<Point2D> dd = new ArrayList<>();
            double[] area2 = rotationArea(ps, dd);
            if (area2[2] < minArea) {
                minArea = area2[2];
                angleF = angle;
                weigh = area2[0];
                height = area2[1];
                //记录外包矩形顶点
                ddl.clear();
                ddl = new ArrayList<>(dd);
            }
        }
        //还原坐标
        List<Point2D> finalDd = restoreCoordinates(ddl, angleF);
        result[0] = finalDd.get(0);
        result[1] = finalDd.get(1);
        result[2] = finalDd.get(2);
        result[3] = finalDd.get(3);
        System.out.println("外包矩形顶点：" + finalDd);
        System.out.println("最小外包矩形面积：" + minArea);
        if(mode == 0){
            PointVisualizer.showDimensions(height, weigh, minArea, "详细数据");
        }
        return result;
    }

    public static Point2D[] fFindMinimumBoundingRectangle(List<Point2D> convexHull, int mode) {
        int n = convexHull.size();
        if (n == 0) return null;

        double minArea = Double.MAX_VALUE;
        Point2D[] result = new Point2D[4]; // 存储最小矩形的四个顶点
        double angleF = 0;
        List<Point2D> ddl = new ArrayList<>();
        double weigh = 0;
        double height = 0;

        // 遍历凸包的每一条边
        for (int i = 0, j = 1, k = 1, l = 1; i < n; i++) {
            Point2D a = convexHull.get(i);
            Point2D b = convexHull.get((i + 1) % n);

            //计算旋转的角度
            double angle = rotationAngle(a, b);
            //计算旋转后的凸包点坐标
            List<Point2D> ps = rotationCoordinates(convexHull, angle);
            //记录旋转后的矩形四角坐标
            List<Point2D> dd = new ArrayList<>();
            double[] area2 = rotationArea(ps, dd);
            if (area2[2] < minArea) {
                minArea = area2[2];
                angleF = angle;
                weigh = area2[0];
                height = area2[1];
                //记录外包矩形顶点
                ddl.clear();
                ddl = new ArrayList<>(dd);
            }
        }
        //还原坐标
        List<Point2D> finalDd = restoreCoordinates(ddl, angleF);
        result[0] = finalDd.get(0);
        result[1] = finalDd.get(1);
        result[2] = finalDd.get(2);
        result[3] = finalDd.get(3);
        System.out.println("外包矩形顶点：" + finalDd);
        System.out.println("最小外包矩形面积：" + minArea);
        if(mode == 0){
            PointVisualizer.showDimensions(height, weigh, minArea, "详细数据");
        }
        return result;
    }

    // 计算旋转角度
    private static double rotationAngle(Point2D a, Point2D b) {
        double x = b.getX() - a.getX();
        double y = b.getY() - a.getY();
        double k = 0;
        double angle = 1.57;
        if(x != 0) {
            k = y / x;
            angle = Math.atan(k);
            System.out.println("斜率：" + k);
        } else {
            if (y < 0 ) {
                angle = -1.57;
            }
            System.out.println("斜率：不存在（垂直）");
        }
        System.out.println("角度：" + angle);
        return angle;
    }

    // 计算旋转后的坐标
    public static List<Point2D> rotationCoordinates(List<Point2D> lp, double angle) {
        List<Point2D> result = new ArrayList<>();
        for (Point2D p : lp) {
            result.add(new Point2D.Double((p.getX() * Math.cos(angle) - p.getY() * Math.sin(angle)), (p.getX() * Math.sin(angle) + p.getY() * Math.cos(angle))));
        }
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
    private static double[] rotationArea(List<Point2D> lp, List<Point2D> dd) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double area = 0;
        double width = 0;
        double height = 0;
        double res[] = new double[3];
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
        res[0] = width;
        res[1] = height;
        res[2] = area;
        return res;
    }

    // 计算叉积
    private static double cross(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY()) - (a.getY() - o.getY()) * (b.getX() - o.getX());
    }

    //计算直线坐标
    public static List<Point2D> computeLine(Point2D a, Point2D b) {
        List<Point2D> listP2D = new ArrayList<>();
        double x = b.getX() - a.getX();
        double y = b.getY() - a.getY();
        double k;
        double c;
        double minX, minY;
        double maxX, maxY;

        // y = kx + c
        List<Point2D> line = new ArrayList<>();
        // 非垂直
        if(x != 0) {
            k = y / x;
            c = a.getY() - k * a.getX();
            if(a.getX() < b.getX()) {
                minX = a.getX();
                maxX = b.getX();
            } else {
                minX = b.getX();
                maxX = a.getX();
            }
            while (minX <= maxX) {
                listP2D.add(new Point2D.Double(minX, k * minX + c));
                minX ++;
            }
        } else { //垂直（x相等，y不相等）
            if(a.getY() < b.getY()) {
                minY = a.getY();
                maxY = b.getY();
            } else {
                minY = b.getY();
                maxY = a.getY();
            }
            while (minY <= maxY) {
                listP2D.add(new Point2D.Double(a.getX(), minY));
                minY++;
            }
        }

        return listP2D;
    }

    // 计算最小矩形框坐标
    public static List<Point2D> computeRec(List<Point2D> points) {
        List<Point2D> result = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            result.addAll(computeLine(points.get(i), points.get(i+1)));
        }
        result.addAll(computeLine(points.get(points.size()-1), points.get(0)));
        return result;
    }

    //过滤掉超出边界的点
    public static List<Point2D> filterOutPoints(List<Point2D> point2DS, int imageWidth, int imageHeight) {
        List<Point2D> result = new ArrayList<>();
        for (Point2D point2D : point2DS) {
            if((point2D.getX() > 0 && point2D.getX() < imageWidth) && (point2D.getY() > 0 && point2D.getY() < imageHeight)) {
                result.add(point2D);
            }
        }
        return result;
    }


    public static List<Point2D> newFilter(List<Point2D> list, int interval) {
        // 处理特殊情况
        if (list == null || list.isEmpty() || interval <= 0) {
            return Collections.emptyList();
        }

        // 如果间隔为1，保留所有点
        if (interval == 1) {
            return new ArrayList<>(list);
        }

        List<Point2D> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i += interval) {
            res.add(list.get(i));
        }
        return res;
    }
}