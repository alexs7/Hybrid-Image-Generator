package com.alexs7;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageProcessor {

    Image lowFrequenciesImage;
    Image highFrequenciesImage;

    public ImageProcessor() {
        this.lowFrequenciesImage = null;
        this.highFrequenciesImage = null;
    }

    //Will generate the hybrid image and set the local lowFrequenciesImage and highFrequenciesImage variables
    public Image generateHybridImage(Image firstImage, Image secondImage,
                                     double firstDeviation, double secondDeviation,
                                     boolean userSeperableKernels) throws InvalidKernelSize {
        Gaussian gaussian = null;
        Image hybridImage = null;
        boolean normalised = true;

        gaussian = new Gaussian(firstDeviation,normalised,userSeperableKernels);
        lowFrequenciesImage = generateLowFrequenciesImage(firstImage,gaussian, userSeperableKernels);

        gaussian = new Gaussian(secondDeviation,normalised,userSeperableKernels);
        highFrequenciesImage = subtractImages(secondImage,generateLowFrequenciesImage(secondImage,gaussian,userSeperableKernels));

        hybridImage = addImages(lowFrequenciesImage,highFrequenciesImage);
        return hybridImage;
    }

    private Image generateLowFrequenciesImage(Image image, Gaussian gaussian, boolean userSeperableKernels) throws InvalidKernelSize {
        if(userSeperableKernels){
            double [][] kernel1DX = gaussian.getKernel1DX();
            double [][] kernel1DY = gaussian.getKernel1DY();
            return convolve(convolve(image,kernel1DX),kernel1DY);
        }else{
            return convolve(image,gaussian.getKernel2D());
        }
    }

    public Image convolve(Image image, double[][] template) throws InvalidKernelSize {
        int templateWidth = Utilities.getWidthFromTemplate(template);
        int templateHeight = Utilities.getHeightFromTemplate(template);

        if((templateWidth % 2 == 0) || (templateHeight % 2 == 0)){
            throw new InvalidKernelSize();
        }

        int templateHalfWidth = (int) Math.floor(templateWidth/2);
        int templateHalfHeight = (int) Math.floor(templateHeight/2);

        image = Utilities.padImage(image,templateHalfWidth,templateHalfHeight);

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

        Pixel[][] normalizedImageValues = Utilities.normalizeImageValues(newImageValues,0,255);
        WritableImage wImage = Utilities.getWritableImageFromArrayValues(normalizedImageValues);

        wImage = (WritableImage) Utilities.dePadImage(wImage,templateHalfWidth,templateHalfHeight);

        return wImage;
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

        Pixel[][] normalizedImageValues = Utilities.normalizeImageValues(newImageValues,0,255);
        WritableImage wImage = Utilities.getWritableImageFromArrayValues(normalizedImageValues);
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

        Pixel[][] normalizedImageValues = Utilities.normalizeImageValues(newImageValues,0,255);
        WritableImage wImage = Utilities.getWritableImageFromArrayValues(normalizedImageValues);
        return wImage;
    }

    public Image getLowFrequenciesImage() {
        return lowFrequenciesImage;
    }

    public Image getHighFrequenciesImage() {
        return highFrequenciesImage;
    }

}
