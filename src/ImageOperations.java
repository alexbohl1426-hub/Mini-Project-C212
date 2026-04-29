import java.awt.*;
import java.awt.image.BufferedImage;

class ImageOperations {

    /**
     * Method removes any red to an image by setting the red to 0 for every pixel in the image
     * @param img - the image that we are removing the red in the image
     * @return an image with zero red for every pixel of the image
     */
    static BufferedImage zeroRed(BufferedImage img) {
        // TODO.
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color readColor = new Color(img.getRGB(j,i));
                Color newColor = new Color(0, readColor.getGreen(), readColor.getBlue());
                newImg.setRGB(j,i,newColor.getRGB());
            }
        }
        return newImg;
    }

    /**
     * Method gets rid of all the color in a image and makes it grey (or black/white techinally) by adding red green and blue
     * and dividing by 3 for each pixel.
     * @param img - image that is losing its color and turning grey every pixel
     * @return a BufferedImage object that is the image that has no color and is in black/white
     */
    static BufferedImage grayscale(BufferedImage img) {

        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color readColor = new Color(img.getRGB(j,i));
                int grey = (readColor.getRed() + readColor.getGreen() + readColor.getBlue()) / 3;
                Color newColor = new Color(grey, grey, grey);
                newImg.setRGB(j,i, newColor.getRGB());
            }
        }
        return newImg;
    }

    /**
     * Changes each color for every pixel to the opposite of its 0-255 scale. So for every color, we do 255- the color
     * @param img - the image that is given to invert
     * @return A image the returns the opposite for each color of every pixel
     */
    static BufferedImage invert(BufferedImage img) {

        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color readColor = new Color(img.getRGB(j, i));
                Color newColor = new Color(255-readColor.getRed(), 255- readColor.getGreen(), 255 - readColor.getBlue());
                newImg.setRGB(j,i, newColor.getRGB());
            }
        }
        return newImg;
    }

    /**
     * Flipping the image, either vertically or horizontally. If it's vertical, we flip every pixel from left to right
     * (top and bottom stau the same). If we flip horizontally, top and bottom pixels flip (left and right stau the same0
     * @param img - the image that is being mirrored either vertically or horizontally
     * @param dir - the direction that we are mirroring the image
     * @return a mirror from the original image that is either vertically or horizontally mirrored
     */
    static BufferedImage mirror(BufferedImage img, MirrorMenuItem.MirrorDirection dir) {
        // TODO instantiate newImg with the *correct* dimensions.
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                if (dir == MirrorMenuItem.MirrorDirection.VERTICAL) {
                    if (j < img.getWidth() / 2) {
                        newImg.setRGB(j, i, img.getRGB(j, i));
                    } else {
                        newImg.setRGB(j, i, img.getRGB(img.getWidth() - 1 - j, i));
                    }
                } else {
                    if (i < img.getHeight() / 2) {
                        newImg.setRGB(j, i, img.getRGB(j, i));
                    } else {
                        newImg.setRGB(j, i, img.getRGB(j, img.getHeight() - 1 - i));
                    }
                }
            }
        }

        return newImg;
    }

    /**
     * Method rotates the image 90 degrees counterclockwise or clockwise depending on the param dir. If a image is wide,
     * the image will become tall. If a image is tall, the image will become wide. We rotate the pixels by moving them to new
     * coordinates by making rows into columns and columns into rows
     * @param img - the image that we are rotating 90 degrees counterclockwise or clockwise
     * @param dir - the direction that we are rotating the image 90 degrees (clockwise or counterclockwise)
     * @return a rotation from the original image based on the direction
     */
    static BufferedImage rotate(BufferedImage img, RotateMenuItem.RotateDirection dir) {
        // TODO instantiate newImg with the *correct* dimensions.
        BufferedImage newImg = new BufferedImage(img.getHeight(), img.getWidth(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                if (dir == RotateMenuItem.RotateDirection.CLOCKWISE) {
                    newImg.setRGB(i, (img.getWidth() - 1) - j, img.getRGB(j,i));
                } else {
                    newImg.setRGB((img.getHeight() - 1) - i, j, img.getRGB(j,i));
                }
            }
        }

        return newImg;
    }

    /**
     * Method copies the pixels either horizontally or vertically a certain amount of times (which is what n is). If it
     * is a horizontal repeat, we copy the width n times. If it is a vertical repeat, we copy the height n times
     * @param img - the image that we are repeating
     * @param n  - the number of times we repeat the image (n * height, n * weight depending on the direction).
     * @param dir - the direction that we are repeating the image
     * @return - a new image that is repeating either horizontally or vertically the same image a n amount of times
     */
    static BufferedImage repeat(BufferedImage img, int n, RepeatMenuItem.RepeatDirection dir) {
        BufferedImage newImg = null;
        if (dir == RepeatMenuItem.RepeatDirection.HORIZONTAL) {
           newImg = new BufferedImage(img.getWidth() * n, img.getHeight(), BufferedImage.TYPE_INT_RGB);
           for(int i = 0; i < n; i++){
               for(int j = 0; j < img.getHeight(); j++){
                   for(int k = 0; k < img.getWidth(); k++){
                       newImg.setRGB(k + i * img.getWidth(), j, img.getRGB(k,j));
                   }
               }
           }
        } else {
            newImg = new BufferedImage(img.getWidth(), img.getHeight() * n, BufferedImage.TYPE_INT_RGB);
            for(int i = 0; i < n; i++){
                for(int j = 0; j < img.getHeight(); j++){
                    for(int k = 0; k < img.getWidth(); k++){
                        newImg.setRGB( k, j + i * img.getHeight(), img.getRGB(k,j));
                    }
                }
            }
        }
        return newImg;
    }

    /**
     * Zooms in on the image. The zoom factor increases in multiplicatives of 10% and
     * decreases in multiplicatives of 10%.
     *
     * @param img        the original image to zoom in on. The image cannot be already zoomed in
     *                   or out because then the image will be distorted.
     * @param zoomFactor The factor to zoom in by.
     * @return the zoomed in image.
     */
    static BufferedImage zoom(BufferedImage img, double zoomFactor) {
        int newImageWidth = (int) (img.getWidth() * zoomFactor);
        int newImageHeight = (int) (img.getHeight() * zoomFactor);
        BufferedImage newImg = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, newImageWidth, newImageHeight, null);
        g2d.dispose();
        return newImg;
    }
}
