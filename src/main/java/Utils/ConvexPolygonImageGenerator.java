package Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.geom.Point2D;

public class ConvexPolygonImageGenerator {

    /**
     * 生成随机凸多边形并保存为PNG图片
     * @param vertexCount 顶点数量(至少3个)
     * @param imageWidth 图片宽度
     * @param imageHeight 图片高度
     * @param fileName 保存的文件名(不需要扩展名)
     * @return 是否保存成功
     */
    public static boolean generateAndSaveConvexPolygon(int vertexCount, int imageWidth, int imageHeight, String fileName) {
        try {
            // 1. 生成随机凸多边形
            List<Point2D.Double> polygon = generateConvexPolygon(vertexCount, imageWidth * 0.8, imageHeight * 0.8);

            // 2. 创建图像
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            // 3. 设置抗锯齿属性
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 4. 设置背景为白色
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, imageWidth, imageHeight);

            // 5. 绘制多边形
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(5)); // 设置边框粗细

            Path2D.Double path = new Path2D.Double();
            path.moveTo(polygon.get(0).x + imageWidth * 0.01, polygon.get(0).y + imageHeight * 0.01);

            for (int i = 1; i < polygon.size(); i++) {
                path.lineTo(polygon.get(i).x + imageWidth * 0.01, polygon.get(i).y + imageHeight * 0.01);
            }

            path.closePath();
            g2d.draw(path);

            // 6. 保存图片
            File outputDir = new File("D:\\杂物\\测试数据\\中等复杂图形");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName + ".png");
            ImageIO.write(image, "png", outputFile);

            g2d.dispose();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成随机凸多边形
     * @param vertexCount 顶点数量(至少3个)
     * @param width 生成区域的宽度
     * @param height 生成区域的高度
     * @return 按顺时针或逆时针顺序排列的顶点列表
     */
    private static List<Point2D.Double> generateConvexPolygon(int vertexCount, double width, double height) {
        if (vertexCount < 3) {
            throw new IllegalArgumentException("顶点数不能少于3");
        }

        Random random = new Random();
        List<Point2D.Double> points = new ArrayList<>();
        List<Double> angles = new ArrayList<>();

        // 1. 生成随机角度
        for (int i = 0; i < vertexCount; i++) {
            angles.add(random.nextDouble() * 2 * Math.PI);
        }
        Collections.sort(angles);

        // 2. 为每个角度生成随机半径
        double[] radii = new double[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            radii[i] = 0.1 + 0.9 * random.nextDouble(); // 避免半径过小
        }

        // 3. 转换为笛卡尔坐标
        double centerX = width / 2;
        double centerY = height / 2;
        double maxRadius = Math.min(width, height) / 2 * 0.9; // 避免超出边界

        for (int i = 0; i < vertexCount; i++) {
            double radius = radii[i] * maxRadius;
            double x = centerX + radius * Math.cos(angles.get(i));
            double y = centerY + radius * Math.sin(angles.get(i));
            points.add(new Point2D.Double(x, y));
        }

        return points;
    }

    public static boolean createImage(int pointNum, int imageWidth, int imageHeight, String fileName){
        // 创建图像
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);

        Point2D[] point2DS = new Point2D[pointNum];
        for(int i=0;i<pointNum;i++){
            Random random = new Random();
            Point2D p = new Point2D.Double(random.nextInt(imageWidth-100) + 50, random.nextInt(imageHeight-100) + 50);
            point2DS[i] = p;
        }
        List<Point2D> list = createLine(point2DS[point2DS.length-1], point2DS[0]);
        for (int i=0;i<pointNum-1;i++){
            list.addAll(createLine(point2DS[i], point2DS[i+1]));
        }

        // 设置点颜色为黑色
        g2d.setColor(Color.BLACK);

        // 5. 绘制多边形
        g2d.setStroke(new BasicStroke(1)); // 设置边框粗细
        Path2D.Double path = new Path2D.Double();

        path.moveTo(list.get(0).getX(), list.get(0).getY());
        for (Point2D point2D : list) {
            path.lineTo(point2D.getX(), point2D.getY());
        }

        path.closePath();
        g2d.draw(path);

        // 6. 保存图片
        File outputDir = new File("D:\\杂物\\测试数据\\中等复杂图形");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, fileName + ".png");
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        g2d.dispose();
        return true;
    }

    public static List<Point2D> createLine(Point2D a, Point2D b){
        List<Point2D> points = new ArrayList<>();

        // 获取两点的坐标
        double x1 = a.getX();
        double y1 = a.getY();
        double x2 = b.getX();
        double y2 = b.getY();

        // 计算两点之间的距离
        double distance = a.distance(b);

        // 计算 x 和 y 方向的步长
        double dx = (x2 - x1) / distance;
        double dy = (y2 - y1) / distance;

        // 遍历两点之间的每个点
        for (int i = 0; i <= distance; i++) {
            double x = x1 + i * dx;
            double y = y1 + i * dy;
            points.add(new Point2D.Double(x, y));
        }

        return points;
    }

    public static void main(String[] args) {
        // 示例：生成一个8边形，图片大小为500x500，保存为"polygon1.png"
        for (int i=1; i<=20; i++){
            boolean success = generateAndSaveConvexPolygon(10, 500, 500, "十边形_"+i);
            System.out.println(i + "图片生成" + (success ? "成功" : "失败"));
        }
        //createImage(4,500,500,"四边形_");

    }
}