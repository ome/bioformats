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
 * $RCSfile: TIFFFieldNode.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2006/04/18 20:47:02 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;
import com.sun.media.imageioimpl.plugins.tiff.TIFFIFD;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadata;

/**
 * The <code>Node</code> representation of a <code>TIFFField</code>
 * wherein the child node is procedural rather than buffered.
 *
 * @since 1.1-beta
 */
public class TIFFFieldNode extends IIOMetadataNode {
    private static String getNodeName(TIFFField f) {
        return f.getData() instanceof TIFFDirectory ?
            "TIFFIFD" : "TIFFField";
    }

    private boolean isIFD;

    /** Initialization flag. */
    private Boolean isInitialized = Boolean.FALSE;

    private TIFFField field;

    // XXX Set the user object to "field"?
    public TIFFFieldNode(TIFFField field) {
        super(getNodeName(field));

        isIFD = field.getData() instanceof TIFFDirectory;

        this.field = field;

        TIFFTag tag = field.getTag();
        int tagNumber = tag.getNumber();
        String tagName = tag.getName();

        if(isIFD) {
            if(tagNumber != 0) {
                setAttribute("parentTagNumber", Integer.toString(tagNumber));
            }
            if(tagName != null) {
                setAttribute("parentTagName", tagName);
            }

            TIFFDirectory dir = (TIFFDirectory)field.getData();
            TIFFTagSet[] tagSets = dir.getTagSets();
            if(tagSets != null) {
                String tagSetNames = "";
                for(int i = 0; i < tagSets.length; i++) {
                    tagSetNames += tagSets[i].getClass().getName();
                    if(i != tagSets.length - 1) {
                        tagSetNames += ",";
                    }
                }
                setAttribute("tagSets", tagSetNames);
            }
        } else {
            setAttribute("number", Integer.toString(tagNumber));
            setAttribute("name", tagName);
        }
    }

    private synchronized void initialize() {
        if(isInitialized == Boolean.TRUE) return;

        if(isIFD) {
            TIFFDirectory dir = (TIFFDirectory)field.getData();
            TIFFField[] fields = dir.getTIFFFields();
            if(fields != null) {
                TIFFTagSet[] tagSets = dir.getTagSets();
                List tagSetList = Arrays.asList(tagSets);
                int numFields = fields.length;
                for(int i = 0; i < numFields; i++) {
                    TIFFField f = fields[i];
                    int tagNumber = f.getTagNumber();
                    TIFFTag tag = TIFFIFD.getTag(tagNumber, tagSetList);

                    Node node = f.getAsNativeNode();

                    if (node != null) {
                        appendChild(node);
                    }
                }
            }
        } else {
            IIOMetadataNode child;
            int count = field.getCount();
            if (field.getType() == TIFFTag.TIFF_UNDEFINED) {
                child = new IIOMetadataNode("TIFFUndefined");

                byte[] data = field.getAsBytes();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < count; i++) {
                    sb.append(Integer.toString(data[i] & 0xff));
                    if (i < count - 1) {
                        sb.append(",");
                    }
                }
                child.setAttribute("value", sb.toString());
            } else {
                child = new IIOMetadataNode("TIFF" +
                                            field.getTypeName(field.getType()) +
                                            "s");

                TIFFTag tag = field.getTag();

                for (int i = 0; i < count; i++) {
                    IIOMetadataNode cchild =
                        new IIOMetadataNode("TIFF" +
                                            field.getTypeName(field.getType()));
                
                    cchild.setAttribute("value", field.getValueAsString(i));
                    if (tag.hasValueNames() && field.isIntegral()) {
                        int value = field.getAsInt(i);
                        String name = tag.getValueName(value);
                        if (name != null) {
                            cchild.setAttribute("description", name);
                        }
                    }
                
                    child.appendChild(cchild);
                }
            }
            appendChild(child);
        }

        isInitialized = Boolean.TRUE;
    }

    // Need to override this method to avoid a stack overflow exception
    // which will occur if super.appendChild is called from initialize().
    public Node appendChild(Node newChild) {
        if (newChild == null) {
            throw new IllegalArgumentException("newChild == null!");
        }

        return super.insertBefore(newChild, null);
    }

    // Override all methods which refer to child nodes.

    public boolean hasChildNodes() {
        initialize();
        return super.hasChildNodes();
    }

    public int getLength() {
        initialize();
        return super.getLength();
    }

    public Node getFirstChild() {
        initialize();
        return super.getFirstChild();
    }

    public Node getLastChild() {
        initialize();
        return super.getLastChild();
    }

    public Node getPreviousSibling() {
        initialize();
        return super.getPreviousSibling();
    }

    public Node getNextSibling() {
        initialize();
        return super.getNextSibling();
    }

    public Node insertBefore(Node newChild, 
                             Node refChild) {
        initialize();
        return super.insertBefore(newChild, refChild);
    }

    public Node replaceChild(Node newChild, 
                             Node oldChild) {
        initialize();
        return super.replaceChild(newChild, oldChild);
    }

    public Node removeChild(Node oldChild) {
        initialize();
        return super.removeChild(oldChild);
    }

    public Node cloneNode(boolean deep) {
        initialize();
        return super.cloneNode(deep);
    }
}
