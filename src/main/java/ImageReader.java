import Jama.Matrix;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by ryan on 3/3/16.
 */
public class ImageReader {

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.loadLibrary("opencv-310");

//
//        File input = new File("/home/ryan/proj/sudoku_app/src/main/resources/sudoku.png");
//        BufferedImage image = ImageIO.read(input);
//
//        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
//        mat.put(0, 0, data);

        Mat mat1 = Imgcodecs.imread("sudoku.png", 0);

//        findSquares(mat1);

//        Mat five = Imgcodecs.imread("pics/id0.png");

//        System.out.println(isAnyEdgeBlack(five));

        ocr();

    }



    public static void findSquares(Mat image) {

        //    Mat pyr, timg, gray0(image.size(), CV_8U), gray; TODO gray0? is this a grayscaling op?


        Mat pyr = new Mat();
        Mat timg = new Mat();
        timg = image;

//        Imgproc.pyrDown(image, pyr, new Size(image.width()/2, image.height()/2));
//        Imgproc.pyrUp(pyr, timg, image.size());

//        List<List<Point>> contours;
        List<MatOfPoint> contours = new ArrayList<>();

        Mat heirarchy = new Mat();

        Mat edges = new Mat(timg.size(), 0);

        Imgproc.Canny(image, edges, 1, 1,3, true);
//        Imgproc.dilate(edges, edges, new Mat(), new Point(-1, -1), 2);
        Imgcodecs.imwrite("pics/edges.png", edges);

        List<Mat> squares = new ArrayList<>(81);
        int width = edges.cols() / 9;
        for (int row=0; row<9; row++) {
            for (int col=0; col<9; col++) {
                Rect rect = new Rect(col*width, row*width, width, width);
                Mat square = new Mat(timg, rect);
                Mat smaller = new Mat();
                Imgproc.pyrDown(square, smaller, new Size(square.width()/2, square.height()/2));
                squares.add(smaller);
                Imgcodecs.imwrite("pics/square" + row + "-" + col + ".png", square);
            }
        }


        squares = trimSquares(squares);

        saveSquaresToFiles(squares);


    }

    public static void saveSquaresToFiles(List<Mat> squares) {

        int id = 0;
        for (Mat mat : squares){
            System.out.println("writing square" + id +" size: " + mat.rows() + "X"+ mat.cols());
            Imgcodecs.imwrite("pics/id" + (id++) + ".png", mat);
        }
    }

    public static List<Mat> trimSquares(Collection<Mat> mats) {

        List<Mat> result = new ArrayList<>();

        int id = 0;
        int largestAdjustment = 0;
        for (Mat square : mats) {
//            System.out.println("square: " + id + " original size: " + square.rows() + "X"+ square.cols());
            Mat copy = new Mat();
            square.copyTo(copy);
            id++;
            int adjustment = 0;
            while (isAnyEdgeBlack(copy)) {
                copy = copy.adjustROI(-1, -1, -1, -1);
                adjustment++;
            }
            System.out.println(adjustment);
            if (adjustment > largestAdjustment)
                largestAdjustment = adjustment;

//            System.out.println("square: " + id++ + " final size: " + square.rows() + "X" + square.cols());

        }
        for (Mat square : mats) {
            square = square.adjustROI(-largestAdjustment,-largestAdjustment,-largestAdjustment,-largestAdjustment);
            result.add(square);
        }

        List<Mat> result2 = new ArrayList<>();

        for (Mat square : result) {

            while (allEdgesSolid(square) && square.width() > 20){
                square = square.adjustROI(-1,-1,-1,-1);
                System.out.println("adjusting for solid edge");
            }
            Imgproc.resize(square, square, new Size(20,20));
            result2.add(square);
        }

        return result2;
    }

    public static boolean isAnyEdgeBlack(Mat mat) {

        int width = mat.width();
        int height = mat.height();
        boolean result = false;
        if (isEdgeBlack(mat.row(0)) || isEdgeBlack(mat.col(0)) || isEdgeBlack(mat.row(height-1)) || isEdgeBlack(mat.col(width-1)))
            result = true;

        Imgcodecs.imwrite("test.png", mat);

        return result;
    }

    public static boolean isEdgeBlack(Mat mat) {

//        int random = (int)(Math.random()*1000);
//        Imgcodecs.imwrite("edge"+random+".png", mat);

        int avg;
        if (mat.rows() == 1) {

            int total = 0;
            int cols = mat.cols();
            for (int col=0; col<cols; col++) {
                double value = mat.get(0, col)[0];
                total += value;
            }
            avg = total/cols;
//            System.out.println(avg);
        }
        else {//if (mat.cols() == 1) {
            int total = 0;
            int rows = mat.rows();
            for (int row=0; row<rows; row++) {
                double value = mat.get(row, 0)[0];
                total += value;
            }
            avg = total/rows;
        }

//        System.out.println("avg color of edge "+ random +": " + avg);


        return avg < 200;
    }

    public static boolean allEdgesSolid(Mat mat) {

        int width = mat.width();
        int height = mat.height();
        boolean result = false;
        if (isEdgeSolid(mat.row(0)) && isEdgeSolid(mat.col(0)) && isEdgeSolid(mat.row(height-1)) && isEdgeSolid(mat.col(width-1)))
            result = true;

        return result;
    }

    public static boolean isEdgeSolid(Mat mat) {

        if (mat.rows() == 1) {

            double color = mat.get(0, 0)[0];
            int cols = mat.cols();
            for (int col=0; col<cols; col++) {
                double value = mat.get(0, col)[0];
                if (color != value)
                    return false;
            }
        }
        else {

            double color = mat.get(0, 0)[0];
            int rows = mat.rows();
            for (int row=0; row<rows; row++) {
                double value = mat.get(row, 0)[0];
                if (color != value)
                    return false;
            }
        }

        return true;
    }

    public static void ocr() {

        Mat digits = Imgcodecs.imread("digits.png", 0);
        System.out.println(digits.rows() + " " + digits.cols());

        List<Mat> cells = new ArrayList<>(5000);
        double[][] values = new double[5000][400];
        List<List<Double[]>> dataByLabel = new ArrayList<>(10);
        List<double[][]> dataByLabel2 = new ArrayList<>(10);//10 arrays of length 500
        int sample = 0;
        int label = 0;
        int sampleForLabel = 0;
        for (int i=0; i<1000; i+=20) {
            for (int j=0; j<2000; j+=20) {
//                System.out.println("creating cell at " +i+","+ j);
                Mat cell = new Mat(digits, new Range(i, i+20), new Range(j, j+20));
                cells.add(cell);
//                Imgcodecs.imwrite("pics/" + label +  "-"+sampleForLabel+".png", cell);


                int pixel = 0;
                for (int row=0; row<cell.height(); row++) {
                    for (int col=0; col<cell.width(); col++){
                        values[sample][pixel] = cell.get(row, col)[0];
                        if (dataByLabel.isEmpty() || dataByLabel.size() <= label) {
                            dataByLabel.add(new ArrayList<>());
                            dataByLabel2.add(new double[500][400]);
                        }
                        if (dataByLabel.get(label).isEmpty() || dataByLabel.get(label).size() <= sampleForLabel) {
                            dataByLabel.get(label).add(new Double[400]);
                        }
//                        System.out.printf("label: %s, sample: %s, sampleForLabel: %S, pixel: %s\n", label, sample, sampleForLabel, pixel);
                        dataByLabel.get(label).get(sampleForLabel)[pixel] = cell.get(row,col)[0];
                        dataByLabel2.get(label)[sampleForLabel][pixel] = cell.get(row,col)[0];
                        pixel++;
                    }
                }
                sample++;
                sampleForLabel++;
                if (sample%500 == 0 && sample>0) {
                    label++;
                    sampleForLabel = 0;
                }
            }
        }


        List<double[][]> labels = new ArrayList<>(); //10 500x1 arrays
        labels.add(new double[500][1]);
        for (label=0; label<=9; label++) {
            for (int i = 0; i < 500; i++) {

                labels.get(label)[i][0] = (double)label;
//                System.out.printf("label: %s, i: %s\n", label, i);
                if ((i + 1) % 500 == 0 && i > 0 && label<9) {
                    labels.add(new double[500][1]);
//                    System.out.println("label: " + label);
                }
            }
        }

        System.out.println(labels);

        Matrix yYes = new Matrix(labels.get(1));
        Matrix yNo = new Matrix(labels.get(0));
        Matrix zeroData = new Matrix(dataByLabel2.get(0));

        Matrix transpose = zeroData.transpose();
        Matrix xtx = transpose.times(zeroData);
        Matrix inverse = xtx.inverse();
        Matrix psuedoInverse = inverse.times(transpose);

        System.out.println(psuedoInverse);
//        Matrix labels = new Matrix(labelArray);

//        Matrix m = new Matrix(values);

//        m.transpose();
//        Matrix xtx = m.transpose().times(m);
//        (m.transpose().times(m)).inverse();

//        Matrix w = (m.transpose().times(m)).inverse().times(m.transpose()).times(labels);

//        System.out.println(w);
    }

}
