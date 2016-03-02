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

    public Image generateHybridImage(Image firstImage, Image secondImage, double firstDeviation, double secondDeviation) throws InvalidKernelSize {
        GaussianKernel gaussianKernel = null;
        Image lowFrequencyFirstImage = null;
        Image lowFrequencySecondImage = null;
        Image highFrequencySecondImage = null;
        Image hybridImage = null;

        gaussianKernel = new GaussianKernel(firstDeviation,5);
        double[][] firstImageGaussianTemplate = normalizeTemplate(gaussianKernel.getTemplate());

        gaussianKernel = new GaussianKernel(secondDeviation,5);
        double[][] secondImageGaussianTemplate = normalizeTemplate(gaussianKernel.getTemplate());

        lowFrequencyFirstImage = convolveImageWithTemplate(firstImage,firstImageGaussianTemplate);

        lowFrequencySecondImage = convolveImageWithTemplate(secondImage,secondImageGaussianTemplate);
        highFrequencySecondImage = subtractImages(secondImage,lowFrequencySecondImage);

        hybridImage = addImages(lowFrequencyFirstImage,highFrequencySecondImage);

        return hybridImage;
    }

    public Image convolve(Image image, double[][] template) throws InvalidKernelSize {
        int templateWidth = getWidthFromTemplate(template);
        int templateHeight = getHeightFromTemplate(template);

        if((templateWidth % 2 == 0) || (templateHeight % 2 == 0)){
            throw new InvalidKernelSize();
        }

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

                newImageValues[iy][ix] = new Pixel(redChannelValue,greenChannelValue,blueChannelValue);

                redChannelValue = 0;
                greenChannelValue = 0;
                blueChannelValue = 0;
            }
        }

        Pixel[][] normalizedImageValues = normalizeImageValues(newImageValues,0,255);
        WritableImage wImage = getWritableImageFromArrayValues(normalizedImageValues);
        return wImage;
    }

    private Pixel[][] normalizeImageValues(Pixel[][] imageValues,double minNewValue,double maxNewValue) {
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

    private Image addImages(Image firstImage, Image secondImage) {
        int imageWidth = (int) firstImage.getWidth();
        int imageHeight = (int) secondImage.getHeight();

        PixelReader firstImagePixelReader = firstImage.getPixelReader();
        PixelReader secondImagePixelReader = secondImage.getPixelReader();

        Pixel[][] newImageValues = new Pixel[imageHeight][imageWidth];

        double redChannelValue = 0;
        double greenChannelValue = 0;
        double blueChannelValue = 0;

        for (int ix = 0; ix < imageWidth; ix++) {
            for (int iy = 0; iy < imageHeight; iy++) {

                redChannelValue = (firstImagePixelReader.getColor(ix,iy).getRed() + secondImagePixelReader.getColor(ix,iy).getRed());
                greenChannelValue = (firstImagePixelReader.getColor(ix,iy).getGreen() + secondImagePixelReader.getColor(ix,iy).getGreen());
                blueChannelValue = (firstImagePixelReader.getColor(ix,iy).getBlue() + secondImagePixelReader.getColor(ix,iy).getBlue());

                newImageValues[iy][ix] = new Pixel(redChannelValue,greenChannelValue,blueChannelValue);
            }
        }

        Pixel[][] normalizedImageValues = normalizeImageValues(newImageValues,0,255);
        WritableImage wImage = getWritableImageFromArrayValues(normalizedImageValues);
        return wImage;
    }


    public Image subtractImages(Image firstImage, Image secondImage) {
        int imageWidth = (int) firstImage.getWidth();
        int imageHeight = (int) secondImage.getHeight();

        PixelReader firstImagePixelReader = firstImage.getPixelReader();
        PixelReader secondImagePixelReader = secondImage.getPixelReader();

        Pixel[][] newImageValues = new Pixel[imageHeight][imageWidth];

        double redChannelValue = 0;
        double greenChannelValue = 0;
        double blueChannelValue = 0;

        for (int ix = 0; ix < imageWidth; ix++) {
            for (int iy = 0; iy < imageHeight; iy++) {

                redChannelValue = (firstImagePixelReader.getColor(ix,iy).getRed() - secondImagePixelReader.getColor(ix,iy).getRed());
                greenChannelValue = (firstImagePixelReader.getColor(ix,iy).getGreen() - secondImagePixelReader.getColor(ix,iy).getGreen());
                blueChannelValue = (firstImagePixelReader.getColor(ix,iy).getBlue() - secondImagePixelReader.getColor(ix,iy).getBlue());

                newImageValues[iy][ix] = new Pixel(redChannelValue,greenChannelValue,blueChannelValue);
            }
        }

        Pixel[][] normalizedImageValues = normalizeImageValues(newImageValues,0,255);
        WritableImage wImage = getWritableImageFromArrayValues(normalizedImageValues);
        return wImage;
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
