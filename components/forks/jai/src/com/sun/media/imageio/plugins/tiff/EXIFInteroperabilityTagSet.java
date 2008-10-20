/*
 * $RCSfile: EXIFInteroperabilityTagSet.java,v $
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
 * $Date: 2005/10/28 16:56:45 $
 * $State: Exp $
 */

package com.sun.media.imageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the tags found in an EXIF Interoperability IFD.
 *
 * @see EXIFTIFFTagSet
 */
public class EXIFInteroperabilityTagSet extends TIFFTagSet {
    /**
     * A tag indicating the identification of the Interoperability rule
     * (type ASCII).
     *
     * @see #INTEROPERABILITY_INDEX_R98
     * @see #INTEROPERABILITY_INDEX_THM
     */
    public static final int TAG_INTEROPERABILITY_INDEX = 1;

    /**
     * A value to be used with the "InteroperabilityIndex" tag. Indicates
     * a file conforming to the R98 file specification of Recommended Exif
     * Interoperability Rules (ExifR98) or to the DCF basic file stipulated
     * by the Design Rule for Camera File System (type ASCII).
     *
     * @see #TAG_INTEROPERABILITY_INDEX
     */
    public static final String INTEROPERABILITY_INDEX_R98 = "R98";

    /**
     * A value to be used with the "InteroperabilityIndex" tag. Indicates
     * a file conforming to the DCF thumbnail file stipulated by the Design
     * rule for Camera File System (type ASCII).
     *
     * @see #TAG_INTEROPERABILITY_INDEX
     */
    public static final String INTEROPERABILITY_INDEX_THM = "THM";

    private static EXIFInteroperabilityTagSet theInstance = null;

    static class InteroperabilityIndex extends TIFFTag {

        public InteroperabilityIndex() {
            super("InteroperabilityIndex",
                  TAG_INTEROPERABILITY_INDEX,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    private static List tags;

    private static void initTags() {
        tags = new ArrayList(42);

        tags.add(new EXIFInteroperabilityTagSet.InteroperabilityIndex());
    }

    private EXIFInteroperabilityTagSet() {
        super(tags);
    }

    /**
     * Returns the shared instance of
     * <code>EXIFInteroperabilityTagSet</code>.
     *
     * @return the <code>EXIFInteroperabilityTagSet</code> instance.
     */
    public synchronized static EXIFInteroperabilityTagSet getInstance() {
        if (theInstance == null) {
            initTags();
            theInstance = new EXIFInteroperabilityTagSet();
            tags = null;
        }
        return theInstance;
    }
}
