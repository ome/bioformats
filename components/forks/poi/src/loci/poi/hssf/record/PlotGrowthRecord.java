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



import loci.poi.util.*;

/**
 * The plot growth record specifies the scaling factors used when a font is scaled.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class PlotGrowthRecord
    extends Record
{
    public final static short      sid                             = 0x1064;
    private  int        field_1_horizontalScale;
    private  int        field_2_verticalScale;


    public PlotGrowthRecord()
    {

    }

    /**
     * Constructs a PlotGrowth record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public PlotGrowthRecord(RecordInputStream in)
    {
        super(in);
    
    }

    /**
     * Checks the sid matches the expected side for this record
     *
     * @param id   the expected sid.
     */
    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("Not a PlotGrowth record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_horizontalScale        = in.readInt();
        field_2_verticalScale          = in.readInt();

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PLOTGROWTH]\n");
        buffer.append("    .horizontalScale      = ")
            .append("0x").append(HexDump.toHex(  getHorizontalScale ()))
            .append(" (").append( getHorizontalScale() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .verticalScale        = ")
            .append("0x").append(HexDump.toHex(  getVerticalScale ()))
            .append(" (").append( getVerticalScale() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/PLOTGROWTH]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putInt(data, 4 + offset + pos, field_1_horizontalScale);
        LittleEndian.putInt(data, 8 + offset + pos, field_2_verticalScale);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 4 + 4;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        PlotGrowthRecord rec = new PlotGrowthRecord();
    
        rec.field_1_horizontalScale = field_1_horizontalScale;
        rec.field_2_verticalScale = field_2_verticalScale;
        return rec;
    }




    /**
     * Get the horizontalScale field for the PlotGrowth record.
     */
    public int getHorizontalScale()
    {
        return field_1_horizontalScale;
    }

    /**
     * Set the horizontalScale field for the PlotGrowth record.
     */
    public void setHorizontalScale(int field_1_horizontalScale)
    {
        this.field_1_horizontalScale = field_1_horizontalScale;
    }

    /**
     * Get the verticalScale field for the PlotGrowth record.
     */
    public int getVerticalScale()
    {
        return field_2_verticalScale;
    }

    /**
     * Set the verticalScale field for the PlotGrowth record.
     */
    public void setVerticalScale(int field_2_verticalScale)
    {
        this.field_2_verticalScale = field_2_verticalScale;
    }


}  // END OF CLASS




