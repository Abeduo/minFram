package CORE;

import BASE.Graph;
import Utils.PolygonOverlap;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static CORE.ImprovedContourExtractor.*;

public class MultiGraphArrangement {
    static double width = 0;
    static double height = 0;
    static double area = Double.MAX_VALUE;

    //移动两个图形直至点重合,将图形的b点移动到与a点重合，返回移动后的图形点集
    public static List<Point2D> moveGraph(Point2D a, Point2D b, List<Point2D> graph){
        List<Point2D> result = new ArrayList<>();
        double gapeX, gabeY;
        gapeX = b.getX() - a.getX();
        gabeY = b.getY() - a.getY();
        for (Point2D point2D : graph) {
            result.add(new Point2D.Double(point2D.getX() - gapeX, point2D.getY() - gabeY));
        }
        return result;
    }

    //旋转图形直至边重合，a1a3向a1a2旋转，返回旋转后的坐标
    public static  List<Point2D> rotateGraph(Point2D a1, Point2D a2, Point2D a3, List<Point2D> graph){
        List<Point2D> result = new ArrayList<>();
        double angle;
        // θ=atan2((x2−x1)(y3−y1)−(x3−x1)(y2−y1),(x2−x1)(x3−x1)+(y2−y1)(y3−y1))
        angle = -Math.atan2((a2.getX() - a1.getX()) * (a3.getY() - a1.getY()) - (a3.getX() - a1.getX()) * (a2.getY() - a1.getY()), (a2.getX() - a1.getX()) * (a3.getX() - a1.getX()) + (a2.getY() - a1.getY()) * (a3.getY() - a1.getY()));
        System.out.println("angle:" + angle);
        for (Point2D p : graph){
            // +-问题
            result.add(new Point2D.Double((p.getX() - a1.getX()) * Math.cos(angle) - (p.getY() - a1.getY()) * Math.sin(angle) + a1.getX(), (p.getX() - a1.getX()) * Math.sin(angle) + (p.getY() - a1.getY()) * Math.cos(angle) + a1.getY()));
        }
        return result;
    }
/*    public static List<Point2D> rotateGraph(Point2D a1, Point2D a2, Point2D a3, List<Point2D> graph) {
        // 计算向量a1->a2和a1->a3
        double v1x = a2.getX() - a1.getX();
        double v1y = a2.getY() - a1.getY();
        double v2x = a3.getX() - a1.getX();
        double v2y = a3.getY() - a1.getY();

        // 计算两个向量的角度（弧度）
        double angle1 = Math.atan2(v1y, v1x);
        double angle2 = Math.atan2(v2y, v2x);

        // 计算需要旋转的角度（从a1a3旋转到a1a2）
        double rotationAngle = angle1 - angle2;

        // 创建结果列表
        List<Point2D> rotatedGraph = new ArrayList<>();

        // 对图形中的每个点应用旋转
        for (Point2D point : graph) {
            // 将点转换为相对于a1的坐标
            double x = point.getX() - a1.getX();
            double y = point.getY() - a1.getY();

            // 应用旋转
            double rotatedX = x * Math.cos(rotationAngle) - y * Math.sin(rotationAngle);
            double rotatedY = x * Math.sin(rotationAngle) + y * Math.cos(rotationAngle);

            // 转换回绝对坐标并添加到结果列表
            rotatedGraph.add(new Point2D.Double(rotatedX + a1.getX(), rotatedY + a1.getY()));
        }

        return rotatedGraph;
    }*/

    //判断图形是否重叠，g1代表是原图形，g2代表是需要检测的图形
    //原理是检测g2的点的x是否在相同y的另一图形轮廓的x之间
    public static boolean isOverlap(List<Point2D> g1, List<Point2D> g2){
        boolean res1 = false;
        boolean res2 = false;
        boolean res = false;
        List<Double> temp1 = new ArrayList<>();
        List<Double> temp2 = new ArrayList<>();
        double maxX = g1.get(0).getX();
        double minX = g1.get(0).getX();
        double maxY = g1.get(0).getY();
        double minY = g1.get(0).getY();
        for(Point2D p : g2) {
            for (Point2D d : g1) {
                if(d.getY() == p.getY()) {
                    temp1.add(d.getX());
                }
                if(d.getX() == p.getX()) {
                    temp2.add(d.getY());
                }
            }
            if(!temp1.isEmpty()){
                for (double d : temp1){
                    if(maxX < d){
                        maxX = d;
                    }
                    if(minX > d){
                        minX = d;
                    }
                }
                if(p.getX()>minX && p.getX() < maxX){
                    res1 = true;
                }
            }
            if(!temp2.isEmpty()){
                for (double d : temp2){
                    if(maxY < d){
                        maxY = d;
                    }
                    if(minY > d){
                        minY = d;
                    }
                }
                if(p.getY()>minY && p.getY() < maxY){
                    res2 = true;
                }
            }
        }
        if(res1 && res2){
            res = true;
        }

        return res;
    }

    public static boolean realIsOverLop(List<Point2D> g1, List<Point2D> g2){
        boolean res = false;
        if(isOverlap(g1, g2) || isOverlap(g2, g1)){
            res = true;
        }
        return res;
    }

    // 计算组合图形的面积
    public static double combineGraphArea(List<Point2D> a, List<Point2D> b) {
        List<Point2D> points;
        Point2D[] fourPoints;
        double area = 0;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        //多个拐点相加
        a.addAll(b);
        //求凸包
        points = MinimumBoundingRectangle.computeConvexHull(a);
        fourPoints = MinimumBoundingRectangle.findMinimumBoundingRectangle(points,1);
        if (fourPoints != null) {
            for(Point2D p : fourPoints){
                if(p.getX() < minX){
                    minX = p.getX();
                }
                if(p.getX() > maxX){
                    maxX = p.getX();
                }
                if(p.getY() < minY){
                    minY = p.getY();
                }
                if(p.getY() > maxY){
                    maxY = p.getY();
                }
            }
            area = (maxX - minX) * (maxY - minY) / 2;
        }
        return area;
    }

    public static void core(List<String> filePaths) throws IOException {
        List<Graph> gs = new ArrayList<>();
        for (String filePath : filePaths){
            Graph graph = new Graph();
            graph.setImagePath(filePath);
            BufferedImage image = ImageIO.read(new File(filePath));
            if(image == null) {
                System.out.println("图片加载失败，请检查路径是否正确。");
                break;
            }

            // 提取轮廓
            List<Point2D> contourPoints = extractContours(image);
            graph.setContourPoints(contourPoints);

            //计算拐点
            List<Point2D> gPoints = MinimumBoundingRectangle.getCornerPoints(contourPoints);
            graph.setCornerPoints(gPoints);
            //PointVisualizer.picAndContour(image, gPoints);

            //计算凸包
            List<Point2D> convexHull =  MinimumBoundingRectangle.computeConvexHull(gPoints);
            graph.setConvexHull(convexHull);


            graph.setTempConvexPoints(convexHull);

            gs.add(graph);
        }
        // System.out.println(isOverlap(gs.get(0).getContourPoints(), gs.get(1).getContourPoints()));
        List<Point2D> multiGraphMinPoints = multiGraph(gs, 0);
        PointVisualizer.visualizePoints(multiGraphMinPoints, "多图形最小矩形框2");
        PointVisualizer.showDimensions(height, width, area, "详细数据");
        /*BufferedImage bufferedImage = ImageIO.read(new File(filePaths.get(0)));
        multiGraphMinPoints = MinimumBoundingRectangle.filterOutPoints(multiGraphMinPoints, bufferedImage.getWidth(), bufferedImage.getHeight());
        PointVisualizer.picAndContour(bufferedImage, multiGraphMinPoints, "多图形最小矩形框");*/

    }

    //递归
    public static List<Point2D> multiGraph(List<Graph> gs, int count){
        double widthMin = 0, heightMin = 0;
        /*List<Graph> gs01 = new ArrayList<>();
        for (Graph g : gs) {
            gs01.add(new Graph(g));
        }*/
        List<Graph> gs02 = new ArrayList<>();
        for (Graph g : gs) {
            gs02.add(new Graph(g));
        }
        // 移动->旋转->检测重叠
        List<Point2D> ps = new ArrayList<>();
        //List<Point2D> tempPs1 = new ArrayList<>(gs.get(0).getConvexHull());
        List<Point2D> tempPs2 = new ArrayList<>(gs.get(0).getConvexHull());
        double minArea = Double.MAX_VALUE;
        boolean overLap = false;
        List<Point2D> g0 = new ArrayList<>();
        //第count个图形的拐点
        if(count == 0){
            g0 = gs.get(count).getConvexHull();
        } else {
            g0 = gs.get(count).getTempConvexPoints();
        }
        //List<Point2D> g0 = gs.get(count).getConvexHull();
        //第count+1个图形的拐点
        List<Point2D> g1 = gs.get(count+1).getConvexHull();
        //count从0开始递增，小于gs.size-1
        for (int i=0; i<gs.get(count).getTempConvexPoints().size()-1; i++){
            //第count个图形的第i个拐点
            Point2D g0_p0 = g0.get(i);
            //第count个图形的第i+1个拐点
            Point2D g0_p1 = gs.get(count).getTempConvexPoints().get(i+1);
            for (int j=0; j<gs.get(count+1).getTempConvexPoints().size()-1; j++){
                //第count+1个图形的第j个拐点
                Point2D g1_p0 = gs.get(count+1).getTempConvexPoints().get(j);
                //第count+1个图形的第j+1个拐点
                Point2D g1_p1 = gs.get(count+1).getTempConvexPoints().get(j+1);

                //1
                /*List<Point2D> afterMovePoints1 = moveGraph(g0_p0, g1_p1, g1);
                Point2D after_g1_p1_1 = afterMovePoints1.get(j);
                List<Point2D> afterRotate1 = rotateGraph(g0_p0, g0_p1, after_g1_p1_1, afterMovePoints1);
                overLap = realIsOverLop(g0, afterRotate1);
                System.out.println(overLap);
                int tempPs1RemoveSize = tempPs1.size();
                if(!overLap){
                    count++;
                    if(count < gs.size()-1){
                        tempPs1.addAll(afterRotate1);
                        gs01.get(count+1).setConvexHull(afterRotate1);
                        multiGraph(gs01, count);
                    } else if(count == gs.size()-1){ //到最后一个图形
                        //计算面积
                        tempPs1.addAll(afterRotate1);
                        List<Point2D> tempPs1_1 = new ArrayList<>(tempPs1);
                        Point2D[] p2DS = MinimumBoundingRectangle.findMinimumBoundingRectangle(MinimumBoundingRectangle.computeConvexHull(tempPs1_1));
                        if (p2DS != null) {
                            double width = Math.sqrt(Math.pow(p2DS[1].getX() - p2DS[0].getX(), 2) + Math.pow((p2DS[1].getY() - p2DS[0].getY()), 2));
                            double height = Math.sqrt(Math.pow(p2DS[1].getX() - p2DS[2].getX(), 2) + Math.pow((p2DS[1].getY() - p2DS[2].getY()), 2));
                            double area = width * height;
                            if(area < minArea){
                                ps.clear();
                                minArea = area;
                                ps.addAll(Arrays.asList(p2DS));
                                widthMin = width;
                                heightMin = height;
                                gs.get(count).setMovePoint1(g0_p0);
                                gs.get(count).setMovePoint2(g1_p1);
                                gs.get(count).setRotatePoint1(g0_p0);
                                gs.get(count).setRotatePoint2(g0_p1);
                                gs.get(count).setRotatePoint3(after_g1_p1_1);
                            }
                        }
                        if (tempPs1RemoveSize + afterRotate1.size() > tempPs1RemoveSize) {
                            tempPs2.subList(tempPs1RemoveSize, tempPs1RemoveSize + afterRotate1.size()).clear();
                        }
                        count--;
                    } else {
                        break;
                    }
                }*/

                //2
                List<Point2D> afterMovePoints2 = moveGraph(g0_p0, g1_p1, g1);
                Point2D after_g1_p1_2 = afterMovePoints2.get(j);
                List<Point2D> afterRotate2 = rotateGraph(g0_p0, g0_p1, after_g1_p1_2, afterMovePoints2);
                overLap = realIsOverLop(g0, afterRotate2);
                //overLap = PolygonOverlap.isOverlap(g0, afterRotate2);
                System.out.println(overLap);
                int tempPs2RemoveSize = tempPs2.size();
                if(!overLap){
                    count++;
                    if(count < gs.size()-1){
                        tempPs2.addAll(afterRotate2);
                        gs02.get(count).setTempConvexPoints(MinimumBoundingRectangle.computeConvexHull(tempPs2));
                        gs02.get(count).setConvexHull(afterRotate2);
                        gs.get(count).setMovePoint1(g0_p0);
                        gs.get(count).setMovePoint2(g1_p1);
                        gs.get(count).setRotatePoint1(g0_p0);
                        gs.get(count).setRotatePoint2(g0_p1);
                        gs.get(count).setRotatePoint3(after_g1_p1_2);
                        gs02.get(count).setMovePoint1(g0_p0);
                        gs02.get(count).setMovePoint2(g1_p1);
                        gs02.get(count).setRotatePoint1(g0_p0);
                        gs02.get(count).setRotatePoint2(g0_p1);
                        gs02.get(count).setRotatePoint3(after_g1_p1_2);
                        multiGraph(gs02, count);
                        break;
                    } else if(count == gs.size()-1){ //到最后一个图形
                        //计算面积
                        tempPs2.addAll(afterRotate2);
                        List<Point2D> tempPs2_1 = new ArrayList<>(tempPs2);
                        Point2D[] p2DS = MinimumBoundingRectangle.findMinimumBoundingRectangle(MinimumBoundingRectangle.computeConvexHull(tempPs2_1),1);
                        if (p2DS != null) {
                            double width = Math.sqrt(Math.pow(p2DS[1].getX() - p2DS[0].getX(), 2) + Math.pow((p2DS[1].getY() - p2DS[0].getY()), 2));
                            double height = Math.sqrt(Math.pow(p2DS[1].getX() - p2DS[2].getX(), 2) + Math.pow((p2DS[1].getY() - p2DS[2].getY()), 2));
                            double area = width * height;
                            if(area < minArea){
                                ps.clear();
                                minArea = area;
                                ps.addAll(Arrays.asList(p2DS));
                                widthMin = width;
                                heightMin = height;
                                gs.get(count).setMovePoint1(g0_p0);
                                gs.get(count).setMovePoint2(g1_p1);
                                gs.get(count).setRotatePoint1(g0_p0);
                                gs.get(count).setRotatePoint2(g0_p1);
                                gs.get(count).setRotatePoint3(after_g1_p1_2);
                            }
                        }
                        if (tempPs2RemoveSize + afterRotate2.size() > tempPs2RemoveSize) {
                            tempPs2.subList(tempPs2RemoveSize, tempPs2RemoveSize + afterRotate2.size()).clear();
                        }
                        count--;
                    } else {
                        break;
                    }
                }
            }
        }
        List<Point2D> res = new ArrayList<>(MinimumBoundingRectangle.computeRec(ps));
        res.addAll(gs.get(0).getContourPoints());
        for (int k=1;k< gs.size();k++){
            List<Point2D> l = moveGraph(gs.get(k).getMovePoint1(), gs.get(k).getMovePoint2(), gs.get(k).getContourPoints());
            res.addAll(rotateGraph(gs.get(k).getRotatePoint1(), gs.get(k).getRotatePoint2(), gs.get(k).getRotatePoint3(), l));
        }

        System.out.println("长：" + widthMin);
        System.out.println("宽：" + heightMin);
        System.out.println("多图形的最小面积为："+minArea);
        if(minArea < area){
            area = minArea;
            width = widthMin;
            height = heightMin;
        }
        return res;
    }
}