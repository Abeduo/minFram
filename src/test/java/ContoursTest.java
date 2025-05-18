import CORE.ImprovedContourExtractor;
import CORE.PointVisualizer;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import CORE.MinimumBoundingRectangle;
import org.junit.jupiter.api.Test;


public class ContoursTest {
    public static void main(String[] args) throws IOException {
        test6();
    }

    public static void test5() throws IOException {
        File file = new File("D://杂物//测试数据//复杂图形//CSS3图标_1745589473.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        long start_time1 = System.currentTimeMillis();
        List<Point2D> convexHull1 = MinimumBoundingRectangle.computeConvexHull(contourPoints);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(convexHull1, 0);
        long end_time1 = System.currentTimeMillis();
        long time1 = end_time1 - start_time1;
        System.out.println("数据量：" + contourPoints.size());
        System.out.println("原算法计算时间：" + time1);
    }

    public static void test6() throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/CSS3图标_1745589473.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        System.out.println("数据量：" + contourPoints.size());
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        long start_time = System.currentTimeMillis();
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(cornerPoints);
        //List<Point2D> filterCornerPoints = newFilter(cornerPoints, 50);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(convexHull, 0);
        long end_time = System.currentTimeMillis();
        long time = end_time - start_time;
        System.out.println("优化后算法计算时间：" + time);
    }
    public static void test4() throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/圆.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        /*String s1 = "轮廓点集数量" + contourPoints.size();
        PointVisualizer.visualizePoints(contourPoints, s1);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(contourPoints, 0);*/

        List<Point2D> filter2 = newFilter(contourPoints,1);
        String s4 = "filter2点集数量" + filter2.size();
        PointVisualizer.visualizePoints(filter2, s4);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(filter2, 0);
        System.out.println(s4);
        System.out.println("------------------------------------------------------------------------\n");
    }

    public static void test3() throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/8号球_1745587782.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        /*String s1 = "轮廓点集数量" + contourPoints.size();
        PointVisualizer.visualizePoints(contourPoints, s1);*/
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        /*String s2 = "拐点点集数量" + cornerPoints.size();
        PointVisualizer.visualizePoints(cornerPoints, s2);*/
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(cornerPoints);
        String s3 = "凸包点集数量" + convexHull.size();
        PointVisualizer.visualizePoints(convexHull, s3);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(convexHull, 0);
        List<Point2D> filter2 = filter(convexHull,3);
        String s4 = "filter2点集数量" + filter2.size();
        PointVisualizer.visualizePoints(convexHull, s4);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(filter2, 0);
    }

    public static void test0() throws IOException {
        File file = new File("D://杂物//测试数据//复杂图形//Discord替代设计_1745589491.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        System.out.println("轮廓点数据量："+contourPoints.size());
        List<Point2D> convexHull1 = MinimumBoundingRectangle.computeConvexHull(contourPoints);
        System.out.println("不计算角点数据量："+convexHull1.size());
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        List<Point2D> convexHull2 = MinimumBoundingRectangle.computeConvexHull(cornerPoints);
        System.out.println("计算角点数据量："+convexHull2.size());
    }

    public static void test1() throws IOException {
        File file = new File("D://杂物//测试数据//复杂图形//Discord替代设计_1745589491.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        System.out.println("轮廓点数据量："+contourPoints.size());
        System.out.println("角点数据量："+cornerPoints.size());
        PointVisualizer.visualizePoints(contourPoints, "轮廓点集");
        PointVisualizer.visualizePoints(cornerPoints,"角点点集");
        double start_time = System.currentTimeMillis();
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(contourPoints);
        PointVisualizer.picAndContour(image, convexHull, "计算拐点");
        double end_time = System.currentTimeMillis();
        double time = end_time - start_time;
        System.out.println("计算拐点："+time);
    }

    public static void test2() throws IOException {
        double start_time = System.currentTimeMillis();
        File file = new File("D://杂物//测试数据//复杂图形//Discord替代设计_1745589491.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> list = ImprovedContourExtractor.extractContours(image);
        //List<Point2D> contourPoints = MinimumBoundingRectangle.getCornerPoints(list);
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(list);
        PointVisualizer.visualizePoints(convexHull, "不计算拐点");
        PointVisualizer.picAndContour(image, convexHull, "凸包+图片");
        double end_time = System.currentTimeMillis();
        double time = end_time - start_time;
        System.out.println("不计算拐点："+time);
    }

    public static List<Point2D> filter(List<Point2D> list,int n) {
        List<Point2D> res = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            if(i%n!=0){
                res.add(list.get(i));
            }
        }
        return res;
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
