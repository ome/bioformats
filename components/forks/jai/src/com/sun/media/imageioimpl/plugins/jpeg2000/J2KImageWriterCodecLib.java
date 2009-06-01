/*
 * $RCSfile: J2KImageWriterCodecLib.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2006/09/22 23:07:25 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;
import com.sun.media.imageioimpl.common.ImageUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.medialib.codec.jp2k.CompParams;
import com.sun.medialib.codec.jp2k.Constants;
import com.sun.medialib.codec.jp2k.Encoder;
import com.sun.medialib.codec.jp2k.Params;
import com.sun.medialib.codec.jp2k.Size;
import com.sun.medialib.codec.jiio.*;

public class J2KImageWriterCodecLib extends ImageWriter {
    /** When the writing is aborted, <code>RenderedImageSrc</code> throws a
     *  <code>RuntimeException</code>.
     */
    public static String WRITE_ABORTED = "Write aborted.";

    /** The output stream to write into */
    private ImageOutputStream stream = null;

    /** The metadata format object. */
    private J2KMetadataFormat format;

    /** medialib encoder. */
    private Encoder encoder;

    /** size parameters for medialib. */
    private Size size;

    /** The tile width for encoding */
    private int tileWidth;

    /** The tile height for encoding */
    private int tileHeight;

    /** The tile grid offset for encoding */
    private int tileXOffset, tileYOffset;

    /** The source -> destination transformation */
    private int scaleX, scaleY, xOffset, yOffset;

    /** The source bands to be encoded. */
    private int[] sourceBands = null;

    /** The number of components in the image */
    private int numComp;

    private RenderedImage input;
    private J2KImageWriteParam param;

    /** The input source raster. */
    private Raster inputRaster;

    private Rectangle destinationRegion = null;

    private SampleModel sampleModel;

    /** Coordinate transform or sub selection is needed before encoding. */
    private boolean noTransform = true;
    private boolean noSubband = true;

    /** Indicates a <code>raster</code> rather than a <code>RenderedImage</code>
     *  to be encoded.
     */
    private boolean writeRaster = false;

    /** Constructs <code>J2KImageWriter</code> based on the provided
     *  <code>ImageWriterSpi</code>.
     */
    public J2KImageWriterCodecLib(ImageWriterSpi originator) {
        super(originator);
    }

    public void setOutput(Object output) {
        super.setOutput(output); // validates output
        if (output != null) {
            if (!(output instanceof ImageOutputStream))
                throw new IllegalArgumentException(I18N.getString("J2KImageWriter0"));
            this.stream = (ImageOutputStream)output;
        } else
            this.stream = null;
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new J2KImageWriteParam();
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                             ImageWriteParam param) {
        return null;
    }

    public void write(IIOMetadata streamMetadata,
                      IIOImage image,
                      ImageWriteParam param) throws java.io.IOException {
        if (stream == null) {
            throw new IllegalStateException(I18N.getString("J2KImageWriterMedialib1"));
        }
        if (image == null) {
            throw new IllegalArgumentException(I18N.getString("J2KImageWriterMedialib2"));
        }
        clearAbortRequest();
        processImageStarted(0);
        encoder = new Encoder(stream);

        writeRaster = image.hasRaster();
        ColorModel colorModel = null;

        if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
        } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();
            colorModel = input.getColorModel();
        }

        if (param == null)
            param = new J2KImageWriteParam();

        if(param instanceof J2KImageWriteParam) {
            J2KImageWriteParam j2kParam = (J2KImageWriteParam)param;
            if (!writeRaster &&
                input.getColorModel() instanceof IndexColorModel) {
                j2kParam.setLossless(true);
                j2kParam.setEncodingRate(Double.MAX_VALUE);
                j2kParam.setFilter(J2KImageWriteParam.FILTER_53);
            } else if (j2kParam.getEncodingRate() ==
                       Double.MAX_VALUE) {
                j2kParam.setLossless(true);
                j2kParam.setFilter(J2KImageWriteParam.FILTER_53);
            }
        }
        setParameters(param);

        Rectangle sourceRegion = param.getSourceRegion();
        if (sourceRegion == null) {
            if (writeRaster)
                sourceRegion = inputRaster.getBounds();
            else
                sourceRegion = new Rectangle(input.getMinX(), input.getMinY(),
                                             input.getWidth(),
                                             input.getHeight());
        } else {
            if (writeRaster)
                sourceRegion =
                    sourceRegion.intersection(inputRaster.getBounds());
            else
                sourceRegion =
                    sourceRegion.intersection(new Rectangle(input.getMinX(),
                                                            input.getMinY(),
                                                            input.getWidth(),
                                                            input.getHeight()));
        }

        if (sourceRegion.isEmpty())
            throw new RuntimeException(I18N.getString("J2KImageWriterCodecLib0"));

        try {
            tileWidth = param.getTileWidth();
            tileHeight = param.getTileHeight();
            tileXOffset = param.getTileGridXOffset();
            tileYOffset = param.getTileGridYOffset();
        } catch (IllegalStateException e) {
            param.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
            if (writeRaster) {
                param.setTiling(inputRaster.getWidth(),
                                inputRaster.getHeight(),
                                inputRaster.getMinX(),
                                inputRaster.getMinY());
            } else {
                param.setTiling(input.getTileWidth(),
                                input.getTileHeight(),
                                input.getTileGridXOffset(),
                                input.getTileGridYOffset());
            }
            tileWidth = param.getTileWidth();
            tileHeight = param.getTileHeight();
            tileXOffset = param.getTileGridXOffset();
            tileYOffset = param.getTileGridYOffset();
        }

        scaleX = param.getSourceXSubsampling();
        scaleY = param.getSourceYSubsampling();
        xOffset = param.getSubsamplingXOffset();
        yOffset = param.getSubsamplingYOffset();

        sourceRegion.translate(xOffset, yOffset);
        sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;

	xOffset = sourceRegion.x % scaleX;
	yOffset = sourceRegion.y % scaleY;

        int minX = sourceRegion.x / scaleX;
        int minY = sourceRegion.y / scaleY;

        int w = (sourceRegion.width + scaleX - 1) / scaleX;
        int h = (sourceRegion.height + scaleY - 1) / scaleY;

        tileXOffset += (minX - tileXOffset)/tileWidth * tileWidth;
        tileYOffset += (minY - tileYOffset)/tileHeight * tileHeight;

        destinationRegion = new Rectangle(minX, minY, w, h);

        if (!destinationRegion.equals(sourceRegion) ||
            tileWidth != sampleModel.getWidth() ||
            tileHeight != sampleModel.getHeight() ||
            (!writeRaster &&
             (tileXOffset != input.getTileGridXOffset() ||
             tileYOffset != input.getTileGridYOffset())) ||
            (writeRaster &&
             (tileXOffset != inputRaster.getMinX() ||
             tileYOffset != inputRaster.getMinY())))
            noTransform = false;

        numComp = sampleModel.getNumBands();
        sourceBands = param.getSourceBands();
        if (sourceBands != null) {
            sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
            colorModel = null;
            noSubband = false;
        } else {
            sourceBands = new int[numComp];
            for (int i = 0; i < numComp; i++)
                sourceBands[i] = i;
        }

        numComp = sourceBands.length;

        sampleModel =
            sampleModel.createCompatibleSampleModel(tileWidth, tileHeight);

        setSize();

        setCompParameters(colorModel, sampleModel, param);

        encoder.setMode(Constants.JP2K_COMPOSITE_TILE);

        /* XXX
        J2KMetadata metadata = (J2KMetadata)image.getMetadata();
        ImageTypeSpecifier type = null;
        if (colorModel != null)
            type = new ImageTypeSpecifier(colorModel, sampleModel);

        J2KMetadata metadata1 =
            new J2KMetadata(colorModel, sampleModel, destinationRegion.width,
                            destinationRegion.height, param, this);

        if (metadata == null)
            metadata = metadata1;
        else
            metadata.mergeTree("com_sun_media_imageio_plugins_jpeg2000_image_1.0",
                               metadata1.getAsTree("com_sun_media_imageio_plugins_jpeg2000_image_1.0"));
        */

        //write the metadata
        if (!((J2KImageWriteParam)param).getWriteCodeStreamOnly()) {
            IIOMetadata inMetadata = image.getMetadata();

            J2KMetadata metadata1 = new J2KMetadata(colorModel,
                                                    sampleModel,
                                                    destinationRegion.width,
                                                    destinationRegion.height,
                                                    param,
                                                    this);

            J2KMetadata metadata = null;

            if (inMetadata == null) {
                metadata = metadata1;
            } else {
                // Convert the input metadata tree to a J2KMetadata.
                if(colorModel != null) {
                    ImageTypeSpecifier imageType = 
                        new ImageTypeSpecifier(colorModel, sampleModel);
                    metadata =
                        (J2KMetadata)convertImageMetadata(inMetadata,
                                                          imageType,
                                                          param);
                } else {
                    String metaFormat = null;
                    List metaFormats =
                        Arrays.asList(inMetadata.getMetadataFormatNames());
                    if(metaFormats.contains(J2KMetadata.nativeMetadataFormatName)) {
                        // Initialize from native image metadata format.
                        metaFormat = J2KMetadata.nativeMetadataFormatName;
                    } else if(inMetadata.isStandardMetadataFormatSupported()) {
                        // Initialize from standard metadata form of the
                        // input tree.
                        metaFormat = 
                            IIOMetadataFormatImpl.standardMetadataFormatName;
                    }

                    metadata = new J2KMetadata();
                    if(metaFormat != null) {
                        metadata.setFromTree(metaFormat,
                                             inMetadata.getAsTree(metaFormat));
                    }
                }

                metadata.mergeTree(J2KMetadata.nativeMetadataFormatName,
                                   metadata1.getAsTree(J2KMetadata.nativeMetadataFormatName));
            }

            writeMetadata(metadata);
	} else {
	    encoder.setEncodeCodeStreamOnly();
        }

        for (int y = getMinTileY(); y <= getMaxTileY(); y++) {
            for (int x = getMinTileX(); x <= getMaxTileX(); x++) {
                Raster currentTile = getTile(x, y);
                int sourceFormatTag =
                    MediaLibAccessor.findCompatibleTag(currentTile);

                MediaLibAccessor accessor =
                    new MediaLibAccessor(currentTile,
                                         currentTile.getBounds(),
                                         sourceFormatTag, true);
                mediaLibImage[] mlImage = accessor.getMediaLibImages();

                encoder.encode(mlImage, x + y * size.nxtiles);
                float percentage =
                    (x + y * size.nxtiles + 1.0F) / (size.nxtiles * size.nytiles);
                processImageProgress(percentage * 100.0F);
            }
        }
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                               ImageWriteParam param) {
        return new J2KMetadata(imageType, param, this);
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata inData,
                                            ImageTypeSpecifier imageType,
                                            ImageWriteParam param) {
        // Check arguments.
        if(inData == null) {
            throw new IllegalArgumentException("inData == null!");
        }
        if(imageType == null) {
            throw new IllegalArgumentException("imageType == null!");
        }

        // If it's one of ours, return a clone.
        if (inData instanceof J2KMetadata) {
            return (IIOMetadata)((J2KMetadata)inData).clone();
        }

        try {
            J2KMetadata outData = new J2KMetadata();

            List formats = Arrays.asList(inData.getMetadataFormatNames());

            String format = null;
            if(formats.contains(J2KMetadata.nativeMetadataFormatName)) {
                // Initialize from native image metadata format.
                format = J2KMetadata.nativeMetadataFormatName;
            } else if(inData.isStandardMetadataFormatSupported()) {
                // Initialize from standard metadata form of the input tree.
                format = IIOMetadataFormatImpl.standardMetadataFormatName;
            }

            if(format != null) {
                outData.setFromTree(format, inData.getAsTree(format));
                return outData;
            }
        } catch(IIOInvalidTreeException e) {
            return null;
        }

        return null;
    }

    public boolean canWriteRasters() {
        return true;
    }

    public synchronized void abort() {
        super.abort();
    }

    public void reset() {
        // reset local Java structures
        super.reset();
        stream = null;
    }

    /** This method wraps the protected method <code>abortRequested</code>
     *  to allow the abortions be monitored by <code>J2KRenderedImage</code>.
     */
    public boolean getAbortRequest() {
        return abortRequested();
    }

    private void checkSampleModel(SampleModel sm) {
        int type = sm.getDataType();

        if (type < DataBuffer.TYPE_BYTE || type > DataBuffer.TYPE_INT)
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter5"));
        if (sm.getNumBands() > 16384)
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter6"));
    }

    private void writeMetadata(J2KMetadata metadata) throws IOException {
        if (metadata == null)
            return;

        IIOMetadataNode root =
            (IIOMetadataNode)metadata.getAsTree("com_sun_media_imageio_plugins_jpeg2000_image_1.0");
        if (root == null)
            return;
        format =
            (J2KMetadataFormat)metadata.getMetadataFormat("com_sun_media_imageio_plugins_jpeg2000_image_1.0");
        writeSuperBox(root);
    }

    private void writeSuperBox(IIOMetadataNode node) throws IOException {
        NodeList list = node.getChildNodes();

        String name = node.getNodeName();
        if (name.startsWith("JPEG2000")) {
/*
            int length = computeLength(node);
            byte[] data = new byte[length];
            generateSuperBoxContent(node, data, 0);
            com.sun.medialib.codec.jp2k.Box box =
                new com.sun.medialib.codec.jp2k.Box();
            box.data = data;
            box.type = Box.getTypeInt((String)Box.getTypeByName(name));
            encoder.encodeSuperBox(box.type, new com.sun.medialib.codec.jp2k.Box[]{box});
            return;
*/
/*
            com.sun.medialib.codec.jp2k.Box box =
                new com.sun.medialib.codec.jp2k.Box();
            box.type = Box.getTypeInt((String)Box.getTypeByName(name));
            encoder.encodeSuperBox(box.type, null);
*/
        }

        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode child = (IIOMetadataNode)list.item(i);

            name = child.getNodeName();
            if (name.startsWith("JPEG2000") && format.isLeaf(name))
                writeBox(child);
            else
                writeSuperBox(child);
        }
    }

    private void writeBox(IIOMetadataNode node) throws IOException {
        com.sun.medialib.codec.jp2k.Box mlibBox =
            new com.sun.medialib.codec.jp2k.Box();
        mlibBox.type = Box.getTypeInt((String)Box.getAttribute(node, "Type"));
        Box box = Box.createBox(mlibBox.type, node);
        mlibBox.data = box.getContent();
        encoder.encodeBox(mlibBox);
    }

    private int computeLength(IIOMetadataNode root) {
        NodeList list = root.getChildNodes();
        int length = 0;
        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode node = (IIOMetadataNode)list.item(i);
            String name = node.getNodeName();

            if (format.isLeaf(name)) {
                String s = (String)Box.getAttribute(node, "Length");
                length += new Integer(s).intValue();
            } else
                length += computeLength(node);

        }

        return length + (root.getNodeName().startsWith("JPEG2000") ? 8 : 0) ;
    }

    private int generateSuperBoxContent(IIOMetadataNode root,
                                        byte[] data,
                                        int pos) throws IOException {
        String name = root.getNodeName();
        if (name.startsWith("JPEG2000")) {
            int length = computeLength(root);
            Box.copyInt(data, pos, length);
            pos += 4;
            int type = Box.getTypeInt((String)Box.getTypeByName(name));
            Box.copyInt(data, pos, type);
            pos += 4;
        }

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode node = (IIOMetadataNode)list.item(i);
            name = node.getNodeName();

            if (format.isLeaf(name)) {
                int type =
                    Box.getTypeInt((String)Box.getAttribute(node, "Type"));
                Box box = Box.createBox(type, node);
                byte[] data1 = box.getContent();
                Box.copyInt(data, pos, data1.length + 8);
                pos += 4;

                Box.copyInt(data, pos, type);
                pos += 4;
                System.arraycopy(data1, 0, data, pos, data1.length);
                pos += data1.length;
            } else {
                pos = generateSuperBoxContent(node, data, pos);
            }
        }

        return pos;
    }

    private Raster getTile(int tileX, int tileY) {
        int sx = tileXOffset + tileX * tileWidth;
        int sy = tileYOffset + tileY * tileHeight;
        Rectangle bounds = new Rectangle(sx, sy, tileWidth, tileHeight);

        if (writeRaster) {
            bounds = bounds.intersection(destinationRegion);
            if (noTransform) {
                return inputRaster.createChild(bounds.x, bounds.y,
                                           bounds.width, bounds.height,
                                           bounds.x, bounds.y, sourceBands);
            }

            sx = bounds.x;
            sy = bounds.y;
            WritableRaster ras =
                Raster.createWritableRaster(sampleModel, new Point(sx, sy));

            int x = mapToSourceX(sx);
            int y = mapToSourceY(sy);

            int minY = inputRaster.getMinY();
            int maxY = inputRaster.getMinY() + inputRaster.getHeight();

            int cTileWidth = bounds.width;

            int length = (cTileWidth - 1) * scaleX + 1;

            for (int j = 0; j < bounds.height; j++, sy++, y += scaleY) {
                if (y < minY || y >= maxY)
                    continue;
                Raster source =
                    inputRaster.createChild(x, y, length, 1,
                                            x, y, null);
                int tempX = sx;
                for (int i = 0, offset = x; i < cTileWidth;
                    i++, tempX++, offset += scaleX) {
                    for (int k = 0; k < numComp; k++) {
                        int p = source.getSample(offset, y, sourceBands[k]);
                        ras.setSample(tempX, sy, k, p);
                    }
                }
            }

            return ras;

        } else {
            if (noTransform) {
                Raster ras = input.getTile(tileX, tileY);
                if (destinationRegion.contains(bounds) && noSubband)
                    return ras;
                else {
                    bounds = bounds.intersection(destinationRegion);
                    return ras.createChild(bounds.x, bounds.y,
                                           bounds.width, bounds.height,
                                           bounds.x, bounds.y, sourceBands);
                }
            }

            bounds = bounds.intersection(destinationRegion);
            sx = bounds.x;
            sy = bounds.y;

            WritableRaster ras =
                Raster.createWritableRaster(sampleModel, new Point(sx, sy));

            int x = mapToSourceX(sx);
            int y = mapToSourceY(sy);

            int minY = input.getMinY();
            int maxY = input.getMinY() + input.getHeight();

            int cTileWidth = bounds.width;
            int length = (cTileWidth -1) * scaleX + 1;

            for (int j = 0; j < bounds.height; j++, sy++, y += scaleY) {
                if (y < minY || y >= maxY)
                    continue;

                Raster source =
                    input.getData(new Rectangle(x, y, length, 1));

                int tempX = sx;
                for (int i = 0, offset = x; i < cTileWidth;
                    i++, tempX++, offset += scaleX) {
                    for (int k = 0; k < numComp; k++) {
                        int p = source.getSample(offset, y, sourceBands[k]);
                        ras.setSample(tempX, sy, k, p);
                    }
                }
            }
            return ras;
        }
    }

    private int mapToSourceX(int x) {
        return x * scaleX + xOffset;
    }

    private int mapToSourceY(int y) {
        return y * scaleY + yOffset;
    }

    private int getMinTileX() {
        return ToTile(destinationRegion.x, tileXOffset, tileWidth);
    }

    private int getMaxTileX() {
        return ToTile(destinationRegion.x + destinationRegion.width - 1,
                      tileXOffset, tileWidth);
    }

    private int getMinTileY() {
        return ToTile(destinationRegion.y, tileYOffset, tileHeight);
    }

    private int getMaxTileY() {
        return ToTile(destinationRegion.y + destinationRegion.height - 1,
                      tileYOffset, tileHeight);
    }

    private static int ToTile(int pos, int tileOffset, int tileSize) {
        pos -= tileOffset;
        if (pos < 0) {
            pos += 1 - tileSize;         // force round to -infinity (ceiling)
        }
        return pos/tileSize;
    }

    private void setSize() {
        size = new Size();
        size.csize = numComp;
        size.nxtiles = getMaxTileX() - getMinTileX() + 1;
        size.nytiles = getMaxTileY() - getMinTileY() + 1;

        size.xosize = destinationRegion.x;
        size.yosize = destinationRegion.y;
        size.xsize = destinationRegion.width + destinationRegion.x;
        size.ysize = destinationRegion.height + destinationRegion.y;
        size.xtosize = tileXOffset;
        size.ytosize = tileYOffset;
        size.xtsize = tileWidth;
        size.ytsize = tileHeight;

        encoder.setSize(size);
    }

    private void setCompParameters(ColorModel colorModel,
                                   SampleModel sampleModel,
                                   ImageWriteParam compParamArg) {
        // Check the parameters.
        if (colorModel == null && sampleModel == null &&
            (compParamArg == null ||
             !(compParamArg instanceof J2KImageWriteParam))) {
            return;
        }

        // Get the bit depths.
        int[] bitDepths = null;
        boolean isSigned = false;
        if(colorModel != null) {
            bitDepths = colorModel.getComponentSize();
            isSigned = colorModel.getTransferType() == DataBuffer.TYPE_SHORT;
        } else if(sampleModel != null) {
            bitDepths = sampleModel.getSampleSize();
            isSigned = sampleModel.getDataType() == DataBuffer.TYPE_SHORT;
        }

        // Get the number of decomposition levels.
        int numDecompositionLevels = -1;
        if(compParamArg != null) {
            // Cast is safe due to parameter check above.
            numDecompositionLevels =
                ((J2KImageWriteParam)compParamArg).getNumDecompositionLevels();
        }

        // Return if nothing to set.
        if(bitDepths == null && numDecompositionLevels == -1) return;

        // Check for unequal bit depths.
        boolean bitDepthVaries = false;
        if(bitDepths != null) {
            for(int i = 1; i < bitDepths.length; i++) {
                if(bitDepths[i] != bitDepths[0]) {
                    bitDepthVaries = true;
                    break;
                }
            }
        }

        CompParams cp = encoder.getCompParams(null, -1);

        // Update the COD segment if needed.
        if((numDecompositionLevels != -1 &&
            numDecompositionLevels != cp.maxlvls) ||
           (bitDepths != null &&
            ((isSigned ? 0x80 : 0x00) | (bitDepths[0] - 1)) != cp.depth)) {

            if(numDecompositionLevels != -1) {
                cp.maxlvls = numDecompositionLevels;
            }

            // Set the main COD bit depth to bitDepths[0].
            if(bitDepths != null) {
                cp.depth = (isSigned ? 0x80 : 0x00) | (bitDepths[0] - 1);
            }

            encoder.setCompParams(cp, -1);
        }

        // Update COC segments if needed.
        if(bitDepthVaries) { // only true if bitDepths != null
            // Loop over component zero even though unnecessary.
            for(int i = 0; i < numComp; i++) {
                cp = encoder.getCompParams(null, i);

                if(numDecompositionLevels != -1) {
                    cp.maxlvls = numDecompositionLevels;
                }

                cp.depth = (isSigned ? 0x80 : 0x00) | (bitDepths[i] - 1);

                encoder.setCompParams(cp, i);
            }
        }
    }

    private void setParameters(ImageWriteParam paramArg) {
        if (paramArg == null ||
            !(paramArg instanceof J2KImageWriteParam)) {
            return;
        }

        J2KImageWriteParam param = (J2KImageWriteParam)paramArg;

        // set the rate
        double rate = param.getEncodingRate();
        if (rate != Double.MAX_VALUE) {
            // convert the rate to the medialib definition
            rate /= ImageUtil.getElementSize(sampleModel);
            encoder.setRate(rate, 0);
        } else
            encoder.setRate(0.0, 0);

        Params params = new Params();

        // set the component transformation flag
        params.enablemct = param.getComponentTransformation() ?
                            Constants.JP2K_MCT_ENABLE :
                            Constants.JP2K_MCT_DISABLE;

        // set coding style
        if (param.getEPH())
            params.cstyle |= Constants.JP2K_COD_EPH;
        if (param.getSOP())
            params.cstyle |= Constants.JP2K_COD_SOP;

        // set the wavelet filter type
        if (J2KImageWriteParam.FILTER_53.equals(param.getFilter()))
            params.wavemode = Constants.JP2K_WAVEMODE_53;
        else if (J2KImageWriteParam.FILTER_97.equals(param.getFilter()))
            params.wavemode = Constants.JP2K_WAVEMODE_97;

        //Set the progressive mode
        String progressiveType = param.getProgressionType();

        if ("layer".equals(progressiveType))
            params.prgorder = Constants.JP2K_COD_LRCPPRG;
        if ("res".equals(progressiveType))
            params.prgorder = Constants.JP2K_COD_RLCPPRG;
        if ("res-pos".equals(progressiveType))
            params.prgorder = Constants.JP2K_COD_RPCLPRG;
        if ("pos-comp".equals(progressiveType))
            params.prgorder = Constants.JP2K_COD_PCRLPRG;
        if ("comp-pos".equals(progressiveType))
            params.prgorder = Constants.JP2K_COD_CPRLPRG;

        encoder.setParams(params);
    }
}

