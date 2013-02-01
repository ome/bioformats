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

import loci.poi.util.LittleEndian;

/**
 * Title: MMS Record<P>
 * Description: defines how many add menu and del menu options are stored
 *                    in the file. Should always be set to 0 for HSSF workbooks<P>
 * REFERENCE:  PG 328 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class MMSRecord
    extends Record
{
    public final static short sid = 0xC1;
    private byte              field_1_addMenuCount;   // = 0;
    private byte              field_2_delMenuCount;   // = 0;

    public MMSRecord()
    {
    }

    /**
     * Constructs a MMS record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public MMSRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A MMS RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_addMenuCount = in.readByte();
        field_2_delMenuCount = in.readByte();
    }

    /**
     * set number of add menu options (set to 0)
     * @param am  number of add menu options
     */

    public void setAddMenuCount(byte am)
    {
        field_1_addMenuCount = am;
    }

    /**
     * set number of del menu options (set to 0)
     * @param dm  number of del menu options
     */

    public void setDelMenuCount(byte dm)
    {
        field_2_delMenuCount = dm;
    }

    /**
     * get number of add menu options (should be 0)
     * @return number of add menu options
     */

    public byte getAddMenuCount()
    {
        return field_1_addMenuCount;
    }

    /**
     * get number of add del options (should be 0)
     * @return number of add menu options
     */

    public byte getDelMenuCount()
    {
        return field_2_delMenuCount;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[MMS]\n");
        buffer.append("    .addMenu        = ")
            .append(Integer.toHexString(getAddMenuCount())).append("\n");
        buffer.append("    .delMenu        = ")
            .append(Integer.toHexString(getDelMenuCount())).append("\n");
        buffer.append("[/MMS]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) 0x02));   // 2 bytes (6 total)
        data[ 4 + offset ] = getAddMenuCount();
        data[ 5 + offset ] = getDelMenuCount();
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
}
