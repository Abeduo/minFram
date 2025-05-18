import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;
import CORE.MultiGraphArrangement;
import CORE.PointVisualizer;
import GUI.ImageUploaderGUI;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static CORE.ImprovedContourExtractor.*;
import static CORE.ImprovedContourExtractor.extractContours;

public class rotateTest {
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("D:\\杂物\\pic\\三角形1.png"));

        // 提取轮廓
        List<Point2D> contourPoints = extractContours(image);

        //计算拐点
        List<Point2D> gPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
        //PointVisualizer.picAndContour(image, gPoints);

        //计算凸包
        List<Point2D> convexHull =  MinimumBoundingRectangle.computeConvexHull(gPoints);

        List<Point2D> ps = MultiGraphArrangement.rotateGraph(contourPoints.get(0), new Point2D.Double(1,1), contourPoints.get(1), contourPoints);
        PointVisualizer.picAndContour(image, ps, "rotateTest");
    }
}
