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
 * $RCSfile: J2KMetadataFormat.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2005/04/27 18:23:01 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.imageio.metadata.*;
import javax.imageio.ImageTypeSpecifier;

public class J2KMetadataFormat extends IIOMetadataFormatImpl {
    /** The table to link the child to its parent.
     */
    private static Hashtable parents = new Hashtable();

    static {
        //children for the root
        parents.put("JPEG2000SignatureBox", "com_sun_media_imageio_plugins_jpeg2000_image_1.0");
        parents.put("JPEG2000FileTypeBox", "com_sun_media_imageio_plugins_jpeg2000_image_1.0");
        parents.put("OtherBoxes", "com_sun_media_imageio_plugins_jpeg2000_image_1.0");

        // children for the boxes other than
        // JPEG2000SignatureBox/JPEG2000FileTypeBox
        parents.put("JPEG2000HeaderSuperBox", "OtherBoxes");
        parents.put("JPEG2000CodeStreamBox", "OtherBoxes");

        parents.put("JPEG2000IntellectualPropertyRightsBox", "OtherBoxes");
        parents.put("JPEG2000XMLBox", "OtherBoxes");
        parents.put("JPEG2000UUIDBox", "OtherBoxes");
        parents.put("JPEG2000UUIDInfoBox", "OtherBoxes");

        // Children of JPEG2000HeaderSuperBox
        parents.put("JPEG2000HeaderBox", "JPEG2000HeaderSuperBox");
        parents.put("OptionalBoxes", "JPEG2000HeaderSuperBox");

        // Optional boxes in JPEG2000HeaderSuperBox
        parents.put("JPEG2000BitsPerComponentBox", "OptionalBoxes");
        parents.put("JPEG2000ColorSpecificationBox", "OptionalBoxes");
        parents.put("JPEG2000PaletteBox", "OptionalBoxes");
        parents.put("JPEG2000ComponentMappingBox", "OptionalBoxes");
        parents.put("JPEG2000ChannelDefinitionBox", "OptionalBoxes");
        parents.put("JPEG2000ResolutionBox", "OptionalBoxes");

        // Children of JPEG2000ResolutionBox
        parents.put("JPEG2000CaptureResolutionBox", "JPEG2000ResolutionBox");
        parents.put("JPEG2000DefaultDisplayResolutionBox",
                    "JPEG2000ResolutionBox");

        // Children of JPEG2000UUIDInfoBox
        parents.put("JPEG2000UUIDListBox", "JPEG2000UUIDInfoBox");
        parents.put("JPEG2000DataEntryURLBox", "JPEG2000UUIDInfoBox");
    }

    private static J2KMetadataFormat instance;

    public static synchronized J2KMetadataFormat getInstance() {
        if (instance == null)
            instance = new J2KMetadataFormat();
        return instance;
    }

    String resourceBaseName = this.getClass().getName() + "Resources";

    /** Constructs <code>J2KMetadataFormat</code>.  Calls the super
     *  class constructor.  Sets the resource base name.  Adds the elements
     *  into this format object based on the XML schema and DTD.
     */
    J2KMetadataFormat() {
        super("com_sun_media_imageio_plugins_jpeg2000_image_1.0", CHILD_POLICY_ALL);
        setResourceBaseName(resourceBaseName);
        addElements();
    }

    /** Adds the elements into this format object based on the XML
     *  schema and DTD.
     */
    private void addElements() {
        addElement("JPEG2000SignatureBox",
                      getParent("JPEG2000SignatureBox"),
                      CHILD_POLICY_EMPTY);

        addElement("JPEG2000FileTypeBox",
                      getParent("JPEG2000FileTypeBox"),
                      CHILD_POLICY_ALL);
        addElement("OtherBoxes",
                      getParent("OtherBoxes"),
                      CHILD_POLICY_CHOICE);

        addElement("JPEG2000HeaderSuperBox",
                      getParent("JPEG2000HeaderSuperBox"),
                      CHILD_POLICY_CHOICE);
        addElement("JPEG2000CodeStreamBox",
                      getParent("JPEG2000CodeStreamBox"),
                      CHILD_POLICY_EMPTY);
        addElement("JPEG2000IntellectualPropertyRightsBox",
                      getParent("JPEG2000IntellectualPropertyRightsBox"),
                      CHILD_POLICY_ALL);
        addElement("JPEG2000XMLBox",
                      getParent("JPEG2000XMLBox"),
                      CHILD_POLICY_ALL);
        addElement("JPEG2000UUIDBox",
                      getParent("JPEG2000UUIDBox"),
                      CHILD_POLICY_ALL);
        addElement("JPEG2000UUIDInfoBox",
                      getParent("JPEG2000UUIDInfoBox"),
                      CHILD_POLICY_ALL);

        addElement("JPEG2000HeaderBox",
                      "JPEG2000HeaderSuperBox",
                      CHILD_POLICY_ALL);
        addElement("OptionalBoxes",
                      "JPEG2000HeaderSuperBox",
                      CHILD_POLICY_CHOICE);
        addElement("JPEG2000BitsPerComponentBox",
                      "OptionalBoxes",
                      CHILD_POLICY_ALL);
        addElement("JPEG2000ColorSpecificationBox",
                      "OptionalBoxes",
                      CHILD_POLICY_ALL);
        addElement("JPEG2000PaletteBox",
                      "OptionalBoxes",
                      CHILD_POLICY_ALL);
        addElement("JPEG2000ComponentMappingBox",
                      "OptionalBoxes",
                      CHILD_POLICY_ALL);
        addElement("JPEG2000ChannelDefinitionBox",
                      "OptionalBoxes",
                      CHILD_POLICY_ALL);
        addElement("JPEG2000ResolutionBox",
                      "OptionalBoxes",
                      CHILD_POLICY_ALL);

        addElement("JPEG2000CaptureResolutionBox",
                   "JPEG2000ResolutionBox",
                   CHILD_POLICY_ALL);
        addElement("JPEG2000DefaultDisplayResolutionBox",
                   "JPEG2000ResolutionBox",
                   CHILD_POLICY_ALL);

        addElement("JPEG2000UUIDListBox",
                      "JPEG2000UUIDInfoBox",
                      CHILD_POLICY_ALL);
        addElement("JPEG2000DataEntryURLBox",
                      "JPEG2000UUIDInfoBox",
                      CHILD_POLICY_ALL);
        // Adds the default attributes "Length", "Type" and "ExtraLength" into
        // the J2K box-related data elements
        Enumeration keys = parents.keys();
        while (keys.hasMoreElements()) {
            String s = (String)keys.nextElement();
            if (s.startsWith("JPEG2000")) {
                addAttribute(s, "Length", DATATYPE_INTEGER, true, null);
                addAttribute(s, "Type", DATATYPE_STRING, true, Box.getTypeByName(s));
                addAttribute(s, "ExtraLength", DATATYPE_STRING, false, null);

                // If it is a simple node, adds the data elements by using
                // relection.
                Class c = Box.getBoxClass(Box.getTypeInt(Box.getTypeByName(s)));

                try {
                    Method m = c.getMethod("getElementNames", (Class[])null);
                    String[] elementNames = (String[])m.invoke(null,
                                                               (Object[])null);
                    for (int i = 0; i < elementNames.length; i++)
                        addElement(elementNames[i], s, CHILD_POLICY_EMPTY);
                } catch (Exception e) {
                    // no such method
                }
            }
        }

        addAttribute("JPEG2000SignatureBox",
                     "Signature",
                     DATATYPE_STRING,
                     true,
                     "0D0A870A");

        addElement("BitDepth",
                      "JPEG2000BitsPerComponentBox",
                      CHILD_POLICY_EMPTY);

        addElement("NumberEntries",
                      "JPEG2000PaletteBox",
                      CHILD_POLICY_EMPTY);

        addElement("NumberColors",
                   "JPEG2000PaletteBox",
                   CHILD_POLICY_EMPTY);

        addElement("BitDepth",
                   "JPEG2000PaletteBox",
                   CHILD_POLICY_EMPTY);

        addElement("LUT",
                   "JPEG2000PaletteBox",
                   1, 1024);

        addElement("LUTRow",
                   "LUT",
                   CHILD_POLICY_EMPTY);

        addElement("Component",
                   "JPEG2000ComponentMappingBox",
                   CHILD_POLICY_EMPTY);

        addElement("ComponentType",
                   "JPEG2000ComponentMappingBox",
                   CHILD_POLICY_EMPTY);

        addElement("ComponentAssociation",
                   "JPEG2000ComponentMappingBox",
                   CHILD_POLICY_EMPTY);

        addElement("NumberOfDefinition",
                   "JPEG2000ChannelDefinitionBox",
                   CHILD_POLICY_EMPTY);

        addElement("Definitions",
                   "JPEG2000ChannelDefinitionBox",
                   0, 9);

        addElement("ChannelNumber",
                   "Definitions",
                   CHILD_POLICY_EMPTY);

        addElement("ChannelType",
                   "Definitions",
                   CHILD_POLICY_EMPTY);
        addElement("ChannelAssociation",
                   "Definitions",
                   CHILD_POLICY_EMPTY);
        addElement("CodeStream",
                   "JPEG2000CodeStreamBox",
                   CHILD_POLICY_EMPTY);
        addElement("Content",
                   "JPEG2000IntellectualPropertyRightsBox",
                   CHILD_POLICY_EMPTY);
        addElement("Content",
                      "JPEG2000XMLBox",
                      CHILD_POLICY_EMPTY);
        addElement("UUID",
                      "JPEG2000UUIDBox",
                      CHILD_POLICY_EMPTY);
        addElement("Data",
                      "JPEG2000UUIDBox",
                      CHILD_POLICY_EMPTY);
        addElement("NumberUUID",
                      "JPEG2000UUIDListBox",
                      CHILD_POLICY_EMPTY);
        addElement("UUID",
                      "JPEG2000UUIDListBox",
                      CHILD_POLICY_EMPTY);
        addElement("Version",
                      "JPEG2000DataEntryURLBox",
                      CHILD_POLICY_EMPTY);
        addElement("Flags",
                      "JPEG2000DataEntryURLBox",
                      CHILD_POLICY_EMPTY);
        addElement("URL",
                      "JPEG2000DataEntryURLBox",
                      CHILD_POLICY_EMPTY);
    }

    public String getParent(String elementName) {
        return (String)parents.get(elementName);
    }

    public boolean canNodeAppear(String elementName,
                                 ImageTypeSpecifier imageType) {
        ColorModel cm = imageType.getColorModel();
        if (!(cm instanceof IndexColorModel))
            if ("JPEG2000PaletteBox".equals(elementName))
                return false;
        if (!cm.hasAlpha())
            if ("JPEG2000ChannelDefinitionBox".equals(elementName))
                return false;

        if (getParent(elementName) != null)
            return true;
        return false;
    }

    public boolean isLeaf(String name) {
        Set keys = parents.keySet();
        Iterator iterator = keys.iterator();
        while(iterator.hasNext()) {
            if (name.equals(parents.get(iterator.next())))
                return false;
        }

        return true;
    }

    public boolean singleInstance(String name) {
        return !(name.equals("JPEG2000IntellectualPropertyRightsBox") ||
                 name.equals("JPEG2000XMLBox") ||
                 name.equals("JPEG2000UUIDBox") ||
                 name.equals("JPEG2000UUIDInfoBox") ||
                 name.equals("JPEG2000UUIDListBox") ||
                 name.equals("JPEG2000DataEntryURLBox"));
    }
}
