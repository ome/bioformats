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
 * $RCSfile: GIFMetadata.java,v $
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
 * $Date: 2006/03/24 22:30:10 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

/**
 * Class which adds utility DOM element attribute access methods to
 * <code>IIOMetadata</code> for subclass use.
 */
abstract class GIFMetadata extends IIOMetadata {

    /**
     * Represents an undefined value of integer attributes.
     */
    static final int UNDEFINED_INTEGER_VALUE = -1;

    //
    // Note: These attribute methods were shamelessly lifted from
    // com.sun.imageio.plugins.png.PNGMetadata and modified.
    //

    // Shorthand for throwing an IIOInvalidTreeException
    protected static void fatal(Node node, String reason)
      throws IIOInvalidTreeException {
        throw new IIOInvalidTreeException(reason, node);
    }

    // Get an integer-valued attribute
    protected static String getStringAttribute(Node node, String name,
                                               String defaultValue,
                                               boolean required,
                                               String[] range)
      throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }
        String value = attr.getNodeValue();

        if (range != null) {
            if (value == null) {
                fatal(node,
                      "Null value for "+node.getNodeName()+
                      " attribute "+name+"!");
            }
            boolean validValue = false;
            int len = range.length;
            for (int i = 0; i < len; i++) {
                if (value.equals(range[i])) {
                    validValue = true;
                    break;
                }
            }
            if (!validValue) {
                fatal(node,
                      "Bad value for "+node.getNodeName()+
                      " attribute "+name+"!");
            }
        }

        return value;
    }


    // Get an integer-valued attribute
    protected static int getIntAttribute(Node node, String name,
                                         int defaultValue, boolean required,
                                         boolean bounded, int min, int max)
      throws IIOInvalidTreeException {
        String value = getStringAttribute(node, name, null, required, null);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }
        
        int intValue = defaultValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            fatal(node,
                  "Bad value for "+node.getNodeName()+
                  " attribute "+name+"!");
        }
        if (bounded && (intValue < min || intValue > max)) {
            fatal(node,
                  "Bad value for "+node.getNodeName()+
                  " attribute "+name+"!");
        }
        return intValue;
    }

    // Get a float-valued attribute
    protected static float getFloatAttribute(Node node, String name,
                                             float defaultValue,
                                             boolean required)
      throws IIOInvalidTreeException {
        String value = getStringAttribute(node, name, null, required, null);
        if (value == null) {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }

    // Get a required integer-valued attribute
    protected static int getIntAttribute(Node node, String name,
                                         boolean bounded, int min, int max)
      throws IIOInvalidTreeException {
        return getIntAttribute(node, name, -1, true, bounded, min, max);
    }

    // Get a required float-valued attribute
    protected static float getFloatAttribute(Node node, String name)
      throws IIOInvalidTreeException {
        return getFloatAttribute(node, name, -1.0F, true);
    }

    // Get a boolean-valued attribute
    protected static boolean getBooleanAttribute(Node node, String name,
                                                 boolean defaultValue,
                                                 boolean required)
      throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }
        String value = attr.getNodeValue();
        // XXX Should be able to use equals() here instead of
        // equalsIgnoreCase() but some boolean attributes are incorrectly
        // set to "true" or "false" by the J2SE core metadata classes
        // getAsTree() method (which are duplicated above). See bug 5082756.
        if (value.equalsIgnoreCase("TRUE")) {
            return true;
        } else if (value.equalsIgnoreCase("FALSE")) {
            return false;
        } else {
            fatal(node, "Attribute " + name + " must be 'TRUE' or 'FALSE'!");
            return false;
        }
    }

    // Get a required boolean-valued attribute
    protected static boolean getBooleanAttribute(Node node, String name)
      throws IIOInvalidTreeException {
        return getBooleanAttribute(node, name, false, true);
    }

    // Get an enumerated attribute as an index into a String array
    protected static int getEnumeratedAttribute(Node node,
                                                String name,
                                                String[] legalNames,
                                                int defaultValue,
                                                boolean required)
      throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }
        String value = attr.getNodeValue();
        for (int i = 0; i < legalNames.length; i++) {
            if(value.equals(legalNames[i])) {
                return i;
            }
        }

        fatal(node, "Illegal value for attribute " + name + "!");
        return -1;
    }

    // Get a required enumerated attribute as an index into a String array
    protected static int getEnumeratedAttribute(Node node,
                                                String name,
                                                String[] legalNames)
      throws IIOInvalidTreeException {
        return getEnumeratedAttribute(node, name, legalNames, -1, true);
    }

    // Get a String-valued attribute
    protected static String getAttribute(Node node, String name,
                                         String defaultValue, boolean required)
      throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }
        return attr.getNodeValue();
    }

    // Get a required String-valued attribute
    protected static String getAttribute(Node node, String name)
      throws IIOInvalidTreeException {
        return getAttribute(node, name, null, true);
    }

    protected GIFMetadata(boolean standardMetadataFormatSupported,
                          String nativeMetadataFormatName,
                          String nativeMetadataFormatClassName,
                          String[] extraMetadataFormatNames,
                          String[] extraMetadataFormatClassNames) {
        super(standardMetadataFormatSupported,
              nativeMetadataFormatName,
              nativeMetadataFormatClassName,
              extraMetadataFormatNames,
              extraMetadataFormatClassNames);
    }

    public void mergeTree(String formatName, Node root)
      throws IIOInvalidTreeException {
        if (formatName.equals(nativeMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeNativeTree(root);
        } else if (formatName.equals
                  (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeStandardTree(root);
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    protected byte[] getColorTable(Node colorTableNode,
                                   String entryNodeName,
                                   boolean lengthExpected,
                                   int expectedLength)
      throws IIOInvalidTreeException {
        byte[] red = new byte[256];
        byte[] green  = new byte[256];
        byte[] blue = new byte[256];
        int maxIndex = -1;

        Node entry = colorTableNode.getFirstChild();
        if (entry == null) {
            fatal(colorTableNode, "Palette has no entries!");
        }

        while (entry != null) {
            if (!entry.getNodeName().equals(entryNodeName)) {
                fatal(colorTableNode,
                      "Only a "+entryNodeName+" may be a child of a "+
                      entry.getNodeName()+"!");
            }

            int index = getIntAttribute(entry, "index", true, 0, 255);
            if (index > maxIndex) {
                maxIndex = index;
            }
            red[index] = (byte)getIntAttribute(entry, "red", true, 0, 255);
            green[index] = (byte)getIntAttribute(entry, "green", true, 0, 255);
            blue[index] = (byte)getIntAttribute(entry, "blue", true, 0, 255);

            entry = entry.getNextSibling();
        }

        int numEntries = maxIndex + 1;

        if (lengthExpected && numEntries != expectedLength) {
            fatal(colorTableNode, "Unexpected length for palette!");
        }

        byte[] colorTable = new byte[3*numEntries];
        for (int i = 0, j = 0; i < numEntries; i++) {
            colorTable[j++] = red[i];
            colorTable[j++] = green[i];
            colorTable[j++] = blue[i];
        }

        return colorTable;
    }

    protected abstract void mergeNativeTree(Node root)
      throws IIOInvalidTreeException;

   protected abstract void mergeStandardTree(Node root)
      throws IIOInvalidTreeException;
}
