import javax.swing.*;
import java.awt.*;
import java.awt.Shape;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

// 自定义绘图面板
class DrawingPanel extends JPanel {
    // 存储图形对象的列表
    private List<Shape> shapes = new ArrayList<>();
    private String currentShape = "Line"; // 默认绘制线条

    // 设置当前图形类型
    public void setCurrentShape(String shape) {
        this.currentShape = shape;
    }

    // 清空所有图形
    public void clear() {
        shapes.clear();
        repaint();
    }

    // 重写paintComponent方法进行绘制
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制所有保存的图形
        for (Shape shape : shapes) {
            g2d.draw(shape);
        }
    }

    // 添加图形到列表
    public void addShape(Shape shape) {
        shapes.add(shape);
        repaint();
    }

    // 获取当前图形类型
    public String getCurrentShape() {
        return currentShape;
    }
}

// 主窗口
public class DrawingApp extends JFrame {
    private DrawingPanel drawingPanel;
    private JButton lineButton, rectButton, circleButton, clearButton;

    public DrawingApp() {
        // 设置窗口
        setTitle("Java Drawing Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建绘图面板
        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        add(drawingPanel, BorderLayout.CENTER);

        // 创建工具栏
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        lineButton = new JButton("Line");
        rectButton = new JButton("Rectangle");
        circleButton = new JButton("Circle");
        clearButton = new JButton("Clear");

        toolbar.add(lineButton);
        toolbar.add(rectButton);
        toolbar.add(circleButton);
        toolbar.add(clearButton);

        add(toolbar, BorderLayout.NORTH);

        // 按钮事件监听
        lineButton.addActionListener(e -> drawingPanel.setCurrentShape("Line"));
        rectButton.addActionListener(e -> drawingPanel.setCurrentShape("Rectangle"));
        circleButton.addActionListener(e -> drawingPanel.setCurrentShape("Circle"));
        clearButton.addActionListener(e -> drawingPanel.clear());

        // 鼠标事件监听
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 获取当前图形类型
                String shapeType = drawingPanel.getCurrentShape();

                int x = e.getX();
                int y = e.getY();

                // 根据当前选择的图形类型创建图形
                Shape shape = null;
                switch (shapeType) {
                    case "Line":
                        shape = new Line2D.Double(x, y, x + 50, y + 50);
                        break;
                    case "Rectangle":
                        shape = new Rectangle2D.Double(x, y, 60, 40);
                        break;
                    case "Circle":
                        shape = new Ellipse2D.Double(x, y, 50, 50);
                        break;
                }

                if (shape != null) {
                    drawingPanel.addShape(shape);
                }
            }
        });

        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 实时绘制图形（例如绘制线条）
                int x = e.getX();
                int y = e.getY();
                String shapeType = drawingPanel.getCurrentShape();

                if (shapeType.equals("Line")) {
                    Shape line = new Line2D.Double(100, 100, x, y);  // 画一条从(100, 100)到拖动点的线
                    drawingPanel.addShape(line);
                }
            }
        });
    }

    // 启动程序
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawingApp app = new DrawingApp();
            app.setVisible(true);
        });
    }
}