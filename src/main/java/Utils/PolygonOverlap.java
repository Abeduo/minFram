package Utils;

import java.awt.geom.Point2D;
import java.util.List;

public class PolygonOverlap {
    /**
     * 判断两个多边形是否重叠（基于分离轴定理，允许边缘重叠）
     * @param g1 第一个多边形的顶点集合
     * @param g2 第二个多边形的顶点集合
     * @return true表示重叠（包括边重合的情况），false表示不重叠
     */
    public static boolean isOverlap(List<Point2D> g1, List<Point2D> g2) {
        // 检查两个多边形是否都有至少3个顶点
        if (g1.size() < 3 || g2.size() < 3) return false;

        // 检查g1的所有边是否能作为分离轴
        if (!checkEdges(g1, g2, true)) return false;

        // 检查g2的所有边是否能作为分离轴
        if (!checkEdges(g2, g1, true)) return false;

        // 所有分离轴检查都通过，说明多边形重叠
        return true;
    }

    // 检查多边形所有边是否能作为分离轴
    // allowEdgeOverlap参数表示是否允许边完全重合的情况
    private static boolean checkEdges(List<Point2D> poly,
                                      List<Point2D> other,
                                      boolean allowEdgeOverlap) {
        for (int i = 0; i < poly.size(); i++) {
            Point2D p1 = poly.get(i);
            Point2D p2 = poly.get((i + 1) % poly.size());

            // 计算边的法向量（分离轴）
            Point2D normal = new Point2D.Double(
                    -(p2.getY() - p1.getY()),
                    p2.getX() - p1.getX()
            );

            // 计算两个多边形在该轴上的投影
            double[] proj1 = project(poly, normal);
            double[] proj2 = project(other, normal);

            // 如果投影不重叠，说明存在分离轴，多边形不重叠
            if (proj1[1] < proj2[0] || proj2[1] < proj1[0]) {
                return false;
            }

            // 如果不允许边完全重合，需要额外检查
            if (!allowEdgeOverlap && proj1[0] == proj2[0] && proj1[1] == proj2[1]) {
                return false;
            }
        }
        return true;
    }

    // 计算多边形在指定轴上的投影范围[min, max]
    private static double[] project(List<Point2D> poly, Point2D axis) {
        double min = dotProduct(poly.get(0), axis);
        double max = min;
        for (Point2D p : poly) {
            double dp = dotProduct(p, axis);
            min = Math.min(min, dp);
            max = Math.max(max, dp);
        }
        return new double[]{min, max};
    }

    // 计算点与轴的点积
    private static double dotProduct(Point2D p, Point2D axis) {
        return p.getX() * axis.getX() + p.getY() * axis.getY();
    }
}