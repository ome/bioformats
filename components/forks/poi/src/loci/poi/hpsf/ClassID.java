/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        
package loci.poi.hpsf;

import loci.poi.util.HexDump;

/**
 *  <p>Represents a class ID (16 bytes). Unlike other little-endian
 *  type the {@link ClassID} is not just 16 bytes stored in the wrong
 *  order. Instead, it is a double word (4 bytes) followed by two
 *  words (2 bytes each) followed by 8 bytes.</p>
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @version $Id: ClassID.java 489730 2006-12-22 19:18:16Z bayard $
 * @since 2002-02-09
 */
public class ClassID
{

    /**
     * <p>The bytes making out the class ID in correct order,
     * i.e. big-endian.</p>
     */
    protected byte[] bytes;



    /**
     *  <p>Creates a {@link ClassID} and reads its value from a byte
     *  array.</p>
     *
     * @param src The byte array to read from.
     * @param offset The offset of the first byte to read.
     */
    public ClassID(final byte[] src, final int offset)
    {
        read(src, offset);
    }


    /**
     *  <p>Creates a {@link ClassID} and initializes its value with
     *  0x00 bytes.</p>
     */
    public ClassID()
    {
        bytes = new byte[LENGTH];
        for (int i = 0; i < LENGTH; i++)
            bytes[i] = 0x00;
    }



    /** <p>The number of bytes occupied by this object in the byte
     * stream.</p> */
    public static final int LENGTH = 16;

    /**
     * @return The number of bytes occupied by this object in the byte
     * stream.
     */
    public int length()
    {
        return LENGTH;
    }



    /**
     * <p>Gets the bytes making out the class ID. They are returned in
     * correct order, i.e. big-endian.</p>
     *
     * @return the bytes making out the class ID.
     */
    public byte[] getBytes()
    {
        return bytes;
    }



    /**
     * <p>Sets the bytes making out the class ID.</p>
     *
     * @param bytes The bytes making out the class ID in big-endian format. They
     * are copied without their order being changed.
     */
    public void setBytes(final byte[] bytes)
    {
        for (int i = 0; i < this.bytes.length; i++)
            this.bytes[i] = bytes[i];
    }



    /**
     * <p>Reads the class ID's value from a byte array by turning
     * little-endian into big-endian.</p>
     *
     * @param src The byte array to read from
     *
     * @param offset The offset within the <var>src</var> byte array
     *
     * @return A byte array containing the class ID.
     */
    public byte[] read(final byte[] src, final int offset)
    {
        bytes = new byte[16];

        /* Read double word. */
        bytes[0] = src[3 + offset];
        bytes[1] = src[2 + offset];
        bytes[2] = src[1 + offset];
        bytes[3] = src[0 + offset];

        /* Read first word. */
        bytes[4] = src[5 + offset];
        bytes[5] = src[4 + offset];

        /* Read second word. */
        bytes[6] = src[7 + offset];
        bytes[7] = src[6 + offset];

        /* Read 8 bytes. */
        for (int i = 8; i < 16; i++)
            bytes[i] = src[i + offset];

        return bytes;
    }



    /**
     * <p>Writes the class ID to a byte array in the
     * little-endian format.</p>
     *
     * @param dst The byte array to write to.
     *
     * @param offset The offset within the <var>dst</var> byte array.
     *
     * @exception ArrayStoreException if there is not enough room for the class
     * ID 16 bytes in the byte array after the <var>offset</var> position.
     */
    public void write(final byte[] dst, final int offset)
    throws ArrayStoreException
    {
        /* Check array size: */
        if (dst.length < 16)
            throw new ArrayStoreException
                ("Destination byte[] must have room for at least 16 bytes, " +
                 "but has a length of only " + dst.length + ".");
        /* Write double word. */
        dst[0 + offset] = bytes[3];
        dst[1 + offset] = bytes[2];
        dst[2 + offset] = bytes[1];
        dst[3 + offset] = bytes[0];

        /* Write first word. */
        dst[4 + offset] = bytes[5];
        dst[5 + offset] = bytes[4];

        /* Write second word. */
        dst[6 + offset] = bytes[7];
        dst[7 + offset] = bytes[6];

        /* Write 8 bytes. */
        for (int i = 8; i < 16; i++)
            dst[i + offset] = bytes[i];
    }



    /**
     * <p>Checks whether this <code>ClassID</code> is equal to another
     * object.</p>
     *
     * @param o the object to compare this <code>PropertySet</code> with
     * @return <code>true</code> if the objects are equal, else
     * <code>false</code>.</p>
     */
    public boolean equals(final Object o)
    {
        if (o == null || !(o instanceof ClassID))
            return false;
        final ClassID cid = (ClassID) o;
        if (bytes.length != cid.bytes.length)
            return false;
        for (int i = 0; i < bytes.length; i++)
            if (bytes[i] != cid.bytes[i])
                return false;
        return true;
    }



    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        return new String(bytes).hashCode();
    }



    /**
     * <p>Returns a human-readable representation of the Class ID in standard 
     * format <code>"{xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx}"</code>.</p>
     * 
     * @return String representation of the Class ID represented by this object.
     */
    public String toString()
    {
        StringBuffer sbClassId = new StringBuffer(38);
        sbClassId.append('{');
        for (int i = 0; i < 16; i++)
        {
            sbClassId.append(HexDump.toHex(bytes[i]));
            if (i == 3 || i == 5 || i == 7 || i == 9)
                sbClassId.append('-');
        }
        sbClassId.append('}');
        return sbClassId.toString();
    }

}
