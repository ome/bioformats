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

/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package loci.poi.ddf;

import loci.poi.util.HexDump;
import loci.poi.util.LittleEndian;

import java.io.ByteArrayOutputStream;

/**
 * @author Glen Stampoultzis
 * @version $Id: EscherBitmapBlip.java 569827 2007-08-26 15:26:29Z yegor $
 */
public class EscherBitmapBlip
        extends EscherBlipRecord
{
    public static final short RECORD_ID_JPEG = (short) 0xF018 + 5;
    public static final short RECORD_ID_PNG = (short) 0xF018 + 6;
    public static final short RECORD_ID_DIB = (short) 0xF018 + 7;

    private static final int HEADER_SIZE = 8;

    private byte[] field_1_UID;
    private byte field_2_marker = (byte) 0xFF;


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
        int bytesAfterHeader = readHeader( data, offset );
        int pos = offset + HEADER_SIZE;

        field_1_UID = new byte[16];
        System.arraycopy( data, pos, field_1_UID, 0, 16 ); pos += 16;
        field_2_marker = data[pos]; pos++;

        field_pictureData = new byte[bytesAfterHeader - 17];
        System.arraycopy( data, pos, field_pictureData, 0, field_pictureData.length );

        return bytesAfterHeader + HEADER_SIZE;
    }

    /**
      * Serializes the record to an existing byte array.
      *
      * @param offset    the offset within the byte array
      * @param data      the data array to serialize to
      * @param listener  a listener for begin and end serialization events.  This
      *                  is useful because the serialization is
      *                  hierarchical/recursive and sometimes you need to be able
      *                  break into that.
      * @return the number of bytes written.
      */
     public int serialize( int offset, byte[] data, EscherSerializationListener listener )
     {
         listener.beforeRecordSerialize(offset, getRecordId(), this);

         LittleEndian.putShort( data, offset, getOptions() );
         LittleEndian.putShort( data, offset + 2, getRecordId() );
         LittleEndian.putInt( data, offset + 4, getRecordSize() - HEADER_SIZE );
         int pos = offset + HEADER_SIZE;

         System.arraycopy( field_1_UID, 0, data, pos, 16 );
         data[pos + 16] = field_2_marker;
         System.arraycopy( field_pictureData, 0, data, pos + 17, field_pictureData.length );

         listener.afterRecordSerialize(offset + getRecordSize(), getRecordId(), getRecordSize(), this);
         return HEADER_SIZE + 16 + 1 + field_pictureData.length;
     }

    /**
      * Returns the number of bytes that are required to serialize this record.
      *
      * @return Number of bytes
      */
     public int getRecordSize()
     {
         return 8 + 16 + 1 + field_pictureData.length;
     }

    public byte[] getUID()
    {
        return field_1_UID;
    }

    public void setUID( byte[] field_1_UID )
    {
        this.field_1_UID = field_1_UID;
    }

    public byte getMarker()
    {
        return field_2_marker;
    }

    public void setMarker( byte field_2_marker )
    {
        this.field_2_marker = field_2_marker;
    }

    public String toString()
    {
        String nl = System.getProperty( "line.separator" );

        String extraData;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try
        {
            HexDump.dump( this.field_pictureData, 0, b, 0 );
            extraData = b.toString();
        }
        catch ( Exception e )
        {
            extraData = e.toString();
        }
        return getClass().getName() + ":" + nl +
                "  RecordId: 0x" + HexDump.toHex( getRecordId() ) + nl +
                "  Options: 0x" + HexDump.toHex( getOptions() ) + nl +
                "  UID: 0x" + HexDump.toHex( field_1_UID ) + nl +
                "  Marker: 0x" + HexDump.toHex( field_2_marker ) + nl +
                "  Extra Data:" + nl + extraData;
    }

}
