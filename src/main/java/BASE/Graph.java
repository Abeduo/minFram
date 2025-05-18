package BASE;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Graph {
    /**
     * 图形文件路径
     */
    String imagePath;
    /**
     * 创建时间
     */
    Date createTime;
    /**
     *图形轮廓点集
     */
    List<Point2D> contourPoints;
    /**
     *拐点（角点）点集
     */
    List<Point2D> cornerPoints;
    /**
     *凸包
     */
    List<Point2D> convexHull;
    /**
     *最小矩形的四个顶点
     */
    List<Point2D> minimumRectangleVertex;
    /**
     *最小矩形的轮廓
     */
    List<Point2D> minimumRectangleContour;
    /**
     *未超出图片边界的最小矩形轮廓点集
     */
    List<Point2D> filterMinimumRectangleContour;

    List<Point2D> tempConvexPoints;

    /**
     * 记录后续移动的基准点
     */
    Point2D movePoint1;
    Point2D movePoint2;

    /**
     * 记录旋转的基准点
     */
    Point2D rotatePoint1;
    Point2D rotatePoint2;
    Point2D rotatePoint3;

    public Graph() {
    }

    public Graph(Graph g){
        List<Point2D> contourPoints = new ArrayList<>(g.contourPoints);
        List<Point2D> cornerPoints = new ArrayList<>(g.cornerPoints);
        List<Point2D> convexHull = new ArrayList<>(g.convexHull);
        List<Point2D> tempConvexHull = new ArrayList<>(g.tempConvexPoints);
        this.imagePath = g.imagePath;
        this.createTime = g.createTime;
        this.contourPoints = contourPoints;
        this.cornerPoints = cornerPoints;
        this.convexHull = convexHull;
        this.tempConvexPoints = tempConvexHull;
        this.minimumRectangleVertex = g.minimumRectangleVertex;
        this.minimumRectangleContour = g.minimumRectangleContour;
        this.filterMinimumRectangleContour = g.filterMinimumRectangleContour;
        this.movePoint1 = g.movePoint1;
        this.movePoint2 = g.movePoint2;
        this.rotatePoint1 = g.rotatePoint1;
        this.rotatePoint2 = g.rotatePoint2;
        this.rotatePoint3 = g.rotatePoint3;
    }

    public Graph(String imagePath, Date createTime, List<Point2D> contourPoints, List<Point2D> cornerPoints, List<Point2D> convexHull, List<Point2D> minimumRectangleVertex, List<Point2D> minimumRectangleContour, List<Point2D> filterMinimumRectangleContour) {
        this.imagePath = imagePath;
        this.createTime = createTime;
        this.contourPoints = contourPoints;
        this.cornerPoints = cornerPoints;
        this.convexHull = convexHull;
        this.minimumRectangleVertex = minimumRectangleVertex;
        this.minimumRectangleContour = minimumRectangleContour;
        this.filterMinimumRectangleContour = filterMinimumRectangleContour;
    }

    public Graph(String imagePath, Date createTime, List<Point2D> contourPoints, List<Point2D> cornerPoints, List<Point2D> convexHull, List<Point2D> minimumRectangleVertex, List<Point2D> minimumRectangleContour, List<Point2D> filterMinimumRectangleContour, Point2D movePoint1, Point2D movePoint2, Point2D rotatePoint1, Point2D rotatePoint2, Point2D rotatePoint3) {
        this.imagePath = imagePath;
        this.createTime = createTime;
        this.contourPoints = contourPoints;
        this.cornerPoints = cornerPoints;
        this.convexHull = convexHull;
        this.minimumRectangleVertex = minimumRectangleVertex;
        this.minimumRectangleContour = minimumRectangleContour;
        this.filterMinimumRectangleContour = filterMinimumRectangleContour;
        this.movePoint1 = movePoint1;
        this.movePoint2 = movePoint2;
        this.rotatePoint1 = rotatePoint1;
        this.rotatePoint2 = rotatePoint2;
        this.rotatePoint3 = rotatePoint3;
    }

    /**
     * 获取图片路径
     * @return imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * 设置图片路径
     * @param imagePath
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * 获取创建时间
     * @return createTime
     */
    public String getCreateTime() {
        return String.format("%tF%n", createTime) + String.format("%tT%n", createTime);
    }

    /**
     * 设置创建时间
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取图形轮廓点集
     * @return contourPoints
     */
    public List<Point2D> getContourPoints() {
        return contourPoints;
    }

    /**
     * 设置图形轮廓点集
     * @param contourPoints
     */
    public void setContourPoints(List<Point2D> contourPoints) {
        this.contourPoints = contourPoints;
    }

    /**
     * 获取拐点（角点）
     * @return cornerPoints
     */
    public List<Point2D> getCornerPoints() {
        return cornerPoints;
    }

    /**
     * 设置拐点（角点）
     * @param cornerPoints
     */
    public void setCornerPoints(List<Point2D> cornerPoints) {
        this.cornerPoints = cornerPoints;
    }

    /**
     * 获取凸包
     * @return convexHull
     */
    public List<Point2D> getConvexHull() {
        return convexHull;
    }

    /**
     * 设置凸包
     * @param convexHull
     */
    public void setConvexHull(List<Point2D> convexHull) {
        this.convexHull = convexHull;
    }

    /**
     * 获取前几个图形组合的凸包
     */
    public List<Point2D> getTempConvexPoints() {
        return tempConvexPoints;
    }

    /**
     * 设置前几个图形组合的凸包
     */
    public void setTempConvexPoints(List<Point2D> tempConvexPoints){
        this.tempConvexPoints = tempConvexPoints;
    }

    /**
     * 获取最小矩形的四个顶点
     * @return minimumRectangleVertex
     */
    public List<Point2D> getMinimumRectangleVertex() {
        return minimumRectangleVertex;
    }

    /**
     * 设置最小矩形的四个顶点
     * @param minimumRectangleVertex
     */
    public void setMinimumRectangleVertex(List<Point2D> minimumRectangleVertex) {
        this.minimumRectangleVertex = minimumRectangleVertex;
    }

    /**
     * 获取最小矩形轮廓
     * @return minimumRectangleContour
     */
    public List<Point2D> getMinimumRectangleContour() {
        return minimumRectangleContour;
    }

    /**
     * 设置最小矩形轮廓
     * @param minimumRectangleContour
     */
    public void setMinimumRectangleContour(List<Point2D> minimumRectangleContour) {
        this.minimumRectangleContour = minimumRectangleContour;
    }

    /**
     * 获取未超出图片边界的最小矩形轮廓
     * @return filterMinimumRectangleContour
     */
    public List<Point2D> getFilterMinimumRectangleContour() {
        return filterMinimumRectangleContour;
    }

    /**
     * 设置未超出图片边界的最小矩形轮廓
     * @param filterMinimumRectangleContour
     */
    public void setFilterMinimumRectangleContour(List<Point2D> filterMinimumRectangleContour) {
        this.filterMinimumRectangleContour = filterMinimumRectangleContour;
    }

    public String toString() {
        return "graph{imagePath = " + imagePath + ", createTime = " + createTime + ", contourPoints = " + contourPoints + ", cornerPoints = " + cornerPoints + ", convexHull = " + convexHull + ", minimumRectangleVertex = " + minimumRectangleVertex + ", minimumRectangleContour = " + minimumRectangleContour + ", filterMinimumRectangleContour = " + filterMinimumRectangleContour + "}";
    }

    /**
     * 获取
     * @return movePoint1
     */
    public Point2D getMovePoint1() {
        return movePoint1;
    }

    /**
     * 设置
     * @param movePoint1
     */
    public void setMovePoint1(Point2D movePoint1) {
        this.movePoint1 = movePoint1;
    }

    /**
     * 获取
     * @return movePoint2
     */
    public Point2D getMovePoint2() {
        return movePoint2;
    }

    /**
     * 设置
     * @param movePoint2
     */
    public void setMovePoint2(Point2D movePoint2) {
        this.movePoint2 = movePoint2;
    }

    /**
     * 获取
     * @return rotatePoint1
     */
    public Point2D getRotatePoint1() {
        return rotatePoint1;
    }

    /**
     * 设置
     * @param rotatePoint1
     */
    public void setRotatePoint1(Point2D rotatePoint1) {
        this.rotatePoint1 = rotatePoint1;
    }

    /**
     * 获取
     * @return rotatePoint2
     */
    public Point2D getRotatePoint2() {
        return rotatePoint2;
    }

    /**
     * 设置
     * @param rotatePoint2
     */
    public void setRotatePoint2(Point2D rotatePoint2) {
        this.rotatePoint2 = rotatePoint2;
    }

    /**
     * 获取
     * @return rotatePoint3
     */
    public Point2D getRotatePoint3() {
        return rotatePoint3;
    }

    /**
     * 设置
     * @param rotatePoint3
     */
    public void setRotatePoint3(Point2D rotatePoint3) {
        this.rotatePoint3 = rotatePoint3;
    }
}
