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
 * Title:        Default Column Width Record<P>
 * Description:  Specifies the default width for columns that have no specific
 *               width set.<P>
 * REFERENCE:  PG 302 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 */

public class DefaultColWidthRecord
    extends Record
{
    public final static short sid = 0x55;
    private short             field_1_col_width;

    public DefaultColWidthRecord()
    {
    }

    /**
     * Constructs a DefaultColumnWidth record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public DefaultColWidthRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A DefaultColWidth RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_col_width = in.readShort();
    }

    /**
     * set the default column width
     * @param height defaultwidth for columns
     */

    public void setColWidth(short height)
    {
        field_1_col_width = height;
    }

    /**
     * get the default column width
     * @return defaultwidth for columns
     */

    public short getColWidth()
    {
        return field_1_col_width;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DEFAULTCOLWIDTH]\n");
        buffer.append("    .colwidth      = ")
            .append(Integer.toHexString(getColWidth())).append("\n");
        buffer.append("[/DEFAULTCOLWIDTH]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x2);
        LittleEndian.putShort(data, 4 + offset, getColWidth());
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 6;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
      DefaultColWidthRecord rec = new DefaultColWidthRecord();
      rec.field_1_col_width = field_1_col_width;
      return rec;
    }
}
