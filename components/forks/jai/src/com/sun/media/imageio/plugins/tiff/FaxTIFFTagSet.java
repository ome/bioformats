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
 * $RCSfile: FaxTIFFTagSet.java,v $
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
 * $Date: 2005/02/11 05:01:17 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the extra tags found in a
 * <a href="http://palimpsest.stanford.edu/bytopic/imaging/std/tiff-f.html">
 * TIFF-F</a> (RFC 2036) file.
 */
public class FaxTIFFTagSet extends TIFFTagSet {

    private static FaxTIFFTagSet theInstance = null;

    // RFC 2036 - TIFF-F extensions

    /** Tag indicating the number of bad fax lines (type SHORT or LONG). */
    public static final int TAG_BAD_FAX_LINES = 326;

    /**
     * Tag indicating the number of lines of clean fax data (type
     * SHORT).
     *
     * @see #CLEAN_FAX_DATA_NO_ERRORS
     * @see #CLEAN_FAX_DATA_ERRORS_CORRECTED
     * @see #CLEAN_FAX_DATA_ERRORS_UNCORRECTED
     */
    public static final int TAG_CLEAN_FAX_DATA = 327;

    /**
     * A value to be used with the "CleanFaxData" tag.
     *
     * @see #TAG_CLEAN_FAX_DATA
     */
    public static final int CLEAN_FAX_DATA_NO_ERRORS = 0;

    /**
     * A value to be used with the "CleanFaxData" tag.
     *
     * @see #TAG_CLEAN_FAX_DATA
     */
    public static final int CLEAN_FAX_DATA_ERRORS_CORRECTED = 1;

    /**
     * A value to be used with the "CleanFaxData" tag.
     *
     * @see #TAG_CLEAN_FAX_DATA
     */
    public static final int CLEAN_FAX_DATA_ERRORS_UNCORRECTED = 2;

    /**
     * Tag indicating the number of consecutive bad lines (type
     * SHORT or LONG).
     */
    public static final int TAG_CONSECUTIVE_BAD_LINES = 328;

    // TIFF-F extensions (RFC 2306)

    // XXX TIFF-F suggested extensions (FaxImageFlags, etc.)?

    static class BadFaxLines extends TIFFTag {
        
        public BadFaxLines() {
            super("BadFaxLines",
                  TAG_BAD_FAX_LINES,
                  1 << TIFF_SHORT |
                  1 << TIFF_LONG);
        }
    }

    static class CleanFaxData extends TIFFTag {

        public CleanFaxData() {
            super("CleanFaxData",
                  TAG_CLEAN_FAX_DATA,
                  1 << TIFF_SHORT);

            addValueName(CLEAN_FAX_DATA_NO_ERRORS,
                         "No errors");
            addValueName(CLEAN_FAX_DATA_ERRORS_CORRECTED,
                         "Errors corrected");
            addValueName(CLEAN_FAX_DATA_ERRORS_UNCORRECTED,
                         "Errors uncorrected");
        }
    }

    static class ConsecutiveBadFaxLines extends TIFFTag {

        public ConsecutiveBadFaxLines() {
            super("ConsecutiveBadFaxLines",
                  TAG_CONSECUTIVE_BAD_LINES,
                  1 << TIFF_SHORT |
                  1 << TIFF_LONG);
        }
    }

    private static List tags;

    private static void initTags() {
        tags = new ArrayList(42);

        tags.add(new FaxTIFFTagSet.BadFaxLines());
        tags.add(new FaxTIFFTagSet.CleanFaxData());
        tags.add(new FaxTIFFTagSet.ConsecutiveBadFaxLines());
    }

    private FaxTIFFTagSet() {
        super(tags);
    }

    /**
     * Returns a shared instance of a <code>FaxTIFFTagSet</code>.
     *
     * @return a <code>FaxTIFFTagSet</code> instance.
     */
    public synchronized static FaxTIFFTagSet getInstance() {
        if (theInstance == null) {
            initTags();
            theInstance = new FaxTIFFTagSet();
            tags = null;
        }
        return theInstance;
    }
}
