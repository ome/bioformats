/*
 * $RCSfile: CLibPNGMetadata.java,v $
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
 * $Date: 2006/02/27 17:25:04 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.w3c.dom.Node;
import com.sun.medialib.codec.png.Decoder;
import com.sun.medialib.codec.png.Encoder;
import com.sun.medialib.codec.png.PNGChunk;
import com.sun.medialib.codec.png.PNGTextualData;
import com.sun.medialib.codec.jiio.mediaLibImage;

//
// Core J2SE problems fixed in this package:
// 5109146:
// PNG: Background color initialization from standard metadata is incomplete
// 5109114:
// PNG: Cannot set IHDR_bitDepth from standard metadata /Data/BitsPerSample
// 5106305:
// PNG standard to native image metadata conversion incorrect for pixel size
// 5106550:
// PNG writer merge standard metadata fails for TextEntry sans #IMPLIED
// attributes
// 5082756:
// Image I/O plug-ins set metadata boolean attributes to "true" or "false"
// 5105068:
// PNGImageWriter.convertImageMetadata() broken for non-PNGMetadata
//

/**
 */
public class CLibPNGMetadata extends IIOMetadata implements Cloneable {

    // package scope
    public static final String
        nativeMetadataFormatName = "javax_imageio_png_1.0";

    protected static final String nativeMetadataFormatClassName 
        = "com.sun.media.imageioimpl.plugins.png.CLibPNGMetadataFormat";

    // Color types for IHDR chunk
    public static final String[] IHDR_colorTypeNames = {
        "Grayscale", null, "RGB", "Palette",
        "GrayAlpha", null, "RGBAlpha"
    };

    public static final int[] IHDR_numChannels = {
        1, 0, 3, 3, 2, 0, 4
    };

    // Bit depths for IHDR chunk
    public static final String[] IHDR_bitDepths = {
        "1", "2", "4", "8", "16"
    };

    // Compression methods for IHDR chunk
    public static final String[] IHDR_compressionMethodNames = {
        "deflate"
    };

    // Filter methods for IHDR chunk
    public static final String[] IHDR_filterMethodNames = {
        "adaptive"
    };

    // Interlace methods for IHDR chunk
    public static final String[] IHDR_interlaceMethodNames = {
        "none", "adam7"
    };

    // Compression methods for iCCP chunk
    public static final String[] iCCP_compressionMethodNames = {
        "deflate"
    };

    // Compression methods for zTXt chunk
    public static final String[] zTXt_compressionMethodNames = {
        "deflate"
    };

    // "Unknown" unit for pHYs chunk
    public static final int PHYS_UNIT_UNKNOWN = 0;

    // "Meter" unit for pHYs chunk
    public static final int PHYS_UNIT_METER = 1;

    // Unit specifiers for pHYs chunk
    public static final String[] unitSpecifierNames = {
        "unknown", "meter"
    };

    // Rendering intents for sRGB chunk
    public static final String[] renderingIntentNames = {
        "Perceptual", // 0
        "Relative colorimetric", // 1
        "Saturation", // 2
        "Absolute colorimetric" // 3

    };

    // Color space types for Chroma->ColorSpaceType node
    public static final String[] colorSpaceTypeNames = {
        "GRAY", null, "RGB", "RGB",
        "GRAY", null, "RGB"
    };

    // BEGIN Definitions required for reading.

    // Critical chunks
    static final int IHDR_TYPE = chunkType("IHDR");
    static final int PLTE_TYPE = chunkType("PLTE");
    static final int IDAT_TYPE = chunkType("IDAT");
    static final int IEND_TYPE = chunkType("IEND");
    
    // Ancillary chunks
    static final int bKGD_TYPE = chunkType("bKGD");
    static final int cHRM_TYPE = chunkType("cHRM");
    static final int gAMA_TYPE = chunkType("gAMA");
    static final int hIST_TYPE = chunkType("hIST");
    static final int iCCP_TYPE = chunkType("iCCP");
    static final int iTXt_TYPE = chunkType("iTXt");
    static final int pHYs_TYPE = chunkType("pHYs");
    static final int sBIT_TYPE = chunkType("sBIT");
    static final int sPLT_TYPE = chunkType("sPLT");
    static final int sRGB_TYPE = chunkType("sRGB");
    static final int tEXt_TYPE = chunkType("tEXt");
    static final int tIME_TYPE = chunkType("tIME");
    static final int tRNS_TYPE = chunkType("tRNS");
    static final int zTXt_TYPE = chunkType("zTXt");

    static final int PNG_COLOR_GRAY = 0;
    static final int PNG_COLOR_RGB = 2;
    static final int PNG_COLOR_PALETTE = 3;
    static final int PNG_COLOR_GRAY_ALPHA = 4;
    static final int PNG_COLOR_RGB_ALPHA = 6;

    // END Definitions required for reading.

    // IHDR chunk
    public boolean IHDR_present;
    public int IHDR_width;
    public int IHDR_height;
    public int IHDR_bitDepth;
    public int IHDR_colorType;
    public int IHDR_compressionMethod;
    public int IHDR_filterMethod;
    public int IHDR_interlaceMethod; // 0 == none, 1 == adam7

    // PLTE chunk
    public boolean PLTE_present;
    public byte[] PLTE_red;
    public byte[] PLTE_green;
    public byte[] PLTE_blue;

    // bKGD chunk
    // If external (non-PNG sourced) data has red = green = blue,
    // always store it as gray and promote when writing
    public boolean bKGD_present;
    public int bKGD_colorType; // PNG_COLOR_GRAY, _RGB, or _PALETTE
    public int bKGD_index;
    public int bKGD_gray;
    public int bKGD_red;
    public int bKGD_green;
    public int bKGD_blue;

    // cHRM chunk
    public boolean cHRM_present;
    public int cHRM_whitePointX;
    public int cHRM_whitePointY;
    public int cHRM_redX;
    public int cHRM_redY;
    public int cHRM_greenX;
    public int cHRM_greenY;
    public int cHRM_blueX;
    public int cHRM_blueY;

    // gAMA chunk
    public boolean gAMA_present;
    public int gAMA_gamma;

    // hIST chunk
    public boolean hIST_present;
    public char[] hIST_histogram;

    // iCCP chunk
    public boolean iCCP_present;
    public String iCCP_profileName;
    public int iCCP_compressionMethod;
    public byte[] iCCP_compressedProfile;

    // iTXt chunk
    public ArrayList iTXt_keyword = new ArrayList(); // Strings
    public ArrayList iTXt_compressionFlag = new ArrayList(); // Integers
    public ArrayList iTXt_compressionMethod = new ArrayList(); // Integers
    public ArrayList iTXt_languageTag = new ArrayList(); // Strings
    public ArrayList iTXt_translatedKeyword = new ArrayList(); // Strings
    public ArrayList iTXt_text = new ArrayList(); // Strings

    // pHYs chunk
    public boolean pHYs_present;
    public int pHYs_pixelsPerUnitXAxis;
    public int pHYs_pixelsPerUnitYAxis;
    public int pHYs_unitSpecifier; // 0 == unknown, 1 == meter

    // sBIT chunk
    public boolean sBIT_present;
    public int sBIT_colorType; // PNG_COLOR_GRAY, _GRAY_ALPHA, _RGB, _RGB_ALPHA
    public int sBIT_grayBits;
    public int sBIT_redBits;
    public int sBIT_greenBits;
    public int sBIT_blueBits;
    public int sBIT_alphaBits;
    
    // sPLT chunk
    public boolean sPLT_present;
    public String sPLT_paletteName; // 1-79 characters
    public int sPLT_sampleDepth; // 8 or 16
    public int[] sPLT_red;
    public int[] sPLT_green;
    public int[] sPLT_blue;
    public int[] sPLT_alpha;
    public int[] sPLT_frequency;

    // sRGB chunk
    public boolean sRGB_present;
    public int sRGB_renderingIntent;

    // tEXt chunk
    public ArrayList tEXt_keyword = new ArrayList(); // 1-79 char Strings
    public ArrayList tEXt_text = new ArrayList(); // Strings

    // tIME chunk
    public boolean tIME_present;
    public int tIME_year;
    public int tIME_month;
    public int tIME_day;
    public int tIME_hour;
    public int tIME_minute;
    public int tIME_second;

    // tRNS chunk
    // If external (non-PNG sourced) data has red = green = blue,
    // always store it as gray and promote when writing
    public boolean tRNS_present;
    public int tRNS_colorType; // PNG_COLOR_GRAY, _RGB, or _PALETTE
    public byte[] tRNS_alpha; // May have fewer entries than PLTE_red, etc.
    public int tRNS_gray;
    public int tRNS_red;
    public int tRNS_green;
    public int tRNS_blue;

    // zTXt chunk
    public ArrayList zTXt_keyword = new ArrayList(); // Strings
    public ArrayList zTXt_compressionMethod = new ArrayList(); // Integers
    public ArrayList zTXt_text = new ArrayList(); // Strings

    // Unknown chunks
    public ArrayList unknownChunkType = new ArrayList(); // Strings
    public ArrayList unknownChunkData = new ArrayList(); // byte arrays

    /**
     * Converts its parameter to another <code>String</code> which contains
     * only printable Latin-1 characters but not leading, trailing, or
     * consecutive spaces.
     *
     * @param s the <code>String</code> to convert.
     * @return a printable Latin-1 <code>String</code> sans superfluous spaces.
     */
    static String toPrintableLatin1(String s) {
        // Pass a null right back.
        if(s == null) return null;

        // Get Latin-1 characters.
        byte[] data = null;
        try {
            data = s.getBytes("ISO-8859-1");
        } catch(UnsupportedEncodingException e) {
            // In theory this should not happen (assert).
            data = s.getBytes();
        }

        // Copy printable characters omitting leading spaces and
        // all but first trailing space.
	int len = 0;
	int prev = 0;
	for (int i = 0; i < data.length; i++) {
	    int d = data[i] & 0xFF;
	    if (prev == 32 && d == 32)
		continue; 
	    if ((d > 32 && d <=126) || (d >= 161 && d <=255) ||
                (d == 32 && len != 0))
		data[len++] = (byte)d;
	    prev = d;
	}

        // Return an empty string if no acceptable characters.
        if(len == 0) return "";

        // Omit trailing space, if any.
        if(data[len - 1] == 32) len--;

	return new String(data, 0, len);
    }

    public CLibPNGMetadata() {
        super(true, 
              nativeMetadataFormatName,
              nativeMetadataFormatClassName,
              null, null);
    }
    
    public CLibPNGMetadata(IIOMetadata metadata)
        throws IIOInvalidTreeException {

        this();

        if(metadata != null) {
            List formats = Arrays.asList(metadata.getMetadataFormatNames());

            if(formats.contains(nativeMetadataFormatName)) {
                // Initialize from native image metadata format.
                String format = nativeMetadataFormatName;
                setFromTree(format, metadata.getAsTree(format));
            } else if(metadata.isStandardMetadataFormatSupported()) {
                // Initialize from standard metadata form of the input tree.
                String format =
                    IIOMetadataFormatImpl.standardMetadataFormatName;
                setFromTree(format, metadata.getAsTree(format));
            }
        }
    }

    /**
     * Sets the instance variables of the IHDR and if necessary PLTE and
     * tRNS chunks. The <code>numBands</code> parameter is necessary since
     * we may only be writing a subset of the image bands.
     */
    public void initialize(ImageTypeSpecifier imageType,
                           int numBands,
                           ImageWriteParam param,
                           int interlaceMethod) {
        ColorModel colorModel = imageType.getColorModel();
        SampleModel sampleModel = imageType.getSampleModel();

        // Intialize IHDR_width and IHDR_height
        IHDR_width = sampleModel.getWidth();
        IHDR_height = sampleModel.getHeight();

        // Initialize IHDR_bitDepth
        int[] sampleSize = sampleModel.getSampleSize();
        int bitDepth = sampleSize[0];
        // Choose max bit depth over all channels
        // Fixes bug 4413109
        for (int i = 1; i < sampleSize.length; i++) {
            if (sampleSize[i] > bitDepth) {
		bitDepth = sampleSize[i];
            }
        }
	// Multi-channel images must have a bit depth of 8 or 16
	if (sampleSize.length > 1 && bitDepth < 8) {
	    bitDepth = 8;
	}
        
        // Round bit depth up to a power of 2
        if (bitDepth > 2 && bitDepth < 4) {
            bitDepth = 4;
        } else if (bitDepth > 4 && bitDepth < 8) {
            bitDepth = 8;
        } else if (bitDepth > 8 && bitDepth < 16) {
            bitDepth = 16;
        } else if (bitDepth > 16) {
            throw new RuntimeException("bitDepth > 16!");
        }
        IHDR_bitDepth = bitDepth;

        // Initialize IHDR_colorType
        if (colorModel instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel)colorModel;
            int size = icm.getMapSize();

            byte[] reds = new byte[size];
            icm.getReds(reds);
            byte[] greens = new byte[size];
            icm.getGreens(greens);
            byte[] blues = new byte[size];
            icm.getBlues(blues);

            // Determine whether the color tables are actually a gray ramp
            // if the color type has not been set previously
            boolean isGray = false;
            if (!IHDR_present ||
                (IHDR_colorType != PNG_COLOR_PALETTE)) {
                isGray = true;
                int scale = 255/((1 << IHDR_bitDepth) - 1);
                for (int i = 0; i < size; i++) {
                    byte red = reds[i];
                    if ((red != (byte)(i*scale)) ||
                        (red != greens[i]) ||
                        (red != blues[i])) {
                        isGray = false;
                        break;
                    }
                }
            }

            // Determine whether transparency exists
            boolean hasAlpha = colorModel.hasAlpha();

            byte[] alpha = null;
            if (hasAlpha) {
                alpha = new byte[size];
                icm.getAlphas(alpha);
            }

            if (isGray && hasAlpha) {
                IHDR_colorType = PNG_COLOR_GRAY_ALPHA;
            } else if (isGray) {
                IHDR_colorType = PNG_COLOR_GRAY;
            } else {
                IHDR_colorType = PNG_COLOR_PALETTE;

                // Initialize PLTE chunk
                PLTE_present = true;
                PLTE_red = (byte[])reds.clone();
                PLTE_green = (byte[])greens.clone();
                PLTE_blue = (byte[])blues.clone();

                if (hasAlpha) {
                    // Initialize tRNS chunk
                    tRNS_present = true;
                    tRNS_colorType = PNG_COLOR_PALETTE;
                    tRNS_alpha = (byte[])alpha.clone();
                }
            }
        } else {
            if (numBands == 1) {
                IHDR_colorType = PNG_COLOR_GRAY;
            } else if (numBands == 2) {
                IHDR_colorType = PNG_COLOR_GRAY_ALPHA;
            } else if (numBands == 3) {
                IHDR_colorType = PNG_COLOR_RGB;
            } else if (numBands == 4) {
                IHDR_colorType = PNG_COLOR_RGB_ALPHA;
            } else {
                throw new RuntimeException("Number of bands not 1-4!");
            }
        }

        // Initialize IHDR_compressionMethod and IHDR_filterMethod
        IHDR_compressionMethod = IHDR_filterMethod = 0; // Only supported value

        // Initialize IHDR_interlaceMethod
        if(param != null &&
           param.getProgressiveMode() == ImageWriteParam.MODE_DISABLED) {
            IHDR_interlaceMethod = 0; // No interlacing.
        } else if(param != null &&
                  param.getProgressiveMode() == ImageWriteParam.MODE_DEFAULT) {
            IHDR_interlaceMethod = 1; // Adam7
        } else {
            // param == null ||
            // param.getProgressiveMode() ==
            // ImageWriteParam.MODE_COPY_FROM_METADATA
            IHDR_interlaceMethod = interlaceMethod;
        }

        IHDR_present = true;
    }

    public boolean isReadOnly() {
        return false;
    }

    private ArrayList cloneBytesArrayList(ArrayList in) {
        if (in == null) {
            return null;
        } else {
            ArrayList list = new ArrayList(in.size());
            Iterator iter = in.iterator();
            while (iter.hasNext()) {
                Object o = iter.next();
                if (o == null) {
                    list.add(null);
                } else {
                    list.add(((byte[])o).clone());
                }
            }

            return list;
        }
    }

    // Deep clone
    public Object clone() {
        CLibPNGMetadata metadata;
        try {
            metadata = (CLibPNGMetadata)super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        
        // unknownChunkData needs deep clone
        metadata.unknownChunkData =
            cloneBytesArrayList(this.unknownChunkData);

        return metadata;
    }

    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    private Node getNativeTree() {
        IIOMetadataNode node = null; // scratch node
        IIOMetadataNode root = new IIOMetadataNode(nativeMetadataFormatName);
        
        // IHDR
        if (IHDR_present) {
            IIOMetadataNode IHDR_node = new IIOMetadataNode("IHDR");
            IHDR_node.setAttribute("width", Integer.toString(IHDR_width));
            IHDR_node.setAttribute("height", Integer.toString(IHDR_height));
            IHDR_node.setAttribute("bitDepth",
                                   Integer.toString(IHDR_bitDepth));
            IHDR_node.setAttribute("colorType",
                                   IHDR_colorTypeNames[IHDR_colorType]);
            // IHDR_compressionMethod must be 0 in PNG 1.1
            IHDR_node.setAttribute("compressionMethod",
                          IHDR_compressionMethodNames[IHDR_compressionMethod]);
            // IHDR_filterMethod must be 0 in PNG 1.1
            IHDR_node.setAttribute("filterMethod",
                                    IHDR_filterMethodNames[IHDR_filterMethod]);
            IHDR_node.setAttribute("interlaceMethod",
                              IHDR_interlaceMethodNames[IHDR_interlaceMethod]);
            root.appendChild(IHDR_node);
        }

        // PLTE
        if (PLTE_present) {
            IIOMetadataNode PLTE_node = new IIOMetadataNode("PLTE");
            int numEntries = PLTE_red.length;
            for (int i = 0; i < numEntries; i++) {
                IIOMetadataNode entry = new IIOMetadataNode("PLTEEntry");
                entry.setAttribute("index", Integer.toString(i));
                entry.setAttribute("red",
                                   Integer.toString(PLTE_red[i] & 0xff));
                entry.setAttribute("green",
                                   Integer.toString(PLTE_green[i] & 0xff));
                entry.setAttribute("blue",
                                   Integer.toString(PLTE_blue[i] & 0xff));
                PLTE_node.appendChild(entry);
            }

            root.appendChild(PLTE_node);
        }

        // bKGD
        if (bKGD_present) {
            IIOMetadataNode bKGD_node = new IIOMetadataNode("bKGD");
            
            if (bKGD_colorType == PNG_COLOR_PALETTE) {
                node = new IIOMetadataNode("bKGD_Palette");
                node.setAttribute("index", Integer.toString(bKGD_index));
            } else if (bKGD_colorType == PNG_COLOR_GRAY) {
                node = new IIOMetadataNode("bKGD_Grayscale");
                node.setAttribute("gray", Integer.toString(bKGD_gray));
            } else if (bKGD_colorType == PNG_COLOR_RGB) {
                node = new IIOMetadataNode("bKGD_RGB");
                node.setAttribute("red", Integer.toString(bKGD_red));
                node.setAttribute("green", Integer.toString(bKGD_green));
                node.setAttribute("blue", Integer.toString(bKGD_blue));
            }
            bKGD_node.appendChild(node);

            root.appendChild(bKGD_node);
        }

        // cHRM
        if (cHRM_present) {
            IIOMetadataNode cHRM_node = new IIOMetadataNode("cHRM");
            cHRM_node.setAttribute("whitePointX",
                              Integer.toString(cHRM_whitePointX));
            cHRM_node.setAttribute("whitePointY",
                              Integer.toString(cHRM_whitePointY));
            cHRM_node.setAttribute("redX", Integer.toString(cHRM_redX));
            cHRM_node.setAttribute("redY", Integer.toString(cHRM_redY));
            cHRM_node.setAttribute("greenX", Integer.toString(cHRM_greenX));
            cHRM_node.setAttribute("greenY", Integer.toString(cHRM_greenY));
            cHRM_node.setAttribute("blueX", Integer.toString(cHRM_blueX));
            cHRM_node.setAttribute("blueY", Integer.toString(cHRM_blueY));

            root.appendChild(cHRM_node);
        }

        // gAMA
        if (gAMA_present) {
            IIOMetadataNode gAMA_node = new IIOMetadataNode("gAMA");
            gAMA_node.setAttribute("value", Integer.toString(gAMA_gamma));

            root.appendChild(gAMA_node);
        }

        // hIST
        if (hIST_present) {
            IIOMetadataNode hIST_node = new IIOMetadataNode("hIST");

            for (int i = 0; i < hIST_histogram.length; i++) {
                IIOMetadataNode hist =
                    new IIOMetadataNode("hISTEntry");
                hist.setAttribute("index", Integer.toString(i));
                hist.setAttribute("value",
                                  Integer.toString(hIST_histogram[i]));
                hIST_node.appendChild(hist);
            }

            root.appendChild(hIST_node);
        }

        // iCCP
        if (iCCP_present) {
            IIOMetadataNode iCCP_node = new IIOMetadataNode("iCCP");
            iCCP_node.setAttribute("profileName", iCCP_profileName);
            iCCP_node.setAttribute("compressionMethod",
                          iCCP_compressionMethodNames[iCCP_compressionMethod]);

            Object profile = iCCP_compressedProfile;
            if (profile != null) {
                profile = ((byte[])profile).clone();
            }
            iCCP_node.setUserObject(profile);

            root.appendChild(iCCP_node);
        }

        // iTXt
        if (iTXt_keyword.size() > 0) {
            IIOMetadataNode iTXt_parent = new IIOMetadataNode("iTXt");
            for (int i = 0; i < iTXt_keyword.size(); i++) {
                Integer val;
                
                IIOMetadataNode iTXt_node = new IIOMetadataNode("iTXtEntry");
                iTXt_node.setAttribute("keyword", (String)iTXt_keyword.get(i));
                val = (Integer)iTXt_compressionFlag.get(i);
                iTXt_node.setAttribute("compressionFlag", val.toString());
                val = (Integer)iTXt_compressionMethod.get(i);
                iTXt_node.setAttribute("compressionMethod", val.toString());
                iTXt_node.setAttribute("languageTag",
                                       (String)iTXt_languageTag.get(i));
                iTXt_node.setAttribute("translatedKeyword",
                                       (String)iTXt_translatedKeyword.get(i));
                iTXt_node.setAttribute("text", (String)iTXt_text.get(i));
                
                iTXt_parent.appendChild(iTXt_node);
            }
            
            root.appendChild(iTXt_parent);
        }

        // pHYs
        if (pHYs_present) {
            IIOMetadataNode pHYs_node = new IIOMetadataNode("pHYs");
            pHYs_node.setAttribute("pixelsPerUnitXAxis",
                              Integer.toString(pHYs_pixelsPerUnitXAxis));
            pHYs_node.setAttribute("pixelsPerUnitYAxis",
                                   Integer.toString(pHYs_pixelsPerUnitYAxis));
            pHYs_node.setAttribute("unitSpecifier",
                                   unitSpecifierNames[pHYs_unitSpecifier]);

            root.appendChild(pHYs_node);
        }

        // sBIT
        if (sBIT_present) {
            IIOMetadataNode sBIT_node = new IIOMetadataNode("sBIT");

            if (sBIT_colorType == PNG_COLOR_GRAY) {
                node = new IIOMetadataNode("sBIT_Grayscale");
                node.setAttribute("gray",
                                  Integer.toString(sBIT_grayBits));
            } else if (sBIT_colorType == PNG_COLOR_GRAY_ALPHA) {
                node = new IIOMetadataNode("sBIT_GrayAlpha");
                node.setAttribute("gray",
                                  Integer.toString(sBIT_grayBits));
                node.setAttribute("alpha",
                                  Integer.toString(sBIT_alphaBits));
            } else if (sBIT_colorType == PNG_COLOR_RGB) {
                node = new IIOMetadataNode("sBIT_RGB");
                node.setAttribute("red",
                                  Integer.toString(sBIT_redBits));
                node.setAttribute("green",
                                  Integer.toString(sBIT_greenBits));
                node.setAttribute("blue",
                                  Integer.toString(sBIT_blueBits));
            } else if (sBIT_colorType == PNG_COLOR_RGB_ALPHA) {
                node = new IIOMetadataNode("sBIT_RGBAlpha");
                node.setAttribute("red",
                                  Integer.toString(sBIT_redBits));
                node.setAttribute("green",
                                  Integer.toString(sBIT_greenBits));
                node.setAttribute("blue",
                                  Integer.toString(sBIT_blueBits));
                node.setAttribute("alpha",
                                  Integer.toString(sBIT_alphaBits));
            } else if (sBIT_colorType == PNG_COLOR_PALETTE) {
                node = new IIOMetadataNode("sBIT_Palette");
                node.setAttribute("red",
                                  Integer.toString(sBIT_redBits));
                node.setAttribute("green",
                                  Integer.toString(sBIT_greenBits));
                node.setAttribute("blue",
                                  Integer.toString(sBIT_blueBits));
            }
            sBIT_node.appendChild(node);
                
            root.appendChild(sBIT_node);
        }

        // sPLT
        if (sPLT_present) {
            IIOMetadataNode sPLT_node = new IIOMetadataNode("sPLT");

            sPLT_node.setAttribute("name", sPLT_paletteName);
            sPLT_node.setAttribute("sampleDepth",
                                   Integer.toString(sPLT_sampleDepth));

            int numEntries = sPLT_red.length;
            for (int i = 0; i < numEntries; i++) {
                IIOMetadataNode entry = new IIOMetadataNode("sPLTEntry");
                entry.setAttribute("index", Integer.toString(i));
                entry.setAttribute("red", Integer.toString(sPLT_red[i]));
                entry.setAttribute("green", Integer.toString(sPLT_green[i]));
                entry.setAttribute("blue", Integer.toString(sPLT_blue[i]));
                entry.setAttribute("alpha", Integer.toString(sPLT_alpha[i]));
                entry.setAttribute("frequency",
                                  Integer.toString(sPLT_frequency[i]));
                sPLT_node.appendChild(entry);
            }

            root.appendChild(sPLT_node);
        }

        // sRGB
        if (sRGB_present) {
            IIOMetadataNode sRGB_node = new IIOMetadataNode("sRGB");
            sRGB_node.setAttribute("renderingIntent",
                                   renderingIntentNames[sRGB_renderingIntent]);

            root.appendChild(sRGB_node);
        }

        // tEXt
        if (tEXt_keyword.size() > 0) {
            IIOMetadataNode tEXt_parent = new IIOMetadataNode("tEXt");
            for (int i = 0; i < tEXt_keyword.size(); i++) {
                IIOMetadataNode tEXt_node = new IIOMetadataNode("tEXtEntry");
                tEXt_node.setAttribute("keyword" , (String)tEXt_keyword.get(i));
                tEXt_node.setAttribute("value" , (String)tEXt_text.get(i));
                
                tEXt_parent.appendChild(tEXt_node);
            }
                
            root.appendChild(tEXt_parent);
        }

        // tIME
        if (tIME_present) {
            IIOMetadataNode tIME_node = new IIOMetadataNode("tIME");
            tIME_node.setAttribute("year", Integer.toString(tIME_year));
            tIME_node.setAttribute("month", Integer.toString(tIME_month));
            tIME_node.setAttribute("day", Integer.toString(tIME_day));
            tIME_node.setAttribute("hour", Integer.toString(tIME_hour));
            tIME_node.setAttribute("minute", Integer.toString(tIME_minute));
            tIME_node.setAttribute("second", Integer.toString(tIME_second));

            root.appendChild(tIME_node);
        }

        // tRNS
        if (tRNS_present) {
            IIOMetadataNode tRNS_node = new IIOMetadataNode("tRNS");

            if (tRNS_colorType == PNG_COLOR_PALETTE) {
                node = new IIOMetadataNode("tRNS_Palette");
                
                for (int i = 0; i < tRNS_alpha.length; i++) {
                    IIOMetadataNode entry =
                        new IIOMetadataNode("tRNS_PaletteEntry");
                    entry.setAttribute("index", Integer.toString(i));
                    entry.setAttribute("alpha",
                                       Integer.toString(tRNS_alpha[i] & 0xff));
                    node.appendChild(entry);
                }
            } else if (tRNS_colorType == PNG_COLOR_GRAY) {
                node = new IIOMetadataNode("tRNS_Grayscale");
                node.setAttribute("gray", Integer.toString(tRNS_gray));
            } else if (tRNS_colorType == PNG_COLOR_RGB) {
                node = new IIOMetadataNode("tRNS_RGB");
                node.setAttribute("red", Integer.toString(tRNS_red));
                node.setAttribute("green", Integer.toString(tRNS_green));
                node.setAttribute("blue", Integer.toString(tRNS_blue));
            }
            tRNS_node.appendChild(node);
            
            root.appendChild(tRNS_node);
        }

        // zTXt
        if (zTXt_keyword.size() > 0) {
            IIOMetadataNode zTXt_parent = new IIOMetadataNode("zTXt");
            for (int i = 0; i < zTXt_keyword.size(); i++) {
                IIOMetadataNode zTXt_node = new IIOMetadataNode("zTXtEntry");
                zTXt_node.setAttribute("keyword", (String)zTXt_keyword.get(i));

                int cm = ((Integer)zTXt_compressionMethod.get(i)).intValue();
                zTXt_node.setAttribute("compressionMethod",
                                       zTXt_compressionMethodNames[cm]);

                zTXt_node.setAttribute("text", (String)zTXt_text.get(i));

                zTXt_parent.appendChild(zTXt_node);
            }

            root.appendChild(zTXt_parent);
        }
        
        // Unknown chunks
        if (unknownChunkType.size() > 0) {
            IIOMetadataNode unknown_parent =
                new IIOMetadataNode("UnknownChunks");
            for (int i = 0; i < unknownChunkType.size(); i++) {
                IIOMetadataNode unknown_node =
                    new IIOMetadataNode("UnknownChunk");
                unknown_node.setAttribute("type",
                                          (String)unknownChunkType.get(i));
                unknown_node.setUserObject((byte[])unknownChunkData.get(i));
                
                unknown_parent.appendChild(unknown_node);
            }
            
            root.appendChild(unknown_parent);
        }

        return root;
    }

    private int getNumChannels() {
        // Determine number of channels
        // Be careful about palette color with transparency
        int numChannels = IHDR_numChannels[IHDR_colorType];
        if (IHDR_colorType == PNG_COLOR_PALETTE &&
            tRNS_present && tRNS_colorType == IHDR_colorType) {
            numChannels = 4;
        }
        return numChannels;
    }

    public IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("ColorSpaceType");
        node.setAttribute("name", colorSpaceTypeNames[IHDR_colorType]);
        chroma_node.appendChild(node);

        node = new IIOMetadataNode("NumChannels");
        node.setAttribute("value", Integer.toString(getNumChannels()));
        chroma_node.appendChild(node);

        if (gAMA_present) {
            node = new IIOMetadataNode("Gamma");
            node.setAttribute("value", Float.toString(gAMA_gamma*1.0e-5F));
            chroma_node.appendChild(node);
        }

        node = new IIOMetadataNode("BlackIsZero");
        node.setAttribute("value", "TRUE");
        chroma_node.appendChild(node);

        if (PLTE_present) {
            boolean hasAlpha = tRNS_present &&
                (tRNS_colorType == PNG_COLOR_PALETTE);

            node = new IIOMetadataNode("Palette");
            for (int i = 0; i < PLTE_red.length; i++) {
                IIOMetadataNode entry =
                    new IIOMetadataNode("PaletteEntry");
                entry.setAttribute("index", Integer.toString(i));
                entry.setAttribute("red",
                                   Integer.toString(PLTE_red[i] & 0xff));
                entry.setAttribute("green",
                                   Integer.toString(PLTE_green[i] & 0xff));
                entry.setAttribute("blue",
                                   Integer.toString(PLTE_blue[i] & 0xff));
                if (hasAlpha) {
		    int alpha = (i < tRNS_alpha.length) ?
			(tRNS_alpha[i] & 0xff) : 255;
                    entry.setAttribute("alpha", Integer.toString(alpha));
                }
                node.appendChild(entry);
            }
            chroma_node.appendChild(node);
        }

        if (bKGD_present) {
            if (bKGD_colorType == PNG_COLOR_PALETTE) {
                node = new IIOMetadataNode("BackgroundIndex");
                node.setAttribute("value", Integer.toString(bKGD_index));
            } else {
                node = new IIOMetadataNode("BackgroundColor");
                int r, g, b;

                if (bKGD_colorType == PNG_COLOR_GRAY) {
                    r = g = b = bKGD_gray;
                } else {
                    r = bKGD_red;
                    g = bKGD_green;
                    b = bKGD_blue;
                }
                node.setAttribute("red", Integer.toString(r));
                node.setAttribute("green", Integer.toString(g));
                node.setAttribute("blue", Integer.toString(b));
            }
            chroma_node.appendChild(node);
        }

        return chroma_node;
    }

    public IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("CompressionTypeName");
        node.setAttribute("value", "deflate");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("Lossless");
        node.setAttribute("value", "TRUE");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("NumProgressiveScans");
        node.setAttribute("value",
                          (IHDR_interlaceMethod == 0) ? "1" : "7");
        compression_node.appendChild(node);

        return compression_node;
    }

    private String repeat(String s, int times) {
        if (times == 1) {
            return s;
        }
        StringBuffer sb = new StringBuffer((s.length() + 1)*times - 1);
        sb.append(s);
        for (int i = 1; i < times; i++) {
            sb.append(" ");
            sb.append(s);
        }
        return sb.toString();
    }

    public IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode data_node = new IIOMetadataNode("Data");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("PlanarConfiguration");
        node.setAttribute("value", "PixelInterleaved");
        data_node.appendChild(node);

        node = new IIOMetadataNode("SampleFormat");
        node.setAttribute("value",
                          IHDR_colorType == PNG_COLOR_PALETTE ?
                          "Index" : "UnsignedIntegral");
        data_node.appendChild(node);

        String bitDepth = Integer.toString(IHDR_bitDepth);
        node = new IIOMetadataNode("BitsPerSample");
        node.setAttribute("value", repeat(bitDepth, getNumChannels()));
        data_node.appendChild(node);

        if (sBIT_present) {
            node = new IIOMetadataNode("SignificantBitsPerSample");
            String sbits;
            if (sBIT_colorType == PNG_COLOR_GRAY ||
                sBIT_colorType == PNG_COLOR_GRAY_ALPHA) {
                sbits = Integer.toString(sBIT_grayBits);
            } else { // sBIT_colorType == PNG_COLOR_RGB ||
                     // sBIT_colorType == PNG_COLOR_RGB_ALPHA
                sbits = Integer.toString(sBIT_redBits) + " " + 
                    Integer.toString(sBIT_greenBits) + " " + 
                    Integer.toString(sBIT_blueBits);
            }

            if (sBIT_colorType == PNG_COLOR_GRAY_ALPHA ||
                sBIT_colorType == PNG_COLOR_RGB_ALPHA) {
                sbits += " " + Integer.toString(sBIT_alphaBits);
            }
            
            node.setAttribute("value", sbits);
            data_node.appendChild(node);
        }

        // SampleMSB

        return data_node;
    }

    public IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("PixelAspectRatio");
        // aspect ratio is pixel width/height which is the ratio of the
        // inverses of pixels per unit length.
        float ratio = pHYs_present ?
            (float)pHYs_pixelsPerUnitYAxis/pHYs_pixelsPerUnitXAxis : 1.0F;
        node.setAttribute("value", Float.toString(ratio));
        dimension_node.appendChild(node);
        
        node = new IIOMetadataNode("ImageOrientation");
        node.setAttribute("value", "Normal");
        dimension_node.appendChild(node);
        
        if (pHYs_present && pHYs_unitSpecifier == PHYS_UNIT_METER) {
            node = new IIOMetadataNode("HorizontalPixelSize");
            node.setAttribute("value",
                              Float.toString(1000.0F/pHYs_pixelsPerUnitXAxis));
            dimension_node.appendChild(node);

            node = new IIOMetadataNode("VerticalPixelSize");
            node.setAttribute("value",
                              Float.toString(1000.0F/pHYs_pixelsPerUnitYAxis));
            dimension_node.appendChild(node);
        }

        return dimension_node;
    }

    public IIOMetadataNode getStandardDocumentNode() {
        if (!tIME_present) {
            return null;
        }

        IIOMetadataNode document_node = new IIOMetadataNode("Document");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("ImageModificationTime");
        node.setAttribute("year", Integer.toString(tIME_year));
        node.setAttribute("month", Integer.toString(tIME_month));
        node.setAttribute("day", Integer.toString(tIME_day));
        node.setAttribute("hour", Integer.toString(tIME_hour));
        node.setAttribute("minute", Integer.toString(tIME_minute));
        node.setAttribute("second", Integer.toString(tIME_second));
        document_node.appendChild(node);

        return document_node;
    }

    public IIOMetadataNode getStandardTextNode() {
        int numEntries = tEXt_keyword.size() +
            iTXt_keyword.size() + zTXt_keyword.size();
        if (numEntries == 0) {
            return null;
        }

        IIOMetadataNode text_node = new IIOMetadataNode("Text");
        IIOMetadataNode node = null; // scratch node

        for (int i = 0; i < tEXt_keyword.size(); i++) {
            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", (String)tEXt_keyword.get(i));
            node.setAttribute("value", (String)tEXt_text.get(i));
            node.setAttribute("encoding", "ISO-8859-1");
            node.setAttribute("compression", "none");
            
            text_node.appendChild(node);
        }

        for (int i = 0; i < iTXt_keyword.size(); i++) {
            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", (String)iTXt_keyword.get(i));
            node.setAttribute("value", (String)iTXt_text.get(i));
            node.setAttribute("language",
                              (String)iTXt_languageTag.get(i));
            if (((Integer)iTXt_compressionFlag.get(i)).intValue() == 1) {
                node.setAttribute("compression", "deflate");
            } else {
                node.setAttribute("compression", "none");
            }
            
            text_node.appendChild(node);
        }

        for (int i = 0; i < zTXt_keyword.size(); i++) {
            node = new IIOMetadataNode("TextEntry");
            node.setAttribute("keyword", (String)zTXt_keyword.get(i));
            node.setAttribute("value", (String)zTXt_text.get(i));
            node.setAttribute("compression", "deflate");
            
            text_node.appendChild(node);
        }

        return text_node;
    }

    public IIOMetadataNode getStandardTransparencyNode() {
        IIOMetadataNode transparency_node =
            new IIOMetadataNode("Transparency");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("Alpha");
        boolean hasAlpha = 
            (IHDR_colorType == PNG_COLOR_RGB_ALPHA) ||
            (IHDR_colorType == PNG_COLOR_GRAY_ALPHA) ||
            (IHDR_colorType == PNG_COLOR_PALETTE &&
             tRNS_present &&
             (tRNS_colorType == IHDR_colorType) &&
             (tRNS_alpha != null));
        node.setAttribute("value", hasAlpha ? "nonpremultiplied" : "none"); 
        transparency_node.appendChild(node);

        if (tRNS_present) {
            if(tRNS_colorType == PNG_COLOR_RGB ||
               tRNS_colorType == PNG_COLOR_GRAY) {
                node = new IIOMetadataNode("TransparentColor");
                if (tRNS_colorType == PNG_COLOR_RGB) {
                    node.setAttribute("value",
                                      Integer.toString(tRNS_red) + " " +
                                      Integer.toString(tRNS_green) + " " +
                                      Integer.toString(tRNS_blue));
                } else if (tRNS_colorType == PNG_COLOR_GRAY) {
                    node.setAttribute("value", Integer.toString(tRNS_gray));
                }
                transparency_node.appendChild(node);
            }
        }

        return transparency_node;
    }

    // Shorthand for throwing an IIOInvalidTreeException
    private void fatal(Node node, String reason)
        throws IIOInvalidTreeException {
        throw new IIOInvalidTreeException(reason, node);
    }

    // Get an integer-valued attribute
    private int getIntAttribute(Node node, String name,
                                int defaultValue, boolean required)
        throws IIOInvalidTreeException {
        String value = getAttribute(node, name, null, required);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    // Get a float-valued attribute
    private float getFloatAttribute(Node node, String name,
                                    float defaultValue, boolean required)
        throws IIOInvalidTreeException {
        String value = getAttribute(node, name, null, required);
        if (value == null) {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }

    // Get a required integer-valued attribute
    private int getIntAttribute(Node node, String name)
        throws IIOInvalidTreeException {
        return getIntAttribute(node, name, -1, true);
    }

    // Get a required float-valued attribute
    private float getFloatAttribute(Node node, String name)
        throws IIOInvalidTreeException {
        return getFloatAttribute(node, name, -1.0F, true);
    }

    // Get a boolean-valued attribute
    private boolean getBooleanAttribute(Node node, String name,
                                        boolean defaultValue, 
                                        boolean required)
        throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }

        String value = attr.getNodeValue();

        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            fatal(node, "Attribute " + name + " must be 'true' or 'false'!");
            return false;
        }
    }

    // Get a required boolean-valued attribute
    private boolean getBooleanAttribute(Node node, String name)
        throws IIOInvalidTreeException {
        return getBooleanAttribute(node, name, false, true);
    }

    // Get an enumerated attribute as an index into a String array
    private int getEnumeratedAttribute(Node node,
                                       String name, String[] legalNames,
                                       int defaultValue, boolean required)
        throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }

        String value = attr.getNodeValue();

        for (int i = 0; i < legalNames.length; i++) {
            if (value.equals(legalNames[i])) {
                return i;
            }
        }

        fatal(node, "Illegal value for attribute " + name + "!");
        return -1;
    }

    // Get a required enumerated attribute as an index into a String array
    private int getEnumeratedAttribute(Node node,
                                       String name, String[] legalNames)
        throws IIOInvalidTreeException {
        return getEnumeratedAttribute(node, name, legalNames, -1, true);
    }

    // Get a String-valued attribute
    private String getAttribute(Node node, String name,
                                String defaultValue, boolean required)
        throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }
        return attr.getNodeValue();
    }

    // Get a required String-valued attribute
    private String getAttribute(Node node, String name)
        throws IIOInvalidTreeException {
            return getAttribute(node, name, null, true);
    }

    public void mergeTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if (formatName.equals(nativeMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeNativeTree(root);
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeStandardTree(root);
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    private void mergeNativeTree(Node root)
        throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName().equals(nativeMetadataFormatName)) {
            fatal(node, "Root must be " + nativeMetadataFormatName);
        }
        
        node = node.getFirstChild();
        while (node != null) {
            String name = node.getNodeName();
            
            if (name.equals("IHDR")) {
                IHDR_width = getIntAttribute(node, "width");
                IHDR_height = getIntAttribute(node, "height");
                IHDR_bitDepth = getEnumeratedAttribute(node, "bitDepth",
                                                       IHDR_bitDepths);
                IHDR_colorType = getEnumeratedAttribute(node, "colorType",
                                                        IHDR_colorTypeNames);
                IHDR_compressionMethod =
                    getEnumeratedAttribute(node, "compressionMethod",
                                           IHDR_compressionMethodNames);
                IHDR_filterMethod =
                    getEnumeratedAttribute(node,
                                           "filterMethod",
                                           IHDR_filterMethodNames);
                IHDR_interlaceMethod =
                    getEnumeratedAttribute(node, "interlaceMethod",
                                           IHDR_interlaceMethodNames);
                IHDR_present = true;
            } else if (name.equals("PLTE")) {
                byte[] red = new byte[256];
                byte[] green  = new byte[256];
                byte[] blue = new byte[256];
                int maxindex = -1;
                
                Node PLTE_entry = node.getFirstChild();
                if (PLTE_entry == null) {
                    fatal(node, "Palette has no entries!");
                }

                while (PLTE_entry != null) {
                    if (!PLTE_entry.getNodeName().equals("PLTEEntry")) {
                        fatal(node,
                              "Only a PLTEEntry may be a child of a PLTE!");
                    }
                    
                    int index = getIntAttribute(PLTE_entry, "index");
                    if (index < 0 || index > 255) {
                        fatal(node,
                              "Bad value for PLTEEntry attribute index!");
                    }
                    if (index > maxindex) {
                        maxindex = index;
                    }
                    red[index] =
                        (byte)getIntAttribute(PLTE_entry, "red");
                    green[index] =
                        (byte)getIntAttribute(PLTE_entry, "green");
                    blue[index] =
                        (byte)getIntAttribute(PLTE_entry, "blue");
                    
                    PLTE_entry = PLTE_entry.getNextSibling();
                }
                
                int numEntries = maxindex + 1;
                PLTE_red = new byte[numEntries];
                PLTE_green = new byte[numEntries];
                PLTE_blue = new byte[numEntries];
                System.arraycopy(red, 0, PLTE_red, 0, numEntries);
                System.arraycopy(green, 0, PLTE_green, 0, numEntries);
                System.arraycopy(blue, 0, PLTE_blue, 0, numEntries);
                PLTE_present = true;
            } else if (name.equals("bKGD")) {
                bKGD_present = false; // Guard against partial overwrite
                Node bKGD_node = node.getFirstChild();
                if (bKGD_node == null) {
                    fatal(node, "bKGD node has no children!");
                }
                String bKGD_name = bKGD_node.getNodeName();
                if (bKGD_name.equals("bKGD_Palette")) {
                    bKGD_index = getIntAttribute(bKGD_node, "index");
                    bKGD_colorType = PNG_COLOR_PALETTE;
                } else if (bKGD_name.equals("bKGD_Grayscale")) {
                    bKGD_gray = getIntAttribute(bKGD_node, "gray");
                    bKGD_colorType = PNG_COLOR_GRAY;
                } else if (bKGD_name.equals("bKGD_RGB")) {
                    bKGD_red = getIntAttribute(bKGD_node, "red");
                    bKGD_green = getIntAttribute(bKGD_node, "green");
                    bKGD_blue = getIntAttribute(bKGD_node, "blue");
                    bKGD_colorType = PNG_COLOR_RGB;
                } else {
                    fatal(node, "Bad child of a bKGD node!");
                }
                if (bKGD_node.getNextSibling() != null) {
                    fatal(node, "bKGD node has more than one child!");
                }

                bKGD_present = true;
            } else if (name.equals("cHRM")) {
                cHRM_whitePointX = getIntAttribute(node, "whitePointX");
                cHRM_whitePointY = getIntAttribute(node, "whitePointY");
                cHRM_redX = getIntAttribute(node, "redX");
                cHRM_redY = getIntAttribute(node, "redY");
                cHRM_greenX = getIntAttribute(node, "greenX");
                cHRM_greenY = getIntAttribute(node, "greenY");
                cHRM_blueX = getIntAttribute(node, "blueX");
                cHRM_blueY = getIntAttribute(node, "blueY");
                
                cHRM_present = true;
            } else if (name.equals("gAMA")) {
                gAMA_gamma = getIntAttribute(node, "value");
                gAMA_present = true;
            } else if (name.equals("hIST")) {
                char[] hist = new char[256];
                int maxindex = -1;
                
                Node hIST_entry = node.getFirstChild();
                if (hIST_entry == null) {
                    fatal(node, "hIST node has no children!");
                }

                while (hIST_entry != null) {
                    if (!hIST_entry.getNodeName().equals("hISTEntry")) {
                        fatal(node,
                              "Only a hISTEntry may be a child of a hIST!");
                    }
                    
                    int index = getIntAttribute(hIST_entry, "index");
                    if (index < 0 || index > 255) {
                        fatal(node,
                              "Bad value for histEntry attribute index!");
                    }
                    if (index > maxindex) {
                        maxindex = index;
                    }
                    hist[index] =
                        (char)getIntAttribute(hIST_entry, "value");
                    
                    hIST_entry = hIST_entry.getNextSibling();
                }
                
                int numEntries = maxindex + 1;
                hIST_histogram = new char[numEntries];
                System.arraycopy(hist, 0, hIST_histogram, 0, numEntries);
                
                hIST_present = true;
            } else if (name.equals("iCCP")) {
                iCCP_profileName =
                    toPrintableLatin1(getAttribute(node, "profileName"));
                iCCP_compressionMethod =
                    getEnumeratedAttribute(node, "compressionMethod",
                                           iCCP_compressionMethodNames);
                Object compressedProfile =
                    ((IIOMetadataNode)node).getUserObject();
                if (compressedProfile == null) {
                    fatal(node, "No ICCP profile present in user object!");
                }
                if (!(compressedProfile instanceof byte[])) {
                    fatal(node, "User object not a byte array!");
                }
                
                iCCP_compressedProfile =
                    (byte[])((byte[])compressedProfile).clone();
                
                iCCP_present = true;
            } else if (name.equals("iTXt")) {
                Node iTXt_node = node.getFirstChild();
                while (iTXt_node != null) {
                    if (!iTXt_node.getNodeName().equals("iTXtEntry")) {
                        fatal(node,
                              "Only an iTXtEntry may be a child of an iTXt!");
                    }
                    
                    String keyword =
                        toPrintableLatin1(getAttribute(iTXt_node, "keyword"));
                    iTXt_keyword.add(keyword);
                    
                    boolean compressionFlag =
                        getBooleanAttribute(iTXt_node, "compressionFlag");
                    iTXt_compressionFlag.add(new Boolean(compressionFlag));
                    
                    String compressionMethod =
                        getAttribute(iTXt_node, "compressionMethod");
                    iTXt_compressionMethod.add(compressionMethod);
                    
                    String languageTag =
                        getAttribute(iTXt_node, "languageTag");
                    iTXt_languageTag.add(languageTag); 
                    
                    String translatedKeyword =
                        getAttribute(iTXt_node, "translatedKeyword");
                    iTXt_translatedKeyword.add(translatedKeyword);
                    
                    String text = getAttribute(iTXt_node, "text");
                    iTXt_text.add(text);
                    
                    iTXt_node = iTXt_node.getNextSibling();
                }
            } else if (name.equals("pHYs")) {
                pHYs_pixelsPerUnitXAxis =
                    getIntAttribute(node, "pixelsPerUnitXAxis");
                pHYs_pixelsPerUnitYAxis =
                    getIntAttribute(node, "pixelsPerUnitYAxis");
                pHYs_unitSpecifier =
                    getEnumeratedAttribute(node, "unitSpecifier",
                                           unitSpecifierNames);
                
                pHYs_present = true;
            } else if (name.equals("sBIT")) {
                sBIT_present = false; // Guard against partial overwrite
                Node sBIT_node = node.getFirstChild();
                if (sBIT_node == null) {
                    fatal(node, "sBIT node has no children!");
                }
                String sBIT_name = sBIT_node.getNodeName();
                if (sBIT_name.equals("sBIT_Grayscale")) {
                    sBIT_grayBits = getIntAttribute(sBIT_node, "gray");
                    sBIT_colorType = PNG_COLOR_GRAY;
                } else if (sBIT_name.equals("sBIT_GrayAlpha")) {
                    sBIT_grayBits = getIntAttribute(sBIT_node, "gray");
                    sBIT_alphaBits = getIntAttribute(sBIT_node, "alpha");
                    sBIT_colorType = PNG_COLOR_GRAY_ALPHA;
                } else if (sBIT_name.equals("sBIT_RGB")) {
                    sBIT_redBits = getIntAttribute(sBIT_node, "red");
                    sBIT_greenBits = getIntAttribute(sBIT_node, "green");
                    sBIT_blueBits = getIntAttribute(sBIT_node, "blue");
                    sBIT_colorType = PNG_COLOR_RGB;
                } else if (sBIT_name.equals("sBIT_RGBAlpha")) {
                    sBIT_redBits = getIntAttribute(sBIT_node, "red");
                    sBIT_greenBits = getIntAttribute(sBIT_node, "green");
                    sBIT_blueBits = getIntAttribute(sBIT_node, "blue");
                    sBIT_alphaBits = getIntAttribute(sBIT_node, "alpha");
                    sBIT_colorType = PNG_COLOR_RGB_ALPHA;
                } else if (sBIT_name.equals("sBIT_Palette")) {
                    sBIT_redBits = getIntAttribute(sBIT_node, "red");
                    sBIT_greenBits = getIntAttribute(sBIT_node, "green");
                    sBIT_blueBits = getIntAttribute(sBIT_node, "blue");
                    sBIT_colorType = PNG_COLOR_PALETTE;
                } else {
                    fatal(node, "Bad child of an sBIT node!");
                }
                if (sBIT_node.getNextSibling() != null) {
                    fatal(node, "sBIT node has more than one child!");
                }

                sBIT_present = true;
            } else if (name.equals("sPLT")) {
                sPLT_paletteName =
                    toPrintableLatin1(getAttribute(node, "name"));
                sPLT_sampleDepth = getIntAttribute(node, "sampleDepth");
                
                int[] red = new int[256];
                int[] green  = new int[256];
                int[] blue = new int[256];
                int[] alpha = new int[256];
                int[] frequency = new int[256];
                int maxindex = -1;
                
                Node sPLT_entry = node.getFirstChild();
                if (sPLT_entry == null) {
                    fatal(node, "sPLT node has no children!");
                }

                while (sPLT_entry != null) {
                    if (!sPLT_entry.getNodeName().equals("sPLTEntry")) {
                        fatal(node,
                              "Only an sPLTEntry may be a child of an sPLT!");
                    }
                    
                    int index = getIntAttribute(sPLT_entry, "index");
                    if (index < 0 || index > 255) {
                        fatal(node,
                              "Bad value for PLTEEntry attribute index!");
                    }
                    if (index > maxindex) {
                        maxindex = index;
                    }
                    red[index] = getIntAttribute(sPLT_entry, "red");
                    green[index] = getIntAttribute(sPLT_entry, "green");
                    blue[index] = getIntAttribute(sPLT_entry, "blue");
                    alpha[index] = getIntAttribute(sPLT_entry, "alpha");
                    frequency[index] =
                        getIntAttribute(sPLT_entry, "frequency");
                    
                    sPLT_entry = sPLT_entry.getNextSibling();
                }
                
                int numEntries = maxindex + 1;
                sPLT_red = new int[numEntries];
                sPLT_green = new int[numEntries];
                sPLT_blue = new int[numEntries];
                sPLT_alpha = new int[numEntries];
                sPLT_frequency = new int[numEntries];
                System.arraycopy(red, 0, sPLT_red, 0, numEntries);
                System.arraycopy(green, 0, sPLT_green, 0, numEntries);
                System.arraycopy(blue, 0, sPLT_blue, 0, numEntries);
                System.arraycopy(alpha, 0, sPLT_alpha, 0, numEntries);
                System.arraycopy(frequency, 0,
                                 sPLT_frequency, 0, numEntries);
                
                sPLT_present = true;
            } else if (name.equals("sRGB")) {
                sRGB_renderingIntent =
                    getEnumeratedAttribute(node, "renderingIntent",
                                           renderingIntentNames);
                
                sRGB_present = true;
            } else if (name.equals("tEXt")) {
                Node tEXt_node = node.getFirstChild();
                while (tEXt_node != null) {
                    if (!tEXt_node.getNodeName().equals("tEXtEntry")) {
                        fatal(node,
                              "Only an tEXtEntry may be a child of an tEXt!");
                    }
                    
                    String keyword =
                        toPrintableLatin1(getAttribute(tEXt_node, "keyword"));
                    tEXt_keyword.add(keyword);
                    
                    String text = getAttribute(tEXt_node, "value");
                    tEXt_text.add(text);
                    
                    tEXt_node = tEXt_node.getNextSibling();
                }
            } else if (name.equals("tIME")) {
                tIME_year = getIntAttribute(node, "year");
                tIME_month = getIntAttribute(node, "month");
                tIME_day = getIntAttribute(node, "day");
                tIME_hour = getIntAttribute(node, "hour");
                tIME_minute = getIntAttribute(node, "minute");
                tIME_second = getIntAttribute(node, "second");
                
                tIME_present = true;
            } else if (name.equals("tRNS")) {
                tRNS_present = false; // Guard against partial overwrite
                Node tRNS_node = node.getFirstChild();
                if (tRNS_node == null) {
                    fatal(node, "tRNS node has no children!");
                }
                String tRNS_name = tRNS_node.getNodeName();
                if (tRNS_name.equals("tRNS_Palette")) {
                    byte[] alpha = new byte[256];
                    int maxindex = -1;
                    
                    Node tRNS_paletteEntry = tRNS_node.getFirstChild();
                    if (tRNS_paletteEntry == null) {
                        fatal(node, "tRNS_Palette node has no children!");
                    }
                    while (tRNS_paletteEntry != null) {
                        if (!tRNS_paletteEntry.getNodeName().equals(
                                                        "tRNS_PaletteEntry")) {
                            fatal(node,
                 "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!");
                        }
                        int index =
                            getIntAttribute(tRNS_paletteEntry, "index");
                        if (index < 0 || index > 255) {
                            fatal(node,
                           "Bad value for tRNS_PaletteEntry attribute index!");
                        }
                        if (index > maxindex) {
                            maxindex = index;
                        }
                        alpha[index] =
                            (byte)getIntAttribute(tRNS_paletteEntry,
                                                  "alpha");
                        
                        tRNS_paletteEntry =
                            tRNS_paletteEntry.getNextSibling();
                    }
                    
                    int numEntries = maxindex + 1;
                    tRNS_alpha = new byte[numEntries];
                    tRNS_colorType = PNG_COLOR_PALETTE;
                    System.arraycopy(alpha, 0, tRNS_alpha, 0, numEntries);
                } else if (tRNS_name.equals("tRNS_Grayscale")) {
                    tRNS_gray = getIntAttribute(tRNS_node, "gray");
                    tRNS_colorType = PNG_COLOR_GRAY;
                } else if (tRNS_name.equals("tRNS_RGB")) {
                    tRNS_red = getIntAttribute(tRNS_node, "red");
                    tRNS_green = getIntAttribute(tRNS_node, "green");
                    tRNS_blue = getIntAttribute(tRNS_node, "blue");
                    tRNS_colorType = PNG_COLOR_RGB;
                } else {
                    fatal(node, "Bad child of a tRNS node!");
                }
                if (tRNS_node.getNextSibling() != null) {
                    fatal(node, "tRNS node has more than one child!");
                }
                
                tRNS_present = true;
            } else if (name.equals("zTXt")) {
                Node zTXt_node = node.getFirstChild();
                while (zTXt_node != null) {
                    if (!zTXt_node.getNodeName().equals("zTXtEntry")) {
                        fatal(node,
                              "Only an zTXtEntry may be a child of an zTXt!");
                    }
                    
                    String keyword =
                        toPrintableLatin1(getAttribute(zTXt_node, "keyword"));
                    zTXt_keyword.add(keyword);
                    
                    int compressionMethod =
                        getEnumeratedAttribute(zTXt_node, "compressionMethod",
                                               zTXt_compressionMethodNames);
                    zTXt_compressionMethod.add(new Integer(compressionMethod));
                    
                    String text = getAttribute(zTXt_node, "text");
                    zTXt_text.add(text);
                    
                    zTXt_node = zTXt_node.getNextSibling();
                }
            } else if (name.equals("UnknownChunks")) {
                Node unknown_node = node.getFirstChild();
                while (unknown_node != null) {
                    if (!unknown_node.getNodeName().equals("UnknownChunk")) {
                        fatal(node,
                   "Only an UnknownChunk may be a child of an UnknownChunks!");
                    }
                    String chunkType = getAttribute(unknown_node, "type");
                    Object chunkData =
                        ((IIOMetadataNode)unknown_node).getUserObject();
                    
                    if (chunkType.length() != 4) {
                        fatal(unknown_node,
                              "Chunk type must be 4 characters!");
                    }
                    if (chunkData == null) {
                        fatal(unknown_node,
                              "No chunk data present in user object!");
                    }
                    if (!(chunkData instanceof byte[])) {
                        fatal(unknown_node,
                              "User object not a byte array!");
                    }
                    unknownChunkType.add(chunkType);
                    unknownChunkData.add(((byte[])chunkData).clone());
                    
                    unknown_node = unknown_node.getNextSibling();
                }
            } else {
                fatal(node, "Unknown child of root node!");
            }
            
            node = node.getNextSibling();
        }
    }

    private boolean isISOLatin(String s) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) > 255) {
                return false;
            }
        }
        return true;
    }

    private void mergeStandardTree(Node root)
        throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName()
            .equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            fatal(node, "Root must be " +
                  IIOMetadataFormatImpl.standardMetadataFormatName);
        }
        
        node = node.getFirstChild();
        while(node != null) {
            String name = node.getNodeName();

            if (name.equals("Chroma")) {
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("Gamma")) {
                        float gamma = getFloatAttribute(child, "value");
                        gAMA_present = true;
                        gAMA_gamma = (int)(gamma*100000 + 0.5);
                    } else if (childName.equals("Palette")) {
                        byte[] red = new byte[256];
                        byte[] green = new byte[256];
                        byte[] blue = new byte[256];
                        int maxindex = -1;
                
                        Node entry = child.getFirstChild();
                        while (entry != null) {
                            String entryName = entry.getNodeName();
                            if(entryName.equals("PaletteEntry")) {
                                int index = getIntAttribute(entry, "index");
                                if (index >= 0 && index <= 255) {
                                    red[index] =
                                        (byte)getIntAttribute(entry, "red");
                                    green[index] =
                                        (byte)getIntAttribute(entry, "green");
                                    blue[index] =
                                        (byte)getIntAttribute(entry, "blue");
                                    if (index > maxindex) {
                                        maxindex = index;
                                    }
                                }
                            }
                            entry = entry.getNextSibling();
                        }
                
                        int numEntries = maxindex + 1;
                        PLTE_red = new byte[numEntries];
                        PLTE_green = new byte[numEntries];
                        PLTE_blue = new byte[numEntries];
                        System.arraycopy(red, 0, PLTE_red, 0, numEntries);
                        System.arraycopy(green, 0, PLTE_green, 0, numEntries);
                        System.arraycopy(blue, 0, PLTE_blue, 0, numEntries);
                        PLTE_present = true;
                    } else if (childName.equals("BackgroundIndex")) {
                        bKGD_present = true;
                        bKGD_colorType = PNG_COLOR_PALETTE;
                        bKGD_index = getIntAttribute(child, "value");
                    } else if (childName.equals("BackgroundColor")) {
                        int red = getIntAttribute(child, "red");
                        int green = getIntAttribute(child, "green");
                        int blue = getIntAttribute(child, "blue");
                        if (red == green && red == blue) {
                            bKGD_colorType = PNG_COLOR_GRAY;
                            bKGD_gray = red;
                        } else {
                            bKGD_colorType = PNG_COLOR_RGB;
                            bKGD_red = red;
                            bKGD_green = green;
                            bKGD_blue = blue;
                        }
                        bKGD_present = true;
                    }
                    // } else if (childName.equals("ColorSpaceType")) {
                    // } else if (childName.equals("NumChannels")) {

                    child = child.getNextSibling();
                }
            } else if (name.equals("Compression")) {
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("NumProgressiveScans")) {
                        // Use Adam7 if NumProgressiveScans > 1
                        int scans = getIntAttribute(child, "value");
                        IHDR_interlaceMethod = (scans > 1) ? 1 : 0;
                        //                  } else if (childName.equals("CompressionTypeName")) {
                        //                  } else if (childName.equals("Lossless")) {
                        //                  } else if (childName.equals("BitRate")) {
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Data")) {
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("BitsPerSample")) {
                        String s = getAttribute(child, "value");
                        StringTokenizer t = new StringTokenizer(s);
                        int maxBits = -1;
                        while (t.hasMoreTokens()) {
                            int bits = Integer.parseInt(t.nextToken());
                            if (bits > maxBits) {
                                maxBits = bits;
                            }
                        }
                        if (maxBits < 1) {
                            maxBits = 1;
                        } else if (maxBits == 3) {
                            maxBits = 4;
                        } else if (maxBits > 4 && maxBits < 8) {
                            maxBits = 8;
                        } else if (maxBits > 8) {
                            maxBits = 16;
                        }
                        IHDR_bitDepth = maxBits;
                    } else if (childName.equals("SignificantBitsPerSample")) {
                        String s = getAttribute(child, "value");
                        StringTokenizer t = new StringTokenizer(s);
                        int numTokens = t.countTokens();
                        if (numTokens == 1) {
                            sBIT_colorType = PNG_COLOR_GRAY;
                            sBIT_grayBits = Integer.parseInt(t.nextToken());
                        } else if (numTokens == 2) {
                            sBIT_colorType =
                                PNG_COLOR_GRAY_ALPHA;
                            sBIT_grayBits = Integer.parseInt(t.nextToken());
                            sBIT_alphaBits = Integer.parseInt(t.nextToken());
                        } else if (numTokens == 3) {
                            sBIT_colorType = PNG_COLOR_RGB;
                            sBIT_redBits = Integer.parseInt(t.nextToken());
                            sBIT_greenBits = Integer.parseInt(t.nextToken());
                            sBIT_blueBits = Integer.parseInt(t.nextToken());
                        } else if (numTokens == 4) {
                            sBIT_colorType =
                                PNG_COLOR_RGB_ALPHA;
                            sBIT_redBits = Integer.parseInt(t.nextToken());
                            sBIT_greenBits = Integer.parseInt(t.nextToken());
                            sBIT_blueBits = Integer.parseInt(t.nextToken());
                            sBIT_alphaBits = Integer.parseInt(t.nextToken());
                        }
                        if (numTokens >= 1 && numTokens <= 4) {
                            sBIT_present = true;
                        }
                        // } else if (childName.equals("PlanarConfiguration")) {
                        // } else if (childName.equals("SampleFormat")) {
                        // } else if (childName.equals("SampleMSB")) {
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Dimension")) {
                boolean gotWidth = false;
                boolean gotHeight = false;
                boolean gotAspectRatio = false;

                float width = -1.0F;
                float height = -1.0F;
                float aspectRatio = -1.0F;
                
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("PixelAspectRatio")) {
                        aspectRatio = getFloatAttribute(child, "value");
                        gotAspectRatio = true;
                    } else if (childName.equals("HorizontalPixelSize")) {
                        width = getFloatAttribute(child, "value");
                        gotWidth = true;
                    } else if (childName.equals("VerticalPixelSize")) {
                        height = getFloatAttribute(child, "value");
                        gotHeight = true;
                        // } else if (childName.equals("ImageOrientation")) {
                        // } else if
                        //     (childName.equals("HorizontalPhysicalPixelSpacing")) {
                        // } else if
                        //     (childName.equals("VerticalPhysicalPixelSpacing")) {
                        // } else if (childName.equals("HorizontalPosition")) {
                        // } else if (childName.equals("VerticalPosition")) {
                        // } else if (childName.equals("HorizontalPixelOffset")) {
                        // } else if (childName.equals("VerticalPixelOffset")) {
                    }
                    child = child.getNextSibling();
                }

                if (gotWidth && gotHeight) {
                    pHYs_present = true;
                    pHYs_unitSpecifier = 1;
                    pHYs_pixelsPerUnitXAxis = (int)(1000.0F/width + 0.5F);
                    pHYs_pixelsPerUnitYAxis = (int)(1000.0F/height + 0.5F);
                } else if (gotAspectRatio) {
                    pHYs_present = true;
                    pHYs_unitSpecifier = 0;

                    // Find a reasonable rational approximation
                    int denom = 1;
                    for (; denom < 100; denom++) {
                        int num = (int)(aspectRatio*denom);
                        if (Math.abs(num/denom - aspectRatio) < 0.001) {
                            break;
                        }
                    }
                    pHYs_pixelsPerUnitXAxis = (int)(aspectRatio*denom);
                    pHYs_pixelsPerUnitYAxis = denom;
                }
            } else if (name.equals("Document")) {
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("ImageModificationTime")) {
                        tIME_present = true;
                        tIME_year = getIntAttribute(child, "year");
                        tIME_month = getIntAttribute(child, "month");
                        tIME_day = getIntAttribute(child, "day");
                        tIME_hour =
                            getIntAttribute(child, "hour", 0, false);
                        tIME_minute =
                            getIntAttribute(child, "minute", 0, false);
                        tIME_second =
                            getIntAttribute(child, "second", 0, false);
                        // } else if (childName.equals("SubimageInterpretation")) {
                        // } else if (childName.equals("ImageCreationTime")) {
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Text")) {
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("TextEntry")) {
                        String keyword = getAttribute(child, "keyword",
                                                      "text", false);
                        String value = getAttribute(child, "value");
                        String encoding = getAttribute(child, "encoding",
                                                       "unknown", false);
                        String language = getAttribute(child, "language",
                                                       "unknown", false);
                        String compression =
                            getAttribute(child, "compression",
                                         "other", false);

                        if (isISOLatin(value)) {
                            if (compression.equals("zip")) {
                                // Use a zTXt node
                                zTXt_keyword.add(toPrintableLatin1(keyword));
                                zTXt_text.add(value);
                                zTXt_compressionMethod.add(new Integer(0));
                            } else {
                                // Use a tEXt node
                                tEXt_keyword.add(toPrintableLatin1(keyword));
                                tEXt_text.add(value);
                            }
                        } else {
                            int flag = compression.equals("zip") ?
                                1 : 0;

                            // Use an iTXt node
                            iTXt_keyword.add(toPrintableLatin1(keyword));
                            iTXt_compressionFlag.add(new Integer(flag));
                            iTXt_compressionMethod.add(new Integer(0));
                            iTXt_languageTag.add(language);
                            iTXt_translatedKeyword.add(keyword); // fake it
                            iTXt_text.add(value);
                        }
                    }
                    child = child.getNextSibling();
                }
                //          } else if (name.equals("Transparency")) {
                //              Node child = node.getFirstChild();
                //              while (child != null) {
                //                  String childName = child.getNodeName();
                //                  if (childName.equals("Alpha")) {
                //                  } else if (childName.equals("TransparentIndex")) {
                //                  } else if (childName.equals("TransparentColor")) {
                //                  } else if (childName.equals("TileTransparencies")) {
                //                  } else if (childName.equals("TileOpacities")) {
                //                  }
                //                  child = child.getNextSibling();
                //              }
                //          } else {
                //              // fatal(node, "Unknown child of root node!");
            }
            
            node = node.getNextSibling();
        }
    }

    // Reset all instance variables to their initial state
    public void reset() {
        IHDR_present = false;
        PLTE_present = false;
        bKGD_present = false;
        cHRM_present = false;
        gAMA_present = false;
        hIST_present = false;
        iCCP_present = false;
        iTXt_keyword = new ArrayList();
        iTXt_compressionFlag = new ArrayList();
        iTXt_compressionMethod = new ArrayList();
        iTXt_languageTag = new ArrayList();
        iTXt_translatedKeyword = new ArrayList();
        iTXt_text = new ArrayList();
        pHYs_present = false;
        sBIT_present = false;
        sPLT_present = false;
        sRGB_present = false;
        tEXt_keyword = new ArrayList();
        tEXt_text = new ArrayList();
        tIME_present = false;
        tRNS_present = false;
        zTXt_keyword = new ArrayList();
        zTXt_compressionMethod = new ArrayList();
        zTXt_text = new ArrayList();
        unknownChunkType = new ArrayList();
        unknownChunkData = new ArrayList();
    }

    // BEGIN metadata reading section.

    private boolean gotHeader = false;
    private boolean gotMetadata = false;

    private Decoder decoder = null;
    private CLibPNGImageReader reader = null;

    private static int chunkType(String typeString) {
        char c0 = typeString.charAt(0);
        char c1 = typeString.charAt(1);
        char c2 = typeString.charAt(2);
        char c3 = typeString.charAt(3);

        int type = (c0 << 24) | (c1 << 16) | (c2 << 8) | c3;
        return type;
    }

    private String readNullTerminatedString(ImageInputStream stream)
        throws IOException {
        StringBuffer b = new StringBuffer();
        int c;

        while ((c = stream.read()) != 0) {
            b.append((char)c);
        }
        return b.toString();
    }

    private void readHeader() throws IIOException {
        if (gotHeader) {
            return;
        }

        try {
            mediaLibImage mlibImage = decoder.getImage();
            int width = mlibImage.getWidth();
            int height = mlibImage.getHeight();
            int bitDepth = decoder.getBitDepth();
            int colorType;
            switch(mlibImage.getChannels()) {
            case 1:
                colorType = decoder.getPalette() == null?
                    PNG_COLOR_GRAY : PNG_COLOR_PALETTE;
                break;
            case 2:
                colorType = PNG_COLOR_GRAY_ALPHA;
                break;
            case 3:
                colorType = PNG_COLOR_RGB;
                break;
            case 4:
                colorType = PNG_COLOR_RGB_ALPHA;
                break;
            default:
                throw new IIOException("Unsupported image type.");
            }

            // Compression method 0 (deflate/inflate) is only supported type.
            int compressionMethod = 0;

            // Filter method 0 (adaptive filtering) is only supported type.
            int filterMethod = 0;

            int interlaceMethod = decoder.getInterlaceMethod();
            
            if (width == 0) {
                throw new IIOException("Image width == 0!");
            }
            if (height == 0) {
                throw new IIOException("Image height == 0!");
            }
            if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 &&
                bitDepth != 8 && bitDepth != 16) {
                throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!");
            }
            if (colorType != 0 && colorType != 2 && colorType != 3 &&
                colorType != 4 && colorType != 6) {
                throw new IIOException("Color type must be 0, 2, 3, 4, or 6!");
            }
            if (colorType == PNG_COLOR_PALETTE && bitDepth == 16) {
                throw new IIOException("Bad color type/bit depth combination!");
            }
            if ((colorType == PNG_COLOR_RGB ||
                 colorType == PNG_COLOR_RGB_ALPHA ||
                 colorType == PNG_COLOR_GRAY_ALPHA) &&
                (bitDepth != 8 && bitDepth != 16)) {
                throw new IIOException("Bad color type/bit depth combination!");
            }
            if (compressionMethod != 0) {
                throw new IIOException("Unknown compression method (not 0)!");
            }
            if (filterMethod != 0) {
                throw new IIOException("Unknown filter method (not 0)!");
            }
            if (interlaceMethod != 0 && interlaceMethod != 1) {
                throw new IIOException("Unknown interlace method (not 0 or 1)!");
            }
        
            IHDR_present = true;
            IHDR_width = width;
            IHDR_height = height;
            IHDR_bitDepth = bitDepth;
            IHDR_colorType = colorType; 
            IHDR_compressionMethod = compressionMethod;
            IHDR_filterMethod = filterMethod;
            IHDR_interlaceMethod = interlaceMethod;
            gotHeader = true;
        } catch (IOException e) {
            throw new IIOException("I/O error reading PNG header!", e);
        }
    }

    private void parse_PLTE_chunk() throws IOException {
        if (PLTE_present) {
            processWarningOccurred(
"A PNG image may not contain more than one PLTE chunk.\n" +
"The chunk will be ignored.");
            return;
        } else if (IHDR_colorType == PNG_COLOR_GRAY ||
                   IHDR_colorType == PNG_COLOR_GRAY_ALPHA) {
            processWarningOccurred(
"A PNG gray or gray alpha image cannot have a PLTE chunk.\n" +
"The chunk will be ignored.");
            return;
        }

        byte[] palette = decoder.getPalette();

        if(palette != null) {
            int numEntries = palette.length/3;
            if (IHDR_colorType == PNG_COLOR_PALETTE) {
                int maxEntries = 1 << IHDR_bitDepth;
                if (numEntries > maxEntries) {
                    processWarningOccurred(
                                           "PLTE chunk contains too many entries for bit depth, ignoring extras.");
                    numEntries = maxEntries;
                }
            }

            // Round array sizes up to 2^2^n
            int paletteEntries;
            if (numEntries > 16) {
                paletteEntries = 256;
            } else if (numEntries > 4) {
                paletteEntries = 16;
            } else if (numEntries > 2) {
                paletteEntries = 4;
            } else {
                paletteEntries = 2;
            }

            PLTE_present = true;
            PLTE_red = new byte[paletteEntries];
            PLTE_green = new byte[paletteEntries];
            PLTE_blue = new byte[paletteEntries];

            int index = 0;
            for (int i = 0; i < numEntries; i++) {
                PLTE_red[i] = palette[index++];
                PLTE_green[i] = palette[index++];
                PLTE_blue[i] = palette[index++];
            }
        }
    }

    private void parse_bKGD_chunk() throws IOException {
        int[] background = decoder.getBackground();
        if(background != null) {
            if (IHDR_colorType == PNG_COLOR_PALETTE) {
                bKGD_colorType = PNG_COLOR_PALETTE;
                bKGD_index = background[0];
            } else if (IHDR_colorType == PNG_COLOR_GRAY ||
                       IHDR_colorType == PNG_COLOR_GRAY_ALPHA) {
                bKGD_colorType = PNG_COLOR_GRAY;
                bKGD_gray = background[0];
            } else { // RGB or RGB_ALPHA
                bKGD_colorType = PNG_COLOR_RGB;
                bKGD_red = background[0];
                bKGD_green = background[1];
                bKGD_blue = background[2];
            }

            bKGD_present = true;
        }
    }

    private void parse_cHRM_chunk() throws IOException {
        int[] chrm = decoder.getAllPrimaryChromaticities();
        if(chrm != null) {
            int i = 0;
            cHRM_whitePointX = chrm[i++];
            cHRM_whitePointY = chrm[i++];
            cHRM_redX = chrm[i++];
            cHRM_redY = chrm[i++];
            cHRM_greenX = chrm[i++];
            cHRM_greenY = chrm[i++];
            cHRM_blueX = chrm[i++];
            cHRM_blueY = chrm[i++];

            cHRM_present = true;
        }
    }

    private void parse_gAMA_chunk() throws IOException {
        int gamma = decoder.getImageGamma();
        if(gamma != decoder.PNG_gAMA_DEFAULT) {
            gAMA_gamma = gamma;

            gAMA_present = true;
        }
    }

    private void parse_hIST_chunk() throws IOException, IIOException {
        short[] histogram = decoder.getHistogram();
        if(histogram != null) {
            if (!PLTE_present) {
                throw new IIOException("hIST chunk without prior PLTE chunk!");
            }

            int length = Math.min(PLTE_red.length, histogram.length);
            hIST_histogram = new char[length];
            for(int i = 0; i < length; i++) {
                hIST_histogram[i] = (char)histogram[i];
            }

            hIST_present = true;
        }
    }

    private void parse_iCCP_chunk() throws IOException {
        String profileName = decoder.getEmbeddedICCProfileName();

        if(profileName != null) {
            iCCP_profileName = profileName;

            byte[] uncompressedProfile = decoder.getEmbeddedICCProfile();

            // Need to compress this profile to match metadata specification.
            Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION);
            compressor.setInput(uncompressedProfile);
            compressor.finish();

            int off = 0;
            int len = uncompressedProfile.length;
            byte[] compressedProfile = new byte[uncompressedProfile.length];
            do {
                int count = compressor.deflate(compressedProfile, off, len);
                off += count;
                len -= count;
            } while(!compressor.finished());

            int compressedDataLength = off;

            iCCP_compressedProfile = new byte[compressedDataLength];
            System.arraycopy(compressedProfile, 0,
                             iCCP_compressedProfile, 0, compressedDataLength);


            iCCP_present = true;
        }
    }
  
    private void parse_pHYs_chunk() throws IOException {
        int unitSpecifier =
            decoder.getPhysicalPixelDimensions(decoder.PNG_PIXELS_UNIT_SPECIFIER);
        if(unitSpecifier != decoder.PNG_pHYs_NOT_DEFINED) {
            pHYs_pixelsPerUnitXAxis =
                decoder.getPhysicalPixelDimensions(decoder.PNG_PIXELS_UNIT_X);
            pHYs_pixelsPerUnitYAxis =
                decoder.getPhysicalPixelDimensions(decoder.PNG_PIXELS_UNIT_Y);
            pHYs_unitSpecifier = unitSpecifier;

            pHYs_present = true;
        }
    }
    
    private void parse_sBIT_chunk() throws IOException {
        byte[] sBits = decoder.getSignificantBits();
        if(sBits != null) {
            int i = 0;
            int colorType = IHDR_colorType;
            if (colorType == PNG_COLOR_GRAY ||
                colorType == PNG_COLOR_GRAY_ALPHA) {
                sBIT_grayBits = sBits[i++];
            } else if (colorType == PNG_COLOR_RGB ||
                       colorType == PNG_COLOR_PALETTE ||
                       colorType == PNG_COLOR_RGB_ALPHA) {
                sBIT_redBits = sBits[i++];
                sBIT_greenBits = sBits[i++];
                sBIT_blueBits = sBits[i++];
            }

            if (colorType == PNG_COLOR_GRAY_ALPHA ||
                colorType == PNG_COLOR_RGB_ALPHA) {
                sBIT_alphaBits = sBits[i++];
            }

            sBIT_colorType = colorType;
            sBIT_present = true;
        }
    }

    private void parse_sPLT_chunk()
        throws IOException, IIOException {

        PNGChunk[] sPLTChunks = decoder.getSuggestedPalette();

        if(sPLTChunks != null &&
           sPLTChunks.length > 0 && sPLTChunks[0] != null) {
            PNGChunk sPLTChunk = sPLTChunks[0];
            byte[] chunkData = sPLTChunk.getData();
            int chunkLength = chunkData.length;

            InputStream is = new ByteArrayInputStream(sPLTChunk.getData());
            ImageInputStream stream = new MemoryCacheImageInputStream(is);

            sPLT_paletteName = readNullTerminatedString(stream);
            chunkLength -= sPLT_paletteName.length() + 1;

            int sampleDepth = stream.readUnsignedByte();
            sPLT_sampleDepth = sampleDepth;

            int numEntries = chunkLength/(4*(sampleDepth/8) + 2);
            sPLT_red = new int[numEntries];
            sPLT_green = new int[numEntries];
            sPLT_blue = new int[numEntries];
            sPLT_alpha = new int[numEntries];
            sPLT_frequency = new int[numEntries];

            if (sampleDepth == 8) {
                for (int i = 0; i < numEntries; i++) {
                    sPLT_red[i] = stream.readUnsignedByte();
                    sPLT_green[i] = stream.readUnsignedByte();
                    sPLT_blue[i] = stream.readUnsignedByte();
                    sPLT_alpha[i] = stream.readUnsignedByte();
                    sPLT_frequency[i] = stream.readUnsignedShort();
                }
            } else if (sampleDepth == 16) {
                for (int i = 0; i < numEntries; i++) {
                    sPLT_red[i] = stream.readUnsignedShort();
                    sPLT_green[i] = stream.readUnsignedShort();
                    sPLT_blue[i] = stream.readUnsignedShort();
                    sPLT_alpha[i] = stream.readUnsignedShort();
                    sPLT_frequency[i] = stream.readUnsignedShort();
                }
            } else {
                throw new IIOException("sPLT sample depth not 8 or 16!");
            }

            sPLT_present = true;
        }
    }

    private void parse_sRGB_chunk() throws IOException {
        int renderingIntent = decoder.getStandardRGB();
        if(renderingIntent != decoder.PNG_sRGB_NOT_DEFINED) {
            sRGB_renderingIntent = renderingIntent;
            sRGB_present = true;
        }
    }

    private void parse_tIME_chunk() throws IOException {
        Calendar cal = decoder.getLastModificationTime();
        if(cal != null) {
            tIME_year = cal.get(Calendar.YEAR);
            tIME_month = cal.get(Calendar.MONTH) + 1;
            tIME_day = cal.get(Calendar.DAY_OF_MONTH);
            tIME_hour = cal.get(Calendar.HOUR_OF_DAY);
            tIME_minute = cal.get(Calendar.MINUTE);
            tIME_second = cal.get(Calendar.SECOND);

            tIME_present = true;
        }
    }

    private void parse_tRNS_chunk() throws IOException {
        int[] transparency = decoder.getTransparency();

        if(transparency == null) {
            return;
        }

        int colorType = IHDR_colorType;
        if (colorType == PNG_COLOR_PALETTE) {
            if (!PLTE_present) {
                processWarningOccurred(
"tRNS chunk without prior PLTE chunk, ignoring it.");
                return;
            }

            // Alpha table may have fewer entries than RGB palette
            int maxEntries = PLTE_red.length;
            int numEntries = transparency.length;
            if (numEntries > maxEntries) {
                processWarningOccurred(
"tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
                numEntries = maxEntries;
            }
            tRNS_alpha = new byte[numEntries];
            tRNS_colorType = PNG_COLOR_PALETTE;
            for(int i = 0; i < numEntries; i++) {
                tRNS_alpha[i] = (byte)transparency[i];
            }
        } else if (colorType == PNG_COLOR_GRAY) {
            if (transparency.length != 1) {
                processWarningOccurred(
"tRNS chunk for gray image must have length 2, ignoring chunk.");
                return;
            }
            tRNS_gray = transparency[0];
            tRNS_colorType = PNG_COLOR_GRAY;
        } else if (colorType == PNG_COLOR_RGB) {
            if (transparency.length != 3) {
                processWarningOccurred(
"tRNS chunk for RGB image must have length 6, ignoring chunk.");
                return;
            }
            tRNS_red = transparency[0];
            tRNS_green = transparency[1];
            tRNS_blue = transparency[2];
            tRNS_colorType = PNG_COLOR_RGB;
        } else {
            processWarningOccurred(
"Gray+Alpha and RGBA images may not have a tRNS chunk, ignoring it.");
            return;
        }

        tRNS_present = true;
    }

    // Parse all iTXt, tEXt, and zTXt chunks.
    private void parseTextChunk() throws IOException {
        PNGTextualData[] textualData = decoder.getTextualData();

        if(textualData != null) {
            for(int i = 0; i < textualData.length; i++) {
                PNGTextualData textData = textualData[i];
                String keyword = textData.getKeyword();
                String text = textData.getText();
                String translatedKeyword = textData.getTranslatedKeyword();

                // No way to detect a zTXt chunk to use tEXt for zTXt.
                // Also, all text is already decompressed.
                if(keyword.equals(translatedKeyword)) { // tEXt and zTXt
                    tEXt_keyword.add(keyword);
                    tEXt_text.add(text);
                } else {                                // iTXt
                    iTXt_keyword.add(keyword);
                    iTXt_text.add(text);
                    iTXt_translatedKeyword.add(translatedKeyword);

                    // XXX No access to compression flag so set to 'false'
                    // as text is decompressed by codecLib.
                    int compressionFlag = 0;
                    iTXt_compressionFlag.add(new Integer(compressionFlag));
        
                    // No access to compression method but only specified
                    // one is '0' (deflate compression with ZLib data stream).
                    int compressionMethod = 0;
                    iTXt_compressionMethod.add(new Integer(compressionMethod));

                    String languageTag = textData.getEncoding();
                    iTXt_languageTag.add(languageTag);        
                }
            }
        }
    }

    synchronized void readMetadata(CLibPNGImageReader reader,
                                   Decoder decoder) throws IIOException {
        if (gotMetadata) {
            return;
        }

        this.reader = reader;
        this.decoder = decoder;

        readHeader();

        try {
            parse_PLTE_chunk();
            parse_bKGD_chunk();
            parse_cHRM_chunk();
            parse_gAMA_chunk();
            parse_hIST_chunk();
            parse_iCCP_chunk();
            parse_pHYs_chunk();
            parse_sBIT_chunk();
            parse_sPLT_chunk();
            parse_sRGB_chunk();
            parse_tIME_chunk();
            parse_tRNS_chunk();

            parseTextChunk();

            PNGChunk[] userChunks = decoder.getUserData();
            if(userChunks != null) {
                for(int i = 0; i < userChunks.length; i++) {
                    // Read an unknown chunk
                    PNGChunk userChunk = userChunks[i];

                    int chunkType = userChunk.getID();
                    byte[] b = userChunk.getData();

                    StringBuffer chunkName = new StringBuffer(4);
                    chunkName.append((char)(chunkType >>> 24));
                    chunkName.append((char)((chunkType >> 16) & 0xff));
                    chunkName.append((char)((chunkType >> 8) & 0xff));
                    chunkName.append((char)(chunkType & 0xff));

                    int ancillaryBit = chunkType >>> 28;
                    if (ancillaryBit == 0) {
                        processWarningOccurred(
                                               "Encountered unknown chunk with critical bit set!");
                    }

                    unknownChunkType.add(chunkName.toString());
                    unknownChunkData.add(b);
                }
            }
        } catch (IOException e) {
            throw new IIOException("Error reading PNG metadata", e);
        } finally {
            this.reader = null;
            this.decoder = null;
        }

        gotMetadata = true;
    }

    void processWarningOccurred(String warning) {
        if(reader != null) {
            reader.forwardWarningMessage(warning);
        }
    }

    // END metadata reading methods.

    // BEGIN metadata writing methods.

    synchronized void writeMetadata(Encoder encoder) throws IIOException {
        if(IHDR_present) {
            encoder.setBitDepth(IHDR_bitDepth);
            encoder.setInterlaceMethod(IHDR_interlaceMethod == 0 ?
                                       Encoder.PNG_INTERLACE_METHOD_DEFAULT :
                                       Encoder.PNG_INTERLACE_METHOD_ADAM7);
        }

        if(PLTE_present) {
            int paletteLength = PLTE_red.length;
            byte[] palette = new byte[3*paletteLength];
            for(int i = 0, j= 0; i < paletteLength; i++) {
                palette[j++] = PLTE_red[i];
                palette[j++] = PLTE_green[i];
                palette[j++] = PLTE_blue[i];
            }
            encoder.setPalette(palette);
        }

        if(bKGD_present) {
            int[] color;
            switch(bKGD_colorType) {
            case PNG_COLOR_GRAY:
                color = new int[] {bKGD_gray};
                break;
            case PNG_COLOR_PALETTE:
                color = new int[] {bKGD_index};
                break;
            default:
                color = new int[] {bKGD_red, bKGD_green, bKGD_blue};
            }
            encoder.setBackground(color);
        }

        if(cHRM_present) {
            encoder.setPrimaryChromaticities(cHRM_whitePointX,
                                             cHRM_whitePointY,
                                             cHRM_redX, cHRM_redY,
                                             cHRM_greenX, cHRM_greenY,
                                             cHRM_blueX, cHRM_blueY);
        }

        if(gAMA_present) {
            encoder.setImageGamma(gAMA_gamma);
        }

        if(hIST_present) {
            int histogramLength = hIST_histogram.length;
            short[] histogram = new short[histogramLength];
            for(int i = 0; i < histogramLength; i++) {
                histogram[i] = (short)hIST_histogram[i];
            }
            encoder.setHistogram(histogram);
        }

        if(iCCP_present) {
            // Encoder expects an uncompressed profile so decompress.
            Inflater decompresser = new Inflater();
            decompresser.setInput(iCCP_compressedProfile);
            byte[] result = new byte[2*decompresser.getRemaining()];

            int off = 0;
            try {
                do {
                    off +=
                        decompresser.inflate(result, off, result.length - off);
                    if(off == result.length && !decompresser.finished()) {
                        byte[] tmpbuf = new byte[2*result.length];
                        System.arraycopy(result, 0, tmpbuf, 0, result.length);
                        result = tmpbuf;
                    }
                } while(!decompresser.finished());
                decompresser.end();

                byte[] uncompressedProfile;
                if(off == result.length) {
                    uncompressedProfile = result;
                } else {
                    uncompressedProfile = new byte[off];
                    System.arraycopy(result, 0, uncompressedProfile, 0, off);
                }

                String iCCPName = toPrintableLatin1(iCCP_profileName);
                encoder.setEmbeddedICCProfile(iCCPName, uncompressedProfile);
            } catch(DataFormatException e) {
                // XXX warning message?
            }
        }

        if(iTXt_keyword.size() > 0) {
            int numChunks = iTXt_keyword.size();
            for(int i = 0; i < numChunks; i++) {
                Integer compressionFlag =
                    Integer.valueOf((String)iTXt_compressionFlag.get(i));
                encoder.setUnicodeTextualData
                    ((String)iTXt_keyword.get(i),
                     (String)iTXt_translatedKeyword.get(i),
                     (String)iTXt_languageTag.get(i),
                     (String)iTXt_text.get(i),
                     compressionFlag.intValue() == 1);
            }
        }

        if(pHYs_present) {
            encoder.setPhysicalPixelDimensions(pHYs_pixelsPerUnitXAxis,
                                               pHYs_pixelsPerUnitYAxis,
                                               pHYs_unitSpecifier);
        }

        if(sBIT_present) {
            byte[] bits;
            switch(sBIT_colorType) {
            case PNG_COLOR_GRAY:
                bits = new byte[] {(byte)(sBIT_grayBits&0xff)};
                break;
            case PNG_COLOR_GRAY_ALPHA:
                bits = new byte[] {(byte)(sBIT_grayBits&0xff),
                                   (byte)(sBIT_alphaBits&0xff)};
                break;
            case PNG_COLOR_RGB_ALPHA:
                bits = new byte[] {(byte)(sBIT_redBits&0xff),
                                   (byte)(sBIT_greenBits&0xff),
                                   (byte)(sBIT_blueBits&0xff),
                                   (byte)(sBIT_alphaBits&0xff)};
                break;
            default: // RGB and PALETTE
                bits = new byte[] {(byte)(sBIT_redBits&0xff),
                                   (byte)(sBIT_greenBits&0xff),
                                   (byte)(sBIT_blueBits&0xff)};
                break;
            }
            encoder.setSignificantBits(bits);
        }

        if(sPLT_present) {
            if(sPLT_sampleDepth == 8) {
                byte[] red = new byte[sPLT_red.length];
                byte[] green = new byte[sPLT_green.length];
                byte[] blue = new byte[sPLT_blue.length];
                byte[] alpha = new byte[sPLT_alpha.length];
                short[] frequency = new short[sPLT_frequency.length];

                int length = red.length;
                for(int i = 0; i < length; i++) {
                    red[i] = (byte)(sPLT_red[i]&0xff);
                    green[i] = (byte)(sPLT_green[i]&0xff);
                    blue[i] = (byte)(sPLT_blue[i]&0xff);
                    alpha[i] = (byte)(sPLT_alpha[i]&0xff);
                    frequency[i] = (short)(sPLT_frequency[i]&0xffff);
                }

                String sPLTName = toPrintableLatin1(sPLT_paletteName);
                encoder.setSuggestedPalette(sPLTName,
                                            red, green, blue, alpha,
                                            frequency);
            } else {
                short[] red = new short[sPLT_red.length];
                short[] green = new short[sPLT_green.length];
                short[] blue = new short[sPLT_blue.length];
                short[] alpha = new short[sPLT_alpha.length];
                short[] frequency = new short[sPLT_frequency.length];

                int length = red.length;
                for(int i = 0; i < length; i++) {
                    red[i] = (short)(sPLT_red[i]&0xffff);
                    green[i] = (short)(sPLT_green[i]&0xffff);
                    blue[i] = (short)(sPLT_blue[i]&0xffff);
                    alpha[i] = (short)(sPLT_alpha[i]&0xffff);
                    frequency[i] = (short)(sPLT_frequency[i]&0xffff);
                }

                String sPLTName = toPrintableLatin1(sPLT_paletteName);
                encoder.setSuggestedPalette(sPLTName,
                                            red, green, blue, alpha,
                                            frequency);
            }
        }

        if(sRGB_present) {
            encoder.setStandardRGB(sRGB_renderingIntent);
        }

        if(tEXt_keyword.size() > 0) {
            int numChunks = tEXt_keyword.size();
            for(int i = 0; i < numChunks; i++) {
                encoder.setTextualData((String)tEXt_keyword.get(i),
                                       (String)tEXt_text.get(i),
                                       false);
            }
        }

        if(tIME_present) {
            encoder.setLastModificationTime
                (new GregorianCalendar(tIME_year, tIME_month - 1, tIME_day,
                                       tIME_hour, tIME_minute, tIME_second));
        }

        if(tRNS_present) {
            if(tRNS_colorType == PNG_COLOR_GRAY) {
                encoder.setTransparency(tRNS_gray, tRNS_gray, tRNS_gray);
            } else if(tRNS_colorType == PNG_COLOR_PALETTE) {
                int length = tRNS_alpha.length;
                int[] color = new int[length];
                for(int i = 0; i < length; i++) {
                    color[i] = tRNS_alpha[i]&0xff;
                }
                encoder.setTransparency(color);
            } else {
                encoder.setTransparency(tRNS_red, tRNS_green, tRNS_blue);
            }
        }

        if(zTXt_keyword.size() > 0) {
            int numChunks = zTXt_keyword.size();
            for(int i = 0; i < numChunks; i++) {
                encoder.setTextualData((String)zTXt_keyword.get(i),
                                       (String)zTXt_text.get(i),
                                       true);
            }
        }

        if(unknownChunkType.size() > 0) {
            int numChunks = unknownChunkType.size();
            for(int i = 0; i < numChunks; i++) {
                encoder.setUserData((String)unknownChunkType.get(i),
                                    (byte[])unknownChunkData.get(i),
                                    Encoder.PNG_SAVE_BEFORE_IMAGE_DATA);
            }
        }
    }

    // END metadata writing methods.
}
