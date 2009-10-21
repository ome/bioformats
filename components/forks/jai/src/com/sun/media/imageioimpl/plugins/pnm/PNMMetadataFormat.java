/*
 * $RCSfile: PNMMetadataFormat.java,v $
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
 * $Date: 2005/02/11 05:01:41 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pnm;

import java.util.Hashtable;
import javax.imageio.metadata.*;
import javax.imageio.ImageTypeSpecifier;

public class PNMMetadataFormat extends IIOMetadataFormatImpl {
    /** The table to link the child to its parent.
     */
    private static Hashtable parents = new Hashtable();

    static {
        //children for the root
        parents.put("FormatName", "com_sun_media_imageio_plugins_pnm_image_1.0");
        parents.put("Variant", "com_sun_media_imageio_plugins_pnm_image_1.0");
        parents.put("Width", "com_sun_media_imageio_plugins_pnm_image_1.0");
        parents.put("Height", "com_sun_media_imageio_plugins_pnm_image_1.0");
        parents.put("MaximumSample", "com_sun_media_imageio_plugins_pnm_image_1.0");
        parents.put("Comment", "com_sun_media_imageio_plugins_pnm_image_1.0");
    }

    private static PNMMetadataFormat instance;

    public static synchronized PNMMetadataFormat getInstance() {
        if (instance == null)
            instance = new PNMMetadataFormat();
        return instance;
    }

    String resourceBaseName = this.getClass().getName() + "Resources";

    /** Constructs <code>PNMMetadataFormat</code>.  Calls the super
     *  class constructor.  Sets the resource base name.  Adds the elements
     *  into this format object based on the XML schema and DTD.
     */
    PNMMetadataFormat() {
        super("com_sun_media_imageio_plugins_pnm_image_1.0", CHILD_POLICY_ALL);
        setResourceBaseName(resourceBaseName);
        addElements();
    }

    /** Adds the elements into this format object based on the XML
     *  schema and DTD.
     */
    private void addElements() {
        addElement("FormatName",
                      getParent("FormatName"),
                      CHILD_POLICY_EMPTY);

        addElement("Variant",
                      getParent("Variant"),
                      CHILD_POLICY_EMPTY);
        addElement("Width",
                      getParent("Width"),
                      CHILD_POLICY_EMPTY);
        addElement("Height",
                      getParent("Height"),
                      CHILD_POLICY_EMPTY);
        addElement("MaximumSample",
                      getParent("MaximumSample"),
                      CHILD_POLICY_EMPTY);
        addElement("Comment",
                      getParent("Comment"),
                      CHILD_POLICY_EMPTY);
    }

    public String getParent(String elementName) {
        return (String)parents.get(elementName);
    }

    public boolean canNodeAppear(String elementName,
                                 ImageTypeSpecifier imageType) {
        if (getParent(elementName) != null)
            return true;
        return false;
    }
}
