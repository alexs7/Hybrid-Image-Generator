package com.alexs7;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;

/**
 * Created by alex on 04/03/2016.
 */
public class Utilities {

    public static Image padImage(Image image, int templateHalfWidth, int templateHalfHeight) {
        Image paddedImage;
        int paddedImageWidth = (int) image.getWidth() + (templateHalfWidth * 2);
        int paddedImageHeight = (int) image.getHeight() + (templateHalfHeight * 2);
        Pixel[][] paddedImageValues = new Pixel[paddedImageHeight][paddedImageWidth];

        PixelReader pixelReader = image.getPixelReader();
        double red;
        double green;
        double blue;

        for (int px = 0; px < paddedImageWidth; px++) {
            for (int py = 0; py < paddedImageHeight; py++) {
                paddedImageValues[py][px] = new Pixel(0,0,0);
            }
        }

        for (int px = templateHalfWidth; px < paddedImageWidth - templateHalfWidth; px++) {
            for (int py = templateHalfHeight; py < paddedImageHeight - templateHalfHeight; py++) {

                red = pixelReader.getColor(px-templateHalfWidth,py-templateHalfHeight).getRed();
                green = pixelReader.getColor(px-templateHalfWidth,py-templateHalfHeight).getGreen();
                blue = pixelReader.getColor(px-templateHalfWidth,py-templateHalfHeight).getBlue();
                paddedImageValues[py][px] = new Pixel(red,green,blue);

            }
        }

        Pixel[][] normalizedImageValues = Utilities.normalizeImageValues(paddedImageValues,0,255);
        paddedImage = Utilities.getWritableImageFromArrayValues(normalizedImageValues);
        return paddedImage;
    }

    public static Image dePadImage(Image inputImage, int templateHalfWidth, int templateHalfHeight) {
        Image image;
        int imageWidth = (int) inputImage.getWidth() - (templateHalfWidth * 2);
        int imageHeight = (int) inputImage.getHeight() - (templateHalfHeight * 2);
        Pixel[][] paddedImageValues = new Pixel[imageHeight][imageWidth];

        PixelReader pixelReader = inputImage.getPixelReader();
        double red;
        double green;
        double blue;

        for (int px = 0; px < imageWidth; px++) {
            for (int py = 0; py < imageHeight; py++) {

                red = pixelReader.getColor(px+templateHalfWidth,py+templateHalfHeight).getRed();
                green = pixelReader.getColor(px+templateHalfWidth,py+templateHalfHeight).getGreen();
                blue = pixelReader.getColor(px+templateHalfWidth,py+templateHalfHeight).getBlue();
                paddedImageValues[py][px] = new Pixel(red,green,blue);

            }
        }

        Pixel[][] normalizedImageValues = normalizeImageValues(paddedImageValues,0,255);
        image = getWritableImageFromArrayValues(normalizedImageValues);
        return image;
    }

    public static Pixel[][] normalizeImageValues(Pixel[][] imageValues,double minNewValue,double maxNewValue) {
        int imageWidth = getWidthFromPixelArray(imageValues);
        int imageHeight = getHeightFromPixelArray(imageValues);
        double minRedValue = Double.MAX_VALUE;
        double maxRedValue = Double.MIN_VALUE;
        double minGreenValue = Double.MAX_VALUE;
        double maxGreenValue = Double.MIN_VALUE;
        double minBlueValue = Double.MAX_VALUE;
        double maxBlueValue = Double.MIN_VALUE;
        double normalizedRedValue;
        double normalizedGreenValue;
        double normalizedBlueValue;
        Pixel[][] normalizedImageValues = new Pixel[imageHeight][imageWidth];
        Pixel pixel;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageValues[y][x];
                if(pixel != null) {
                    if(pixel.getRed() < minRedValue) { minRedValue = pixel.getRed(); }
                    if(pixel.getRed() > maxRedValue) { maxRedValue = pixel.getRed(); }

                    if(pixel.getGreen() < minGreenValue) { minGreenValue = pixel.getGreen(); }
                    if(pixel.getGreen() > maxGreenValue) { maxGreenValue = pixel.getGreen(); }

                    if(pixel.getBlue() < minBlueValue) { minBlueValue = pixel.getBlue(); }
                    if(pixel.getBlue() > maxBlueValue) { maxBlueValue = pixel.getBlue(); }
                }
            }
        }

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageValues[y][x];
                if(pixel != null) {

                    double red = pixel.getRed();
                    double green = pixel.getGreen();
                    double blue = pixel.getBlue();

                    normalizedRedValue = (red - minRedValue) * ((maxNewValue - minNewValue)/(maxRedValue - minRedValue)) + minNewValue;
                    normalizedGreenValue = (green - minGreenValue) * ((maxNewValue - minNewValue)/(maxGreenValue - minGreenValue)) + minNewValue;
                    normalizedBlueValue = (blue - minBlueValue) * ((maxNewValue - minNewValue)/(maxBlueValue - minBlueValue)) + minNewValue;

                    normalizedImageValues[y][x] = new Pixel( normalizedRedValue,
                            normalizedGreenValue,
                            normalizedBlueValue);
                }
            }
        }

        return normalizedImageValues;
    }

    public static WritableImage getWritableImageFromArrayValues(Pixel[][] imageValues) {
        int imageWidth = Utilities.getWidthFromPixelArray(imageValues);
        int imageHeight = Utilities.getHeightFromPixelArray(imageValues);
        WritableImage wImage = new WritableImage(imageWidth,imageHeight);
        wImage = fillImage(wImage, Color.BLACK);
        PixelWriter pixelWriter = wImage.getPixelWriter();
        Pixel pixel;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageValues[y][x];
                if(pixel != null) {
                    pixelWriter.setColor(x, y, Color.rgb((int) pixel.getRed(),
                            (int) pixel.getGreen(),
                            (int) pixel.getBlue()));
                }
            }
        }
        return wImage;
    }

    public static WritableImage fillImage(WritableImage wImage, Color color) {
        PixelWriter pixelWriter = wImage.getPixelWriter();
        for (int ix = 0; ix < wImage.getWidth(); ix++) {
            for (int iy = 0; iy < wImage.getHeight(); iy++) {
                pixelWriter.setColor(ix, iy, color);
            }
        }
        return wImage;
    }

    public static int getHeightFromTemplate(double[][] template) {
        return template.length;
    }

    public static int getWidthFromTemplate(double[][] template) {
        return template[0].length;
    }

    public static int getHeightFromPixelArray(Pixel[][] template) {
        return template.length;
    }

    public static int getWidthFromPixelArray(Pixel[][] template) {
        return template[0].length;
    }

    //methods below used for debugging and testing purposes
    private static void printTemplate(double[][] template, int width, int height){
        DecimalFormat df = new DecimalFormat("0.000000");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(df.format(template[y][x]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static double[][] getAveragingTemplate(){
        return new double[][]{
                { 0.333, 0.333, 0.333 },
                { 0.333, 0.333, 0.333 },
                { 0.333, 0.333, 0.333 },
        };
    }

}
