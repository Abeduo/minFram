import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageChooserAndDisplayExample {
    public static void main(String[] args) {
        // 创建一个JFrame窗口
        JFrame frame = new JFrame("图片选择与显示示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // 创建一个按钮，点击后打开文件选择器
        JButton button = new JButton("选择图片");
        frame.add(button, BorderLayout.NORTH);

        // 创建一个JLabel用于显示图片
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER); // 设置图片居中显示
        frame.add(imageLabel, BorderLayout.CENTER);

        // 添加按钮的点击事件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建一个文件选择器
                JFileChooser fileChooser = new JFileChooser();
                // 设置文件过滤器，只显示图片文件
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".png") ||
                                f.getName().toLowerCase().endsWith(".jpg") ||
                                f.getName().toLowerCase().endsWith(".jpeg") ||
                                f.getName().toLowerCase().endsWith(".gif");
                    }

                    @Override
                    public String getDescription() {
                        return "图片文件 (*.png, *.jpg, *.jpeg, *.gif)";
                    }
                });

                // 打开文件选择器
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // 获取选中的文件
                    File selectedFile = fileChooser.getSelectedFile();
                    // 加载图片并显示在JLabel中
                    ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                    // 调整图片大小以适应标签
                    Image scaledImage = imageIcon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    imageLabel.setIcon(scaledIcon);
                }
            }
        });

        // 显示窗口
        frame.setVisible(true);
    }
}