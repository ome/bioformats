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
 * Title:        Delta Record<P>
 * Description:  controls the accuracy of the calculations<P>
 * REFERENCE:  PG 303 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 */

public class DeltaRecord
    extends Record
{
    public final static short  sid           = 0x10;
    public final static double DEFAULT_VALUE = 0.0010;   // should be .001

    // a double is an IEEE 8-byte float...damn IEEE and their goofy standards an
    // ambiguous numeric identifiers
    private double             field_1_max_change;

    public DeltaRecord()
    {
    }

    /**
     * Constructs a Delta record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public DeltaRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A DELTA RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_max_change = in.readDouble();
    }

    /**
     * set the maximum change
     * @param maxChange - maximum rounding error
     */

    public void setMaxChange(double maxChange)
    {
        field_1_max_change = maxChange;
    }

    /**
     * get the maximum change
     * @return maxChange - maximum rounding error
     */

    public double getMaxChange()
    {
        return field_1_max_change;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DELTA]\n");
        buffer.append("    .maxchange      = ").append(getMaxChange())
            .append("\n");
        buffer.append("[/DELTA]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x8);
        LittleEndian.putDouble(data, 4 + offset, getMaxChange());
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 12;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
      DeltaRecord rec = new DeltaRecord();
      rec.field_1_max_change = field_1_max_change;
      return rec;
    }
}
