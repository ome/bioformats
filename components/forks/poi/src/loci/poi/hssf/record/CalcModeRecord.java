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
 * Title:        Calc Mode Record<P>
 * Description:  Tells the gui whether to calculate formulas
 *               automatically, manually or automatically
 *               except for tables.<P>
 * REFERENCE:  PG 292 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 * @see loci.poi.hssf.record.CalcCountRecord
 */

public class CalcModeRecord
    extends Record
{
    public final static short sid                     = 0xD;

    /**
     * manually calculate formulas (0)
     */

    public final static short MANUAL                  = 0;

    /**
     * automatically calculate formulas (1)
     */

    public final static short AUTOMATIC               = 1;

    /**
     * automatically calculate formulas except for tables (-1)
     */

    public final static short AUTOMATIC_EXCEPT_TABLES = -1;
    private short             field_1_calcmode;

    public CalcModeRecord()
    {
    }

    /**
     * Constructs a CalcModeRecord and sets its fields appropriately
     * @param in the RecordInputstream to read the record from
     */

    public CalcModeRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT An Calc Mode RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_calcmode = in.readShort();
    }

    /**
     * set the calc mode flag for formulas
     *
     * @see #MANUAL
     * @see #AUTOMATIC
     * @see #AUTOMATIC_EXCEPT_TABLES
     *
     * @param calcmode one of the three flags above
     */

    public void setCalcMode(short calcmode)
    {
        field_1_calcmode = calcmode;
    }

    /**
     * get the calc mode flag for formulas
     *
     * @see #MANUAL
     * @see #AUTOMATIC
     * @see #AUTOMATIC_EXCEPT_TABLES
     *
     * @return calcmode one of the three flags above
     */

    public short getCalcMode()
    {
        return field_1_calcmode;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CALCMODE]\n");
        buffer.append("    .calcmode       = ")
            .append(Integer.toHexString(getCalcMode())).append("\n");
        buffer.append("[/CALCMODE]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x2);
        LittleEndian.putShort(data, 4 + offset, getCalcMode());
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
      CalcModeRecord rec = new CalcModeRecord();
      rec.field_1_calcmode = field_1_calcmode;
      return rec;
    }
}
