/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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
        

package loci.poi.poifs.storage;

import java.io.*;

import java.util.*;

/**
 * Storage for documents that are too small to use regular
 * DocumentBlocks for their data
 *
 * @author  Marc Johnson (mjohnson at apache dot org)
 */

public class SmallDocumentBlock
    implements BlockWritable, ListManagedBlock
{
    private byte[]            _data;
    private static final byte _default_fill         = ( byte ) 0xff;
    private static final int  _block_size           = 64;
    private int  _blocks_per_big_block;

    private SmallDocumentBlock(final byte [] data, final int index, int size)
    {
        this(size);
        System.arraycopy(data, index * _block_size, _data, 0, _block_size);
    }

    private SmallDocumentBlock(int size)
    {
        _blocks_per_big_block = size / _block_size;
        _data = new byte[ _block_size ];
    }

    public int getBigBlockSize() { return _blocks_per_big_block * _block_size; }

    /**
     * convert a single long array into an array of SmallDocumentBlock
     * instances
     *
     * @param array the byte array to be converted
     * @param size the intended size of the array (which may be smaller)
     *
     * @return an array of SmallDocumentBlock instances, filled from
     *         the array
     */

    public static SmallDocumentBlock [] convert(final byte [] array,
                                                final int size)
    {
        SmallDocumentBlock[] rval   =
            new SmallDocumentBlock[ (size + _block_size - 1) / _block_size ];
        int                  offset = 0;

        for (int k = 0; k < rval.length; k++)
        {
            rval[ k ] = new SmallDocumentBlock(size);
            if (offset < array.length)
            {
                int length = Math.min(_block_size, array.length - offset);

                System.arraycopy(array, offset, rval[ k ]._data, 0, length);
                if (length != _block_size)
                {
                    Arrays.fill(rval[ k ]._data, length, _block_size,
                                _default_fill);
                }
            }
            else
            {
                Arrays.fill(rval[ k ]._data, _default_fill);
            }
            offset += _block_size;
        }
        return rval;
    }

    /**
     * fill out a List of SmallDocumentBlocks so that it fully occupies
     * a set of big blocks
     *
     * @param blocks the List to be filled out
     *
     * @return number of big blocks the list encompasses
     */

    public static int fill(final List blocks, int size)
    {
        int count           = blocks.size();
        int big_block_count = (count + (size / _block_size) - 1)
                              / (size / _block_size);
        int full_count      = big_block_count * (size / _block_size);

        for (; count < full_count; count++)
        {
            blocks.add(makeEmptySmallDocumentBlock(
              (size / _block_size) * _block_size));
        }
        return big_block_count;
    }

    /**
     * Factory for creating SmallDocumentBlocks from DocumentBlocks
     *
     * @param store the original DocumentBlocks
     * @param size the total document size
     *
     * @return an array of new SmallDocumentBlocks instances
     *
     * @exception IOException on errors reading from the DocumentBlocks
     * @exception ArrayIndexOutOfBoundsException if, somehow, the store
     *            contains less data than size indicates
     */

    public static SmallDocumentBlock [] convert(final BlockWritable [] store,
                                                final int size)
        throws IOException, ArrayIndexOutOfBoundsException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (int j = 0; j < store.length; j++)
        {
            store[ j ].writeBlocks(stream);
        }
        byte[]               data = stream.toByteArray();
        SmallDocumentBlock[] rval =
            new SmallDocumentBlock[ convertToBlockCount(size) ];

        for (int index = 0; index < rval.length; index++)
        {
            rval[ index ] = new SmallDocumentBlock(data, index, size);
        }
        return rval;
    }

    /**
     * create a list of SmallDocumentBlock's from raw data
     *
     * @param blocks the raw data containing the SmallDocumentBlock
     *               data
     *
     * @return a List of SmallDocumentBlock's extracted from the input
     *
     * @exception IOException
     */

    public static List extract(ListManagedBlock [] blocks, int size)
        throws IOException
    {
        List sdbs = new ArrayList();

        for (int j = 0; j < blocks.length; j++)
        {
            byte[] data = blocks[ j ].getData();

            for (int k = 0; k < (size / _block_size); k++)
            {
                sdbs.add(new SmallDocumentBlock(data, k, size));
            }
        }
        return sdbs;
    }

    /**
     * read data from an array of SmallDocumentBlocks
     *
     * @param blocks the blocks to read from
     * @param buffer the buffer to write the data into
     * @param offset the offset into the array of blocks to read from
     */

    public static void read(final BlockWritable [] blocks,
                            final byte [] buffer, final int offset)
    {
        int firstBlockIndex  = offset / _block_size;
        int firstBlockOffset = offset % _block_size;
        int lastBlockIndex   = (offset + buffer.length - 1) / _block_size;

        if (firstBlockIndex == lastBlockIndex)
        {
            System.arraycopy(
                (( SmallDocumentBlock ) blocks[ firstBlockIndex ])._data,
                firstBlockOffset, buffer, 0, buffer.length);
        }
        else
        {
            int buffer_offset = 0;

            System.arraycopy(
                (( SmallDocumentBlock ) blocks[ firstBlockIndex ])._data,
                firstBlockOffset, buffer, buffer_offset,
                _block_size - firstBlockOffset);
            buffer_offset += _block_size - firstBlockOffset;
            for (int j = firstBlockIndex + 1; j < lastBlockIndex; j++)
            {
                System.arraycopy((( SmallDocumentBlock ) blocks[ j ])._data,
                                 0, buffer, buffer_offset, _block_size);
                buffer_offset += _block_size;
            }
            System.arraycopy(
                (( SmallDocumentBlock ) blocks[ lastBlockIndex ])._data, 0,
                buffer, buffer_offset, buffer.length - buffer_offset);
        }
    }

    /**
     * Calculate the storage size of a set of SmallDocumentBlocks
     *
     * @param size number of SmallDocumentBlocks
     *
     * @return total size
     */

    public static int calcSize(int size)
    {
        return size * _block_size;
    }

    private static SmallDocumentBlock makeEmptySmallDocumentBlock(int size)
    {
        SmallDocumentBlock block = new SmallDocumentBlock(size);

        Arrays.fill(block._data, _default_fill);
        return block;
    }

    private static int convertToBlockCount(final int size)
    {
        return (size + _block_size - 1) / _block_size;
    }

    /* ********** START implementation of BlockWritable ********** */

    /**
     * Write the storage to an OutputStream
     *
     * @param stream the OutputStream to which the stored data should
     *               be written
     *
     * @exception IOException on problems writing to the specified
     *            stream
     */

    public void writeBlocks(final OutputStream stream)
        throws IOException
    {
        stream.write(_data);
    }

    /* **********  END  implementation of BlockWritable ********** */
    /* ********** START implementation of ListManagedBlock ********** */

    /**
     * Get the data from the block
     *
     * @return the block's data as a byte array
     *
     * @exception IOException if there is no data
     */

    public byte [] getData()
        throws IOException
    {
        return _data;
    }

    /* **********  END  implementation of ListManagedBlock ********** */
}   // end public class SmallDocumentBlock

