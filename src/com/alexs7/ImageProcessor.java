package com.alexs7;

import com.sun.corba.se.spi.ior.Writeable;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import javax.imageio.ImageReader;
import java.text.DecimalFormat;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageProcessor {

    public void generateHybridImage(Image firstImage, Image secondImage, int templateWidthInt, int templateHeightInt) {
        double[][] gaussianTemplate = getGaussianTemplate(templateWidthInt,templateHeightInt, 0.1);

        Image image = convolveImageWithTemplate(firstImage,gaussianTemplate,templateWidthInt,templateHeightInt);

    }

    public Image convolveImageWithTemplate(Image image, double[][] template, int templateWidth, int templateHeight) {
        int templateHalfWidth = (int) Math.floor(templateWidth/2);
        int templateHalfHeight = (int) Math.floor(templateHeight/2);
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();

        Pixel[][] newImageValues = new Pixel[imageHeight][imageWidth];

        double redChannelValue = 0;
        double greenChannelValue = 0;
        double blueChannelValue = 0;

        for (int ix = templateHalfWidth; ix < (imageWidth - templateHalfWidth); ix++) {
            for (int iy = templateHalfHeight; iy < (imageHeight - templateHalfHeight); iy++) {

                for (int tx = 0; tx < templateWidth; tx++) {
                    for (int ty = 0; ty < templateHeight; ty++) {
                        int xIndex = ix+tx-templateHalfWidth;
                        int yIndex = iy+ty-templateHalfHeight;
                        redChannelValue += (pixelReader.getColor(xIndex,yIndex).getRed() * 255) * template[ty][tx];
                        greenChannelValue += (pixelReader.getColor(xIndex,yIndex).getGreen() * 255) * template[ty][tx];
                        blueChannelValue += (pixelReader.getColor(xIndex,yIndex).getBlue() * 255) * template[ty][tx];
                    }
                }

                newImageValues[iy][ix] = new Pixel();
                newImageValues[iy][ix].setRed(redChannelValue);
                newImageValues[iy][ix].setGreen(greenChannelValue);
                newImageValues[iy][ix].setBlue(blueChannelValue);

                redChannelValue = 0;
                greenChannelValue = 0;
                blueChannelValue = 0;
            }
        }

        Pixel[][] normalizedImageValues = normalizeImageValues(newImageValues,imageWidth,imageHeight);
        WritableImage wImage = getWritableImageFromArrayValues(normalizedImageValues,imageWidth,imageHeight);
        return wImage;
    }

    private Pixel[][] normalizeImageValues(Pixel[][] imageValues, int imageWidth, int imageHeight) {
        double minRedValue = 0;
        double maxRedValue = 0;
        double minGreenValue = 0;
        double maxGreenValue = 0;
        double minBlueValue = 0;
        double maxBlueValue = 0;
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

        double newRedRange = maxRedValue - minRedValue;
        double newGreenRange = maxGreenValue - minGreenValue;
        double newBlueRange = maxBlueValue - minBlueValue;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageValues[y][x];
                if(pixel != null) {
                    normalizedImageValues[y][x] = new Pixel( pixel.getRed() * 255 / newRedRange,
                                                             pixel.getGreen() * 255 / newGreenRange,
                                                             pixel.getBlue() * 255 / newBlueRange);
                }
            }
        }

        return normalizedImageValues;
    }

    private WritableImage getWritableImageFromArrayValues(Pixel[][] imageValues, int imageWidth, int imageHeight) {
        WritableImage wImage = new WritableImage(imageWidth,imageHeight);
        wImage = fillImage(wImage,Color.BLACK);
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

    public WritableImage fillImage(WritableImage wImage, Color color) {
        PixelWriter pixelWriter = wImage.getPixelWriter();
        for (int ix = 0; ix < wImage.getWidth(); ix++) {
            for (int iy = 0; iy < wImage.getHeight(); iy++) {
                pixelWriter.setColor(ix, iy, color);
            }
        }
        return wImage;
    }

    public double[][] getGaussianTemplate(int width, int height, double sigma){
        double[][] gaussianTemplate = new double [height][width];
        double coefficient;
        double piProduct;
        double eulerNumberProductExponent;
        double eulerNumberProduct;

        piProduct = Math.pow(2 * Math.PI * Math.pow(sigma,2), -1);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                eulerNumberProductExponent = (Math.pow(x,2) + Math.pow(y,2)) / (2 * Math.pow(sigma,2));
                eulerNumberProduct = Math.pow(Math.E, -1*eulerNumberProductExponent);
                coefficient = piProduct * eulerNumberProduct;

                gaussianTemplate[y][x] = coefficient;
                System.out.println("At x: "+x+" and y: "+y+" the coefficient is: "+coefficient);
            }
        }

        return gaussianTemplate;
    }

    public double[][] getAveragingTemplate(){
        return new double[][]{
                { 0.333, 0.333, 0.333 },
                { 0.333, 0.333, 0.333 },
                { 0.333, 0.333, 0.333 },
        };
    }

    //used for debugging purposes
    private void printTemplate(double[][] template, int width, int height){
        DecimalFormat df = new DecimalFormat("0.000000");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                System.out.print(" | " + df.format(template[y][x]));
            }
            System.out.println();
        }
    }
}
