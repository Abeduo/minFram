import CORE.MinimumBoundingRectangle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class biao52 {

    public static List<Point2D> test(List<Point2D> list){
        List<Point2D> res = new ArrayList<>();
        Point2D[] minimumBoundingRectangle = MinimumBoundingRectangle.findMinimumBoundingRectangle(list, 0);
        if (minimumBoundingRectangle != null) {
            res.addAll(Arrays.asList(minimumBoundingRectangle));
        }
        return res;
    }

    public static void main(String[] args) {
        List<Point2D> test1 = new ArrayList<>();
        test1.add(new Point2D.Double(0,0));
        test1.add(new Point2D.Double(0,4));
        test1.add(new Point2D.Double(4,0));
        test1.add(new Point2D.Double(4,4));
        List<Point2D> test1Res = test(test1);


        List<Point2D> test2 = new ArrayList<>();
        List<Point2D> test2Res = test(test2);


        List<Point2D> test3 = new ArrayList<>();
        test3.add(new Point2D.Double(0,0));
        test3.add(new Point2D.Double(0,4));
        test3.add(new Point2D.Double(4,0));
        test3.add(new Point2D.Double(4,4));
        test3.add(new Point2D.Double(2,2));
        List<Point2D> test3Res = test(test3);


        System.out.println(test1Res.size());
        System.out.println(test2Res.size());
        System.out.println(test3Res.size());
    }
}
