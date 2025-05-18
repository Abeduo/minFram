import CORE.MultiGraphArrangement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class biao53 {
    public static void main(String[] args) throws IOException {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("src/main/resources/pic/测试相交1.png");
        filePaths.add("src/main/resources/pic/测试相交1.png");
        MultiGraphArrangement.core(filePaths);
    }
}
