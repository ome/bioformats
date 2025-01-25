/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package loci.formats.pyramidio;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author Antoine Vandecreme
 */
public class ImageResizingHelper {

    private ImageResizingHelper() {
    }

    /**
     * Returns resized image Useful reference on high quality image resizing can
     * be found here:
     * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
     *
     * @param width the required width
     * @param height the required height
     * @param img the image to be resized
     * @return the resized image
     */
    public static BufferedImage resizeImage(BufferedImage img,
            int width, int height) {
        int currentWidth = img.getWidth();
        int currentHeight = img.getHeight();
        if (currentWidth == width && currentHeight == height) {
            return img;
        }
        if (img.getType() == BufferedImage.TYPE_USHORT_GRAY) {
            return resize16bppImage(img, width, height);
        }
        if (img.getColorModel().getPixelSize() == 32) {
            return resize32bppImage(img, width, height);
        }

        if (width > currentWidth || height > currentHeight) {
            return resizeImageGraphics2D(img, width, height);
        } else {
            BufferedImage result = img;
            double ratio = 0.79;
            do {
                if (currentWidth > width) {
                    currentWidth *= ratio;
                    if (currentWidth < width) {
                        currentWidth = width;
                    }
                }
                if (currentHeight > height) {
                    currentHeight *= ratio;
                    if (currentHeight < height) {
                        currentHeight = height;
                    }
                }
                result = resizeImageGraphics2D(result, currentWidth, currentHeight);
            } while (currentWidth != width || currentHeight != height);
            return result;
        }
    }

    private static BufferedImage resizeImageGraphics2D(BufferedImage img,
            int width, int height) {
        BufferedImage result = new BufferedImage(width, height, img.getType());
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();
        return result;
    }

    /**
     * Resize a 16bpp image. Code based on imagej.
     *
     * @param img
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    private static BufferedImage resize16bppImage(BufferedImage img,
            int dstWidth, int dstHeight) {
        BufferedImage result = new BufferedImage(dstWidth, dstHeight, img.getType());
        WritableRaster raster = result.getRaster();

        int width = img.getWidth();
        int height = img.getHeight();
        double srcCenterX = width / 2.0;
        double srcCenterY = height / 2.0;
        double xScale = dstWidth / (double) width;
        double yScale = dstHeight / (double) height;
        double dstCenterX = (dstWidth + xScale) / 2;
        double dstCenterY = (dstHeight + yScale) / 2;

        for (int y = 0; y < dstHeight; y++) {
            double ys = (y - dstCenterY) / yScale + srcCenterY;
            for (int x = 0; x < dstWidth; x++) {
                double xs = (x - dstCenterX) / xScale + srcCenterX;
                int value = (int) (getBicubicInterpolatedPixel(img, xs, ys) + 0.5);
                if (value < 0) {
                    value = 0;
                }
                if (value > 65535) {
                    value = 65535;
                }
                raster.setSample(x, y, 0, value);
            }
        }
        return result;
    }

    private static BufferedImage resize32bppImage(BufferedImage img,
            int dstWidth, int dstHeight) {
        BufferedImage result = BufferedImageHelper.createBufferedImage(
                dstWidth, dstHeight, img);
        WritableRaster raster = result.getRaster();

        int width = img.getWidth();
        int height = img.getHeight();
        double srcCenterX = width / 2.0;
        double srcCenterY = height / 2.0;
        double xScale = dstWidth / (double) width;
        double yScale = dstHeight / (double) height;
        double dstCenterX = (dstWidth + xScale) / 2;
        double dstCenterY = (dstHeight + yScale) / 2;

        for (int y = 0; y < dstHeight; y++) {
            double ys = (y - dstCenterY) / yScale + srcCenterY;
            for (int x = 0; x < dstWidth; x++) {
                double xs = (x - dstCenterX) / xScale + srcCenterX;
                float value = (float) getBicubicInterpolatedPixel(img, xs, ys);
                raster.setSample(x, y, 0, value);
            }
        }
        return result;
    }

    /**
     * This method is from Chapter 16 of "Digital Image Processing: An
     * Algorithmic Introduction Using Java" by Burger and Burge
     * (http://www.imagingbook.com/).
     */
    private static double getBicubicInterpolatedPixel(BufferedImage image,
            double x0, double y0) {
        int width = image.getWidth();
        int height = image.getHeight();
        int u0 = (int) Math.floor(x0);	//use floor to handle negative coordinates too
        int v0 = (int) Math.floor(y0);
        if (u0 <= 0 || u0 >= width - 2 || v0 <= 0 || v0 >= height - 2) {
            return getBilinearInterpolatedPixel(image, x0, y0);
        }
        WritableRaster raster = image.getRaster();
        double q = 0;
        for (int j = 0; j <= 3; j++) {
            int v = v0 - 1 + j;
            double p = 0;
            for (int i = 0; i <= 3; i++) {
                int u = u0 - 1 + i;
                p = p + raster.getSampleDouble(u, v, 0) * cubic(x0 - u);
            }
            q = q + p * cubic(y0 - v);
        }
        return q;
    }

    private static double getBilinearInterpolatedPixel(BufferedImage image,
            double x, double y) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (x < -1 || x >= width || y < -1 || y >= height) {
            return 0;
        }
        if (width == 1 && height == 1) {
            return image.getRaster().getSampleDouble(0, 0, 0);
        }

        if (x < 0.0) {
            x = 0.0;
        }
        if (x >= width - 1.0) {
            x = width - 1.001;
        }
        if (y < 0.0) {
            y = 0.0;
        }
        if (y >= height - 1.0) {
            y = height - 1.001;
        }
        int xbase = (int) x;
        int ybase = (int) y;
        double xFraction = x - xbase;
        double yFraction = y - ybase;
        WritableRaster raster = image.getRaster();
        double upperAverage;
        double lowerAverage;
        if (height == 1) {
            double left = raster.getSampleDouble(xbase, ybase, 0);
            double right = raster.getSampleDouble(xbase + 1, ybase, 0);
            if (Double.isNaN(left) && xFraction >= 0.5) {
                return right;
            }
            if (Double.isNaN(right) && xFraction < 0.5) {
                return left;
            }
            return left + xFraction * (right - left);
        } else if (width == 1) {
            upperAverage = raster.getSampleDouble(xbase, ybase, 0);
            lowerAverage = raster.getSampleDouble(xbase, ybase + 1, 0);
        } else {
            double lowerLeft = raster.getSampleDouble(xbase, ybase, 0);
            double lowerRight = raster.getSampleDouble(xbase + 1, ybase, 0);
            double upperLeft = raster.getSampleDouble(xbase, ybase + 1, 0);
            double upperRight = raster.getSampleDouble(xbase + 1, ybase + 1, 0);
            if (Double.isNaN(upperLeft) && xFraction >= 0.5) {
                upperAverage = upperRight;
            } else if (Double.isNaN(upperRight) && xFraction < 0.5) {
                upperAverage = upperLeft;
            } else {
                upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
            }
            if (Double.isNaN(lowerLeft) && xFraction >= 0.5) {
                lowerAverage = lowerRight;
            } else if (Double.isNaN(lowerRight) && xFraction < 0.5) {
                lowerAverage = lowerLeft;
            } else {
                lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
            }
        }
        if (Double.isNaN(lowerAverage) && yFraction >= 0.5) {
            return upperAverage;
        }
        if (Double.isNaN(upperAverage) && yFraction < 0.5) {
            return lowerAverage;
        }
        return lowerAverage + yFraction * (upperAverage - lowerAverage);
    }

    private static double cubic(double x) {
        if (x < 0.0) {
            x = -x;
        }
        double z = 0.0;
        double a = 0.5;// Catmull-Rom interpolation
        if (x < 1.0) {
            z = x * x * (x * (-a + 2.0) + (a - 3.0)) + 1.0;
        } else if (x < 2.0) {
            z = -a * x * x * x + 5.0 * a * x * x - 8.0 * a * x + 4.0 * a;
        }
        return z;
    }
}
