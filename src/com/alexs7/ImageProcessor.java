package com.alexs7;

import javafx.scene.image.Image;

import java.text.DecimalFormat;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageProcessor {

    public void generateHybridImage(Image firstImage, Image secondImage, int templateWidthInt, int templateHeightInt) {
        double[][] gaussianTemplate = getGaussianTemplate(templateWidthInt,templateHeightInt, 0.1);

        Image image = convolveImageWithTemplate(firstImage,gaussianTemplate,templateWidthInt,templateHeightInt);

    }

    public Image convolveImageWithTemplate(Image firstImage, double[][] gaussianTemplate, int templateWidth, int templateHeight) {
        int templateHalfWidth = (int) Math.floor(templateWidth/2);
        int templateHalfHeight = (int) Math.floor(templateHeight/2);

        for (int ix = templateHalfWidth; ix < ((firstImage.getWidth() - templateHalfWidth)-1) ; ix++) {
            for (int iy = templateHalfHeight; iy < ((firstImage.getHeight() - templateHalfHeight)-1); iy++) {

                for (int tx = 0; tx < templateHalfWidth; tx++) {
                    for (int ty = 0; ty < templateHalfHeight; ty++) {
                        //do convolution here!
                    }
                }

            }
        }


        return firstImage;
    }

    public double[][] getGaussianTemplate(int width, int height, double sigma){
        double[][] gaussianTemplate = new double [height][width];
        double coefficient;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                coefficient = Math.exp(-((x*x+y*y)/2*sigma*sigma))/(2*Math.PI*sigma*sigma); //??
                System.out.println(coefficient);
                gaussianTemplate[y][x] = coefficient;
            }
        }

        double[][] normalizedGaussianTemplate = normalizeTemplate(gaussianTemplate,width,height);

        printTemplate(gaussianTemplate,width,height);

        return normalizedGaussianTemplate;
    }

    public double[][] normalizeTemplate(double[][] gaussianTemplate, int width, int height) {
        double sum = 0.0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                sum += gaussianTemplate[y][x];
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gaussianTemplate[y][x] = gaussianTemplate[y][x] / sum;
            }
        }

        return gaussianTemplate;
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
