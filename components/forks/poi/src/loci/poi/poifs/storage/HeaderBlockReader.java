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

import loci.poi.poifs.filesystem.OfficeXmlFileException;
import loci.poi.util.IOUtils;
import loci.poi.util.IntegerField;
import loci.poi.util.LittleEndian;
import loci.poi.util.LittleEndianConsts;
import loci.poi.util.LongField;
import loci.poi.util.ShortField;

/**
 * The block containing the archive header
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 */

public class HeaderBlockReader
    implements HeaderBlockConstants
{

    // number of big block allocation table blocks (int)
    private IntegerField _bat_count;

    // start of the property set block (int index of the property set
    // chain's first big block)
    private IntegerField _property_start;

    // start of the small block allocation table (int index of small
    // block allocation table's first big block)
    private IntegerField _sbat_start;

    // big block index for extension to the big block allocation table
    private IntegerField _xbat_start;
    private IntegerField _xbat_count;
    private byte[]       _data;
    private int batsInHeader;

    /**
     * create a new HeaderBlockReader from an InputStream
     *
     * @param stream the source InputStream
     * @param blockSize the size of a big block
     *
     * @exception IOException on errors or bad data
     */

    public HeaderBlockReader(final InputStream stream, int blockSize)
        throws IOException
    {
        batsInHeader = (blockSize - _bat_array_offset) /
          LittleEndianConsts.INT_SIZE;
        _data = new byte[ blockSize ];
        int byte_count = IOUtils.readFully(stream, _data);

        if (byte_count != blockSize)
        {
        	if (byte_count == -1)
        		//Cant have -1 bytes read in the error message!
        		byte_count = 0;
            String type = " byte" + ((byte_count == 1) ? ("")
                                                       : ("s"));

            throw new IOException("Unable to read entire header; "
                                  + byte_count + type + " read; expected "
                                  + blockSize + " bytes");
        }

        // verify signature
        LongField signature = new LongField(_signature_offset, _data);

        if (signature.get() != _signature)
        {
			// Is it one of the usual suspects?
			if(_data[0] == 0x50 && _data[1] == 0x4b && _data[2] == 0x03 &&
					_data[3] == 0x04) {
				throw new OfficeXmlFileException("The supplied data appears to be in the Office 2007+ XML. POI only supports OLE2 Office documents");
			}

			// Give a generic error
            throw new IOException("Invalid header signature; read "
                                  + signature.get() + ", expected "
                                  + _signature);
        }
        _bat_count      = new IntegerField(_bat_count_offset, _data);
        _property_start = new IntegerField(_property_start_offset, _data);
        _sbat_start     = new IntegerField(_sbat_start_offset, _data);
        _xbat_start     = new IntegerField(_xbat_start_offset, _data);
        _xbat_count     = new IntegerField(_xbat_count_offset, _data);
    }

    /**
     * get start of Property Table
     *
     * @return the index of the first block of the Property Table
     */

    public int getPropertyStart()
    {
        return _property_start.get();
    }

    /**
     * @return start of small block allocation table
     */

    public int getSBATStart()
    {
        return _sbat_start.get();
    }

    /**
     * @return number of BAT blocks
     */

    public int getBATCount()
    {
        return _bat_count.get();
    }

    /**
     * @return BAT array
     */

    public int [] getBATArray()
    {
        int[] result = new int[ batsInHeader ];
        int   offset = _bat_array_offset;

        for (int j = 0; j < batsInHeader; j++)
        {
            result[ j ] = LittleEndian.getInt(_data, offset);
            offset      += LittleEndianConsts.INT_SIZE;
        }
        return result;
    }

    /**
     * @return XBAT count
     */

    public int getXBATCount()
    {
        return _xbat_count.get();
    }

    /**
     * @return XBAT index
     */

    public int getXBATIndex()
    {
        return _xbat_start.get();
    }
}   // end public class HeaderBlockReader

