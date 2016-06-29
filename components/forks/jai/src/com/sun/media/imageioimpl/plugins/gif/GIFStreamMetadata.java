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
 * $RCSfile: GIFStreamMetadata.java,v $
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
 * $Date: 2006/03/24 22:30:10 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.gif;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

// TODO - document elimination of globalColorTableFlag

/**
 * @version 0.5
 */
public class GIFStreamMetadata extends GIFMetadata {

    // package scope
    static final String
        nativeMetadataFormatName = "javax_imageio_gif_stream_1.0";

    public static final String[] versionStrings = { "87a", "89a" };

    public String version; // 87a or 89a
    public int logicalScreenWidth;
    public int logicalScreenHeight;
    public int colorResolution; // 1 to 8
    public int pixelAspectRatio;

    public int backgroundColorIndex; // Valid if globalColorTable != null
    public boolean sortFlag; // Valid if globalColorTable != null

    public static final String[] colorTableSizes = {
        "2", "4", "8", "16", "32", "64", "128", "256"
    };

    // Set global color table flag in header to 0 if null, 1 otherwise
    public byte[] globalColorTable = null;

    protected GIFStreamMetadata(boolean standardMetadataFormatSupported,
                                String nativeMetadataFormatName,
                                String nativeMetadataFormatClassName,
                                String[] extraMetadataFormatNames,
                                String[] extraMetadataFormatClassNames)
    {
        super(standardMetadataFormatSupported,
              nativeMetadataFormatName,
              nativeMetadataFormatClassName,
              extraMetadataFormatNames,
              extraMetadataFormatClassNames);
    }
    
    public GIFStreamMetadata() {
        this(true, 
              nativeMetadataFormatName,
              "com.sun.media.imageioimpl.plugins.gif.GIFStreamMetadataFormat",
              null, null);

    }

    public boolean isReadOnly() {
        return true;
    }
    
    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    private Node getNativeTree() {
        IIOMetadataNode node; // scratch node
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);
            
        node = new IIOMetadataNode("Version");
        node.setAttribute("value", version);
        root.appendChild(node);
        
        // Image descriptor
        node = new IIOMetadataNode("LogicalScreenDescriptor");
        /* NB: At the moment we use empty strings to support undefined 
         * integer values in tree representation. 
         * We need to add better support for undefined/default values later.
         */  
        node.setAttribute("logicalScreenWidth",
                          logicalScreenWidth == UNDEFINED_INTEGER_VALUE ?
                          "" : Integer.toString(logicalScreenWidth));
        node.setAttribute("logicalScreenHeight",
                          logicalScreenHeight == UNDEFINED_INTEGER_VALUE ?
                          "" : Integer.toString(logicalScreenHeight));
        // Stored value plus one
        node.setAttribute("colorResolution",
                          colorResolution == UNDEFINED_INTEGER_VALUE ?
                          "" : Integer.toString(colorResolution));
        node.setAttribute("pixelAspectRatio",
                          Integer.toString(pixelAspectRatio));
        root.appendChild(node);

        if (globalColorTable != null) {
            node = new IIOMetadataNode("GlobalColorTable");
            int numEntries = globalColorTable.length/3;
            node.setAttribute("sizeOfGlobalColorTable",
                              Integer.toString(numEntries));
            node.setAttribute("backgroundColorIndex",
                              Integer.toString(backgroundColorIndex));
            node.setAttribute("sortFlag",
                              sortFlag ? "TRUE" : "FALSE");

            for (int i = 0; i < numEntries; i++) {
                IIOMetadataNode entry =
                    new IIOMetadataNode("ColorTableEntry");
                entry.setAttribute("index", Integer.toString(i));
                int r = globalColorTable[3*i] & 0xff;
                int g = globalColorTable[3*i + 1] & 0xff;
                int b = globalColorTable[3*i + 2] & 0xff;
                entry.setAttribute("red", Integer.toString(r));
                entry.setAttribute("green", Integer.toString(g));
                entry.setAttribute("blue", Integer.toString(b));
                node.appendChild(entry);
            }
            root.appendChild(node);
        }

        return root;
    }

    public IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode chroma_node = new IIOMetadataNode("Chroma");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("ColorSpaceType");
        node.setAttribute("name", "RGB");
        chroma_node.appendChild(node);

        node = new IIOMetadataNode("BlackIsZero");
        node.setAttribute("value", "TRUE");
        chroma_node.appendChild(node);

        // NumChannels not in stream
        // Gamma not in format

        if (globalColorTable != null) {
            node = new IIOMetadataNode("Palette");
            int numEntries = globalColorTable.length/3;
            for (int i = 0; i < numEntries; i++) {
                IIOMetadataNode entry =
                    new IIOMetadataNode("PaletteEntry");
                entry.setAttribute("index", Integer.toString(i));
                entry.setAttribute("red",
                           Integer.toString(globalColorTable[3*i] & 0xff));
                entry.setAttribute("green",
                           Integer.toString(globalColorTable[3*i + 1] & 0xff));
                entry.setAttribute("blue",
                           Integer.toString(globalColorTable[3*i + 2] & 0xff));
                node.appendChild(entry);
            }
            chroma_node.appendChild(node);

            // backgroundColorIndex is valid iff there is a color table
            node = new IIOMetadataNode("BackgroundIndex");
            node.setAttribute("value", Integer.toString(backgroundColorIndex));
            chroma_node.appendChild(node);
        }

        return chroma_node;
    }

    public IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode compression_node = new IIOMetadataNode("Compression");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("CompressionTypeName");
        node.setAttribute("value", "lzw");
        compression_node.appendChild(node);

        node = new IIOMetadataNode("Lossless");
        node.setAttribute("value", "true");
        compression_node.appendChild(node);

        // NumProgressiveScans not in stream
        // BitRate not in format

        return compression_node;
    }

    public IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode data_node = new IIOMetadataNode("Data");
        IIOMetadataNode node = null; // scratch node

        // PlanarConfiguration

        node = new IIOMetadataNode("SampleFormat");
        node.setAttribute("value", "Index");
        data_node.appendChild(node);

        node = new IIOMetadataNode("BitsPerSample");
        node.setAttribute("value",
                          colorResolution == UNDEFINED_INTEGER_VALUE ?
                          "" : Integer.toString(colorResolution));
        data_node.appendChild(node);
        
        // SignificantBitsPerSample
        // SampleMSB
        
        return data_node;
    }

    public IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dimension_node = new IIOMetadataNode("Dimension");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("PixelAspectRatio");
        float aspectRatio = 1.0F;
        if (pixelAspectRatio != 0) {
            aspectRatio = (pixelAspectRatio + 15)/64.0F;
        }
        node.setAttribute("value", Float.toString(aspectRatio));
        dimension_node.appendChild(node);

        node = new IIOMetadataNode("ImageOrientation");
        node.setAttribute("value", "Normal");
        dimension_node.appendChild(node);

        // HorizontalPixelSize not in format
        // VerticalPixelSize not in format
        // HorizontalPhysicalPixelSpacing not in format
        // VerticalPhysicalPixelSpacing not in format
        // HorizontalPosition not in format
        // VerticalPosition not in format
        // HorizontalPixelOffset not in stream
        // VerticalPixelOffset not in stream

        node = new IIOMetadataNode("HorizontalScreenSize");
        node.setAttribute("value", 
                          logicalScreenWidth == UNDEFINED_INTEGER_VALUE ?
                          "" : Integer.toString(logicalScreenWidth));
        dimension_node.appendChild(node);
        
        node = new IIOMetadataNode("VerticalScreenSize");
        node.setAttribute("value",
                          logicalScreenHeight == UNDEFINED_INTEGER_VALUE ?
                          "" : Integer.toString(logicalScreenHeight));
        dimension_node.appendChild(node);
        
        return dimension_node;
    }

    public IIOMetadataNode getStandardDocumentNode() {
        IIOMetadataNode document_node = new IIOMetadataNode("Document");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("FormatVersion");
        node.setAttribute("value", version);
        document_node.appendChild(node);

        // SubimageInterpretation not in format
        // ImageCreationTime not in format
        // ImageModificationTime not in format

        return document_node;
    }

    public IIOMetadataNode getStandardTextNode() {
        // Not in stream
        return null;
    }

    public IIOMetadataNode getStandardTransparencyNode() {
        // Not in stream
        return null;
    }

    public void setFromTree(String formatName, Node root)
        throws IIOInvalidTreeException
    {
        throw new IllegalStateException("Metadata is read-only!");
    }

    protected void mergeNativeTree(Node root) throws IIOInvalidTreeException
    {
        throw new IllegalStateException("Metadata is read-only!");
    }

    protected void mergeStandardTree(Node root) throws IIOInvalidTreeException
    {
        throw new IllegalStateException("Metadata is read-only!");
    }

    public void reset() {
        throw new IllegalStateException("Metadata is read-only!");
    }
}
