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

/**
 * Title:        Default Row Height Record
 * Description:  Row height for rows with undefined or not explicitly defined
 *               heights.
 * REFERENCE:  PG 301 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 */

public class DefaultRowHeightRecord
    extends Record
{
    public final static short sid = 0x225;
    private short             field_1_option_flags;
    private short             field_2_row_height;

    public DefaultRowHeightRecord()
    {
    }

    /**
     * Constructs a DefaultRowHeight record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public DefaultRowHeightRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A DefaultRowHeight RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_option_flags = in.readShort();
        field_2_row_height   = in.readShort();
    }

    /**
     * set the (currently unimportant to HSSF) option flags
     * @param flags the bitmask to set
     */

    public void setOptionFlags(short flags)
    {
        field_1_option_flags = flags;
    }

    /**
     * set the default row height
     * @param height    for undefined rows/rows w/undefined height
     */

    public void setRowHeight(short height)
    {
        field_2_row_height = height;
    }

    /**
     * get the (currently unimportant to HSSF) option flags
     * @return flags - the current bitmask
     */

    public short getOptionFlags()
    {
        return field_1_option_flags;
    }

    /**
     * get the default row height
     * @return rowheight for undefined rows/rows w/undefined height
     */

    public short getRowHeight()
    {
        return field_2_row_height;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DEFAULTROWHEIGHT]\n");
        buffer.append("    .optionflags    = ")
            .append(Integer.toHexString(getOptionFlags())).append("\n");
        buffer.append("    .rowheight      = ")
            .append(Integer.toHexString(getRowHeight())).append("\n");
        buffer.append("[/DEFAULTROWHEIGHT]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x4);
        LittleEndian.putShort(data, 4 + offset, getOptionFlags());
        LittleEndian.putShort(data, 6 + offset, getRowHeight());
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 8;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
      DefaultRowHeightRecord rec = new DefaultRowHeightRecord();
      rec.field_1_option_flags = field_1_option_flags;
      rec.field_2_row_height = field_2_row_height;
      return rec;
    }
}
