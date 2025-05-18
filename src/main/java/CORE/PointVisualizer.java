package CORE;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PointVisualizer extends JFrame {

/*    public List<Point2D> points; // 点集坐标
    // private int scale = 1; // 缩放比例，方便查看

    public PointVisualizer(List<Point2D> points) {
        this.points = points;
        initUI();
    }

    private void initUI() {
        setTitle("点集坐标可视化");
        setSize(800, 600); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中

        // 添加自定义画布
        add(new DrawCanvas());
    }

    // 自定义画布类
    private class DrawCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 设置背景颜色
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            // 设置点的颜色
            g.setColor(Color.BLACK);

            // 绘制所有点
            for (Point2D point : points) {
                int x = point.getX() * scale; // 缩放点坐标
                int y = point.getY() * scale;
                g.fillOval(x, y, 5, 5); // 绘制一个实心圆表示点
            }
        }
    }


    public static void pv(List<Point> points) {
        // 示例点集坐标
        *//*List<Point> points = new ArrayList<>();
        points.add(new Point(10, 10));
        points.add(new Point(20, 20));
        points.add(new Point(30, 30));
        points.add(new Point(40, 40));
        points.add(new Point(50, 50));*//*


        // 在事件调度线程中创建并显示GUI
        SwingUtilities.invokeLater(() -> {
            PointVisualizer visualizer = new PointVisualizer(points);
            visualizer.setVisible(true);
        });
    }*/

    // 可视化轮廓(轮廓展示在原图片上)
    public static void picAndContour(BufferedImage image, List<Point2D> contourPoints, String title) {
        // 创建一个新的图像，用于绘制轮廓
        BufferedImage contourImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = contourImage.createGraphics();
        g2d.drawImage(image, 0, 0, null); // 绘制原始图像

        // 设置轮廓颜色
        g2d.setColor(Color.RED);

        // 绘制轮廓点
        try {
            for (Point2D point : contourPoints) {
                contourImage.setRGB((int) point.getX(), (int) point.getY(), Color.RED.getRGB());
            }

        } catch (ArrayIndexOutOfBoundsException ae) {
            // 使用 JOptionPane 显示错误信息
            JOptionPane.showMessageDialog(
                    null,                     // 父组件（null 表示默认窗口）
                    "发生错误: 最小矩形点集已超出原有图片尺寸范围！", // 错误信息
                    "错误",                   // 弹窗标题
                    JOptionPane.ERROR_MESSAGE // 消息类型（ERROR_MESSAGE 显示错误图标）
            );
            System.out.println("发生错误:" + ae.getMessage());
        }


        // 显示图像
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(contourImage)));
        frame.pack();
        frame.setLocationRelativeTo(null); // 窗口居中
        frame.setVisible(true);
    }

    // 显示轮廓
    public static void visualizePoints(List<Point2D> points, String title) {
        if (points == null || points.isEmpty()) {
            JOptionPane.showMessageDialog(null, "点集为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 计算点集的边界
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point2D point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }

        // 计算图像大小（比点集范围大100像素）
        int width = (int) (maxX - minX) + 100;
        int height = (int) (maxY - minY) + 100;

        // 创建图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景为白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 设置点颜色为红色
        g2d.setColor(Color.RED);

        // 计算偏移量，使点集居中
        int offsetX = (width - (int)(maxX - minX)) / 2 - (int)minX;
        int offsetY = (height - (int)(maxY - minY)) / 2 - (int)minY;

        // 绘制所有点
        try {
            for (Point2D point : points) {
                int x = (int) point.getX() + offsetX;
                int y = (int) point.getY() + offsetY;

                // 绘制一个1x1像素的方块代表点
                g2d.fillRect(x - 2, y - 2, 1, 1);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "绘制点集时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            System.out.println("发生错误:" + e.getMessage());
        } finally {
            g2d.dispose();
        }

        // 创建并显示窗口
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setLocationRelativeTo(null); // 窗口居中
        frame.setVisible(true);
    }

    //只显示轮廓
    public static void onlyContour(BufferedImage image, List<Point2D> contourPoints) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.RED);
        // 绘制轮廓点
        for (Point2D point : contourPoints) {
            bufferedImage.setRGB((int) point.getX(), (int) point.getY(), Color.RED.getRGB());
        }
        // 显示图像
        JFrame frame = new JFrame("轮廓可视化");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(bufferedImage)));
        frame.pack();
        frame.setVisible(true);
    }


    public static void visualizePoints(List<Point2D> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Point list cannot be null or empty");
        }

        // 创建主窗口
        JFrame frame = new JFrame("Point Set Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建绘图面板
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // 启用抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 获取面板尺寸
                int width = getWidth();
                int height = getHeight();

                // 计算边距
                int margin = 50;
                int plotWidth = width - 2 * margin;
                int plotHeight = height - 2 * margin;

                // 计算点的最小和最大坐标
                double minX = Double.MAX_VALUE;
                double maxX = -Double.MAX_VALUE;
                double minY = Double.MAX_VALUE;
                double maxY = -Double.MAX_VALUE;

                for (Point2D point : points) {
                    minX = Math.min(minX, point.getX());
                    maxX = Math.max(maxX, point.getX());
                    minY = Math.min(minY, point.getY());
                    maxY = Math.max(maxY, point.getY());
                }

                // 添加一些边界空间
                double xRange = maxX - minX;
                double yRange = maxY - minY;
                minX -= xRange * 0.1;
                maxX += xRange * 0.1;
                minY -= yRange * 0.1;
                maxY += yRange * 0.1;
                xRange = maxX - minX;
                yRange = maxY - minY;

                // 绘制坐标轴
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));

                // X轴
                g2d.drawLine(margin, height - margin, width - margin, height - margin);
                // Y轴
                g2d.drawLine(margin, height - margin, margin, margin);

                // 绘制刻度
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));

                // X轴刻度
                int xTicks = 10;
                for (int i = 0; i <= xTicks; i++) {
                    double xValue = minX + (i * xRange / xTicks);
                    int xPos = margin + (int)(i * plotWidth / xTicks);
                    g2d.drawLine(xPos, height - margin - 5, xPos, height - margin + 5);
                    g2d.drawString(String.format("%.2f", xValue), xPos - 15, height - margin + 20);
                }

                // Y轴刻度
                int yTicks = 10;
                for (int i = 0; i <= yTicks; i++) {
                    double yValue = minY + (i * yRange / yTicks);
                    int yPos = height - margin - (int)(i * plotHeight / yTicks);
                    g2d.drawLine(margin - 5, yPos, margin + 5, yPos);
                    g2d.drawString(String.format("%.2f", yValue), margin - 45, yPos + 5);
                }

                // 绘制点
                g2d.setColor(Color.RED);
                int pointSize = 6;

                for (Point2D point : points) {
                    // 将数据坐标转换为屏幕坐标
                    int x = margin + (int)((point.getX() - minX) / xRange * plotWidth);
                    int y = height - margin - (int)((point.getY() - minY) / yRange * plotHeight);

                    // 绘制点
                    g2d.fillOval(x - pointSize/2, y - pointSize/2, pointSize, pointSize);
                }

                // 绘制标题
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("Point Set Visualization", width/2 - 100, margin/2);
            }
        };

        // 设置初始大小并显示窗口
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 展示长、宽、面积数据的窗口
     * @param length 长度值
     * @param width 宽度值
     * @param area 面积值
     * @param title 窗口标题
     */
    public static void showDimensions(double length, double width, double area, String title) {
        // 创建数字格式化器，保留两位小数
        DecimalFormat df = new DecimalFormat("#0.00");

        // 创建主面板
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加标签和数据
        panel.add(new JLabel("长度:", SwingConstants.RIGHT));
        panel.add(new JLabel(df.format(length)));

        panel.add(new JLabel("宽度:", SwingConstants.RIGHT));
        panel.add(new JLabel(df.format(width)));

        panel.add(new JLabel("面积:", SwingConstants.RIGHT));
        panel.add(new JLabel(df.format(area)));

        // 创建并设置窗口
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // 窗口居中
        frame.setVisible(true);
    }

}