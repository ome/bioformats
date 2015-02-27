/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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

import loci.poi.poifs.common.POIFSConstants;
import loci.poi.poifs.property.Property;
import loci.poi.util.IntegerField;
import loci.poi.util.LittleEndian;
import loci.poi.util.LittleEndianConsts;

/**
 * A block of Property instances
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class PropertyBlock
    extends BigBlock
{
    private Property[]       _properties;

    private int _properties_per_block;
    private int blockSize;

    /**
     * Create a single instance initialized with default values
     *
     * @param properties the properties to be inserted
     * @param offset the offset into the properties array
     * @param blockSize size of a big block
     */

    private PropertyBlock(final Property [] properties, final int offset,
        int blockSize)
    {
        this.blockSize = blockSize;
        _properties_per_block = blockSize / POIFSConstants.PROPERTY_SIZE;
        _properties = new Property[ _properties_per_block ];
        for (int j = 0; j < _properties_per_block; j++)
        {
            _properties[ j ] = properties[ j + offset ];
        }
    }

    public int getBigBlockSize() { return blockSize; }

    /**
     * Create an array of PropertyBlocks from an array of Property
     * instances, creating empty Property instances to make up any
     * shortfall
     *
     * @param properties the Property instances to be converted into
     *                   PropertyBlocks, in a java List
     *
     * @return the array of newly created PropertyBlock instances
     */

    public static BlockWritable [] createPropertyBlockArray(
            final List properties, int size)
    {
        int        block_count   =
            (properties.size() + (size / POIFSConstants.PROPERTY_SIZE) - 1)
            / (size / POIFSConstants.PROPERTY_SIZE);
        Property[] to_be_written =
            new Property[ block_count * (size / POIFSConstants.PROPERTY_SIZE) ];

        System.arraycopy(properties.toArray(new Property[ 0 ]), 0,
                         to_be_written, 0, properties.size());
        for (int j = properties.size(); j < to_be_written.length; j++)
        {

            // create an instance of an anonymous inner class that
            // extends Property
            to_be_written[ j ] = new Property()
            {
                protected void preWrite()
                {
                }

                public boolean isDirectory()
                {
                    return false;
                }
            };
        }
        BlockWritable[] rvalue = new BlockWritable[ block_count ];

        for (int j = 0; j < block_count; j++)
        {
            rvalue[ j ] = new PropertyBlock(to_be_written,
                            j * (size / POIFSConstants.PROPERTY_SIZE), size);
        }
        return rvalue;
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
        for (int j = 0; j < _properties_per_block; j++)
        {
            _properties[ j ].writeData(stream);
        }
    }

    /* **********  END  extension of BigBlock ********** */
}   // end public class PropertyBlock

