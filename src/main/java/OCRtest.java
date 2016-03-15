import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.scanner.DocumentScanner;
import net.sourceforge.javaocr.scanner.DocumentScannerListener;
import net.sourceforge.javaocr.scanner.DocumentScannerListenerAdaptor;
import net.sourceforge.javaocr.scanner.PixelImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryan on 3/3/16.
 */
public class OCRtest {

    public static void main(String[] args) throws IOException {

        Image image = ImageIO.read(new File("/home/ryan/Pictures/sudoku.png"));

        PixelImage pixelImage = new PixelImage(image);

//        DocumentScanner ds = new DocumentScanner();
//
//        DocumentScannerListenerAdaptor a = new DocumentScannerListenerAdaptor();
//
//        ds.scan(pixelImage, a, 0, 0, pixelImage.height, pixelImage.width);
//

        OCRScanner scanner = new OCRScanner();

        CharacterRange cr = new CharacterRange(0,255);
        CharacterRange[] crs = new CharacterRange[]{cr};


        HashMap<Character, ArrayList<TrainingImage>> trainingImages = buildTrainingImages();
        scanner.addTrainingImages(trainingImages);

        String text = scanner.scan(image, 50, 50, 195,195, null);

        System.out.println(text);
    }

    public static HashMap<Character, ArrayList<TrainingImage>> buildTrainingImages() throws IOException {

        File f = new File("/home/ryan/Pictures/5.png");
        Image image = ImageIO.read(f);
        PixelImage pixelImage = new PixelImage(image);

        TrainingImage trainingImage = new TrainingImage(pixelImage.pixels,16,16, 3, 3);

        HashMap<Character, ArrayList<TrainingImage>> result = new HashMap<>();
        ArrayList<TrainingImage> images = new ArrayList<>();
        images.add(trainingImage);
        result.put('5', images);

        return result;
    }


}
