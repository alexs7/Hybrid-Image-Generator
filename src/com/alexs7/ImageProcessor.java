package com.alexs7;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

/**
 * Created by alex on 20/02/2016.
 */
public class ImageProcessor {
    public void generateHybridImage(Image firstImage, Image secondImage) {
        PixelReader pixelReader = firstImage.getPixelReader();
        System.out.println("Pixel Format: "+ pixelReader.getPixelFormat());
    }
}
