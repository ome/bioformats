/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Arrays;

import loci.common.*;

import loci.poi.poifs.common.POIFSConstants;
import loci.poi.util.*;

/**
 * A block of document data.
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class DocumentBlock
    extends BigBlock
{
    private static final byte _default_value = ( byte ) 0xFF;
    private int               _bytes_read;
    private int blockSize;
    private long offset;

    /**
     * create a document block from a raw data block
     *
     * @param block the raw data block
     * @param size the block size
     *
     * @exception IOException
     */

    public DocumentBlock(final RawDataBlock block, int size)
        throws IOException
    {
        offset = block.getOffset();
        _bytes_read = block.getLength();
        blockSize = size;
    }

    /**
     * Create a single instance initialized with data.
     *
     * @param stream the InputStream delivering the data.
     *
     * @exception IOException
     */

    public DocumentBlock(final RandomAccessInputStream stream, int size)
        throws IOException
    {
        this(size);
        int count = stream.skipBytes(size);
        _bytes_read = (count == -1) ? 0 : count;
    }

    /**
     * Create a single instance initialized with default values
     */

    private DocumentBlock(int size)
    {
        blockSize = size;
    }

    /**
     * Get the number of bytes read for this block
     *
     * @return bytes read into the block
     */

    public int size()
    {
        return _bytes_read;
    }

    public int getBigBlockSize() { return blockSize; }

    /**
     * Was this a partially read block?
     *
     * @return true if the block was only partially filled with data
     */

    public boolean partiallyRead()
    {
        return _bytes_read != blockSize;
    }

    /**
     * @return the fill byte used
     */

    public static byte getFillByte()
    {
        return _default_value;
    }

    /**
     * convert a single long array into an array of DocumentBlock
     * instances
     *
     * @param offset the offset into the array of blocks to read from
     * @param stream the {@link RandomAccessInputStream} to read from
     * @param size the intended size of the array (which may be smaller)
     * @param numBytes the number of bytes
     * @param blockSize the size of a big block
     *
     * @return an array of DocumentBlock instances, filled from the
     *         input array
     */

    public static DocumentBlock[] convert(long offset,
      RandomAccessInputStream stream, int numBytes, int size, int blockSize)
    {
        DocumentBlock[] rval   =
            new DocumentBlock[ (size + blockSize - 1) / blockSize ];

        int subOffset = 0;
        for (int k = 0; k < rval.length; k++)
        {
            rval[ k ] = new DocumentBlock(blockSize);
            if (subOffset < numBytes)
            {
                rval[k].offset = offset + subOffset;
                rval[k]._bytes_read = Math.min(blockSize, numBytes - subOffset);
            }
            offset += blockSize;
        }
        return rval;
    }

    /**
     * read data from an array of DocumentBlocks
     *
     * @param blocks the blocks to read from
     * @param buffer the buffer to write the data into
     * @param offset the offset into the array of blocks to read from
     */

    public static void read(final DocumentBlock [] blocks,
                            final byte [] buffer, final int offset,
                            int blockSize, RandomAccessInputStream stream)
    {
        int firstBlockIndex  = offset / blockSize;
        int firstBlockOffset = offset % blockSize;
        int lastBlockIndex   = (offset + buffer.length - 1)
                               / blockSize;
        try {
          if (firstBlockIndex == lastBlockIndex)
          {
              stream.seek(blocks[firstBlockIndex].offset + firstBlockOffset);
              stream.read(buffer, 0, buffer.length);
          }
          else
          {
              int buffer_offset = 0;
              stream.seek(blocks[firstBlockIndex].offset + firstBlockOffset);
              stream.read(buffer, buffer_offset, blockSize - firstBlockOffset);
              buffer_offset += blockSize - firstBlockOffset;
              for (int j = firstBlockIndex + 1; j < lastBlockIndex; j++)
              {
                  stream.seek(blocks[j].offset);
                  stream.read(buffer, buffer_offset, blockSize);
                  buffer_offset += blockSize;
              }
              stream.seek(blocks[lastBlockIndex].offset);
              stream.read(buffer, buffer_offset, buffer.length - buffer_offset);
          }
        }
        catch (IOException e) { }
    }

    /* ********** START extension of BigBlock ********** */

    /**
     * Write the block's data to an OutputStream
     *
     * @param stream the OutputStream to which the stored data should
     *               be written
     *
     * @exception IOException on problems writing to the specified
     *            stream
     */

    void writeData(final OutputStream stream)
        throws IOException
    {
        doWriteData(stream, new byte[blockSize]);
    }

    /* **********  END  extension of BigBlock ********** */
}   // end public class DocumentBlock

