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
 * Title: Codepage Record<P>
 * Description:  the default characterset. for the workbook<P>
 * REFERENCE:  PG 293 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class CodepageRecord
    extends Record
{
    public final static short sid = 0x42;
    private short             field_1_codepage;   // = 0;

    /**
     * the likely correct value for CODEPAGE (at least for US versions).  We could use
     * some help with international versions (which we do not have access to documentation
     * for)
     */

    public final static short CODEPAGE = ( short ) 0x4b0;

    public CodepageRecord()
    {
    }

    /**
     * Constructs a CodepageRecord and sets its fields appropriately
     * @param in the RecordInputstream to read the record from
     */

    public CodepageRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A CODEPAGE RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_codepage = in.readShort();
    }

    /**
     * set the codepage for this workbook
     *
     * @see #CODEPAGE
     * @param cp the codepage to set
     */

    public void setCodepage(short cp)
    {
        field_1_codepage = cp;
    }

    /**
     * get the codepage for this workbook
     *
     * @see #CODEPAGE
     * @return codepage - the codepage to set
     */

    public short getCodepage()
    {
        return field_1_codepage;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CODEPAGE]\n");
        buffer.append("    .codepage        = ")
            .append(Integer.toHexString(getCodepage())).append("\n");
        buffer.append("[/CODEPAGE]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) 0x02));   // 2 bytes (6 total)
        LittleEndian.putShort(data, 4 + offset, getCodepage());
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
