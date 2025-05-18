import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

//1.src/main/resources/测试数据/复杂图形/圆.png                    表3-3
//2.src/main/resources/测试数据/复杂图形/8号球_1745587782.png      表3-4
//3.src/main/resources/测试数据/复杂图形/CSS3图标_1745589473.png   表3-5
//test1是优化前，test2是优化后
public class biao33 {
    public static void main(String[] args) throws IOException {
        test1();
        //test2();
    }

    //50625  5268
    public static void test1() throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/8号球_1745587782.png");
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

    public static void test2() throws IOException {
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

}
