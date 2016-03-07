# Hybrid Image Generator In Java
Java code implementing this [paper](http://cvcl.mit.edu/publications/OlivaTorralb_Hybrid_Siggraph06.pdf).
The purpose of this is to use 2 equal sized images and generate a hybrid image, as discussed in the above paper. The high image frequencies are removed by using a Guassian kernel and using convolution with the image.

The seperability of the Gaussian 2D kernel is taken into consideration, and I split it into two 1D components for increased performance.

#### Code for template and image convolution:

```java
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
```

#### Screenshots
As you zoom out the low frequencies image is shown.

![](http://s29.postimg.org/q4342awtz/image.png)

![](http://s11.postimg.org/s1qagc0sj/image.png)

![](http://s14.postimg.org/spb7iqig1/image.png)

