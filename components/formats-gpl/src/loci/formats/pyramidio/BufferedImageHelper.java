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

import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 *
 * @author Antoine Vandecreme
 */
public class BufferedImageHelper {

    private BufferedImageHelper() {
    }

    /**
     * Create a new buffered image with the same characteristics (color model,
     * raster type, properties...) than the specified one.
     *
     * @param width the width
     * @param height the height
     * @param image an image with the same characteristics than the one which
     * will be created.
     * @return
     */
    public static BufferedImage createBufferedImage(int width, int height,
            BufferedImage image) {
        Hashtable<String, Object> properties = null;
        String[] propertyNames = image.getPropertyNames();
        if (propertyNames != null) {
            properties = new Hashtable<>(propertyNames.length);
            for (String propertyName : propertyNames) {
                properties.put(propertyName, image.getProperty(propertyName));
            }
        }
        return new BufferedImage(
                image.getColorModel(),
                image.getRaster().createCompatibleWritableRaster(width, height),
                image.isAlphaPremultiplied(),
                properties);
    }

}
