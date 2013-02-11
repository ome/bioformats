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
 * Title:        Calc Count Record
 * Description:  Specifies the maximum times the gui should perform a formula
 *               recalculation.  For instance: in the case a formula includes
 *               cells that are themselves a result of a formula and a value
 *               changes.  This is essentially a failsafe against an infinate
 *               loop in the event the formulas are not independant. <P>
 * REFERENCE:  PG 292 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 * @see loci.poi.hssf.record.CalcModeRecord
 */

public class CalcCountRecord
    extends Record
{
    public final static short sid = 0xC;
    private short             field_1_iterations;

    public CalcCountRecord()
    {
    }

    /**
     * Constructs a CalcCountRecord and sets its fields appropriately
     * @param in the RecordInputstream to read the record from
     *
     */

    public CalcCountRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT An Calc Count RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_iterations = in.readShort();
    }

    /**
     * set the number of iterations to perform
     * @param iterations to perform
     */

    public void setIterations(short iterations)
    {
        field_1_iterations = iterations;
    }

    /**
     * get the number of iterations to perform
     * @return iterations
     */

    public short getIterations()
    {
        return field_1_iterations;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CALCCOUNT]\n");
        buffer.append("    .iterations     = ")
            .append(Integer.toHexString(getIterations())).append("\n");
        buffer.append("[/CALCCOUNT]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x2);
        LittleEndian.putShort(data, 4 + offset, getIterations());
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
      CalcCountRecord rec = new CalcCountRecord();
      rec.field_1_iterations = field_1_iterations;
      return rec;
    }
}
