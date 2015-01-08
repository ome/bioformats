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

import java.io.IOException;
import java.io.OutputStream;

import java.util.*;

import loci.poi.poifs.common.POIFSConstants;
import loci.poi.poifs.filesystem.BATManaged;
import loci.poi.util.IntList;
import loci.poi.util.LittleEndian;
import loci.poi.util.LittleEndianConsts;

/**
 * This class manages and creates the Block Allocation Table, which is
 * basically a set of linked lists of block indices.
 * <P>
 * Each block of the filesystem has an index. The first block, the
 * header, is skipped; the first block after the header is index 0,
 * the next is index 1, and so on.
 * <P>
 * A block's index is also its index into the Block Allocation
 * Table. The entry that it finds in the Block Allocation Table is the
 * index of the next block in the linked list of blocks making up a
 * file, or it is set to -2: end of list.
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class BlockAllocationTableWriter
    implements BlockWritable, BATManaged
{
    private IntList    _entries;
    private BATBlock[] _blocks;
    private int        _start_block;

    /**
     * create a BlockAllocationTableWriter
     */

    public BlockAllocationTableWriter()
    {
        _start_block = POIFSConstants.END_OF_CHAIN;
        _entries     = new IntList();
        _blocks      = new BATBlock[ 0 ];
    }

    /**
     * Create the BATBlocks we need
     *
     * @return start block index of BAT blocks
     */

    public int createBlocks(int size)
    {
        int xbat_blocks = 0;
        int bat_blocks  = 0;

        while (true)
        {
            int calculated_bat_blocks  =
                BATBlock.calculateStorageRequirements(bat_blocks
                                                      + xbat_blocks
                                                      + _entries.size(), size);
            int calculated_xbat_blocks =
                HeaderBlockWriter
                    .calculateXBATStorageRequirements(calculated_bat_blocks,
                    size);

            if ((bat_blocks == calculated_bat_blocks)
                    && (xbat_blocks == calculated_xbat_blocks))
            {

                // stable ... we're OK
                break;
            }
            else
            {
                bat_blocks  = calculated_bat_blocks;
                xbat_blocks = calculated_xbat_blocks;
            }
        }
        int startBlock = allocateSpace(bat_blocks);

        allocateSpace(xbat_blocks);
        simpleCreateBlocks(size);
        return startBlock;
    }

    /**
     * Allocate space for a block of indices
     *
     * @param blockCount the number of blocks to allocate space for
     *
     * @return the starting index of the blocks
     */

    public int allocateSpace(final int blockCount)
    {
        int startBlock = _entries.size();

        if (blockCount > 0)
        {
            int limit = blockCount - 1;
            int index = startBlock + 1;

            for (int k = 0; k < limit; k++)
            {
                _entries.add(index++);
            }
            _entries.add(POIFSConstants.END_OF_CHAIN);
        }
        return startBlock;
    }

    /**
     * get the starting block
     *
     * @return the starting block index
     */

    public int getStartBlock()
    {
        return _start_block;
    }

    /**
     * create the BATBlocks
     */

    void simpleCreateBlocks(int size)
    {
        _blocks = BATBlock.createBATBlocks(_entries.toArray(), size);
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
        for (int j = 0; j < _blocks.length; j++)
        {
            _blocks[ j ].writeBlocks(stream);
        }
    }

    /* **********  END  implementation of BlockWritable ********** */
    /* ********** START implementation of BATManaged ********** */

    /**
     * Return the number of BigBlock's this instance uses
     *
     * @return count of BigBlock instances
     */

    public int countBlocks()
    {
        return _blocks.length;
    }

    /**
     * Set the start block for this instance
     *
     * @param start_block
     */

    public void setStartBlock(int start_block)
    {
        _start_block = start_block;
    }

    /* **********  END  implementation of BATManaged ********** */
}   // end class BlockAllocationTableWriter

