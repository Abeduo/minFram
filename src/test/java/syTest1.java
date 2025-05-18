import CORE.ImprovedContourExtractor;
import CORE.MinimumBoundingRectangle;
import Utils.PolygonAreaCalculator;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class syTest1 {
    public static void main(String[] args) throws IOException {
        File folder = new File("D://杂物//测试数据//复杂图形");
        String[] s = folder.list();
        double e_area_h = 0;
        double time_h = 0;
        double mjjc = 0;
        double jd_mjjc = 0;
        for (String s1 : s){
            System.out.println(s1);
            String filePath = "D://杂物//测试数据//复杂图形//"+s1;
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            List<Point2D> list = ImprovedContourExtractor.extractContours(image);
            list = MinimumBoundingRectangle.getCornerPoints(list);
            list = MinimumBoundingRectangle.computeConvexHull(list);
            double area_shape = PolygonAreaCalculator.calculatePolygonArea(list);
            double start_time = System.currentTimeMillis();
            Point2D[] point2DS = MinimumBoundingRectangle.findMinimumBoundingRectangle(list,1);
            long end_time = System.currentTimeMillis();
            double time = end_time - start_time;
            time_h = time_h + time;
            double area_rect = PolygonAreaCalculator.calculatePolygonArea(Arrays.asList(point2DS));
            double e_area = area_shape /area_rect;
            double jd_mjjc_tp = area_rect - area_shape;
            jd_mjjc = jd_mjjc + jd_mjjc_tp;
            double mjjc_tp = jd_mjjc_tp / area_shape;
            mjjc = mjjc + mjjc_tp;
            e_area_h = e_area_h + e_area;
            System.out.println(e_area_h);
            System.out.println(mjjc);
            System.out.println(time+"hm");
        }
        System.out.println("平均紧致度："+e_area_h / s.length); // 0.60 0.66 0.68 0.69 0.70||0.64 0.63 0.65 0.7 0.72||0.80
        System.out.println("平均面积极差："+mjjc / s.length);
        System.out.println("平均绝对面积极差：" + jd_mjjc / s.length);
        System.out.println("平均计算时间："+time_h / s.length); // |0.65 0.7 0.81 0.8|
    }
}
