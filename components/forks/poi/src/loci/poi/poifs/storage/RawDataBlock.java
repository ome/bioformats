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

import loci.common.*;
import loci.poi.util.*;

import java.io.*;

/**
 * A big block created from an InputStream, holding the raw data
 *
 * @author Marc Johnson (mjohnson at apache dot org
 */

public class RawDataBlock
    implements ListManagedBlock
{
    private boolean _eof;
    private int blockSize;
    private RandomAccessInputStream stream;
    private long offset;
    private int length;

    /**
     * Constructor RawDataBlock
     *
     * @param stream the InputStream from which the data will be read
     * @param size the block size
     *
     * @exception IOException on I/O errors, and if an insufficient
     *            amount of data is read
     */

    public RawDataBlock(final RandomAccessInputStream stream, int size, long pointer)
        throws IOException
    {
        this.stream = stream;
        blockSize = size;
        length = blockSize;
        _eof = (stream.length() - pointer) <= 0;
        offset = pointer;
    }

    /**
     * When we read the data, did we hit end of file?
     *
     * @return true if no data was read because we were at the end of
     *         the file, else false
     *
     * @exception IOException
     */

    public boolean eof()
        throws IOException
    {
        return _eof;
    }

    public int getBlockSize() { return blockSize; }

    public long getOffset() { return offset; }

    public int getLength() { return length; }

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
        if (eof())
        {
            throw new IOException("Cannot return empty data");
        }
        byte[] b = new byte[length];
        stream.seek(offset);
        stream.read(b);
        return b;
    }

    /* **********  END  implementation of ListManagedBlock ********** */
}   // end public class RawDataBlock

