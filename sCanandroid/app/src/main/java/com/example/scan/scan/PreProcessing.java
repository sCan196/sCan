package com.example.scan.scan;


import android.graphics.Bitmap;

/**
 * SHUT UP ANDROID STUDIO
 * Created by Kyle & friends on 10/9/16.
 */

public class PreProcessing {

    /**
     * encapsulation
     */
    public static Bitmap doStuff(Bitmap in) {
        Bitmap bmp = toGrayscale(cloneToMute(in));

        System.out.println(bmp.getWidth());
        System.out.println(bmp.getHeight());
        return bmp;
    }

    /**
     * @param bmp the bitmap to clone
     * @return a new, mutable copy of the bitmap
     */
    public static Bitmap cloneToMute(Bitmap bmp) {
        return bmp.copy(bmp.getConfig(), true);
    }

    /**
     * @param pixel packed RRGGBB pixel
     * @return int[] {red, green, blue}
     */
    private static int[] unpackRGB(int pixel) {
        int red = (pixel & 0xFF0000) >> 16;
        int green = (pixel & 0x00FF00) >> 8;
        int blue = (pixel & 0x0000FF);
        return new int[] {red, green, blue};
    }

    /**
     * taken from Wikipedia...
     */
    private static int RGBtoGray(int r, int g, int b) {
        return Math.round(r * 0.2126f + g * 0.7152f + b * 0.0722f);
    }

    /**
     * in-place to-grayscale
     * @param bmp input bitmap
     * @return the same bitmap, but modified into grayscale
     */
    public static Bitmap toGrayscale(Bitmap bmp) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();



        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] prgb = unpackRGB(bmp.getPixel(j, i));
                int conv = RGBtoGray(prgb[0], prgb[1], prgb[2]);
                bmp.setPixel(j, i, conv * 0x010101);
            }
        }

        return bmp;
    }

    /**
     * Returns an image histogram.
     * @param bmp the image to analyze
     * @return an int[][] object, with one int[] for each of R, G, B
     */
    public static int[][] getHistogram(Bitmap bmp) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        int[] histR = new int[256];
        int[] histG = new int[256];
        int[] histB = new int[256];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] prgb = unpackRGB(bmp.getPixel(j, i));
                histR[prgb[0]] += 1;
                histG[prgb[1]] += 1;
                histB[prgb[2]] += 1;
            }
        }

        return new int[][] {histR, histG, histB};
    }

    /**
     *
     *
     * @param bmp the bitmap to find the corners off of
     * @return int[] corners a integer array with 8 values,
     * each one represents an x (odd indicies) or y (even) coordinate,
     * starting with the top and going clockwise
     *
     * an array because Java doesn't have good tuples
     */
    public static int[] getCorners(Bitmap bmp) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        int[] corners = new int[8];
        int counter = 0;

        // finds top corner
        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {

                if (bmp.getPixel(j, i) == 0xFFFFFFFF) {
                    corners[counter] = j;
                    counter++;
                    corners[counter] = i;
                    counter++;
                    break;
                }

            }

        }

        // finds right corner
        for (int i = width - 1; i >= 0; i--) {

            for (int j = 0; j < height; j++) {

                if (bmp.getPixel(i, j) == 0xFFFFFFFF) {
                    corners[counter] = j;
                    counter++;
                    corners[counter] = i;
                    counter++;
                    break;
                }

            }

        }

        // finds bottom corner
        for (int i = height - 1; i >= 0; i--) {

            for (int j = width - 1; j >= 0; j--) {

                if (bmp.getPixel(j, i) == 0xFFFFFFFF) {
                    corners[counter] = j;
                    counter++;
                    corners[counter] = i;
                    counter++;
                    break;
                }

            }

        }

        // finds left corner
        for (int i = 0; i < width; i++) {

            for (int j = 0; j < height; j++) {

                if (bmp.getPixel(i, j) == 0xFFFFFFFF) {
                    corners[counter] = j;
                    counter++;
                    corners[counter] = i;
                    counter++;
                    break;
                }

            }

        }

        return corners;
    }

    /**
     *
     * @param bmp the Bitmap with imperfect orientation
     * @param corners the array of corners to work off of as reference
     * @return Bitmap the corrected file, NOT resized
     */
    public static Bitmap correctSpin(Bitmap bmp, int[] corners) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        Bitmap resultBmp = bmp.copy(bmp.getConfig(), bmp.isMutable());

        double slope = (corners[3] - corners[1] + 0.0) / (corners[2] - corners[0] + 0.0);
        double angle = Math.atan(slope);

        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j ++) {
                // this math has not been checked, please do check
                int pixelTemp = bmp.getPixel(corners[0] + (int) (j * Math.cos(angle)), corners[1] + (int) (i * Math.sin(angle)));
                resultBmp.setPixel(j, i, pixelTemp);
            }

        }

        return resultBmp;
    }


}