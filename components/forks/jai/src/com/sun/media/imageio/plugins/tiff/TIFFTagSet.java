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
 * $RCSfile: TIFFTagSet.java,v $
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
 * $Date: 2005/02/11 05:01:19 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

// Should implement Set?

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class representing a set of TIFF tags.  Each tag in the set must
 * have a unique number (this is a limitation of the TIFF
 * specification itself).
 *
 * <p> This class and its subclasses are responsible for mapping
 * between raw tag numbers and <code>TIFFTag</code> objects, which
 * contain additional information about each tag, such as the tag's
 * name, legal data types, and mnemonic names for some or all of ts
 * data values.
 *
 * @see TIFFTag
 */
public class TIFFTagSet {

    private SortedMap allowedTagsByNumber = new TreeMap();

    private SortedMap allowedTagsByName = new TreeMap();
   
    /**
     * Constructs a TIFFTagSet.
     */
    private TIFFTagSet() {}

    /**
     * Constructs a <code>TIFFTagSet</code>, given a <code>List</code>
     * of <code>TIFFTag</code> objects.
     *
     * @param tags a <code>List</code> object containing
     * <code>TIFFTag</code> objects to be added to this tag set.
     *
     * @throws IllegalArgumentException if <code>tags</code> is
     * <code>null</code>, or contains objects that are not instances
     * of the <code>TIFFTag</code> class.
     */
    public TIFFTagSet(List tags) {
        if (tags == null) {
            throw new IllegalArgumentException("tags == null!");
        }
        Iterator iter = tags.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (!(o instanceof TIFFTag)) {
                throw new IllegalArgumentException(
                                               "tags contains a non-TIFFTag!");
            }
            TIFFTag tag = (TIFFTag)o;

            allowedTagsByNumber.put(new Integer(tag.getNumber()), tag);
            allowedTagsByName.put(tag.getName(), tag);
        }
    }

    /**
     * Returns the <code>TIFFTag</code> from this set that is
     * associated with the given tag number, or <code>null</code> if
     * no tag exists for that number.
     *
     * @param tagNumber the number of the tag to be retrieved.
     *
     * @return the numbered <code>TIFFTag</code>, or <code>null</code>.
     */
    public TIFFTag getTag(int tagNumber) {
        return (TIFFTag)allowedTagsByNumber.get(new Integer(tagNumber));
    }

    /**
     * Returns the <code>TIFFTag</code> having the given tag name, or
     * <code>null</code> if the named tag does not belong to this tag set.
     *
     * @param tagName the name of the tag to be retrieved, as a
     * <code>String</code>.
     *
     * @return the named <code>TIFFTag</code>, or <code>null</code>.
     *
     * @throws IllegalArgumentException if <code>tagName</code> is
     * <code>null</code>.
     */
    public TIFFTag getTag(String tagName) {
        if (tagName == null) {
            throw new IllegalArgumentException("tagName == null!");
        }
        return (TIFFTag)allowedTagsByName.get(tagName);
    }

    /**
     * Retrieves an unmodifiable numerically increasing set of tag numbers.
     *
     * <p>The returned object is unmodifiable and contains the tag
     * numbers of all <code>TIFFTag</code>s in this <code>TIFFTagSet</code>
     * sorted into ascending order according to
     * {@link <code>Integer#compareTo(Object)</code>}.</p>
     *
     * @return All tag numbers in this set.
     */
    public SortedSet getTagNumbers() {
        Set tagNumbers = allowedTagsByNumber.keySet();
        SortedSet sortedTagNumbers;
        if(tagNumbers instanceof SortedSet) {
            sortedTagNumbers = (SortedSet)tagNumbers;
        } else {
            sortedTagNumbers = new TreeSet(tagNumbers);
        }

        return Collections.unmodifiableSortedSet(sortedTagNumbers);
    }

    /**
     * Retrieves an unmodifiable lexicographically increasing set of tag names.
     *
     * <p>The returned object is unmodifiable and contains the tag
     * names of all <code>TIFFTag</code>s in this <code>TIFFTagSet</code>
     * sorted into ascending order according to
     * {@link <code>String#compareTo(Object)</code>}.</p>
     *
     * @return All tag names in this set.
     */
    public SortedSet getTagNames() {
        Set tagNames = allowedTagsByName.keySet();
        SortedSet sortedTagNames;
        if(tagNames instanceof SortedSet) {
            sortedTagNames = (SortedSet)tagNames;
        } else {
            sortedTagNames = new TreeSet(tagNames);
        }

        return Collections.unmodifiableSortedSet(sortedTagNames);
    }
}
