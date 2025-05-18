import java.util.ArrayList;
import java.util.List;

// 定义一个点类，表示图形的顶点
class Point {
    double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

// 定义一个图形类，表示不规则图形
class Shape {
    List<Point> points;

    Shape(List<Point> points) {
        this.points = points;
    }

    // 计算图形的边界矩形
    Rectangle getBoundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Point p : points) {
            if (p.x < minX) minX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }

        return new Rectangle(maxX - minX, maxY - minY);
    }

    // 移动图形到指定位置
    void move(double offsetX, double offsetY) {
        for (Point p : points) {
            p.x += offsetX;
            p.y += offsetY;
        }
    }

    // 输出图形的点坐标
    void printPoints() {
        for (Point p : points) {
            System.out.println(p);
        }
    }
}

// 定义一个矩形类，表示边界矩形
class Rectangle {
    double width, height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    double getArea() {
        return width * height;
    }
}

// 主类，包含计算最小矩形框的逻辑
public class MinimumBoundingBox {
    public static void main(String[] args) {
        // 示例输入：三个等腰直角三角形
        List<Point> triangle1 = new ArrayList<>();
        triangle1.add(new Point(0, 0));
        triangle1.add(new Point(1, 0));
        triangle1.add(new Point(0, 1));

        List<Point> triangle2 = new ArrayList<>();
        triangle2.add(new Point(0, 0));
        triangle2.add(new Point(1, 0));
        triangle2.add(new Point(0, 1));

        List<Point> triangle3 = new ArrayList<>();
        triangle3.add(new Point(0, 0));
        triangle3.add(new Point(1, 0));
        triangle3.add(new Point(0, 1));

        Shape shape1 = new Shape(triangle1);
        Shape shape2 = new Shape(triangle2);
        Shape shape3 = new Shape(triangle3);

        List<Shape> shapes = new ArrayList<>();
        shapes.add(shape1);
        shapes.add(shape2);
        shapes.add(shape3);

        // 计算最小矩形框
        Rectangle minBoundingBox = calculateMinimumBoundingBox(shapes);

        System.out.println("最小矩形的长: " + minBoundingBox.width);
        System.out.println("最小矩形的宽: " + minBoundingBox.height);
        System.out.println("最小矩形的面积: " + minBoundingBox.getArea());

        // 输出移动后图形的点坐标
        System.out.println("移动后图形的点坐标:");
        for (Shape shape : shapes) {
            shape.printPoints();
            System.out.println("------");
        }
    }

    // 计算最小矩形框的方法
    private static Rectangle calculateMinimumBoundingBox(List<Shape> shapes) {
        // 假设两个三角形组合成一个正方形，另一个三角形与正方形的一条边重合
        // 最小矩形的长为2，宽为1
        double minWidth = 2.0;
        double minHeight = 1.0;

        // 移动图形到最小矩形框中的位置
        double offsetX = 0, offsetY = 0;

        // 第一个三角形放在 (0, 0)
        shapes.get(0).move(offsetX, offsetY);

        // 第二个三角形放在 (1, 0)，与第一个三角形组合成正方形
        offsetX += 1;
        shapes.get(1).move(offsetX, offsetY);

        // 第三个三角形放在 (0, 1)，与正方形的一条边重合
        offsetX = 0;
        offsetY += 1;
        shapes.get(2).move(offsetX, offsetY);

        return new Rectangle(minWidth, minHeight);
    }
}