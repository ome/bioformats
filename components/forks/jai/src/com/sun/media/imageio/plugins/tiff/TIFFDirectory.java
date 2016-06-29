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
 * $RCSfile: TIFFDirectory.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2006/08/25 00:16:49 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;
import com.sun.media.imageioimpl.plugins.tiff.TIFFIFD;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadata;

/**
 * A convenience class for simplifying interaction with TIFF native
 * image metadata. A TIFF image metadata tree represents an Image File
 * Directory (IFD) from a TIFF 6.0 stream. An IFD consists of a number of
 * IFD Entries each of which associates an identifying tag number with
 * a compatible value. A <code>TIFFDirectory</code> instance corresponds
 * to an IFD and contains a set of {@link TIFFField}s each of which
 * corresponds to an IFD Entry in the IFD.
 *
 * <p>When reading, a <code>TIFFDirectory</code> may be created by passing
 * the value returned by {@link javax.imageio.ImageReader#getImageMetadata
 * ImageReader.getImageMetadata()} to {@link #createFromMetadata
 * createFromMetadata()}. The {@link TIFFField}s in the directory may then
 * be obtained using the accessor methods provided in this class.</p>
 *
 * <p>When writing, an {@link IIOMetadata} object for use by one of the
 * <core>write()</code> methods of {@link javax.imageio.ImageWriter} may be
 * created from a <code>TIFFDirectory</code> by {@link #getAsMetadata()}.
 * The <code>TIFFDirectory</code> itself may be created by construction or
 * from the <code>IIOMetadata</code> object returned by
 * {@link javax.imageio.ImageWriter#getDefaultImageMetadata
 * ImageWriter.getDefaultImageMetadata()}. The <code>TIFFField</code>s in the
 * directory may be set using the mutator methods provided in this class.</p>
 *
 * <p>A <code>TIFFDirectory</code> is aware of the tag numbers in the
 * group of {@link TIFFTagSet}s associated with it. When
 * a <code>TIFFDirectory</code> is created from a native image metadata
 * object, these tag sets are derived from the <tt>tagSets</tt> attribute
 * of the <tt>TIFFIFD</tt> node.</p>
 *
 * <p>A <code>TIFFDirectory</code> might also have a parent {@link TIFFTag}.
 * This will occur if the directory represents an IFD other than the root
 * IFD of the image. The parent tag is the tag of the IFD Entry which is a
 * pointer to the IFD represented by this <code>TIFFDirectory</code>. The
 * {@link TIFFTag#isIFDPointer} method of this parent <code>TIFFTag</code>
 * must return <code>true</code>.  When a <code>TIFFDirectory</code> is
 * created from a native image metadata object, the parent tag set is set
 * from the <tt>parentTagName</tt> attribute of the corresponding
 * <tt>TIFFIFD</tt> node. Note that a <code>TIFFDirectory</code> instance
 * which has a non-<code>null</code> parent tag will be contained in the
 * data field of a <code>TIFFField</code> instance which has a tag field
 * equal to the contained directory's parent tag.</p>
 * 
 * <p>As an example consider an EXIF image. The <code>TIFFDirectory</code>
 * instance corresponding to the EXIF IFD in the EXIF stream would have parent
 * tag {@link EXIFParentTIFFTagSet#TAG_EXIF_IFD_POINTER TAG_EXIF_IFD_POINTER}
 * and would include {@link EXIFTIFFTagSet} in its group of known tag sets.
 * The <code>TIFFDirectory</code> corresponding to this EXIF IFD will be
 * contained in the data field of a <code>TIFFField</code> which will in turn
 * be contained in the <code>TIFFDirectory</code> corresponding to the primary
 * IFD of the EXIF image which will itself have a <code>null</code>-valued
 * parent tag.</p>
 *
 * <p><b>Note that this implementation is not synchronized.</b> If multiple
 * threads use a <code>TIFFDirectory</code> instance concurrently, and at
 * least one of the threads modifies the directory, for example, by adding
 * or removing <code>TIFFField</code>s or <code>TIFFTagSet</code>s, it
 * <i>must</i> be synchronized externally.</p>
 *
 * @see IIOMetadata
 * @see TIFFField
 * @see TIFFTag
 * @see TIFFTagSet
 *
 * @since 1.1-beta
 */
// XXX doc: not thread safe
public class TIFFDirectory implements Cloneable {

    /** The largest low-valued tag number in the TIFF 6.0 specification. */
    private static final int MAX_LOW_FIELD_TAG_NUM =
        BaselineTIFFTagSet.TAG_REFERENCE_BLACK_WHITE;

    /** The <code>TIFFTagSets</code> associated with this directory. */
    private List tagSets;

    /** The parent <code>TIFFTag</code> of this directory. */
    private TIFFTag parentTag;

    /**
     * The fields in this directory which have a low tag number. These are
     * managed as an array for efficiency as they are the most common fields.
     */
    private TIFFField[] lowFields = new TIFFField[MAX_LOW_FIELD_TAG_NUM + 1];

    /** The number of low tag numbered fields in the directory. */
    private int numLowFields = 0;

    /**
     * A mapping of <code>Integer</code> tag numbers to <code>TIFFField</code>s
     * for fields which are not low tag numbered.
     */
    private Map highFields = new TreeMap();

    /**
     * Creates a <code>TIFFDirectory</code> instance from the contents of
     * an image metadata object. The supplied object must support an image
     * metadata format supported by the TIFF {@link javax.imageio.ImageWriter}
     * plug-in. This will usually be either the TIFF native image metadata
     * format <tt>com_sun_media_imageio_plugins_tiff_1.0</tt> or the Java
     * Image I/O standard metadata format <tt>javax_imageio_1.0</tt>.
     *
     * @param tiffImageMetadata A metadata object which supports a compatible
     * image metadata format.
     * 
     * @return A <code>TIFFDirectory</code> populated from the contents of
     * the supplied metadata object.
     *
     * @throws IllegalArgumentException if <code>tiffImageMetadata</code>
     * is <code>null</code>.
     * @throws IllegalArgumentException if <code>tiffImageMetadata</code>
     * does not support a compatible image metadata format.
     * @throws IIOInvalidTreeException if the supplied metadata object
     * cannot be parsed.
     */
    public static TIFFDirectory
        createFromMetadata(IIOMetadata tiffImageMetadata)
        throws IIOInvalidTreeException {

        if(tiffImageMetadata == null) {
            throw new IllegalArgumentException("tiffImageMetadata == null");
        }

        TIFFImageMetadata tim;
        if(tiffImageMetadata instanceof TIFFImageMetadata) {
            tim = (TIFFImageMetadata)tiffImageMetadata;
        } else {
            // Create a native metadata object.
            ArrayList l = new ArrayList(1);
            l.add(BaselineTIFFTagSet.getInstance());
            tim = new TIFFImageMetadata(l);

            // Determine the format name to use.
            String formatName = null;
            if(TIFFImageMetadata.nativeMetadataFormatName.equals
               (tiffImageMetadata.getNativeMetadataFormatName())) {
                formatName = TIFFImageMetadata.nativeMetadataFormatName;
            } else {
                String[] extraNames =
                    tiffImageMetadata.getExtraMetadataFormatNames();
                if(extraNames != null) {
                    for(int i = 0; i < extraNames.length; i++) {
                        if(TIFFImageMetadata.nativeMetadataFormatName.equals
                           (extraNames[i])) {
                            formatName = extraNames[i];
                            break;
                        }
                    }
                }

                if(formatName == null) {
                    if(tiffImageMetadata.isStandardMetadataFormatSupported()) {
                        formatName =
                            IIOMetadataFormatImpl.standardMetadataFormatName;
                    } else {
                        throw new IllegalArgumentException
                            ("Parameter does not support required metadata format!");
                    }
                }
            }

            // Set the native metadata object from the tree.
            tim.setFromTree(formatName,
                            tiffImageMetadata.getAsTree(formatName));
        }

        return tim.getRootIFD();
    }

    /**
     * Converts a <code>TIFFDirectory</code> to a <code>TIFFIFD</code>.
     */
    private static TIFFIFD getDirectoryAsIFD(TIFFDirectory dir) {
        if(dir instanceof TIFFIFD) {
            return (TIFFIFD)dir;
        }

        TIFFIFD ifd = new TIFFIFD(Arrays.asList(dir.getTagSets()),
                                  dir.getParentTag());
        TIFFField[] fields = dir.getTIFFFields();
        int numFields = fields.length;
        for(int i = 0; i < numFields; i++) {
            TIFFField f = fields[i];
            TIFFTag tag = f.getTag();
            if(tag.isIFDPointer()) {
                TIFFDirectory subIFD =
                    getDirectoryAsIFD((TIFFDirectory)f.getData());
                f = new TIFFField(tag, f.getType(), f.getCount(), subIFD);
            }
            ifd.addTIFFField(f);
        }

        return ifd;
    }

    /**
     * Constructs a <code>TIFFDirectory</code> which is aware of a given
     * group of {@link TIFFTagSet}s. An optional parent {@link TIFFTag}
     * may also be specified.
     *
     * @param tagSets The <code>TIFFTagSets</code> associated with this
     * directory.
     * @param parentTag The parent <code>TIFFTag</code> of this directory;
     * may be <code>null</code>.
     * @throws IllegalArgumentException if <code>tagSets</code> is
     * <code>null</code>.
     */
    public TIFFDirectory(TIFFTagSet[] tagSets, TIFFTag parentTag) {
        if(tagSets == null) {
            throw new IllegalArgumentException("tagSets == null!");
        }
        this.tagSets = new ArrayList(tagSets.length);
        int numTagSets = tagSets.length;
        for(int i = 0; i < numTagSets; i++) {
            this.tagSets.add(tagSets[i]);
        }
        this.parentTag = parentTag;
    }

    /**
     * Returns the {@link TIFFTagSet}s of which this directory is aware.
     *
     * @return The <code>TIFFTagSet</code>s associated with this
     * <code>TIFFDirectory</code>.
     */
    public TIFFTagSet[] getTagSets() {
        return (TIFFTagSet[])tagSets.toArray(new TIFFTagSet[tagSets.size()]);
    }

    /**
     * Adds an element to the group of {@link TIFFTagSet}s of which this
     * directory is aware.
     *
     * @param tagSet The <code>TIFFTagSet</code> to add.
     * @throws IllegalArgumentException if <code>tagSet</code> is
     * <code>null</code>.
     */
    public void addTagSet(TIFFTagSet tagSet) {
        if(tagSet == null) {
            throw new IllegalArgumentException("tagSet == null");
        }

        if(!tagSets.contains(tagSet)) {
            tagSets.add(tagSet);
        }
    }

    /**
     * Removes an element from the group of {@link TIFFTagSet}s of which this
     * directory is aware.
     *
     * @param tagSet The <code>TIFFTagSet</code> to remove.
     * @throws IllegalArgumentException if <code>tagSet</code> is
     * <code>null</code>.
     */
    public void removeTagSet(TIFFTagSet tagSet) {
        if(tagSet == null) {
            throw new IllegalArgumentException("tagSet == null");
        }

        if(tagSets.contains(tagSet)) {
            tagSets.remove(tagSet);
        }
    }

    /**
     * Returns the parent {@link TIFFTag} of this directory if one
     * has been defined or <code>null</code> otherwise.
     *
     * @return The parent <code>TIFFTag</code> of this
     * <code>TIFFDiectory</code> or <code>null</code>.
     */
    public TIFFTag getParentTag() {
        return parentTag;
    }

    /**
     * Returns the {@link TIFFTag} which has tag number equal to
     * <code>tagNumber</code> or <code>null</code> if no such tag
     * exists in the {@link TIFFTagSet}s associated with this
     * directory.
     *
     * @param tagNumber The tag number of interest.
     * @return The corresponding <code>TIFFTag</code> or <code>null</code>.
     */
    public TIFFTag getTag(int tagNumber) {
        return TIFFIFD.getTag(tagNumber, tagSets);
    }

    /**
     * Returns the number of {@link TIFFField}s in this directory.
     *
     * @return The number of <code>TIFFField</code>s in this
     * <code>TIFFDirectory</code>.
     */
    public int getNumTIFFFields() {
        return numLowFields + highFields.size();
    }

    /**
     * Determines whether a TIFF field with the given tag number is
     * contained in this directory.
     *
     * @return Whether a {@link TIFFTag} with tag number equal to
     * <code>tagNumber</code> is present in this <code>TIFFDirectory</code>.
     */
    public boolean containsTIFFField(int tagNumber) {
        return (tagNumber >= 0 && tagNumber <= MAX_LOW_FIELD_TAG_NUM &&
                lowFields[tagNumber] != null) ||
            highFields.containsKey(new Integer(tagNumber));
    }

    /**
     * Adds a TIFF field to the directory.
     *
     * @param f The field to add.
     * @throws IllegalArgumentException if <code>f</code> is <code>null</code>.
     */
    public void addTIFFField(TIFFField f) {
        if(f == null) {
            throw new IllegalArgumentException("f == null");
        }
        int tagNumber = f.getTagNumber();
        if(tagNumber >= 0 && tagNumber <= MAX_LOW_FIELD_TAG_NUM) {
            if(lowFields[tagNumber] == null) {
                numLowFields++;
            }
            lowFields[tagNumber] = f;
        } else {
            highFields.put(new Integer(tagNumber), f);
        }
    }

    /**
     * Retrieves a TIFF field from the directory.
     *
     * @param tagNumber The tag number of the tag associated with the field.
     * @return A <code>TIFFField</code> with the requested tag number of
     * <code>null</code> if no such field is present.
     */
    public TIFFField getTIFFField(int tagNumber) {
        TIFFField f;
        if(tagNumber >= 0 && tagNumber <= MAX_LOW_FIELD_TAG_NUM) {
            f = lowFields[tagNumber];
        } else {
            f = (TIFFField)highFields.get(new Integer(tagNumber));
        }
        return f;
    }

    /**
     * Removes a TIFF field from the directory.
     *
     * @param tagNumber The tag number of the tag associated with the field.
     */
    public void removeTIFFField(int tagNumber) {
        if(tagNumber >= 0 && tagNumber <= MAX_LOW_FIELD_TAG_NUM) {
            if(lowFields[tagNumber] != null) {
                numLowFields--;
                lowFields[tagNumber] = null;
            }
        } else {
            highFields.remove(new Integer(tagNumber));
        }
    }

    /**
     * Retrieves all TIFF fields from the directory.
     *
     * @return An array of all TIFF fields in order of numerically increasing
     * tag number.
     */
    public TIFFField[] getTIFFFields() {
        // Allocate return value.
        TIFFField[] fields = new TIFFField[numLowFields + highFields.size()];

        // Copy any low-index fields.
        int nextIndex = 0;
        for(int i = 0; i <= MAX_LOW_FIELD_TAG_NUM; i++) {
            if(lowFields[i] != null) {
                fields[nextIndex++] = lowFields[i];
                if(nextIndex == numLowFields) break;
            }
        }

        // Copy any high-index fields.
        if(!highFields.isEmpty()) {
            Iterator keys = highFields.keySet().iterator();
            while(keys.hasNext()) {
                fields[nextIndex++] = (TIFFField)highFields.get(keys.next());
            }
        }

        return fields;
    }

    /**
     * Removes all TIFF fields from the directory.
     */
    public void removeTIFFFields() {
        Arrays.fill(lowFields, (Object)null);
        numLowFields = 0;
        highFields.clear();
    }

    /**
     * Converts the directory to a metadata object.
     *
     * @return A metadata instance initialized from the contents of this
     * <code>TIFFDirectory</code>.
     */
    public IIOMetadata getAsMetadata() {
        return new TIFFImageMetadata(getDirectoryAsIFD(this));
    }

    /**
     * Clones the directory and all the fields contained therein.
     *
     * @return A clone of this <code>TIFFDirectory</code>.
     */
    public Object clone() {
        TIFFDirectory dir = new TIFFDirectory(getTagSets(), getParentTag());
        TIFFField[] fields = getTIFFFields();
        int numFields = fields.length;
        for(int i = 0; i < numFields; i++) {
            dir.addTIFFField(fields[i]);
        }

        return dir;
    }
}
