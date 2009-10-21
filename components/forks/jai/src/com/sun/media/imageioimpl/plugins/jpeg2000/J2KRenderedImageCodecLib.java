/*
 * $RCSfile: J2KRenderedImageCodecLib.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2006/10/03 23:40:14 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.sun.medialib.codec.jp2k.CompParams;
import com.sun.medialib.codec.jp2k.Constants;
import com.sun.medialib.codec.jp2k.Decoder;
import com.sun.medialib.codec.jp2k.Size;
import com.sun.medialib.codec.jiio.mediaLibImage;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam;
import com.sun.media.imageioimpl.common.SimpleRenderedImage;
import com.sun.media.imageioimpl.common.ImageUtil;

// XXX Overall documentation

public class J2KRenderedImageCodecLib extends SimpleRenderedImage {
    /** The sample model for the original image. */
    private SampleModel originalSampleModel;

    private Raster currentTile;
    private Point currentTileGrid;
    private J2KMetadata metadata;

    /** The input stream we read from */
    private ImageInputStream iis = null;

    /** Caches the <code>J2KImageReader</code> which creates this object.  This
     *  variable is used to monitor the abortion.
     */
    private J2KImageReaderCodecLib reader;

    /** The <code>J2KImageReadParam</code> to create this
     *  <code>renderedImage</code>.
     */
    private J2KImageReadParam param = null;

    /** Caches the medialib decoder. */
    private Decoder decoder;
    private Size size;
    private CompParams compParam;
    private int xStep, yStep; // JPEG 2000 internal subsampling parameters

    /** The destination bounds. */
    Rectangle destinationRegion;
    Rectangle originalRegion;
    Point sourceOrigin;

    /** The subsampling parameters. */
    private int scaleX, scaleY, xOffset, yOffset;
    private int[] destinationBands = null;
    private int[] sourceBands = null;
    private int nComp;
    private int[] channelMap;

    /** Coordinate transform is not needed from the source (image stream)
     *  to the destination.
     */
    private boolean noTransform = true;

    /** The raster for medialib tiles to share. */
    private WritableRaster rasForATile;

    private BufferedImage destImage;

    public J2KRenderedImageCodecLib(ImageInputStream iis,
                                    J2KImageReaderCodecLib reader,
                                    ImageReadParam param) throws IOException {
        this.iis = iis;
        this.reader = reader;

        // Ensure the ImageReadParam is a J2KImageReadParam
        boolean allowZeroDestOffset = true;
        if(param == null) {
            // Use the default
            param = (J2KImageReadParam)reader.getDefaultReadParam();
            allowZeroDestOffset = false;
        } else if(!(param instanceof J2KImageReadParam)) {
            // Create a new one
            param = new J2KImageReadParamJava(param);
            allowZeroDestOffset = false;
        }
        this.param = (J2KImageReadParam)param;

        decoder = new Decoder(iis);

        decoder.setMode(Constants.JP2K_COMPOSITE_TILE);

        //set resolution before any calling of any calling for decode/decodeSize
        int resolution = ((J2KImageReadParam)param).getResolution();
        if (resolution != -1)
            decoder.setMaxLevels(resolution);

        size = decoder.decodeSize(null);

        compParam = new CompParams();
        for (int i = 0; i < size.csize; i++) {
            decoder.decodeCompParams(compParam, i);
            if(i == 0) {
                xStep = compParam.xstep;
                yStep = compParam.ystep;
            } else if(compParam.xstep != xStep || compParam.ystep != yStep) {
                // All components must have same subsampling along each axis.
                throw new IIOException
                    ("All components must have the same subsampling factors!");
            }
        }

        // Set source sub-banding.
        sourceBands = param.getSourceBands();
        if (sourceBands == null) {
            nComp = size.csize;
            sourceBands = new int[nComp];
            for (int i = 0; i < nComp; i++)
                sourceBands[i] = i;
        } else {
            for(int i = 0; i < sourceBands.length; i++) {
                if(sourceBands[i] < 0 ||
                   sourceBands[i] >= size.csize) {
                    throw new IIOException
                        ("Source band out of range!");
                }
            }
        }

        // Cache number of components.
        nComp = sourceBands.length;

        // Set destination sub-banding.
        destinationBands = param.getDestinationBands();
        if (destinationBands == null) {
            destinationBands = new int[nComp];
            for (int i = 0; i < nComp; i++)
                destinationBands[i] = i;
        } else {
            for(int i = 0; i < destinationBands.length; i++) {
                if(destinationBands[i] < 0 ||
                   destinationBands[i] >= size.csize) {
                    throw new IIOException
                        ("Destination band out of range!");
                }
            }
        }

        // Check number of source and dest bands.
        if(destinationBands.length != sourceBands.length) {
                throw new IIOException
                    ("Number of source and destination bands must be equal!");
        }

        this.width = (size.xosize + size.xsize + xStep - 1)/xStep;
        this.height = (size.yosize + size.ysize + yStep - 1)/yStep;

        Rectangle sourceRegion =
                new Rectangle(0, 0, this.width, this.height);

        originalRegion = (Rectangle)sourceRegion.clone();

        destinationRegion = (Rectangle)sourceRegion.clone();

        J2KImageReader.computeRegionsWrapper(param,
                                             allowZeroDestOffset,
                                             this.width, this.height,
                                             param.getDestination(),
                                             sourceRegion,
                                             destinationRegion);
        scaleX = param.getSourceXSubsampling();
        scaleY = param.getSourceYSubsampling();
        xOffset = param.getSubsamplingXOffset();
        yOffset = param.getSubsamplingYOffset();

        sourceOrigin = new Point(sourceRegion.x, sourceRegion.y);
        if (!destinationRegion.equals(originalRegion))
            noTransform = false;

        this.tileWidth = (size.xtsize + xStep - 1)/xStep;
        this.tileHeight = (size.ytsize + yStep - 1)/yStep;
        this.tileGridXOffset =
            (size.xtosize + xStep - 1)/xStep - (size.xosize + xStep - 1)/xStep;
        this.tileGridYOffset =
            (size.ytosize + yStep - 1)/yStep - (size.yosize + yStep - 1)/yStep;

        this.width = destinationRegion.width;
        this.height = destinationRegion.height;
        this.minX = destinationRegion.x;
        this.minY = destinationRegion.y;

        originalSampleModel = createOriginalSampleModel();
        sampleModel = createSampleModel();
        colorModel = createColorModel();
        tileGridXOffset +=
          (XToTileX(minX) - XToTileX(tileGridXOffset)) * tileWidth;
        tileGridYOffset +=
          (YToTileY(minY) - YToTileY(tileGridYOffset)) * tileHeight;

        // sets the resolution and decoding rate to the medialib decoder
        // Java decoding rate is in bit-per-pixel; the medialib rate is in
        // percentage; so convert first.
        double rate = ((J2KImageReadParam)param).getDecodingRate();
        if (rate != Double.MAX_VALUE) {
            // XXX Obtain bits per sample from elsewhere, e.g., ColorModel.
            rate /= ImageUtil.getElementSize(sampleModel);
            decoder.setRate(rate, 0);
        }
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if (currentTile != null &&
            currentTileGrid.x == tileX &&
            currentTileGrid.y == tileY)
            return currentTile;

        if (tileX < getMinTileX() || tileY < getMinTileY() ||
            tileX > getMaxTileX() || tileY > getMaxTileY())
            throw new IllegalArgumentException(I18N.getString("J2KReadState1"));

        int x = tileXToX(tileX);
        int y = tileYToY(tileY);
        currentTile = Raster.createWritableRaster(sampleModel,
                                                  new Point(x, y));

        try {
            readAsRaster((WritableRaster)currentTile);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }

        if (currentTileGrid == null)
            currentTileGrid = new Point(tileX, tileY);
        else {
            currentTileGrid.x = tileX;
            currentTileGrid.y = tileY;
        }

        return currentTile;
    }

    synchronized WritableRaster readAsRaster(WritableRaster raster)
        throws IOException {
        int x = raster.getMinX();
        int y = raster.getMinY();

        try {
            if (noTransform) {
                int E2c = (size.xosize + xStep - 1)/xStep;
                int E1c = (size.yosize + yStep - 1)/yStep;

                int tXStart =
                    ((x + E2c)*xStep - size.xtosize)/size.xtsize;
                int tXEnd =
                    ((x + raster.getWidth() - 1 + E2c)*xStep - size.xtosize)/
                    size.xtsize;
                int tYStart =
                    ((y + E2c)*yStep - size.ytosize)/size.ytsize;
                int tYEnd =
                    ((y + raster.getHeight() - 1 + E2c)*yStep - size.ytosize)/
                    size.ytsize;

                int sourceFormatTag =
                    MediaLibAccessor.findCompatibleTag(raster);

                if(tXStart == tXEnd && tYStart == tYEnd) {
                    MediaLibAccessor accessor =
                        new MediaLibAccessor(raster,
                                             raster.getBounds().intersection(originalRegion),
                                             sourceFormatTag, true);

                    mediaLibImage[] mlImage = accessor.getMediaLibImages();

                    //this image may be a subregion of the image in the stream
                    // So use the original tile number.
                    int tileNo = tXStart + tYStart*size.nxtiles;
                    decoder.decode(mlImage, tileNo);
                    accessor.copyDataToRaster(channelMap);
                } else {
                    for(int ty = tYStart; ty <= tYEnd; ty++) {
                        for(int tx = tXStart; tx <= tXEnd; tx++) {
                            int sx = (size.xtosize + tx*size.xtsize +
                                      xStep - 1)/xStep - E2c;
                            int sy = (size.ytosize + ty*size.ytsize +
                                      yStep - 1)/yStep - E1c;
                            int ex = (size.xtosize + (tx + 1)*size.xtsize +
                                      xStep - 1)/xStep - E2c;
                            int ey = (size.ytosize + (ty + 1)*size.ytsize +
                                      yStep - 1)/yStep - E1c;
                            Rectangle subRect =
                                new Rectangle(sx, sy, ex - sx, ey - sy);
                            if(subRect.isEmpty()) {
                                continue;
                            }
                            if (rasForATile == null) {
                                rasForATile =
                                    Raster.createWritableRaster
                                    (originalSampleModel, null);
                            }
                            WritableRaster subRaster =
                                rasForATile.createWritableChild
                                (rasForATile.getMinX(),
                                 rasForATile.getMinY(),
                                 subRect.width, subRect.height,
                                 subRect.x, subRect.y, null);
                            MediaLibAccessor accessor =
                                new MediaLibAccessor(subRaster,
                                                     subRect,
                                                     sourceFormatTag, true);

                            mediaLibImage[] mlImage =
                                accessor.getMediaLibImages();

                            int tileNo = tx + ty*size.nxtiles;
                            decoder.decode(mlImage, tileNo);
                            accessor.copyDataToRaster(channelMap);

                            Rectangle rasBounds = raster.getBounds();
                            Rectangle childRect =
                                rasBounds.intersection(subRect);
                            if(childRect.isEmpty()) {
                                continue;
                            }

                            Raster childRaster =
                                subRaster.createChild(childRect.x, childRect.y,
                                                      childRect.width,
                                                      childRect.height,
                                                      childRect.x, childRect.y,
                                                      null);
                            ((WritableRaster)raster).setRect(childRaster);
                        }
                    }
                }
            } else {
                readSubsampledRaster(raster);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return raster;
    }

    private void readSubsampledRaster(WritableRaster raster)
        throws IOException {

        int numBands = sourceBands.length;

        Rectangle destRect = raster.getBounds().intersection(destinationRegion);

        int offx = destinationRegion.x;
        int offy = destinationRegion.y;

        int sourceSX = (destRect.x - offx) * scaleX + sourceOrigin.x;
        int sourceSY = (destRect.y - offy) * scaleY + sourceOrigin.y;
        int sourceEX = (destRect.width - 1) * scaleX + sourceSX;
        int sourceEY = (destRect.height - 1)* scaleY + sourceSY;

        int E2c = (size.xosize + xStep - 1)/xStep;
        int E1c = (size.yosize + yStep - 1)/yStep;

        int startXTile =
            ((sourceSX + E2c)*xStep - size.xtosize)/size.xtsize;
        int endXTile =
            ((sourceEX + E2c)*xStep - size.xtosize)/size.xtsize;
        int startYTile =
            ((sourceSY + E1c)*yStep - size.ytosize)/size.ytsize;
        int endYTile =
            ((sourceEY + E1c)*yStep - size.ytosize)/size.ytsize;

        startXTile = clip(startXTile, 0,  size.nxtiles - 1);
        startYTile = clip(startYTile, 0, size.nytiles - 1);
        endXTile = clip(endXTile, 0,  size.nxtiles - 1);
        endYTile = clip(endYTile, 0, size.nytiles - 1);

        int totalXTiles = endXTile - startXTile + 1;
        int totalYTiles = endYTile - startYTile + 1;
        int totalTiles = totalXTiles * totalYTiles;

        int[] pixbuf = null;  // integer buffer for the decoded pixels.

        // Start the data delivery to the cached consumers tile by tile
        for(int y=startYTile; y <= endYTile; y++){
            if (reader.getAbortRequest())
                break;

            // Loop on horizontal tiles
            for(int x=startXTile; x <= endXTile; x++){
                if (reader.getAbortRequest())
                    break;

                float percentage = // XXX Incorrect?
                    (x - startXTile + 1.0F + y * totalXTiles) / totalTiles;

                int startX =
                    (x * size.xtsize + size.xtosize + xStep - 1)/xStep - E2c;
                int startY =
                    (y * size.ytsize + size.ytosize + yStep - 1)/yStep - E1c;
                int endX =
                    ((x + 1)*size.xtsize + size.xtosize + xStep - 1)/
                    xStep - E2c;
                int endY =
                    ((y + 1)*size.ytsize + size.ytosize + yStep - 1)/
                    yStep - E1c;

                if (rasForATile == null) {
                    rasForATile =
                        Raster.createWritableRaster(originalSampleModel,
                                                    new Point(startX, startY));
                } else {
                    rasForATile =
                        rasForATile.createWritableTranslatedChild(startX, startY);
                }

                int tw = endX - startX;
                int th = endY - startY;
                WritableRaster targetRas;
                if(tw != tileWidth || th != tileHeight) {
                    targetRas = rasForATile.createWritableChild
                        (startX, startY, tw, th, startX, startY, null);
                } else {
                    targetRas = rasForATile;
                }

                int sourceFormatTag =
                    MediaLibAccessor.findCompatibleTag(targetRas);

                MediaLibAccessor accessor =
                    new MediaLibAccessor(targetRas,
//                                         targetRas.getBounds(),
                                         targetRas.getBounds().intersection(originalRegion),
                                         sourceFormatTag, true);

                mediaLibImage[] mlImage = accessor.getMediaLibImages();
                decoder.decode(mlImage, x + y * size.nxtiles);
                accessor.copyDataToRaster(channelMap);

                int cTileHeight = th;
                int cTileWidth = tw;

                if (startY + cTileHeight >= originalRegion.height)
                    cTileHeight = originalRegion.height - startY;

                if (startX + cTileWidth >= originalRegion.width)
                    cTileWidth = originalRegion.width - startX;

                int tx = startX;
                int ty = startY;

                if (sourceSX > startX) {
                    cTileWidth += startX - sourceSX;
                    tx = sourceSX;
                    startX = sourceSX;
                }

                if (sourceSY > startY) {
                    cTileHeight += startY - sourceSY;
                    ty = sourceSY;
                    startY = sourceSY;
                }

                if (sourceEX < startX + cTileWidth - 1) {
                    cTileWidth += sourceEX - startX - cTileWidth + 1;
                }

                if (sourceEY < startY + cTileHeight - 1) {
                    cTileHeight += sourceEY - startY - cTileHeight + 1;
                }

                // The start X in the destination
                int x1 = (startX + scaleX - 1 - sourceOrigin.x) / scaleX;
                int x2 = (startX + scaleX -1 + cTileWidth - sourceOrigin.x) / scaleX;
                int lineLength = x2 - x1;
                // Suppress further processing if lineLength is non-positive
                // XXX (which it should never be).
                if(lineLength <= 0) continue;
                x2 = (x2 - 1) * scaleX + sourceOrigin.x;

                int y1 = (startY + scaleY -1 - sourceOrigin.y) /scaleY;
                startY = y1 * scaleY + sourceOrigin.y;
                startX = x1 * scaleX + sourceOrigin.x;

                x1 += offx;
                y1 += offy;

                if (pixbuf == null || pixbuf.length < lineLength)
                    pixbuf = new int[lineLength]; // line buffer for pixel data

                // Deliver in lines to reduce memory usage
                for (int l = startY, m = y1; l < ty + cTileHeight; l += scaleY, m++) {
                    if (reader.getAbortRequest())
                        break;

                    // Request line data
                    for (int i = 0; i < numBands; i++) {
                        for (int j = lineLength - 1, k1 = x2; j >= 0; j--, k1-=scaleX) {
                            pixbuf[j] = targetRas.getSample(k1, l, i);
                        }

                        // Send the line data to the BufferedImage
                        raster.setSamples(x1, m, lineLength, 1, destinationBands[i], pixbuf);
                    }

                    if (destImage != null)
                        reader.processImageUpdateWrapper(destImage, x1, m,
                                                         cTileWidth, 1, 1, 1,
                                                         destinationBands);

                    reader.processImageProgressWrapper(percentage +
                                                (l - startY + 1.0F) /
                                                cTileHeight / totalTiles);
		}
            } // End loop on horizontal tiles
        } // End loop on vertical tiles
    }

    public void setDestImage(BufferedImage image) {
        destImage = image;
    }

    public void clearDestImage() {
        destImage = null;
    }

    private int getTileNum(int x, int y) {
        int num = (y - getMinTileY()) * getNumXTiles() + x - getMinTileX();

        if (num < 0 || num >= getNumXTiles() * getNumYTiles())
            throw new IllegalArgumentException(I18N.getString("J2KReadState1"));

        return num;
    }

    private int clip(int value, int min, int max) {
        if (value < min)
            value = min;
        if (value > max)
            value = max;
        return value;
    }

    private SampleModel createSampleModel() throws IOException {
        if (sampleModel != null)
            return sampleModel;

        if (metadata == null)
            readImageMetadata();

        HeaderBox header = (HeaderBox)metadata.getElement("JPEG2000HeaderBox");
        int maxDepth = 0;
        boolean isSigned = false;
        if (header != null) {
            maxDepth = header.getBitDepth();
            isSigned = (maxDepth & 0x80) > 0;
            maxDepth = (maxDepth & 0x7F) + 1;
        } else {
            CompParams compParam = new CompParams();
            for (int i = 0; i < size.csize; i++) {
                decoder.decodeCompParams(compParam, i);
                maxDepth = (compParam.depth & 0x7F) + 1;
                isSigned = (compParam.depth & 0x80) > 0 ? true : false;
            }
        }

        BitsPerComponentBox bits =
            (BitsPerComponentBox)metadata.getElement("JPEG2000BitsPerComponentBox");

        if (bits != null) {
            byte[] depths = bits.getBitDepth();
            maxDepth = (depths[0] & 0x7F) + 1;
            isSigned = (depths[0] & 0x80) > 0;
            for (int i = 1; i < nComp; i++)
                if (maxDepth > depths[sourceBands[i]])
                    maxDepth = (depths[sourceBands[i]] & 0x7F) + 1;
        }

        int[] bandOffsets = new int[nComp];
        for (int i = 0; i < nComp; i++)
            bandOffsets[i] = i;

        ChannelDefinitionBox cdb=
            (ChannelDefinitionBox)metadata.getElement("JPEG2000ChannelDefinitionBox");

        if (cdb != null &&
            metadata.getElement("JPEG2000PaletteBox") == null) {
            short[] assoc = cdb.getAssociation();
            short[] types = cdb.getTypes();
            short[] channels = cdb.getChannel();

            for (int i = 0; i < types.length; i++)
                if (types[i] == 0)
                    bandOffsets[sourceBands[channels[i]]] = assoc[i] - 1;
                else if (types[i] == 1 || types[i] == 2)
                    bandOffsets[sourceBands[channels[i]]] = channels[i];
        }

        return createSampleModel(nComp, maxDepth, bandOffsets,
                                 isSigned, tileWidth, tileHeight);
    }

    private SampleModel createOriginalSampleModel() throws IOException {
        if (metadata == null)
            readImageMetadata();

        HeaderBox header = (HeaderBox)metadata.getElement("JPEG2000HeaderBox");
        int maxDepth = 0;
        boolean isSigned = false;
        int nc = size.csize;
        if (header != null) {
            maxDepth = header.getBitDepth();
            isSigned = (maxDepth & 0x80) > 0;
            maxDepth = (maxDepth & 0x7F) + 1;
        } else {
            CompParams compParam = new CompParams();
            for (int i = 0; i < size.csize; i++) {
                decoder.decodeCompParams(compParam, i);
                maxDepth = (compParam.depth & 0x7F) + 1;
                isSigned = (compParam.depth & 0x80) > 0 ? true : false;
            }
        }

        BitsPerComponentBox bits =
            (BitsPerComponentBox)metadata.getElement("JPEG2000BitsPerComponentBox");

        if (bits != null) {
            byte[] depths = bits.getBitDepth();
            maxDepth = (depths[0] & 0x7F) + 1;
            isSigned = (depths[0] & 0x80) > 0;
            for (int i = 1; i < nc; i++)
                if (maxDepth > depths[i])
                    maxDepth = (depths[i] & 0x7F) + 1;
        }

        int[] bandOffsets = new int[nc];
        for (int i = 0; i < nc; i++)
            bandOffsets[i] = i;

        ChannelDefinitionBox cdb=
            (ChannelDefinitionBox)metadata.getElement("JPEG2000ChannelDefinitionBox");
        if (cdb != null &&
            metadata.getElement("JPEG2000PaletteBox") == null) {
            short[] assoc = cdb.getAssociation();
            short[] types = cdb.getTypes();
            short[] channels = cdb.getChannel();

            channelMap = new int[nc];

            for (int i = 0; i < types.length; i++)
                if (types[i] == 0) {
                    bandOffsets[channels[i]] = assoc[i] - 1;
                    channelMap[assoc[i] - 1] = channels[i];
                }
                else if (types[i] == 1 || types[i] == 2) {
                    bandOffsets[channels[i]] = channels[i];
                    channelMap[channels[i]] = channels[i];
                }
        }

        return createSampleModel(nc, maxDepth, bandOffsets, isSigned,
                                 tileWidth, tileHeight);
    }

    private SampleModel createSampleModel(int nc, int maxDepth,
                                   int[] bandOffsets, boolean isSigned,
                                   int tw, int th) {
        SampleModel sm = null;
        if (nc == 1 && (maxDepth == 1 || maxDepth == 2 || maxDepth == 4))
            sm = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
                                                    tw, th, maxDepth);
        else if (maxDepth <= 8)
            sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                                       tw, th, nc, tw * nc, bandOffsets);
        else if (maxDepth <=16)
            sm = new PixelInterleavedSampleModel(isSigned ? DataBuffer.TYPE_SHORT : DataBuffer.TYPE_USHORT,
                                       tw, th, nc, tw * nc, bandOffsets);
        else if (maxDepth <= 32)
            sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_INT,
                                       tw, th, nComp, tw * nComp,
                                       bandOffsets);
        else
            throw new IllegalArgumentException(I18N.getString("J2KReadState11") + " " +
						+ maxDepth);

        return sm;
    }

    private ColorModel createColorModel() throws IOException {
        if (colorModel != null)
            return colorModel;

        PaletteBox pBox = (PaletteBox)metadata.getElement("JPEG2000PaletteBox");
        ChannelDefinitionBox cdef =
            (ChannelDefinitionBox)metadata.getElement("JPEG2000ChannelDefinitionBox");

        // Check 'nComp' instance variable here in case there is an
        // embedded palette such as in the pngsuite images pp0n2c16.png
        // and pp0n6a08.png.
        if (pBox != null && nComp == 1) {
            byte[][] lut = pBox.getLUT();
            int numComp = pBox.getNumComp();

            int[] mapping = new int[numComp];

            for (int i = 0; i < numComp; i++)
                mapping[i] = i;

            ComponentMappingBox cmap =
                (ComponentMappingBox)metadata.getElement("JPEG2000ComponentMappingBox");

            short[] comps = null;
            byte[] type = null;
            byte[] maps = null;

            if (cmap != null) {
                comps = cmap.getComponent();
                type = cmap.getComponentType();
                maps = cmap.getComponentAssociation();
            }

            if (comps != null)
                for (int i = 0; i < numComp; i++)
                    if (type[i] == 1)
                        mapping[i] = maps[i];

            if (numComp == 3)
                colorModel = new IndexColorModel(sampleModel.getSampleSize(0), lut[0].length,
                                                 lut[mapping[0]],
                                                 lut[mapping[1]],
                                                 lut[mapping[2]]);
            else if (numComp == 4)
                colorModel = new IndexColorModel(sampleModel.getSampleSize(0), lut[0].length,
                                                 lut[mapping[0]],
                                                 lut[mapping[1]],
                                                 lut[mapping[2]],
                                                 lut[mapping[3]]);
        } else if (cdef != null){
            HeaderBox header =
                (HeaderBox)metadata.getElement("JPEG2000HeaderBox");
            int numComp = header.getNumComponents();
            int bitDepth = header.getBitDepth();

            boolean hasAlpha = false;
            int alphaChannel = numComp - 1;

            short[] channels = cdef.getChannel();
            short[] cType = cdef.getTypes();
            short[] associations = cdef.getAssociation();

            for (int i = 0; i < channels.length; i++) {
                if (cType[i] == 1 && channels[i] == alphaChannel)
                    hasAlpha = true;
            }

            boolean[] isPremultiplied = new boolean[] {false};

            if (hasAlpha) {
                isPremultiplied = new boolean[alphaChannel];

                for (int i = 0; i < alphaChannel; i++)
                    isPremultiplied[i] = false;

                for (int i = 0; i < channels.length; i++) {
                    if (cType[i] == 2)
                        isPremultiplied[associations[i] - 1] = true;
                }

                for (int i = 1; i < alphaChannel; i++)
                    isPremultiplied[0] &= isPremultiplied[i];
            }

            ColorSpecificationBox cBox =
                (ColorSpecificationBox)metadata.getElement("JPEG2000ColorSpecificationBox");
            ICC_Profile profile = null;
            int colorSpaceType = 0;

            if (cBox != null) {
                profile = cBox.getICCProfile();
                colorSpaceType = cBox.getEnumeratedColorSpace();
            }

            ColorSpace cs = null;
            if (profile != null)
                cs = new ICC_ColorSpace(profile);
            else if (colorSpaceType == ColorSpecificationBox.ECS_sRGB)
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            else if (colorSpaceType == ColorSpecificationBox.ECS_GRAY)
                cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            else if (colorSpaceType == ColorSpecificationBox.ECS_YCC)
                cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);

            byte[] bitDepths = null;
            boolean isSigned = ((bitDepth & 0x80) == 0x80) ? true : false;

            BitsPerComponentBox bitBox =
                (BitsPerComponentBox)metadata.getElement("JPEG2000BitsPerComponentBox");
            if (bitBox != null)
                bitDepths = bitBox.getBitDepth();

            int[] bits = new int[numComp];
            for (int i = 0; i < numComp; i++)
                if (bitDepths != null)
                    bits[i] = (bitDepths[i] & 0x7F) + 1;
                else
                    bits[i] = (bitDepth &0x7F) + 1;

            int maxBitDepth = 1 + (bitDepth & 0x7F);
            if (bitDepths != null)
                for (int i = 0; i < numComp; i++)
                    if (bits[i] > maxBitDepth)
                        maxBitDepth = bits[i];

            int type = -1;

            if (maxBitDepth <= 8)
                type = DataBuffer.TYPE_BYTE;
            else if (maxBitDepth <= 16)
                type = isSigned ? DataBuffer.TYPE_SHORT : DataBuffer.TYPE_USHORT;
            else if (maxBitDepth <= 32)
                type = DataBuffer.TYPE_INT;

            if (type == -1)
                return null;

            if (cs != null) {
                colorModel = new ComponentColorModel(cs,
                                                 bits,
                                                 hasAlpha,
                                                 isPremultiplied[0],
                                                 hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE ,
                                                 type);
            }
        }

        if (colorModel != null)
            return colorModel;

        if(nComp <= 4) {
            // XXX: Code essentially duplicated from FileFormatReader.getColorModel().
            // Create the ColorModel from the SIZ marker segment parameters.
            ColorSpace cs;
            if(nComp > 2) {
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            } else {
                cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            }

            int[] bitsPerComponent = new int[nComp];
            boolean isSigned = false;
            int maxBitDepth = -1;
            for(int i = 0; i < nComp; i++) {
                bitsPerComponent[i] = (compParam.depth & 0x7f) + 1;
                if(maxBitDepth < bitsPerComponent[i]) {
                    maxBitDepth = bitsPerComponent[i];
                }
                isSigned |= (compParam.depth & 0x80) != 0;
            }

            boolean hasAlpha = nComp % 2 == 0;

            int type = -1;

            if (maxBitDepth <= 8) {
                type = DataBuffer.TYPE_BYTE;
            } else if (maxBitDepth <= 16) {
                type = isSigned ? DataBuffer.TYPE_SHORT : DataBuffer.TYPE_USHORT;
            } else if (maxBitDepth <= 32) {
                type = DataBuffer.TYPE_INT;
            }

            if (type != -1) {
                if (nComp == 1 &&
                    (maxBitDepth == 1 || maxBitDepth == 2 ||
                     maxBitDepth == 4)) {
                    colorModel = ImageUtil.createColorModel(getSampleModel());
                } else {
                    colorModel = new ComponentColorModel(cs,
                                                         bitsPerComponent,
                                                         hasAlpha,
                                                         false,
                                                         hasAlpha ?
                                                         Transparency.TRANSLUCENT :
                                                         Transparency.OPAQUE ,
                                                         type);
                }

                return colorModel;
            }
        }

        return ImageUtil.createColorModel(null, getSampleModel());
    }

    public J2KMetadata readImageMetadata() throws IOException {
        if (metadata == null) {
            metadata = new J2KMetadata();
            com.sun.medialib.codec.jp2k.Box mlibBox = null;
            com.sun.media.imageioimpl.plugins.jpeg2000.Box box = null;

            while ((mlibBox = decoder.decodeBox()) != null) {
		box = null;
                Class c = com.sun.media.imageioimpl.plugins.jpeg2000.Box.getBoxClass(mlibBox.type);
                if (c != null) {
                    try {
                        Constructor cons = c.getConstructor(new Class[]{byte[].class});
                        if (cons != null) {
                             box = (Box)cons.newInstance(new Object[]{mlibBox.data});
                        }
                    } catch(NoSuchMethodException e) {
                        try {
                            Constructor cons = c.getConstructor(new Class[]{int.class, byte[].class});
                            if (cons != null) {
                                box = (com.sun.media.imageioimpl.plugins.jpeg2000.Box)
                                      cons.newInstance(new Object[]{new Integer(mlibBox.type), mlibBox.data});
                            }
                        } catch (NoSuchMethodException e1) {
                            box = createUnknowBox(mlibBox);
                        } catch(InvocationTargetException e1) {
                            box = createUnknowBox(mlibBox);
                        } catch (IllegalAccessException e1) {
                            box = createUnknowBox(mlibBox);
                        } catch (InstantiationException e1) {
                            box = createUnknowBox(mlibBox);
                        }
                    } catch(InvocationTargetException e) {
                        box = createUnknowBox(mlibBox);
                    } catch (IllegalAccessException e) {
                        box = createUnknowBox(mlibBox);
                    } catch (InstantiationException e) {
                        box = createUnknowBox(mlibBox);
                    }
                } else {
                    if (mlibBox.data != null)
                        box = createUnknowBox(mlibBox);
                }
                if (box != null)
                    metadata.addNode(box);
            }
        }

        return metadata;
    }

    private com.sun.media.imageioimpl.plugins.jpeg2000.Box
        createUnknowBox(com.sun.medialib.codec.jp2k.Box mlibBox) {
        return
            new com.sun.media.imageioimpl.plugins.jpeg2000.Box(8 + mlibBox.data.length,
                                                           mlibBox.type,
                                                           mlibBox.data);
    }
}
