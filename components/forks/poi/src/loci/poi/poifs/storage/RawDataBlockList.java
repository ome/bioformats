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
import loci.common.*;
import loci.poi.util.*;

/**
 * A list of RawDataBlocks instances, and methods to manage the list
 *
 * @author Marc Johnson (mjohnson at apache dot org
 */

public class RawDataBlockList
    extends BlockListImpl
{
    private int bigBlockSize;

    /**
     * Constructor RawDataBlockList
     *
     * @param stream the InputStream from which the data will be read
     *
     * @exception IOException on I/O errors, and if an incomplete
     *            block is read
     */

    public RawDataBlockList(final RandomAccessInputStream stream, int size)
        throws IOException
    {
        List blocks = new ArrayList();
        bigBlockSize = size;
        long pointer = stream.getFilePointer();
        while (true)
        {
            RawDataBlock block = new RawDataBlock(stream, size, pointer);

            if (block.eof())
            {
                break;
            }
            blocks.add(block);
            if (size + pointer > stream.length()) {
              break;
            }
            pointer += size;
        }
        stream.seek(pointer);
        setBlocks(( RawDataBlock [] ) blocks.toArray(new RawDataBlock[ 0 ]));
    }

    public int getBigBlockSize() { return bigBlockSize; }

    public ListManagedBlock[] getBlocks() { return _blocks; }

}   // end public class RawDataBlockList

