package com.example.scan.scan;


import android.graphics.Bitmap;

/**
 * Created by Kyle & friends on 10/9/16.
 */

public class PreProcessing {

    /**
     * @param bmp the bitmap to clone
     * @return a new, mutable copy of the bitmap
     */
    public static Bitmap cloneToMute(Bitmap bmp) {
        return bmp.copy(bmp.getConfig(), true);
    }

    /**
     * @param bmp the bitmap (in color) that will be converted into B/W
     * @return Bitmap the converted, B/W only, image
     */
    public static Bitmap toBlackWhite(Bitmap bmp) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {
                int pixel = bmp.getPixel(j, i);
                int red = (pixel & 0xFF0000) << 16;
                int green = (pixel & 0x00FF00) << 8;
                int blue = (pixel & 0x0000FF);

                // arbitrary thresholds are fun
                if (red + green + blue > (128 * 3)) {
                    bmp.setPixel(j, i, 0xFFFFFFFF);
                } else {
                    bmp.setPixel(j, i, 0xFF000000);
                }

            }

        }


        return bmp;
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