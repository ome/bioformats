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
        
package loci.poi.ddf;

import loci.poi.hssf.record.RecordFormatException;
import loci.poi.util.HexDump;
import loci.poi.util.LittleEndian;

/**
 * Holds data from the parent application. Most commonly used to store
 *  text in the format of the parent application, rather than in 
 *  Escher format. We don't attempt to understand the contents, since
 *  they will be in the parent's format, not Escher format.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Nick Burch  (nick at torchbox dot com)
 */
public class EscherTextboxRecord extends EscherRecord
{
    public static final short RECORD_ID = (short)0xF00D;
    public static final String RECORD_DESCRIPTION = "msofbtClientTextbox";

    private static final byte[] NO_BYTES = new byte[0];

    /** The data for this record not including the the 8 byte header */
    private byte[] thedata = NO_BYTES;

    public EscherTextboxRecord()
    {
    }

    /**
     * This method deserializes the record from a byte array.
     *
     * @param data          The byte array containing the escher record information
     * @param offset        The starting offset into <code>data</code>.
     * @param recordFactory May be null since this is not a container record.
     * @return The number of bytes read from the byte array.
     */
    public int fillFields( byte[] data, int offset, EscherRecordFactory recordFactory )
    {
        int bytesRemaining = readHeader( data, offset );

        // Save the data, ready for the calling code to do something
        //  useful with it
        thedata = new byte[bytesRemaining];
        System.arraycopy( data, offset + 8, thedata, 0, bytesRemaining );
        return bytesRemaining + 8;
    }

    /**
     * Writes this record and any contained records to the supplied byte
     * array.
     *
     * @return  the number of bytes written.
     */
    public int serialize( int offset, byte[] data, EscherSerializationListener listener )
    {
        listener.beforeRecordSerialize( offset, getRecordId(), this );

        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset+2, getRecordId());
        int remainingBytes = thedata.length;
        LittleEndian.putInt(data, offset+4, remainingBytes);
        System.arraycopy(thedata, 0, data, offset+8, thedata.length);
        int pos = offset+8+thedata.length;

        listener.afterRecordSerialize( pos, getRecordId(), pos - offset, this );
        int size = pos - offset;
        if (size != getRecordSize())
            throw new RecordFormatException(size + " bytes written but getRecordSize() reports " + getRecordSize());
        return size;
    }

    /**
     * Returns any extra data associated with this record.  In practice excel
     * does not seem to put anything here, but with PowerPoint this will
     * contain the bytes that make up a TextHeaderAtom followed by a
     * TextBytesAtom/TextCharsAtom
     */
    public byte[] getData()
    {
        return thedata;
    }

    /**
     * Sets the extra data (in the parent application's format) to be
     * contained by the record. Used when the parent application changes
     * the contents.
     */
    public void setData(byte[] b, int start, int length)
    {
        thedata = new byte[length];
        System.arraycopy(b,start,thedata,0,length);
    }
    public void setData(byte[] b) {
        setData(b,0,b.length);
    }


    /**
     * Returns the number of bytes that are required to serialize this record.
     *
     * @return Number of bytes
     */
    public int getRecordSize()
    {
        return 8 + thedata.length;
    }

    public Object clone()
    {
        // shallow clone
        return super.clone();
    }

    /**
     * The short name for this record
     */
    public String getRecordName()
    {
        return "ClientTextbox";
    }

    public String toString()
    {
        String nl = System.getProperty( "line.separator" );

        String theDumpHex = "";
        try
        {
            if (thedata.length != 0)
            {
                theDumpHex = "  Extra Data:" + nl;
                theDumpHex += HexDump.dump(thedata, 0, 0);
            }
        }
        catch ( Exception e )
        {
            theDumpHex = "Error!!";
        }

        return getClass().getName() + ":" + nl +
                "  isContainer: " + isContainerRecord() + nl +
                "  options: 0x" + HexDump.toHex( getOptions() ) + nl +
                "  recordId: 0x" + HexDump.toHex( getRecordId() ) + nl +
                "  numchildren: " + getChildRecords().size() + nl +
                theDumpHex;
    }

}



