package com.alexs7;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageProcessor {

    public Image generateHybridImage(Image firstImage, Image secondImage, double firstDeviation, double secondDeviation) {
        GaussianKernel gaussianKernel;

        gaussianKernel = new GaussianKernel(firstDeviation,5);
        double[][] firstImageGaussianTemplate = normalizeTemplate(gaussianKernel.getTemplate());
        gaussianKernel = new GaussianKernel(secondDeviation,5);
        double[][] secondImageGaussianTemplate = normalizeTemplate(gaussianKernel.getTemplate());

        Image lowFrequencyImage = convolveImageWithTemplate(secondImage,secondImageGaussianTemplate);
        //Image highFrequencyImage = subtractImages(secondImage, lowFrequencyImage);

        return lowFrequencyImage;
    }

    public Image subtractImages(Image image, Image lowFreqImage) {
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();

        PixelReader imagePixelReader = image.getPixelReader();
        PixelReader lowFreqImagePixelReader = lowFreqImage.getPixelReader();

        Pixel[][] newImageValues = new Pixel[imageHeight][imageWidth];

        double redChannelValue = 0;
        double greenChannelValue = 0;
        double blueChannelValue = 0;

        for (int ix = 0; ix < imageWidth; ix++) {
            for (int iy = 0; iy < imageHeight; iy++) {
                redChannelValue = (imagePixelReader.getColor(ix,iy).getRed() * 255) - (lowFreqImagePixelReader.getColor(ix,iy).getRed()* 255);
                greenChannelValue = (imagePixelReader.getColor(ix,iy).getGreen() * 255) - (lowFreqImagePixelReader.getColor(ix,iy).getGreen()* 255);
                blueChannelValue = (imagePixelReader.getColor(ix,iy).getBlue() * 255) - (lowFreqImagePixelReader.getColor(ix,iy).getBlue()* 255);

                if(redChannelValue < 0) { redChannelValue = 0; }
                if(greenChannelValue < 0) { greenChannelValue = 0; }
                if(blueChannelValue < 0) { blueChannelValue = 0; }

                if(redChannelValue > 255) { redChannelValue = 255; }
                if(greenChannelValue > 255) { greenChannelValue = 255; }
                if(blueChannelValue > 255) { blueChannelValue = 255; }

                newImageValues[iy][ix] = new Pixel();
                newImageValues[iy][ix].setRed(redChannelValue);
                newImageValues[iy][ix].setGreen(greenChannelValue);
                newImageValues[iy][ix].setBlue(blueChannelValue);

                //System.out.println("Value at x: "+ix+" and y: "+iy+" wil be red: "+redChannelValue+" green: "+greenChannelValue+" blue: "+ blueChannelValue);
            }
        }

        Pixel[][] normalizedImageValues = normalizeImageValues(newImageValues);
        WritableImage wImage = getWritableImageFromArrayValues(normalizedImageValues);
        return wImage;
    }

    public Image convolveImageWithTemplate(Image image, double[][] template) {
        int templateWidth = getWidthFromTemplate(template);
        int templateHeight = getHeightFromTemplate(template);

        int templateHalfWidth = (int) Math.floor(templateWidth/2);
        int templateHalfHeight = (int) Math.floor(templateHeight/2);

        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();

        Pixel[][] newImageValues = new Pixel[imageHeight][imageWidth];

        double redChannelValue = 0;
        double greenChannelValue = 0;
        double blueChannelValue = 0;

        for (int ix = templateHalfWidth; ix < (imageWidth - templateHalfWidth); ix++) { //x=templateHalfWidth needs to change!!!
            for (int iy = templateHalfHeight; iy < (imageHeight - templateHalfHeight); iy++) {

                for (int tx = 0; tx < templateWidth; tx++) {
                    for (int ty = 0; ty < templateHeight; ty++) {
                        int xIndex = ix+tx-templateHalfWidth;
                        int yIndex = iy+ty-templateHalfHeight;
                        redChannelValue += pixelReader.getColor(xIndex,yIndex).getRed() * template[ty][tx];
                        greenChannelValue += pixelReader.getColor(xIndex,yIndex).getGreen() * template[ty][tx];
                        blueChannelValue += pixelReader.getColor(xIndex,yIndex).getBlue() * template[ty][tx];
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

        Pixel[][] normalizedImageValues = normalizeImageValues(newImageValues);
        WritableImage wImage = getWritableImageFromArrayValues(normalizedImageValues);
        return wImage;
    }

    private Pixel[][] normalizeImageValues(Pixel[][] imageValues) {
        int imageWidth = getWidthFromPixelArray(imageValues);
        int imageHeight = getHeightFromPixelArray(imageValues);
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

                    double red = pixel.getRed();
                    double green = pixel.getGreen();
                    double blue = pixel.getBlue();

                    normalizedImageValues[y][x] = new Pixel( red * (255 / newRedRange),
                                                             green * (255 / newGreenRange),
                                                             blue * (255 / newBlueRange));
                }
            }
        }

        return normalizedImageValues;
    }

    private WritableImage getWritableImageFromArrayValues(Pixel[][] imageValues) {
        int imageWidth = getWidthFromPixelArray(imageValues);
        int imageHeight = getHeightFromPixelArray(imageValues);
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

    private double[][] normalizeTemplate(double[][] gaussianTemplate) {
        int width = getWidthFromTemplate(gaussianTemplate);
        int height  = getHeightFromTemplate(gaussianTemplate);
        double[][] normalizedTemplate = new double[height][width];
        double sum = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                sum += gaussianTemplate[y][x];
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                normalizedTemplate[y][x] = gaussianTemplate[y][x] / sum;
            }
        }

        return normalizedTemplate;
    }

    private int getHeightFromTemplate(double[][] template) {
        return template.length;
    }

    private int getWidthFromTemplate(double[][] template) {
        return template[0].length;
    }

    private int getHeightFromPixelArray(Pixel[][] template) {
        return template.length;
    }

    private int getWidthFromPixelArray(Pixel[][] template) {
        return template[0].length;
    }

    //methods below used for debugging and testing purposes

    private void printTemplate(double[][] template, int width, int height){
        DecimalFormat df = new DecimalFormat("0.000000");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(df.format(template[y][x]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private double[][] getAveragingTemplate(){
        return new double[][]{
                { 0.333, 0.333, 0.333 },
                { 0.333, 0.333, 0.333 },
                { 0.333, 0.333, 0.333 },
        };
    }
}
