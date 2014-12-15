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
        

package loci.poi.poifs.property;

import java.io.IOException;
import java.io.OutputStream;

import java.util.*;

import loci.poi.poifs.common.POIFSConstants;
import loci.poi.poifs.filesystem.BATManaged;
import loci.poi.poifs.storage.BlockWritable;
import loci.poi.poifs.storage.PropertyBlock;
import loci.poi.poifs.storage.RawDataBlock;
import loci.poi.poifs.storage.RawDataBlockList;

/**
 * This class embodies the Property Table for the filesystem; this is
 * basically the dsirectory for all of the documents in the
 * filesystem.
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class PropertyTable
    implements BATManaged, BlockWritable
{
    private int             _start_block;
    private List            _properties;
    private BlockWritable[] _blocks;

    /**
     * Default constructor
     */

    public PropertyTable()
    {
        _start_block = POIFSConstants.END_OF_CHAIN;
        _properties  = new ArrayList();
        addProperty(new RootProperty());
        _blocks = null;
    }

    /**
     * reading constructor (used when we've read in a file and we want
     * to extract the property table from it). Populates the
     * properties thoroughly
     *
     * @param startBlock the first block of the property table
     * @param blockList the list of blocks
     *
     * @exception IOException if anything goes wrong (which should be
     *            a result of the input being NFG)
     */

    public PropertyTable(final int startBlock,
                         final RawDataBlockList blockList)
        throws IOException
    {
        _start_block = POIFSConstants.END_OF_CHAIN;
        _blocks      = null;
        _properties  =
            PropertyFactory
                .convertToProperties(blockList.fetchBlocks(startBlock));
        populatePropertyTree(( DirectoryProperty ) _properties.get(0));
    }

    /**
     * Add a property to the list of properties we manage
     *
     * @param property the new Property to manage
     */

    public void addProperty(final Property property)
    {
        _properties.add(property);
    }

    /**
     * Remove a property from the list of properties we manage
     *
     * @param property the Property to be removed
     */

    public void removeProperty(final Property property)
    {
        _properties.remove(property);
    }

    /**
     * Get the root property
     *
     * @return the root property
     */

    public RootProperty getRoot()
    {

        // it's always the first element in the List
        return ( RootProperty ) _properties.get(0);
    }

    /**
     * Prepare to be written
     */

    public void preWrite()
    {
        Property[] properties =
            ( Property [] ) _properties.toArray(new Property[ 0 ]);

        // give each property its index
        for (int k = 0; k < properties.length; k++)
        {
            properties[ k ].setIndex(k);
        }

        // allocate the blocks for the property table
        _blocks = PropertyBlock.createPropertyBlockArray(_properties, 512);

        // prepare each property for writing
        for (int k = 0; k < properties.length; k++)
        {
            properties[ k ].preWrite();
        }
    }

    /**
     * Get the start block for the property table
     *
     * @return start block index
     */

    public int getStartBlock()
    {
        return _start_block;
    }

    private void populatePropertyTree(DirectoryProperty root)
        throws IOException
    {
        int index = root.getChildIndex();

        if (!Property.isValidIndex(index))
        {

            // property has no children
            return;
        }
        Stack children = new Stack();

        children.push(_properties.get(index));
        while (!children.empty())
        {
            Property property = ( Property ) children.pop();

            root.addChild(property);
            if (property.isDirectory())
            {
                populatePropertyTree(( DirectoryProperty ) property);
            }
            index = property.getPreviousChildIndex();
            if (Property.isValidIndex(index))
            {
                children.push(_properties.get(index));
            }
            index = property.getNextChildIndex();
            if (Property.isValidIndex(index))
            {
                children.push(_properties.get(index));
            }
        }
    }

    /* ********** START implementation of BATManaged ********** */

    /**
     * Return the number of BigBlock's this instance uses
     *
     * @return count of BigBlock instances
     */

    public int countBlocks()
    {
        return (_blocks == null) ? 0
                                 : _blocks.length;
    }

    /**
     * Set the start block for this instance
     *
     * @param index index into the array of BigBlock instances making
     *              up the the filesystem
     */

    public void setStartBlock(final int index)
    {
        _start_block = index;
    }

    /* **********  END  implementation of BATManaged ********** */
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
        if (_blocks != null)
        {
            for (int j = 0; j < _blocks.length; j++)
            {
                _blocks[ j ].writeBlocks(stream);
            }
        }
    }

    /* **********  END  implementation of BlockWritable ********** */
}   // end public class PropertyTable

