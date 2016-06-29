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
 * $RCSfile: TIFFImageMetadataFormat.java,v $
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;

public class TIFFImageMetadataFormat extends TIFFMetadataFormat {

    private static TIFFImageMetadataFormat theInstance = null;

    static {
    }

    public boolean canNodeAppear(String elementName,
                                 ImageTypeSpecifier imageType) {
        return false;
    }

    private TIFFImageMetadataFormat() {
        this.resourceBaseName =
     "com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadataFormatResources";
        this.rootName = TIFFImageMetadata.nativeMetadataFormatName;

        TIFFElementInfo einfo;
        TIFFAttrInfo ainfo;
        String[] empty = new String[0];
        String[] childNames;
        String[] attrNames;

        childNames = new String[] { "TIFFIFD" };
        einfo = new TIFFElementInfo(childNames, empty, CHILD_POLICY_SEQUENCE);

        elementInfoMap.put(TIFFImageMetadata.nativeMetadataFormatName,
                           einfo);

        childNames = new String[] { "TIFFField", "TIFFIFD" };
        attrNames =
            new String[] { "tagSets", "parentTagNumber", "parentTagName" };
        einfo = new TIFFElementInfo(childNames, attrNames, CHILD_POLICY_SEQUENCE);
        elementInfoMap.put("TIFFIFD", einfo);

        ainfo = new TIFFAttrInfo();
        ainfo.dataType = DATATYPE_STRING;
        ainfo.isRequired = true;
        attrInfoMap.put("TIFFIFD/tagSets", ainfo);

        ainfo = new TIFFAttrInfo();
        ainfo.dataType = DATATYPE_INTEGER;
        ainfo.isRequired = false;
        attrInfoMap.put("TIFFIFD/parentTagNumber", ainfo);

        ainfo = new TIFFAttrInfo();
        ainfo.dataType = DATATYPE_STRING;
        ainfo.isRequired = false;
        attrInfoMap.put("TIFFIFD/parentTagName", ainfo);

        String[] types = {
            "TIFFByte",
            "TIFFAscii",
            "TIFFShort",
            "TIFFSShort",
            "TIFFLong",
            "TIFFSLong",
            "TIFFRational",
            "TIFFSRational",
            "TIFFFloat",
            "TIFFDouble",
            "TIFFUndefined"
        };

        attrNames = new String[] { "value", "description" };
        String[] attrNamesValueOnly = new String[] { "value" };
        TIFFAttrInfo ainfoValue = new TIFFAttrInfo();
        TIFFAttrInfo ainfoDescription = new TIFFAttrInfo();

        for (int i = 0; i < types.length; i++) {
            if (!types[i].equals("TIFFUndefined")) {
                childNames = new String[1];
                childNames[0] = types[i];
                einfo =
                    new TIFFElementInfo(childNames, empty, CHILD_POLICY_SEQUENCE);
                elementInfoMap.put(types[i] + "s", einfo);
            }

            boolean hasDescription =
                !types[i].equals("TIFFUndefined") &&
                !types[i].equals("TIFFAscii") &&
                !types[i].equals("TIFFRational") &&
                !types[i].equals("TIFFSRational") &&
                !types[i].equals("TIFFFloat") &&
                !types[i].equals("TIFFDouble");

            String[] anames = hasDescription ? attrNames : attrNamesValueOnly;
            einfo = new TIFFElementInfo(empty, anames, CHILD_POLICY_EMPTY);
            elementInfoMap.put(types[i], einfo);

            attrInfoMap.put(types[i] + "/value", ainfoValue);
            if (hasDescription) {
                attrInfoMap.put(types[i] + "/description", ainfoDescription);
            }
        }
            
        childNames = new String[2*types.length - 1];
        for (int i = 0; i < types.length; i++) {
            childNames[2*i] = types[i];
            if (!types[i].equals("TIFFUndefined")) {
                childNames[2*i + 1] = types[i] + "s";
            }
        }
        attrNames = new String[] { "number", "name" };
        einfo = new TIFFElementInfo(childNames, attrNames, CHILD_POLICY_CHOICE);
        elementInfoMap.put("TIFFField", einfo);

        ainfo = new TIFFAttrInfo();
        ainfo.isRequired = true;
        attrInfoMap.put("TIFFField/number", ainfo);

        ainfo = new TIFFAttrInfo();
        attrInfoMap.put("TIFFField/name", ainfo);
    }

    public static synchronized IIOMetadataFormat getInstance() {
        if (theInstance == null) {
            theInstance = new TIFFImageMetadataFormat();
        }
        return theInstance;
    }
}
