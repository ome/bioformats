/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/*
 * $RCSfile: PNMImageWriter.java,v $
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
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:40 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pnm;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import java.io.IOException;

import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.media.imageio.plugins.pnm.PNMImageWriteParam;
import com.sun.media.imageioimpl.common.ImageUtil;
/**
 * The Java Image IO plugin writer for encoding a binary RenderedImage into
 * a PNM format.
 *
 * The encoding process may clip, subsample using the parameters
 * specified in the <code>ImageWriteParam</code>.
 *
 * @see com.sun.media.imageio.plugins.pnm.PNMImageWriteParam
 */
public class PNMImageWriter extends ImageWriter {
    private static final int PBM_ASCII  = '1';
    private static final int PGM_ASCII  = '2';
    private static final int PPM_ASCII  = '3';
    private static final int PBM_RAW    = '4';
    private static final int PGM_RAW    = '5';
    private static final int PPM_RAW    = '6';

    private static final int SPACE      = ' ';

    private static final String COMMENT =
        "# written by com.sun.media.imageioimpl.PNMImageWriter";

    private static byte[] lineSeparator;

    private int variant;
    private int maxValue;

    static {
        if (lineSeparator == null) {
            String ls = (String)java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));
            lineSeparator = ls.getBytes();
        }
    }

    /** The output stream to write into */
    private ImageOutputStream stream = null;

    /** Constructs <code>PNMImageWriter</code> based on the provided
     *  <code>ImageWriterSpi</code>.
     */
    public PNMImageWriter(ImageWriterSpi originator) {
        super(originator);
    }

    public void setOutput(Object output) {
        super.setOutput(output); // validates output
        if (output != null) {
            if (!(output instanceof ImageOutputStream))
                throw new IllegalArgumentException(I18N.getString("PNMImageWriter0"));
            this.stream = (ImageOutputStream)output;
        } else
            this.stream = null;
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new PNMImageWriteParam();
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                               ImageWriteParam param) {
        return new PNMMetadata(imageType, param);
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                             ImageWriteParam param) {
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

        PNMMetadata outData = null;

        // Obtain a PNMMetadata object.
        if(inData instanceof PNMMetadata) {
            // Clone the input metadata.
            outData = (PNMMetadata)((PNMMetadata)inData).clone();
        } else {
            try {
                outData = new PNMMetadata(inData);
            } catch(IIOInvalidTreeException e) {
                // XXX Warning
                outData = new PNMMetadata();
            }
        }

        // Update the metadata per the image type and param.
        outData.initialize(imageType, param);

        return outData;
    }

    public boolean canWriteRasters() {
        return true;
    }

    public void write(IIOMetadata streamMetadata,
                      IIOImage image,
                      ImageWriteParam param) throws IOException {
        clearAbortRequest();
        processImageStarted(0);
        if (param == null)
            param = getDefaultWriteParam();

        RenderedImage input = null;
        Raster inputRaster = null;
        boolean writeRaster = image.hasRaster();
        Rectangle sourceRegion = param.getSourceRegion();
        SampleModel sampleModel = null;
        ColorModel colorModel = null;

        if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
            if (sourceRegion == null)
                sourceRegion = inputRaster.getBounds();
            else
                sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
        } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();
            colorModel = input.getColorModel();
            Rectangle rect = new Rectangle(input.getMinX(), input.getMinY(),
                                           input.getWidth(), input.getHeight());
            if (sourceRegion == null)
                sourceRegion = rect;
            else
                sourceRegion = sourceRegion.intersection(rect);
        }

        if (sourceRegion.isEmpty())
            throw new RuntimeException(I18N.getString("PNMImageWrite1"));

	ImageUtil.canEncodeImage(this, colorModel, sampleModel);

        int scaleX = param.getSourceXSubsampling();
        int scaleY = param.getSourceYSubsampling();
        int xOffset = param.getSubsamplingXOffset();
        int yOffset = param.getSubsamplingYOffset();

        sourceRegion.translate(xOffset, yOffset);
        sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;

        int minX = sourceRegion.x / scaleX;
        int minY = sourceRegion.y / scaleY;
        int w = (sourceRegion.width + scaleX - 1) / scaleX;
        int h = (sourceRegion.height + scaleY - 1) / scaleY;

        Rectangle destinationRegion = new Rectangle(minX, minY, w, h);

        int tileHeight = sampleModel.getHeight();
        int tileWidth = sampleModel.getWidth();

        // Raw data can only handle bytes, everything greater must be ASCII.
        int[] sampleSize = sampleModel.getSampleSize();
        int[] sourceBands = param.getSourceBands();
        boolean noSubband = true;
        int numBands = sampleModel.getNumBands();

        if (sourceBands != null) {
            sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
            colorModel = null;
            noSubband = false;
            numBands = sampleModel.getNumBands();
        } else {
            sourceBands = new int[numBands];
            for (int i = 0; i < numBands; i++)
                sourceBands[i] = i;
        }

        // Colormap populated for non-bilevel IndexColorModel only.
        byte[] reds = null;
        byte[] greens = null;
        byte[] blues = null;

        // Flag indicating that PB data should be inverted before writing.
        boolean isPBMInverted = false;

        if (numBands == 1) {
            if (colorModel instanceof IndexColorModel) {
                IndexColorModel icm = (IndexColorModel)colorModel;

                int mapSize = icm.getMapSize();
                if (mapSize < (1 << sampleSize[0]))
                    throw new RuntimeException(I18N.getString("PNMImageWrite2"));

                if(sampleSize[0] == 1) {
                    variant = PBM_RAW;

                    // Set PBM inversion flag if 1 maps to a higher color
                    // value than 0: PBM expects white-is-zero so if this
                    // does not obtain then inversion needs to occur.
                    isPBMInverted = icm.getRed(1) > icm.getRed(0);
                } else {
                    variant = PPM_RAW;

                    reds = new byte[mapSize];
                    greens = new byte[mapSize];
                    blues = new byte[mapSize];

                    icm.getReds(reds);
                    icm.getGreens(greens);
                    icm.getBlues(blues);
                }
            } else if (sampleSize[0] == 1) {
                variant = PBM_RAW;
            } else if (sampleSize[0] <= 8) {
                variant = PGM_RAW;
            } else {
                variant = PGM_ASCII;
            }
        } else if (numBands == 3) {
            if (sampleSize[0] <= 8 && sampleSize[1] <= 8 &&
                sampleSize[2] <= 8) {	// all 3 bands must be <= 8
                variant = PPM_RAW;
            } else {
                variant = PPM_ASCII;
            }
        } else {
            throw new RuntimeException(I18N.getString("PNMImageWrite3"));
        }

        IIOMetadata inputMetadata = image.getMetadata();
        ImageTypeSpecifier imageType;
        if(colorModel != null) {
            imageType = new ImageTypeSpecifier(colorModel, sampleModel);
        } else {
            int dataType = sampleModel.getDataType();
            switch(numBands) {
            case 1:
                imageType =
                    ImageTypeSpecifier.createGrayscale(sampleSize[0], dataType,
                                                       false);
                break;
            case 3:
                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                imageType =
                    ImageTypeSpecifier.createInterleaved(cs,
                                                         new int[] {0, 1, 2},
                                                         dataType,
                                                         false, false);
                break;
            default:
                throw new IIOException("Cannot encode image with "+
                                       numBands+" bands!");
            }
        }

        PNMMetadata metadata;
        if(inputMetadata != null) {
            // Convert metadata.
            metadata = (PNMMetadata)convertImageMetadata(inputMetadata,
                                                         imageType, param);
        } else {
            // Use default.
            metadata = (PNMMetadata)getDefaultImageMetadata(imageType, param);
        }

        // Read parameters
        boolean isRawPNM;
        if(param instanceof PNMImageWriteParam) {
            isRawPNM = ((PNMImageWriteParam)param).getRaw();
        } else {
            isRawPNM = metadata.isRaw();
        }

        maxValue = metadata.getMaxValue();
        for (int i = 0; i < sampleSize.length; i++) {
            int v = (1 << sampleSize[i]) - 1;
            if (v > maxValue) {
                maxValue = v;
            }
        }

        if (isRawPNM) {
            // Raw output is desired.
            int maxBitDepth = metadata.getMaxBitDepth();
            if (!isRaw(variant) && maxBitDepth <= 8) {
                // Current variant is ASCII and the bit depth is acceptable
                // so convert to RAW variant by adding '3' to variant.
                variant += 0x3;
            } else if(isRaw(variant) && maxBitDepth > 8) {
                // Current variant is RAW and the bit depth it too large for
                // RAW so convert to ASCII.
                variant -= 0x3;
            }
            // Omitted cases are (variant == RAW && max <= 8) and
            // (variant == ASCII && max > 8) neither of which requires action.
        } else if(isRaw(variant)) {
            // Raw output is NOT desired so convert to ASCII
            variant -= 0x3;
        }

        // Write PNM file.
        stream.writeByte('P');			// magic value: 'P'
        stream.writeByte(variant);

        stream.write(lineSeparator);
        stream.write(COMMENT.getBytes());	// comment line

        // Write the comments provided in the metadata
        Iterator comments = metadata.getComments();
        if(comments != null) {
            while(comments.hasNext()) {
                stream.write(lineSeparator);
                String comment = "# " + (String)comments.next();
                stream.write(comment.getBytes());
            }
        }

        stream.write(lineSeparator);
        writeInteger(stream, w);		// width
        stream.write(SPACE);
        writeInteger(stream, h);		// height

        // Write sample max value for non-binary images
        if ((variant != PBM_RAW) && (variant != PBM_ASCII)) {
            stream.write(lineSeparator);
            writeInteger(stream, maxValue);
        }

        // The spec allows a single character between the
        // last header value and the start of the raw data.
        if (variant == PBM_RAW ||
            variant == PGM_RAW ||
            variant == PPM_RAW) {
            stream.write('\n');
        }

        // Set flag for optimal image writing case: row-packed data with
        // correct band order if applicable.
        boolean writeOptimal = false;
        if (variant == PBM_RAW &&
            sampleModel.getTransferType() == DataBuffer.TYPE_BYTE &&
            sampleModel instanceof MultiPixelPackedSampleModel) {

            MultiPixelPackedSampleModel mppsm =
                (MultiPixelPackedSampleModel)sampleModel;

            int originX = 0;
            if (writeRaster)
                originX = inputRaster.getMinX();
            else
                originX = input.getMinX();

            // Must have left-aligned bytes with unity bit stride.
            if(mppsm.getBitOffset((sourceRegion.x - originX) % tileWidth) == 0 &&
               mppsm.getPixelBitStride() == 1 && scaleX == 1)
                writeOptimal = true;
        } else if ((variant == PGM_RAW || variant == PPM_RAW) &&
                   sampleModel instanceof ComponentSampleModel &&
                   !(colorModel instanceof IndexColorModel)) {

            ComponentSampleModel csm =
                (ComponentSampleModel)sampleModel;

            // Pixel stride must equal band count.
            if(csm.getPixelStride() == numBands && scaleX == 1) {
                writeOptimal = true;

                // Band offsets must equal band indices.
                if(variant == PPM_RAW) {
                    int[] bandOffsets = csm.getBandOffsets();
                    for(int b = 0; b < numBands; b++) {
                        if(bandOffsets[b] != b) {
                            writeOptimal = false;
                            break;
                        }
                    }
                }
            }
        }

        // Write using an optimal approach if possible.
        if(writeOptimal) {
            int bytesPerRow = variant == PBM_RAW ?
                (w + 7)/8 : w * sampleModel.getNumBands();
            byte[] bdata = null;
            byte[] invertedData = new byte[bytesPerRow];

            // Loop over tiles to minimize cobbling.
            for(int j = 0; j < sourceRegion.height; j++) {
                if (abortRequested())
                    break;
                Raster lineRaster = null;
                if (writeRaster) {
                    lineRaster = inputRaster.createChild(sourceRegion.x,
                                                         j,
                                                         sourceRegion.width,
                                                         1, 0, 0, null);
                } else {
                    lineRaster =
                        input.getData(new Rectangle(sourceRegion.x,
                                                    sourceRegion.y + j,
                                                    w, 1));
                    lineRaster = lineRaster.createTranslatedChild(0, 0);
                }

                bdata = ((DataBufferByte)lineRaster.getDataBuffer()).getData();

                sampleModel = lineRaster.getSampleModel();
                int offset = 0;
                if (sampleModel instanceof ComponentSampleModel) {
                    offset =
                        ((ComponentSampleModel)sampleModel).getOffset(lineRaster.getMinX()-lineRaster.getSampleModelTranslateX(),
                                                                      lineRaster.getMinY()-lineRaster.getSampleModelTranslateY());
                } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
                    offset = ((MultiPixelPackedSampleModel)sampleModel).getOffset(lineRaster.getMinX() -
                                                                        lineRaster.getSampleModelTranslateX(),
                                                                      lineRaster.getMinX()-lineRaster.getSampleModelTranslateY());
                }

		if (isPBMInverted) {
                    for(int k = offset, m = 0; m < bytesPerRow; k++, m++)
                        invertedData[m] = (byte)~bdata[k];
                    bdata = invertedData;
                    offset = 0;
                }

                stream.write(bdata, offset, bytesPerRow);
                processImageProgress(100.0F * j / sourceRegion.height);
            }

            // Write all buffered bytes and return.
            stream.flush();
            if (abortRequested())
                processWriteAborted();
            else
                processImageComplete();
            return;
        }

        // Buffer for 1 rows of original pixels
        int size = sourceRegion.width * numBands;

        int[] pixels = new int[size];

        // Also allocate a buffer to hold the data to be written to the file,
        // so we can use array writes.
        byte[] bpixels =
            reds == null ? new byte[w * numBands] : new byte[w * 3];

        // The index of the sample being written, used to
        // place a line separator after every 16th sample in
        // ASCII mode.  Not used in raw mode.
        int count = 0;

        // Process line by line
        int lastRow = sourceRegion.y + sourceRegion.height;

        for (int row = sourceRegion.y; row < lastRow; row += scaleY) {
            if (abortRequested())
                break;
            // Grab the pixels
            Raster src = null;

            if (writeRaster)
                src = inputRaster.createChild(sourceRegion.x,
                                              row,
                                              sourceRegion.width, 1,
                                              sourceRegion.x, row, sourceBands);
            else
                src = input.getData(new Rectangle(sourceRegion.x, row,
                                                  sourceRegion.width, 1));
            src.getPixels(sourceRegion.x, row, sourceRegion.width, 1, pixels);

            if (isPBMInverted)
                for (int i = 0; i < size; i += scaleX)
                    bpixels[i] ^= 1;

            switch (variant) {
            case PBM_ASCII:
            case PGM_ASCII:
                for (int i = 0; i < size; i += scaleX) {
                    if ((count++ % 16) == 0)
                        stream.write(lineSeparator);
                    else
                        stream.write(SPACE);

                    writeInteger(stream, pixels[i]);
                }
                stream.write(lineSeparator);
                break;

            case PPM_ASCII:
                if (reds == null) {	// no need to expand
                    int[] bandOffset =
                        ((ComponentSampleModel)sampleModel).getBandOffsets();
                    for (int i = 0; i < size; i += scaleX * numBands) {
                        for (int j = 0; j < numBands; j++) {
                            if ((count++ % 16) == 0)
                                stream.write(lineSeparator);
                            else
                                stream.write(SPACE);

                            writeInteger(stream, pixels[i + j]);
                        }
                    }
                } else {
                    for (int i = 0; i < size; i += scaleX) {
                        if ((count++ % 5) == 0)
                            stream.write(lineSeparator);
                        else
                            stream.write(SPACE);

                        writeInteger(stream, (reds[pixels[i]] & 0xFF));
                        stream.write(SPACE);
                        writeInteger(stream, (greens[pixels[i]] & 0xFF));
                        stream.write(SPACE);
                        writeInteger(stream, (blues[pixels[i]] & 0xFF));
                    }
                }
                stream.write(lineSeparator);
                break;

            case PBM_RAW:
                // 8 pixels packed into 1 byte, the leftovers are padded.
                int kdst = 0;
                int ksrc = 0;
                int b = 0;
                int pos = 7;
                for (int i = 0; i < size; i += scaleX) {
                    b |= pixels[i] << pos;
                    pos--;
                    if (pos == -1) {
                        bpixels[kdst++] = (byte)b;
                        b = 0;
                        pos = 7;
                    }
                }

                if (pos != 7)
                    bpixels[kdst++] = (byte)b;

                stream.write(bpixels, 0, kdst);
                break;

            case PGM_RAW:
                for (int i = 0, j = 0; i < size; i += scaleX) {
                    bpixels[j++] = (byte)(pixels[i]);
                }
                stream.write(bpixels, 0, w);
                break;

            case PPM_RAW:
                if (reds == null) {	// no need to expand
                    for (int i = 0, k = 0; i < size; i += scaleX * numBands) {
                        for (int j = 0; j < numBands; j++)
                          bpixels[k++] = (byte)(pixels[i + j] & 0xFF);
                    }
                } else {
                    for (int i = 0, j = 0; i < size; i += scaleX) {
                        bpixels[j++] = reds[pixels[i]];
                        bpixels[j++] = greens[pixels[i]];
                        bpixels[j++] = blues[pixels[i]];
                    }
                }
                stream.write(bpixels, 0, bpixels.length);
                break;
            }

            processImageProgress(100.0F * (row - sourceRegion.y) /
                                 sourceRegion.height);
        }

        // Force all buffered bytes to be written out.
        stream.flush();

        if (abortRequested())
            processWriteAborted();
        else
            processImageComplete();
    }

    public void reset() {
        super.reset();
        stream = null;
    }

    /** Writes an integer to the output in ASCII format. */
    private void writeInteger(ImageOutputStream output, int i) throws IOException {
        output.write(Integer.toString(i).getBytes());
    }

    /** Writes a byte to the output in ASCII format. */
    private void writeByte(ImageOutputStream output, byte b) throws IOException {
        output.write(Byte.toString(b).getBytes());
    }

    /** Returns true if file variant is raw format, false if ASCII. */
    private boolean isRaw(int v) {
        return (v >= PBM_RAW);
    }
}
