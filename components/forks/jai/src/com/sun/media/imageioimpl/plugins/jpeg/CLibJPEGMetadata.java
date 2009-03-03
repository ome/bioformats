/*
 * $RCSfile: CLibJPEGMetadata.java,v $
 *
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Revision: 1.7 $
 * $Date: 2007/08/28 18:45:53 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.jpeg;

import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.EXIFGPSTagSet;
import com.sun.media.imageio.plugins.tiff.EXIFInteroperabilityTagSet;
import com.sun.media.imageio.plugins.tiff.EXIFParentTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.EXIFTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;

public class CLibJPEGMetadata extends IIOMetadata {
    // --- Constants ---

    static final String NATIVE_FORMAT = "javax_imageio_jpeg_image_1.0";
    // XXX Reference to a non-API J2SE class:
    static final String NATIVE_FORMAT_CLASS =
        "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat";

    static final String TIFF_FORMAT =
        "com_sun_media_imageio_plugins_tiff_image_1.0";
    static final String TIFF_FORMAT_CLASS =
        "com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadataFormat";

    // Marker codes from J2SE in numerically increasing order.

    /** For temporary use in arithmetic coding */
    static final int TEM = 0x01;

    // Codes 0x02 - 0xBF are reserved

    // SOF markers for Nondifferential Huffman coding
    /** Baseline DCT */
    static final int SOF0 = 0xC0;
    /** Extended Sequential DCT */
    static final int SOF1 = 0xC1;
    /** Progressive DCT */
    static final int SOF2 = 0xC2;
    /** Lossless Sequential */
    static final int SOF3 = 0xC3;

    /** Define Huffman Tables */
    static final int DHT = 0xC4;    

    // SOF markers for Differential Huffman coding
    /** Differential Sequential DCT */
    static final int SOF5 = 0xC5;
    /** Differential Progressive DCT */
    static final int SOF6 = 0xC6;
    /** Differential Lossless */
    static final int SOF7 = 0xC7;

    /** Reserved for JPEG extensions */
    static final int JPG = 0xC8;

    // SOF markers for Nondifferential arithmetic coding
    /** Extended Sequential DCT, Arithmetic coding */
    static final int SOF9 = 0xC9;
    /** Progressive DCT, Arithmetic coding */
    static final int SOF10 = 0xCA;
    /** Lossless Sequential, Arithmetic coding */
    static final int SOF11 = 0xCB;

    /** Define Arithmetic conditioning tables */
    static final int DAC = 0xCC;

    // SOF markers for Differential arithmetic coding
    /** Differential Sequential DCT, Arithmetic coding */
    static final int SOF13 = 0xCD;
    /** Differential Progressive DCT, Arithmetic coding */
    static final int SOF14 = 0xCE;
    /** Differential Lossless, Arithmetic coding */
    static final int SOF15 = 0xCF;

    // Restart Markers
    static final int RST0 = 0xD0;
    static final int RST1 = 0xD1;
    static final int RST2 = 0xD2;
    static final int RST3 = 0xD3;
    static final int RST4 = 0xD4;
    static final int RST5 = 0xD5;
    static final int RST6 = 0xD6;
    static final int RST7 = 0xD7;
    /** Number of restart markers */
    static final int RESTART_RANGE = 8;

    /** Start of Image */
    static final int SOI = 0xD8;
    /** End of Image */
    static final int EOI = 0xD9;
    /** Start of Scan */
    static final int SOS = 0xDA;

    /** Define Quantisation Tables */
    static final int DQT = 0xDB;

    /** Define Number of lines */
    static final int DNL = 0xDC;

    /** Define Restart Interval */
    static final int DRI = 0xDD;

    /** Define Heirarchical progression */
    static final int DHP = 0xDE;

    /** Expand reference image(s) */
    static final int EXP = 0xDF;

    // Application markers
    /** APP0 used by JFIF */
    static final int APP0 = 0xE0;
    static final int APP1 = 0xE1;
    static final int APP2 = 0xE2;
    static final int APP3 = 0xE3;
    static final int APP4 = 0xE4;
    static final int APP5 = 0xE5;
    static final int APP6 = 0xE6;
    static final int APP7 = 0xE7;
    static final int APP8 = 0xE8;
    static final int APP9 = 0xE9;
    static final int APP10 = 0xEA;
    static final int APP11 = 0xEB;
    static final int APP12 = 0xEC;
    static final int APP13 = 0xED;
    /** APP14 used by Adobe */
    static final int APP14 = 0xEE;
    static final int APP15 = 0xEF;

    // codes 0xF0 to 0xFD are reserved

    /** Comment marker */
    static final int COM = 0xFE;

    // Marker codes for JPEG-LS

    /** JPEG-LS SOF marker */
    // This was SOF48 in an earlier revision of the JPEG-LS specification.
    // "55" is the numerical value of SOF55 - SOF0 (= 247 - 192).
    static final int SOF55 = 0xF7;

    /** JPEG-LS parameters */
    static final int LSE = 0xF2;

    // Min and max APPn codes.
    static final int APPN_MIN = APP0;
    static final int APPN_MAX = APP15;

    // Min and max contiguous SOFn codes.
    static final int SOFN_MIN = SOF0;
    static final int SOFN_MAX = SOF15;

    // Min and Max RSTn codes.
    static final int RST_MIN = RST0;
    static final int RST_MAX = RST7;

    // Specific segment types defined as (code << 8) | X.
    static final int APP0_JFIF   = (APP0 << 8) | 0;
    static final int APP0_JFXX   = (APP0 << 8) | 1;
    static final int APP1_EXIF   = (APP1 << 8) | 0;
    static final int APP2_ICC    = (APP2 << 8) | 0;
    static final int APP14_ADOBE = (APP14 << 8) | 0;
    static final int UNKNOWN_MARKER = 0xffff;
    static final int SOF_MARKER = (SOF0 << 8) | 0;

    // Resolution unit types.
    static final int JFIF_RESUNITS_ASPECT = 0;
    static final int JFIF_RESUNITS_DPI = 1;
    static final int JFIF_RESUNITS_DPC = 2;

    // Thumbnail types
    static final int THUMBNAIL_JPEG = 0x10;
    static final int THUMBNAIL_PALETTE = 0x11;
    static final int THUMBNAIL_RGB = 0x12;

    // Adobe transform type.
    static final int ADOBE_TRANSFORM_UNKNOWN = 0;
    static final int ADOBE_TRANSFORM_YCC = 1;
    static final int ADOBE_TRANSFORM_YCCK = 2;

    // Zig-zag to natural re-ordering array.
    static final int [] zigzag = {
        0,  1,  5,  6, 14, 15, 27, 28,
        2,  4,  7, 13, 16, 26, 29, 42,
        3,  8, 12, 17, 25, 30, 41, 43,
        9, 11, 18, 24, 31, 40, 44, 53,
        10, 19, 23, 32, 39, 45, 52, 54,
        20, 22, 33, 38, 46, 51, 55, 60,
        21, 34, 37, 47, 50, 56, 59, 61,
        35, 36, 48, 49, 57, 58, 62, 63
    };

    // --- Static methods ---

    private static IIOImage getThumbnail(ImageInputStream stream, int len,
                                         int thumbnailType, int w, int h)
        throws IOException {

        IIOImage result;

        long startPos = stream.getStreamPosition();

        if(thumbnailType == THUMBNAIL_JPEG) {
            Iterator readers = ImageIO.getImageReaders(stream);
            if(readers == null || !readers.hasNext()) return null;
            ImageReader reader = (ImageReader)readers.next();
            reader.setInput(stream);
            BufferedImage image = reader.read(0, null);
            IIOMetadata metadata = null;
            try {
                metadata = reader.getImageMetadata(0);
            } catch(Exception e) {
                // Ignore it
            }
            result = new IIOImage(image, null, metadata);
        } else {
            int numBands;
            ColorModel cm;
            if(thumbnailType == THUMBNAIL_PALETTE) {
                if(len < 768 + w*h) {
                    return null;
                }

                numBands = 1;

                byte[] palette = new byte[768];
                stream.readFully(palette);
                byte[] r = new byte[256];
                byte[] g = new byte[256];
                byte[] b = new byte[256];
                for(int i = 0, off = 0; i < 256; i++) {
                    r[i] = palette[off++];
                    g[i] = palette[off++];
                    b[i] = palette[off++];
                }

                cm = new IndexColorModel(8, 256, r, g, b);
            } else {
                if(len < 3*w*h) {
                    return null;
                }

                numBands = 3;

                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                cm = new ComponentColorModel(cs, false, false,
                                             Transparency.OPAQUE,
                                             DataBuffer.TYPE_BYTE);
            }

            byte[] data = new byte[w*h*numBands];
            stream.readFully(data);
            DataBufferByte db = new DataBufferByte(data, data.length);
            WritableRaster wr =
                Raster.createInterleavedRaster(db, w, h, w*numBands, numBands,
                                               new int[] {0, 1, 2}, null);
            BufferedImage image = new BufferedImage(cm, wr, false, null);
            result = new IIOImage(image, null, null);
        }

        stream.seek(startPos + len);

        return result;
    }

    // --- Instance variables ---

    /** Whether the object may be edited. */
    private boolean isReadOnly = true;

    // APP0 JFIF marker segment parameters.
    boolean app0JFIFPresent;
    int majorVersion = 1;
    int minorVersion = 2;
    int resUnits; // (0 = aspect ratio; 1 = dots/inch; 2 = dots/cm)
    int Xdensity = 1;
    int Ydensity = 1;
    int thumbWidth = 0;
    int thumbHeight = 0;
    BufferedImage jfifThumbnail;

    // APP0 JFIF thumbnail(s).
    boolean app0JFXXPresent;
    List extensionCodes; // Integers 0x10, 0x11, 0x12
    List jfxxThumbnails; // IIOImages

    // APP2 ICC_PROFILE marker segment parameters.
    boolean app2ICCPresent;
    ICC_Profile profile = null;

    // DQT marker segment parameters.
    boolean dqtPresent;
    List qtables; // Each element is a List of QTables

    // DHT marker segment parameters.
    boolean dhtPresent;
    List htables; // Each element is a List of HuffmanTables

    // DRI marker segment parameters.
    boolean driPresent;
    int driInterval;

    // COM marker segment parameters.
    boolean comPresent;
    List comments; // byte[]s

    // Unknown marker segment parameters.
    boolean unknownPresent;
    List markerTags; // Integers
    List unknownData; // byte[] (NB: 'length' parameter is array length)

    // APP14 Adobe marker segment parameters.
    boolean app14AdobePresent;
    int version = 100;
    int flags0 = 0;
    int flags1 = 0;
    int transform; // 0 = Unknown, 1 = YCbCr, 2 = YCCK

    // SOF marker segment parameters.
    boolean sofPresent;
    int sofProcess;
    int samplePrecision = 8;
    int numLines;
    int samplesPerLine;
    int numFrameComponents;
    int[] componentId;
    int[] hSamplingFactor;
    int[] vSamplingFactor;
    int[] qtableSelector;

    // SOS marker segment parameters.
    boolean sosPresent;
    int numScanComponents;
    int[] componentSelector;
    int[] dcHuffTable;
    int[] acHuffTable;
    int startSpectralSelection;
    int endSpectralSelection;
    int approxHigh;
    int approxLow;

    // Embedded TIFF stream from EXIF segment.
    byte[] exifData = null;

    /** Marker codes in the order encountered. */
    private List markers = null; // List of Integer

    // Standard metadata variables.
    private boolean hasAlpha = false;

    // Agregated list of thumbnails: JFIF > JFXX > EXIF.
    private boolean thumbnailsInitialized = false;
    private List thumbnails = new ArrayList();

    CLibJPEGMetadata() {
        super(true, NATIVE_FORMAT, NATIVE_FORMAT_CLASS,
              new String[] {TIFF_FORMAT}, new String[] {TIFF_FORMAT_CLASS});
              
        this.isReadOnly = isReadOnly;
    }

    CLibJPEGMetadata(ImageInputStream stream)
        throws IIOException {
        this();

        try {
            initializeFromStream(stream);
        } catch(IOException e) {
            throw new IIOException("Cannot initialize JPEG metadata!", e);
        }
    }

    private class QTable {
        private static final int QTABLE_SIZE = 64;

        int elementPrecision;
        int tableID;
        JPEGQTable table;

        int length;

        QTable(ImageInputStream stream) throws IOException {
            elementPrecision = (int)stream.readBits(4);
            tableID = (int)stream.readBits(4);
            byte[] tmp = new byte[QTABLE_SIZE];
            stream.readFully(tmp);
            int[] data = new int[QTABLE_SIZE];
            for (int i = 0; i < QTABLE_SIZE; i++) {
                data[i] = tmp[zigzag[i]] & 0xff;
            }
            table = new JPEGQTable(data);
            length = data.length + 1;
        }
    }

    private class HuffmanTable {
        private static final int NUM_LENGTHS = 16;

        int tableClass;
        int tableID;
        JPEGHuffmanTable table;

        int length;

        HuffmanTable(ImageInputStream stream) throws IOException {
            tableClass = (int)stream.readBits(4);
            tableID = (int)stream.readBits(4);
            short[] lengths = new short[NUM_LENGTHS];
            for (int i = 0; i < NUM_LENGTHS; i++) {
                lengths[i] = (short)stream.read();
            }
            int numValues = 0;
            for (int i = 0; i < NUM_LENGTHS; i++) {
                numValues += lengths[i];
            }
            short[] values = new short[numValues];
            for (int i = 0; i < numValues; i++) {
                values[i] = (short)stream.read();
            }
            table = new JPEGHuffmanTable(lengths, values);

            length = 1 + NUM_LENGTHS + values.length;
        }
    }

    private synchronized void initializeFromStream(ImageInputStream iis)
        throws IOException {
        iis.mark();
        iis.setByteOrder(ByteOrder.BIG_ENDIAN);

        markers = new ArrayList();

        boolean isICCProfileValid = true;
        int numICCProfileChunks = 0;
        long[] iccProfileChunkOffsets = null;
        int[] iccProfileChunkLengths = null;

        while(true) {
            try {
                // 0xff denotes a potential marker.
                if(iis.read() == 0xff) {
                    // Get next byte.
                    int code = iis.read();

                    // Is a marker if and only if code not in {0x00, 0xff}.
                    // Continue to next marker if this is not a marker or if
                    // it is an empty marker.
                    if(code == 0x00 || code == 0xff ||
                       code == SOI || code == TEM ||
                       (code >= RST_MIN && code <= RST_MAX)) {
                        continue;
                    }

                    // If at the end, quit.
                    if(code == EOI) {
                        break;
                    }

                    // Get the content length.
                    int dataLength = iis.readUnsignedShort() - 2;

                    if(APPN_MIN <= code && code <= APPN_MAX) {
                        long pos = iis.getStreamPosition();
                        boolean appnAdded = false;

                        switch(code) {
                        case APP0:
                            if(dataLength >= 5) {
                                byte[] b = new byte[5];
                                iis.readFully(b);
                                String id = new String(b);
                                if(id.startsWith("JFIF") &&
                                   !app0JFIFPresent) {
                                    app0JFIFPresent = true;
                                    markers.add(new Integer(APP0_JFIF));
                                    majorVersion = iis.read();
                                    minorVersion = iis.read();
                                    resUnits = iis.read();
                                    Xdensity = iis.readUnsignedShort();
                                    Ydensity = iis.readUnsignedShort();
                                    thumbWidth = iis.read();
                                    thumbHeight = iis.read();
                                    if(thumbWidth > 0 && thumbHeight > 0) {
                                        IIOImage imiio =
                                            getThumbnail(iis, dataLength - 14,
                                                         THUMBNAIL_RGB,
                                                         thumbWidth,
                                                         thumbHeight);
                                        if(imiio != null) {
                                            jfifThumbnail = (BufferedImage)
                                                imiio.getRenderedImage();
                                        }
                                    }
                                    appnAdded = true;
                                } else if(id.startsWith("JFXX")) {
                                    if(!app0JFXXPresent) {
                                        extensionCodes = new ArrayList(1);
                                        jfxxThumbnails = new ArrayList(1);
                                        app0JFXXPresent = true;
                                    }
                                    markers.add(new Integer(APP0_JFXX));
                                    int extCode = iis.read();
                                    extensionCodes.add(new Integer(extCode));
                                    int w = 0, h = 0, offset = 6;
                                    if(extCode != THUMBNAIL_JPEG) {
                                        w = iis.read();
                                        h = iis.read();
                                        offset += 2;
                                    }
                                    IIOImage imiio =
                                        getThumbnail(iis, dataLength - offset,
                                                     extCode, w, h);
                                    if(imiio != null) {
                                        jfxxThumbnails.add(imiio);
                                    }
                                    appnAdded = true;
                                }
                            }
                            break;
                        case APP1:
                            if(dataLength >= 6) {
                                byte[] b = new byte[6];
                                iis.readFully(b);
                                if(b[0] == (byte)'E' &&
                                   b[1] == (byte)'x' &&
                                   b[2] == (byte)'i' &&
                                   b[3] == (byte)'f' &&
                                   b[4] == (byte)0 &&
                                   b[5] == (byte)0) {
                                    exifData = new byte[dataLength - 6];
                                    iis.readFully(exifData);
                                }
                            }
                        case APP2:
                            if(dataLength >= 12) {
                                byte[] b = new byte[12];
                                iis.readFully(b);
                                String id = new String(b);
                                if(id.startsWith("ICC_PROFILE")) {
                                    if(!isICCProfileValid) {
                                        iis.skipBytes(dataLength - 12);
                                        continue;
                                    }

                                    int chunkNum = iis.read();
                                    int numChunks = iis.read();
                                    if(numChunks == 0 ||
                                       chunkNum == 0 ||
                                       chunkNum > numChunks ||
                                       (app2ICCPresent &&
                                        (numChunks != numICCProfileChunks ||
                                         iccProfileChunkOffsets[chunkNum]
                                         != 0L))) {
                                        isICCProfileValid = false;
                                        iis.skipBytes(dataLength - 14);
                                        continue;
                                    }

                                    if(!app2ICCPresent) {
                                        app2ICCPresent = true;
                                        // Only flag one marker even though
                                        // multiple may be present.
                                        markers.add(new Integer(APP2_ICC));

                                        numICCProfileChunks = numChunks;

                                        if(numChunks == 1) {
                                            b = new byte[dataLength - 14];
                                            iis.readFully(b);
                                            profile =
                                                ICC_Profile.getInstance(b);
                                        } else {
                                            iccProfileChunkOffsets =
                                                new long[numChunks + 1];
                                            iccProfileChunkLengths =
                                                new int[numChunks + 1];
                                            iccProfileChunkOffsets[chunkNum] =
                                                iis.getStreamPosition();
                                            iccProfileChunkLengths[chunkNum] =
                                                dataLength - 14;
                                            iis.skipBytes(dataLength - 14);
                                        }
                                    } else {
                                        iccProfileChunkOffsets[chunkNum] =
                                            iis.getStreamPosition();
                                        iccProfileChunkLengths[chunkNum] =
                                            dataLength - 14;
                                        iis.skipBytes(dataLength - 14);
                                    }

                                    appnAdded = true;
                                }
                            }
                            break;
                        case APP14:
                            if(dataLength >= 5) {
                                byte[] b = new byte[5];
                                iis.readFully(b);
                                String id = new String(b);
                                if(id.startsWith("Adobe") &&
                                   !app14AdobePresent) { // Adobe segment
                                    app14AdobePresent = true;
                                    markers.add(new Integer(APP14_ADOBE));
                                    version = iis.readUnsignedShort();
                                    flags0 = iis.readUnsignedShort();
                                    flags1 = iis.readUnsignedShort();
                                    transform = iis.read();
                                    iis.skipBytes(dataLength - 12);
                                    appnAdded = true;
                                }
                            }
                            break;
                        default:
                            appnAdded = false;
                            break;
                        }

                        if(!appnAdded) {
                            iis.seek(pos);
                            addUnknownMarkerSegment(iis, code, dataLength);
                        }
                    } else if(code == DQT) {
                        if(!dqtPresent) {
                            dqtPresent = true;
                            qtables = new ArrayList(1);
                        }
                        markers.add(new Integer(DQT));
                        List l = new ArrayList(1);
                        do {
                            QTable t = new QTable(iis);
                            l.add(t);
                            dataLength -= t.length;
                        } while(dataLength > 0);
                        qtables.add(l);
                    } else if(code == DHT) {
                        if(!dhtPresent) {
                            dhtPresent = true;
                            htables = new ArrayList(1);
                        }
                        markers.add(new Integer(DHT));
                        List l = new ArrayList(1);
                        do {
                            HuffmanTable t = new HuffmanTable(iis);
                            l.add(t);
                            dataLength -= t.length;
                        } while(dataLength > 0);
                        htables.add(l);
                    } else if(code == DRI) {
                        if(!driPresent) {
                            driPresent = true;
                        }
                        markers.add(new Integer(DRI));
                        driInterval = iis.readUnsignedShort();
                    } else if(code == COM) {
                        if(!comPresent) {
                            comPresent = true;
                            comments = new ArrayList(1);
                        }
                        markers.add(new Integer(COM));
                        byte[] b = new byte[dataLength];
                        iis.readFully(b);
                        comments.add(b);
                    } else if((code >= SOFN_MIN && code <= SOFN_MAX) ||
                              code == SOF55) { // SOFn
                        if(!sofPresent) {
                            sofPresent = true;
                            sofProcess = code - SOFN_MIN;
                            samplePrecision = iis.read();
                            numLines = iis.readUnsignedShort();
                            samplesPerLine = iis.readUnsignedShort();
                            numFrameComponents = iis.read();
                            componentId = new int[numFrameComponents];
                            hSamplingFactor = new int[numFrameComponents];
                            vSamplingFactor = new int[numFrameComponents];
                            qtableSelector = new int[numFrameComponents];
                            for(int i = 0; i < numFrameComponents; i++) {
                                componentId[i] = iis.read();
                                hSamplingFactor[i] = (int)iis.readBits(4);
                                vSamplingFactor[i] = (int)iis.readBits(4);
                                qtableSelector[i] = iis.read();
                            }
                            markers.add(new Integer(SOF_MARKER));
                        }
                    } else if(code == SOS) {
                        if(!sosPresent) {
                            sosPresent = true;
                            numScanComponents = iis.read();
                            componentSelector = new int[numScanComponents];
                            dcHuffTable = new int[numScanComponents];
                            acHuffTable = new int[numScanComponents];
                            for(int i = 0; i < numScanComponents; i++) {
                                componentSelector[i] = iis.read();
                                dcHuffTable[i] = (int)iis.readBits(4);
                                acHuffTable[i] = (int)iis.readBits(4);
                            }
                            startSpectralSelection = iis.read();
                            endSpectralSelection = iis.read();
                            approxHigh = (int)iis.readBits(4);
                            approxLow = (int)iis.readBits(4);
                            markers.add(new Integer(SOS));
                        }
                        break;
                    } else { // Any other marker
                        addUnknownMarkerSegment(iis, code, dataLength);
                    }
                }
            } catch(EOFException eofe) {
                // XXX Should this be caught?
                break;
            }
        }

        if(app2ICCPresent && isICCProfileValid && profile == null) {
            int profileDataLength = 0;
            for(int i = 1; i <= numICCProfileChunks; i++) {
                if(iccProfileChunkOffsets[i] == 0L) {
                    isICCProfileValid = false;
                    break;
                }
                profileDataLength += iccProfileChunkLengths[i];
            }

            if(isICCProfileValid) {
                byte[] b = new byte[profileDataLength];
                int off = 0;
                for(int i = 1; i <= numICCProfileChunks; i++) {
                    iis.seek(iccProfileChunkOffsets[i]);
                    iis.read(b, off, iccProfileChunkLengths[i]);
                    off += iccProfileChunkLengths[i];
                }

                profile = ICC_Profile.getInstance(b);
            }
        }

        iis.reset();
    }

    private void addUnknownMarkerSegment(ImageInputStream stream,
                                         int code, int len)
        throws IOException {
        if(!unknownPresent) {
            unknownPresent = true;
            markerTags = new ArrayList(1);
            unknownData = new ArrayList(1);
        }
        markerTags.add(new Integer(code));
        byte[] b = new byte[len];
        stream.readFully(b);
        unknownData.add(b);
        markers.add(new Integer(UNKNOWN_MARKER));
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        } else if(formatName.equals(TIFF_FORMAT)) {
            return getTIFFTree();
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    public void mergeTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if(isReadOnly) {
            throw new IllegalStateException("isReadOnly() == true!");
        }
    }

    public void reset() {
        if(isReadOnly) {
            throw new IllegalStateException("isReadOnly() == true!");
        }
    }

    // Native tree method.

    private Node getNativeTree() {
        int jfxxIndex = 0;
        int dqtIndex = 0;
        int dhtIndex = 0;
        int comIndex = 0;
        int unknownIndex = 0;

        IIOMetadataNode root = new IIOMetadataNode(nativeMetadataFormatName);

        IIOMetadataNode JPEGvariety = new IIOMetadataNode("JPEGvariety");
        root.appendChild(JPEGvariety);

        IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
        root.appendChild(markerSequence);

        IIOMetadataNode app0JFIF = null;
        if(app0JFIFPresent || app0JFXXPresent || app2ICCPresent) {
            app0JFIF = new IIOMetadataNode("app0JFIF");
            app0JFIF.setAttribute("majorVersion",
                                  Integer.toString(majorVersion));
            app0JFIF.setAttribute("minorVersion",
                                  Integer.toString(minorVersion));
            app0JFIF.setAttribute("resUnits",
                                  Integer.toString(resUnits));
            app0JFIF.setAttribute("Xdensity",
                                  Integer.toString(Xdensity));
            app0JFIF.setAttribute("Ydensity",
                                  Integer.toString(Ydensity));
            app0JFIF.setAttribute("thumbWidth",
                                  Integer.toString(thumbWidth));
            app0JFIF.setAttribute("thumbHeight",
                                  Integer.toString(thumbHeight));
            JPEGvariety.appendChild(app0JFIF);
        }

        IIOMetadataNode JFXX = null;
        if(app0JFXXPresent) {
            JFXX = new IIOMetadataNode("JFXX");
            app0JFIF.appendChild(JFXX);
        }

        Iterator markerIter = markers.iterator();
        while(markerIter.hasNext()) {
            int marker = ((Integer)markerIter.next()).intValue();
            switch(marker) {
            case APP0_JFIF:
                // Do nothing: already handled above.
                break;
            case APP0_JFXX:
                IIOMetadataNode app0JFXX = new IIOMetadataNode("app0JFXX");
                Integer extensionCode = (Integer)extensionCodes.get(jfxxIndex);
                app0JFXX.setAttribute("extensionCode",
                                      extensionCode.toString());
                IIOMetadataNode JFIFthumb = null;
                switch(extensionCode.intValue()) {
                case THUMBNAIL_JPEG:
                    JFIFthumb = new IIOMetadataNode("JFIFthumbJPEG");
                    break;
                case THUMBNAIL_PALETTE:
                    JFIFthumb = new IIOMetadataNode("JFIFthumbPalette");
                    break;
                case THUMBNAIL_RGB:
                    JFIFthumb = new IIOMetadataNode("JFIFthumbRGB");
                    break;
                default:
                    // No JFIFthumb node will be appended.
                }
                if(JFIFthumb != null) {
                    IIOImage img = (IIOImage)jfxxThumbnails.get(jfxxIndex++);
                    if(extensionCode.intValue() == THUMBNAIL_JPEG) {
                        IIOMetadata thumbMetadata = img.getMetadata();
                        if(thumbMetadata != null) {
                            Node thumbTree =
                                thumbMetadata.getAsTree(nativeMetadataFormatName);
                            if(thumbTree instanceof IIOMetadataNode) {
                                IIOMetadataNode elt =
                                    (IIOMetadataNode)thumbTree;
                                NodeList elts =
                                    elt.getElementsByTagName("markerSequence");
                                if(elts.getLength() > 0) {
                                    JFIFthumb.appendChild(elts.item(0));
                                }
                            }
                        }
                    } else {
                        BufferedImage thumb =
                            (BufferedImage)img.getRenderedImage();
                        JFIFthumb.setAttribute("thumbWidth",
                                               Integer.toString(thumb.getWidth()));
                        JFIFthumb.setAttribute("thumbHeight",
                                               Integer.toString(thumb.getHeight()));
                    }
                    // Add thumbnail as a user object even though not in
                    // metadata specification.
                    JFIFthumb.setUserObject(img);
                    app0JFXX.appendChild(JFIFthumb);
                }
                JFXX.appendChild(app0JFXX);
                break;
            case APP2_ICC:
                IIOMetadataNode app2ICC = new IIOMetadataNode("app2ICC");
                app2ICC.setUserObject(profile);
                app0JFIF.appendChild(app2ICC);
                break;
            case DQT:
                IIOMetadataNode dqt = new IIOMetadataNode("dqt");
                List tables = (List)qtables.get(dqtIndex++);
                int numTables = tables.size();
                for(int j = 0; j < numTables; j++) {
                    IIOMetadataNode dqtable = new IIOMetadataNode("dqtable");
                    QTable t = (QTable)tables.get(j);
                    dqtable.setAttribute("elementPrecision",
                                         Integer.toString(t.elementPrecision));
                    dqtable.setAttribute("qtableId",
                                         Integer.toString(t.tableID));
                    dqtable.setUserObject(t.table);
                    dqt.appendChild(dqtable);                    
                }
                markerSequence.appendChild(dqt);
                break;
            case DHT:
                IIOMetadataNode dht = new IIOMetadataNode("dht");
                tables = (List)htables.get(dhtIndex++);
                numTables = tables.size();
                for(int j = 0; j < numTables; j++) {
                    IIOMetadataNode dhtable = new IIOMetadataNode("dhtable");
                    HuffmanTable t = (HuffmanTable)tables.get(j);
                    dhtable.setAttribute("class",
                                         Integer.toString(t.tableClass));
                    dhtable.setAttribute("htableId",
                                         Integer.toString(t.tableID));
                    dhtable.setUserObject(t.table);
                    dht.appendChild(dhtable);                    
                }
                markerSequence.appendChild(dht);
                break;
            case DRI:
                IIOMetadataNode dri = new IIOMetadataNode("dri");
                dri.setAttribute("interval", Integer.toString(driInterval));
                markerSequence.appendChild(dri);
                break;
            case COM:
                IIOMetadataNode com = new IIOMetadataNode("com");
                com.setUserObject(comments.get(comIndex++));
                markerSequence.appendChild(com);
                break;
            case UNKNOWN_MARKER:
                IIOMetadataNode unknown = new IIOMetadataNode("unknown");
                Integer markerTag = (Integer)markerTags.get(unknownIndex);
                unknown.setAttribute("MarkerTag", markerTag.toString());
                unknown.setUserObject(unknownData.get(unknownIndex++));
                markerSequence.appendChild(unknown);
                break;
            case APP14_ADOBE:
                IIOMetadataNode app14Adobe = new IIOMetadataNode("app14Adobe");
                app14Adobe.setAttribute("version", Integer.toString(version));
                app14Adobe.setAttribute("flags0", Integer.toString(flags0));
                app14Adobe.setAttribute("flags1", Integer.toString(flags1));
                app14Adobe.setAttribute("transform",
                                        Integer.toString(transform));
                markerSequence.appendChild(app14Adobe);
                break;
            case SOF_MARKER:
                IIOMetadataNode sof = new IIOMetadataNode("sof");
                sof.setAttribute("process", Integer.toString(sofProcess));
                sof.setAttribute("samplePrecision",
                                 Integer.toString(samplePrecision));
                sof.setAttribute("numLines", Integer.toString(numLines));
                sof.setAttribute("samplesPerLine",
                                 Integer.toString(samplesPerLine));
                sof.setAttribute("numFrameComponents",
                                 Integer.toString(numFrameComponents));
                for(int i = 0; i < numFrameComponents; i++) {
                    IIOMetadataNode componentSpec =
                        new IIOMetadataNode("componentSpec");
                    componentSpec.setAttribute("componentId",
                                               Integer.toString(componentId[i]));
                    componentSpec.setAttribute("HsamplingFactor",
                                               Integer.toString(hSamplingFactor[i]));
                    componentSpec.setAttribute("VsamplingFactor",
                                               Integer.toString(vSamplingFactor[i]));
                    componentSpec.setAttribute("QtableSelector",
                                               Integer.toString(qtableSelector[i]));
                    sof.appendChild(componentSpec);
                }
                markerSequence.appendChild(sof);
                break;
            case SOS:
                IIOMetadataNode sos = new IIOMetadataNode("sos");
                sos.setAttribute("numScanComponents",
                                 Integer.toString(numScanComponents));
                sos.setAttribute("startSpectralSelection",
                                 Integer.toString(startSpectralSelection));
                sos.setAttribute("endSpectralSelection",
                                 Integer.toString(endSpectralSelection));
                sos.setAttribute("approxHigh", Integer.toString(approxHigh));
                sos.setAttribute("approxLow", Integer.toString(approxLow));
                for(int i = 0; i < numScanComponents; i++) {
                    IIOMetadataNode scanComponentSpec =
                        new IIOMetadataNode("scanComponentSpec");
                    scanComponentSpec.setAttribute("componentSelector",
                                                   Integer.toString(componentSelector[i]));
                    scanComponentSpec.setAttribute("dcHuffTable",
                                                   Integer.toString(dcHuffTable[i]));
                    scanComponentSpec.setAttribute("acHuffTable",
                                                   Integer.toString(acHuffTable[i]));
                    sos.appendChild(scanComponentSpec);
                }
                markerSequence.appendChild(sos);
                break;
            }
        }

        return root;
    }

    // Standard tree node methods

    protected IIOMetadataNode getStandardChromaNode() {
        if(!sofPresent) {
            // No image, so no chroma
            return null;
        }

        IIOMetadataNode chroma = new IIOMetadataNode("Chroma");
        IIOMetadataNode csType = new IIOMetadataNode("ColorSpaceType");
        chroma.appendChild(csType);

        IIOMetadataNode numChanNode = new IIOMetadataNode("NumChannels");
        chroma.appendChild(numChanNode);
        numChanNode.setAttribute("value",
                                 Integer.toString(numFrameComponents));

        // Check JFIF presence.
        if(app0JFIFPresent) {
            if(numFrameComponents == 1) {
                csType.setAttribute("name", "GRAY");
            } else {
                csType.setAttribute("name", "YCbCr");
            }
            return chroma;
        }

        // How about an Adobe marker segment?
        if(app14AdobePresent){
            switch(transform) {
            case ADOBE_TRANSFORM_YCCK: // YCCK
                csType.setAttribute("name", "YCCK");
                break;
            case ADOBE_TRANSFORM_YCC: // YCC
                csType.setAttribute("name", "YCbCr");
                break;
            case ADOBE_TRANSFORM_UNKNOWN: // Unknown
                if(numFrameComponents == 3) {
                    csType.setAttribute("name", "RGB");
                } else if(numFrameComponents == 4) {
                    csType.setAttribute("name", "CMYK");
                }
                break;
            }
            return chroma;
        }

        // Initially assume no opacity.
        hasAlpha = false;

        // Neither marker.  Check components
        if(numFrameComponents < 3) {
            csType.setAttribute("name", "GRAY");
            if(numFrameComponents == 2) {
                hasAlpha = true;
            }
            return chroma;
        }

        boolean idsAreJFIF = true;

        for(int i = 0; i < componentId.length; i++) {
            int id = componentId[i];
            if((id < 1) || (id >= componentId.length)) {
                idsAreJFIF = false;
            }
        }
        
        if(idsAreJFIF) {
            csType.setAttribute("name", "YCbCr");
            if(numFrameComponents == 4) {
                hasAlpha = true;
            }
            return chroma;
        }

        // Check against the letters
        if(componentId[0] == 'R' &&
           componentId[1] == 'G' &&
           componentId[2] == 'B'){
            csType.setAttribute("name", "RGB");
            if(numFrameComponents == 4 && componentId[3] == 'A') {
                hasAlpha = true;
            }
            return chroma;
        }
        
        if(componentId[0] == 'Y' &&
           componentId[1] == 'C' &&
           componentId[2] == 'c'){
            csType.setAttribute("name", "PhotoYCC");
            if(numFrameComponents == 4 &&
               componentId[3] == 'A') {
                hasAlpha = true;
            }            
            return chroma;
        }
        
        // Finally, 3-channel subsampled are YCbCr, unsubsampled are RGB
        // 4-channel subsampled are YCbCrA, unsubsampled are CMYK

        boolean subsampled = false;

        int hfactor = hSamplingFactor[0];
        int vfactor = vSamplingFactor[0];

        for(int i = 1; i < componentId.length; i++) {
            if(hSamplingFactor[i] != hfactor ||
               vSamplingFactor[i] != vfactor){
                subsampled = true;
                break;
            }
        }

        if(subsampled) {
            csType.setAttribute("name", "YCbCr");
            if(numFrameComponents == 4) {
                hasAlpha = true;
            }
            return chroma;
        }
         
        // Not subsampled.  numFrameComponents < 3 is taken care of above
        if(numFrameComponents == 3) {
            csType.setAttribute("name", "RGB");
        } else {
            csType.setAttribute("name", "CMYK");
        }

        return chroma;
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode compression = null;

        if(sofPresent || sosPresent) {
            compression = new IIOMetadataNode("Compression");

            if(sofPresent) {
                // Process 55 is JPEG-LS, others are lossless JPEG.
                boolean isLossless =
                    sofProcess == 3 || sofProcess == 7 || sofProcess == 11 ||
                    sofProcess == 15 || sofProcess == 55;

                // CompressionTypeName
                IIOMetadataNode name =
                    new IIOMetadataNode("CompressionTypeName");
                String compressionType = isLossless ?
                    (sofProcess == 55 ? "JPEG-LS" : "JPEG-LOSSLESS") : "JPEG";
                name.setAttribute("value", compressionType);
                compression.appendChild(name);

                // Lossless - false
                IIOMetadataNode lossless = new IIOMetadataNode("Lossless");
                lossless.setAttribute("value", isLossless ? "true" : "false");
                compression.appendChild(lossless);
            }

            if(sosPresent) {
                IIOMetadataNode prog =
                    new IIOMetadataNode("NumProgressiveScans");
                prog.setAttribute("value", "1");
                compression.appendChild(prog);
            }
        }

        return compression;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        IIOMetadataNode orient = new IIOMetadataNode("ImageOrientation");
        orient.setAttribute("value", "normal");
        dim.appendChild(orient);

        if(app0JFIFPresent) {
            float aspectRatio;
            if(resUnits == JFIF_RESUNITS_ASPECT) {
                // Aspect ratio.
                aspectRatio = (float)Xdensity/(float)Ydensity;
            } else {
                // Density.
                aspectRatio = (float)Ydensity/(float)Xdensity;
            }
            IIOMetadataNode aspect = new IIOMetadataNode("PixelAspectRatio");
            aspect.setAttribute("value", Float.toString(aspectRatio));
            dim.insertBefore(aspect, orient);

            if(resUnits != JFIF_RESUNITS_ASPECT) {
                // 1 == dpi, 2 == dpc
                float scale = (resUnits == JFIF_RESUNITS_DPI) ? 25.4F : 10.0F;

                IIOMetadataNode horiz = 
                    new IIOMetadataNode("HorizontalPixelSize");
                horiz.setAttribute("value", 
                                   Float.toString(scale/Xdensity));
                dim.appendChild(horiz);

                IIOMetadataNode vert = 
                    new IIOMetadataNode("VerticalPixelSize");
                vert.setAttribute("value", 
                                  Float.toString(scale/Ydensity));
                dim.appendChild(vert);
            }
        }
        return dim;
    }

    protected IIOMetadataNode getStandardTextNode() {
        IIOMetadataNode text = null;
        if(comPresent) {
            text = new IIOMetadataNode("Text");
            Iterator iter = comments.iterator();
            while (iter.hasNext()) {
                IIOMetadataNode entry = new IIOMetadataNode("TextEntry");
                entry.setAttribute("keyword", "comment");
                byte[] data = (byte[])iter.next();
                try {
                    entry.setAttribute("value",
                                       new String(data, "ISO-8859-1"));
                } catch(UnsupportedEncodingException e) {
                    entry.setAttribute("value", new String(data));
                }
                text.appendChild(entry);
            }
        }
        return text;
    }

    // This method assumes that getStandardChromaNode() has already been
    // called to initialize hasAlpha.
    protected IIOMetadataNode getStandardTransparencyNode() {
        IIOMetadataNode trans = null;
        if (hasAlpha == true) {
            trans = new IIOMetadataNode("Transparency");
            IIOMetadataNode alpha = new IIOMetadataNode("Alpha");
            alpha.setAttribute("value", "nonpremultiplied"); // Always assume
            trans.appendChild(alpha);
        }
        return trans;
    }

    // TIFF tree method

    private Node getTIFFTree() {
        String metadataName = TIFF_FORMAT;

        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

        TIFFDirectory dir =
            new TIFFDirectory(new TIFFTagSet[] {
                base, EXIFParentTIFFTagSet.getInstance()
            }, null);

        if(sofPresent) {
            // sofProcess -> Compression ?
            int compression = BaselineTIFFTagSet.COMPRESSION_JPEG;
            TIFFField compressionField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_COMPRESSION),
                              compression);
            dir.addTIFFField(compressionField);

            // samplePrecision -> BitsPerSample
            char[] bitsPerSample = new char[numFrameComponents];
            Arrays.fill(bitsPerSample, (char)(samplePrecision & 0xff));
            TIFFField bitsPerSampleField =
                new TIFFField(
                              base.getTag(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE),
                              TIFFTag.TIFF_SHORT,
                              bitsPerSample.length,
                              bitsPerSample);
            dir.addTIFFField(bitsPerSampleField);

            // numLines -> ImageLength
            TIFFField imageLengthField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_IMAGE_LENGTH),
                              numLines);
            dir.addTIFFField(imageLengthField);

            // samplesPerLine -> ImageWidth
            TIFFField imageWidthField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_IMAGE_WIDTH),
                              samplesPerLine);
            dir.addTIFFField(imageWidthField);

            // numFrameComponents -> SamplesPerPixel
            TIFFField samplesPerPixelField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_SAMPLES_PER_PIXEL),
                              numFrameComponents);
            dir.addTIFFField(samplesPerPixelField);

            // componentId -> PhotometricInterpretation + ExtraSamples
            IIOMetadataNode chroma = getStandardChromaNode();
            if(chroma != null) {
                IIOMetadataNode csType =
                    (IIOMetadataNode)chroma.getElementsByTagName("ColorSpaceType").item(0);
                String name = csType.getAttribute("name");
                int photometricInterpretation = -1;
                if(name.equals("GRAY")) {
                    photometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO;
                } else if(name.equals("YCbCr") || name.equals("PhotoYCC")) {
                    // NOTE: PhotoYCC -> YCbCr
                    photometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR;
                } else if(name.equals("RGB")) {
                    photometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_RGB;
                } else if(name.equals("CMYK") || name.equals("YCCK")) {
                    // NOTE: YCCK -> CMYK
                    photometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CMYK;
                }

                if(photometricInterpretation != -1) {
                    TIFFField photometricInterpretationField =
                        new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION),
                                      photometricInterpretation);
                    dir.addTIFFField(photometricInterpretationField);
                }

                if(hasAlpha) {
                    char[] extraSamples =
                        new char[] {BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA};
                    TIFFField extraSamplesField =
                        new TIFFField(
                                      base.getTag(BaselineTIFFTagSet.TAG_EXTRA_SAMPLES),
                                      TIFFTag.TIFF_SHORT,
                                      extraSamples.length,
                                      extraSamples);
                    dir.addTIFFField(extraSamplesField);
                }
            } // chroma != null
        } // sofPresent

        // JFIF APP0 -> Resolution fields.
        if(app0JFIFPresent) {
            long[][] xResolution = new long[][] {{Xdensity, 1}};
            TIFFField XResolutionField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION),
                              TIFFTag.TIFF_RATIONAL,
                              1,
                              xResolution);
            dir.addTIFFField(XResolutionField);

            long[][] yResolution = new long[][] {{Ydensity, 1}};
            TIFFField YResolutionField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION),
                              TIFFTag.TIFF_RATIONAL,
                              1,
                              yResolution);
            dir.addTIFFField(YResolutionField);

            int resolutionUnit = BaselineTIFFTagSet.RESOLUTION_UNIT_NONE;
            switch(resUnits) {
            case JFIF_RESUNITS_ASPECT:
                resolutionUnit = BaselineTIFFTagSet.RESOLUTION_UNIT_NONE;
            case JFIF_RESUNITS_DPI:
                resolutionUnit = BaselineTIFFTagSet.RESOLUTION_UNIT_INCH;
                break;
            case JFIF_RESUNITS_DPC:
                resolutionUnit = BaselineTIFFTagSet.RESOLUTION_UNIT_CENTIMETER;
                break;
            }
            TIFFField ResolutionUnitField =
                new TIFFField(base.getTag
                              (BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
                              resolutionUnit);
            dir.addTIFFField(ResolutionUnitField);
        }

        // DQT + DHT -> JPEGTables.
        byte[] jpegTablesData = null;
        if(dqtPresent || dqtPresent) {
            // Determine length of JPEGTables data.
            int jpegTablesLength = 2; // SOI
            if(dqtPresent) {
                Iterator dqts = qtables.iterator();
                while(dqts.hasNext()) {
                    Iterator qtiter = ((List)dqts.next()).iterator();
                    while(qtiter.hasNext()) {
                        QTable qt = (QTable)qtiter.next();
                        jpegTablesLength += 4 + qt.length;
                    }
                }
            }
            if(dhtPresent) {
                Iterator dhts = htables.iterator();
                while(dhts.hasNext()) {
                    Iterator htiter = ((List)dhts.next()).iterator();
                    while(htiter.hasNext()) {
                        HuffmanTable ht = (HuffmanTable)htiter.next();
                        jpegTablesLength += 4 + ht.length;
                    }
                }
            }
            jpegTablesLength += 2; // EOI

            // Allocate space.
            jpegTablesData = new byte[jpegTablesLength];

            // SOI
            jpegTablesData[0] = (byte)0xff;
            jpegTablesData[1] = (byte)SOI;
            int jpoff = 2;

            if(dqtPresent) {
                Iterator dqts = qtables.iterator();
                while(dqts.hasNext()) {
                    Iterator qtiter = ((List)dqts.next()).iterator();
                    while(qtiter.hasNext()) {
                        jpegTablesData[jpoff++] = (byte)0xff;
                        jpegTablesData[jpoff++] = (byte)DQT;
                        QTable qt = (QTable)qtiter.next();
                        int qtlength = qt.length + 2;
                        jpegTablesData[jpoff++] =
                            (byte)((qtlength & 0xff00) >> 8);
                        jpegTablesData[jpoff++] = (byte)(qtlength & 0xff);
                        jpegTablesData[jpoff++] =
                            (byte)(((qt.elementPrecision & 0xf0) << 4) |
                                   (qt.tableID & 0x0f));
                        int[] table = qt.table.getTable();
                        int qlen = table.length;
                        for(int i = 0; i < qlen; i++) {
                            jpegTablesData[jpoff + zigzag[i]] = (byte)table[i];
                        }
                        jpoff += qlen;
                    }
                }
            }

            if(dhtPresent) {
                Iterator dhts = htables.iterator();
                while(dhts.hasNext()) {
                    Iterator htiter = ((List)dhts.next()).iterator();
                    while(htiter.hasNext()) {
                        jpegTablesData[jpoff++] = (byte)0xff;
                        jpegTablesData[jpoff++] = (byte)DHT;
                        HuffmanTable ht = (HuffmanTable)htiter.next();
                        int htlength = ht.length + 2;
                        jpegTablesData[jpoff++] =
                            (byte)((htlength & 0xff00) >> 8);
                        jpegTablesData[jpoff++] = (byte)(htlength & 0xff);
                        jpegTablesData[jpoff++] =
                            (byte)(((ht.tableClass & 0x0f) << 4) |
                                   (ht.tableID & 0x0f));
                        short[] lengths = ht.table.getLengths();
                        int numLengths = lengths.length;
                        for(int i = 0; i < numLengths; i++) {
                            jpegTablesData[jpoff++] = (byte)lengths[i];
                        }
                        short[] values = ht.table.getValues();
                        int numValues = values.length;
                        for(int i = 0; i < numValues; i++) {
                            jpegTablesData[jpoff++] = (byte)values[i];
                        }
                    }
                }
            }

            jpegTablesData[jpoff++] = (byte)0xff;
            jpegTablesData[jpoff] = (byte)EOI;            
        }
        if(jpegTablesData != null) {
            TIFFField JPEGTablesField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_JPEG_TABLES),
                              TIFFTag.TIFF_UNDEFINED,
                              jpegTablesData.length,
                              jpegTablesData);
            dir.addTIFFField(JPEGTablesField);
        }

        IIOMetadata tiffMetadata = dir.getAsMetadata();

        if(exifData != null) {
            try {
                Iterator tiffReaders =
                    ImageIO.getImageReadersByFormatName("TIFF");
                if(tiffReaders != null && tiffReaders.hasNext()) {
                    ImageReader tiffReader = (ImageReader)tiffReaders.next();
                    ByteArrayInputStream bais =
                        new ByteArrayInputStream(exifData);
                    ImageInputStream exifStream =
                        new MemoryCacheImageInputStream(bais);
                    tiffReader.setInput(exifStream);
                    IIOMetadata exifMetadata = tiffReader.getImageMetadata(0);
                    tiffMetadata.mergeTree(metadataName,
                                           exifMetadata.getAsTree(metadataName));
                    tiffReader.reset();
                }
            } catch(IOException ioe) {
                // Ignore it.
            }
        }

        return tiffMetadata.getAsTree(metadataName);
    }

    // Thumbnail methods

    private void initializeThumbnails() {
        synchronized(thumbnails) {
            if(!thumbnailsInitialized) {
                // JFIF/JFXX are not supposed to coexist in the same
                // JPEG stream but in reality sometimes they do.

                // JFIF thumbnail
                if(app0JFIFPresent && jfifThumbnail != null) {
                    thumbnails.add(jfifThumbnail);
                }

                // JFXX thumbnail(s)
                if(app0JFXXPresent && jfxxThumbnails != null) {
                    int numJFXX = jfxxThumbnails.size();
                    for(int i = 0; i < numJFXX; i++) {
                        IIOImage img = (IIOImage)jfxxThumbnails.get(i);
                        BufferedImage jfxxThumbnail =
                            (BufferedImage)img.getRenderedImage();
                        thumbnails.add(jfxxThumbnail);
                    }
                }

                // EXIF thumbnail
                if(exifData != null) {
                    try {
                        Iterator tiffReaders =
                            ImageIO.getImageReadersByFormatName("TIFF");
                        if(tiffReaders != null && tiffReaders.hasNext()) {
                            ImageReader tiffReader =
                                (ImageReader)tiffReaders.next();
                            ByteArrayInputStream bais =
                                new ByteArrayInputStream(exifData);
                            ImageInputStream exifStream =
                                new MemoryCacheImageInputStream(bais);
                            tiffReader.setInput(exifStream);
                            if(tiffReader.getNumImages(true) > 1) {
                                BufferedImage exifThumbnail =
                                    tiffReader.read(1, null);
                                thumbnails.add(exifThumbnail);
                            }
                            tiffReader.reset();
                        }
                    } catch(IOException ioe) {
                        // Ignore it.
                    }
                }

                thumbnailsInitialized = true;
            } // if(!thumbnailsInitialized)
        } // sychronized
    }

    int getNumThumbnails() throws IOException {
        initializeThumbnails();
        return thumbnails.size();
    }

    BufferedImage getThumbnail(int thumbnailIndex) throws IOException {
        if(thumbnailIndex < 0) {
            throw new IndexOutOfBoundsException("thumbnailIndex < 0!");
        }

        initializeThumbnails();

        if(thumbnailIndex >= thumbnails.size()) {
            throw new IndexOutOfBoundsException
                ("thumbnailIndex > getNumThumbnails()");
        }

        return (BufferedImage)thumbnails.get(thumbnailIndex);
    }
}
