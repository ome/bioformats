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
 * Title: Double Stream Flag Record<P>
 * Description:  tells if this is a double stream file. (always no for HSSF generated files)<P>
 *               Double Stream files contain both BIFF8 and BIFF7 workbooks.<P>
 * REFERENCE:  PG 305 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class DSFRecord
    extends Record
{
    public final static short sid = 0x161;
    private short             field_1_dsf;

    public DSFRecord()
    {
    }

    /**
     * Constructs a DBCellRecord and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public DSFRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A DSF RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_dsf = in.readShort();
    }

    /**
     * set the DSF flag
     * @param dsfflag (0-off,1-on)
     */

    public void setDsf(short dsfflag)
    {
        field_1_dsf = dsfflag;
    }

    /**
     * get the DSF flag
     * @return dsfflag (0-off,1-on)
     */

    public short getDsf()
    {
        return field_1_dsf;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DSF]\n");
        buffer.append("    .isDSF           = ")
            .append(Integer.toHexString(getDsf())).append("\n");
        buffer.append("[/DSF]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) 0x02));   // 2 bytes (6 total)
        LittleEndian.putShort(data, 4 + offset, getDsf());
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
