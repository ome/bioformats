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
        

package loci.poi.hssf.record;

import loci.poi.util.LittleEndian;
import loci.poi.util.StringUtil;

/**
 * Supports the STRING record structure.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class StringRecord
        extends Record
{
    public final static short   sid = 0x207;
    private int                 field_1_string_length;
    private byte                field_2_unicode_flag;
    private String              field_3_string;


    public StringRecord()
    {
    }

    /**
     * Constructs a String record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */
    public StringRecord(RecordInputStream in)
    {
        super(in);
    }


    /**
     * Throw a runtime exception in the event of a
     * record passed with a differing ID.
     *
     * @param id alleged id for this record
     */
    protected void validateSid( short id )
    {
        if (id != sid)
        {
            throw new RecordFormatException("Not a valid StringRecord");
        }
    }

    /**
     * @param in the RecordInputstream to read the record from
     */
    protected void fillFields( RecordInputStream in)
    {
        field_1_string_length           = in.readShort();
        field_2_unicode_flag            = in.readByte();
        byte[] data = in.readRemainder();
        //Why isnt this using the in.readString methods???
        if (isUnCompressedUnicode())
        {
            field_3_string = StringUtil.getFromUnicodeLE(data, 0, field_1_string_length );
        }
        else
        {
            field_3_string = StringUtil.getFromCompressedUnicode(data, 0, field_1_string_length);
        }
    }

    public boolean isInValueSection()
    {
        return true;
    }

    private int getStringByteLength()
    {
        return isUnCompressedUnicode() ? field_1_string_length * 2 : field_1_string_length;
    }

    /**
     * gives the current serialized size of the record. Should include the sid and reclength (4 bytes).
     */
    public int getRecordSize()
    {
        return 4 + 2 + 1 + getStringByteLength();
    }

    /**
     * is this uncompressed unicode (16bit)?  Or just 8-bit compressed?
     * @return isUnicode - True for 16bit- false for 8bit
     */
    public boolean isUnCompressedUnicode()
    {
        return (field_2_unicode_flag == 1);
    }

    /**
     * called by the class that is responsible for writing this sucker.
     * Subclasses should implement this so that their data is passed back in a
     * byte array.
     *
     * @param offset to begin writing at
     * @param data byte array containing instance data
     * @return number of bytes written
     */
    public int serialize( int offset, byte[] data )
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) (3 + getStringByteLength()));
        LittleEndian.putUShort(data, 4 + offset, field_1_string_length);
        data[6 + offset] = field_2_unicode_flag;
        if (isUnCompressedUnicode())
        {
            StringUtil.putUnicodeLE(field_3_string, data, 7 + offset);
        }
        else
        {
            StringUtil.putCompressedUnicode(field_3_string, data, 7 + offset);
        }
        return getRecordSize();
    }

    /**
     * return the non static version of the id for this record.
     */
    public short getSid()
    {
        return sid;
    }

    /**
     * @return The string represented by this record.
     */
    public String getString()
    {
        return field_3_string;
    }

    /**
     * Sets whether the string is compressed or not
     * @param unicode_flag   1 = uncompressed, 0 = compressed
     */
    public void setCompressedFlag( byte unicode_flag )
    {
        this.field_2_unicode_flag = unicode_flag;
    }

    /**
     * Sets the string represented by this record.
     */
    public void setString( String string )
    {
        this.field_1_string_length = string.length();
        this.field_3_string = string;
        setCompressedFlag(StringUtil.hasMultibyte(string) ?  (byte)1 : (byte)0);        
    }



    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[STRING]\n");
        buffer.append("    .string            = ")
            .append(field_3_string).append("\n");
        buffer.append("[/STRING]\n");
        return buffer.toString();
    }
    
    public Object clone() {
        StringRecord rec = new StringRecord();
        rec.field_1_string_length = this.field_1_string_length;
        rec.field_2_unicode_flag= this.field_2_unicode_flag;
        rec.field_3_string = this.field_3_string;
        return rec;

    }

}
