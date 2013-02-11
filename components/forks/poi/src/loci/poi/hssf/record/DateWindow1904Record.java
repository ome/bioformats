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
 * Title:        Date Window 1904 Flag record <P>
 * Description:  Flag specifying whether 1904 date windowing is used.
 *               (tick toc tick toc...BOOM!) <P>
 * REFERENCE:  PG 280 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class DateWindow1904Record
    extends Record
{
    public final static short sid = 0x22;
    private short             field_1_window;

    public DateWindow1904Record()
    {
    }

    /**
     * Constructs a DateWindow1904 record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public DateWindow1904Record(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A 1904 RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_window = in.readShort();
    }

    /**
     * sets whether or not to use 1904 date windowing (which means you'll be screwed in 2004)
     * @param window flag - 0/1 (false,true)
     */

    public void setWindowing(short window)
    {   // I hate using numbers in method names so I wont!
        field_1_window = window;
    }

    /**
     * gets whether or not to use 1904 date windowing (which means you'll be screwed in 2004)
     * @return window flag - 0/1 (false,true)
     */

    public short getWindowing()
    {
        return field_1_window;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[1904]\n");
        buffer.append("    .is1904          = ")
            .append(Integer.toHexString(getWindowing())).append("\n");
        buffer.append("[/1904]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) 0x02));   // 2 bytes (6 total)
        LittleEndian.putShort(data, 4 + offset, getWindowing());
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
