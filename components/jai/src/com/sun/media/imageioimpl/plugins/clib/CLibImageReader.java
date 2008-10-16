/*
 * $RCSfile: CLibImageReader.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.11 $
 * $Date: 2006/02/28 01:33:31 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.clib;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import com.sun.medialib.codec.jiio.Constants;
import com.sun.medialib.codec.jiio.mediaLibImage;

// XXX Need to verify compliance of all methods with ImageReader specificaiton.
public abstract class CLibImageReader extends ImageReader {
    // The current image index.
    private int currIndex = -1;

    // The position of the byte after the last byte read so far.
    private long highWaterMark = Long.MIN_VALUE;

    // An <code>ArrayList</code> of <code>Long</code>s indicating the stream
    // positions of the start of each image. Entries are added as needed.
    private ArrayList imageStartPosition = new ArrayList();

    // The number of images in the stream, if known, otherwise -1.
    private int numImages = -1;

    // The image returned by the codecLib Decoder.
    private mediaLibImage mlibImage = null;

    // The index of the cached image.
    private int mlibImageIndex = -1;

    /**
     * Returns true if and only if both arguments are null or
     * both are non-null and have the same length and content.
     */
    private static boolean subBandsMatch(int[] sourceBands,
                                         int[] destinationBands) {
        if(sourceBands == null && destinationBands == null) {
            return true;
        } else if(sourceBands != null && destinationBands != null) {
            if (sourceBands.length != destinationBands.length) {
                // Shouldn't happen ...
                return false;
            }
            for (int i = 0; i < sourceBands.length; i++) {
                if (sourceBands[i] != destinationBands[i]) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Creates a <code>ImageTypeSpecifier</code> corresponding to a
     * <code>mediaLibImage</code>.  The <code>mediaLibImage</code> is
     * assumed always to be either bilevel-packed (MLIB_BIT) or
     * pixel interleaved in the order ((G|I)|RGB)[A] where 'I' indicates
     * an index as for palette images.
     */
    protected static final ImageTypeSpecifier
        createImageType(mediaLibImage mlImage,
                        ColorSpace colorSpace,
                        int bitDepth,
                        byte[] redPalette,
                        byte[] greenPalette,
                        byte[] bluePalette,
                        byte[] alphaPalette) throws IOException {

        // Get the mediaLibImage attributes.
        int mlibType = mlImage.getType();
        int mlibWidth = mlImage.getWidth();
        int mlibHeight = mlImage.getHeight();
        int mlibBands = mlImage.getChannels();
        int mlibStride = mlImage.getStride();

        // Convert mediaLib type to Java2D type.
        int dataType;
        switch(mlibType) {
        case Constants.MLIB_BIT:
        case Constants.MLIB_BYTE:
            dataType = DataBuffer.TYPE_BYTE;
            break;
        case Constants.MLIB_SHORT:
        case Constants.MLIB_USHORT:
            // Deliberately cast MLIB_SHORT to TYPE_USHORT.
            dataType = DataBuffer.TYPE_USHORT;
            break;
        default:
            throw new UnsupportedOperationException
                (I18N.getString("Generic0")+" "+mlibType);
        }

        // Set up the SampleModel.
        SampleModel sampleModel = null;
        if(mlibType == Constants.MLIB_BIT) {
            // Bilevel-packed
            sampleModel =
                new MultiPixelPackedSampleModel(dataType,
                                                mlibWidth,
                                                mlibHeight,
                                                1,
                                                mlibStride,
                                                mlImage.getBitOffset());
        } else {
            // Otherwise has to be interleaved in the order ((G|I)|RGB)[A].
            int[] bandOffsets = new int[mlibBands];
            for(int i = 0; i < mlibBands; i++) {
                bandOffsets[i] = i;
            }

            sampleModel =
                new PixelInterleavedSampleModel(dataType,
                                                mlibWidth,
                                                mlibHeight,
                                                mlibBands,
                                                mlibStride,
                                                bandOffsets);
        }

        // Set up the ColorModel.
        ColorModel colorModel = null;
        if(mlibBands == 1 &&
           redPalette   != null &&
           greenPalette != null &&
           bluePalette  != null &&
           redPalette.length == greenPalette.length &&
           redPalette.length == bluePalette.length) {

            // Indexed image.
            int paletteLength = redPalette.length;
            if(alphaPalette != null) {
                if(alphaPalette.length != paletteLength) {
                    byte[] alphaTmp = new byte[paletteLength];
                    if(alphaPalette.length > paletteLength) {
                        System.arraycopy(alphaPalette, 0,
                                         alphaTmp, 0, paletteLength);
                    } else { // alphaPalette.length < paletteLength
                        System.arraycopy(alphaPalette, 0,
                                         alphaTmp, 0, alphaPalette.length);
                        for(int i = alphaPalette.length; i < paletteLength; i++) {
                            alphaTmp[i] = (byte)255; // Opaque.
                        }
                    }
                    alphaPalette = alphaTmp;
                }

                colorModel = new IndexColorModel(bitDepth, //XXX 8
                                                 paletteLength,
                                                 redPalette,
                                                 greenPalette,
                                                 bluePalette,
                                                 alphaPalette);
            } else {
                colorModel = new IndexColorModel(bitDepth, //XXX 8
                                                 paletteLength,
                                                 redPalette,
                                                 greenPalette,
                                                 bluePalette);
            }
        } else if(mlibType == Constants.MLIB_BIT) {
            // Bilevel image with no palette: assume black-is-zero.
            byte[] cmap = new byte[] { (byte)0x00, (byte)0xFF };
            colorModel = new IndexColorModel(1, 2, cmap, cmap, cmap);
        } else {
            // Set the color space and the alpha flag.
            ColorSpace cs;
            boolean hasAlpha;
            if(colorSpace != null &&
               (colorSpace.getNumComponents() == mlibBands ||
                colorSpace.getNumComponents() == mlibBands - 1)) {
                // Use the provided ColorSpace.
                cs = colorSpace;

                // Set alpha if numBands == numColorComponents + 1.
                hasAlpha = colorSpace.getNumComponents() != mlibBands;
            } else {
                // RGB if more than 2 bands.
                cs = ColorSpace.getInstance(mlibBands < 3 ?
                                            ColorSpace.CS_GRAY :
                                            ColorSpace.CS_sRGB);

                // Alpha if band count is even.
                hasAlpha = mlibBands % 2 == 0;
            }

            // All bands have same depth.
            int[] bits = new int[mlibBands];
            for(int i = 0; i < mlibBands; i++) {
                bits[i] = bitDepth;
            }

            colorModel =
                new ComponentColorModel(cs,
                                        bits,
                                        hasAlpha,
                                        false,
                                        hasAlpha ?
                                        Transparency.TRANSLUCENT :
                                        Transparency.OPAQUE,
                                        dataType);
        }

        return new ImageTypeSpecifier(colorModel, sampleModel);
    }

    private static final void subsample(Raster src, int subX, int subY,
                                        WritableRaster dst) {
        int sx0 = src.getMinX();
        int sy0 = src.getMinY();
        int sw = src.getWidth();
        int syUB = sy0 + src.getHeight();

        int dx0 = dst.getMinX();
        int dy0 = dst.getMinY();
        int dw = dst.getWidth();

        int b = src.getSampleModel().getNumBands();
        int t = src.getSampleModel().getDataType();

        int numSubSamples = (sw + subX - 1)/subX;

        if(t == DataBuffer.TYPE_FLOAT || t == DataBuffer.TYPE_DOUBLE) {
            float[] fsamples = new float[sw];
            float[] fsubsamples = new float[numSubSamples];

            for(int k = 0; k < b; k++) {
                for(int sy = sy0, dy = dy0; sy < syUB; sy += subY, dy++) {
                    src.getSamples(sx0, sy, sw, 1, k, fsamples);
                    for(int i = 0, s = 0; i < sw; s++, i += subX) {
                        fsubsamples[s] = fsamples[i];
                    }
                    dst.setSamples(dx0, dy, dw, 1, k, fsubsamples);
                }
            }
        } else {
            int[] samples = new int[sw];
            int[] subsamples = new int[numSubSamples];

            for(int k = 0; k < b; k++) {
                for(int sy = sy0, dy = dy0; sy < syUB; sy += subY, dy++) {
                    src.getSamples(sx0, sy, sw, 1, k, samples);
                    for(int i = 0, s = 0; i < sw; s++, i += subX) {
                        subsamples[s] = samples[i];
                    }
                    dst.setSamples(dx0, dy, dw, 1, k, subsamples);
                }
            }
        }
    }                                 

    protected CLibImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    /**
     * An <code>Iterator</code> over a single element.
     */
    private class SoloIterator implements Iterator {
        Object theObject;

        SoloIterator(Object o) {
            if(o == null) {
                new IllegalArgumentException
                    (I18N.getString("CLibImageReader0"));
            }
            theObject = o;
        }

        public boolean hasNext() {
            return theObject != null;
        }

        public Object next() {
            if(theObject == null) {
                throw new NoSuchElementException();
            }
            Object theNextObject = theObject;
            theObject = null;
            return theNextObject;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // Stores the location of the image at the specified index in the
    // imageStartPosition List.
    private int locateImage(int imageIndex) throws IIOException {
        if (imageIndex < 0) {
            throw new IndexOutOfBoundsException("imageIndex < 0!");
        }

        try {
            // Find closest known index (which can be -1 if none read before).
            int index = Math.min(imageIndex, imageStartPosition.size() - 1);

            ImageInputStream stream = (ImageInputStream)input;

            // Seek unless at beginning of stream 
            if(index >= 0) { // index == -1
                if(index == imageIndex) {
                    // Seek to previously identified position and return.
                    Long l = (Long)imageStartPosition.get(index);
                    stream.seek(l.longValue());
                    return imageIndex;
                } else if(highWaterMark >= 0) {
                    // index >= imageStartPosition.size()
                    // Seek to first unread byte.
                    stream.seek(highWaterMark);
                }
            }

            // Get the reader SPI.
            ImageReaderSpi provider = getOriginatingProvider();

            // Search images until at desired index or last image found.
            do {
                try {
                    if(provider.canDecodeInput(stream)) {
                        // Append the image position.
                        long offset = stream.getStreamPosition();
                        imageStartPosition.add(new Long(offset));
                    } else {
                        return index;
                    }
                } catch(IOException e) {
                    // Ignore it.
                    return index;
                } 

                // Incrememt the index.
                if(++index == imageIndex) break;

                // Skip the image.
                if(!skipImage()) return index - 1;
            } while(true);
        } catch (IOException e) {
            throw new IIOException("IOException", e);
        }

        currIndex = imageIndex;

        return imageIndex;
    }

    // Verify that imageIndex is in bounds and find the image position.
    protected void seekToImage(int imageIndex) throws IIOException {
        // Check lower bound.
        if (imageIndex < minIndex) {
            throw new IndexOutOfBoundsException("imageIndex < minIndex!");
        }

        // Update lower bound if cannot seek back.
        if (seekForwardOnly) {
            minIndex = imageIndex;
        }

        // Locate the image.
        int index = locateImage(imageIndex);

        // If the located is not the one sought => exception.
        if (index != imageIndex) {
            throw new IndexOutOfBoundsException("imageIndex out of bounds!");
        }
    }

    /**
     * Skip the current image. If possible subclasses should override
     * this method with a more efficient implementation.
     *
     * @return Whether the image was successfully skipped.
     */
    protected boolean skipImage() throws IOException {
        boolean retval = false;

        if(input == null) {
            throw new IllegalStateException("input == null");
        }
        InputStream stream = null;
        if(input instanceof ImageInputStream) {
            stream = new InputStreamAdapter((ImageInputStream)input);
        } else {
            throw new IllegalArgumentException
                ("!(input instanceof ImageInputStream)");
        }

        retval = decode(stream) != null;

        if(retval) {
            long pos = ((ImageInputStream)input).getStreamPosition();
            if(pos > highWaterMark) {
                highWaterMark = pos;
            }
        }

        return retval;
    }

    /**
     * Decodes an image from the supplied <code>InputStream</code>.
     */
    protected abstract mediaLibImage decode(InputStream stream)
        throws IOException;

    /**
     * Returns the value of the private <code>mlibImage</code> instance
     * variable initializing it first if it is <code>null</code>.
     */
    protected synchronized mediaLibImage getImage(int imageIndex)
        throws IOException {
        if(mlibImage == null || imageIndex != mlibImageIndex) {
            if(input == null) {
                throw new IllegalStateException("input == null");
            }
            seekToImage(imageIndex);
            InputStream stream = null;
            if(input instanceof ImageInputStream) {
                stream = new InputStreamAdapter((ImageInputStream)input);
            } else {
                throw new IllegalArgumentException
                    ("!(input instanceof ImageInputStream)");
            }
            mlibImage = decode(stream);
            if(mlibImage != null) {
                mlibImageIndex = imageIndex;
                long pos = ((ImageInputStream)input).getStreamPosition();
                if(pos > highWaterMark) {
                    highWaterMark = pos;
                }
            } else { // mlibImage == null
                mlibImageIndex = -1;
            }
        }
        return mlibImage;
    }

    /**
     * Returns the index of the image cached in the private
     * <code>mlibImage</code> instance variable or -1 if no
     * image is currently cached.
     */
    protected int getImageIndex() {
        return mlibImageIndex;
    }

    public int getNumImages(boolean allowSearch) throws IOException {
        if (input == null) {
            throw new IllegalStateException("input == null");
        }
        if (seekForwardOnly && allowSearch) {
            throw new IllegalStateException
                ("seekForwardOnly && allowSearch!");
        }

        if (numImages > 0) {
            return numImages;
        }
        if (allowSearch) {
            this.numImages = locateImage(Integer.MAX_VALUE) + 1;
        }
        return numImages;
    }

    public int getWidth(int imageIndex) throws IOException {
        seekToImage(imageIndex);

        return getImage(imageIndex).getWidth();
    }

    public int getHeight(int imageIndex) throws IOException {
        seekToImage(imageIndex);

        return getImage(imageIndex).getHeight();
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        seekToImage(imageIndex);

        return null;
    }

    public synchronized BufferedImage read(int imageIndex,
                                           ImageReadParam param)
        throws IOException {

        processImageStarted(imageIndex);

        seekToImage(imageIndex);

        processImageProgress(0.0F);
        processImageProgress(0.5F);

        ImageTypeSpecifier rawImageType = getRawImageType(imageIndex);

        processImageProgress(0.95F);

        mediaLibImage mlImage = getImage(imageIndex);
        int dataOffset = mlImage.getOffset();

        SampleModel rawSampleModel = rawImageType.getSampleModel();

        DataBuffer db;
        int smType = rawSampleModel.getDataType();
        switch(smType) {
        case DataBuffer.TYPE_BYTE:
            byte[] byteData = mlImage.getType() == mediaLibImage.MLIB_BIT ?
                mlImage.getBitData() : mlImage.getByteData();
            db = new DataBufferByte(byteData,
                                    byteData.length - dataOffset,
                                    dataOffset);
            break;
        case DataBuffer.TYPE_USHORT:
            // Deliberately cast MLIB_SHORT to TYPE_USHORT.
            short[] shortData = mlImage.getShortData();
            if(shortData == null) {
                shortData = mlImage.getUShortData();
            }
            db = new DataBufferUShort(shortData,
                                      shortData.length - dataOffset,
                                      dataOffset);
            break;
        default:
            throw new UnsupportedOperationException
                (I18N.getString("Generic0")+" "+smType);
        }

        WritableRaster rawRaster =
            Raster.createWritableRaster(rawSampleModel, db, null);

        ColorModel rawColorModel = rawImageType.getColorModel();

        BufferedImage image =
            new BufferedImage(rawColorModel,
                              rawRaster,
                              rawColorModel.isAlphaPremultiplied(),
                              null); // XXX getDestination()?

        Rectangle destRegion = new Rectangle(image.getWidth(),
                                             image.getHeight());
        int[] destinationBands = null;
        int subX = 1;
        int subY = 1;

        if(param != null) {
            BufferedImage destination = param.getDestination();
            destinationBands = param.getDestinationBands();
            Point destinationOffset = param.getDestinationOffset();
            int[] sourceBands = param.getSourceBands();
            Rectangle sourceRegion = param.getSourceRegion();
            subX = param.getSourceXSubsampling();
            subY = param.getSourceYSubsampling();

            boolean isNominal =
                destination == null &&
                destinationBands == null &
                destinationOffset.x == 0 && destinationOffset.y == 0 &&
                sourceBands == null &&
                sourceRegion == null &&
                subX == 1 && subY == 1;

            if(!isNominal) {
                int srcWidth = image.getWidth();
                int srcHeight = image.getHeight();

                if(destination == null) {
                    destination = getDestination(param,
                                                 getImageTypes(imageIndex),
                                                 srcWidth,
                                                 srcHeight);
                }

                checkReadParamBandSettings(param,
                                           image.getSampleModel().getNumBands(),
                                           destination.getSampleModel().getNumBands());

                Rectangle srcRegion = new Rectangle();
                computeRegions(param, srcWidth, srcHeight, destination,
                               srcRegion, destRegion);

                WritableRaster dst =
                    destination.getWritableTile(0, 0).createWritableChild(
                        destRegion.x, destRegion.y,
                        destRegion.width, destRegion.height,
                        destRegion.x, destRegion.y,
                        destinationBands);

                if(subX != 1 || subY != 1) { // Subsampling
                    WritableRaster src =
                        image.getWritableTile(0, 0).createWritableChild(
                                srcRegion.x, srcRegion.y,
                                srcRegion.width, srcRegion.height,
                                srcRegion.x, srcRegion.y,
                                sourceBands);
                    subsample(src, subX, subY, dst);
                } else { // No subsampling
                    WritableRaster src =
                        image.getWritableTile(0, 0).createWritableChild(
                            srcRegion.x, srcRegion.y,
                            srcRegion.width, srcRegion.height,
                            destRegion.x, destRegion.y,
                            sourceBands);
                    dst.setRect(src);
                }

                image = destination;
            } else if(param.getDestinationType() != null) {
                // Check for image type other than raw image type.
                ImageTypeSpecifier destImageType = param.getDestinationType();
                ColorSpace rawColorSpace = rawColorModel.getColorSpace();
                ColorSpace destColorSpace =
                    destImageType.getColorModel().getColorSpace();
                if(!destColorSpace.equals(rawColorSpace) ||
                   !destImageType.equals(rawImageType)) {
                    // Look for destination type in legal types list.
                    Iterator imageTypes = getImageTypes(imageIndex);
                    boolean isLegalType = false;
                    while(imageTypes.hasNext()) {
                        ImageTypeSpecifier imageType =
                            (ImageTypeSpecifier)imageTypes.next();
                        if(imageType.equals(destImageType)) {
                            isLegalType = true;
                            break;
                        }
                    }

                    if(isLegalType) {
                        // Set the destination raster.
                        WritableRaster raster;
                        if(rawSampleModel.equals(destImageType.getSampleModel())) {
                            // Re-use the raw raster.
                            raster = rawRaster;
                        } else {
                            // Create a new raster and copy the data.
                            SampleModel sm = destImageType.getSampleModel();
                            raster = Raster.createWritableRaster(sm, null);
                            raster.setRect(rawRaster);
                        }

                        // Replace the output image.
                        ColorModel cm = destImageType.getColorModel();
                        image = new BufferedImage(cm,
                                                  raster,
                                                  cm.isAlphaPremultiplied(),
                                                  null);
                    }
                }
            }
        }

        processImageUpdate(image,
                           destRegion.x, destRegion.y,
                           destRegion.width, destRegion.height,
                           subX, subY, destinationBands);

        processImageProgress(1.0F);
        processImageComplete();

        return image;
    }

    public void reset() {
        resetLocal();
        super.reset();
    }

    protected void resetLocal() {
        currIndex = -1;
        highWaterMark = Long.MIN_VALUE;
        imageStartPosition.clear();
        numImages = -1;
        mlibImage = null;
        mlibImageIndex = -1;
    }

    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        if (input != null) {
            // Check the class type.
            if (!(input instanceof ImageInputStream)) {
                throw new IllegalArgumentException
                    ("!(input instanceof ImageInputStream)");
            }
        }
        resetLocal();
    }
}
