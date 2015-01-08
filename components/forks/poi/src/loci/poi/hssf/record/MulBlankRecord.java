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
 * MulBlankRecord.java
 *
 * Created on December 10, 2001, 12:49 PM
 */
package loci.poi.hssf.record;

/**
 * Title:        Mulitple Blank cell record <P>
 * Description:  Represents a  set of columns in a row with no value but with styling.
 *               In this release we have read-only support for this record type.
 *               The RecordFactory converts this to a set of BlankRecord objects.<P>
 * REFERENCE:  PG 329 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Glen Stampoultzis (glens at apache.org)
 * @version 2.0-pre
 * @see loci.poi.hssf.record.BlankRecord
 */

public class MulBlankRecord
    extends Record
{
    public final static short sid = 0xbe;
    //private short             field_1_row;
    private int             field_1_row;
    private short             field_2_first_col;
    private short[]           field_3_xfs;
    private short             field_4_last_col;

    /** Creates new MulBlankRecord */

    public MulBlankRecord()
    {
    }

    /**
     * Constructs a MulBlank record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public MulBlankRecord(RecordInputStream in)
    {
        super(in);
    }

    /**
     * get the row number of the cells this represents
     *
     * @return row number
     */

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
     * @param coffset  the column (coffset = column - field_2_first_col)
     * @return the XF index for the column
     */

    public short getXFAt(int coffset)
    {
        return field_3_xfs[ coffset ];
    }

    /**
     * @param in the RecordInputstream to read the record from
     */
    protected void fillFields(RecordInputStream in)
    {
        //field_1_row       = LittleEndian.getShort(data, 0 + offset);
        field_1_row       = in.readUShort();
        field_2_first_col = in.readShort();
        field_3_xfs       = parseXFs(in);
        field_4_last_col  = in.readShort();
    }

    private short [] parseXFs(RecordInputStream in)
    {
        short[] retval = new short[ (in.remaining() - 2) / 2 ];

        for (int idx = 0; idx < retval.length;idx++)
        {
          retval[idx] = in.readShort();
        }
        return retval;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[MULBLANK]\n");
        buffer.append("row  = ")
            .append(Integer.toHexString(getRow())).append("\n");
        buffer.append("firstcol  = ")
            .append(Integer.toHexString(getFirstColumn())).append("\n");
        buffer.append(" lastcol  = ")
            .append(Integer.toHexString(getLastColumn())).append("\n");
        for (int k = 0; k < getNumColumns(); k++)
        {
            buffer.append("xf").append(k).append("        = ")
                .append(Integer.toHexString(getXFAt(k))).append("\n");
        }
        buffer.append("[/MULBLANK]\n");
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
            throw new RecordFormatException("Not a MulBlankRecord!");
        }
    }

    public short getSid()
    {
        return sid;
    }

    public int serialize(int offset, byte [] data)
    {
        throw new RecordFormatException(
            "Sorry, you can't serialize a MulBlank in this release");
    }
}
