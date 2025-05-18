package Utils;

import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PolygonAreaCalculator {

    /**
     * 使用鞋带公式计算多边形的面积
     * @param points 多边形的顶点列表（按顺序排列）
     * @return 多边形的面积
     */
    public static double calculatePolygonArea(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            throw new IllegalArgumentException("多边形至少需要3个顶点");
        }

        double area = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            Point2D current = points.get(i);
            Point2D next = points.get((i + 1) % n); // 循环到第一个点
            area += (current.getX() * next.getY() - next.getX() * current.getY());
        }

        return Math.abs(area) / 2.0;
    }

    public static void main(String[] args) throws IOException {
        //D://杂物//测试数据//八边形//八边形_1.png
        //D://杂物//pic//测试相交1.png
        File file = new File("D://杂物//测试数据//八边形//八边形_1.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> list = ImprovedContourExtractor.extractContours(image);
        list = MinimumBoundingRectangle.getCornerPoints(list);
        list = MinimumBoundingRectangle.computeConvexHull(list);
        //list = MinimumBoundingRectangle.computeConvexHull(list);
        // 计算多边形的面积
        double area = calculatePolygonArea(list);
        System.out.println("多边形的面积为: " + area);
    }
}