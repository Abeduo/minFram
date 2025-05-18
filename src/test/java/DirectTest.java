import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;
import CORE.PointVisualizer;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static CORE.DirectionalConvexHull.computeDirectionalConvexHulls;

public class DirectTest {
    public static void main(String[] args) throws IOException {
        File file = new File("D://杂物//测试数据//复杂图形//Discord替代设计_1745589491.png");
        System.out.println(file.getPath());
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contourPoints = ImprovedContourExtractor.extractContours(image);
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(cornerPoints);
        //PointVisualizer.visualizePoints(convexHull,"原凸包");
        List<Point2D> convexHull2 = computeDirectionalConvexHulls(cornerPoints, image.getWidth(), image.getHeight());
        PointVisualizer.visualizePoints(convexHull2, "凸包");


        File file1 = new File("D://杂物//测试数据//复杂图形//Discord替代设计_1745589491.png");
        BufferedImage image1 = ImageIO.read(file1);
        List<Point2D> list = ImprovedContourExtractor.extractContours(image1);
        List<Point2D> cornerPoints1 = MinimumBoundingRectangle.getCornerPoints(list);
        List<Point2D> list1 = computeDirectionalConvexHulls(cornerPoints1, image1.getWidth(), image1.getHeight());
        PointVisualizer.visualizePoints(list1, "凸包测测试");
    }
}
