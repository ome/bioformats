/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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
 * $RCSfile: FileFormatReader.java,v $
 * $Revision: 1.2 $
 * $Date: 2005/04/28 01:25:38 $
 * $State: Exp $
 *
 * Class:                   FileFormatReader
 *
 * Description:             Read J2K file stream
 *
 * COPYRIGHT:
 *
 * This software module was originally developed by Raphaël Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelöf (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Félix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 *
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.fileformat.reader;

import java.awt.Transparency;
import java.awt.color.ICC_Profile;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;

import jj2000.j2k.codestream.*;
import jj2000.j2k.fileformat.*;
import jj2000.j2k.io.*;

import java.util.*;
import java.io.*;
import com.sun.media.imageioimpl.plugins.jpeg2000.Box;
import com.sun.media.imageioimpl.plugins.jpeg2000.BitsPerComponentBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.ChannelDefinitionBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.ColorSpecificationBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.ComponentMappingBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.DataEntryURLBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.FileTypeBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.HeaderBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KMetadata;
import com.sun.media.imageioimpl.plugins.jpeg2000.ResolutionBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.PaletteBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.UUIDBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.UUIDListBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.XMLBox;
import com.sun.media.imageioimpl.plugins.jpeg2000.SignatureBox;

/**
 * This class reads the file format wrapper that may or may not exist around a
 * valid JPEG 2000 codestream. Since no information from the file format is
 * used in the actual decoding, this class simply goes through the file and
 * finds the first valid codestream.
 *
 * @see jj2000.j2k.fileformat.writer.FileFormatWriter
 * */
public class FileFormatReader implements FileFormatBoxes{

    /** The random access from which the file format boxes are read */
    private RandomAccessIO in;

    /** The positions of the codestreams in the fileformat*/
    private Vector codeStreamPos;

    /** The lengths of the codestreams in the fileformat*/
    private Vector codeStreamLength;

    /** Create a IndexColorModel from the palette box if there is one */
    private ColorModel colorModel = null;

    /** The meta data */
    private J2KMetadata metadata;

    /** Parameters in header box */
    private int width;
    private int height;
    private int numComp;
    private int bitDepth;
    private int compressionType;
    private int unknownColor;
    private int intelProp;

    /** Various bit depth */
    private byte[] bitDepths;

    /** The lut in the palette box */
    private byte[][] lut;
    private byte[] compSize;

    /** Component mapping */
    private short[] comps;
    private byte[] type;
    private byte[] maps;

    /** Channel definitions */
    private short[] channels ;
    private short[] cType;
    private short[] associations;

    /** Color specification */
    private int colorSpaceType;

    /** ICC profile */
    private ICC_Profile profile;

    /**
     * The constructor of the FileFormatReader
     *
     * @param in The RandomAccessIO from which to read the file format
     * */
    public FileFormatReader(RandomAccessIO in, J2KMetadata metadata){
        this.in = in;
        this.metadata = metadata;
    }


    /**
     * This method checks whether the given RandomAccessIO is a valid JP2 file
     * and if so finds the first codestream in the file. Currently, the
     * information in the codestream is not used
     *
     * @param in The RandomAccessIO from which to read the file format
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     * @exception java.io.EOFException If end of file is reached
     * */
    public void readFileFormat() throws IOException, EOFException {

        int foundCodeStreamBoxes=0;
        int box;
        int length;
        long longLength=0;
        int pos;
        short marker;
        boolean jp2HeaderBoxFound=false;
        boolean lastBoxFound = false;

        try{

            // Go through the randomaccessio and find the first
            // contiguous codestream box. Check also that the File Format is
            // correct

            pos = in.getPos();

            // Make sure that the first 12 bytes is the JP2_SIGNATURE_BOX
            // or if not that the first 2 bytes is the SOC marker
            if(in.readInt() != 0x0000000c ||
               in.readInt() != JP2_SIGNATURE_BOX ||
               in.readInt() != 0x0d0a870a){ // Not a JP2 file
                in.seek(pos);

                marker = (short)in.readShort();
                if(marker != Markers.SOC) //Standard syntax marker found
                    throw new Error("File is neither valid JP2 file nor "+
                                    "valid JPEG 2000 codestream");
                in.seek(pos);
                if(codeStreamPos == null)
                    codeStreamPos = new Vector();
                codeStreamPos.addElement(new Integer(pos));
                return;
            }

            if (metadata != null)
                metadata.addNode(new SignatureBox());

            // Read all remaining boxes
            while(!lastBoxFound){
                pos = in.getPos();
                length = in.readInt();
                if((pos+length) == in.length())
                    lastBoxFound = true;

                box = in.readInt();
                if (length == 0) {
                    lastBoxFound = true;
                    length = in.length()-in.getPos();
                } else if(length == 1) {
                    longLength = in.readLong();
                    throw new IOException("File too long.");
                } else longLength = (long) 0;

                pos = in.getPos();
                length -= 8;

                switch(box){
                case FILE_TYPE_BOX:
                    readFileTypeBox(length + 8, longLength);
                    break;
                case CONTIGUOUS_CODESTREAM_BOX:
                    if(!jp2HeaderBoxFound)
                        throw new Error("Invalid JP2 file: JP2Header box not "+
                                        "found before Contiguous codestream "+
                                        "box ");
                    readContiguousCodeStreamBox(length + 8, longLength);
                    break;
                case JP2_HEADER_BOX:
                    if(jp2HeaderBoxFound)
                        throw new Error("Invalid JP2 file: Multiple "+
                                        "JP2Header boxes found");
                    readJP2HeaderBox(length + 8);
                    jp2HeaderBoxFound = true;
                    length = 0;
                    break;
                case IMAGE_HEADER_BOX:
                    readImageHeaderBox(length);
                    break;
                case INTELLECTUAL_PROPERTY_BOX:
                    readIntPropertyBox(length);
                    break;
                case XML_BOX:
                    readXMLBox(length);
                    break;
                case UUID_INFO_BOX:
                    length = 0;
                    break;
                case UUID_BOX:
                    readUUIDBox(length);
                    break;
                case UUID_LIST_BOX:
                    readUUIDListBox(length);
                    break;
                case URL_BOX:
                    readURLBox(length);
                    break;
                case PALETTE_BOX:
                    readPaletteBox(length + 8);
                    break;
                case BITS_PER_COMPONENT_BOX:
                    readBitsPerComponentBox(length);
                    break;
                case COMPONENT_MAPPING_BOX:
                    readComponentMappingBox(length);
                    break;
                case COLOUR_SPECIFICATION_BOX:
                    readColourSpecificationBox(length);
                    break;
                case CHANNEL_DEFINITION_BOX:
                    readChannelDefinitionBox(length);
                    break;
                case RESOLUTION_BOX:
                    length = 0;
                    break;
                case CAPTURE_RESOLUTION_BOX:
                case DEFAULT_DISPLAY_RESOLUTION_BOX:
                    readResolutionBox(box, length);
                    break;
                default:
                    if (metadata != null) {
                        byte[] data = new byte[length];
                        in.readFully(data, 0, length);
                        metadata.addNode(new Box(length + 8,
                                                 box,
                                                 longLength,
                                                 data));
                    }
                }
                if(!lastBoxFound)
                    in.seek(pos+length);
            }
        }catch( EOFException e ){
            throw new Error("EOF reached before finding Contiguous "+
                            "Codestream Box");
        }

        if(codeStreamPos.size() == 0){
          // Not a valid JP2 file or codestream
          throw new Error("Invalid JP2 file: Contiguous codestream box "+
                          "missing");
        }

        return;
    }

    /**
     * This method reads the File Type box
     *
     * @return false if the File Type box was not found or invalid else true
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     * @exception java.io.EOFException If the end of file was reached
     * */
    public boolean readFileTypeBox(int length, long longLength)
        throws IOException, EOFException {
        int nComp;
        boolean foundComp=false;

        // Check for XLBox
        if(length == 1) { // Box has 8 byte length;
            longLength = in.readLong();
            throw new IOException("File too long.");
        }

        // Check that this is a correct DBox value
        // Read Brand field
        if(in.readInt() != FT_BR)
            return false;

        // Read MinV field
        int minorVersion = in.readInt();

        // Check that there is at least one FT_BR entry in in
        // compatibility list
        nComp = (length - 16)/4; // Number of compatibilities.
        int[] comp = new int[nComp];
        for(int i=0; i < nComp; i++){
            if((comp[i] = in.readInt()) == FT_BR)
                foundComp = true;
        }
        if(!foundComp)
            return false;

        if (metadata != null)
            metadata.addNode(new FileTypeBox(FT_BR, minorVersion, comp));

        return true;
    }

    /**
     * This method reads the JP2Header box
     *
     * @param pos The position in the file
     *
     * @param length The length of the JP2Header box
     *
     * @param long length The length of the JP2Header box if greater than
     * 1<<32
     *
     * @return false if the JP2Header box was not found or invalid else true
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     * @exception java.io.EOFException If the end of file was reached
     * */
    public boolean readJP2HeaderBox(int length)
        throws IOException, EOFException {

        if(length == 0) // This can not be last box
            throw new Error("Zero-length of JP2Header Box");

        // Here the JP2Header data (DBox) would be read if we were to use it
        return true;
    }

   /**
     * This method reads the Image Header box
     * @param length The length of the JP2Header box
     *
     * @return false if the JP2Header box was not found or invalid else true
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     * @exception java.io.EOFException If the end of file was reached
     * */
    public boolean readImageHeaderBox(int length)
        throws IOException, EOFException {

        if(length == 0) // This can not be last box
            throw new Error("Zero-length of JP2Header Box");

        // Here the JP2Header data (DBox) would be read if we were to use it

        height = in.readInt();
        width = in.readInt();
        numComp = in.readShort();
        bitDepth = in.readByte();

        compressionType = in.readByte();
        unknownColor = in.readByte();
        intelProp = in.readByte();

        if (metadata != null) {

            metadata.addNode(new HeaderBox(height, width, numComp, bitDepth,
                                           compressionType, unknownColor,
                                           intelProp));
        }
        return true;
    }

     /**
     * This method skips the Contiguous codestream box and adds position
     * of contiguous codestream to a vector
     *
     * @param pos The position in the file
     *
     * @param length The length of the JP2Header box
     *
     * @param long length The length of the JP2Header box if greater than 1<<32
     *
     * @return false if the Contiguous codestream box was not found or invalid
     * else true
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     * @exception java.io.EOFException If the end of file was reached
     * */
    public boolean readContiguousCodeStreamBox(int length,
                                               long longLength)
        throws IOException, EOFException {

        // Add new codestream position to position vector
        int ccpos = in.getPos();

        if(codeStreamPos == null)
            codeStreamPos = new Vector();
        codeStreamPos.addElement(new Integer(ccpos));

        // Add new codestream length to length vector
        if(codeStreamLength == null)
            codeStreamLength = new Vector();
        codeStreamLength.addElement(new Integer(length));

        return true;
    }

    /**
     * This method reads the contents of the Intellectual property box
     * */
    public void readIntPropertyBox(int length) throws IOException {
        if (metadata != null) {
            byte[] data = new byte[length];
            in.readFully(data, 0, length);
            metadata.addNode(new Box(length + 8, 0x6A703269, data));
        }
    }

    /**
     * This method reads the contents of the XML box
     */
    public void readXMLBox(int length) throws IOException {
        if (metadata != null) {
            byte[] data = new byte[length];
            in.readFully(data, 0, length);
            metadata.addNode(new XMLBox(data));
        }
    }

    /**
     * This method reads the contents of the XML box
     */
    public void readURLBox(int length) throws IOException {
        if (metadata != null) {
            byte[] data = new byte[length];
            in.readFully(data, 0, length);
            metadata.addNode(new DataEntryURLBox(data));
        }
    }

    /**
     * This method reads the contents of the Intellectual property box
     */
    public void readUUIDBox(int length) throws IOException {
        if (metadata != null) {
            byte[] data = new byte[length];
            in.readFully(data, 0, length);
            metadata.addNode(new UUIDBox(data));
        }
    }

    /**
     * This method reads the contents of the UUID List box
     * */
    public void readUUIDListBox(int length) throws IOException {
        if (metadata != null) {
            byte[] data = new byte[length];
            in.readFully(data, 0, length);
            metadata.addNode(new UUIDListBox(data));
        }
    }

    /** This method reads the content of the palette box */
    public void readPaletteBox(int length) throws IOException {
        // Get current position in file
        int pos = in.getPos();

        int lutSize = in.readShort();
        int numComp = in.readByte();
        compSize = new byte[numComp];

        for (int i = 0; i < numComp; i++) {
            compSize[i] = (byte)in.readByte();
        }

        lut = new byte[numComp][lutSize];

        for (int n=0; n < lutSize; n++) {
            for (int c = 0; c < numComp; c++) {
                int depth = 1 + (compSize[c] & 0x7F);
                if (depth > 32)
                    depth = 32;
                int numBytes = (depth + 7)>>3;
                int mask = (1 << depth) - 1;
                byte[] buf = new byte[numBytes];
                in.readFully(buf, 0, numBytes);

                int val = 0;

                for (int k = 0; k < numBytes; k++) {
                    val = buf[k] + (val << 8);
                }
                lut[c][n] = (byte)val;
            }
        }
        if (metadata != null) {
            metadata.addNode(new PaletteBox(length, compSize, lut));
        }
    }

    /** Read the component mapping channel.
     */
    public void readComponentMappingBox(int length)throws IOException {
        int num = length / 4;

        comps = new short[num];
        type = new byte[num];
        maps = new byte[num];

        for (int i = 0; i < num; i++) {
            comps[i] = in.readShort();
            type[i] = in.readByte();
            maps[i] = in.readByte();
        }

        if (metadata != null) {
            metadata.addNode(new ComponentMappingBox(comps, type, maps));
        }
    }

    /**
     * This method reads the Channel Definition box
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     */
    public void readChannelDefinitionBox(int length)throws IOException {
        int num = in.readShort();
        channels = new short[num];
        cType = new short[num];
        associations = new short[num];

        for (int i = 0; i < num; i++) {
            channels[i] = in.readShort();
            cType[i] = in.readShort();
            associations[i] = in.readShort();
        }
        if (metadata != null) {
            metadata.addNode(new ChannelDefinitionBox(channels, cType, associations));
        }
    }

    /** Read the bits per component.
     */
    public void readBitsPerComponentBox(int length)throws IOException {
        bitDepths = new byte[length];
        in.readFully(bitDepths, 0, length);

        if (metadata != null) {
            metadata.addNode(new BitsPerComponentBox(bitDepths));
        }
    }

    /** Read the color specifications.
     */
    public void readColourSpecificationBox(int length)throws IOException {
        // read METHOD field
        byte method = (byte)in.readByte();

        // read PREC field
        byte prec = (byte)in.readByte();

        // read APPROX field
        byte approx = (byte)in.readByte();

        if (method == 2) {
            byte[] data = new byte[length - 3];
            in.readFully(data, 0, data.length);
            profile = ICC_Profile.getInstance(data);
        } else  // read EnumCS field
            colorSpaceType = in.readInt();

        if (metadata != null) {
            metadata.addNode(new ColorSpecificationBox(method, prec, approx,
                                                       colorSpaceType,
                                                       profile));
        }
    }

    /** Read the resolution.
     */
    public void readResolutionBox(int type, int length)throws IOException {
        byte[] data = new byte[length];
        in.readFully(data, 0, length);
        if (metadata != null) {
            metadata.addNode(new ResolutionBox(type, data));
        }
    }

    /**
     * This method creates and returns an array of positions to contiguous
     * codestreams in the file
     *
     * @return The positions of the contiguous codestreams in the file
     * */
    public long[] getCodeStreamPos(){
        int size = codeStreamPos.size();
        long[] pos = new long[size];
        for(int i=0 ; i<size ; i++)
            pos[i]=((Integer)(codeStreamPos.elementAt(i))).longValue();
        return pos;
    }

    /**
     * This method returns the position of the first contiguous codestreams in
     * the file
     *
     * @return The position of the first contiguous codestream in the file
     * */
    public int getFirstCodeStreamPos(){
        return ((Integer)(codeStreamPos.elementAt(0))).intValue();
    }

    /**
     * This method returns the length of the first contiguous codestreams in
     * the file
     *
     * @return The length of the first contiguous codestream in the file
     * */
    public int getFirstCodeStreamLength(){
        return ((Integer)(codeStreamLength.elementAt(0))).intValue();
    }

    /**
     * Returns the color model created from the palette box.
     */
    public ColorModel getColorModel() {
        // Check 'numComp' instance variable here in case there is an
        // embedded palette such as in the pngsuite images pp0n2c16.png
        // and pp0n6a08.png.
        if (lut != null && numComp == 1) {
            int numComp = lut.length;

            int maxDepth = 1 + (bitDepth & 0x7F);

            if (maps == null) {
                maps = new byte[numComp];
                for (int i = 0; i < numComp; i++)
                    maps[i] = (byte)i;
            }
            if (numComp == 3)
                colorModel = new IndexColorModel(maxDepth, lut[0].length,
                                                 lut[maps[0]],
                                                 lut[maps[1]],
                                                 lut[maps[2]]);
            else if (numComp == 4)
                colorModel = new IndexColorModel(maxDepth, lut[0].length,
                                                 lut[maps[0]],
                                                 lut[maps[1]],
                                                 lut[maps[2]],
                                                 lut[maps[3]]);
        } else if (channels != null){
            boolean hasAlpha = false;
            int alphaChannel = numComp - 1;

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

            ColorSpace cs = null;
            if (profile != null)
                cs = new ICC_ColorSpace(profile);
            else if (colorSpaceType == CSB_ENUM_SRGB)
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            else if (colorSpaceType == CSB_ENUM_GREY)
                cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            else if (colorSpaceType == CSB_ENUM_YCC)
                cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);

            int[] bits = new int[numComp];
            for (int i = 0; i < numComp; i++)
                if (bitDepths != null)
                    bits[i] = (bitDepths[i] & 0x7F) + 1;
                else
                    bits[i] = (bitDepth &0x7F) + 1;

            int maxBitDepth = 1 + (bitDepth & 0x7F);
	    boolean isSigned = (bitDepth & 0x80) == 0x80;
	    if (bitDepths != null)
		isSigned = (bitDepths[0]  & 0x80) == 0x80;

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
        return colorModel;
    }
}
