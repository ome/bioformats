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



import loci.poi.util.*;

/**
 * The axis line format record defines the axis type details.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class AxisLineFormatRecord
    extends Record
{
    public final static short      sid                             = 0x1021;
    private  short      field_1_axisType;
    public final static short       AXIS_TYPE_AXIS_LINE            = 0;
    public final static short       AXIS_TYPE_MAJOR_GRID_LINE      = 1;
    public final static short       AXIS_TYPE_MINOR_GRID_LINE      = 2;
    public final static short       AXIS_TYPE_WALLS_OR_FLOOR       = 3;


    public AxisLineFormatRecord()
    {

    }

    /**
     * Constructs a AxisLineFormat record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public AxisLineFormatRecord(RecordInputStream in)
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
            throw new RecordFormatException("Not a AxisLineFormat record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_axisType               = in.readShort();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AXISLINEFORMAT]\n");
        buffer.append("    .axisType             = ")
            .append("0x").append(HexDump.toHex(  getAxisType ()))
            .append(" (").append( getAxisType() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/AXISLINEFORMAT]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_axisType);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        AxisLineFormatRecord rec = new AxisLineFormatRecord();
    
        rec.field_1_axisType = field_1_axisType;
        return rec;
    }




    /**
     * Get the axis type field for the AxisLineFormat record.
     *
     * @return  One of 
     *        AXIS_TYPE_AXIS_LINE
     *        AXIS_TYPE_MAJOR_GRID_LINE
     *        AXIS_TYPE_MINOR_GRID_LINE
     *        AXIS_TYPE_WALLS_OR_FLOOR
     */
    public short getAxisType()
    {
        return field_1_axisType;
    }

    /**
     * Set the axis type field for the AxisLineFormat record.
     *
     * @param field_1_axisType
     *        One of 
     *        AXIS_TYPE_AXIS_LINE
     *        AXIS_TYPE_MAJOR_GRID_LINE
     *        AXIS_TYPE_MINOR_GRID_LINE
     *        AXIS_TYPE_WALLS_OR_FLOOR
     */
    public void setAxisType(short field_1_axisType)
    {
        this.field_1_axisType = field_1_axisType;
    }


}  // END OF CLASS




