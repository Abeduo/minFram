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

public class tu56 {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/React图标_1745589355.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contours = ImprovedContourExtractor.extractContours(image);
        List<Point2D> cornerPoints = MinimumBoundingRectangle.getCornerPoints(contours);
        List<Point2D> convexHulls = DirectionalConvexHull.computeDirectionalConvexHulls(cornerPoints, image.getWidth(), image.getHeight());
        PointVisualizer.visualizePoints(convexHulls, "凸包点集");
        PointVisualizer.picAndContour(image, convexHulls, "凸包与原图");
    }
}
