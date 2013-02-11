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

import java.io.IOException;
import java.io.OutputStream;

import loci.poi.util.LittleEndian;

/**
 * <p>Class for writing little-endian data and more.</p>
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @version $Id: TypeWriter.java 489730 2006-12-22 19:18:16Z bayard $
 * @since 2003-02-20
 */
public class TypeWriter
{

    /**
     * <p>Writes a two-byte value (short) to an output stream.</p>
     *
     * @param out The stream to write to.
     * @param n The value to write.
     * @return The number of bytes that have been written.
     * @exception IOException if an I/O error occurs
     */
    public static int writeToStream(final OutputStream out, final short n)
        throws IOException
    {
        final int length = LittleEndian.SHORT_SIZE;
        byte[] buffer = new byte[length];
        LittleEndian.putShort(buffer, 0, n); // FIXME: unsigned
        out.write(buffer, 0, length);
        return length;
    }



    /**
     * <p>Writes a four-byte value to an output stream.</p>
     *
     * @param out The stream to write to.
     * @param n The value to write.
     * @exception IOException if an I/O error occurs
     * @return The number of bytes written to the output stream. 
     */
    public static int writeToStream(final OutputStream out, final int n)
        throws IOException
    {
        final int l = LittleEndian.INT_SIZE;
        final byte[] buffer = new byte[l];
        LittleEndian.putInt(buffer, 0, n);
        out.write(buffer, 0, l);
        return l;
        
    }



    /**
     * <p>Writes a eight-byte value to an output stream.</p>
     *
     * @param out The stream to write to.
     * @param n The value to write.
     * @exception IOException if an I/O error occurs
     * @return The number of bytes written to the output stream. 
     */
    public static int writeToStream(final OutputStream out, final long n)
        throws IOException
    {
        final int l = LittleEndian.LONG_SIZE;
        final byte[] buffer = new byte[l];
        LittleEndian.putLong(buffer, 0, n);
        out.write(buffer, 0, l);
        return l;
        
    }



    /**
     * <p>Writes an unsigned two-byte value to an output stream.</p>
     *
     * @param out The stream to write to
     * @param n The value to write
     * @exception IOException if an I/O error occurs
     */
    public static void writeUShortToStream(final OutputStream out, final int n)
        throws IOException
    {
        int high = n & 0xFFFF0000;
        if (high != 0)
            throw new IllegalPropertySetDataException
                ("Value " + n + " cannot be represented by 2 bytes.");
        writeToStream(out, (short) n);
    }



    /**
     * <p>Writes an unsigned four-byte value to an output stream.</p>
     *
     * @param out The stream to write to.
     * @param n The value to write.
     * @return The number of bytes that have been written to the output stream.
     * @exception IOException if an I/O error occurs
     */
    public static int writeUIntToStream(final OutputStream out, final long n)
        throws IOException
    {
        long high = n & 0xFFFFFFFF00000000L;
        if (high != 0 && high != 0xFFFFFFFF00000000L)
            throw new IllegalPropertySetDataException
                ("Value " + n + " cannot be represented by 4 bytes.");
        return writeToStream(out, (int) n);
    }



    /**
     * <p>Writes a 16-byte {@link ClassID} to an output stream.</p>
     *
     * @param out The stream to write to
     * @param n The value to write
     * @return The number of bytes written
     * @exception IOException if an I/O error occurs
     */
    public static int writeToStream(final OutputStream out, final ClassID n)
        throws IOException
    {
        byte[] b = new byte[16];
        n.write(b, 0);
        out.write(b, 0, b.length);
        return b.length;
    }



    /**
     * <p>Writes an array of {@link Property} instances to an output stream
     * according to the Horrible Property Stream Format.</p>
     * 
     * @param out The stream to write to
     * @param properties The array to write to the stream
     * @param codepage The codepage number to use for writing strings
     * @exception IOException if an I/O error occurs
     * @throws UnsupportedVariantTypeException if HPSF does not support some
     *         variant type.
     */
    public static void writeToStream(final OutputStream out,
                                     final Property[] properties,
                                     final int codepage)
        throws IOException, UnsupportedVariantTypeException
    {
        /* If there are no properties don't write anything. */
        if (properties == null)
            return;

        /* Write the property list. This is a list containing pairs of property
         * ID and offset into the stream. */
        for (int i = 0; i < properties.length; i++)
        {
            final Property p = properties[i];
            writeUIntToStream(out, p.getID());
            writeUIntToStream(out, p.getSize());
        }

        /* Write the properties themselves. */
        for (int i = 0; i < properties.length; i++)
        {
            final Property p = properties[i];
            long type = p.getType();
            writeUIntToStream(out, type);
            VariantSupport.write(out, (int) type, p.getValue(), codepage);
        }
    }



    /**
     * <p>Writes a double value value to an output stream.</p>
     *
     * @param out The stream to write to.
     * @param n The value to write.
     * @exception IOException if an I/O error occurs
     * @return The number of bytes written to the output stream. 
     */
    public static int writeToStream(final OutputStream out, final double n)
        throws IOException
    {
        final int l = LittleEndian.DOUBLE_SIZE;
        final byte[] buffer = new byte[l];
        LittleEndian.putDouble(buffer, 0, n);
        out.write(buffer, 0, l);
        return l;
    }

}
