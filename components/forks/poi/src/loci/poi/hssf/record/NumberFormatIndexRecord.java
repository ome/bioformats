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
        

package loci.poi.hssf.record;



import loci.poi.util.*;

/**
 * The number format index record indexes format table.  This applies to an axis.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class NumberFormatIndexRecord
    extends Record
{
    public final static short      sid                             = 0x104e;
    private  short      field_1_formatIndex;


    public NumberFormatIndexRecord()
    {

    }

    /**
     * Constructs a NumberFormatIndex record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public NumberFormatIndexRecord(RecordInputStream in)
    {
        super(in);
    
    }

    /**
     * Checks the sid matches the expected side for this record
     *
     * @param id   the expected sid.
     */
    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("Not a NumberFormatIndex record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_formatIndex            = in.readShort();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[IFMT]\n");
        buffer.append("    .formatIndex          = ")
            .append("0x").append(HexDump.toHex(  getFormatIndex ()))
            .append(" (").append( getFormatIndex() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/IFMT]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_formatIndex);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        NumberFormatIndexRecord rec = new NumberFormatIndexRecord();
    
        rec.field_1_formatIndex = field_1_formatIndex;
        return rec;
    }




    /**
     * Get the format index field for the NumberFormatIndex record.
     */
    public short getFormatIndex()
    {
        return field_1_formatIndex;
    }

    /**
     * Set the format index field for the NumberFormatIndex record.
     */
    public void setFormatIndex(short field_1_formatIndex)
    {
        this.field_1_formatIndex = field_1_formatIndex;
    }


}  // END OF CLASS




