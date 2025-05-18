import CORE.ImprovedContourExtractor;
import CORE.PointVisualizer;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class tu55 {
    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/测试数据/复杂图形/MongoDB图标_1745589338.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contours = ImprovedContourExtractor.extractContours(image);
        PointVisualizer.visualizePoints(contours, "轮廓点集");
        PointVisualizer.picAndContour(image, contours, "图片与轮廓");
    }
}
