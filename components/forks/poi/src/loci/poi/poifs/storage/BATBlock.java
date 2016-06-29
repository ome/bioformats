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
import java.io.OutputStream;

import java.util.Arrays;

import loci.poi.poifs.common.POIFSConstants;
import loci.poi.util.IntegerField;
import loci.poi.util.LittleEndian;
import loci.poi.util.LittleEndianConsts;

/**
 * A block of block allocation table entries. BATBlocks are created
 * only through a static factory method: createBATBlocks.
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class BATBlock
    extends BigBlock
{
    private static final byte _default_value          = ( byte ) 0xFF;
    private IntegerField[]    _fields;
    private byte[]            _data;
    private int _entries_per_block;
    private int _xbat_chain_offset;
    private int blockSize;

    /**
     * Create a single instance initialized with default values
     */

    private BATBlock(int blockSize)
    {
        this.blockSize = blockSize;
        _entries_per_block = blockSize / LittleEndianConsts.INT_SIZE;
        _xbat_chain_offset =
          (_entries_per_block - 1) * LittleEndianConsts.INT_SIZE;
        _data = new byte[ blockSize ];
        Arrays.fill(_data, _default_value);
        _fields = new IntegerField[ _entries_per_block ];
        int offset = 0;

        for (int j = 0; j < _entries_per_block; j++)
        {
            _fields[ j ] = new IntegerField(offset);
            offset       += LittleEndianConsts.INT_SIZE;
        }
    }

    public int getBigBlockSize() { return blockSize; }

    /**
     * Create an array of BATBlocks from an array of int block
     * allocation table entries
     *
     * @param entries the array of int entries
     *
     * @return the newly created array of BATBlocks
     */

    public static BATBlock [] createBATBlocks(final int [] entries, int size)
    {
        int        block_count = calculateStorageRequirements(entries.length,
          size);
        BATBlock[] blocks      = new BATBlock[ block_count ];
        int        index       = 0;
        int        remaining   = entries.length;

        int entriesPerBlock = size / LittleEndianConsts.INT_SIZE;
        for (int j = 0; j < entries.length; j += entriesPerBlock)
        {
            blocks[ index++ ] = new BATBlock(entries, j,
                                             (remaining > entriesPerBlock)
                                             ? j + entriesPerBlock
                                             : entries.length, size);
            remaining         -= entriesPerBlock;
        }
        return blocks;
    }

    /**
     * Create an array of XBATBlocks from an array of int block
     * allocation table entries
     *
     * @param entries the array of int entries
     * @param startBlock the start block of the array of XBAT blocks
     *
     * @return the newly created array of BATBlocks
     */

    public static BATBlock [] createXBATBlocks(final int [] entries,
                                               final int startBlock, int size)
    {
        int        block_count =
            calculateXBATStorageRequirements(entries.length, size);
        BATBlock[] blocks      = new BATBlock[ block_count ];
        int        index       = 0;
        int        remaining   = entries.length;

        int entriesPerXBAT = (size / LittleEndianConsts.INT_SIZE) - 1;

        if (block_count != 0)
        {
            for (int j = 0; j < entries.length; j += entriesPerXBAT)
            {
                blocks[ index++ ] =
                    new BATBlock(entries, j,
                                 (remaining > entriesPerXBAT)
                                 ? j + entriesPerXBAT
                                 : entries.length, size);
                remaining         -= entriesPerXBAT;
            }
            for (index = 0; index < blocks.length - 1; index++)
            {
                blocks[ index ].setXBATChain(startBlock + index + 1);
            }
            blocks[ index ].setXBATChain(POIFSConstants.END_OF_CHAIN);
        }
        return blocks;
    }

    /**
     * Calculate how many BATBlocks are needed to hold a specified
     * number of BAT entries.
     *
     * @param entryCount the number of entries
     *
     * @return the number of BATBlocks needed
     */

    public static int calculateStorageRequirements(final int entryCount,
      int size)
    {
        int entriesPerBlock = size / LittleEndianConsts.INT_SIZE;
        return (entryCount + entriesPerBlock - 1) / entriesPerBlock;
    }

    /**
     * Calculate how many XBATBlocks are needed to hold a specified
     * number of BAT entries.
     *
     * @param entryCount the number of entries
     *
     * @return the number of XBATBlocks needed
     */

    public static int calculateXBATStorageRequirements(final int entryCount,
      int size)
    {
        int entriesPerXBAT = (size / LittleEndianConsts.INT_SIZE) - 1;
        return (entryCount + entriesPerXBAT - 1) / entriesPerXBAT;
    }

    /**
     * @return number of entries per block
     */

    public static final int entriesPerBlock(int size)
    {
        return size / LittleEndianConsts.INT_SIZE;
    }

    /**
     * @return number of entries per XBAT block
     */

    public static final int entriesPerXBATBlock(int size)
    {
      return entriesPerBlock(size) - 1;
    }

    /**
     * @return offset of chain index of XBAT block
     */

    public static final int getXBATChainOffset(int size)
    {
      return entriesPerXBATBlock(size) * LittleEndianConsts.INT_SIZE;
    }

    private void setXBATChain(int chainIndex)
    {
        _fields[ _entries_per_block - 1 ].set(chainIndex, _data);
    }
/**
     * Create a single instance initialized (perhaps partially) with entries
     *
     * @param entries the array of block allocation table entries
     * @param start_index the index of the first entry to be written
     *                    to the block
     * @param end_index the index, plus one, of the last entry to be
     *                  written to the block (writing is for all index
     *                  k, start_index <= k < end_index)
     */

    private BATBlock(final int [] entries, final int start_index,
                     final int end_index, int size)
    {
        this(size);
        for (int k = start_index; k < end_index; k++)
        {
            _fields[ k - start_index ].set(entries[ k ], _data);
        }
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
        doWriteData(stream, _data);
    }

    /* **********  END  extension of BigBlock ********** */
}   // end public class BATBlock

