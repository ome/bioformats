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
 * $RCSfile: Box.java,v $
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
 * $Revision: 1.6 $
 * $Date: 2007/09/05 20:03:20 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;

import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.IIOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageInputStream;

import com.sun.media.imageioimpl.common.ImageUtil;

/**
 * This class is defined to create the box of JP2 file format.  A box has
 *  a length, a type, an optional extra length and its content.  The subclasses
 *  should explain the content information.
 */
public class Box {
    /** The table to link tag names for all the JP2 boxes. */
    private static Hashtable names = new Hashtable();

    // Initializes the hash table "names".
    static {
        //children for the root
        names.put(new Integer(0x6A502020), "JPEG2000SignatureBox");
        names.put(new Integer(0x66747970), "JPEG2000FileTypeBox");

        // children for the boxes other than
        //JPEG2000SignatureBox/JPEG2000FileTypeBox
        names.put(new Integer(0x6A703269),
                              "JPEG2000IntellectualPropertyRightsBox");
        names.put(new Integer(0x786D6C20), "JPEG2000XMLBox");
        names.put(new Integer(0x75756964), "JPEG2000UUIDBox");
        names.put(new Integer(0x75696E66), "JPEG2000UUIDInfoBox");

        // Children of HeadCStream
        names.put(new Integer(0x6a703268), "JPEG2000HeaderSuperBox");
        names.put(new Integer(0x6a703263), "JPEG2000CodeStreamBox");

        // Children of JPEG2000HeaderSuperBox
        names.put(new Integer(0x69686472), "JPEG2000HeaderBox");

        // Optional boxes in JPEG2000HeaderSuperBox
        names.put(new Integer(0x62706363), "JPEG2000BitsPerComponentBox");
        names.put(new Integer(0x636f6c72), "JPEG2000ColorSpecificationBox");
        names.put(new Integer(0x70636c72), "JPEG2000PaletteBox");
        names.put(new Integer(0x636d6170), "JPEG2000ComponentMappingBox");
        names.put(new Integer(0x63646566), "JPEG2000ChannelDefinitionBox");
        names.put(new Integer(0x72657320), "JPEG2000ResolutionBox");

        // Children of JPEG2000ResolutionBox
        names.put(new Integer(0x72657363), "JPEG2000CaptureResolutionBox");
        names.put(new Integer(0x72657364),
                              "JPEG2000DefaultDisplayResolutionBox");

        // Children of JPEG2000UUIDInfoBox
        names.put(new Integer(0x756c7374), "JPEG2000UUIDListBox");
        names.put(new Integer(0x75726c20), "JPEG2000DataEntryURLBox");
    }

    /** A Hashtable contains the class names for each type of the boxes.
     *  This table will be used to construct a Box object from a Node object
     *  by using reflection.
     */
    private static Hashtable boxClasses = new Hashtable();

    // Initializes the hash table "boxClasses".
    static {
        //children for the root
        boxClasses.put(new Integer(0x6A502020), SignatureBox.class);
        boxClasses.put(new Integer(0x66747970), FileTypeBox.class);

        // children for the boxes other than
        //JPEG2000SignatureBox/JPEG2000FileTypeBox
        boxClasses.put(new Integer(0x6A703269), Box.class);
        boxClasses.put(new Integer(0x786D6C20), XMLBox.class);
        boxClasses.put(new Integer(0x75756964), UUIDBox.class);

        // Children of JPEG2000HeaderSuperBox
        boxClasses.put(new Integer(0x69686472), HeaderBox.class);

        // Optional boxes in JPEG2000HeaderSuperBox
        boxClasses.put(new Integer(0x62706363), BitsPerComponentBox.class);
        boxClasses.put(new Integer(0x636f6c72), ColorSpecificationBox.class);
        boxClasses.put(new Integer(0x70636c72), PaletteBox.class);
        boxClasses.put(new Integer(0x636d6170), ComponentMappingBox.class);
        boxClasses.put(new Integer(0x63646566), ChannelDefinitionBox.class);
        boxClasses.put(new Integer(0x72657320), ResolutionBox.class);

        // Children of JPEG2000ResolutionBox
        boxClasses.put(new Integer(0x72657363), ResolutionBox.class);
        boxClasses.put(new Integer(0x72657364), ResolutionBox.class);

        // Children of JPEG2000UUIDInfoBox
        boxClasses.put(new Integer(0x756c7374), UUIDListBox.class);
        boxClasses.put(new Integer(0x75726c20), DataEntryURLBox.class);
    }

    /** Returns the XML tag name defined in JP2 XML xsd/dtd for the box
     *  with the provided <code>type</code>. If the <code>type</code> is
     * not known, the string <code>"unknown"</code> is returned.
     */
    public static String getName(int type) {
        String name = (String)names.get(new Integer(type));
        return name == null ? "unknown" : name;
    }

    /** Returns the Box class for the box with the provided <code>type</code>.
     */
    public static Class getBoxClass(int type) {
        if (type == 0x6a703268 || type == 0x72657320)
            return null;
        return (Class)boxClasses.get(new Integer(type));
    }

    /** Returns the type String based on the provided name. */
    public static String getTypeByName(String name) {
        Enumeration keys = names.keys();
        while (keys.hasMoreElements()) {
            Integer i = (Integer)keys.nextElement();
            if (name.equals(names.get(i)))
                return getTypeString(i.intValue());
        }
        return null;
    }

    /** Creates a <code>Box</code> object with the provided <code>type</code>
     *  based on the provided Node object based on reflection.
     */
    public static Box createBox(int type,
                                Node node) throws IIOInvalidTreeException {
        Class boxClass = (Class)boxClasses.get(new Integer(type));

        try {
            // gets the constructor with <code>Node</code parameter
            Constructor cons =
                boxClass.getConstructor(new Class[] {Node.class});
            if (cons != null) {
                return (Box)cons.newInstance(new Object[]{node});
            }
        } catch(NoSuchMethodException e) {
            // If exception throws, create a <code>Box</code> instance.
            e.printStackTrace();
            return new Box(node);
        } catch(InvocationTargetException e) {
            e.printStackTrace();
            return new Box(node);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new Box(node);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return new Box(node);
        }

        return null;
    }

    /** Extracts the value of the attribute from name. */
    public static Object getAttribute(Node node, String name) {
        NamedNodeMap map = node.getAttributes();
        node = map.getNamedItem(name);
        return (node != null) ? node.getNodeValue() : null;
    }

    /** Parses the byte array expressed by a string. */
    public static byte[] parseByteArray(String value) {
	if (value == null)
	    return null;

        StringTokenizer token = new StringTokenizer(value);
        int count = token.countTokens();

        byte[] buf = new byte[count];
        int i = 0;
        while(token.hasMoreElements()) {
            buf[i++] = new Byte(token.nextToken()).byteValue();
        }
        return buf;
    }

    /** Parses the integer array expressed a string. */
    protected static int[] parseIntArray(String value) {
	if (value == null)
	    return null;

        StringTokenizer token = new StringTokenizer(value);
        int count = token.countTokens();

        int[] buf = new int[count];
        int i = 0;
        while(token.hasMoreElements()) {
            buf[i++] = new Integer(token.nextToken()).intValue();
        }
        return buf;
    }

    /** Gets its <code>String</code> value from an <code>IIOMetadataNode</code>.
     */
    protected static String getStringElementValue(Node node) {

        if (node instanceof IIOMetadataNode) { 
            Object obj = ((IIOMetadataNode)node).getUserObject(); 
            if (obj instanceof String) 
                return (String)obj; 
        }

	return node.getNodeValue();
    }

    /** Gets its byte value from an <code>IIOMetadataNode</code>. */
    protected static byte getByteElementValue(Node node) {	
	if (node instanceof IIOMetadataNode) {
	    Object obj = ((IIOMetadataNode)node).getUserObject();
	    if (obj instanceof Byte)
		return ((Byte)obj).byteValue();
	}

	String value = node.getNodeValue();
	if (value != null)
	    return new Byte(value).byteValue();
	return (byte)0;
    }

    /** Gets its integer value from an <code>IIOMetadataNode</code>. */
    protected static int getIntElementValue(Node node) {
        if (node instanceof IIOMetadataNode) {
            Object obj = ((IIOMetadataNode)node).getUserObject();
            if (obj instanceof Integer)
                return ((Integer)obj).intValue();
        }

	String value = node.getNodeValue();
	if (value != null)
	    return new Integer(value).intValue();
	return 0;
    }

    /** Gets its short value from an <code>IIOMetadataNode</code>. */
    protected static short getShortElementValue(Node node) {
        if (node instanceof IIOMetadataNode) {
            Object obj = ((IIOMetadataNode)node).getUserObject();
            if (obj instanceof Short)
                return ((Short)obj).shortValue();
        }
        String value = node.getNodeValue();
	if (value != null)
	    return new Short(value).shortValue();
	return (short)0;
    }

    /** Gets the byte array from an <code>IIOMetadataNode</code>. */
    protected static byte[] getByteArrayElementValue(Node node) {
        if (node instanceof IIOMetadataNode) {
            Object obj = ((IIOMetadataNode)node).getUserObject();
            if (obj instanceof byte[])
                return (byte[])obj;
        }

        return parseByteArray(node.getNodeValue());
    }

    /** Gets the integer array from an <code>IIOMetadataNode</code>. */
    protected static int[] getIntArrayElementValue(Node node) {
        if (node instanceof IIOMetadataNode) {
            Object obj = ((IIOMetadataNode)node).getUserObject();
            if (obj instanceof int[])
                return (int[])obj;
        }

        return parseIntArray(node.getNodeValue());
    }

    /** Copies that four bytes of an integer into the byte array.  Necessary
     *  for the subclasses to compose the content array from the data elements
     */
    public static void copyInt(byte[] data, int pos, int value) {
        data[pos++] = (byte)(value >> 24);
        data[pos++] = (byte)(value >> 16);
        data[pos++] = (byte)(value >> 8);
        data[pos++] = (byte)(value & 0xFF);
    }

    /** Converts the box type from integer to string. This is necessary because
     *  type is defined as String in xsd/dtd and integer in the box classes.
     */
    public static String getTypeString(int type) {
        byte[] buf = new byte[4];
        for (int i = 3; i >= 0; i--) {
            buf[i] = (byte)(type & 0xFF);
            type >>>= 8;
        }

        return new String(buf);
    }

    /**
     * Converts the box type from integer to string.  This is necessary because
     *  type is defined as String in xsd/dtd and integer in the box classes.
     */
    public static int getTypeInt(String s) {
        byte[] buf = s.getBytes();
        int t = buf[0];
        for (int i = 1; i < 4; i++) {
            t = (t <<8) | buf[i];
        }

        return t;
    }

    /** Box length, extra length, type and content data array */
    protected int length;
    protected long extraLength;
    protected int type;
    protected byte[] data;

    /** Constructs a <code>Box</code> instance using the provided
     *  the box type and the box content in byte array format.
     *
     * @param length The provided box length.
     * @param type The provided box type.
     * @param data The provided box content in a byte array.
     *
     * @throws IllegalArgumentException If the length of the content byte array
     *         is not length - 8.
     */
    public Box(int length, int type, byte[] data) {
        this.type = type;
        setLength(length);
        setContent(data);
    }

    /** Constructs a <code>Box</code> instance using the provided
     *  the box type, the box extra length, and the box content in byte
     *  array format.  In this case, the length of the box is set to 1,
     *  which indicates the extra length is meaningful.
     *
     * @param length The provided box length.
     * @param type The provided box type.
     * @param extraLength The provided box extra length.
     * @param data The provided box content in a byte array.
     *
     * @throws IllegalArgumentException If the length of the content byte array
     *         is not extra length - 16.
     */
    public Box(int length, int type, long extraLength, byte[] data) {
        this.type = type;
        setLength(length);
        if (length == 1)
            setExtraLength(extraLength);
        setContent(data);
    }

    /** Constructs a <code>Box</code> instance from the provided <code>
     *  ImageInputStream</code> at the specified position.
     *
     * @param iis The <code>ImageInputStream</code> contains the box.
     * @param pos The position from where to read the box.
     * @throws IOException If any IOException is thrown in the called read
     *         methods.
     */
    public Box(ImageInputStream iis, int pos) throws IOException {
        read(iis, pos);
    }

    /**
     * Constructs a Box from an "unknown" Node.  This node has at
     * least the attribute "Type", and may have the attribute "Length",
     * "ExtraLength" and a child "Content".  The child node content is a
     * IIOMetaDataNode with a byte[] user object.
     */
    public Box(Node node) throws IIOInvalidTreeException {
        NodeList children = node.getChildNodes();

        String value = (String)Box.getAttribute(node, "Type");
        type = getTypeInt(value);
        if (value == null || names.get(new Integer(type)) == null)
            throw new IIOInvalidTreeException("Type is not defined", node);

        value = (String)Box.getAttribute(node, "Length");
        if (value != null)
            length = new Integer(value).intValue();

        value = (String)Box.getAttribute(node, "ExtraLength");
        if (value != null)
            extraLength = new Long(value).longValue();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("Content".equals(child.getNodeName())) {
		if (child instanceof IIOMetadataNode) {
		    IIOMetadataNode cnode = (IIOMetadataNode)child;
		    try {
			data = (byte[])cnode.getUserObject();
		    } catch (Exception e) {
		    }
		}else  {
		    data = getByteArrayElementValue(child);
		}

		if (data == null) {
		    value = node.getNodeValue();
		    if (value != null)
			data = value.getBytes();
		}
            }
        }
    }

    /** Creates an <code>IIOMetadataNode</code> from this
     *  box.  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        String name = Box.getName(getType());
        if (name == null)
            name = "unknown";

        IIOMetadataNode node = new IIOMetadataNode(name);
        setDefaultAttributes(node);
        IIOMetadataNode child = new IIOMetadataNode("Content");
        child.setUserObject(data);
	child.setNodeValue(ImageUtil.convertObjectToString(data));
        node.appendChild(child);

        return node;
    }

    /** Creates an <code>IIOMetadataNode</code> from this
     *  box.  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     *
     *  This method is designed for the types of boxes whose XML tree
     *  only has 2 levels.
     */
    protected IIOMetadataNode getNativeNodeForSimpleBox() {
        try {
            Method m = this.getClass().getMethod("getElementNames",
                                                 (Class[])null);
            String[] elementNames = (String[])m.invoke(null, (Object[])null);

            IIOMetadataNode node = new IIOMetadataNode(Box.getName(getType()));
            setDefaultAttributes(node);
            for (int i = 0; i < elementNames.length; i++) {
                IIOMetadataNode child = new IIOMetadataNode(elementNames[i]);
                m = this.getClass().getMethod("get" + elementNames[i],
                                              (Class[])null);
		Object obj = m.invoke(this, (Object[])null);
                child.setUserObject(obj);
		child.setNodeValue(ImageUtil.convertObjectToString(obj));
                node.appendChild(child);
            }
            return node;
        } catch (Exception e) {
            throw new IllegalArgumentException(I18N.getString("Box0"));
        }
    }

    /** Sets the default attributes, "Length", "Type", and "ExtraLength", to
     *  the provided <code>IIOMetadataNode</code>.
     */
    protected void setDefaultAttributes(IIOMetadataNode node) {
        node.setAttribute("Length", Integer.toString(length));
        node.setAttribute("Type", getTypeString(type));

        if (length == 1) {
            node.setAttribute("ExtraLength", Long.toString(extraLength));
        }
    }

    /** Returns the box length. */
    public int getLength() {
        return length;
    }

    /** Returns the box type. */
    public int getType() {
        return type;
    }

    /** Returns the box extra length. */
    public long getExtraLength() {
        return extraLength;
    }

    /** Returns the box content in byte array. */
    public byte[] getContent() {
        if (data == null)
            compose();
        return data;
    }

    /** Sets the box length to the provided value. */
    public void setLength(int length) {
        this.length = length;
    }

    /** Sets the box extra length length to the provided value. */
    public void setExtraLength(long extraLength) {
        if (length != 1)
            throw new IllegalArgumentException(I18N.getString("Box1"));
        this.extraLength = extraLength;
    }

    /** Sets the box content.  If the content length is not length -8 or
     *  extra length - 16, IllegalArgumentException will be thrown.
     */
    public void setContent(byte[] data) {
        if (data != null &&
            ((length ==1 && (extraLength - 16 != data.length)) ||
            (length != 1 && length - 8 != data.length)))
            throw new IllegalArgumentException(I18N.getString("Box2"));
        this.data = data;
        if (data != null)
            parse(data);
    }

    /** Writes this box instance into a <code>ImageOutputStream</code>. */
    public void write(ImageOutputStream ios) throws IOException {
        ios.writeInt(length);
        ios.writeInt(type);
        if (length == 1) {
            ios.writeLong(extraLength);
            ios.write(data, 0, (int)extraLength);
        } else if (data != null)
            ios.write(data, 0, length);
    }

    /** Reads a box from the <code>ImageInputStream</code. at the provided
     *  position.
     */
    public void read(ImageInputStream iis, int pos) throws IOException {
        iis.mark();
        iis.seek(pos);
        length = iis.readInt();
        type = iis.readInt();
        int dataLength = 0;
        if(length == 0) {
            // Length unknown at time of stream creation.
            long streamLength = iis.length();
            if(streamLength != -1)
                // Calculate box length from known stream length.
                dataLength = (int)(streamLength - iis.getStreamPosition());
            else {
                // Calculate box length by reading to EOF.
                long dataPos = iis.getStreamPosition();
                int bufLen = 1024;
                byte[] buf = new byte[bufLen];
                long savePos = dataPos;
                try {
                    iis.readFully(buf);
                    dataLength += bufLen;
                    savePos = iis.getStreamPosition();
                } catch(EOFException eofe) {
                    iis.seek(savePos);
                    while(iis.read() != -1) dataLength++;
                }
                iis.seek(dataPos);
            }
        } else if(length == 1) {
            // Length given by XL parameter.
            extraLength = iis.readLong();
            dataLength = (int)(extraLength - 16);
        } else if(length >= 8 && length < (1 << 32)) {
            // Length given by L parameter.
            dataLength = length - 8;
        } else {
            // Illegal value for L parameter.
            throw new IIOException("Illegal value "+length+
                                   " for box length parameter.");
        }
        data = new byte[dataLength];
        iis.readFully(data);
        iis.reset();
    }

    /** Parses the data elements from the byte array.  The subclasses should
     *  override this method.
     */
    protected void parse(byte[] data) {
    }

    /** Composes the content byte array from the data elements.
     */
    protected void compose() {
    }
}
