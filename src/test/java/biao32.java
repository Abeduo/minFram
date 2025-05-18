import CORE.DirectionalConvexHull;
import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;
import CORE.PointVisualizer;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class biao32 {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/Discord替代设计_1745589491.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contours = ImprovedContourExtractor.extractContours(image);

        long start_time1 = System.currentTimeMillis();
        List<Point2D> convexHull = MinimumBoundingRectangle.computeConvexHull(contours);
        long time1 = System.currentTimeMillis() - start_time1;
        PointVisualizer.visualizePoints(convexHull, "优化前");

        long start_time2 = System.currentTimeMillis();
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contours);
        List<Point2D> convexHulls = DirectionalConvexHull.computeDirectionalConvexHulls(cornerPoints, image.getWidth(), image.getHeight());
        long time2 = System.currentTimeMillis() - start_time2;

        PointVisualizer.visualizePoints(convexHulls, "优化后");
        System.out.println("轮廓点集数量：" + contours.size());
        System.out.println("优化前凸包点集数量：" + convexHull.size());
        System.out.println("优化后凸包点集数量：" + convexHulls.size());
        System.out.println("优化前用时：" + time1);
        System.out.println("优化后用时：" + time2);
    }
}
