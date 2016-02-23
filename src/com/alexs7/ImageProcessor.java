package com.alexs7;

import javafx.scene.image.Image;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageProcessor {

    public void generateHybridImage(Image firstImage, Image secondImage, int templateWidthInt, int templateHeightInt) {

        getGaussianTemplate(templateWidthInt,templateHeightInt, 0.1);

    }

    public double[][] getGaussianTemplate(int width, int height, double sigma){
        double[][] gaussianTemplate = new double [height][width];
        double coefficient = 0.0;
        double sum = 0.0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                coefficient = Math.exp(-((x*x+y*y)/2*sigma*sigma))/(2*Math.PI*sigma*sigma); //??
                sum = sum + coefficient;
                gaussianTemplate[y][x] = coefficient;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gaussianTemplate[y][x] = gaussianTemplate[y][x] / sum;
                System.out.print(" " + gaussianTemplate[y][x]);
            }
            System.out.println();
        }

        return gaussianTemplate;
    }
}
