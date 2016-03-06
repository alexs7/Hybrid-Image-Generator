package com.alexs7;

/**
 * Created by alex on 01/03/2016.
 */
public class Gaussian {

    double[][] kernel;
    double[] xComponentKernel;
    double[] yComponentKernel;

    public Gaussian(double sigma, boolean normalised) {
        int size = (int) (8.0f * sigma + 1.0f);
        if (size % 2 == 0) size++;
        if(normalised){
            this.kernel = normalizeTemplate(calculateTemplate(size,sigma));

            this.xComponentKernel = calculateXComponentKernel(size,sigma);
        }else{
            this.kernel = calculateTemplate(size,sigma);
        }
    }

    public Gaussian(double sigma, int size, boolean normalised) {
        if(normalised){
            this.kernel = normalizeTemplate(calculateTemplate(size,sigma));
        }else{
            this.kernel = calculateTemplate(size,sigma);
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

    private double[] calculateXComponentKernel(int size, double sigma) {
        int templateCenter = (int) Math.floor(size/2);
        double[] xComponentKernel = new double [size];
        double coefficient;
        double piProduct;
        double eulerNumberProductExponent;
        double eulerNumberProduct;
        piProduct = Math.pow(2 * Math.PI * Math.pow(sigma,2), -1);
        double sum = 0;

        for (int x = 0; x < size; x++) {

                int indexXRelativeToCenter = x - templateCenter;
                eulerNumberProductExponent = (Math.pow(indexXRelativeToCenter,2)) / (2 * Math.pow(sigma,2));
                eulerNumberProduct = Math.pow(Math.E, -1*eulerNumberProductExponent);
                coefficient = piProduct * eulerNumberProduct;

                xComponentKernel[x] = coefficient;
                sum += coefficient;
        }

        for (int x = 0; x < size; x++) {
            xComponentKernel[x] = xComponentKernel[x] / sum;

            System.out.print(" "+xComponentKernel[x]);

        }

        System.out.println();

        double foo = 0;
        for (int x = 0; x < size; x++) {
            foo += xComponentKernel[x];

            System.out.print("foo "+foo);
        }


        return xComponentKernel;
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

    public double[][] getKernel() {
        return kernel;
    }
}
