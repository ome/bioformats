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
        

package loci.poi.poifs.storage;

import java.io.*;

import java.util.*;

/**
 * A simple implementation of BlockList
 *
 * @author Marc Johnson (mjohnson at apache dot org
 */

class BlockListImpl
    implements BlockList
{
    protected ListManagedBlock[]         _blocks;
    private BlockAllocationTableReader _bat;

    /**
     * Constructor BlockListImpl
     */

    protected BlockListImpl()
    {
        _blocks = new ListManagedBlock[ 0 ];
        _bat    = null;
    }

    /**
     * provide blocks to manage
     *
     * @param blocks blocks to be managed
     */

    protected void setBlocks(final ListManagedBlock [] blocks)
    {
        _blocks = blocks;
    }

    /* ********** START implementation of BlockList ********** */

    /**
     * remove the specified block from the list
     *
     * @param index the index of the specified block; if the index is
     *              out of range, that's ok
     */

    public void zap(final int index)
    {
        if ((index >= 0) && (index < _blocks.length))
        {
            _blocks[ index ] = null;
        }
    }

    /**
     * remove and return the specified block from the list
     *
     * @param index the index of the specified block
     *
     * @return the specified block
     *
     * @exception IOException if the index is out of range or has
     *            already been removed
     */

    public ListManagedBlock remove(final int index)
        throws IOException
    {
        ListManagedBlock result = null;

        try
        {
            result = _blocks[ index ];
            if (result == null)
            {
                throw new IOException("block[ " + index
                                      + " ] already removed");
            }
            _blocks[ index ] = null;
        }
        catch (ArrayIndexOutOfBoundsException ignored)
        {
            throw new IOException("Cannot remove block[ " + index
                                  + " ]; out of range");
        }
        return result;
    }

    /**
     * get the blocks making up a particular stream in the list. The
     * blocks are removed from the list.
     *
     * @param startBlock the index of the first block in the stream
     *
     * @return the stream as an array of correctly ordered blocks
     *
     * @exception IOException if blocks are missing
     */

    public ListManagedBlock [] fetchBlocks(final int startBlock)
        throws IOException
    {
        if (_bat == null)
        {
            throw new IOException(
                "Improperly initialized list: no block allocation table provided");
        }
        return _bat.fetchBlocks(startBlock, this);
    }

    /**
     * set the associated BlockAllocationTable
     *
     * @param bat the associated BlockAllocationTable
     *
     * @exception IOException
     */

    public void setBAT(final BlockAllocationTableReader bat)
        throws IOException
    {
        if (_bat != null)
        {
            throw new IOException(
                "Attempt to replace existing BlockAllocationTable");
        }
        _bat = bat;
    }

    /* **********  END  implementation of BlockList ********** */
}   // end package-scope class BlockListImpl

