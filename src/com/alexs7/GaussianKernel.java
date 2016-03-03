package com.alexs7;

/**
 * Created by alex on 01/03/2016.
 */
public class GaussianKernel {

    double[][] template;

    public GaussianKernel(double sigma, boolean normalised) {
        int size = (int) (8.0f * sigma + 1.0f);
        if (size % 2 == 0) size++;
        if(normalised){
            this.template = normalizeTemplate(calculateTemplate(size,sigma));
        }else{
            this.template = calculateTemplate(size,sigma);
        }
    }

    public GaussianKernel(double sigma, int size, boolean normalised) {
        if(normalised){
            this.template = normalizeTemplate(calculateTemplate(size,sigma));
        }else{
            this.template = calculateTemplate(size,sigma);
        }
    }

    private double[][] calculateTemplate(int size, double sigma) {
        int templateCenterXY = (int) Math.floor(size/2);
        double[][] gaussianTemplate = new double [size][size];
        double coefficient;
        double piProduct;
        double eulerNumberProductExponent;
        double eulerNumberProduct;
        piProduct = Math.pow(2 * Math.PI * Math.pow(sigma,2), -1);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {

                int indexXRelativeToCenter = x - templateCenterXY;
                int indexYRelativeToCenter = y - templateCenterXY;

                eulerNumberProductExponent = (Math.pow(indexXRelativeToCenter,2) + Math.pow(indexYRelativeToCenter,2)) / (2 * Math.pow(sigma,2));
                eulerNumberProduct = Math.pow(Math.E, -1*eulerNumberProductExponent);
                coefficient = piProduct * eulerNumberProduct;

                gaussianTemplate[y][x] = coefficient;
            }
        }
        return gaussianTemplate;
    }

    private double[][] normalizeTemplate(double[][] gaussianTemplate) {
        int width = Utilities.getWidthFromTemplate(gaussianTemplate);
        int height  = Utilities.getHeightFromTemplate(gaussianTemplate);
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

    public double[][] getTemplate() {
        return template;
    }
}
