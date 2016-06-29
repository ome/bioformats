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
 * $RCSfile: TIFFTag.java,v $
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
 * $Date: 2006/04/11 22:10:35 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.util.Map;
import java.util.HashMap;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

/**
 * A class defining the notion of a TIFF tag.  A TIFF tag is a key
 * that may appear in an Image File Directory (IFD).  In the IFD
 * each tag has some data associated with it, which may consist of zero
 * or more values of a given data type. The combination of a tag and a
 * value is known as an IFD Entry or TIFF Field.
 *
 * <p> The actual tag values used in the root IFD of a standard ("baseline")
 * tiff stream are defined in the {@link BaselineTIFFTagSet
 * <code>BaselineTIFFTagSet</code>} class.
 *
 * @see BaselineTIFFTagSet
 * @see TIFFField
 * @see TIFFTagSet
 */
public class TIFFTag {

    // TIFF 6.0 + Adobe PageMaker(R) 6.0 TIFF Technical Notes 1 IFD data type

    /** Flag for 8 bit unsigned integers. */
    public static final int TIFF_BYTE        =  1;

    /** Flag for null-terminated ASCII strings. */
    public static final int TIFF_ASCII       =  2;

    /** Flag for 16 bit unsigned integers. */
    public static final int TIFF_SHORT       =  3;

    /** Flag for 32 bit unsigned integers. */
    public static final int TIFF_LONG        =  4;

    /** Flag for pairs of 32 bit unsigned integers. */
    public static final int TIFF_RATIONAL    =  5;

    /** Flag for 8 bit signed integers. */
    public static final int TIFF_SBYTE       =  6;

    /** Flag for 8 bit uninterpreted bytes. */
    public static final int TIFF_UNDEFINED   =  7;

    /** Flag for 16 bit signed integers. */
    public static final int TIFF_SSHORT      =  8;

    /** Flag for 32 bit signed integers. */
    public static final int TIFF_SLONG       =  9;

    /** Flag for pairs of 32 bit signed integers. */
    public static final int TIFF_SRATIONAL   = 10;

    /** Flag for 32 bit IEEE floats. */
    public static final int TIFF_FLOAT       = 11;

    /** Flag for 64 bit IEEE doubles. */
    public static final int TIFF_DOUBLE      = 12;

    /**
     * Flag for IFD pointer defined in TIFF Tech Note 1 in
     * TIFF Specification Supplement 1.
     */
    public static final int TIFF_IFD_POINTER = 13;

    /**
     * The numerically smallest constant representing a TIFF data type.
     */
    public static final int MIN_DATATYPE = TIFF_BYTE;

    /**
     * The numerically largest constant representing a TIFF data type.
     */
    public static final int MAX_DATATYPE = TIFF_IFD_POINTER;

    private static final int[] sizeOfType = {
        0, //  0 = n/a
        1, //  1 = byte
        1, //  2 = ascii
        2, //  3 = short
        4, //  4 = long
        8, //  5 = rational
        1, //  6 = sbyte
        1, //  7 = undefined
        2, //  8 = sshort
        4, //  9 = slong
        8, // 10 = srational
        4, // 11 = float
        8, // 12 = double 
        4, // 13 = IFD_POINTER
    };

    // Other tags

    // Tech notes: http://partners.adobe.com/asn/developer/pdfs/tn/TIFFPM6.pdf

//     // Tech note 1: TIFF Trees
//     // Adds additional data type 13 = "IFD" (like LONG)
//     public static final int TAG_SUB_IFDS = 330; // IFD or LONG)

//     // Tech note 2: Clipping Path
//     public static final int TAG_CLIP_PATH = 343; // BYTE
//     public static final int TAG_X_CLIP_PATH_UNITS = 344; // DWORD
//     public static final int TAG_Y_CLIP_PATH_UNITS = 345; // DWORD
        
//     // Tech note 3: Indexed Images
//     public static final int TAG_INDEXED = 346; // SHORT
    
//     // Tech note 4: ICC L*a*b*
//     // New PhotometricInterpretation = 9

//     // Adobe

//     // PageMaker stuff
//     public static final int TAG_IMAGE_ID = 32781; // ASCII
//     public static final int TAG_OPI_PROXY = 351; // SHORT

//     // Photoshop stuff
//     public static final int TAG_IMAGE_SOURCE_DATA = 37724; // UNDEFINED
//     // 34377 - Image Resource Blocks
    
//     // GeoTIFF
//     public static final int TAG_MODEL_PIXEL_SCALE = 33550;
//     public static final int TAG_MODEL_TRANSFORMATION = 34264;
//     public static final int TAG_MODEL_TIEPOINT = 33922;
//     public static final int TAG_GEO_KEY_DIRECTORY = 34735;
//     public static final int TAG_GEO_DOUBLE_PARAMS = 34736;
//     public static final int TAG_GEO_ASCII_PARAMS = 34737;
//     public static final int TAG_INTERGRAPH_MATRIX = 33920;

//     // 33918 - Intergraph
//     // See http://remotesensing.org/lists/libtiff_archive/msg00557.html    

//     // Helios ICC profile tagging
    
//     // 34841 - HELIOS ICC profile reference       (ASCII)

//     // eiSTream Annotation Specification , Version 1.00.06
//     // Formerly Wang?

//     // 32932 - eiStream Annotation Data           (BYTE/any)
//     // 32934 - ???

    int number;

    String name;

    int dataTypes;

    TIFFTagSet tagSet = null;

    // Mnemonic names for integral enumerated constants
    Map valueNames = null;

    /**
     * Constructs a <code>TIFFTag</code> with a given name, tag number, set
     * of legal data types, and <code>TIFFTagSet</code> to which it refers.
     * The <code>tagSet</code> parameter will generally be
     * non-<code>null</code> only if this <code>TIFFTag</code> corresponds
     * to a pointer to a TIFF IFD. In this case <code>tagSet</code> will
     * represent the set of <code>TIFFTag</code>s which appear in the IFD
     * pointed to. A <code>TIFFTag</code> represents an IFD pointer if and
     * only if <code>tagSet</code> is non-<code>null</code> or the data
     * type <code>TIFF_IFD_POINTER</code> is legal.
     *
     * <p> If there are mnemonic names to be associated with the legal
     * data values for the tag, {@link #addValueName(int, String)
     * <code>addValueName()</code>} should be called on the new instance
     * for each name.</p>
     *
     * <p> See the documentation for {@link #getDataTypes()
     * <code>getDataTypes()</code>} for an explanation of how the set
     * of data types is to be converted into a bit mask.</p>
     *
     * @param name the name of the tag; may be <code>null</code>.
     * @param number the number used to represent the tag.
     * @param dataTypes a bit mask indicating the set of legal data
     * types for this tag.
     * @param tagSet the <code>TIFFTagSet</code> to which this tag
     * belongs; may be <code>null</code>.
     */
    public TIFFTag(String name, int number, int dataTypes, TIFFTagSet tagSet) {
        this.name = name;
        this.number = number;
        this.dataTypes = dataTypes;
        this.tagSet = tagSet;
    }

    /**
     * Constructs  a  <code>TIFFTag</code>  with  a  given  name,  tag
     * number,  and set  of  legal  data  types.  The  tag  will  have  no
     * associated <code>TIFFTagSet</code>.
     *
     * @param name the name of the tag; may be <code>null</code>.
     * @param number the number used to represent the tag.
     * @param dataTypes a bit mask indicating the set of legal data
     * types for this tag.
     *
     * @see #TIFFTag(String, int, int, TIFFTagSet)
     */
    public TIFFTag(String name, int number, int dataTypes) {
        this(name, number, dataTypes, null);
    }

    /**
     * Returns the number of bytes used to store a value of the given
     * data type.
     *
     * @param dataType the data type to be queried.
     *
     * @return the number of bytes used to store the given data type.
     *
     * @throws IllegalArgumentException if <code>datatype</code> is
     * less than <code>MIN_DATATYPE</code> or greater than
     * <code>MAX_DATATYPE</code>.
     */
    public static int getSizeOfType(int dataType) {
        if (dataType < MIN_DATATYPE ||dataType > MAX_DATATYPE) {
            throw new IllegalArgumentException("dataType out of range!");
        }
            
        return sizeOfType[dataType];
    }

    /**
     * Returns the name of the tag, as it will appear in image metadata.
     *
     * @return the tag name, as a <code>String</code>.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the integer used to represent the tag.
     *
     * @return the tag number, as an <code>int</code>.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns a bit mask indicating the set of data types that may
     * be used to store the data associated with the tag.
     * For example, a tag that can store both SHORT and LONG values
     * would return a value of:
     *
     * <pre>
     * (1 << TIFFTag.TIFF_SHORT) | (1 << TIFFTag.TIFF_LONG)
     * </pre>
     *
     * @return an <code>int</code> containing a bitmask encoding the
     * set of valid data types.
     */
    public int getDataTypes() {
        return dataTypes;
    }

    /**
     * Returns <code>true</code> if the given data type
     * may be used for the data associated with this tag.
     *
     * @param dataType the data type to be queried, one of
     * <code>TIFF_BYTE</code>, <code>TIFF_SHORT</code>, etc.
     *
     * @return a <code>boolean</code> indicating whether the given
     * data type may be used with this tag.
     *
     * @throws IllegalArgumentException if <code>datatype</code> is
     * less than <code>MIN_DATATYPE</code> or greater than
     * <code>MAX_DATATYPE</code>.
     */
    public boolean isDataTypeOK(int dataType) {
        if (dataType < MIN_DATATYPE || dataType > MAX_DATATYPE) {
            throw new IllegalArgumentException("datatype not in range!");
        }
        return (dataTypes & (1 << dataType)) != 0;
    }

    /**
     * Returns the <code>TIFFTagSet</code> of which this tag is a part.
     *
     * @return the containing <code>TIFFTagSet</code>.
     */
    public TIFFTagSet getTagSet() {
        return tagSet;
    }

    /**
     * Returns <code>true</code> if this tag is used to point to an IFD
     * structure containing additional tags.  This condition will be
     * satisfied if and only if either
     * <code>getTagSet()&nbsp;!=&nbsp;null</code> or
     * <code>isDataTypeOK(TIFF_IFD_POINTER)&nbsp;==&nbsp;true</code>.
     *
     * <p>Many TIFF extensions use this mechanism in order to limit the
     * number of new tags that may appear in the root IFD.</p>
     *
     * @return <code>true</code> if this tag points to an IFD.
     */
    public boolean isIFDPointer() {
        return tagSet != null || ((dataTypes & (1 << TIFF_IFD_POINTER)) != 0);
    }

    /**
     * Returns <code>true</code> if there are mnemonic names associated with
     * the set of legal values for the data associated with this tag.
     *
     * @return <code>true</code> if mnemonic value names are available.
     */
    public boolean hasValueNames() {
        return valueNames != null;
    }

    /**
     * Adds a mnemonic name for a particular value that this tag's
     * data may take on.
     *
     * @param value the data value.
     * @param name the name to associate with the value.
     */
    protected void addValueName(int value, String name) {
        if (valueNames == null) {
            valueNames = new HashMap();
        }
        valueNames.put(new Integer(value), name);
    }
    
    /**
     * Returns the mnemonic name associated with a particular value
     * that this tag's data may take on, or <code>null</code> if
     * no name is present.
     *
     * @param value the data value.
     *
     * @return the mnemonic name associated with the value, as a
     * <code>String</code>.
     */
    public String getValueName(int value) {
        if (valueNames == null) {
            return null;
        }
        return (String)valueNames.get(new Integer(value));
    }
    
}
