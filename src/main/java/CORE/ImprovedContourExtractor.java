package CORE;

import BASE.Graph;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static CORE.DirectionalConvexHull.computeDirectionalConvexHulls;


public class ImprovedContourExtractor {

    public static void ice(String imagePath) {
        try {
            Graph graph = new Graph();
            graph.setImagePath(imagePath);
            // 读取图片
            BufferedImage image = ImageIO.read(new File(imagePath));

            if (image == null) {
                System.out.println("图片加载失败，请检查路径是否正确。");
                return;
            }

            // 提取轮廓
            List<Point2D> contourPoints = extractContours(image);
            graph.setContourPoints(contourPoints);

            //计算拐点
            List<Point2D> gPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
            graph.setCornerPoints(gPoints);
            PointVisualizer.visualizePoints(gPoints, "角点");
            //计算凸包
            MinimumBoundingRectangle.computeConvexHull(gPoints);
            List<Point2D> convexHull = computeDirectionalConvexHulls(gPoints, image.getWidth(), image.getHeight());


            //List<Point2D> convexHull =  MinimumBoundingRectangle.computeConvexHull(gPoints);
            PointVisualizer.visualizePoints(convexHull, "凸包");
            System.out.println("宽:"+image.getWidth());
            System.out.println("长:"+image.getHeight());
            graph.setConvexHull(convexHull);

            //计算最小外包矩形
            Point2D[] mbr = MinimumBoundingRectangle.findMinimumBoundingRectangle(convexHull, 0);
            List<Point2D> minRec = new ArrayList<>();
            System.out.println("最小外包矩形的四个顶点：");
            for (Point2D p : mbr) {
                minRec.add(p);
                System.out.println("(" + p.getX() + ", " + p.getY() + ")");
            }
            graph.setMinimumRectangleVertex(minRec);

            // 输出轮廓的点集坐标
            System.out.println("轮廓的点集坐标：");
            for (Point2D point : contourPoints) {
                System.out.println("(" + point.getX() + ", " + point.getY() + ")");
            }

            //最小矩形的轮廓点集
            List<Point2D> minFRec = MinimumBoundingRectangle.computeRec(minRec);
            graph.setMinimumRectangleContour(minFRec);

            //过滤掉超出边界的点
            List<Point2D> filterMinFRec = MinimumBoundingRectangle.filterOutPoints(minFRec, image.getWidth(), image.getHeight());
            graph.setFilterMinimumRectangleContour(filterMinFRec);
            graph.setCreateTime(new Date());

            // 可视化轮廓
            // PointVisualizer.onlyContour(image, contourPoints);
            //PointVisualizer.picAndContour(image, contourPoints, "图形轮廓");
            //PointVisualizer.picAndContour(image, convexHull, "凸包");
            PointVisualizer.picAndContour(image, filterMinFRec, "最小矩形框");
            // visualizeContours(image, contourPoints);
            //PointVisualizer.visualizePoints(contourPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将图像转换为灰度图像
    public static BufferedImage toGrayScale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                grayImage.setRGB(x, y, rgb); //自动转换
            }
        }

        return grayImage;
    }

    // 检测背景是否为白色
    public static boolean isBackgroundWhite(BufferedImage grayImage) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();

        // 统计四个角的像素值
        int corner1 = grayImage.getRGB(0, 0) & 0xFF;
        int corner2 = grayImage.getRGB(width - 1, 0) & 0xFF;
        int corner3 = grayImage.getRGB(0, height - 1) & 0xFF;
        int corner4 = grayImage.getRGB(width - 1, height - 1) & 0xFF;

        // 如果四个角中有三个以上是白色（灰度值 > 127），则认为背景是白色
        int whiteCount = 0;
        if (corner1 > 127) whiteCount++;
        if (corner2 > 127) whiteCount++;
        if (corner3 > 127) whiteCount++;
        if (corner4 > 127) whiteCount++;

        return whiteCount >= 3;
    }

    // 自适应阈值二值化
    public static BufferedImage adaptiveThreshold(BufferedImage grayImage, boolean isBackgroundWhite) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int blockSize = 15; // 邻域大小
        int constant = 5; // 常数

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int sum = 0;
                int count = 0;

                // 计算邻域均值
                for (int dy = -blockSize / 2; dy <= blockSize / 2; dy++) {
                    for (int dx = -blockSize / 2; dx <= blockSize / 2; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            sum += grayImage.getRGB(nx, ny) & 0xFF;
                            count++;
                        }
                    }
                }

                int mean = sum / count;
                int pixel = grayImage.getRGB(x, y) & 0xFF;
                int binary = (pixel > mean - constant) ? 255 : 0;

                if (isBackgroundWhite) {
                    binary = 255 - binary; // 反转二值化结果
                }

                binaryImage.setRGB(x, y, binary == 255 ? 0xFFFFFF : 0x000000);
            }
        }

        return binaryImage;
    }

    // 提取轮廓
    public static List<Point2D> extractContours(BufferedImage image) {
        // 转换为灰度图像
        BufferedImage grayImage = toGrayScale(image);

        // 动态检测背景颜色（黑色或白色）
        boolean isBackgroundWhite = isBackgroundWhite(grayImage);

        // 使用自适应阈值进行二值化
        BufferedImage binaryImage = adaptiveThreshold(grayImage, isBackgroundWhite);

        int width = binaryImage.getWidth();
        int height = binaryImage.getHeight();
        List<Point2D> contourPoints = new ArrayList<>();

        // 遍历图像，查找轮廓点
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int center = binaryImage.getRGB(x, y) & 0xFF; // 中心像素
                boolean isForeground = (isBackgroundWhite) ? (center == 0) : (center == 255); // 判断是否为前景

                if (isForeground) {
                    // 检查8邻域
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue; // 跳过中心
                            int neighbor = binaryImage.getRGB(x + dx, y + dy) & 0xFF;
                            boolean isNeighborBackground = (isBackgroundWhite) ? (neighbor == 255) : (neighbor == 0);
                            if (isNeighborBackground) { // 如果邻域有背景
                                contourPoints.add(new Point2D.Double(x, y));
                                break;
                            }
                        }
                    }
                }
            }
        }

        return contourPoints;
    }
}