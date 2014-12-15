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
        

/*
 * MulRKRecord.java
 *
 * Created on November 9, 2001, 4:53 PM
 */
package loci.poi.hssf.record;

import java.util.ArrayList;

import loci.poi.hssf.util.RKUtil;

/**
 * Used to store multiple RK numbers on a row.  1 MulRk = Multiple Cell values.
 * HSSF just converts this into multiple NUMBER records.  READ-ONLY SUPPORT!<P>
 * REFERENCE:  PG 330 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class MulRKRecord
    extends Record
{
    public final static short sid = 0xbd;
    //private short             field_1_row;
    private int             field_1_row;
    private short             field_2_first_col;
    private ArrayList         field_3_rks;
    private short             field_4_last_col;

    /** Creates new MulRKRecord */

    public MulRKRecord()
    {
    }

    /**
     * Constructs a MulRK record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public MulRKRecord(RecordInputStream in)
    {
        super(in);
    }

    //public short getRow()
    public int getRow()
    {
        return field_1_row;
    }

    /**
     * starting column (first cell this holds in the row)
     * @return first column number
     */

    public short getFirstColumn()
    {
        return field_2_first_col;
    }

    /**
     * ending column (last cell this holds in the row)
     * @return first column number
     */

    public short getLastColumn()
    {
        return field_4_last_col;
    }

    /**
     * get the number of columns this contains (last-first +1)
     * @return number of columns (last - first +1)
     */

    public int getNumColumns()
    {
        return field_4_last_col - field_2_first_col + 1;
    }

    /**
     * returns the xf index for column (coffset = column - field_2_first_col)
     * @return the XF index for the column
     */

    public short getXFAt(int coffset)
    {
        return (( RkRec ) field_3_rks.get(coffset)).xf;
    }

    /**
     * returns the rk number for column (coffset = column - field_2_first_col)
     * @return the value (decoded into a double)
     */

    public double getRKNumberAt(int coffset)
    {
        return RKUtil.decodeNumber((( RkRec ) field_3_rks.get(coffset)).rk);
    }

    /**
     * @param in the RecordInputstream to read the record from
     */
    protected void fillFields(RecordInputStream in)
    {
        //field_1_row       = LittleEndian.getShort(data, 0 + offset);
        field_1_row       = in.readUShort();
        field_2_first_col = in.readShort();
        field_3_rks       = parseRKs(in);
        field_4_last_col  = in.readShort();
    }

    private ArrayList parseRKs(RecordInputStream in)
    {
        ArrayList retval = new ArrayList();
        while ((in.remaining()-2) > 0) {
            RkRec rec = new RkRec();

            rec.xf = in.readShort();
            rec.rk = in.readInt();
            retval.add(rec);
        }
        return retval;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[MULRK]\n");
        buffer.append("firstcol  = ")
            .append(Integer.toHexString(getFirstColumn())).append("\n");
        buffer.append(" lastcol  = ")
            .append(Integer.toHexString(getLastColumn())).append("\n");
        for (int k = 0; k < getNumColumns(); k++)
        {
            buffer.append("xf").append(k).append("        = ")
                .append(Integer.toHexString(getXFAt(k))).append("\n");
            buffer.append("rk").append(k).append("        = ")
                .append(getRKNumberAt(k)).append("\n");
        }
        buffer.append("[/MULRK]\n");
        return buffer.toString();
    }

    /**
     * called by constructor, should throw runtime exception in the event of a
     * record passed with a differing ID.
     *
     * @param id alleged id for this record
     */

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("Not a MulRKRecord!");
        }
    }

    public short getSid()
    {
        return sid;
    }

    public int serialize(int offset, byte [] data)
    {
        throw new RecordFormatException(
            "Sorry, you can't serialize a MulRK in this release");
    }
}

class RkRec
{
    public short xf;
    public int   rk;
}
