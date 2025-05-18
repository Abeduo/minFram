package GUI;

import CORE.ImprovedContourExtractor;
import CORE.MultiGraphArrangement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUploaderGUI extends JFrame {

    private boolean isFirstClick = true;

    public ImageUploaderGUI() {
        // 设置窗口标题
        setTitle("不规则图形的最小矩形框定");

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.5);
        int height = (int) (screenSize.getHeight() * 0.5);

        // 设置窗口大小
        setSize(width, height);

        // 居中窗口
        setLocationRelativeTo(null);

        // 设置布局为BorderLayout
        setLayout(new BorderLayout());

        // 创建标题标签
        JLabel titleLabel = new JLabel("不规则图形的最小矩形框定", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        // 创建上传图片按钮
        JButton uploadButton = new JButton("上传图片");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFirstClick) {
                    JOptionPane.showMessageDialog(ImageUploaderGUI.this,
                            "注意：上传的图片只能有一个图形且无背景或水印！");
                    isFirstClick = false;
                }

                // 创建文件选择器
                JFileChooser fileChooser = new JFileChooser("D://杂物//测试数据//复杂图形");

                // 设置文件过滤器，仅允许图片文件
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        // 允许目录和图片文件
                        if (f.isDirectory()) {
                            return true;
                        }
                        String fileName = f.getName().toLowerCase();
                        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                                fileName.endsWith(".bmp");
                    }

                    @Override
                    public String getDescription() {
                        return "图片文件 (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
                    }
                });

                int result = fileChooser.showOpenDialog(ImageUploaderGUI.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String imagePath = selectedFile.getAbsolutePath();
                    ImprovedContourExtractor.ice(imagePath);
                    System.out.println("图片文件路径: " + imagePath);
                }
            }
        });

        // 创建绘制图形按钮
        JButton drawButton = new JButton("绘制图形");
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 绘制图形的功能暂时不写
                // TODO: 实现绘制图形的功能
                System.out.println("绘制图形的功能尚未实现。");
            }
        });

        // 创建多图片上传按钮
        JButton manyButton = new JButton("多个图形");
        manyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 创建文件选择器
                JFileChooser fileChooser = new JFileChooser("D://杂物//测试数据//复杂图形");
                fileChooser.setMultiSelectionEnabled(true); // 允许多选
                fileChooser.setDialogTitle("选择多个图片文件");

                // 设置文件过滤器（仅显示图片文件）
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String name = file.getName().toLowerCase();
                        return file.isDirectory() ||
                                name.endsWith(".jpg") ||
                                name.endsWith(".jpeg") ||
                                name.endsWith(".png") ||
                                name.endsWith(".gif");
                    }

                    @Override
                    public String getDescription() {
                        return "图片文件 (*.jpg, *.jpeg, *.png, *.gif)";
                    }
                });

                // 显示文件选择对话框
                int result = fileChooser.showOpenDialog(null);
                List<String> manyFilePath = new ArrayList<>();

                // 如果用户点击了"确定"
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = fileChooser.getSelectedFiles(); // 获取所有选中的文件
                    if (selectedFiles.length > 0) {
                        StringBuilder message = new StringBuilder("已选择以下文件：\n");
                        for (File file : selectedFiles) {
                            message.append(file.getName()).append("\n");
                            manyFilePath.add(file.getPath());
                        }
                        JOptionPane.showMessageDialog(null, message.toString(), "文件选择结果", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "未选择任何文件！", "警告", JOptionPane.WARNING_MESSAGE);
                    }
                }
                try {
                    //MultiGraphArrangement.core(manyFilePath);
                    if(!manyFilePath.isEmpty()){
                        if(manyFilePath.size() == 1){
                            ImprovedContourExtractor.ice(manyFilePath.get(0));
                        }else {
                            MultiGraphArrangement.core(manyFilePath);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (String s : manyFilePath){
                    System.out.println(s);
                }
            }
        });

        // 添加按钮到面板
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // 设置按钮之间的间距
        buttonPanel.add(uploadButton, gbc);

        //gbc.gridy = 1;
        //buttonPanel.add(drawButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(manyButton, gbc);

        // 添加按钮面板到窗口
        add(buttonPanel, BorderLayout.CENTER);

        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 显示窗口
        setVisible(true);
    }

    public static void main(String[] args) {
        // 使用事件调度线程创建并显示GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ImageUploaderGUI();
            }
        });
    }
}