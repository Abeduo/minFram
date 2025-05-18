import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;
import CORE.PointVisualizer;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static CORE.MinimumBoundingRectangle.newFilter;


// 间隔值为newFilter中第二个参数，需要手动修改,间隔值为1表示没有过滤。面积变化率也需要手动计算，所有数据(最小框定矩形的长和宽以及面积)均展示在数据展示窗口
// 原始点集数量为10060，原始面积为556853.4000000001
// 面积变化率=|(原始最小框定矩形面积 - 过滤后最小框定矩形面积)| / 原始最小框定矩形面积 * 100%
// 过滤比 = 1 - 过滤后点集数量 / 原点集数量
public class biao31 {
    public static void main(String[] args) throws IOException {
        double glb = 0;
        File file = new File("src/main/resources/测试数据/复杂图形/圆.png");
        BufferedImage image = ImageIO.read(file);
        List<Point2D> contours = ImprovedContourExtractor.extractContours(image);  // 原始点集数量 10060
        List<Point2D> filter = newFilter(contours,1);
        String s4 = "过滤后点集数量：" + filter.size();
        glb = 1 - ((double) filter.size() / contours.size());
        PointVisualizer.visualizePoints(filter, s4);
        MinimumBoundingRectangle.findMinimumBoundingRectangle(filter, 0);
        System.out.println(s4);
        System.out.println("过滤比：" + glb);
    }
}
