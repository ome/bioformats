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
 * $RCSfile: TIFFImageMetadataFormatResources.java,v $
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
 * $Date: 2005/02/11 05:01:46 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.util.ListResourceBundle;

public class TIFFImageMetadataFormatResources extends ListResourceBundle {

    private static final Object[][] contents = {
        { "TIFFIFD", "An IFD (directory) containing fields" },
        { "TIFFIFD/parentTagNumber",
          "The tag number of the field pointing to this IFD" },
        { "TIFFIFD/parentTagName",
          "A mnemonic name for the field pointing to this IFD, if known" },
        { "TIFFField", "A field containing data" },
        { "TIFFField/number", "The tag number asociated with the field" },
        { "TIFFField/name",
          "A mnemonic name associated with the field, if known" },

        { "TIFFUndefined", "Uninterpreted byte data" },
        { "TIFFUndefined/value", "A list of comma-separated byte values" },

        { "TIFFBytes", "A sequence of TIFFByte nodes" },
        { "TIFFByte", "An integral value between 0 and 255" },
        { "TIFFByte/value", "The value" },
        { "TIFFByte/description", "A description, if available" },

        { "TIFFAsciis", "A sequence of TIFFAscii nodes" },
        { "TIFFAscii", "A String value" },
        { "TIFFAscii/value", "The value" },

        { "TIFFShorts", "A sequence of TIFFShort nodes" },
        { "TIFFShort", "An integral value between 0 and 65535" },
        { "TIFFShort/value", "The value" },
        { "TIFFShort/description", "A description, if available" },

        { "TIFFSShorts", "A sequence of TIFFSShort nodes" },
        { "TIFFSShort", "An integral value between -32768 and 32767" },
        { "TIFFSShort/value", "The value" },
        { "TIFFSShort/description", "A description, if available" },

        { "TIFFLongs", "A sequence of TIFFLong nodes" },
        { "TIFFLong", "An integral value between 0 and 4294967295" },
        { "TIFFLong/value", "The value" },
        { "TIFFLong/description", "A description, if available" },

        { "TIFFSLongs", "A sequence of TIFFSLong nodes" },
        { "TIFFSLong", "An integral value between -2147483648 and 2147483647" },
        { "TIFFSLong/value", "The value" },
        { "TIFFSLong/description", "A description, if available" },

        { "TIFFRationals", "A sequence of TIFFRational nodes" },
        { "TIFFRational",
          "A rational value consisting of an unsigned numerator and denominator" },
        { "TIFFRational/value",
          "The numerator and denominator, separated by a slash" },

        { "TIFFSRationals", "A sequence of TIFFSRational nodes" },
        { "TIFFSRational",
          "A rational value consisting of a signed numerator and denominator" },
        { "TIFFSRational/value",
          "The numerator and denominator, separated by a slash" },

        { "TIFFFloats", "A sequence of TIFFFloat nodes" },
        { "TIFFFloat", "A single-precision floating-point value" },
        { "TIFFFloat/value", "The value" },

        { "TIFFDoubles", "A sequence of TIFFDouble nodes" },
        { "TIFFDouble", "A double-precision floating-point value" },
        { "TIFFDouble/value", "The value" },

    };

    public TIFFImageMetadataFormatResources() {
    }

    public Object[][] getContents() {
        return contents;
    }
}
