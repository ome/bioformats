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

/**
 * Abstract base class of all POIFS block storage classes. All
 * extensions of BigBlock should write 512 bytes of data when
 * requested to write their data.
 *
 * This class has package scope, as there is no reason at this time to
 * make the class public.
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

import java.io.IOException;
import java.io.OutputStream;

abstract class BigBlock
    implements BlockWritable
{

    /**
     * Default implementation of write for extending classes that
     * contain their data in a simple array of bytes.
     *
     * @param stream the OutputStream to which the data should be
     *               written.
     * @param data the byte array of to be written.
     *
     * @exception IOException on problems writing to the specified
     *            stream.
     */

    protected void doWriteData(final OutputStream stream, final byte [] data)
        throws IOException
    {
        stream.write(data);
    }

    /**
     * Write the block's data to an OutputStream
     *
     * @param stream the OutputStream to which the stored data should
     *               be written
     *
     * @exception IOException on problems writing to the specified
     *            stream
     */

    abstract void writeData(final OutputStream stream)
        throws IOException;

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
        writeData(stream);
    }

    abstract int getBigBlockSize();

    /* **********  END  implementation of BlockWritable ********** */
}   // end abstract class BigBlock

