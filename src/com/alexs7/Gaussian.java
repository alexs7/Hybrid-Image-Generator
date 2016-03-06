package com.alexs7;

/**
 * Created by alex on 01/03/2016.
 */
public class Gaussian {

    double[][] kernel2D;
    double[][] kernel1DX;
    double[][] kernel1DY;

    public Gaussian(double sigma, boolean normalised, boolean seperable) {
        int size = (int) (8.0f * sigma + 1.0f);
        if (size % 2 == 0) size++;

        if(seperable){
            this.kernel1DX = calculate1DComponentKernelX(size,sigma);
            this.kernel1DY = calculate1DComponentKernelY(size,sigma);
            return;
        }

        if(normalised){
            this.kernel2D = normalizeTemplate(calculateTemplate(size,sigma));
        }else{
            this.kernel2D = calculateTemplate(size,sigma);
        }
    }

    public Gaussian(double sigma, int size, boolean normalised, boolean seperable) {

        if(seperable){
            this.kernel1DX = calculate1DComponentKernelX(size,sigma);
            this.kernel1DY = calculate1DComponentKernelY(size,sigma);
            return;
        }

        if(normalised){
            this.kernel2D = normalizeTemplate(calculateTemplate(size,sigma));
        }else{
            this.kernel2D = calculateTemplate(size,sigma);
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

    private double[][] calculate1DComponentKernelX(int size, double sigma) {
        int templateCenter = (int) Math.floor(size/2);
        double[][] componentKernel = new double [1][size];
        double coefficient;
        double piProduct;
        double eulerNumberProductExponent;
        double eulerNumberProduct;
        piProduct = Math.pow(2 * Math.PI * Math.pow(sigma,2), -1);
        double sum = 0;

        for (int i = 0; i < size; i++) {

                int indexRelativeToCenter = i - templateCenter;
                eulerNumberProductExponent = (Math.pow(indexRelativeToCenter,2)) / (2 * Math.pow(sigma,2));
                eulerNumberProduct = Math.pow(Math.E, -1*eulerNumberProductExponent);
                coefficient = piProduct * eulerNumberProduct;

                componentKernel[0][i] = coefficient;
                sum += coefficient;
        }

        for (int i = 0; i < size; i++) {
            componentKernel[0][i] = componentKernel[0][i] / sum;
        }

        return componentKernel;
    }

    private double[][] calculate1DComponentKernelY(int size, double sigma) {
        int templateCenter = (int) Math.floor(size/2);
        double[][] componentKernel = new double [size][1];
        double coefficient;
        double piProduct;
        double eulerNumberProductExponent;
        double eulerNumberProduct;
        piProduct = Math.pow(2 * Math.PI * Math.pow(sigma,2), -1);
        double sum = 0;

        for (int i = 0; i < size; i++) {

            int indexRelativeToCenter = i - templateCenter;
            eulerNumberProductExponent = (Math.pow(indexRelativeToCenter,2)) / (2 * Math.pow(sigma,2));
            eulerNumberProduct = Math.pow(Math.E, -1*eulerNumberProductExponent);
            coefficient = piProduct * eulerNumberProduct;

            componentKernel[i][0] = coefficient;
            sum += coefficient;
        }

        for (int i = 0; i < size; i++) {
            componentKernel[i][0] = componentKernel[i][0] / sum;
        }

        return componentKernel;
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

    public double[][] getKernel2D() {
        return kernel2D;
    }

    public double[][] getKernel1DX() {
        return kernel1DX;
    }

    public double[][] getKernel1DY() {
        return kernel1DY;
    }

}
