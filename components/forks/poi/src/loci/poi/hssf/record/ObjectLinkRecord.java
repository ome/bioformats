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
 * Links text to an object on the chart or identifies it as the title.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Andrew C. Oliver (acoliver at apache.org)
 */
public class ObjectLinkRecord
    extends Record
{
    public final static short      sid                             = 0x1027;
    private  short      field_1_anchorId;
    public final static short       ANCHOR_ID_CHART_TITLE          = 1;
    public final static short       ANCHOR_ID_Y_AXIS               = 2;
    public final static short       ANCHOR_ID_X_AXIS               = 3;
    public final static short       ANCHOR_ID_SERIES_OR_POINT      = 4;
    public final static short       ANCHOR_ID_Z_AXIS               = 7;
    private  short      field_2_link1;
    private  short      field_3_link2;


    public ObjectLinkRecord()
    {

    }

    /**
     * Constructs a ObjectLink record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public ObjectLinkRecord(RecordInputStream in)
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
            throw new RecordFormatException("Not a ObjectLink record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_anchorId               = in.readShort();
        field_2_link1                  = in.readShort();
        field_3_link2                  = in.readShort();

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[OBJECTLINK]\n");
        buffer.append("    .anchorId             = ")
            .append("0x").append(HexDump.toHex(  getAnchorId ()))
            .append(" (").append( getAnchorId() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .link1                = ")
            .append("0x").append(HexDump.toHex(  getLink1 ()))
            .append(" (").append( getLink1() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .link2                = ")
            .append("0x").append(HexDump.toHex(  getLink2 ()))
            .append(" (").append( getLink2() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/OBJECTLINK]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_anchorId);
        LittleEndian.putShort(data, 6 + offset + pos, field_2_link1);
        LittleEndian.putShort(data, 8 + offset + pos, field_3_link2);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2 + 2 + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        ObjectLinkRecord rec = new ObjectLinkRecord();
    
        rec.field_1_anchorId = field_1_anchorId;
        rec.field_2_link1 = field_2_link1;
        rec.field_3_link2 = field_3_link2;
        return rec;
    }




    /**
     * Get the anchor id field for the ObjectLink record.
     *
     * @return  One of 
     *        ANCHOR_ID_CHART_TITLE
     *        ANCHOR_ID_Y_AXIS
     *        ANCHOR_ID_X_AXIS
     *        ANCHOR_ID_SERIES_OR_POINT
     *        ANCHOR_ID_Z_AXIS
     */
    public short getAnchorId()
    {
        return field_1_anchorId;
    }

    /**
     * Set the anchor id field for the ObjectLink record.
     *
     * @param field_1_anchorId
     *        One of 
     *        ANCHOR_ID_CHART_TITLE
     *        ANCHOR_ID_Y_AXIS
     *        ANCHOR_ID_X_AXIS
     *        ANCHOR_ID_SERIES_OR_POINT
     *        ANCHOR_ID_Z_AXIS
     */
    public void setAnchorId(short field_1_anchorId)
    {
        this.field_1_anchorId = field_1_anchorId;
    }

    /**
     * Get the link 1 field for the ObjectLink record.
     */
    public short getLink1()
    {
        return field_2_link1;
    }

    /**
     * Set the link 1 field for the ObjectLink record.
     */
    public void setLink1(short field_2_link1)
    {
        this.field_2_link1 = field_2_link1;
    }

    /**
     * Get the link 2 field for the ObjectLink record.
     */
    public short getLink2()
    {
        return field_3_link2;
    }

    /**
     * Set the link 2 field for the ObjectLink record.
     */
    public void setLink2(short field_3_link2)
    {
        this.field_3_link2 = field_3_link2;
    }


}  // END OF CLASS




