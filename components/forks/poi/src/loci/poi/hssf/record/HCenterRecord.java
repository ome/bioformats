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
 * Title:        HCenter record<P>
 * Description:  whether to center between horizontal margins<P>
 * REFERENCE:  PG 320 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 */

public class HCenterRecord
    extends Record
{
    public final static short sid = 0x83;
    private short             field_1_hcenter;

    public HCenterRecord()
    {
    }

    /**
     * Constructs an HCenter record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public HCenterRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A HCenter RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_hcenter = in.readShort();
    }

    /**
     * set whether or not to horizonatally center this sheet.
     * @param hc  center - t/f
     */

    public void setHCenter(boolean hc)
    {
        if (hc == true)
        {
            field_1_hcenter = 1;
        }
        else
        {
            field_1_hcenter = 0;
        }
    }

    /**
     * get whether or not to horizonatally center this sheet.
     * @return center - t/f
     */

    public boolean getHCenter()
    {
        return (field_1_hcenter == 1);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[HCENTER]\n");
        buffer.append("    .hcenter        = ").append(getHCenter())
            .append("\n");
        buffer.append("[/HCENTER]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x2);
        LittleEndian.putShort(data, 4 + offset, ( short ) field_1_hcenter);
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
      HCenterRecord rec = new HCenterRecord();
      rec.field_1_hcenter = field_1_hcenter;
      return rec;
    }
}
