/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * $RCSfile: TIFFImageReader.java,v $
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
 * $Revision: 1.13 $
 * $Date: 2007/12/19 20:17:02 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.w3c.dom.Node;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFColorConverter;
import com.sun.media.imageio.plugins.tiff.TIFFDecompressor;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFImageReadParam;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageioimpl.common.PackageUtil;

public class TIFFImageReader extends ImageReader {

    private static final boolean DEBUG = false; // XXX 'false' for release!!!

    // The current ImageInputStream source.
    ImageInputStream stream = null;

    // True if the file header has been read.
    boolean gotHeader = false;

    ImageReadParam imageReadParam = getDefaultReadParam();

    // Stream metadata, or null.
    TIFFStreamMetadata streamMetadata = null;

    // The current image index.
    int currIndex = -1;

    // Metadata for image at 'currIndex', or null.
    TIFFImageMetadata imageMetadata = null;
    
    // A <code>List</code> of <code>Long</code>s indicating the stream
    // positions of the start of the IFD for each image.  Entries
    // are added as needed.
    List imageStartPosition = new ArrayList();

    // The number of images in the stream, if known, otherwise -1.
    int numImages = -1;

    // The ImageTypeSpecifiers of the images in the stream.
    // Contains a map of Integers to Lists.
    HashMap imageTypeMap = new HashMap();

    BufferedImage theImage = null;

    int width = -1;
    int height = -1;
    int numBands = -1;
    int tileOrStripWidth = -1, tileOrStripHeight = -1;

    int planarConfiguration = BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY;

    int rowsDone = 0;

    int compression;
    int photometricInterpretation;
    int samplesPerPixel;
    int[] sampleFormat;
    int[] bitsPerSample;
    int[] extraSamples;
    char[] colorMap;

    int sourceXOffset;
    int sourceYOffset;
    int srcXSubsampling;
    int srcYSubsampling;

    int dstWidth;
    int dstHeight;
    int dstMinX;
    int dstMinY;
    int dstXOffset;
    int dstYOffset;

    int tilesAcross;
    int tilesDown;

    int pixelsRead;
    int pixelsToRead;

    public TIFFImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);

        // Clear all local values based on the previous stream contents.
        resetLocal();

        if (input != null) {
            if (!(input instanceof ImageInputStream)) {
                throw new IllegalArgumentException
                    ("input not an ImageInputStream!"); 
            }
            this.stream = (ImageInputStream)input;
        } else {
            this.stream = null;
        }
    }

    // Do not seek to the beginning of the stream so as to allow users to
    // point us at an IFD within some other file format
    private void readHeader() throws IIOException {
        if (gotHeader) {
            return;
        }
        if (stream == null) {
            throw new IllegalStateException("Input not set!");
        }

        // Create an object to store the stream metadata
        this.streamMetadata = new TIFFStreamMetadata();
        
        try {
            int byteOrder = stream.readUnsignedShort();
            if (byteOrder == 0x4d4d) {
                streamMetadata.byteOrder = ByteOrder.BIG_ENDIAN;
                stream.setByteOrder(ByteOrder.BIG_ENDIAN);
            } else if (byteOrder == 0x4949) {
                streamMetadata.byteOrder = ByteOrder.LITTLE_ENDIAN;
                stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            } else {
                processWarningOccurred(
                           "Bad byte order in header, assuming little-endian");
                streamMetadata.byteOrder = ByteOrder.LITTLE_ENDIAN;
                stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            }
            
            int magic = stream.readUnsignedShort();
            if (magic != 42) {
                processWarningOccurred(
                                     "Bad magic number in header, continuing");
            }
            
            // Seek to start of first IFD
            long offset = stream.readUnsignedInt();
            imageStartPosition.add(new Long(offset));
            stream.seek(offset);
        } catch (IOException e) {
            throw new IIOException("I/O error reading header!", e);
        }

        gotHeader = true;
    }

    private int locateImage(int imageIndex) throws IIOException {
        readHeader();

        try {
            // Find closest known index
            int index = Math.min(imageIndex, imageStartPosition.size() - 1);

            // Seek to that position
            Long l = (Long)imageStartPosition.get(index);
            stream.seek(l.longValue());

            // Skip IFDs until at desired index or last image found
            while (index < imageIndex) {
                int count = stream.readUnsignedShort();
                stream.skipBytes(12*count);

                long offset = stream.readUnsignedInt();
                if (offset == 0) {
                    return index;
                }
                
                imageStartPosition.add(new Long(offset));
                stream.seek(offset);
                ++index;
            }
        } catch (IOException e) {
            throw new IIOException("Couldn't seek!", e);
        }

        if (currIndex != imageIndex) {
            imageMetadata = null;
        }
        currIndex = imageIndex;
        return imageIndex;
    }

    public int getNumImages(boolean allowSearch) throws IOException {
        if (stream == null) {
            throw new IllegalStateException("Input not set!");
        }
        if (seekForwardOnly && allowSearch) {
            throw new IllegalStateException
                ("seekForwardOnly and allowSearch can't both be true!");
        }

        if (numImages > 0) {
            return numImages;
        }
        if (allowSearch) {
            this.numImages = locateImage(Integer.MAX_VALUE) + 1;
        }
        return numImages;
    }

    public IIOMetadata getStreamMetadata() throws IIOException {
        readHeader();
        return streamMetadata;
    }

    // Throw an IndexOutOfBoundsException if index < minIndex,
    // and bump minIndex if required.
    private void checkIndex(int imageIndex) {
        if (imageIndex < minIndex) {
            throw new IndexOutOfBoundsException("imageIndex < minIndex!");
        }
        if (seekForwardOnly) {
            minIndex = imageIndex;
        }
    }

    // Verify that imageIndex is in bounds, find the image IFD, read the
    // image metadata, initialize instance variables from the metadata.
    private void seekToImage(int imageIndex) throws IIOException {
        checkIndex(imageIndex);

        int index = locateImage(imageIndex);
        if (index != imageIndex) {
            throw new IndexOutOfBoundsException("imageIndex out of bounds!");
        }

        readMetadata();

        initializeFromMetadata();
    }

    // Stream must be positioned at start of IFD for 'currIndex'
    private void readMetadata() throws IIOException {
        if (stream == null) {
            throw new IllegalStateException("Input not set!");
        }

        if (imageMetadata != null) {
            return;
        }
        try {
            // Create an object to store the image metadata
            List tagSets;
            if (imageReadParam instanceof TIFFImageReadParam) {
                tagSets =
                    ((TIFFImageReadParam)imageReadParam).getAllowedTagSets();
            } else {
                tagSets = new ArrayList(1);
                tagSets.add(BaselineTIFFTagSet.getInstance());
            }

            this.imageMetadata = new TIFFImageMetadata(tagSets);
            imageMetadata.initializeFromStream(stream, ignoreMetadata);
        } catch (IIOException iioe) {
            throw iioe;
        } catch (IOException ioe) {
            throw new IIOException("I/O error reading image metadata!", ioe);
        }
    }

    private int getWidth() {
        return this.width;
    }

    private int getHeight() {
        return this.height;
    }

    private int getNumBands() {
        return this.numBands;
    }

    // Returns tile width if image is tiled, else image width
    private int getTileOrStripWidth() {
        TIFFField f =
            imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_TILE_WIDTH);
        return (f == null) ? getWidth() : f.getAsInt(0);
    }

    // Returns tile height if image is tiled, else strip height
    private int getTileOrStripHeight() {
        TIFFField f =
            imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_TILE_LENGTH);
        if (f != null) {
            return f.getAsInt(0);
        }

        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);
        // Default for ROWS_PER_STRIP is 2^32 - 1, i.e., infinity
        int h = (f == null) ? -1 : f.getAsInt(0);
        return (h == -1) ? getHeight() : h;
    }

    private int getPlanarConfiguration() {
        TIFFField f =
        imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_PLANAR_CONFIGURATION);
        if (f != null) {
            int planarConfigurationValue = f.getAsInt(0);
            if(planarConfigurationValue ==
               BaselineTIFFTagSet.PLANAR_CONFIGURATION_PLANAR) {
                // Some writers (e.g. Kofax standard Multi-Page TIFF
                // Storage Filter v2.01.000; cf. bug 4929147) do not
                // correctly set the value of this field. Attempt to
                // ascertain whether the value is correctly Planar.
                if(getCompression() ==
                   BaselineTIFFTagSet.COMPRESSION_OLD_JPEG &&
                   imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT) !=
                   null) {
                    // JPEG interchange format cannot have
                    // PlanarConfiguration value Chunky so reset.
                    processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with JPEGInterchangeFormat; resetting to \"Chunky\".");
                    planarConfigurationValue =
                        BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY;
                } else {
                    TIFFField offsetField =
                        imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_TILE_OFFSETS);
                    if (offsetField == null) {
                        // Tiles
                        offsetField =
                            imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_STRIP_OFFSETS);
                        int tw = getTileOrStripWidth();
                        int th = getTileOrStripHeight();
                        int tAcross = (getWidth() + tw - 1)/tw;
                        int tDown = (getHeight() + th - 1)/th;
                        int tilesPerImage = tAcross*tDown;
                        long[] offsetArray = offsetField.getAsLongs();
                        if(offsetArray != null &&
                           offsetArray.length == tilesPerImage) {
                            // Length of offsets array is
                            // TilesPerImage for Chunky and
                            // SamplesPerPixel*TilesPerImage for Planar.
                            processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with TileOffsets field value count; resetting to \"Chunky\".");
                            planarConfigurationValue =
                                BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY;
                        }
                    } else {
                        // Strips
                        int rowsPerStrip = getTileOrStripHeight();
                        int stripsPerImage =
                            (getHeight() + rowsPerStrip - 1)/rowsPerStrip;
                        long[] offsetArray = offsetField.getAsLongs();
                        if(offsetArray != null &&
                           offsetArray.length == stripsPerImage) {
                            // Length of offsets array is
                            // StripsPerImage for Chunky and
                            // SamplesPerPixel*StripsPerImage for Planar.
                            processWarningOccurred("PlanarConfiguration \"Planar\" value inconsistent with StripOffsets field value count; resetting to \"Chunky\".");
                            planarConfigurationValue =
                                BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY;
                        }
                    }
                }
            }
            return planarConfigurationValue;
        }

        return BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY;
    }

    private long getTileOrStripOffset(int tileIndex) throws IIOException {
        TIFFField f =
            imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_TILE_OFFSETS);
        if (f == null) {
            f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_STRIP_OFFSETS);
        }
        if (f == null) {
            f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT);
        }

        if(f == null) {
            throw new IIOException
                ("Missing required strip or tile offsets field.");
        }

        return f.getAsLong(tileIndex);
    }

    private long getTileOrStripByteCount(int tileIndex) throws IOException {
        TIFFField f =
           imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_TILE_BYTE_COUNTS);
        if (f == null) {
            f =
          imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS);
        }
        if (f == null) {
            f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
        }

        long tileOrStripByteCount;
        if(f != null) {
            tileOrStripByteCount = f.getAsLong(tileIndex);
        } else {
            processWarningOccurred("TIFF directory contains neither StripByteCounts nor TileByteCounts field: attempting to calculate from strip or tile width and height.");

            // Initialize to number of bytes per strip or tile assuming
            // no compression.
            int bitsPerPixel = bitsPerSample[0];
            for(int i = 1; i < samplesPerPixel; i++) {
                bitsPerPixel += bitsPerSample[i];
            }
            int bytesPerRow = (getTileOrStripWidth()*bitsPerPixel + 7)/8;
            tileOrStripByteCount = bytesPerRow*getTileOrStripHeight();

            // Clamp to end of stream if possible.
            long streamLength = stream.length();
            if(streamLength != -1) {
                tileOrStripByteCount =
                    Math.min(tileOrStripByteCount,
                             streamLength - getTileOrStripOffset(tileIndex));
            } else {
                processWarningOccurred("Stream length is unknown: cannot clamp estimated strip or tile byte count to EOF.");
            }
        }

        return tileOrStripByteCount;
    }

    private int getCompression() {
        TIFFField f =
            imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_COMPRESSION);
        if (f == null) {
	    return BaselineTIFFTagSet.COMPRESSION_NONE;
	} else {
            return f.getAsInt(0);
	}
    }

    public int getWidth(int imageIndex) throws IOException {
        seekToImage(imageIndex);
        return getWidth();
    }

    public int getHeight(int imageIndex) throws IOException {
        seekToImage(imageIndex);
        return getHeight();
    }

    /**
     * Initializes these instance variables from the image metadata:
     * <pre>
     * compression
     * width
     * height
     * samplesPerPixel
     * numBands
     * colorMap
     * photometricInterpretation
     * sampleFormat
     * bitsPerSample
     * extraSamples
     * tileOrStripWidth
     * tileOrStripHeight
     * </pre>
     */
    private void initializeFromMetadata() {
        TIFFField f;

        // Compression
        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_COMPRESSION);
        if (f == null) {
            processWarningOccurred
                ("Compression field is missing; assuming no compression");
            compression = BaselineTIFFTagSet.COMPRESSION_NONE;
        } else {
            compression = f.getAsInt(0);
        }

        // Whether key dimensional information is absent.
        boolean isMissingDimension = false;

        // ImageWidth -> width
        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_IMAGE_WIDTH);
        if (f != null) {
            this.width = f.getAsInt(0);
        } else {
            processWarningOccurred("ImageWidth field is missing.");
            isMissingDimension = true;
        }

        // ImageLength -> height
        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_IMAGE_LENGTH);
        if (f != null) {
            this.height = f.getAsInt(0);
        } else {
            processWarningOccurred("ImageLength field is missing.");
            isMissingDimension = true;
        }

        // SamplesPerPixel
        f =
          imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_SAMPLES_PER_PIXEL);
        if (f != null) {
            samplesPerPixel = f.getAsInt(0);
        } else {
            samplesPerPixel = 1;
            isMissingDimension = true;
        }

        // If any dimension is missing and there is a JPEG stream available
        // get the information from it.
        int defaultBitDepth = 1;
        if(isMissingDimension &&
           (f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT)) != null) {
            Iterator iter = ImageIO.getImageReadersByFormatName("JPEG");
            if(iter != null && iter.hasNext()) {
                ImageReader jreader = (ImageReader)iter.next();
                try {
                    stream.mark();
                    stream.seek(f.getAsLong(0));
                    jreader.setInput(stream);
                    if(imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_IMAGE_WIDTH) == null) {
                        this.width = jreader.getWidth(0);
                    }
                    if(imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_IMAGE_LENGTH) == null) {
                        this.height = jreader.getHeight(0);
                    }
                    ImageTypeSpecifier imageType = jreader.getRawImageType(0);
                    if(imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_SAMPLES_PER_PIXEL) == null) {
                        this.samplesPerPixel =
                            imageType.getSampleModel().getNumBands();
                    }
                    stream.reset();
                    defaultBitDepth =
                        imageType.getColorModel().getComponentSize(0);
                } catch(IOException e) {
                    // Ignore it and proceed: an error will occur later.
                }
                jreader.dispose();
            }
        }

        if (samplesPerPixel < 1) {
            processWarningOccurred("Samples per pixel < 1!");
        }

        // SamplesPerPixel -> numBands
        numBands = samplesPerPixel;

        // ColorMap
        this.colorMap = null;
        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_COLOR_MAP);
        if (f != null) {
            // Grab color map
            colorMap = f.getAsChars();
        }
        
        // PhotometricInterpretation
        f =
        imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION);
        if (f == null) {
            if (compression == BaselineTIFFTagSet.COMPRESSION_CCITT_RLE ||
                compression == BaselineTIFFTagSet.COMPRESSION_CCITT_T_4 ||
                compression == BaselineTIFFTagSet.COMPRESSION_CCITT_T_6) {
                processWarningOccurred
                    ("PhotometricInterpretation field is missing; "+
                     "assuming WhiteIsZero");
                photometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO;
            } else if(this.colorMap != null) {
                photometricInterpretation =
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_PALETTE_COLOR;
            } else if(samplesPerPixel == 3 || samplesPerPixel == 4) {
                photometricInterpretation =
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_RGB;
            } else {
                processWarningOccurred
                    ("PhotometricInterpretation field is missing; "+
                     "assuming BlackIsZero");
                photometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO;
            }
        } else {
            photometricInterpretation = f.getAsInt(0);
        }

        // SampleFormat
        boolean replicateFirst = false;
        int first = -1;

        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_SAMPLE_FORMAT);
        sampleFormat = new int[samplesPerPixel];
        replicateFirst = false;
        if (f == null) {
            replicateFirst = true;
            first = BaselineTIFFTagSet.SAMPLE_FORMAT_UNDEFINED;
        } else if (f.getCount() != samplesPerPixel) {
            replicateFirst = true;
            first = f.getAsInt(0);
        }

        for (int i = 0; i < samplesPerPixel; i++) {
            sampleFormat[i] = replicateFirst ? first : f.getAsInt(i);
            if (sampleFormat[i] !=
                  BaselineTIFFTagSet.SAMPLE_FORMAT_UNSIGNED_INTEGER &&
                sampleFormat[i] !=
                  BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER &&
                sampleFormat[i] !=
                  BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT &&
                sampleFormat[i] !=
                  BaselineTIFFTagSet.SAMPLE_FORMAT_UNDEFINED) {
                processWarningOccurred(
          "Illegal value for SAMPLE_FORMAT, assuming SAMPLE_FORMAT_UNDEFINED");
                sampleFormat[i] = BaselineTIFFTagSet.SAMPLE_FORMAT_UNDEFINED;
            }
        }

        // BitsPerSample
        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
        this.bitsPerSample = new int[samplesPerPixel];
        replicateFirst = false;
        if (f == null) {
            replicateFirst = true;
            first = defaultBitDepth;
        } else if (f.getCount() != samplesPerPixel) {
            replicateFirst = true;
            first = f.getAsInt(0);
        }
        
        for (int i = 0; i < samplesPerPixel; i++) {
            // Replicate initial value if not enough values provided
            bitsPerSample[i] = replicateFirst ? first : f.getAsInt(i);

            if (DEBUG) {
                System.out.println("bitsPerSample[" + i + "] = "
                                   + bitsPerSample[i]);
            }
        }

        // ExtraSamples
        this.extraSamples = null;
        f = imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_EXTRA_SAMPLES);
        if (f != null) {
            extraSamples = f.getAsInts();
        }

//         System.out.println("colorMap = " + colorMap);
//         if (colorMap != null) {
//             for (int i = 0; i < colorMap.length; i++) {
//              System.out.println("colorMap[" + i + "] = " + (int)(colorMap[i]));
//             }
//         }

    }

    public Iterator getImageTypes(int imageIndex) throws IIOException {
        List l; // List of ImageTypeSpecifiers

        Integer imageIndexInteger = new Integer(imageIndex);
        if(imageTypeMap.containsKey(imageIndexInteger)) {
            // Return the cached ITS List.
            l = (List)imageTypeMap.get(imageIndexInteger);
        } else {
            // Create a new ITS List.
            l = new ArrayList(1);

            // Create the ITS and cache if for later use so that this method
            // always returns an Iterator containing the same ITS objects.
            seekToImage(imageIndex);
            ImageTypeSpecifier itsRaw = 
                TIFFDecompressor.getRawImageTypeSpecifier
                    (photometricInterpretation,
                     compression,
                     samplesPerPixel,
                     bitsPerSample,
                     sampleFormat,
                     extraSamples,
                     colorMap);

            // Check for an ICCProfile field.
            TIFFField iccProfileField =
                imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_ICC_PROFILE);

            // If an ICCProfile field is present change the ImageTypeSpecifier
            // to use it if the data layout is component type.
            if(iccProfileField != null &&
               itsRaw.getColorModel() instanceof ComponentColorModel) {
                // Create a ColorSpace from the profile.
                byte[] iccProfileValue = iccProfileField.getAsBytes();
                ICC_Profile iccProfile =
                    ICC_Profile.getInstance(iccProfileValue);
                ICC_ColorSpace iccColorSpace =
                    new ICC_ColorSpace(iccProfile);

                // Get the raw sample and color information.
                ColorModel cmRaw = itsRaw.getColorModel();
                ColorSpace csRaw = cmRaw.getColorSpace();
                SampleModel smRaw = itsRaw.getSampleModel();

                // Get the number of samples per pixel and the number
                // of color components.
                int numBands = smRaw.getNumBands();
                int numComponents = iccColorSpace.getNumComponents();

                // Replace the ColorModel with the ICC ColorModel if the
                // numbers of samples and color components are amenable.
                if(numBands == numComponents ||
                   numBands == numComponents + 1) {
                    // Set alpha flags.
                    boolean hasAlpha = numComponents != numBands;
                    boolean isAlphaPre =
                        hasAlpha && cmRaw.isAlphaPremultiplied();

                    // Create a ColorModel of the same class and with
                    // the same transfer type.
                    ColorModel iccColorModel =
                        new ComponentColorModel(iccColorSpace,
                                                cmRaw.getComponentSize(),
                                                hasAlpha,
                                                isAlphaPre,
                                                cmRaw.getTransparency(),
                                                cmRaw.getTransferType());

                    // Prepend the ICC profile-based ITS to the List. The
                    // ColorModel and SampleModel are guaranteed to be
                    // compatible as the old and new ColorModels are both
                    // ComponentColorModels with the same transfer type
                    // and the same number of components.
                    l.add(new ImageTypeSpecifier(iccColorModel, smRaw));

                    // Append the raw ITS to the List if and only if its
                    // ColorSpace has the same type and number of components
                    // as the ICC ColorSpace.
                    if(csRaw.getType() == iccColorSpace.getType() &&
                       csRaw.getNumComponents() ==
                       iccColorSpace.getNumComponents()) {
                        l.add(itsRaw);
                    }
                } else { // ICCProfile not compatible with SampleModel.
                    // Append the raw ITS to the List.
                    l.add(itsRaw);
                }
            } else { // No ICCProfile field or raw ColorModel not component.
                // Append the raw ITS to the List.
                l.add(itsRaw);
            }

            // Cache the ITS List.
            imageTypeMap.put(imageIndexInteger, l);
        }

        return l.iterator();
    }

    public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
        seekToImage(imageIndex);
        TIFFImageMetadata im =
            new TIFFImageMetadata(imageMetadata.getRootIFD().getTagSetList());
        Node root =
            imageMetadata.getAsTree(TIFFImageMetadata.nativeMetadataFormatName);
        im.setFromTree(TIFFImageMetadata.nativeMetadataFormatName, root);
        return im;
    }

    public IIOMetadata getStreamMetadata(int imageIndex) throws IIOException {
        readHeader();
        TIFFStreamMetadata sm = new TIFFStreamMetadata();
        Node root = sm.getAsTree(TIFFStreamMetadata.nativeMetadataFormatName);
        sm.setFromTree(TIFFStreamMetadata.nativeMetadataFormatName, root);
        return sm;
    }

    public boolean isRandomAccessEasy(int imageIndex) throws IOException {
        if(currIndex != -1) {
            seekToImage(currIndex);
            return getCompression() == BaselineTIFFTagSet.COMPRESSION_NONE;
        } else {
            return false;
        }
    }

    // Thumbnails

    public boolean readSupportsThumbnails() {
        return false;
    }

    public boolean hasThumbnails(int imageIndex) {
        return false;
    }

    public int getNumThumbnails(int imageIndex) throws IOException {
        return 0;
    }

    public ImageReadParam getDefaultReadParam() {
        return new TIFFImageReadParam();
    }

    public boolean isImageTiled(int imageIndex) throws IOException {
        seekToImage(imageIndex);

        TIFFField f =
            imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_TILE_WIDTH);
        return f != null;
    }

    public int getTileWidth(int imageIndex) throws IOException {
        seekToImage(imageIndex);
        return getTileOrStripWidth();
    }

    public int getTileHeight(int imageIndex) throws IOException {
        seekToImage(imageIndex);
        return getTileOrStripHeight();
    }

    public BufferedImage readTile(int imageIndex, int tileX, int tileY)
        throws IOException {

        int w = getWidth(imageIndex);
        int h = getHeight(imageIndex);
        int tw = getTileWidth(imageIndex);
        int th = getTileHeight(imageIndex);

        int x = tw*tileX;
        int y = th*tileY;

        if(tileX < 0 || tileY < 0 || x >= w || y >= h) {
            throw new IllegalArgumentException
                ("Tile indices are out of bounds!");
        }

        if (x + tw > w) {
            tw = w - x;
        }

        if (y + th > h) {
            th = h - y;
        }

        ImageReadParam param = getDefaultReadParam();
        Rectangle tileRect = new Rectangle(x, y, tw, th);
        param.setSourceRegion(tileRect);

        return read(imageIndex, param);
    }

    public boolean canReadRaster() {
        // Enable this?
        return false;
    }

    public Raster readRaster(int imageIndex, ImageReadParam param)
        throws IOException {
        // Enable this?
        throw new UnsupportedOperationException();
    }

//     public BufferedImage readTileRaster(int imageIndex,
//                                         int tileX, int tileY)
//         throws IOException {
//     }

    private int[] sourceBands;
    private int[] destinationBands;

    private TIFFDecompressor decompressor;

    // floor(num/den)
    private static int ifloor(int num, int den) {
        if (num < 0) {
            num -= den - 1;
        }
        return num/den;
    }

    // ceil(num/den)
    private static int iceil(int num, int den) {
        if (num > 0) {
            num += den - 1;
        }
        return num/den;
    }

    private void prepareRead(int imageIndex, ImageReadParam param)
        throws IOException {
        if (stream == null) {
            throw new IllegalStateException("Input not set!");
        }

        // A null ImageReadParam means we use the default
        if (param == null) {
            param = getDefaultReadParam();
        }

        this.imageReadParam = param;

        seekToImage(imageIndex);

        this.tileOrStripWidth = getTileOrStripWidth();
        this.tileOrStripHeight = getTileOrStripHeight();
        this.planarConfiguration = getPlanarConfiguration();

        this.sourceBands = param.getSourceBands();
        if (sourceBands == null) {
            sourceBands = new int[numBands];
            for (int i = 0; i < numBands; i++) {
                sourceBands[i] = i;
            }
        }

        // Initialize the destination image
        Iterator imageTypes = getImageTypes(imageIndex);
        ImageTypeSpecifier theImageType =
            ImageUtil.getDestinationType(param, imageTypes);

        int destNumBands = theImageType.getSampleModel().getNumBands();

        this.destinationBands = param.getDestinationBands();
        if (destinationBands == null) {
            destinationBands = new int[destNumBands];
            for (int i = 0; i < destNumBands; i++) {
                destinationBands[i] = i;
            }
        }

        if (sourceBands.length != destinationBands.length) {
            throw new IllegalArgumentException(
                              "sourceBands.length != destinationBands.length");
        }

        for (int i = 0; i < sourceBands.length; i++) {
            int sb = sourceBands[i];
            if (sb < 0 || sb >= numBands) {
                throw new IllegalArgumentException(
                                                  "Source band out of range!");
            }
            int db = destinationBands[i];
            if (db < 0 || db >= destNumBands) {
                throw new IllegalArgumentException(
                                             "Destination band out of range!");
            }
        }
    }

    public RenderedImage readAsRenderedImage(int imageIndex,
                                             ImageReadParam param)
        throws IOException {
        prepareRead(imageIndex, param);
        return new TIFFRenderedImage(this, imageIndex, imageReadParam,
                                     width, height);
    }

    private void decodeTile(int ti, int tj, int band) throws IOException {
        if(DEBUG) {
            System.out.println("decodeTile("+ti+","+tj+","+band+")");
        }

        // Compute the region covered by the strip or tile
        Rectangle tileRect = new Rectangle(ti*tileOrStripWidth,
                                           tj*tileOrStripHeight,
                                           tileOrStripWidth,
                                           tileOrStripHeight);

        // Clip against the image bounds if the image is not tiled. If it
        // is tiled, the tile may legally extend beyond the image bounds.
        if(!isImageTiled(currIndex)) {
            tileRect =
                tileRect.intersection(new Rectangle(0, 0, width, height));
        }

        // Return if the intersection is empty.
        if(tileRect.width <= 0 || tileRect.height <= 0) {
            return;
        }
        
        int srcMinX = tileRect.x;
        int srcMinY = tileRect.y;
        int srcWidth = tileRect.width;
        int srcHeight = tileRect.height;

        // Determine dest region that can be derived from the
        // source region
        
        dstMinX = iceil(srcMinX - sourceXOffset, srcXSubsampling);
        int dstMaxX = ifloor(srcMinX + srcWidth - 1 - sourceXOffset,
                         srcXSubsampling);
        
        dstMinY = iceil(srcMinY - sourceYOffset, srcYSubsampling);
        int dstMaxY = ifloor(srcMinY + srcHeight - 1 - sourceYOffset,
                             srcYSubsampling);
        
        dstWidth = dstMaxX - dstMinX + 1;
        dstHeight = dstMaxY - dstMinY + 1;
        
        dstMinX += dstXOffset;
        dstMinY += dstYOffset;
        
        // Clip against image bounds
        
        Rectangle dstRect = new Rectangle(dstMinX, dstMinY,
                                          dstWidth, dstHeight);
        dstRect =
            dstRect.intersection(theImage.getRaster().getBounds());
        
        dstMinX = dstRect.x;
        dstMinY = dstRect.y;
        dstWidth = dstRect.width;
        dstHeight = dstRect.height;
        
        if (dstWidth <= 0 || dstHeight <= 0) {
            return;
        }
        
        // Backwards map dest region to source to determine
        // active source region
        
        int activeSrcMinX = (dstMinX - dstXOffset)*srcXSubsampling +
            sourceXOffset;
        int sxmax = 
            (dstMinX + dstWidth - 1 - dstXOffset)*srcXSubsampling +
            sourceXOffset;
        int activeSrcWidth = sxmax - activeSrcMinX + 1;
        
        int activeSrcMinY = (dstMinY - dstYOffset)*srcYSubsampling +
            sourceYOffset;
        int symax =
            (dstMinY + dstHeight - 1 - dstYOffset)*srcYSubsampling +
            sourceYOffset;
        int activeSrcHeight = symax - activeSrcMinY + 1;
        
        decompressor.setSrcMinX(srcMinX);
        decompressor.setSrcMinY(srcMinY);
        decompressor.setSrcWidth(srcWidth);
        decompressor.setSrcHeight(srcHeight);
        
        decompressor.setDstMinX(dstMinX);
        decompressor.setDstMinY(dstMinY);
        decompressor.setDstWidth(dstWidth);
        decompressor.setDstHeight(dstHeight);
        
        decompressor.setActiveSrcMinX(activeSrcMinX);
        decompressor.setActiveSrcMinY(activeSrcMinY);
        decompressor.setActiveSrcWidth(activeSrcWidth);
        decompressor.setActiveSrcHeight(activeSrcHeight);

        int tileIndex = tj*tilesAcross + ti;

        if (planarConfiguration ==
            BaselineTIFFTagSet.PLANAR_CONFIGURATION_PLANAR) {
            tileIndex += band*tilesAcross*tilesDown;
        }
        
        long offset = getTileOrStripOffset(tileIndex);
        long byteCount = getTileOrStripByteCount(tileIndex);

        //
        // Attempt to handle truncated streams, i.e., where reading the
        // compressed strip or tile would result in an EOFException. The
        // number of bytes to read is clamped to the number available
        // from the stream starting at the indicated position in the hope
        // that the decompressor will handle it.
        //
        long streamLength = stream.length();
        if(streamLength > 0 && offset + byteCount > streamLength) {
            processWarningOccurred("Attempting to process truncated stream.");
            if(Math.max(byteCount = streamLength - offset, 0) == 0) {
                processWarningOccurred("No bytes in strip/tile: skipping.");
                return;
            }
        }

        decompressor.setStream(stream);
        decompressor.setOffset(offset);
        decompressor.setByteCount((int)byteCount);
        
        decompressor.beginDecoding();

        stream.mark();
        decompressor.decode();
        stream.reset();
    }

    private void reportProgress() {
        // Report image progress/update to listeners after each tile
        pixelsRead += dstWidth*dstHeight;
        processImageProgress(100.0f*pixelsRead/pixelsToRead);
        processImageUpdate(theImage,
                           dstMinX, dstMinY, dstWidth, dstHeight,
                           1, 1,
                           destinationBands);
    }

    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IOException {
        prepareRead(imageIndex, param);
        this.theImage = getDestination(param,
                                       getImageTypes(imageIndex),
                                       width, height);

        srcXSubsampling = imageReadParam.getSourceXSubsampling();
        srcYSubsampling = imageReadParam.getSourceYSubsampling();

        Point p = imageReadParam.getDestinationOffset();
        dstXOffset = p.x;
        dstYOffset = p.y;

        // This could probably be made more efficient...
        Rectangle srcRegion = new Rectangle(0, 0, 0, 0);
        Rectangle destRegion = new Rectangle(0, 0, 0, 0);

        computeRegions(imageReadParam, width, height, theImage,
                       srcRegion, destRegion);

        // Initial source pixel, taking source region and source
        // subsamplimg offsets into account
        sourceXOffset = srcRegion.x;
        sourceYOffset = srcRegion.y;

        pixelsToRead = destRegion.width*destRegion.height;
        pixelsRead = 0;

        processImageStarted(imageIndex);
        processImageProgress(0.0f);

        tilesAcross = (width + tileOrStripWidth - 1)/tileOrStripWidth;
        tilesDown = (height + tileOrStripHeight - 1)/tileOrStripHeight;

        int compression = getCompression();

        // Attempt to get decompressor and color converted from the read param
        
        TIFFColorConverter colorConverter = null;
        if (imageReadParam instanceof TIFFImageReadParam) {
            TIFFImageReadParam tparam =
                (TIFFImageReadParam)imageReadParam;
            this.decompressor = tparam.getTIFFDecompressor();
            colorConverter = tparam.getColorConverter();
        }

        // If we didn't find one, use a standard decompressor
        if (this.decompressor == null) {
            if (compression ==
                BaselineTIFFTagSet.COMPRESSION_NONE) {
                // Get the fillOrder field.
                TIFFField fillOrderField =
                    imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_FILL_ORDER);

                // Set the decompressor based on the fill order.
                if(fillOrderField != null && fillOrderField.getAsInt(0) == 2) {
                    this.decompressor = new TIFFLSBDecompressor();
                } else {
                    this.decompressor = new TIFFNullDecompressor();
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_CCITT_T_6) {
                // Fall back to the Java decompressor.
                if (this.decompressor == null) {
                    if(DEBUG) {
                        System.out.println("Using Java T.6 decompressor");
                    }
                    this.decompressor = new TIFFFaxDecompressor();
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_CCITT_T_4) {
                // Fall back to the Java decompressor.
                if (this.decompressor == null) {
                    if(DEBUG) {
                        System.out.println("Using Java T.4 decompressor");
                    }
                    this.decompressor = new TIFFFaxDecompressor();
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_CCITT_RLE) {
                this.decompressor = new TIFFFaxDecompressor();
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_PACKBITS) {
                if(DEBUG) {
                    System.out.println("Using TIFFPackBitsDecompressor");
                }
                this.decompressor = new TIFFPackBitsDecompressor();
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_LZW) {
                if(DEBUG) {
                    System.out.println("Using TIFFLZWDecompressor");
                }
		TIFFField predictorField =
                    imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_PREDICTOR);
		int predictor = ((predictorField == null) ?
                                 BaselineTIFFTagSet.PREDICTOR_NONE :
                                 predictorField.getAsInt(0));
                this.decompressor = new TIFFLZWDecompressor(predictor);
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_JPEG) {
                this.decompressor = new TIFFJPEGDecompressor();
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_ZLIB ||
                       compression ==
                       BaselineTIFFTagSet.COMPRESSION_DEFLATE) {
		TIFFField predictorField =
                    imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_PREDICTOR);
		int predictor = ((predictorField == null) ?
                                 BaselineTIFFTagSet.PREDICTOR_NONE :
                                 predictorField.getAsInt(0));
                this.decompressor = new TIFFDeflateDecompressor(predictor);
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                TIFFField JPEGProcField =
                    imageMetadata.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_PROC);
                if(JPEGProcField == null) {
                    processWarningOccurred
                        ("JPEGProc field missing; assuming baseline sequential JPEG process.");
                } else if(JPEGProcField.getAsInt(0) !=
                   BaselineTIFFTagSet.JPEG_PROC_BASELINE) {
                    throw new IIOException
                        ("Old-style JPEG supported for baseline sequential JPEG process only!");
                }
                this.decompressor = new TIFFOldJPEGDecompressor();
                //throw new IIOException("Old-style JPEG not supported!");
            } else {
                throw new IIOException
                    ("Unsupported compression type (tag number = "+
                     compression+")!");
            }

            if (photometricInterpretation ==
                BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR &&
                compression != BaselineTIFFTagSet.COMPRESSION_JPEG &&
                compression != BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                boolean convertYCbCrToRGB =
                    theImage.getColorModel().getColorSpace().getType() ==
                    ColorSpace.TYPE_RGB;
                TIFFDecompressor wrappedDecompressor =
                    this.decompressor instanceof TIFFNullDecompressor ?
                    null : this.decompressor;
                this.decompressor =
                    new TIFFYCbCrDecompressor(wrappedDecompressor,
                                              convertYCbCrToRGB);
            }
        }

        if(DEBUG) {
            System.out.println("\nDecompressor class = "+
                               decompressor.getClass().getName()+"\n");
        }

        if (colorConverter == null) {
            if (photometricInterpretation ==
                BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CIELAB &&
                theImage.getColorModel().getColorSpace().getType() ==
                ColorSpace.TYPE_RGB) {
                colorConverter = new TIFFCIELabColorConverter();
             } else if (photometricInterpretation ==
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR &&
                        !(this.decompressor instanceof TIFFYCbCrDecompressor) &&
                        compression != BaselineTIFFTagSet.COMPRESSION_JPEG &&
                        compression != BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                 colorConverter = new TIFFYCbCrColorConverter(imageMetadata);
            }
        }
        
        decompressor.setReader(this);
        decompressor.setMetadata(imageMetadata);
        decompressor.setImage(theImage);

        decompressor.setPhotometricInterpretation(photometricInterpretation);
        decompressor.setCompression(compression);
        decompressor.setSamplesPerPixel(samplesPerPixel);
        decompressor.setBitsPerSample(bitsPerSample);
        decompressor.setSampleFormat(sampleFormat);
        decompressor.setExtraSamples(extraSamples);
        decompressor.setColorMap(colorMap);

        decompressor.setColorConverter(colorConverter);

        decompressor.setSourceXOffset(sourceXOffset);
        decompressor.setSourceYOffset(sourceYOffset);
        decompressor.setSubsampleX(srcXSubsampling);
        decompressor.setSubsampleY(srcYSubsampling);

        decompressor.setDstXOffset(dstXOffset);
        decompressor.setDstYOffset(dstYOffset);

        decompressor.setSourceBands(sourceBands);
        decompressor.setDestinationBands(destinationBands);

        // Compute bounds on the tile indices for this source region.
        int minTileX =
            TIFFImageWriter.XToTileX(srcRegion.x, 0, tileOrStripWidth);
        int minTileY =
            TIFFImageWriter.YToTileY(srcRegion.y, 0, tileOrStripHeight);
        int maxTileX =
            TIFFImageWriter.XToTileX(srcRegion.x + srcRegion.width - 1,
                                     0, tileOrStripWidth);
        int maxTileY =
            TIFFImageWriter.YToTileY(srcRegion.y + srcRegion.height - 1,
                                     0, tileOrStripHeight);

        boolean isAbortRequested = false;
        if (planarConfiguration ==
            BaselineTIFFTagSet.PLANAR_CONFIGURATION_PLANAR) {
            
            decompressor.setPlanar(true);
            
            int[] sb = new int[1];
            int[] db = new int[1];
            for (int tj = minTileY; tj <= maxTileY; tj++) {
                for (int ti = minTileX; ti <= maxTileX; ti++) {
                    for (int band = 0; band < numBands; band++) {
                        sb[0] = sourceBands[band];
                        decompressor.setSourceBands(sb);
                        db[0] = destinationBands[band];
                        decompressor.setDestinationBands(db);
                        //XXX decompressor.beginDecoding();

                        // The method abortRequested() is synchronized
                        // so check it only once per loop just before
                        // doing any actual decoding.
                        if(abortRequested()) {
                            isAbortRequested = true;
                            break;
                        }

                        decodeTile(ti, tj, band);
                    }

                    if(isAbortRequested) break;

                    reportProgress();
                }

                if(isAbortRequested) break;
            }
        } else {
            //XXX decompressor.beginDecoding();

            for (int tj = minTileY; tj <= maxTileY; tj++) {
                for (int ti = minTileX; ti <= maxTileX; ti++) {
                    // The method abortRequested() is synchronized
                    // so check it only once per loop just before
                    // doing any actual decoding.
                    if(abortRequested()) {
                        isAbortRequested = true;
                        break;
                    }

                    decodeTile(ti, tj, -1);

                    reportProgress();
                }

                if(isAbortRequested) break;
            }
        }

        if (isAbortRequested) {
            processReadAborted();
        } else {
            processImageComplete();
        }

        return theImage;
    }

    public void reset() {
        super.reset();
        resetLocal();
    }

    protected void resetLocal() {
        stream = null;
        gotHeader = false;
        imageReadParam = getDefaultReadParam();
        streamMetadata = null;
        currIndex = -1;
        imageMetadata = null;
        imageStartPosition = new ArrayList();
        numImages = -1;
        imageTypeMap = new HashMap();
        width = -1;
        height = -1;
        numBands = -1;
        tileOrStripWidth = -1;
        tileOrStripHeight = -1;
        planarConfiguration = BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY;
        rowsDone = 0;
    }

    /**
     * Package scope method to allow decompressors, for example, to
     * emit warning messages.
     */
    void forwardWarningMessage(String warning) {
        processWarningOccurred(warning);
    }
    
    protected static BufferedImage getDestination(ImageReadParam param,
                                                  Iterator imageTypes,
                                                  int width, int height)
            throws IIOException {
        if (imageTypes == null || !imageTypes.hasNext()) {
            throw new IllegalArgumentException("imageTypes null or empty!");
        }        
        
        BufferedImage dest = null;
        ImageTypeSpecifier imageType = null;

        // If param is non-null, use it
        if (param != null) {
            // Try to get the image itself
            dest = param.getDestination();
            if (dest != null) {
                return dest;
            }
        
            // No image, get the image type
            imageType = param.getDestinationType();
        }

        // No info from param, use fallback image type
        if (imageType == null) {
            Object o = imageTypes.next();
            if (!(o instanceof ImageTypeSpecifier)) {
                throw new IllegalArgumentException
                    ("Non-ImageTypeSpecifier retrieved from imageTypes!");
            }
            imageType = (ImageTypeSpecifier)o;
        } else {
            boolean foundIt = false;
            while (imageTypes.hasNext()) {
                ImageTypeSpecifier type =
                    (ImageTypeSpecifier)imageTypes.next();
                if (type.equals(imageType)) {
                    foundIt = true;
                    break;
                }
            }

            if (!foundIt) {
                throw new IIOException
                    ("Destination type from ImageReadParam does not match!");
            }
        }

        Rectangle srcRegion = new Rectangle(0,0,0,0);
        Rectangle destRegion = new Rectangle(0,0,0,0);
        computeRegions(param,
                       width,
                       height,
                       null,
                       srcRegion,
                       destRegion);
        
        int destWidth = destRegion.x + destRegion.width;
        int destHeight = destRegion.y + destRegion.height;
        // Create a new image based on the type specifier
        
        if ((long)destWidth*destHeight > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                ("width*height > Integer.MAX_VALUE!");
        }
        
        return imageType.createBufferedImage(destWidth, destHeight);
    }    
}
