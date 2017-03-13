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
 * $RCSfile: TIFFIFD.java,v $
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
 * $Revision: 1.7 $
 * $Date: 2006/09/27 23:56:30 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;

public class TIFFIFD extends TIFFDirectory {

    private long stripOrTileByteCountsPosition = -1;
    private long stripOrTileOffsetsPosition = -1;
    private long lastPosition = -1;

    public static TIFFTag getTag(int tagNumber, List tagSets) {
        Iterator iter = tagSets.iterator();
        while (iter.hasNext()) {
            TIFFTagSet tagSet = (TIFFTagSet)iter.next();
            TIFFTag tag = tagSet.getTag(tagNumber);
            if (tag != null) {
                return tag;
            }
        }

        return null;
    }

    public static TIFFTag getTag(String tagName, List tagSets) {
        Iterator iter = tagSets.iterator();
        while (iter.hasNext()) {
            TIFFTagSet tagSet = (TIFFTagSet)iter.next();
            TIFFTag tag = tagSet.getTag(tagName);
            if (tag != null) {
                return tag;
            }
        }

        return null;
    }

    private static void writeTIFFFieldToStream(TIFFField field,
                                               ImageOutputStream stream)
        throws IOException {
        int count = field.getCount();
        Object data = field.getData();

        switch (field.getType()) {
        case TIFFTag.TIFF_ASCII:
            for (int i = 0; i < count; i++) {
                String s = ((String[])data)[i];
                int length = s.length();
                for (int j = 0; j < length; j++) {
                    stream.writeByte(s.charAt(j) & 0xff);
                }
                stream.writeByte(0);
            }
            break;
        case TIFFTag.TIFF_UNDEFINED:
        case TIFFTag.TIFF_BYTE:
        case TIFFTag.TIFF_SBYTE:
            stream.write((byte[])data);
            break;
        case TIFFTag.TIFF_SHORT:
            stream.writeChars((char[])data, 0, ((char[])data).length);
            break;
        case TIFFTag.TIFF_SSHORT:
            stream.writeShorts((short[])data, 0, ((short[])data).length);
            break;
        case TIFFTag.TIFF_SLONG:
            stream.writeInts((int[])data, 0, ((int[])data).length);
            break;
        case TIFFTag.TIFF_LONG:
            for (int i = 0; i < count; i++) {
                stream.writeInt((int)(((long[])data)[i]));
            }
            break;
        case TIFFTag.TIFF_IFD_POINTER:
            stream.writeInt(0); // will need to be backpatched
            break;
        case TIFFTag.TIFF_FLOAT:
            stream.writeFloats((float[])data, 0, ((float[])data).length);
            break;
        case TIFFTag.TIFF_DOUBLE:
            stream.writeDoubles((double[])data, 0, ((double[])data).length);
            break;
        case TIFFTag.TIFF_SRATIONAL:
            for (int i = 0; i < count; i++) {
                stream.writeInt(((int[][])data)[i][0]);
                stream.writeInt(((int[][])data)[i][1]);
            }
            break;
        case TIFFTag.TIFF_RATIONAL:
            for (int i = 0; i < count; i++) {
                long num = ((long[][])data)[i][0];
                long den = ((long[][])data)[i][1];
                stream.writeInt((int)num);
                stream.writeInt((int)den);
            }
            break;
        default:
            // error
        }
    }

    public TIFFIFD(List tagSets, TIFFTag parentTag) {
        super((TIFFTagSet[])tagSets.toArray(new TIFFTagSet[tagSets.size()]),
              parentTag);
    }

    public TIFFIFD(List tagSets) {
        this(tagSets, null);
    }

    public List getTagSetList() {
        return Arrays.asList(getTagSets());
    }

    /**
     * Returns an <code>Iterator</code> over the TIFF fields. The
     * traversal is in the order of increasing tag number.
     */
    // Note: the sort is guaranteed for low fields by the use of an
    // array wherein the index corresponds to the tag number and for
    // the high fields by the use of a TreeMap with tag number keys.
    public Iterator iterator() {
        return Arrays.asList(getTIFFFields()).iterator();
    }

    // Stream position initially at beginning, left at end
    // if ignoreUnknownFields is true, do not load fields for which
    // a tag cannot be found in an allowed TagSet.
    public void initialize(ImageInputStream stream,
                           boolean ignoreUnknownFields) throws IOException {
        removeTIFFFields();

        List tagSetList = getTagSetList();

        int numEntries = stream.readUnsignedShort();
        for (int i = 0; i < numEntries; i++) {
            // Read tag number, value type, and value count.
            int tag = stream.readUnsignedShort();
            int type = stream.readUnsignedShort();
            int count = (int)stream.readUnsignedInt();

            // Get the associated TIFFTag.
            TIFFTag tiffTag = getTag(tag, tagSetList);

            // Ignore unknown fields.
            if(ignoreUnknownFields && tiffTag == null) {
                // Skip the value/offset so as to leave the stream
                // position at the start of the next IFD entry.
                stream.skipBytes(4);

                // XXX Warning message ...

                // Continue with the next IFD entry.
                continue;
            }
            
            long nextTagOffset = stream.getStreamPosition() + 4;
            
            int sizeOfType = TIFFTag.getSizeOfType(type);
            if (count*sizeOfType > 4) {
                long value = stream.readUnsignedInt();
                stream.seek(value);
            }
            
            if (tag == BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS ||
                tag == BaselineTIFFTagSet.TAG_TILE_BYTE_COUNTS ||
                tag == BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH) {
                this.stripOrTileByteCountsPosition =
                    stream.getStreamPosition();
            } else if (tag == BaselineTIFFTagSet.TAG_STRIP_OFFSETS ||
                       tag == BaselineTIFFTagSet.TAG_TILE_OFFSETS ||
                       tag == BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT) {
                this.stripOrTileOffsetsPosition =
                    stream.getStreamPosition();
            }

            Object obj = null;

            try {
                switch (type) {
                case TIFFTag.TIFF_BYTE:
                case TIFFTag.TIFF_SBYTE:
                case TIFFTag.TIFF_UNDEFINED:
                case TIFFTag.TIFF_ASCII:
                    byte[] bvalues = new byte[count];
                    stream.readFully(bvalues, 0, count);
                
                    if (type == TIFFTag.TIFF_ASCII) {
                        // Can be multiple strings
                        Vector v = new Vector();
                        boolean inString = false;
                        int prevIndex = 0;
                        for (int index = 0; index <= count; index++) {
                            if (index < count && bvalues[index] != 0) {
                                if (!inString) {
                                // start of string
                                    prevIndex = index;
                                    inString = true;
                                }
                            } else { // null or special case at end of string
                                if (inString) {
                                // end of string
                                    String s = new String(bvalues, prevIndex,
                                                          index - prevIndex);
                                    v.add(s);
                                    inString = false;
                                }
                            }
                        }

                        count = v.size();
                        String[] strings;
                        if(count != 0) {
                            strings = new String[count];
                            for (int c = 0 ; c < count; c++) {
                                strings[c] = (String)v.elementAt(c);
                            }
                        } else {
                            // This case has been observed when the value of
                            // 'count' recorded in the field is non-zero but
                            // the value portion contains all nulls.
                            count = 1;
                            strings = new String[] {""};
                        }
                    
                        obj = strings;
                    } else {
                        obj = bvalues;
                    }
                    break;
                
                case TIFFTag.TIFF_SHORT:
                    char[] cvalues = new char[count];
                    for (int j = 0; j < count; j++) {
                        cvalues[j] = (char)(stream.readUnsignedShort());
                    }
                    obj = cvalues;
                    break;
                
                case TIFFTag.TIFF_LONG:
                case TIFFTag.TIFF_IFD_POINTER:
                    long[] lvalues = new long[count];
                    for (int j = 0; j < count; j++) {
                        lvalues[j] = stream.readUnsignedInt();
                    }
                    obj = lvalues;
                    break;
                
                case TIFFTag.TIFF_RATIONAL:
                    long[][] llvalues = new long[count][2];
                    for (int j = 0; j < count; j++) {
                        llvalues[j][0] = stream.readUnsignedInt();
                        llvalues[j][1] = stream.readUnsignedInt();
                    }
                    obj = llvalues;
                    break;
                
                case TIFFTag.TIFF_SSHORT:
                    short[] svalues = new short[count];
                    for (int j = 0; j < count; j++) {
                        svalues[j] = stream.readShort();
                    }
                    obj = svalues;
                    break;
                
                case TIFFTag.TIFF_SLONG:
                    int[] ivalues = new int[count];
                    for (int j = 0; j < count; j++) {
                        ivalues[j] = stream.readInt();
                    }
                    obj = ivalues;
                    break;
                
                case TIFFTag.TIFF_SRATIONAL:
                    int[][] iivalues = new int[count][2];
                    for (int j = 0; j < count; j++) {
                        iivalues[j][0] = stream.readInt();
                        iivalues[j][1] = stream.readInt();
                    }
                    obj = iivalues;
                    break;
                
                case TIFFTag.TIFF_FLOAT:
                    float[] fvalues = new float[count];
                    for (int j = 0; j < count; j++) {
                        fvalues[j] = stream.readFloat();
                    }
                    obj = fvalues;
                    break;
                
                case TIFFTag.TIFF_DOUBLE:
                    double[] dvalues = new double[count];
                    for (int j = 0; j < count; j++) {
                        dvalues[j] = stream.readDouble();
                    }
                    obj = dvalues;
                    break;
                
                default:
                    // XXX Warning
                    break;
                }
            } catch(EOFException eofe) {
                // The TIFF 6.0 fields have tag numbers less than or equal
                // to 532 (ReferenceBlackWhite) or equal to 33432 (Copyright).
                // If there is an error reading a baseline tag, then re-throw
                // the exception and fail; otherwise continue with the next
                // field.
                if(BaselineTIFFTagSet.getInstance().getTag(tag) == null) {
                    throw eofe;
                }
            }
            
            if (tiffTag == null) {
                // XXX Warning: unknown tag
            } else if (!tiffTag.isDataTypeOK(type)) {
                // XXX Warning: bad data type
            } else if (tiffTag.isIFDPointer() && obj != null) {
                stream.mark();
                stream.seek(((long[])obj)[0]);

                List tagSets = new ArrayList(1);
                tagSets.add(tiffTag.getTagSet());
                TIFFIFD subIFD = new TIFFIFD(tagSets);

                // XXX Use same ignore policy for sub-IFD fields?
                subIFD.initialize(stream, ignoreUnknownFields);
                obj = subIFD;
                stream.reset();
            }

            if (tiffTag == null) {
                tiffTag = new TIFFTag(null, tag, 1 << type, null);
            }

            // Add the field if its contents have been initialized which
            // will not be the case if an EOF was ignored above.
            if(obj != null) {
                TIFFField f = new TIFFField(tiffTag, type, count, obj);
                addTIFFField(f);
            }

            stream.seek(nextTagOffset);
        }

        this.lastPosition = stream.getStreamPosition();
    }

    public void writeToStream(ImageOutputStream stream)
        throws IOException {

        int numFields = getNumTIFFFields();
        stream.writeShort(numFields);

        long nextSpace = stream.getStreamPosition() + 12*numFields + 4;

        Iterator iter = iterator();
        while (iter.hasNext()) {
            TIFFField f = (TIFFField)iter.next();
            
            TIFFTag tag = f.getTag();

            int type = f.getType();
            int count = f.getCount();

            // Hack to deal with unknown tags
            if (type == 0) {
                type = TIFFTag.TIFF_UNDEFINED;
            }
            int size = count*TIFFTag.getSizeOfType(type);

            if (type == TIFFTag.TIFF_ASCII) {
                int chars = 0;
                for (int i = 0; i < count; i++) {
                    chars += f.getAsString(i).length() + 1;
                }
                count = chars;
                size = count;
            }

            int tagNumber = f.getTagNumber();
            stream.writeShort(tagNumber);
            stream.writeShort(type);
            stream.writeInt(count);

            // Write a dummy value to fill space
            stream.writeInt(0);
            stream.mark(); // Mark beginning of next field
            stream.skipBytes(-4);

            long pos;

            if (size > 4 || tag.isIFDPointer()) {
                // Ensure IFD or value is written on a word boundary
                nextSpace = (nextSpace + 3) & ~0x3;

                stream.writeInt((int)nextSpace);
                stream.seek(nextSpace);
                pos = nextSpace;

                if (tag.isIFDPointer()) {
                    TIFFIFD subIFD = (TIFFIFD)f.getData();
                    subIFD.writeToStream(stream);
                    nextSpace = subIFD.lastPosition;
                } else {
                    writeTIFFFieldToStream(f, stream);
                    nextSpace = stream.getStreamPosition();
                }
            } else {
                pos = stream.getStreamPosition();
                writeTIFFFieldToStream(f, stream);
            }

            // If we are writing the data for the
            // StripByteCounts, TileByteCounts, StripOffsets,
            // TileOffsets, JPEGInterchangeFormat, or
            // JPEGInterchangeFormatLength fields, record the current stream
            // position for backpatching
            if (tagNumber ==
                BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS ||
                tagNumber == BaselineTIFFTagSet.TAG_TILE_BYTE_COUNTS ||
                tagNumber == BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH) {
                this.stripOrTileByteCountsPosition = pos;
            } else if (tagNumber ==
                       BaselineTIFFTagSet.TAG_STRIP_OFFSETS ||
                       tagNumber ==
                       BaselineTIFFTagSet.TAG_TILE_OFFSETS ||
                       tagNumber ==
                       BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT) {
                this.stripOrTileOffsetsPosition = pos;
            }

            stream.reset(); // Go to marked position of next field
        }
        
        this.lastPosition = nextSpace;
    }

    public long getStripOrTileByteCountsPosition() {
        return stripOrTileByteCountsPosition;
    }

    public long getStripOrTileOffsetsPosition() {
        return stripOrTileOffsetsPosition;
    }

    public long getLastPosition() {
        return lastPosition;
    }

    void setPositions(long stripOrTileOffsetsPosition,
                      long stripOrTileByteCountsPosition,
                      long lastPosition) {
        this.stripOrTileOffsetsPosition = stripOrTileOffsetsPosition;
        this.stripOrTileByteCountsPosition = stripOrTileByteCountsPosition;
        this.lastPosition = lastPosition;
    }

    /**
     * Returns a <code>TIFFIFD</code> wherein all fields from the
     * <code>BaselineTIFFTagSet</code> are copied by value and all other
     * fields copied by reference.
     */
    public TIFFIFD getShallowClone() {
        // Get the baseline TagSet.
        TIFFTagSet baselineTagSet = BaselineTIFFTagSet.getInstance();

        // If the baseline TagSet is not included just return.
        List tagSetList = getTagSetList();
        if(!tagSetList.contains(baselineTagSet)) {
            return this;
        }

        // Create a new object.
        TIFFIFD shallowClone = new TIFFIFD(tagSetList, getParentTag());

        // Get the tag numbers in the baseline set.
        Set baselineTagNumbers = baselineTagSet.getTagNumbers();

        // Iterate over the fields in this IFD.
        Iterator fields = iterator();
        while(fields.hasNext()) {
            // Get the next field.
            TIFFField field = (TIFFField)fields.next();

            // Get its tag number.
            Integer tagNumber = new Integer(field.getTagNumber());

            // Branch based on membership in baseline set.
            TIFFField fieldClone;
            if(baselineTagNumbers.contains(tagNumber)) {
                // Copy by value.
                Object fieldData = field.getData();

                int fieldType = field.getType();

                try {
                    switch (fieldType) {
                    case TIFFTag.TIFF_BYTE:
                    case TIFFTag.TIFF_SBYTE:
                    case TIFFTag.TIFF_UNDEFINED:
                        fieldData = ((byte[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_ASCII:
                        fieldData = ((String[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_SHORT:
                        fieldData = ((char[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_LONG:
                    case TIFFTag.TIFF_IFD_POINTER:
                        fieldData = ((long[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_RATIONAL:
                        fieldData = ((long[][])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_SSHORT:
                        fieldData = ((short[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_SLONG:
                        fieldData = ((int[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_SRATIONAL:
                        fieldData = ((int[][])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_FLOAT:
                        fieldData = ((float[])fieldData).clone();
                        break;
                    case TIFFTag.TIFF_DOUBLE:
                        fieldData = ((double[])fieldData).clone();
                        break;
                    default:
                        // Shouldn't happen but do nothing ...
                    }
                } catch(Exception e) {
                    // Ignore it and copy by reference ...
                }

                fieldClone = new TIFFField(field.getTag(), fieldType,
                                           field.getCount(), fieldData);
            } else {
                // Copy by reference.
                fieldClone = field;
            }

            // Add the field to the clone.
            shallowClone.addTIFFField(fieldClone);
        }

        // Set positions.
        shallowClone.setPositions(stripOrTileOffsetsPosition,
                                  stripOrTileByteCountsPosition,
                                  lastPosition);

        return shallowClone;
    }
}
