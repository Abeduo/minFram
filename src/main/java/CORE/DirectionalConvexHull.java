package CORE;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static CORE.ImprovedContourExtractor.extractContours;

public class DirectionalConvexHull {
    // 主方法：将点集按图片中心划分区域并计算各区域的方向凸包
    public static List<Point2D> computeDirectionalConvexHulls(List<Point2D> points, int imageWidth, int imageHeight) {
        List<Point2D> result = new ArrayList<>();
        List<Point2D> safePoints = new ArrayList<>(points);
        sort(safePoints);

        if (safePoints.isEmpty()) return result;

        double centerX = imageWidth / 2.0;
        double centerY = imageHeight / 2.0;

        Map<String, List<Point2D>> regions = partitionPoints(safePoints, centerX, centerY);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Map<String, Future<List<Point2D>>> futureMap = new HashMap<>();

        for (Map.Entry<String, List<Point2D>> entry : regions.entrySet()) {
            String region = entry.getKey();
            List<Point2D> regionPoints = entry.getValue();

            if (!regionPoints.isEmpty()) {
                Future<List<Point2D>> future = executor.submit(() ->
                        computeRegionalConvexHull(regionPoints, region)
                );
                futureMap.put(region, future);
            }
        }

        boolean allTasksCompleted = true;

        for (Map.Entry<String, Future<List<Point2D>>> entry : futureMap.entrySet()) {
            try {
                List<Point2D> convexHull = entry.getValue().get();
                result.addAll(convexHull);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                allTasksCompleted = false;
            }
        }

        if (allTasksCompleted) {
            executor.shutdown();
        } else {
            // 处理未完成的任务，例如重新提交或记录错误
            executor.shutdownNow();
        }
        result = MinimumBoundingRectangle.computeConvexHull(result);
        // result.add(safePoints.get(0));
        return result;
    }

    public static List<Point2D> computeDirectionalConvexHulls0(List<Point2D> points, int imageWidth, int imageHeight){
        List<Point2D> safePoints = new ArrayList<>(points);
        List<Point2D> res = new ArrayList<>();
        if (safePoints.isEmpty()) return res;

        double centerX = imageWidth / 2.0;
        double centerY = imageHeight / 2.0;

        Map<String, List<Point2D>> regions = partitionPoints(safePoints, centerX, centerY);
        for (String key : regions.keySet()){
            List<Point2D> list = computeRegionalConvexHull(regions.get(key), key);
            res.addAll(list);
        }
        PointVisualizer.visualizePoints(res, "方法里的凸包"+regions.size());
        return res;
    }

    // 将点集按中心点划分为四个区域
    private static Map<String, List<Point2D>> partitionPoints(List<Point2D> points, double centerX, double centerY) {
        Map<String, List<Point2D>> regions = new HashMap<>();
        regions.put("TOP_LEFT", new ArrayList<>());
        regions.put("TOP_RIGHT", new ArrayList<>());
        regions.put("BOTTOM_LEFT", new ArrayList<>());
        regions.put("BOTTOM_RIGHT", new ArrayList<>());

        for (Point2D p : points) {
            if (p.getX() <= centerX && p.getY() <= centerY) {
                regions.get("TOP_LEFT").add(p);
            } else if (p.getX() > centerX && p.getY() <= centerY) {
                regions.get("TOP_RIGHT").add(p);
            } else if (p.getX() <= centerX && p.getY() > centerY) {
                regions.get("BOTTOM_LEFT").add(p);
            } else {
                regions.get("BOTTOM_RIGHT").add(p);
            }
        }

        return regions;
    }

    // 计算特定区域的方向凸包
    private static List<Point2D> computeRegionalConvexHull(List<Point2D> points, String region) {
        // 点集小于3个，直接返回
        if (points.size() < 3) return new ArrayList<>(points);

        Point2D pivot = findPivotPoint(points, region);
        List<Point2D> sortedPoints = sortByAngle(points, pivot, region);
        return scan(sortedPoints, pivot, region);
    }

    // 根据区域找到枢轴点（起始点）
    private static Point2D findPivotPoint(List<Point2D> points, String region) {
        Point2D pivot = points.get(0);

        switch (region) {
            case "TOP_LEFT":
                // 左上区域：找x和y都最小的点（左下角）
                for (Point2D p : points) {
                    if (p.getX() < pivot.getX() || (p.getX() == pivot.getX() && p.getY() < pivot.getY())) {
                        pivot = p;
                    }
                }
                break;
            case "TOP_RIGHT":
                // 右上区域：找x最大且y最小的点（左上角）
                for (Point2D p : points) {
                    if (p.getX() > pivot.getX() || (p.getX() == pivot.getX() && p.getY() < pivot.getY())) {
                        pivot = p;
                    }
                }
                break;
            case "BOTTOM_LEFT":
                // 左下区域：找x最小且y最大的点（右下角）
                for (Point2D p : points) {
                    if (p.getX() < pivot.getX() || (p.getX() == pivot.getX() && p.getY() > pivot.getY())) {
                        pivot = p;
                    }
                }
                break;
            case "BOTTOM_RIGHT":
                // 右下区域：找x和y都最大的点（右上角）
                for (Point2D p : points) {
                    if (p.getX() > pivot.getX() || (p.getX() == pivot.getX() && p.getY() > pivot.getY())) {
                        pivot = p;
                    }
                }
                break;
        }

        return pivot;
    }

    // 根据区域和枢轴点对顶点按角度排序
    private static List<Point2D> sortByAngle(List<Point2D> points, Point2D pivot, String region) {
        List<Point2D> sortedPoints = new ArrayList<>(points);
        sortedPoints.remove(pivot);

        // 根据区域确定角度范围
        double minAngle = 0;
        double maxAngle = 0;

        switch (region) {
            case "TOP_LEFT":
                /*minAngle = -Math.PI / 2;  // -90度
                maxAngle = Math.PI / 2;   // 90度*/
                minAngle = 0;
                maxAngle = Math.PI;
                break;
            case "TOP_RIGHT":
                /*minAngle = 0;             // 0度
                maxAngle = Math.PI;       // 180度*/
                minAngle = Math.PI / 2;
                maxAngle = 3 * Math.PI / 2;
                break;
            case "BOTTOM_LEFT":
                /*minAngle = Math.PI;       // 180度
                maxAngle = 2 * Math.PI;   // 360度*/
                minAngle = -Math.PI / 2;
                maxAngle = Math.PI / 2;
                break;
            case "BOTTOM_RIGHT":
                /*minAngle = Math.PI / 2;   // 90度
                maxAngle = 3 * Math.PI / 2; // 270度*/
                minAngle = Math.PI;
                maxAngle = 2 * Math.PI;
                break;
        }

        // 按极角排序，并过滤超出角度范围的点
        final double fMinAngle = minAngle;
        final double fMaxAngle = maxAngle;
        final Point2D fPivot = pivot;

        sortedPoints.sort((p1, p2) -> {
            double angle1 = Math.atan2(p1.getY() - fPivot.getY(), p1.getX() - fPivot.getX());
            double angle2 = Math.atan2(p2.getY() - fPivot.getY(), p2.getX() - fPivot.getX());

            // 调整角度到[0, 2π)范围
            angle1 = normalizeAngle(angle1);
            angle2 = normalizeAngle(angle2);

            // 检查角度是否在范围内
            boolean inRange1 = isAngleInRange(angle1, fMinAngle, fMaxAngle);
            boolean inRange2 = isAngleInRange(angle2, fMinAngle, fMaxAngle);

            if (!inRange1 && !inRange2) return 0;
            if (!inRange1) return 1;
            if (!inRange2) return -1;

            return Double.compare(angle1, angle2);
        });

        // 添加枢轴点作为第一个点
        List<Point2D> result = new ArrayList<>();
        result.add(pivot);
        result.addAll(sortedPoints);

        return result;
    }

    private static List<Point2D> sort(List<Point2D> points) {
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
        //System.out.println("按级角排序后的点：" + points);
        return points;
    }

    // 将角度归一化到[0, 2π)范围
    private static double normalizeAngle(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    // 检查角度是否在指定范围内
    private static boolean isAngleInRange(double angle, double minAngle, double maxAngle) {
        if (minAngle < maxAngle) {
            return angle >= minAngle && angle <= maxAngle;
        } else {
            // 范围跨0度
            return angle >= minAngle || angle <= maxAngle;
        }
    }

    private static List<Point2D> scan(List<Point2D> points, Point2D pivot, String region) {
        if (points.size() < 3) return new ArrayList<>(points);

        Stack<Point2D> hull = new Stack<>();
        hull.push(pivot);
        hull.push(points.get(1));

        for (int i = 2; i < points.size(); i++) {
            Point2D top = hull.pop();
            while (!hull.isEmpty() && cross(hull.peek(), top, points.get(i)) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points.get(i));
        }

        return new ArrayList<>(hull);
    }

    // 计算向量叉积
    private static double cross(Point2D a, Point2D b, Point2D c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
    }


    // 示例使用方法
    public static void main(String[] args) throws IOException{
        File file = new File("D://杂物//测试数据//复杂图形//Discord替代设计_1745589491.png");
        System.out.println(file.getPath());
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        PointVisualizer.visualizePoints(cornerPoints, "角点");
        Long start_time = System.currentTimeMillis();
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(cornerPoints);
        Long end_time = System.currentTimeMillis();
        System.out.println(end_time-start_time);
        PointVisualizer.visualizePoints(convexHull,"原凸包");
        Long start_time1 = System.currentTimeMillis();
        List<Point2D> convexHull2 = computeDirectionalConvexHulls(cornerPoints, image.getWidth(), image.getHeight());
        Long end_time1 = System.currentTimeMillis();
        System.out.println(end_time1 - start_time1);
        PointVisualizer.visualizePoints(convexHull2, "凸包");
        System.out.println(image.getWidth());
        System.out.println(image.getHeight());

    }
}